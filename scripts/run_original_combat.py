"""A separate oracle world; the source JAR and existing golden fixtures stay untouched."""
from pathlib import Path
import hashlib, shutil, subprocess, zipfile
ROOT=Path(__file__).resolve().parents[1]
RUN=ROOT/'tools/neoforge-combat-parity'
BASE=ROOT/'tools/neoforge-parity'
JAVA=ROOT/'tools/jdk/jdk-25.0.4.1+1/bin'
RUN.mkdir(exist_ok=True)
if not (RUN/'libraries').exists():
 subprocess.run(['powershell','-NoProfile','-Command',f"New-Item -ItemType Junction -Path '{RUN/'libraries'}' -Target '{BASE/'libraries'}' | Out-Null"],check=True)
mods=RUN/'mods';mods.mkdir(exist_ok=True)
source=next((ROOT/'original').glob('*.jar'));before=hashlib.sha256(source.read_bytes()).hexdigest()
target=mods/source.name
if not target.exists() or target.read_bytes()!=source.read_bytes():shutil.copy2(source,target)
(RUN/'server.properties').write_text('server-ip=127.0.0.1\nserver-port=25599\nonline-mode=false\nlevel-name=combat-oracle\nview-distance=2\nsimulation-distance=2\npvp=true\nmax-tick-time=-1\n')
(RUN/'eula.txt').write_text('eula=true\n')
output=RUN/'probe-classes';output.mkdir(exist_ok=True)
jars=list((BASE/'libraries').rglob('*.jar'));priority=[p for p in jars if 'minecraft-server-patched' in str(p) or p.name in ['gson-2.13.2.jar','neoforge-26.1.2.122-universal.jar']]
classpath=';'.join(str(p.resolve()) for p in priority+[p for p in jars if p not in priority]+[source])
args=['-encoding','UTF-8','-cp',classpath,'-d',str(output),str(ROOT/'scripts/shared/CombatProbe.java'),str(ROOT/'scripts/parity/OriginalCombatProbe.java')]
argfile=RUN/'javac.args';argfile.write_text('\n'.join('"'+s.replace('\\','/')+'"' for s in args))
subprocess.run([str(JAVA/'javac.exe'),'@'+str(argfile)],check=True)
with zipfile.ZipFile(mods/'combat-probe.jar','w') as z:
 for p in output.rglob('*.class'):z.write(p,p.relative_to(output).as_posix())
 z.writestr('META-INF/neoforge.mods.toml','modLoader="javafml"\nloaderVersion="[1,)"\nlicense="All Rights Reserved"\n[[mods]]\nmodId="mahou_combat_probe"\nversion="1"\ndisplayName="Local combat oracle"\n')
marker=ROOT/'analysis/phase025-original-combat.passed';marker.unlink(missing_ok=True)
with (ROOT/'analysis/phase025-original-combat.log').open('w',encoding='utf-8') as log:
 subprocess.run([str(JAVA/'java.exe'),'-Xmx2G','@libraries/net/neoforged/neoforge/26.1.2.122/win_args.txt','nogui'],cwd=RUN,stdout=log,stderr=subprocess.STDOUT,check=True,timeout=600)
assert marker.is_file(),'Original combat probe did not complete'
assert hashlib.sha256(source.read_bytes()).hexdigest()==before
print(marker.read_text())
