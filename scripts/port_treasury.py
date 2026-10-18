from remaining_port_helpers import *
p=main/'effects/projection/PowerConsolidationSpellEffect.java';write(p,p.read_text().replace('import stepsword.mahoutsukai.dataattachments.chunks.IChunkMahou;','').replace('world.getGameTime() % MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE','world.getGameTime() % Math.max(1,MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE)'))
append(main/'util/Utils.java',' public static String getRegistryName(net.minecraft.world.item.Item item,Level world){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString();}\n')
ritual('item/spells/projection/TreasuryProjectionSpellScroll.java','block/spells/projection/TreasuryProjectionMahoujin.java','tile/projection/TreasuryProjectionMahoujinTileEntity.java','treasuryProjectionMahoujin')
p=copy('item/spells/projection/TreasuryProjection/TreasuryProjectionGauntlet.java');s=p.read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=s.replace('import stepsword.mahoutsukai.MahouTsukaiMod;','').replace('         || MahouTsukaiMod.jousting.treasuryProjection(tmpi);',';').replace('List<String> white','List<? extends String> white').replace('List<String> black','List<? extends String> black').replace('super("treasury_projection_gauntlet", new Properties());','super("treasury_projection_gauntlet", new Properties().stacksTo(1));');s=re.sub(r'ModTriggers\.(\w+)\.get\(\)',r'ModTriggers.\1',s)
s=re.sub(r'ResourceHandler<ItemResource> inv = Utils.getInventory\(stack1\);\s*if \(inv != null\) \{\s*int z = inv.size\(\);\s*for \(int j = 0; j < z; j\+\+\) \{\s*ItemStack stack2 = ItemUtil.getStack\(inv, j\);\s*if \(this.isArrow\(stack2, player.level\(\)\)\) \{\s*projectables.add\(stack2\);\s*}\s*}\s*}', 'for(ItemStack stack2:Utils.snapshotNestedItems(stack1))if(this.isArrow(stack2,player.level()))projectables.add(stack2);',s);write(p,s)
insert(main/'item/ModItems.java','    static {','    public static final Supplier<Item> treasuryProjectionGauntlet=register("treasury_projection_gauntlet",stepsword.mahoutsukai.item.spells.projection.TreasuryProjection.TreasuryProjectionGauntlet::new);\n')
entity('GATE_CHAIN','entity/mahoujin/GateChainEntity.java','gate_chain_mahoujin_entity')
p=main/'entity/mahoujin/GateChainEntity.java';s=p.read_text().replace('import stepsword.mahoutsukai.handlers.ServerHandler;','').replace('ServerHandler.tickCounter % MTConfig.TREASURY_PROJECTION_CHAINS_COOLDOWN','this.level().getGameTime() % Math.max(1,MTConfig.TREASURY_PROJECTION_CHAINS_COOLDOWN)');a=s.find('   public AABB getBoundingBox()');
if a>=0:s=s[:a]+s[s.index('\n   }',a)+5:]
s=s.replace('      Vec3 motion = this.getDeltaMovement();','      if(f<1.0E-6)return;\n      Vec3 motion = this.getDeltaMovement();');write(p,s)
append(main/'util/Utils.java',''' public static java.util.List<ItemStack> snapshotNestedItems(ItemStack stack){
  var container=stack.get(net.minecraft.core.component.DataComponents.CONTAINER);if(container!=null)return container.nonEmptyItemCopyStream().toList();
  var bundle=stack.get(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS);if(bundle!=null)return bundle.itemCopyStream().toList();
  var storage=net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.withConstant(stack).find(net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.ITEM);var result=new java.util.ArrayList<ItemStack>();if(storage!=null)for(var view:storage){if(!view.isResourceBlank() && view.getAmount()>0)result.add(view.getResource().toStack((int)Math.min(Integer.MAX_VALUE,view.getAmount())));if(result.size()>=4096)break;}return result;
 }
''')
insert(main/'sounds/ModSounds.java',' public static void init()',' public static final Supplier<SoundEvent> CHAINS=register("chain.pull");\n')
insert(main/'advancements/ModTriggers.java',' public static void init()',' public static final MahouTrigger TREASURY_PROJECTION=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","treasury_projection"),new MahouTrigger());\n')
config()
