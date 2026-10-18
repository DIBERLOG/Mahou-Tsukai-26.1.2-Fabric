"""List recovered source files that are actually included in the Fabric build."""
from pathlib import Path
import hashlib
import json

root = Path(__file__).resolve().parents[1]
recovered = root/'recovered'
adapted = root/'MahouTsukai-Fabric/src/main/java'
rows = []
for original in sorted(recovered.rglob('*.java')):
    relative = original.relative_to(recovered)
    client_target = root/'MahouTsukai-Fabric/src/client/java'/relative
    target = adapted/relative if (adapted/relative).exists() else client_target
    rows.append({
        'source': relative.as_posix(),
        'status': 'included-in-fabric-build' if target.exists() else 'recovered-only',
        'changed': target.read_bytes() != original.read_bytes() if target.exists() else None,
        'recovered_sha256': hashlib.sha256(original.read_bytes()).hexdigest(),
    })
(root/'analysis/source-coverage.json').write_text(json.dumps(rows, indent=2)+'\n')
included = [row['source'] for row in rows if row['status'] == 'included-in-fabric-build']
(root/'analysis/adapted-source-files.txt').write_text('\n'.join(included)+'\n')
print(f'{len(included)} of {len(rows)} recovered top-level Java files included; inclusion is not functional parity')
