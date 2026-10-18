"""Reproducible archive/source inventory. Does not execute original mod code."""
import collections
import hashlib
import json
from pathlib import Path
import re
import struct
import zipfile

root = Path(__file__).resolve().parents[1]
jar = next((root/'original').glob('*.jar'))
with zipfile.ZipFile(jar) as archive:
    assert archive.testzip() is None, 'Invalid archive CRC'
    classes = [n for n in archive.namelist() if n.endswith('.class')]
    report = {
        'sha256': hashlib.sha256(jar.read_bytes()).hexdigest(),
        'class_count': len(classes),
        'bytecode_versions': dict(collections.Counter(struct.unpack('>H', archive.read(n)[6:8])[0] for n in classes)),
        'packages': dict(collections.Counter(n.rsplit('/', 1)[0] for n in classes)),
        'nested_jars': [n for n in archive.namelist() if n.endswith('.jar')],
        'resource_file_count': sum(not n.is_dir() and not n.filename.endswith('.class') for n in archive.infolist()),
        'directory_entry_count': sum(n.is_dir() for n in archive.infolist()),
    }
files = sorted((root/'recovered').rglob('*.java'))
imports = collections.defaultdict(list)
edges = {}
for file in files:
    text = file.read_text(encoding='utf-8')
    name = str(file.relative_to(root/'recovered')).replace('\\', '/')
    dependencies = re.findall(r'^import ([\w.]+);', text, re.M)
    edges[name] = dependencies
    for dependency in dependencies:
        if dependency.startswith(('net.neoforged.', 'com.mojang.blaze3d.', 'mezz.jei.', 'dev.shadowsoffire.')):
            imports[dependency].append(name)
report['recovered_java_files'] = len(files)
report['classes_with_neoforge_imports'] = sum(any(i.startswith('net.neoforged.') for i in deps) for deps in edges.values())
for name, value in [('inventory', report), ('platform-dependencies', imports), ('source-import-graph', edges)]:
    (root/'analysis'/f'{name}.json').write_text(json.dumps(value, indent=2, ensure_ascii=False)+'\n', encoding='utf-8')
print(json.dumps(report | {'packages': '(see inventory.json)'}, indent=2))
