"""Compile and run the test-only probe beside the unmodified NeoForge original.

Requires the local NeoForge 26.1.2.122 server installation in tools/neoforge-parity.
Never points at a user's world. The server exits itself after writing fixtures.
"""
from pathlib import Path
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
RUN = ROOT / "tools/neoforge-parity"
JAVA = ROOT / "tools/jdk/jdk-25.0.4.1+1/bin"


def main():
    output = RUN / "probe-classes"
    output.mkdir(exist_ok=True)
    jars = list((RUN / "libraries").rglob("*.jar"))
    priority = [p for p in jars if "minecraft-server-patched" in str(p)
                or p.name in ("gson-2.13.2.jar", "neoforge-26.1.2.122-universal.jar")]
    classpath = ";".join(str(p.resolve()) for p in priority
                         + [p for p in jars if p not in priority]
                         + list((ROOT / "original").glob("*.jar")))
    args = ["-encoding", "UTF-8", "-cp", classpath, "-d", str(output),
            str(ROOT / "scripts/parity/OriginalProbe.java")]
    argfile = RUN / "javac.args"
    argfile.write_text("\n".join('"' + s.replace("\\", "/") + '"' for s in args), encoding="utf-8")
    subprocess.run([str(JAVA / "javac.exe"), "@" + str(argfile)], check=True)
    with zipfile.ZipFile(RUN / "mods/mahou-parity-probe.jar", "w") as jar:
        for path in output.rglob("*.class"):
            jar.write(path, path.relative_to(output).as_posix())
        jar.writestr("META-INF/neoforge.mods.toml", 'modLoader="javafml"\nloaderVersion="[1,)"\n'
                     'license="All Rights Reserved"\n[[mods]]\nmodId="mahou_parity_probe"\n'
                     'version="1"\ndisplayName="Local Mahou Parity Probe"\n')
    marker = ROOT / "analysis/original-fixtures/probe.passed"
    marker.unlink(missing_ok=True)
    with (ROOT / "analysis/phase018-original-probe.log").open("w", encoding="utf-8") as log:
        subprocess.run([str(JAVA / "java.exe"), "-Xmx2G",
                        "@libraries/net/neoforged/neoforge/26.1.2.122/win_args.txt", "nogui"],
                       cwd=RUN, stdout=log, stderr=subprocess.STDOUT, check=True)
    if not marker.is_file():
        raise RuntimeError("Original probe did not pass; inspect analysis/phase018-original-probe.log")
    print(marker.read_text(encoding="utf-8").strip())


if __name__ == "__main__":
    main()
