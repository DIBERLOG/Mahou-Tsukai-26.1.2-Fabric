"""Run the existing compiled Loom server test when Gradle's UDP lock service cannot bind.

Requires a successful prior build. Uses the actual generated launch configuration and
classpath, removing only the four optional integration JARs. Does not modify system services.
"""
from pathlib import Path
import subprocess

ROOT=Path(__file__).resolve().parents[1]
PROJECT=ROOT/'MahouTsukai-Fabric'
RUN=PROJECT/'run/economy-checks'
args=(PROJECT/'build/loom-cache/argFiles/runEconomyChecks').read_text().splitlines()
assert len(args)==2 and args[0]=='-classpath', 'Unexpected generated Loom argument format'
entries=args[1].replace('"','').split(';')
optional={'apotheosis-adventure-fabric-0.2.1-public.jar','cardinal-components-api-8.0.1.jar',
          'placebo-fabric-0.1.3.jar','Jousting-Fabric-1.0.12-fabric.0.1-incomplete.jar'}
removed={Path(entry).name for entry in entries if Path(entry).name in optional}
assert removed==optional, 'Use the launch arguments from the successful optional integration run'
classpath=';'.join(entry for entry in entries if Path(entry).name not in optional)
marker=RUN/'economy-checks.passed';marker.unlink(missing_ok=True)
log=ROOT/'analysis/phase024-five-minute-base-direct.log'
command=[str(ROOT/'tools/jdk/jdk-25.0.4.1+1/bin/java.exe'),'-Xmx2G',
         f'-Dfabric.dli.config={PROJECT/".gradle/loom-cache/launch.cfg"}',
         '-Dfabric.dli.env=server','-Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotServer',
         '-Djava.net.preferIPv4Stack=true','--enable-native-access=ALL-UNNAMED',
         '--sun-misc-unsafe-memory-access=allow','-XX:StackShadowPages=32',
         '-cp',classpath,'net.fabricmc.devlaunchinjector.Main','nogui']
with log.open('w',encoding='utf-8') as output:
 subprocess.run(command,cwd=RUN,stdout=output,stderr=subprocess.STDOUT,check=True,timeout=120)
contents=log.read_text(encoding='utf-8',errors='replace')
assert marker.is_file() and 'PASS: ECONOMY_INTEGRATION' in contents, 'Prepared base checks did not complete'
assert '\t- jousting ' not in contents and '\t- apotheosis ' not in contents, 'Optional mods still loaded'
assert 'five-minute native cooldown boundary and saved deadline' in contents, 'Latest cooldown checks did not run'
receipt='PASS: existing compiled Loom economy test, same generated launch config, optional JARs absent; full server suite and exact five-minute native cooldown boundary passed. Gradle UDP bootstrap was bypassed; test assertions were unchanged.\n'
(ROOT/'analysis/phase024-five-minute-base-direct.passed').write_text(receipt)
print(receipt.strip())
