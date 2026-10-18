"""Run unmodified Mahou/Jousting beside a probe in a separate NeoForge world."""
from pathlib import Path
import shutil,subprocess,zipfile
ROOT=Path(__file__).resolve().parents[1]
RUN=ROOT/'tools/neoforge-optional-parity'
BASE=ROOT/'tools/neoforge-parity'
JAVA=ROOT/'tools/jdk/jdk-25.0.4.1+1/bin'
RUN.mkdir(exist_ok=True)
assert (RUN/'libraries').is_dir(), 'Create the libraries junction to the existing NeoForge installation first'
mods=RUN/'mods';mods.mkdir(exist_ok=True)
for jar in [next((ROOT/'original').glob('*.jar')),ROOT/'tools/compat/jousting-26.1.2-v1.0.12.jar']:
 target=mods/jar.name
 if not target.exists() or target.read_bytes()!=jar.read_bytes():shutil.copy2(jar,target)
(RUN/'server.properties').write_text('server-ip=127.0.0.1\nserver-port=25597\nonline-mode=false\nlevel-name=optional-oracle\nview-distance=2\nsimulation-distance=2\n')
(RUN/'eula.txt').write_text('eula=true\n')
output=RUN/'probe-classes';output.mkdir(exist_ok=True)
jars=list((BASE/'libraries').rglob('*.jar'));priority=[p for p in jars if 'minecraft-server-patched' in str(p) or p.name in ['gson-2.13.2.jar','neoforge-26.1.2.122-universal.jar']]
classpath=';'.join(str(p.resolve()) for p in priority+[p for p in jars if p not in priority]+list(mods.glob('*.jar')))
args=['-encoding','UTF-8','-cp',classpath,'-d',str(output),str(ROOT/'scripts/parity/OriginalOptionalProbe.java')]
argfile=RUN/'javac.args';argfile.write_text('\n'.join('"'+s.replace('\\','/')+'"' for s in args))
subprocess.run([str(JAVA/'javac.exe'),'@'+str(argfile)],check=True)
with zipfile.ZipFile(mods/'optional-probe.jar','w') as z:
 for p in output.rglob('*.class'):z.write(p,p.relative_to(output).as_posix())
 z.writestr('META-INF/neoforge.mods.toml','modLoader="javafml"\nloaderVersion="[1,)"\nlicense="All Rights Reserved"\n[[mods]]\nmodId="mahou_optional_probe"\nversion="1"\ndisplayName="Local optional integration oracle"\n')
marker=ROOT/'analysis/phase023-original-optional.passed';marker.unlink(missing_ok=True)
with (ROOT/'analysis/phase023-original-optional.log').open('w',encoding='utf-8') as log:
 subprocess.run([str(JAVA/'java.exe'),'-Xmx2G','@libraries/net/neoforged/neoforge/26.1.2.122/win_args.txt','nogui'],cwd=RUN,stdout=log,stderr=subprocess.STDOUT,check=True)
assert marker.is_file(),'Original optional probe did not complete'
print(marker.read_text())
