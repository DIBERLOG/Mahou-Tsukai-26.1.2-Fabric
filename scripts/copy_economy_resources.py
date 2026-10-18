"""Copy original assets and their model/texture dependencies for the adapted economy."""
import json
from pathlib import Path
import shutil

root = Path(__file__).resolve().parents[1]
source = root/'recovered'
target = root/'MahouTsukai-Fabric/src/main/resources'

def copy(name):
    out = target/name
    out.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source/name, out)

seen = set()
def model(name):
    if not name.startswith('mahoutsukai:') or name in seen:
        return
    seen.add(name)
    path = 'assets/mahoutsukai/models/'+name.split(':', 1)[1]+'.json'
    copy(path)
    data = json.loads((source/path).read_text())
    if data.get('parent') == 'builtin/entity':
        del data['parent']
        (target/path).write_text(json.dumps(data, indent=2)+'\n')
    model(data.get('parent', ''))
    for texture in data.get('textures', {}).values():
        if texture.startswith('mahoutsukai:'):
            copy('assets/mahoutsukai/textures/'+texture.split(':', 1)[1]+'.png')

for item in ['attuned_diamond', 'attuned_emerald', 'attuner', 'mana_circuit', 'mana_circuit_magitech']:
    copy('assets/mahoutsukai/items/'+item+'.json')
    model('mahoutsukai:item/'+item)
for item in ['attuned_diamond', 'attuned_emerald', 'attuner', 'mana_circuit_magitech']:
    copy('data/mahoutsukai/recipe/'+item+'.json')
for block in ['mana_circuit', 'mana_circuit_magitech']:
    copy('assets/mahoutsukai/blockstates/'+block+'.json')
    model('mahoutsukai:block/'+block)
    copy('data/mahoutsukai/loot_table/blocks/'+block+'.json')
for texture in ['block/mahoujin_casting', 'block/runes', 'block/white', 'particle/depth']:
    copy('assets/mahoutsukai/textures/'+texture+'.png')
