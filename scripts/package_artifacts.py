"""Create a local, explicitly incomplete handoff without caches, tools or test credentials."""
import hashlib
from pathlib import Path
import zipfile

root = Path(__file__).resolve().parents[1]
version = next(line.split('=',1)[1] for line in (root/'MahouTsukai-Fabric/gradle.properties').read_text().splitlines() if line.startswith('version='))
jousting_version = next(line.split('=',1)[1].strip().strip("'") for line in (root/'Jousting-Fabric/build.gradle').read_text().splitlines() if line.startswith('version='))
jar_prefixes = {root/'MahouTsukai-Fabric/build/libs': f'MahouTsukai-Fabric-{version}',
                root/'Jousting-Fabric/build/libs': f'Jousting-Fabric-{jousting_version}'}
output = root/'artifacts'
output.mkdir(exist_ok=True)
archive = output/'MahouTsukai-Fabric-INCOMPLETE-project.zip'
with zipfile.ZipFile(archive, 'w', zipfile.ZIP_DEFLATED) as bundle:
    for directory in ['original', 'recovered', 'analysis', 'scripts', 'MahouTsukai-Fabric', 'Jousting-Fabric']:
        for file in sorted((root/directory).rglob('*')):
            if not file.is_file():
                continue
            relative = file.relative_to(root)
            if any(part in {'.gradle', 'run', '__pycache__'} for part in relative.parts):
                continue
            if 'build' in relative.parts and file.parent not in jar_prefixes:
                continue
            if file.parent in jar_prefixes and not file.name.startswith(jar_prefixes[file.parent]):
                continue
            bundle.write(file, relative.as_posix())
    for name in ['README.md', '.gitignore']:
        bundle.write(root/name, name)
with zipfile.ZipFile(archive) as bundle:
    assert bundle.testzip() is None
    assert not any('/run/' in n or '/.gradle/' in n or n.startswith('tools/') for n in bundle.namelist())
files = [archive, *(file for directory, prefix in jar_prefixes.items() for file in directory.glob(f'{prefix}*.jar'))]
(output/'SHA256SUMS.txt').write_text(''.join(
    hashlib.sha256(file.read_bytes()).hexdigest()+'  '+file.relative_to(root).as_posix()+'\n'
    for file in files
))
print(f'Created {archive.name}: {archive.stat().st_size} bytes; CRC verified')
