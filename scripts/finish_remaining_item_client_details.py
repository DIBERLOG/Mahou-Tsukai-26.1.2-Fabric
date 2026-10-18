from remaining_port_helpers import *
write(client/'client/itemmodel/SlotSelectProperty.java',(orig/'client/itemmodel/SlotSelectProperty.java').read_text(encoding='utf-8'))
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',' public static void init(){\n  net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties.ID_MAPPER.put(stepsword.mahoutsukai.client.itemmodel.SlotSelectProperty.ID,stepsword.mahoutsukai.client.itemmodel.SlotSelectProperty.TYPE);');write(p,s)
p=Path('scripts/check_resources.py');s=p.read_text(encoding='utf-8').replace("    validate_reference(recipe['result']['id'], 'item')","    if recipe.get('type')=='mahoutsukai:william_bookcloning':\n        assert 'william' in ids\n        continue\n    validate_reference(recipe['result']['id'], 'item')").replace("    elif value['type'] == 'minecraft:composite':","    elif value['type'] == 'minecraft:select':\n        check_item_model(value['fallback'])\n        for case in value['cases']:check_item_model(case['model'])\n    elif value['type'] == 'minecraft:composite':");write(p,s)
# Preserve original Nobu target graphics using native collected geometry.
helper=Path('scripts/finish_weapon_visuals.py').read_text(encoding='utf-8');exec(helper[:helper.index('\nfor cls,entitycls')])
s=(orig/'client/NobuClientEffect.java').read_text(encoding='utf-8');a=s.index('   public static void renderSelection');b=s.index('   public static boolean isNobuTargetable',a);c=s.index('   public static void renderCrossHairLine',b);methods=s[a:b]+s[c:s.rfind('}')];methods=methods.replace('MahoujinRenderType.wrapNoDepthLayer(','(');methods=geometry(methods)
p=client/'client/NobuClientEffect.java';t=p.read_text(encoding='utf-8');imports='''import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.render.*;
import stepsword.mahoutsukai.render.item.RuleBreakerRenderer;
import stepsword.mahoutsukai.render.overlay.RenderSelectedEntity;
''';t=t.replace('public final class NobuClientEffect',imports+'public final class NobuClientEffect');write(p,t[:t.rfind('}')]+methods+'}\n')
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8');anchor='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class';s=s.replace(anchor,'   if(stepsword.mahoutsukai.client.NobuClientEffect.targets!=null)for(var target:stepsword.mahoutsukai.client.NobuClientEffect.targets)if(target.isAlive())stepsword.mahoutsukai.client.NobuClientEffect.renderSelection(context.poseStack(),context.submitNodeCollector(),partial,target,mc.player);\n'+anchor);write(p,s)
