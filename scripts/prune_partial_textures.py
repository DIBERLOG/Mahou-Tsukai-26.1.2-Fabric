"""Exclude unused original textures from the partial build; retain recovered originals."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
resources = root/'MahouTsukai-Fabric/src/main/resources/assets/mahoutsukai'
keep = {'block/mahoujin_casting.png', 'block/runes.png', 'block/white.png', 'particle/depth.png', 'entity/fae.png', 'mob_effect/fay_sight_eyes.png', 'mob_effect/bleeding.png', 'mob_effect/probability_alter.png'}
keep |= { 'block/'+name+'.png' for name in ['blood-circle','blood-circle2','blood-circle3','blood-square2','fay-circle','fay-circle2','fay-circle3','fay-square2','mahoujin_changeable','spell_cloth'] }
keep.add('advancements/background-advancements.png')
for model in (resources/'models').rglob('*.json'):
    for texture in json.loads(model.read_text()).get('textures', {}).values():
        if texture.startswith('mahoutsukai:'):
            keep.add(texture.split(':', 1)[1]+'.png')
base = (resources/'textures').resolve()
removed = 0
for texture in base.rglob('*.png'):
    if texture.relative_to(base).as_posix() not in keep:
        assert base in texture.resolve().parents
        texture.unlink()
        removed += 1
print(f'Excluded {removed} unused textures; recovered/ unchanged')
