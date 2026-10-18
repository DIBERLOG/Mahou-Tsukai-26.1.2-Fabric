"""Launch the packaged JAR twice on a COPY of the original NeoForge test world.

No development source sets or JEI are used. Requires the downloaded Fabric server
launcher and prepared world under tools/fabric-release-proof.
"""
from pathlib import Path
import hashlib
import shutil
import subprocess
import time

ROOT = Path(__file__).resolve().parents[1]
RUN = ROOT / "tools/fabric-release-proof"
JAVA = ROOT / "tools/jdk/jdk-25.0.4.1+1/bin/java.exe"


def hashes(directory):
    return {str(p.relative_to(directory)): hashlib.sha256(p.read_bytes()).hexdigest()
            for p in directory.rglob("*") if p.is_file()}


def main():
    marker = ROOT / "analysis/phase025-release-world.passed"
    marker.unlink(missing_ok=True)
    original = ROOT / "tools/neoforge-parity/parity-original"
    before = hashes(original)
    mods = RUN / "mods"
    mods.mkdir(exist_ok=True)
    version = next(line.split("=", 1)[1] for line in (ROOT / "MahouTsukai-Fabric/gradle.properties").read_text().splitlines() if line.startswith("version="))
    jar = ROOT / f"MahouTsukai-Fabric/build/libs/MahouTsukai-Fabric-{version}.jar"
    for previous in mods.glob("MahouTsukai-Fabric-*.jar"):
        if previous.name!=jar.name:
            previous.unlink()
    shutil.copy2(jar, mods / jar.name)
    api = next((Path.home() / ".gradle/caches/modules-2/files-2.1/net.fabricmc.fabric-api/fabric-api/0.155.3+26.1.2").rglob("*.jar"))
    shutil.copy2(api, mods / api.name)
    (RUN / "eula.txt").write_text("eula=true\n", encoding="utf-8")
    (RUN / "server.properties").write_text("server-ip=127.0.0.1\nserver-port=25596\n"
                                            "online-mode=false\nlevel-name=migrated-world\n"
                                            "view-distance=2\nsimulation-distance=2\n", encoding="utf-8")
    for index, commands in enumerate([
        "setblock 8 280 8 mahoutsukai:mana_circuit\n"
        "data merge block 8 280 8 {MAHOUTSUKAI_MANA_TAG:321}\n"
        "data get block 8 280 8 MAHOUTSUKAI_MANA_TAG\n"
        "execute in mahoutsukai:reality_marble run setblock 8 90 8 minecraft:stone\n"
        "summon mahoutsukai:rho_aias_entity 8 282 8\nsave-all flush\nstop\n",
        "data get block 8 280 8 MAHOUTSUKAI_MANA_TAG\n"
        "execute in mahoutsukai:reality_marble if block 8 90 8 minecraft:stone run say MARBLE_PERSISTED\n"
        "stop\n"
    ], 1):
        log = ROOT / f"analysis/phase025-release-world-{index}.log"
        with log.open("w", encoding="utf-8") as output:
            process = subprocess.Popen([str(JAVA), "-Xmx2G", "-jar", "fabric-server-launch.jar", "nogui"],
                                       cwd=RUN, stdin=subprocess.PIPE, text=True, encoding="utf-8",
                                       stdout=output, stderr=subprocess.STDOUT)
            try:
                deadline = time.monotonic() + 90
                # Console input captures its source immediately. Sending it before world
                # loading finishes gives vanilla commands a null ServerLevel.
                while "Done (" not in log.read_text(encoding="utf-8", errors="replace"):
                    if process.poll() is not None or time.monotonic() > deadline:
                        raise RuntimeError("Production server did not become ready")
                    time.sleep(.1)
                process.stdin.write("forceload add 0 0\nexecute in mahoutsukai:reality_marble run forceload add 0 0\n")
                process.stdin.flush()
                while True:
                    process.stdin.write("execute if loaded 8 280 8 run say OVERWORLD_READY\n"
                                        "execute in mahoutsukai:reality_marble if loaded 8 90 8 run say MARBLE_READY\n")
                    process.stdin.flush()
                    time.sleep(.25)
                    ready = log.read_text(encoding="utf-8", errors="replace")
                    if "OVERWORLD_READY" in ready and "MARBLE_READY" in ready:
                        break
                    if process.poll() is not None or time.monotonic() > deadline:
                        raise RuntimeError("Migration proof chunks did not load")
                commands = commands.replace("stop\n", "forceload remove 0 0\nexecute in mahoutsukai:reality_marble run forceload remove 0 0\nstop\n")
                process.communicate(input=commands, timeout=60)
            finally:
                if process.poll() is None:
                    process.kill()
                    process.wait()
        contents = log.read_text(encoding="utf-8", errors="replace")
        if process.returncode or "Done (" not in contents or "following block data: 321" not in contents or "Stopping server" not in contents:
            raise RuntimeError(f"Production world pass {index} did not succeed; inspect {log}")
        if "Encountered an unexpected exception" in contents or "Failed to start" in contents or "An unexpected error occurred" in contents:
            raise RuntimeError(f"Production world pass {index} reported a server failure")
        if index == 2 and "MARBLE_PERSISTED" not in contents:
            raise RuntimeError("Reality Marble change did not survive production restart")
    if before != hashes(original):
        raise AssertionError("Original source world changed during migration proof")
    marker.write_text("PASS: packaged Fabric JAR + Fabric API, no JEI or development classes; original NeoForge world COPY loaded, old entity ID summoned, battery mana and Reality Marble persisted across clean shutdown/restart; original source hashes unchanged\n", encoding="utf-8")
    with marker.open("a", encoding="utf-8") as output:
        output.write("JAR SHA256: " + hashlib.sha256(jar.read_bytes()).hexdigest() + "\n")
    print(marker.read_text(encoding="utf-8").strip())


if __name__ == "__main__":
    main()

