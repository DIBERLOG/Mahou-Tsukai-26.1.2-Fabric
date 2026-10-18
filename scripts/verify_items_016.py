from pathlib import Path
import hashlib,json,zipfile,shutil
root=Path.cwd();version='1.36.36-fabric.0.16-incomplete';jar=root/'MahouTsukai-Fabric/build/libs'/f'MahouTsukai-Fabric-{version}.jar'
assert 'BUILD SUCCESSFUL' in (root/'analysis/0.16-final-build-server.log').read_text(encoding='utf-8')
assert 'BUILD SUCCESSFUL' in (root/'analysis/0.16-package-build.log').read_text(encoding='utf-8')
assert hashlib.sha256((root/'original/mahoutsukai-26.1.2-v1.36.36.jar').read_bytes()).hexdigest()=='7bccdce504cf01a5edf0bca7ae6d647c09ff8dca67425577804cc3132a6c090a'
with zipfile.ZipFile(jar) as z:
 assert z.testzip() is None
 assert json.loads(z.read('fabric.mod.json'))['version']==version
 names=z.namelist();assert not any('VisualCapture' in n or 'Integration.class' in n or 'SelfCheck.class' in n for n in names)
 for cls in ['item/MahouTsukaiGuidebook','item/mysticcode/MysticCode','item/mysticcode/MysticCodeFirstSorcery','item/emrys/StaffEmrys','item/replica/Replica','item/nobu/Nobu','item/william/William','render/gui/ReplicaGui','render/gui/WheelGui','render/gui/guide/GuidebookInstance' if False else 'item/guide/GuidebookInstance']:
  assert 'stepsword/mahoutsukai/'+cls+'.class' in names,cls
 for resource in ['assets/mahoutsukai/items/nobu.json','assets/mahoutsukai/items/william.json','data/mahoutsukai/recipe/william_book_cloning.json','mahoutsukai.fabric.mixins.json']:
  assert z.read(resource)==(root/'MahouTsukai-Fabric/src/main/resources'/resource).read_bytes(),resource
inventory=json.loads((root/'analysis/feature-inventory.json').read_text(encoding='utf-8'))
report={'version':version,'jar':jar.relative_to(root).as_posix(),'jar_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'jar_bytes':jar.stat().st_size,'original_items_registered':sum(i['status']=='registered' for i in inventory['original_items']),'original_items_total':len(inventory['original_items']),'original_ritual_families_checked':53,'original_entities_checked':25,'original_entities_total':27,'original_source_jar_unchanged':True,'production_jar_excludes_test_classes':True,'server_integration':'analysis/0.16-final-build-server.log','package_build':'analysis/0.16-package-build.log','native_client_proofs':['analysis/0.16-items-guide-open.passed','analysis/0.16-items-models-check.passed','analysis/0.16-items-mystic-menu-check.passed','analysis/0.16-native-nobu-shot.json'],'whole_mod_complete':False}
(root/'analysis/0.16-release-verification.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8');print(json.dumps(report,indent=2))
