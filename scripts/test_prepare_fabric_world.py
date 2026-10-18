import json
from pathlib import Path
import tempfile
import unittest
from prepare_fabric_world import prepare


class WorldPreparationTests(unittest.TestCase):
    def test_copy_converts_config_and_preserves_source(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            world = root / "original"
            (world / "serverconfig").mkdir(parents=True)
            (world / "level.dat").write_bytes(b"fixture")
            (world / "session.lock").write_bytes(b"123")
            (world / "serverconfig/mahoutsukai-server.toml").write_text('[Server]\nMAX_MANA_CAP=12345\nLEY_DISTANCE=777\n', encoding="utf-8")
            before = {str(p.relative_to(world)): p.read_bytes() for p in world.rglob("*") if p.is_file()}
            report = prepare(world, root / "fabric")
            self.assertIn("LEY_DISTANCE", report["converted_settings"])
            self.assertEqual(json.loads((root / "fabric/serverconfig/mahoutsukai-fabric-mana.json").read_text())["MAX_MANA_CAP"], 12345)
            self.assertEqual(json.loads((root / "fabric/serverconfig/mahoutsukai-fabric-economy.json").read_text())["LEY_DISTANCE"], 777)
            self.assertFalse((root / "fabric/session.lock").exists())
            self.assertEqual(before, {str(p.relative_to(world)): p.read_bytes() for p in world.rglob("*") if p.is_file()})
            with self.assertRaises(ValueError):
                prepare(world, root / "fabric")

    def test_unsupported_custom_setting_does_not_create_copy(self):
        with tempfile.TemporaryDirectory() as temp:
            world = Path(temp) / "original"
            (world / "serverconfig").mkdir(parents=True)
            (world / "level.dat").write_bytes(b"fixture")
            (world / "serverconfig/mahoutsukai-server.toml").write_text('ONE_MADE_UP_SETTING=1\n', encoding="utf-8")
            destination = Path(temp) / "fabric"
            with self.assertRaises(ValueError):
                prepare(world, destination)
            self.assertFalse(destination.exists())
            with self.assertRaises(ValueError):
                prepare(world, world / "nested")


if __name__ == "__main__":
    unittest.main()
