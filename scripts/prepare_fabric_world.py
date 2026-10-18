"""Copy a stopped 26.1.2 NeoForge world and translate supported Mahou server settings.

The mod imports original attachments when the COPY is loaded. Source files are
never rewritten. Other mods' world data requires those mods' own migration.
"""
import argparse
from contextlib import contextmanager
import json
from pathlib import Path
import shutil
import tomllib

ROOT = Path(__file__).resolve().parents[1]
MANA = {"MAX_MANA_CAP", "MANA_UP_COUNTER", "MANA_UP_COUNTER_RATIO", "MANA_UP_COUNTER_MAX",
        "MANA_INCREASE", "MANA_INCREASE_LOG_PUNISHMENT", "MANA_INCREASE_PUNISHMENT_FACTOR"}


def flatten(table):
    result = {}
    for name, value in table.items():
        if isinstance(value, dict):
            child = flatten(value)
            if result.keys() & child.keys():
                raise ValueError("Duplicate configuration names in nested TOML")
            result.update(child)
        else:
            if name in result:
                raise ValueError("Duplicate configuration name: " + name)
            result[name] = value
    return result


@contextmanager
def stopped_world(source):
    lock = source / "session.lock"
    if not lock.exists():
        yield
        return
    # Hold the same exclusive byte-range lock throughout the copy.
    with lock.open("r+b") as handle:
        if __import__("os").name == "nt":
            import msvcrt
            msvcrt.locking(handle.fileno(), msvcrt.LK_NBLCK, 1)
            try:
                yield
            finally:
                handle.seek(0)
                msvcrt.locking(handle.fileno(), msvcrt.LK_UNLCK, 1)
        else:
            import fcntl
            fcntl.lockf(handle, fcntl.LOCK_EX | fcntl.LOCK_NB)
            try:
                yield
            finally:
                fcntl.lockf(handle, fcntl.LOCK_UN)


def prepare(source, destination, config=None):
    source, destination = Path(source).resolve(), Path(destination).resolve()
    if not (source / "level.dat").is_file():
        raise ValueError("Source must be a Minecraft world containing level.dat")
    if destination.exists() or destination.is_relative_to(source) or source.is_relative_to(destination):
        raise ValueError("Destination must be a new separate directory outside the source")
    schema = json.loads((ROOT / "MahouTsukai-Fabric/docs/config-defaults-0.18.json").read_text(encoding="utf-8"))
    original = json.loads((ROOT / "analysis/original-fixtures/config.json").read_text(encoding="utf-8"))
    names = json.loads((ROOT / "analysis/original-fixtures/config-names.json").read_text(encoding="utf-8"))
    configs = []
    if config is not None:
        configs.append(Path(config).resolve())
    elif (source.parent / "config/mahoutsukai-server.toml").is_file():
        configs.append(source.parent / "config/mahoutsukai-server.toml")
    override = source / "serverconfig/mahoutsukai-server.toml"
    if override.is_file() and override not in configs:
        configs.append(override)
    values = {}
    for path in configs:
        values.update(flatten(tomllib.loads(path.read_text(encoding="utf-8-sig"))))
    supported, unchanged_unsupported = {}, []
    for toml_key, value in values.items():
        key = names.get(toml_key, toml_key)
        if key in schema or key in MANA:
            supported[key] = value
        elif key in original and value == original[key]:
            unchanged_unsupported.append(key)
        else:
            raise ValueError(f"Unsupported non-default setting {key}; source preserved, no copy created")
    # Validate serialization before copying; TOML permits infinities, JSON settings do not.
    json.dumps(supported, allow_nan=False)
    report = {"source": str(source), "destination": str(destination),
              "config_files": [str(p) for p in configs], "converted_settings": sorted(supported),
              "unsupported_settings_at_original_defaults": sorted(unchanged_unsupported),
              "status": "prepared; game load and save verification still required",
              "scope": "Mahou Tsukai 26.1.2 only; original source is preserved"}
    with stopped_world(source):
        shutil.copytree(source, destination, ignore=shutil.ignore_patterns("session.lock"))
        serverconfig = destination / "serverconfig"
        serverconfig.mkdir(exist_ok=True)
        for name, fields in [("mana", {k: v for k, v in supported.items() if k in MANA}),
                             ("economy", {k: v for k, v in supported.items() if k not in MANA})]:
            path = serverconfig / f"mahoutsukai-fabric-{name}.json"
            if not path.exists():
                path.write_text(json.dumps(fields, indent=2, ensure_ascii=False, allow_nan=False) + "\n", encoding="utf-8")
        (destination / "mahoutsukai-migration.json").write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    return report


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", type=Path)
    parser.add_argument("destination", type=Path)
    parser.add_argument("--config", type=Path, help="Original instance's mahoutsukai-server.toml")
    args = parser.parse_args()
    print(json.dumps(prepare(args.source, args.destination, args.config), indent=2, ensure_ascii=False))
