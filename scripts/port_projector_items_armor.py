from pathlib import Path
import re,json
root=Path(__file__).resolve().parents[1];orig=root/'recovered/stepsword/mahoutsukai';m=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';c=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'
def put(base,name,s):
 p=base/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def edit(base,name,a,b):
 s=(base/name).read_text();assert a in s,(name,a);put(base,name,s.replace(a,b))
for name in ['datacomponents/projectorenchant/ProjectorEnchant.java','datacomponents/fog/FogProjectorEnchant.java','enchant/MahoujinProjectorEnchant.java']:
 put(m,name,(orig/name).read_text())
edit(m,'enchant/MahoujinProjectorEnchant.java','e.getPv().addLast(mte.pv);','e.getPv().addLast(ProjectorValues.readFromTag(ProjectorValues.writeToTag(mte.pv)));')
edit(m,'enchant/MahoujinProjectorEnchant.java','            e.getPv().addLast(', '            if(e.getPv().size()>=64)return;\n            e.getPv().addLast(')
for field,cls,codec,id in [('PROJECTOR_ENCHANT_COMPONENT','stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant','stepsword.mahoutsukai.render.enchant.ProjectorValues.CODEC.listOf().xmap(v->new stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant(new java.util.ArrayList<>(v)),stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant::getPv)','projector_enchant_mahou'),('FOG_PROJECTOR_ENCHANT_COMPONENT','stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant','stepsword.mahoutsukai.util.FogProjectorValues.CODEC.xmap(stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant::new,stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant::getPv)','fog_projector_enchant_mahou')]:
 edit(m,'datacomponents/DataComponents.java','public final class DataComponents {',f'public final class DataComponents {{\n public static final Codec<{cls}> {field}_CODEC={codec};\n public static final DataComponentType<{cls}> {field}=DataComponentType.<{cls}>builder().persistent({field}_CODEC).networkSynchronized(ByteBufCodecs.fromCodec({field}_CODEC)).build();')
 edit(m,'datacomponents/DataComponents.java','    public static void init() {',f'    public static void init() {{\n        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),{field});')
edit(m,'util/Utils.java','public final class Utils {','''public final class Utils {
 public static stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant getProjectorEnchant(net.minecraft.world.item.ItemStack stack){return stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT);}
 public static void setProjectorEnchant(net.minecraft.world.item.ItemStack stack,stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant v){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT,v.copy());}
 public static boolean hasProjectorEnchant(net.minecraft.world.item.ItemStack stack){return stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT);}
''')
s=(orig/'networking/ProjectorRightClickPacket.java').read_text();s=re.sub(r'import net\.neoforged[^;]+;\n','',s)
s=s.replace('ProjectorRightClickPacket.ARMOR_ACTION.values()[buf.readInt() % ProjectorRightClickPacket.ARMOR_ACTION.values().length]','readAction(buf.readInt())')
a=s.index('   public static void handle(');b=s.index('   public Type<ProjectorRightClickPacket> type()',a)
s=s[:a]+'''   public static final Type<ProjectorRightClickPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","projector_right_click"));
   private static ARMOR_ACTION readAction(int value){if(value<0||value>=ARMOR_ACTION.values().length)throw new IllegalArgumentException("Invalid projector armor action");return ARMOR_ACTION.values()[value];}
   public static void handle(ProjectorRightClickPacket message,ServerPlayer player){
      if(player.isSpectator()||message.action==null||message.pos==null||!player.level().hasChunkAt(message.pos)||!player.level().mayInteract(player,message.pos)||player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(message.pos))>64)return;
      if(player.level().getBlockEntity(message.pos) instanceof MahoujinProjectorTileEntity projector && player.getMainHandItem().is(net.minecraft.tags.ItemTags.ARMOR))MahoujinProjectorEnchant.setProjectorNBT(player.getMainHandItem(),projector,message.action);
   }

'''+s[b:];s=s.replace('MahouPackets.PROJECTOR_RIGHT_CLICK_TYPE','TYPE');put(m,'networking/ProjectorRightClickPacket.java',s)
edit(m,'fabric/MahouFabric.java','    public void onInitialize() {','''    public void onInitialize() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ProjectorRightClickPacket.TYPE,stepsword.mahoutsukai.networking.ProjectorRightClickPacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ProjectorRightClickPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.ProjectorRightClickPacket.handle(packet,context.player()));
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,level,hand,hit)->stepsword.mahoutsukai.fabric.ProjectorInteractions.use(player,level,hand,hit));''')
put(m,'fabric/ProjectorInteractions.java','''package stepsword.mahoutsukai.fabric;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.Items;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.tile.*;
public final class ProjectorInteractions {
 public static InteractionResult use(Player player,Level level,InteractionHand hand,BlockHitResult hit){
  if(player.isSpectator()||!level.mayInteract(player,hit.getBlockPos()))return InteractionResult.PASS;
  var stack=player.getItemInHand(hand);if(!stack.is(Items.STICK))return InteractionResult.PASS;
  var tile=level.getBlockEntity(hit.getBlockPos());
  if(tile instanceof MahoujinProjectorTileEntity circle){if(!level.isClientSide()){
   if(!player.isDiscrete())stepsword.mahoutsukai.enchant.MahoujinProjectorEnchant.setProjectorNBT(stack,circle,stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.REPLACE);
   else if(MahoujinProjectorTileEntity.canEdit(player,circle)){var saved=stack.get(DataComponents.PROJECTOR_ENCHANT_COMPONENT);if(saved!=null&&!saved.getPv().isEmpty()){circle.pv=stepsword.mahoutsukai.render.enchant.ProjectorValues.readFromTag(stepsword.mahoutsukai.render.enchant.ProjectorValues.writeToTag(saved.getPv().getFirst()));circle.sendUpdates();}}
  }return InteractionResult.SUCCESS;}
  if(tile instanceof FogProjectorTileEntity fog){if(!level.isClientSide()){
   if(!player.isDiscrete())stack.set(DataComponents.FOG_PROJECTOR_ENCHANT_COMPONENT,new stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant(stepsword.mahoutsukai.util.FogProjectorValues.readFromTag(stepsword.mahoutsukai.util.FogProjectorValues.writeToTag(fog.pv))));
   else{var saved=stack.get(DataComponents.FOG_PROJECTOR_ENCHANT_COMPONENT);if(saved!=null){fog.pv=stepsword.mahoutsukai.util.FogProjectorValues.readFromTag(stepsword.mahoutsukai.util.FogProjectorValues.writeToTag(saved.getPv()));fog.sendUpdates();}}
  }return InteractionResult.SUCCESS;}
  return InteractionResult.PASS;
 }
}
''')
edit(c,'fabric/ProjectorClient.java','   if(tile instanceof MahoujinProjectorTileEntity){','''   if(player.getItemInHand(hand).is(net.minecraft.world.item.Items.STICK))return InteractionResult.PASS;
   if(tile instanceof MahoujinProjectorTileEntity && player.getMainHandItem().is(net.minecraft.tags.ItemTags.ARMOR)){
    var action=player.isDiscrete()?stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.ADD:stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.REPLACE;
    if(net.minecraft.client.input.InputWithModifiers.hasControlDown(net.minecraft.client.Minecraft.getInstance().getWindow()))action=stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.REMOVE;
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.ProjectorRightClickPacket(action,hit.getBlockPos()));return InteractionResult.SUCCESS;
   }
   if(tile instanceof MahoujinProjectorTileEntity){''')
for name in ['render/item/MahoujinProjectorItemStackRenderer.java']:
 put(c,name,(orig/name).read_text())
edit(c,'render/item/MahouSpecialItemModel.java','            if (renderer.equals("staff_emrys"))','            if (renderer.equals("mahoujin_projector")) return new MahoujinProjectorItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),context.entityModelSet());\n            if (renderer.equals("staff_emrys"))')
r=root/'MahouTsukai-Fabric/src/main/resources';p=r/'assets/mahoutsukai/items/mahoujin_projector.json';p.write_text(json.dumps({'model':{'type':'minecraft:special','base':'mahoutsukai:block/mahoujin_projector','model':{'type':'mahoutsukai:magical_item','renderer':'mahoujin_projector'}}})+'\n')
print('Connected projector copying, armor decoration, persistent item components and projector item renderer')
