from remaining_port_helpers import *
copy('datacomponents/mysticcode/MysticCodeData.java')
p=main/'datacomponents/DataComponents.java';s=p.read_text();s=s.replace('import com.mojang.serialization.Codec;','import com.mojang.serialization.Codec;\nimport stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData;');source=(orig/'datacomponents/DataComponents.java').read_text();a=source.index('   public static final Codec<MysticCodeData>');b=source.index('   public static final DeferredHolder',a);defs=source[a:b]+'''   public static final DataComponentType<MysticCodeData> MYSTIC_CODE_COMPONENT=DataComponentType.<MysticCodeData>builder().persistent(MYSTIC_CODE_CODEC).networkSynchronized(MYSTIC_CODE_STREAM_CODEC).build();
''';s=s.replace('    public static void init() {',defs+'\n    public static void init() {\n        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","mystic_code_mahou"),MYSTIC_CODE_COMPONENT);');write(p,s)
write(main/'containers/MysticCodeContents.java','''package stepsword.mahoutsukai.containers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
/** Three real component-backed slots; every native container edit is committed to the held item. */
public final class MysticCodeContents extends SimpleContainer {
 private final ItemStack holder;
 public MysticCodeContents(ItemStack holder){super(3);this.holder=holder;var contents=holder.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY);var items=NonNullList.withSize(3,ItemStack.EMPTY);contents.copyInto(items);for(int i=0;i<3;i++)super.setItem(i,items.get(i));}
 @Override public void setChanged(){super.setChanged();if(holder==null)return;var values=new java.util.ArrayList<ItemStack>();for(int i=0;i<3;i++)values.add(getItem(i).copy());holder.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(values));}
}
''')
p=main/'util/Utils.java';append(p,''' public static stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData getMysticCodeData(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.MYSTIC_CODE_COMPONENT,new stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData()).copy();}
 public static void setMysticCodeData(ItemStack stack,stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.MYSTIC_CODE_COMPONENT,value.copy());}
 public static stepsword.mahoutsukai.containers.MysticCodeContents getMCInventory(ItemStack stack){return stack.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode?new stepsword.mahoutsukai.containers.MysticCodeContents(stack):null;}
''')
for cls in ['MysticCode','MysticCodeFirstSorcery']:
 p=copy('item/mysticcode/'+cls+'.java');s=p.read_text(encoding='utf-8');s=s.replace('import net.neoforged.neoforge.transfer.item.ItemUtil;','').replace('MysticCodeItemAccessItemHandler','MysticCodeContents').replace('ItemUtil.getStack(inventory, slot)','inventory.getItem(slot)').replace('inventory.getStackInSlot(stack, slot)','inventory.getItem(slot)').replace('inventory.setStackInSlot(stack, slot, ammo)','inventory.setItem(slot,ammo)');s=s.replace('public ArrayList<? extends String> getBlacklist()','public java.util.List<? extends String> getBlacklist()').replace('return (ArrayList<? extends String>)MTConfig.MYSTIC_CODE_FIRST_SORCERY_BLACKLIST;','return MTConfig.MYSTIC_CODE_FIRST_SORCERY_BLACKLIST;');s=s.replace('new Properties().durability(MTConfig.FIRST_SORCERY_DURABILITY)','new Properties().stacksTo(1).durability(MTConfig.FIRST_SORCERY_DURABILITY)');s=s.replace('stack.hurtAndBreak(1, entityLiving, EquipmentSlot.MAINHAND)','stack.hurtAndBreak(1, entityLiving, entityLiving.getOffhandItem()==stack?EquipmentSlot.OFFHAND:EquipmentSlot.MAINHAND)').replace('stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)','stack.hurtAndBreak(1, player, player.getOffhandItem()==stack?EquipmentSlot.OFFHAND:EquipmentSlot.MAINHAND)');s=s.replace('ammo.getItem().onUseTick(level, player, ammo, count);','ammo.getItem().onUseTick(level, player, ammo, count);\n               inventory.setItem(slot,ammo);');s=s.replace('new MysticCodeInventory(i, Utils.getMCInventory(stack), playerInventory, stack)','new MysticCodeInventory(i,playerInventory,stack)');write(p,s)
# Native menu and slots preserve the original slot positions and scroll-only restriction.
write(main/'containers/ModContainers.java','''package stepsword.mahoutsukai.containers;
public final class ModContainers {
 private static final net.minecraft.world.inventory.MenuType<MysticCodeInventory> TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.MENU,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mystic_code_inventory"),new net.minecraft.world.inventory.MenuType<>(MysticCodeInventory::new,net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
 public static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<MysticCodeInventory>> MYSTIC_CODE=()->TYPE;
 public static void init(){}
}
''')
write(main/'containers/MysticCodeInventory.java','''package stepsword.mahoutsukai.containers;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.mysticcode.MysticCode;
import stepsword.mahoutsukai.item.spells.SpellScroll;
public class MysticCodeInventory extends AbstractContainerMenu {
 private final MysticCodeContents contents;
 private final ItemStack code;
 public MysticCodeInventory(int id,Inventory inventory){this(id,inventory,inventory.player.getMainHandItem().getItem() instanceof MysticCode?inventory.player.getMainHandItem():inventory.player.getOffhandItem());}
 public MysticCodeInventory(int id,Inventory inventory,ItemStack code){super(ModContainers.MYSTIC_CODE.get(),id);this.code=code;contents=new MysticCodeContents(code);
  for(int i=0;i<3;i++)addSlot(new Slot(contents,i,44+36*i,20){@Override public boolean mayPlace(ItemStack stack){return stack.getItem() instanceof SpellScroll && !hasItem();}@Override public void setChanged(){super.setChanged();save();}});
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addPlayerSlot(inventory,col+row*9+9,8+col*18,row*18+51);
  for(int col=0;col<9;col++)addPlayerSlot(inventory,col,8+col*18,109);
 }
 private void addPlayerSlot(Inventory inv,int i,int x,int y){addSlot(new Slot(inv,i,x,y){@Override public boolean mayPickup(Player player){return getItem()!=code;}@Override public boolean mayPlace(ItemStack stack){return getItem()!=code && stack!=code;}});}
 private void save(){if(code.getItem() instanceof MysticCode mystic)mystic.saveCapabilityToStack(code);}
 @Override public boolean stillValid(Player player){return !code.isEmpty() && (player.getMainHandItem()==code || player.getOffhandItem()==code);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0 || index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem() || slot.getItem()==code)return ItemStack.EMPTY;var stack=slot.getItem();var result=stack.copy();if(index<3){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(!(stack.getItem() instanceof SpellScroll) || !moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);save();return result;}
 @Override public void removed(Player player){super.removed(player);save();}
}
''')
for field,id,cls in [('mysticCode','mystic_code','MysticCode'),('mysticCodeFirstSorcery','mystic_code_first_sorcery','MysticCodeFirstSorcery')]:insert(main/'item/ModItems.java','    static {',f'    public static final Supplier<Item> {field}=register("{id}",stepsword.mahoutsukai.item.mysticcode.{cls}::new);\n')
insert(main/'fabric/MahouFabric.java','        ModItems.init();','        stepsword.mahoutsukai.containers.ModContainers.init();\n')
p=copy('networking/MysticCodeSwitchPacket.java');s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.network.handling.IPayloadContext;','').replace('MysticCodeItemAccessItemHandler','MysticCodeContents').replace('inventory.getStackInSlot(stack, slot)','inventory.getItem(slot)');a=s.index('   public static void handle(');b=s.index('   public static void handleSwitch',a);s=s[:a]+s[b:];s=s.replace('   int slot;','   public int slot;\n   public static final Type<MysticCodeSwitchPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mystic_code_switch"));');s=s.replace('      ItemStack stack =','      if(message.slot<0 || message.slot>=3 || player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND))return;\n      ItemStack stack =',1).replace('return MahouPackets.MYSTIC_CODE_SWITCH_TYPE;','return TYPE;');write(p,s)
insert(main/'fabric/MahouNetworking.java','        PayloadTypeRegistry.serverboundPlay()', '''        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.TYPE,stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.handleSwitch(packet,context.player()));
''')
# Restore the original nested-scroll recognition removed while this item was unavailable.
for name in ['FallenDownEntity','FallenDownBeamEntity']:
 p=main/('entity/mahoujin/'+name+'.java');s=p.read_text();original=(orig/('entity/mahoujin/'+name+'.java')).read_text();a=original.index('import stepsword.mahoutsukai.item.mysticcode.MysticCode;') if 'import stepsword.mahoutsukai.item.mysticcode.MysticCode;' in original else -1
 # Keep native on-use validation but allow the real selected nested scroll.
 s=s.replace('!(caster.getUseItem().getItem() instanceof FallenDownSpellScroll)','!stepsword.mahoutsukai.item.mysticcode.MysticCode.isChanneling(caster,FallenDownSpellScroll.class)').replace('!(this.caster.getUseItem().getItem() instanceof FallenDownSpellScroll)','!stepsword.mahoutsukai.item.mysticcode.MysticCode.isChanneling(this.caster,FallenDownSpellScroll.class)');write(p,s)
append(main/'item/mysticcode/MysticCode.java',''' public static boolean isChanneling(LivingEntity user,Class<? extends net.minecraft.world.item.Item> spell){var stack=user.getUseItem();if(spell.isInstance(stack.getItem()))return true;var inventory=Utils.getMCInventory(stack);return inventory!=null && spell.isInstance(inventory.getItem(getSlot(stack)).getItem());}
''')
config()
