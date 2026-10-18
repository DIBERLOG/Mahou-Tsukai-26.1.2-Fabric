"""Temporary original NeoForge visual-test server; only an isolated test world is used."""
from pathlib import Path
import subprocess
import time

ROOT = Path(__file__).resolve().parents[1]
RUN = ROOT / "tools/neoforge-parity"


def main():
    properties = RUN / "server.properties"
    previous = properties.read_bytes()
    probe = RUN / "mods/mahou-parity-probe.jar"
    disabled = probe.with_suffix(".jar.disabled")
    if probe.exists():
        probe.rename(disabled)
    properties.write_text("server-ip=127.0.0.1\nserver-port=25595\nonline-mode=false\n"
                          "level-name=visual-original\ngamemode=creative\nview-distance=2\nsimulation-distance=2\n", encoding="utf-8")
    request = RUN / "visual-console.request"
    request.unlink(missing_ok=True)
    process = None
    try:
        with (ROOT / "analysis/phase018-original-visual-server.log").open("w", encoding="utf-8") as log:
            process = subprocess.Popen([str(ROOT / "tools/jdk/jdk-25.0.4.1+1/bin/java.exe"), "-Xmx2G",
                                        "@libraries/net/neoforged/neoforge/26.1.2.122/win_args.txt", "nogui"],
                                       cwd=RUN, stdin=subprocess.PIPE, text=True, stdout=log, stderr=subprocess.STDOUT)
            while process.poll() is None:
                if request.is_file():
                    commands = request.read_text(encoding="utf-8")
                    request.unlink()
                    process.stdin.write(commands.rstrip() + "\n")
                    process.stdin.flush()
                time.sleep(.1)
            if process.returncode:
                raise RuntimeError("Original visual server failed; inspect its log")
    finally:
        if process and process.poll() is None:
            process.kill()
            process.wait()
        properties.write_bytes(previous)
        if disabled.exists():
            disabled.rename(probe)


if __name__ == "__main__":
    main()
