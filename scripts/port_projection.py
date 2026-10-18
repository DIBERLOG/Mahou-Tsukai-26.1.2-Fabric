"""Historical one-shot adaptation of original Projection; do not rerun over hand edits."""
from pathlib import Path
import re,json,shutil
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
def copy(rel):
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'),encoding='utf-8');return p
for rel in ['datacomponents/projected/ProjectedMahou.java','datacomponents/stackholder/StackHolderMahou.java','item/spells/projection/ProjectionSpellScroll.java','block/spells/projection/ProjectionMahoujin.java','tile/projection/ProjectionMahoujinTileEntity.java']:copy(rel)
edit(src/'tile/projection/ProjectionMahoujinTileEntity.java','ModItems.projectionScroll.get()','ModItems.REGISTERED.get("scroll_projection")')
p=copy('effects/projection/ProjectionSpellEffect.java');s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('import stepsword.mahoutsukai.entity.WeaponProjectileEntity;\n','')
a=s.index('   public static void projectionUpdateAnvil(');b=s.index('   public static void projectionHelp(',a)
s=s[:a]+'''   /** Native anvil result hook; original shared-enchantment minimum prevents upgrades by copies. */
   public static void projectionRepairAnvil(ItemStack left,ItemStack right,ItemStack output) {
      if ((getProjected(left) || getProjected(right)) && !MTConfig.PROJECTION_ANVIL_CANCEL && !output.isEmpty()) {
         ItemEnchantments enchs = EnchantmentHelper.getEnchantmentsForCrafting(output);
         ItemEnchantments enchsleft = EnchantmentHelper.getEnchantmentsForCrafting(left);
         ItemEnchantments enchsright = EnchantmentHelper.getEnchantmentsForCrafting(right);
         EnchantmentHelper.updateEnchantments(output, outputEnchants -> {
            for (Holder<Enchantment> enchantment : enchs.keySet()) {
               if (enchsleft.keySet().contains(enchantment) && enchsright.keySet().contains(enchantment))
                  outputEnchants.set(enchantment, Math.min(enchsleft.getLevel(enchantment), enchsright.getLevel(enchantment)));
            }
         });
      }
   }

'''+s[b:]
a=s.index('         } else if (e instanceof WeaponProjectileEntity');b=s.index('         } else if (e instanceof ThrownTrident)',a);s=s[:a]+s[b:]
s=s.replace('Utils.isSword(tmpi)','stack.is(net.minecraft.tags.ItemTags.SWORDS)').replace('Utils.isTool(tmpi)','Utils.isTool(stack)')
s=s.replace('Utils.getRegistryKey(tmpi, level)','net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi)').replace('Utils.getRegistryName(tmpi, level)','net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi).toString()');p.write_text(s,encoding='utf-8')
# Match the original component fields and wire codecs; getters return copies to preserve native item-copy isolation.
p=src/'datacomponents/DataComponents.java';original=(orig/'datacomponents/DataComponents.java').read_text(encoding='utf-8');a=original.index('   public static final Codec<StackHolderMahou>');b=original.index('   public static final Codec<MysticCodeData>',a);s=original[a:b]
s=re.sub(r'   public static final DeferredHolder[^\n]+\n      "(\w+)", \(\) -> DataComponentType.builder\(\).persistent\((\w+)\).networkSynchronized\((\w+)\).build\(\)\n   \);',lambda m: '   public static final DataComponentType<'+('StackHolderMahou' if m[1]=='stack_holder_mahou' else 'ProjectedMahou')+'> '+('STACK_HOLDERS_COMPONENT' if m[1]=='stack_holder_mahou' else 'PROJECTED_COMPONENT')+' = DataComponentType.<'+('StackHolderMahou' if m[1]=='stack_holder_mahou' else 'ProjectedMahou')+'>builder().persistent('+m[2]+').networkSynchronized('+m[3]+').build();',s)
edit(p,'import com.mojang.serialization.Codec;','import stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou;\nimport stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou;\nimport net.minecraft.world.item.ItemStack;\nimport com.mojang.serialization.Codec;')
edit(p,'    public static void init() {',s+'    public static void init() {\n        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","projected_mahou"),PROJECTED_COMPONENT);\n        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","stack_holder_mahou"),STACK_HOLDERS_COMPONENT);')
p=src/'util/Utils.java';edit(p,'public static void giveOrDrop(Player player, ItemStack stack) { if (!player.getInventory().add(stack)) player.drop(stack, false); }','public static boolean giveOrDrop(Player player, ItemStack stack) { if(player==null || stack.isEmpty()) return false; if (!player.getInventory().add(stack)) player.drop(stack, false); return true; }')
edit(p,'SELECTIVE_DISPLACEMENT,SCROLL_RETRIBUTION','SCROLL_PROJECTION,SELECTIVE_DISPLACEMENT,SCROLL_RETRIBUTION')
edit(p,'        if(set.contains(MAHOU_SELECTION.SELECTIVE_DISPLACEMENT))','        if(set.contains(MAHOU_SELECTION.SCROLL_PROJECTION)) return entity!=player && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity || entity instanceof net.minecraft.world.entity.decoration.ItemFrame || entity instanceof net.minecraft.world.entity.projectile.arrow.ThrownTrident);\n        if(set.contains(MAHOU_SELECTION.SELECTIVE_DISPLACEMENT))')
edit(p,'    public static void debug(String message)', '''    public static stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou getProjectedMahou(ItemStack stack) {var value=stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTED_COMPONENT);return value==null ? new stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou() : value.copy();}
    public static void setProjectedMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTED_COMPONENT,value);}
    public static boolean hasProjectedMahou(ItemStack stack) {return stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTED_COMPONENT);}
    public static stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou getStackHolderMahou(ItemStack stack) {var value=stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.STACK_HOLDERS_COMPONENT);return value==null ? new stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou() : value.copy();}
    public static void setStackHolderMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.STACK_HOLDERS_COMPONENT,value);}
    public static java.util.ArrayList<ItemStack> getEquipmentSlotList(net.minecraft.world.entity.LivingEntity entity) {var result=new java.util.ArrayList<ItemStack>();for(var slot:net.minecraft.world.entity.EquipmentSlot.VALUES) result.add(entity.getItemBySlot(slot));return result;}
    public static void updateHeldItem(Player player) {if(player instanceof net.minecraft.server.level.ServerPlayer sp) sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket(sp.getInventory().getSelectedSlot()));}
    public static void debug(String message)''')
edit(src/'item/ModItems.java','        register("scroll_strengthening",','        register("scroll_projection",stepsword.mahoutsukai.item.spells.projection.ProjectionSpellScroll::new);\n        register("scroll_strengthening",')
edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain(', '    public static final Supplier<Block> projectionMahoujin=plain("mahoujin_projection",new stepsword.mahoutsukai.block.spells.projection.ProjectionMahoujin(),false);\n    private static Supplier<Block> plain(')
edit(src/'tile/ModTileEntities.java','    public static void init() {}','    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.projection.ProjectionMahoujinTileEntity>> projection=register("mahoujin_projection",stepsword.mahoutsukai.tile.projection.ProjectionMahoujinTileEntity::new,ModBlocks.projectionMahoujin.get());\n    public static void init() {}')
edit(src/'block/spells/MahoujinRecipeRegistrar.java','      var geas =','      var projection=makeRecipe(POWDERED_DIAMOND,POWDERED_DIAMOND,POWDERED_QUARTZ);\n      recipes.put(projection,(BlockBase)ModBlocks.projectionMahoujin.get());needsCloth.put(projection,true);\n      var geas =')
p=src/'config/MTConfig.java';edit(p,'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES','''    public static int PROJECTION_MANA_COST=1000, PROJECTION_DURABILITY_FACTOR=5;
    public static boolean PROJECTION_IS_BLACKLIST=true,PROJECTION_DURABILITY_FLAT=true,PROJECTION_ANVIL_CANCEL=false;
    public static java.util.List<String> PROJECTION_BLACKLIST=java.util.List.of(), PROJECTION_ENCHANTMENT_BLACKLIST=java.util.List.of(),PROJECTION_EXPLICIT_WHITELIST=java.util.List.of();
    private static final java.util.Map<String,double[]> ORIGINAL_RANGES''')
edit(p,'ORIGINAL_RANGES = java.util.Map.ofEntries(', 'ORIGINAL_RANGES = java.util.Map.ofEntries(\n        java.util.Map.entry("PROJECTION_MANA_COST",new double[]{0,100000000}),\n        java.util.Map.entry("PROJECTION_DURABILITY_FACTOR",new double[]{0,100000000}),')
# Original item/block assets reference existing shared circle and scroll textures.
for rel in ['assets/mahoutsukai/items/scroll_projection.json','assets/mahoutsukai/models/item/scroll_projection.json','assets/mahoutsukai/blockstates/mahoujin_projection.json','assets/mahoutsukai/models/block/mahoujin_projection.json']:
 source=root/'recovered'/rel
 if source.exists():
  target=res/rel;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
print('Projection native mechanics connected')
