"""Validate the adapted resource subset without starting Minecraft."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
resources = root/'MahouTsukai-Fabric/src/main/resources'
import re
java = root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
ids = set(re.findall(r'register\("([a-z0-9_]+)"',(java/'item/ModItems.java').read_text()))
ids |= {'mana_circuit','mana_circuit_magitech','mahoujin','spell_cloth'}
ids |= set(re.findall(r'plain\("([a-z0-9_]+)"[^;\n]+,\s*true\)', (java/'block/ModBlocks.java').read_text()))

def validate_reference(value, kind):
    if not isinstance(value, str) or not value.startswith('mahoutsukai:'):
        return
    path = value.split(':', 1)[1]
    if kind == 'texture':
        assert (resources/'assets/mahoutsukai/textures'/f'{path}.png').is_file(), value
    elif kind == 'model':
        assert (resources/'assets/mahoutsukai/models'/f'{path}.json').is_file(), value
    else:
        assert path in ids, f'Unregistered item reference: {value}'

recipes = list((resources/'data/mahoutsukai/recipe').glob('*.json'))
for file in recipes:
    recipe = json.loads(file.read_text(encoding='utf-8'))
    if recipe.get('type')=='mahoutsukai:william_bookcloning':
        assert 'william' in ids
        continue
    validate_reference(recipe['result']['id'], 'item')
    for value in recipe.get('ingredients', []):
        validate_reference(value, 'item')
    for value in recipe.get('key', {}).values():
        validate_reference(value, 'item')
def check_item_model(value):
    if value['type'] == 'minecraft:condition':
        check_item_model(value['on_false']); check_item_model(value['on_true'])
    elif value['type'] == 'minecraft:select':
        check_item_model(value['fallback'])
        for case in value['cases']:check_item_model(case['model'])
    elif value['type'] == 'minecraft:composite':
        for child in value['models']: check_item_model(child)
    elif value['type'] == 'minecraft:special':
        validate_reference(value['base'], 'model')
        assert value['model']['type'] == 'mahoutsukai:magical_item'
        assert value['model']['renderer'] in set(re.findall(r'renderer.equals\("([^"]+)"\)', (root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/render/item/MahouSpecialItemModel.java').read_text()))
    else:
        validate_reference(value['model'], 'model')

for name in sorted(ids):
    item = json.loads((resources/f'assets/mahoutsukai/items/{name}.json').read_text())
    check_item_model(item['model'])
    model = json.loads((resources/f'assets/mahoutsukai/models/item/{name}.json').read_text())
    for texture in model.get('textures', {}).values():
        validate_reference(texture, 'texture')
print(f'PASS: {len(ids)} item definitions, models and textures; {len(recipes)} recipes reference registered items')

# Native block registrations must have an existing model, including non-item ritual blocks.
blocks=set(re.findall(r'(?:plain|register)\("([a-z0-9_]+)"',(java/'block/ModBlocks.java').read_text(encoding='utf-8')))
for name in blocks:
    definition=json.loads((resources/f'assets/mahoutsukai/blockstates/{name}.json').read_text(encoding='utf-8'))
    for variant in definition.get('variants',{}).values():
        for entry in variant if isinstance(variant,list) else [variant]:
            validate_reference(entry['model'],'model')
for sprite in ['murky_still','murky_flow']:
    validate_reference('mahoutsukai:block/'+sprite,'texture')
print(f'PASS: {len(blocks)} registered blockstates and local model references; both animated murky fluid sprites')
