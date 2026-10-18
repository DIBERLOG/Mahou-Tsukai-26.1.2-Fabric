"""Launch the unchanged original in the isolated official NeoForge client install.

Downloads missing declared libraries, verifies their published SHA1, uses the
already downloaded local Minecraft asset cache, and never alters the user's launcher.
"""
from concurrent.futures import ThreadPoolExecutor
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import urllib.request
import zipfile

ROOT = Path(__file__).resolve().parents[1]
RUN = ROOT / "tools/neoforge-client-parity"
JAVA = ROOT / "tools/jdk/jdk-25.0.4.1+1/bin"


def allowed(rules):
    if not rules:
        return True
    answer = False
    for rule in rules:
        os = rule.get("os", {})
        if os.get("name", "windows") != "windows" or os.get("arch", "amd64") not in ("amd64", "x86_64"):
            continue
        if any(value != (key == "has_custom_resolution") for key, value in rule.get("features", {}).items()):
            continue
        answer = rule["action"] == "allow"
    return answer


def main():
    vanilla = json.loads((RUN / "versions/26.1.2/26.1.2.json").read_text())
    neo = json.loads((RUN / "versions/neoforge-26.1.2.122/neoforge-26.1.2.122.json").read_text())
    libraries = {}
    for library in vanilla["libraries"] + neo["libraries"]:
        if allowed(library.get("rules")):
            parts = library["name"].split(":")
            key = ":".join(parts[:2] + parts[3:])
            libraries[key] = library

    def obtain(lib):
        artifact = lib.get("downloads", {}).get("artifact")
        if artifact:
            relative = artifact["path"]
        else:
            group, name, version, *classifier = lib["name"].split(":")
            relative = f'{group.replace(".", "/")}/{name}/{version}/{name}-{version}' + ("-" + classifier[0] if classifier else "") + ".jar"
        path = (RUN / "libraries" / relative).resolve()
        if not path.is_relative_to((RUN / "libraries").resolve()):
            raise ValueError(f"Library path outside isolated installation: {path} ({relative})")
        if not path.exists():
            if not artifact:
                raise ValueError(f"Installer did not provide {relative}")
            path.parent.mkdir(parents=True, exist_ok=True)
            urllib.request.urlretrieve(artifact["url"], path)
        if artifact and artifact.get("sha1") and hashlib.sha1(path.read_bytes()).hexdigest() != artifact["sha1"]:
            raise ValueError(f"Library checksum mismatch: {relative}")
        return path

    with ThreadPoolExecutor(max_workers=8) as pool:
        jars = list(pool.map(obtain, libraries.values()))
    mods = RUN / "mods"
    mods.mkdir(exist_ok=True)
    original = next((ROOT / "original").glob("*.jar"))
    installed = mods / original.name
    if not installed.exists():
        shutil.copy2(original, installed)
    elif hashlib.sha256(installed.read_bytes()).digest() != hashlib.sha256(original.read_bytes()).digest():
        raise ValueError("Installed original differs; refusing to overwrite it")
    output = RUN / "probe-classes"
    output.mkdir(exist_ok=True)
    # The installer supplies these game modules for FML's own module discovery;
    # they are intentionally absent from the ordinary launcher library list.
    compile_jars = list(jars) + list((RUN / "libraries/net/neoforged/minecraft-client-patched").rglob("*.jar")) + list((RUN / "libraries/net/neoforged/neoforge").rglob("*.jar"))
    priority = [p for p in compile_jars if "minecraft-client-patched" in str(p) or p.name == "gson-2.13.2.jar"]
    compile_classpath = ";".join(str(p) for p in priority + [p for p in compile_jars if p not in priority])
    classpath = ";".join(str(p) for p in jars + [RUN / "versions/26.1.2/26.1.2.jar"])
    args = ["-encoding", "UTF-8", "-cp", compile_classpath, "-d", str(output), str(ROOT / "scripts/parity/OriginalVisualProbe.java")]
    argfile = RUN / "javac.args"
    argfile.write_text("\n".join('"' + s.replace("\\", "/") + '"' for s in args), encoding="utf-8")
    subprocess.run([str(JAVA / "javac.exe"), "@" + str(argfile)], check=True)
    with zipfile.ZipFile(mods / "mahou-visual-probe.jar", "w") as jar:
        for path in output.rglob("*.class"):
            jar.write(path, path.relative_to(output).as_posix())
        jar.writestr("META-INF/neoforge.mods.toml", 'modLoader="javafml"\nloaderVersion="[1,)"\nlicense="All Rights Reserved"\n[[mods]]\nmodId="mahou_visual_probe"\nversion="1"\ndisplayName="Local Original Visual Capture"\n')
    natives = RUN / "natives"
    natives.mkdir(exist_ok=True)
    options = RUN / "options.txt"
    if options.exists():
        text = options.read_text(encoding="utf-8").replace("pauseOnLostFocus:true", "pauseOnLostFocus:false")
        text = re.sub(r"(?m)^tutorialStep:.*$", "tutorialStep:none", text)
        options.write_text(text, encoding="utf-8")
    values = {"natives_directory": str(natives), "launcher_name": "LocalParity", "launcher_version": "1",
              "classpath": classpath, "library_directory": str(RUN / "libraries"),
              "auth_player_name": "OriginalVisual", "version_name": "26.1.2",
              "game_directory": str(RUN), "assets_root": str(Path.home() / ".gradle/caches/fabric-loom/assets"),
              "assets_index_name": "26.1.2-30", "auth_uuid": "00000000000000000000000000000001",
              "auth_access_token": "0", "clientid": "", "auth_xuid": "", "version_type": "release",
              "resolution_width": "1280", "resolution_height": "720"}

    def arguments(entries):
        result = []
        for entry in entries:
            if isinstance(entry, dict):
                if not allowed(entry.get("rules")):
                    continue
                entry = entry["value"]
            for value in entry if isinstance(entry, list) else [entry]:
                result.append(re.sub(r"\$\{([^}]+)\}", lambda m: values[m[1]], value))
        return result

    command = ["-Xmx2G"] + arguments(vanilla["arguments"]["jvm"] + neo["arguments"]["jvm"])
    command += [neo["mainClass"]] + arguments(vanilla["arguments"]["game"] + neo["arguments"]["game"])
    command += ["--quickPlayMultiplayer", "127.0.0.1:25595"]
    launch = RUN / "client.args"
    launch.write_text("\n".join('"' + s.replace("\\", "/") + '"' for s in command), encoding="utf-8")
    with (ROOT / "analysis/phase018-original-client.log").open("w", encoding="utf-8") as log:
        subprocess.run([str(JAVA / "java.exe"), "@" + str(launch)], cwd=RUN, stdout=log, stderr=subprocess.STDOUT, check=True)


if __name__ == "__main__":
    main()
