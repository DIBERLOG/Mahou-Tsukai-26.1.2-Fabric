"""Author the editable Blockbench rig, pixel atlases and native Java model from one source."""
from pathlib import Path
from PIL import Image, ImageDraw
import base64, hashlib, json, math, random, shutil, uuid

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'illager_archmage'
ASSETS=ROOT/'MahouTsukai-Fabric/src/main/resources/assets/mahoutsukai'
JAVA=ROOT/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/render/entity/archmage'
for p in [OUT/'models',OUT/'textures',OUT/'animations',OUT/'previews',JAVA]:p.mkdir(parents=True,exist_ok=True)
uid=lambda name:str(uuid.uuid5(uuid.NAMESPACE_URL,'mahou/illager_archmage/'+name))
groups={}
def bone(name,origin,parent=None):groups[name]={'name':name,'origin':origin,'parent':parent,'uuid':uid(name),'children':[]}
bone('root_motion',[0,4,0]);bone('body',[0,30,0],'root_motion');bone('head',[0,36,0],'body')
bone('left_arm',[10,34,0],'body');bone('right_arm',[-10,34,0],'body')
bone('left_leg',[4,16,0],'root_motion');bone('right_leg',[-4,16,0],'root_motion')
bone('shoulder_armor',[0,34,0],'body');bone('cloak',[0,35,5],'body')
bone('staff',[-11,18,-3],'right_arm')
cubes=[]
def box(b,name,a,bounds,material='robe',texture=0):
    cubes.append({'name':name,'bone':b,'from':a,'to':bounds,'material':material,'texture':texture,'uuid':uid('cube/'+name)})
box('body','robe_torso',[-7,16,-4],[7,36,4])
box('body','gold_front',[-2,19,-5],[2,35,-4],'gold')
box('body','collar_left',[-6,33,-5],[-2,36,-4],'gold')
box('body','collar_right',[2,33,-5],[6,36,-4],'gold')
box('body','collar_center',[-2,31,-6],[2,34,-5],'gold')
box('body','gold_clasp',[-1,29,-6],[1,31,-5],'gold')
box('body','belt',[-7,17,-6],[7,19,6],'gold_dark')
box('head','illager_head',[-6,36,-5],[6,48,5],'skin')
box('head','nose',[-2,35,-9],[2,43,-5],'nose')
for side,x in [('right',-5),('left',2)]:
    box('head',side+'_eye_white',[x,40,-6],[x+3,42,-5],'white')
    box('head',side+'_iris',[x+1,40,-7],[x+2,42,-6],'eye')
    box('head',side+'_brow',[x,43,-6],[x+4,45,-5],'black')
for name,s in [('left',1),('right',-1)]:
    x0,x1=sorted([8*s,13*s])
    box(name+'_arm',name+'_sleeve',[x0,20,-3],[x1,34,3])
    box(name+'_arm',name+'_cuff',[x0-1,19,-4],[x1+1,22,4],'gold_dark')
    box(name+'_arm',name+'_hand',[x0,16,-3],[x1,19,3],'skin')
    xa,xb=(0,7) if s==1 else (-7,0)
    box(name+'_leg',name+'_leg',[xa,0,-3],[xb,16,3],'boots')
    box(name+'_leg',name+'_skirt',[xa,5,-5],[xb,18,5],'robe')
    box(name+'_leg',name+'_hem',[xa,3,-5],[xb,5,5],'gold_dark')
    xa,xb=sorted([7*s,15*s])
    box('shoulder_armor',name+'_pauldron',[xa,32,-5],[xb,35,5],'gold_dark')
    box('shoulder_armor',name+'_pauldron_top',[xa+1,35,-4],[xb-1,37,4],'gold')
    box('shoulder_armor',name+'_pauldron_inset',[xa+1,32,-6],[xb-1,34,-5],'robe')
box('cloak','cloak_panel',[-9,5,5],[9,35,6],'cloak')
box('cloak','cloak_left_tail',[-9,2,5],[-4,9,6],'cloak')
box('cloak','cloak_right_tail',[4,2,5],[9,9,6],'cloak')
box('cloak','cloak_hem',[-9,4,6],[9,6,7],'gold_dark')
box('staff','staff_shaft',[-12,1,-4],[-10,43,-2],'wood',1)
for y in [3,15,33]:box('staff',f'staff_ring_{y}',[-13,y,-5],[-9,y+2,-1],'gold',1)
for x in [-16,-8]:box('staff',f'cage_side_{x}',[x,42,-4],[x+2,50,-2],'gold_dark',1)
for y in [41,50]:box('staff',f'cage_cap_{y}',[-16,y,-5],[-6,y+2,-1],'gold',1)
for y,w in [(43,2),(44,4),(45,6),(47,4),(49,2)]:
    box('staff',f'crystal_{y}',[-11-w/2,y,-3-w/2],[-11+w/2,y+2,-3+w/2],'crystal',1)

pal={'robe':(29,29,34),'black':(16,17,20),'skin':(111,117,120),'nose':(99,105,107),'white':(185,194,184),'eye':(28,167,63),'gold':(211,172,84),'gold_dark':(158,121,55),'boots':(61,64,66),'cloak':(22,23,29),'wood':(78,54,40),'crystal':(255,211,80)}
sizes=[256,128];images=[Image.new('RGBA',(n,n)) for n in sizes]
emissions=[Image.new('RGBA',(n,n)) for n in sizes]
cursor=[[2,2,0],[2,2,0]]
faces={}
for cube in cubes:
    w,h,d=[int(b-a) for a,b in zip(cube['from'],cube['to'])]
    netw,neth=2*(w+d),h+d
    t=cube['texture'];x,y,rowh=cursor[t];n=sizes[t]
    if x+netw+2>n:x,y,rowh=2,y+rowh+2,0
    assert y+neth+2<=n,(cube['name'],'atlas full')
    cube['uv_offset']=[x,y];cursor[t]=[x+netw+2,y,max(rowh,neth)]
    rects={'west':[x,y+d,x+d,y+d+h],'north':[x+d,y+d,x+d+w,y+d+h],'east':[x+d+w,y+d,x+2*d+w,y+d+h],'south':[x+2*d+w,y+d,x+2*d+2*w,y+d+h],'up':[x+d,y,x+d+w,y+d],'down':[x+d+w,y,x+d+2*w,y+d]}
    faces[cube['uuid']]=rects
    for face,rect in rects.items():
        draw=ImageDraw.Draw(images[t]);a,b,c,e=rect;mat=cube['material'];base=pal[mat]
        rng=random.Random(cube['name']+'/'+face)
        for py in range(b,e):
            for px in range(a,c):
                delta=rng.choice([-7,-3,0,0,0,3,6]);col=tuple(max(0,min(255,v+delta)) for v in base)
                if px in (a,c-1) or py in (b,e-1):col=tuple(int(v*.78) for v in col)
                draw.point((px,py),fill=(*col,255))
        if mat in ['robe','cloak'] and c-a>=5 and e-b>=8:
            draw.line([(a+1,b),(a+1,e-1)],fill=(*pal['gold_dark'],255))
            draw.line([(c-2,b),(c-2,e-1)],fill=(*pal['gold_dark'],255))
            for py in range(b+4,e-2,6):
                draw.line([(a+2,py),(a+3,py+1),(a+2,py+2)],fill=(*pal['gold'],255))
        if cube['name']=='cloak_panel' and face=='south':
            cx=(a+c)//2;cy=b+13
            draw.ellipse((cx-5,cy-5,cx+5,cy+5),outline=(*pal['gold'],255),width=1)
            draw.rectangle((cx-1,cy-1,cx+1,cy+1),fill=(*pal['gold'],255))
            draw.line([(cx,cy-9),(cx+2,cy-6),(cx,cy-7),(cx-2,cy-6),(cx,cy-9)],fill=(*pal['gold'],255))
            draw.line([(cx,cy+5),(cx,cy+11)],fill=(*pal['gold'],255),width=1)
            draw.line([(cx-2,cy+9),(cx+2,cy+9)],fill=(*pal['gold'],255))
        if mat in ['crystal','eye']:
            emissions[t].paste(images[t].crop((a,b,c,e)),(a,b))
for i,name in enumerate(['illager_archmage','staff']):images[i].save(OUT/f'textures/{name}.png')
atlas=Image.new('RGBA',(512,256));atlas.paste(images[0],(0,0));atlas.paste(images[1],(256,0))
emissive=Image.new('RGBA',(512,256));emissive.paste(emissions[0],(0,0));emissive.paste(emissions[1],(256,0))
(ASSETS/'textures/entity').mkdir(exist_ok=True)
atlas.save(ASSETS/'textures/entity/illager_archmage.png');emissive.save(ASSETS/'textures/entity/illager_archmage_emissive.png')
shutil.copy2(ASSETS/'textures/particle/fog3.png',ASSETS/'textures/item/fog_projector_icon.png')

# Every animation has actual bone keyframes. The same curves are exported into the Java rig.
animations=[]
def anim(name,length,tracks,loop=True):
    data={'name':name,'length':length,'loop':loop,'tracks':tracks};animations.append(data);return data
def track(bone,channel,times,values):return {'bone':bone,'channel':channel,'times':times,'values':values}
anim('idle',4,[track('body','position',[0,2,4],[[0,0,0],[0,.35,0],[0,0,0]]),track('cloak','rotation',[0,2,4],[[2,0,0],[6,0,0],[2,0,0]]),track('head','rotation',[0,2,4],[[0,-2,0],[1,2,0],[0,-2,0]])])
walk=[]
for b,sign in [('left_leg',1),('right_leg',-1),('left_arm',-1),('right_arm',1)]:
    swing=7 if b=='right_arm' else 23
    walk.append(track(b,'rotation',[0,.3,.6,.9,1.2],[[0,0,0],[swing*sign,0,0],[0,0,0],[-swing*sign,0,0],[0,0,0]]))
walk.append(track('cloak','rotation',[0,.6,1.2],[[8,0,0],[16,0,0],[8,0,0]]));anim('walk',1.2,walk)
anim('cast_spell',1.4,[track('left_arm','rotation',[0,.5,1,1.4],[[0,0,0],[-90,0,-35],[-100,0,-30],[0,0,0]]),track('right_arm','rotation',[0,.5,1,1.4],[[0,0,0],[-30,0,18],[-40,0,12],[0,0,0]]),track('head','rotation',[0,.6,1.4],[[0,0,0],[-8,0,0],[0,0,0]])],False)
anim('staff_attack',.8,[track('right_arm','rotation',[0,.25,.4,.8],[[0,0,0],[-65,-20,15],[35,20,-15],[0,0,0]]),track('body','rotation',[0,.25,.4,.8],[[0,0,0],[0,-16,0],[0,12,0],[0,0,0]])],False)
anim('levitation',3,[track('root_motion','position',[0,1.5,3],[[0,2,0],[0,5,0],[0,2,0]]),track('cloak','rotation',[0,1.5,3],[[20,0,0],[30,0,0],[20,0,0]]),track('left_arm','rotation',[0,1.5,3],[[0,0,-25],[-12,0,-35],[0,0,-25]]),track('right_arm','rotation',[0,1.5,3],[[-15,0,20],[-25,0,30],[-15,0,20]])])
anim('summon_pillars',1.6,[track('left_arm','rotation',[0,.6,1,1.6],[[0,0,0],[-125,0,-35],[-20,0,-65],[0,0,0]]),track('right_arm','rotation',[0,.6,1,1.6],[[0,0,0],[-100,0,20],[-30,0,10],[0,0,0]]),track('cloak','rotation',[0,.8,1.6],[[4,0,0],[25,0,0],[4,0,0]])],False)
anim('area_attack',1.5,[track('body','rotation',[0,.5,.9,1.5],[[0,0,0],[0,-25,0],[0,30,0],[0,0,0]]),track('right_arm','rotation',[0,.5,.9,1.5],[[0,0,0],[-100,0,45],[25,0,25],[0,0,0]]),track('left_arm','rotation',[0,.5,.9,1.5],[[0,0,0],[-40,0,-55],[10,0,-70],[0,0,0]])],False)
anim('golden_volley',1.6,[track('body','rotation',[0,.5,1.1,1.6],[[0,0,0],[0,-18,0],[0,8,0],[0,0,0]]),track('left_arm','rotation',[0,.5,1.1,1.6],[[0,0,0],[-80,14,-12],[-95,-5,-8],[0,0,0]]),track('right_arm','rotation',[0,.5,1.1,1.6],[[0,0,0],[-25,0,10],[-40,0,10],[0,0,0]]),track('head','rotation',[0,.5,1.1,1.6],[[0,0,0],[-4,15,0],[-3,-6,0],[0,0,0]])],False)
anim('summon_vex',1.6,[track('left_arm','rotation',[0,.6,1.2,1.6],[[0,0,0],[-155,0,-25],[-125,0,-55],[0,0,0]]),track('right_arm','rotation',[0,.6,1.2,1.6],[[0,0,0],[-15,0,30],[-25,0,40],[0,0,0]]),track('head','rotation',[0,.6,1.2,1.6],[[0,0,0],[-25,0,-6],[-12,0,6],[0,0,0]]),track('cloak','rotation',[0,.8,1.6],[[4,0,0],[32,0,0],[4,0,0]])],False)
anim('arrive',2.5,[track('root_motion','position',[0,.6,1.4,2.5],[[0,-10,0],[0,-5,0],[0,1.3,0],[0,0,0]]),track('root_motion','rotation',[0,.6,1.4,2.5],[[0,-40,0],[0,-15,0],[0,8,0],[0,0,0]]),track('left_arm','rotation',[0,.6,1.4,2.5],[[-55,0,-15],[-130,0,-40],[-35,0,-18],[0,0,0]]),track('right_arm','rotation',[0,.6,1.4,2.5],[[-20,0,12],[-45,0,25],[-20,0,8],[0,0,0]]),track('cloak','rotation',[0,.6,1.4,2.5],[[8,0,0],[42,0,0],[18,0,0],[4,0,0]]),track('head','rotation',[0,.6,1.4,2.5],[[20,0,0],[-15,0,0],[-4,0,0],[0,0,0]])],False)
anim('hurt_reaction',.35,[track('body','rotation',[0,.1,.35],[[0,0,0],[-7,0,-5],[0,0,0]]),track('head','rotation',[0,.1,.35],[[0,0,0],[-12,5,0],[0,0,0]]),track('right_arm','rotation',[0,.1,.35],[[0,0,0],[-8,0,10],[0,0,0]]),track('left_arm','rotation',[0,.1,.35],[[0,0,0],[-12,0,-8],[0,0,0]]),track('cloak','rotation',[0,.1,.35],[[0,0,0],[14,0,0],[0,0,0]])],False)
# All five releases flow into a common lowered-hands recovery without snapping to idle.
for a in animations:
    if a['name'] in ('cast_spell','summon_pillars','golden_volley','area_attack','summon_vex'):
        for tr in a['tracks']:
            if tr['channel']=='rotation' and tr['bone']=='right_arm':tr['values'][-1]=[-20,0,8]
            if tr['channel']=='rotation' and tr['bone']=='left_arm':tr['values'][-1]=[-35,0,-18]
anim('cast_recovery',.6,[track('right_arm','rotation',[0,.25,.6],[[-20,0,8],[-12,0,5],[0,0,0]]),track('left_arm','rotation',[0,.25,.6],[[-35,0,-18],[-18,0,-10],[0,0,0]]),track('cloak','rotation',[0,.25,.6],[[8,0,0],[12,0,0],[0,0,0]])],False)
anim('combat_idle',3.2,[track('body','rotation',[0,1.6,3.2],[[0,-3,0],[0,3,0],[0,-3,0]]),track('head','rotation',[0,1.6,3.2],[[0,3,0],[-3,-3,0],[0,3,0]]),track('right_arm','rotation',[0,1.6,3.2],[[-5,0,5],[-9,0,7],[-5,0,5]]),track('left_arm','rotation',[0,1.6,3.2],[[-8,0,-8],[-14,0,-12],[-8,0,-8]]),track('cloak','rotation',[0,1.6,3.2],[[7,0,0],[10,0,0],[7,0,0]])])
anim('death',1,[track('root_motion','rotation',[0,.35,1],[[0,0,0],[20,0,0],[80,0,0]]),track('root_motion','position',[0,.35,1],[[0,0,0],[0,-1,0],[0,-2,0]]),track('head','rotation',[0,.5,1],[[0,0,0],[20,0,0],[30,0,0]]),track('right_arm','rotation',[0,.5,1],[[0,0,0],[20,0,25],[45,0,50]]),track('cloak','rotation',[0,.5,1],[[0,0,0],[30,0,0],[65,0,0]])],False)

# Blockbench faces -Z: positive arm X lifts the hands forward; negative cloak X trails behind.
for a in animations:
    for tr in a['tracks']:
        if tr['channel']!='rotation':continue
        if tr['bone'] in ('left_arm','right_arm'):
            for value in tr['values']:
                value[0]=-value[0]
                if a['name']=='walk':value[0]+=28
        if tr['bone']=='cloak':
            for value in tr['values']:value[0]=-abs(value[0])-4

def sample(tr,t):
    if t<=tr['times'][0]:return tr['values'][0]
    for i in range(1,len(tr['times'])):
        if t<=tr['times'][i]:
            p=(t-tr['times'][i-1])/(tr['times'][i]-tr['times'][i-1]);p=p*p*(3-2*p)
            return [a+p*(b-a) for a,b in zip(tr['values'][i-1],tr['values'][i])]
    return tr['values'][-1]

def rotation(value):
    x,y,z=map(math.radians,value);cx,sx=math.cos(x),math.sin(x);cy,sy=math.cos(y),math.sin(y);cz,sz=math.cos(z),math.sin(z)
    return [[cz*cy,cz*sy*sx-sz*cx,cz*sy*cx+sz*sx],[sz*cy,sz*sy*sx+cz*cx,sz*sy*cx-cz*sx],[-sy,cy*sx,cy*cx]]

def wrist_rotation(arm,target):
    # Counter the arm at its grip, then add the deliberate casting/wrist gesture.
    a=rotation(arm);b=rotation(target)
    m=[[sum(a[k][row]*b[k][col] for k in range(3)) for col in range(3)] for row in range(3)]
    return [math.degrees(math.atan2(m[2][1],m[2][2])),math.degrees(math.asin(max(-1,min(1,-m[2][0])))),math.degrees(math.atan2(m[1][0],m[0][0]))]

staff_gestures={
    'cast_spell':[[0,0,0],[-18,0,10],[-35,0,-8],[-8,0,5]],
    'summon_pillars':[[0,0,0],[30,0,15],[-35,0,0],[-8,0,5]],
    'area_attack':[[0,0,0],[-20,0,38],[15,0,-45],[-8,0,5]],
    'golden_volley':[[0,0,0],[-28,0,10],[-48,0,-6],[-8,0,5]],
    'summon_vex':[[0,0,0],[12,0,28],[-12,0,35],[-8,0,5]],
    'arrive':[[-15,0,12],[18,0,25],[-8,0,5],[0,0,0]],
    'hurt_reaction':[[0,0,0],[0,0,6],[0,0,0]],
    'cast_recovery':[[-8,0,5],[-4,0,2],[0,0,0]],
}

# Every wrist gesture is authored in the editor as actual grip-parented keyframes.
# Upright compensation is reserved for the resting/walking/levitating stance.
for a in animations:
    if a['name'] in ('staff_attack','death'):continue
    arm=next((tr for tr in a['tracks'] if tr['bone']=='right_arm' and tr['channel']=='rotation'),None)
    if arm:
        targets=staff_gestures.get(a['name'],[[0,0,0] for _ in arm['times']])
        target=track('staff','rotation',arm['times'],targets)
        times=[round(i*a['length']/math.ceil(a['length']*40),6) for i in range(math.ceil(a['length']*40)+1)]
        counter=track('staff','rotation',times,[wrist_rotation(sample(arm,t),sample(target,t)) for t in times]);counter['interpolation']='linear';a['tracks'].append(counter)

bb_anims=[]
for a in animations:
    animators={}
    for tr in a['tracks']:
        entry=animators.setdefault(uid(tr['bone']),{'name':tr['bone'],'type':'bone','keyframes':[]})
        for i,(ti,value) in enumerate(zip(tr['times'],tr['values'])):
            left=(ti-tr['times'][i-1])/3 if i else 0;right=(tr['times'][i+1]-ti)/3 if i+1<len(tr['times']) else 0
            entry['keyframes'].append({'channel':tr['channel'],'data_points':[dict(zip(['x','y','z'],map(str,value)))],'uuid':uid(a['name']+tr['bone']+tr['channel']+str(ti)),'time':ti,'color':-1,'interpolation':tr.get('interpolation','bezier'),'bezier_linked':False,'bezier_left_time':[-left]*3,'bezier_left_value':[0]*3,'bezier_right_time':[right]*3,'bezier_right_value':[0]*3})
    bb_anims.append({'uuid':uid('animation/'+a['name']),'name':a['name'],'loop':'loop' if a['loop'] else 'once','override':False,'length':a['length'],'snapping':20,'animators':animators})
    (OUT/f'animations/{a["name"]}.json').write_text(json.dumps(a,indent=2)+'\n',encoding='utf-8')
elements=[]
for c in cubes:
    elements.append({'name':c['name'],'type':'cube','uuid':c['uuid'],'from':c['from'],'to':c['to'],'origin':groups[c['bone']]['origin'],'box_uv':False,'rescale':False,'rotation':[0,0,0],'faces':{k:{'uv':r,'texture':c['texture']} for k,r in faces[c['uuid']].items()}})
def hierarchy(g):return {'uuid':g['uuid'],'isOpen':True,'children':[c['uuid'] for c in cubes if c['bone']==g['name']]+[hierarchy(ch) for ch in groups.values() if ch['parent']==g['name']]}
textures=[]
for i,name in enumerate(['illager_archmage','staff']):textures.append({'path':'../textures/'+name+'.png','name':name+'.png','folder':'','namespace':'','id':str(i),'uuid':uid('texture/'+name),'source':'data:image/png;base64,'+base64.b64encode((OUT/f'textures/{name}.png').read_bytes()).decode(),'mode':'bitmap','visible':True,'saved':True,'uv_width':sizes[i],'uv_height':sizes[i],'width':sizes[i],'height':sizes[i]})
project={'meta':{'format_version':'5.0','model_format':'free','box_uv':False},'name':'Illager Archmage','model_identifier':'illager_archmage','visible_box':[4,4,1],'resolution':{'width':256,'height':256},'elements':elements,'groups':[{k:v for k,v in g.items() if k not in ['parent','children']} for g in groups.values()],'outliner':[hierarchy(g) for g in groups.values() if g['parent'] is None],'textures':textures,'animations':bb_anims}
(OUT/'models/illager_archmage.bbmodel').write_text(json.dumps(project,indent=2)+'\n',encoding='utf-8')
(OUT/'model-source.json').write_text(json.dumps({'bones':groups,'cubes':cubes,'animations':animations},indent=2)+'\n',encoding='utf-8')

# Native model: export transforms/UVs from the same rig rather than approximating a second model.
f=lambda x:f'{float(x):.8f}F'
lines=['package stepsword.mahoutsukai.render.entity.archmage;','import net.minecraft.client.model.EntityModel;','import net.minecraft.client.model.geom.*;','import net.minecraft.client.model.geom.builders.*;','import net.minecraft.resources.Identifier;','import org.joml.Quaternionf;','import org.joml.Vector3f;','import stepsword.mahoutsukai.render.entity.RenderRealityMarbleBoss;','/** Generated by scripts/build_archmage.py from the editable Blockbench rig. */','public final class ArchmageModel extends EntityModel<RenderRealityMarbleBoss.State> implements net.minecraft.client.model.ArmedModel<RenderRealityMarbleBoss.State> {',' public static final ModelLayerLocation LAYER=new ModelLayerLocation(Identifier.fromNamespaceAndPath("mahoutsukai","illager_archmage"),"main");',' private final java.util.Map<String,ModelPart> bones=new java.util.HashMap<>();',' public ArchmageModel(ModelPart root){super(root);ModelPart rig=root.getChild("rig");']
for g in groups.values():
    chain=[];c=g
    while c:chain.insert(0,c['name']);c=groups.get(c['parent'])
    lines.append(' bones.put("'+g['name']+'",rig'+''.join('.getChild("'+v+'")' for v in chain)+');')
lines+=[' }',' public static LayerDefinition createBodyLayer(){var mesh=new MeshDefinition();var rig=mesh.getRoot().addOrReplaceChild("rig",CubeListBuilder.create(),PartPose.offset(0,24,0));']
for g in groups.values():
    build='CubeListBuilder.create()'
    # The editable original staff remains a design reference; runtime uses the real Emrys item.
    for c in [c for c in cubes if c['bone']==g['name'] and c['bone']!='staff']:
        a,b=c['from'],c['to'];o=g['origin'];u,v=c['uv_offset'];u+=256*c['texture']
        build+=f'.texOffs({u},{v}).addBox('+','.join(map(f,[a[0]-o[0],o[1]-b[1],a[2]-o[2],b[0]-a[0],b[1]-a[1],b[2]-a[2]]))+')'
    parent=g['parent'] or 'rig';po=groups[g['parent']]['origin'] if g['parent'] else [0,0,0];o=g['origin']
    lines.append(f' var {g["name"]}={parent}.addOrReplaceChild("{g["name"]}",{build},PartPose.offset('+','.join(map(f,[o[0]-po[0],po[1]-o[1],o[2]-po[2]]))+'));')
lines+=[' return LayerDefinition.create(mesh,512,256);}',' private static float curve(float t,float[] keys,float[] values,boolean linear){if(t<=keys[0])return values[0];for(int i=1;i<keys.length;i++)if(t<=keys[i]){float p=(t-keys[i-1])/(keys[i]-keys[i-1]);if(!linear)p=p*p*(3-2*p);return values[i-1]+p*(values[i]-values[i-1]);}return values[values.length-1];}',' public void pose(String name,float seconds,float weight){switch(name){']
for a in animations:
    lines.append(f' case "{a["name"]}" -> {{float t='+('Math.floorMod((long)(seconds*1000),'+str(int(a['length']*1000))+')/1000F' if a['loop'] else 'Math.clamp(seconds,0F,'+f(a['length'])+')')+';')
    for tr in a['tracks']:
        for axis in range(3):
            vals=[v[axis] for v in tr['values']]
            if not any(vals):continue
            field=['xRot','yRot','zRot'][axis] if tr['channel']=='rotation' else ['x','y','z'][axis]
            sign=-1 if (tr['channel']=='position' and axis==1) or (tr['channel']=='rotation' and axis in [0,1]) else 1
            coeff=(math.pi/180 if tr['channel']=='rotation' else 1)*sign
            lines.append(f' bones.get("{tr["bone"]}").{field}+=weight*{f(coeff)}*curve(t,new float[]{{'+','.join(map(f,tr['times']))+'},new float[]{'+','.join(map(f,vals))+'},'+str(tr.get('interpolation')=='linear').lower()+');')
    lines.append(' }')
lines+=[' default -> {} }}',
' public void translateToHand(RenderRealityMarbleBoss.State state,net.minecraft.world.entity.HumanoidArm hand,com.mojang.blaze3d.vertex.PoseStack pose){root.translateAndRotate(pose);root.getChild("rig").translateAndRotate(pose);bones.get("root_motion").translateAndRotate(pose);bones.get("body").translateAndRotate(pose);bones.get(hand==net.minecraft.world.entity.HumanoidArm.RIGHT?"right_arm":"left_arm").translateAndRotate(pose);if(hand==net.minecraft.world.entity.HumanoidArm.RIGHT)bones.get("staff").translateAndRotate(pose);else pose.translate(0,1,0);}',
' private void trailingCloak(){var cloak=bones.get("cloak");cloak.xRot=Math.clamp(cloak.xRot,.08F,.65F);cloak.yRot=0;cloak.z=5;}',
' /** Keep the resting/walking shaft vertical after blending; casts retain authored wrist gestures. */',
' public void uprightStaff(){var arm=bones.get("right_arm");var staff=bones.get("staff");var euler=new Quaternionf().rotationZYX(arm.zRot,arm.yRot,arm.xRot).invert().getEulerAnglesZYX(new Vector3f());staff.xRot=euler.x;staff.yRot=euler.y;staff.zRot=euler.z;}',
' public static String spellAnimation(int kind){return switch(kind){case 2->"summon_pillars";case 3->"golden_volley";case 4->"area_attack";case 5->"summon_vex";default->"cast_spell";};}',
' @Override public void setupAnim(RenderRealityMarbleBoss.State s){',
'  super.setupAnim(s);root.getAllParts().forEach(ModelPart::resetPose);pose("idle",s.ageInTicks/20F,1);',
'  if(s.deathTime>0){pose("death",s.deathTime/20F,1);trailingCloak();return;}',
'  if(s.arrivalTicks>0){pose("arrive",Math.clamp((50-s.arrivalTicks)/20F,0F,2.5F),1);trailingCloak();return;}',
'  boolean action=s.attackTicks>0||s.recoveryTicks>0||s.melee>0;',
'  pose("combat_idle",s.ageInTicks/20F,action?0F:(s.healthRatio>0&&s.healthRatio<.5F?.75F:.3F));',
'  if(!action&&s.walkAnimationSpeed>.01F)pose("walk",s.walkAnimationPos*.12F,Math.min(1,s.walkAnimationSpeed*4));',
'  if(s.levitating)pose("levitation",s.ageInTicks/20F,1);',
'  if(s.attackTicks>0){String animation=spellAnimation(s.attackKind);float duration=s.attackKind==1?1.4F:s.attackKind==4?1.5F:1.6F;float t=Math.clamp((32-s.attackTicks)/32F,0F,1F)*duration;pose(animation,t,1);}',
'  else if(s.recoveryTicks>0)pose("cast_recovery",Math.clamp((12-s.recoveryTicks)/20F,0F,.6F),1);',
'  else uprightStaff();',
'  if(s.melee>0)pose("staff_attack",s.melee*.8F,1);',
'  if(s.hurtTime>0)pose("hurt_reaction",Math.clamp((10-s.hurtTime)/20F,0F,.35F),1);',
'  trailingCloak();',
' }','}']
(JAVA/'ArchmageModel.java').write_text('\n'.join(lines)+'\n',encoding='utf-8')

# Orthographic renders of the actual posed cuboids, sampling every authored UV pixel.
def preview(name,yaw,animation=None,seconds=0):
    canvas=Image.new('RGBA',(768,1024),(21,23,31,255));draw=ImageDraw.Draw(canvas)
    for y in range(100,1000,32):draw.line([(28,y),(740,y)],fill=(27,30,40,255))
    scale=16;angle=math.radians(yaw);co,si=math.cos(angle),math.sin(angle)
    transforms={n:{'position':[0,0,0],'rotation':[0,0,0]} for n in groups}
    if animation:
        for tr in next(a for a in animations if a['name']==animation)['tracks']:
            transforms[tr['bone']][tr['channel']]=sample(tr,seconds)
    def transform(point,bone_name):
        g=groups[bone_name];tr=transforms[bone_name];r=rotation(tr['rotation']);p=[a-b for a,b in zip(point,g['origin'])]
        point=[sum(r[i][j]*p[j] for j in range(3))+g['origin'][i]+tr['position'][i] for i in range(3)]
        return transform(point,g['parent']) if g['parent'] else point
    points=[transform([x,y,z],c['bone']) for c in cubes for x in (c['from'][0],c['to'][0]) for y in (c['from'][1],c['to'][1]) for z in (c['from'][2],c['to'][2])]
    minimum=min(p[0]*co-p[2]*si for p in points);maximum=max(p[0]*co-p[2]*si for p in points)
    bottom=min(p[1] for p in points);top=max(p[1] for p in points)
    scale=min(scale,700/(maximum-minimum),840/(top-bottom));center=384-(minimum+maximum)*scale/2;base=940+bottom*scale
    project=lambda p:(center+(p[0]*co-p[2]*si)*scale,base-p[1]*scale,p[2]*co+p[0]*si)
    polys=[]
    for c in cubes:
        x,y,z=c['from'];X,Y,Z=c['to']
        verts={'north':[(x,Y,z),(X,Y,z),(X,y,z),(x,y,z)],'south':[(X,Y,Z),(x,Y,Z),(x,y,Z),(X,y,Z)],'west':[(x,Y,Z),(x,Y,z),(x,y,z),(x,y,Z)],'east':[(X,Y,z),(X,Y,Z),(X,y,Z),(X,y,z)],'up':[(x,Y,Z),(X,Y,Z),(X,Y,z),(x,Y,z)],'down':[(x,y,z),(X,y,z),(X,y,Z),(x,y,Z)]}
        for face,vs in verts.items():
            vs=[transform(v,c['bone']) for v in vs]
            a=[vs[1][i]-vs[0][i] for i in range(3)];b=[vs[2][i]-vs[0][i] for i in range(3)]
            normal=[a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]]
            if normal[2]*co+normal[0]*si>=-.001:continue
            pts=[project(v) for v in vs];polys.append((sum(v[2] for v in pts)/4,c,face,pts))
    for depth,c,face,pts in sorted(polys,key=lambda p:-p[0]):
        x,y,X,Y=faces[c['uuid']][face];texture=images[c['texture']]
        def vertex(u,v):return tuple(pts[0][i]+u*(pts[1][i]-pts[0][i])+v*(pts[3][i]-pts[0][i]) for i in range(2))
        for py in range(y,Y):
            for px in range(x,X):
                u,v=(px-x)/(X-x),(py-y)/(Y-y);U,V=(px+1-x)/(X-x),(py+1-y)/(Y-y)
                draw.polygon([vertex(u,v),vertex(U,v),vertex(U,V),vertex(u,V)],fill=texture.getpixel((px,py)))
    draw=ImageDraw.Draw(canvas);draw.text((32,30),'ILLAGER ARCHMAGE / '+name.upper(),fill=(222,190,108));draw.text((32,52),'48 model units = 3 blocks / authored UV textures',fill=(160,168,180))
    canvas.convert('RGB').save(OUT/f'previews/{name}.png')
    return canvas
for name,yaw in [('front',0),('back',180),('side',90)]:preview(name,yaw)
sheet=Image.new('RGB',(1536,2048),(21,23,31))
for i,a in enumerate(animations):
    # Key poses are images of the authored rig, not a substitute for runtime animation tests.
    t={'arrive':.6,'hurt_reaction':.1,'cast_recovery':.25,'staff_attack':.25,'death':.8}.get(a['name'],a['length']*.45)
    image=preview('pose-'+a['name'],15,a['name'],t);image.thumbnail((384,512));sheet.paste(image,((i%4)*384,(i//4)*512))
sheet.save(OUT/'previews/animation-poses.png')
print(f'Created {len(cubes)} cuboids, {len(groups)} bones, {len(animations)} animations; atlas rows {cursor}')
