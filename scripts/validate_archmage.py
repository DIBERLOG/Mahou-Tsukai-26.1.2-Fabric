"""Validate the actual editable rig, atlas pixels/UV islands, hierarchy and animation channels."""
from pathlib import Path
import base64, json, hashlib, math
from PIL import Image
root=Path(__file__).resolve().parents[1];out=root/'illager_archmage'
p=out/'models/illager_archmage.bbmodel';model=json.loads(p.read_text(encoding='utf-8'))
groups={g['uuid']:g for g in model['groups']};cubes={c['uuid']:c for c in model['elements']}
assert len(groups)==10 and len(cubes)==50
assert not set(groups)&set(cubes),'Group/cube UUID collision'
required={'head','body','left_arm','right_arm','left_leg','right_leg','shoulder_armor','cloak','staff','root_motion'}
assert {g['name'] for g in groups.values()}==required
seen=[];parents={}
def walk(node,parent=None):
 if isinstance(node,str):assert node in cubes;seen.append(node);return
 id=node['uuid'];assert id in groups;seen.append(id);parents[groups[id]['name']]=None if parent is None else groups[parent]['name']
 for child in node['children']:walk(child,id)
for node in model['outliner']:walk(node)
assert len(seen)==len(set(seen))==60
assert parents['staff']=='right_arm' and parents['head']=='body'
images=[]
for tex in model['textures']:
 raw=base64.b64decode(tex['source'].split(',')[1]);path=out/'textures'/tex['name'];assert path.read_bytes()==raw
 image=Image.open(path).convert('RGBA');assert image.size==(tex['uv_width'],tex['uv_height']);assert len(image.getcolors(100000))>10
 images.append(image)
used=[set(),set()];faces=0
for c in cubes.values():
 assert all(b>a for a,b in zip(c['from'],c['to']))
 assert len(c['origin'])==3 and all(math.isfinite(v) for v in c['origin'])
 for face in c['faces'].values():
  texture=face['texture'];image=images[texture];x,y,X,Y=map(int,face['uv']);assert 0<=x<X<=image.width and 0<=y<Y<=image.height
  pixels={(a,b) for a in range(x,X) for b in range(y,Y)}
  assert not pixels&used[texture],f'UV overlap: {c["name"]}'
  assert all(image.getpixel(v)[3]==255 for v in pixels),f'Unpainted face: {c["name"]}'
  used[texture]|=pixels;faces+=1
names={'idle','walk','cast_spell','staff_attack','levitation','summon_pillars','area_attack','death','golden_volley','summon_vex','arrive','hurt_reaction','cast_recovery','combat_idle'}
assert {a['name'] for a in model['animations']}==names
keys=0
for a in model['animations']:
 assert a['length']>0 and a['animators']
 moving=False
 for bone,track in a['animators'].items():
  assert bone in groups and track['type']=='bone'
  channels={}
  for k in track['keyframes']:
   assert 0<=k['time']<=a['length'];assert k['channel'] in ('rotation','position')
   vals=tuple(float(k['data_points'][0][axis]) for axis in ('x','y','z'));assert all(math.isfinite(v) for v in vals)
   channels.setdefault(k['channel'],[]).append((k['time'],vals));keys+=1
  for channel,values in channels.items():
   values.sort();assert len({t for t,v in values})==len(values)
   moving|=len({v for t,v in values})>1
   if a['loop']=='loop':assert values[0][1]==values[-1][1],f'Loop seam: {a["name"]}'
 assert moving,f'Static placeholder animation: {a["name"]}'

# Read the exported source, then independently sample arm/wrist rotations through each clip.
# Resting poses stay upright; combat clips contain deliberate, bounded wrist gestures.
source=json.loads((out/'model-source.json').read_text(encoding='utf-8'))
staff=source['bones']['staff'];hand=next(c for c in source['cubes'] if c['name']=='right_hand');shaft=next(c for c in source['cubes'] if c['name']=='staff_shaft')
assert all(a<=v<=b for a,v,b in zip(hand['from'],staff['origin'],hand['to'])),'Staff pivot must remain inside the fist'
assert all(abs((shaft['from'][i]+shaft['to'][i])/2-staff['origin'][i])<1e-8 for i in (0,2)),'Shaft must pass through grip pivot'
def rotation(value):
 x,y,z=map(math.radians,value);cx,sx=math.cos(x),math.sin(x);cy,sy=math.cos(y),math.sin(y);cz,sz=math.cos(z),math.sin(z)
 return [[cz*cy,cz*sy*sx-sz*cx,cz*sy*cx+sz*sx],[sz*cy,sz*sy*sx+cz*cx,sz*sy*cx-cz*sx],[-sy,cy*sx,cy*cx]]
def sample(tr,t):
 if t<=tr['times'][0]:return tr['values'][0]
 for i in range(1,len(tr['times'])):
  if t<=tr['times'][i]:
   p=(t-tr['times'][i-1])/(tr['times'][i]-tr['times'][i-1])
   if tr.get('interpolation')!='linear':p=p*p*(3-2*p)
   return [a+p*(b-a) for a,b in zip(tr['values'][i-1],tr['values'][i])]
 return tr['values'][-1]
worst_tilt=0;staff_samples=0;gesture_tilts={}
for animation in source['animations']:
 if animation['name'] in ('staff_attack','death'):continue
 arm=next((tr for tr in animation['tracks'] if tr['bone']=='right_arm' and tr['channel']=='rotation'),None)
 if arm is None:continue
 wrist=next(tr for tr in animation['tracks'] if tr['bone']=='staff' and tr['channel']=='rotation')
 max_tilt=0
 for i in range(401):
  t=animation['length']*i/400;a=rotation(sample(arm,t));b=rotation(sample(wrist,t))
  axis=[sum(a[row][k]*b[k][1] for k in range(3)) for row in range(3)]
  max_tilt=max(max_tilt,math.degrees(math.acos(max(-1,min(1,axis[1])))));staff_samples+=1
 if animation['name'] in ('idle','walk','levitation','combat_idle'):
  worst_tilt=max(worst_tilt,max_tilt)
 else:gesture_tilts[animation['name']]=round(max_tilt,4)
assert worst_tilt<.6,f'Authored staff tilts in a resting/walking clip: {worst_tilt}'
for name in ('cast_spell','summon_pillars','golden_volley','area_attack','summon_vex'):
 assert 15<gesture_tilts[name]<65,f'Casting staff gesture must be intentional and bounded: {name}'
lookup={a['name']:a for a in source['animations']}
arrival=lookup['arrive'];arrival_position=next(t for t in arrival['tracks'] if t['bone']=='root_motion' and t['channel']=='position')
assert arrival['length']==2.5 and arrival_position['values'][0][1]<0 and arrival_position['values'][-1]==[0,0,0],'Arrival must rise from below ground to the exact resting height'
recovery=lookup['cast_recovery']
for name in ('cast_spell','summon_pillars','golden_volley','area_attack','summon_vex'):
 for bone in ('left_arm','right_arm','staff'):
  release=next(t for t in lookup[name]['tracks'] if t['bone']==bone and t['channel']=='rotation')
  recover=next(t for t in recovery['tracks'] if t['bone']==bone and t['channel']=='rotation')
  assert all(abs(a-b)<1e-6 for a,b in zip(release['values'][-1],recover['values'][0])),f'Release/recovery discontinuity: {name} {bone}'
for name in ('hurt_reaction','cast_recovery'):
 assert all(all(abs(v)<1e-6 for v in t['values'][-1]) for t in lookup[name]['tracks']),f'{name} must settle back to resting transforms'
body=[c for c in cubes.values() if not c['name'].startswith(('staff_','cage_','crystal_'))]
assert min(c['from'][1] for c in body)==0 and max(c['to'][1] for c in body)==48
for name in ('front','back','side'):
 image=Image.open(out/f'previews/{name}.png');assert image.size==(768,1024) and image.getbbox()
receipt={'status':'PASS','cuboids':len(cubes),'bones':len(groups),'faces':faces,'keyframes':keys,'animations':sorted(names),'height_blocks':3,'uv_overlap_pixels':0,'unpainted_face_pixels':0,'staff_grip_inside_fist':True,'authored_staff_samples':staff_samples,'authored_resting_staff_max_tilt_degrees':round(worst_tilt,5),'casting_staff_max_tilt_degrees':gesture_tilts,'attack_animation_mapping':{'1':'cast_spell','2':'summon_pillars','3':'golden_volley','4':'area_attack','5':'summon_vex'},'arrival_finishes_at_resting_height':True,'release_recovery_arm_and_grip_continuity':True,'model_sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'checks':['unique UUIDs and complete hierarchy','parented staff and head','positive cuboid volumes and finite pivots','per-face atlas bounds and zero UV intersections','embedded PNGs match external files','all face pixels opaque','14 non-static bone animations','loop endpoint continuity','upright shaft during resting, walking and normal levitation','five distinct bounded casting wrist gestures','staff grip inside fist and shaft centered on pivot','arrival rises from ground and finishes at resting height','release/recovery arm and grip continuity','hurt and recovery settle to rest','48-unit body height','three geometry-based preview PNGs'],'editor_opened':False}
(out/'validation.json').write_text(json.dumps(receipt,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(json.dumps(receipt,ensure_ascii=False))
