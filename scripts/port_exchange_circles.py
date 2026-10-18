from pathlib import Path
import re,shutil
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a);p.write_text(s.replace(a,b))
for cls,field,name,cats in [('ChronalExchange','chronalExchange','chronal_exchange',['EMERALD','EMERALD','QUARTZ']),('CatalystExchange','catalystExchange','catalyst_exchange',['EMERALD']*3),('AlchemicalExchange','alchemicalExchange','alchemical_exchange',['EMERALD','EMERALD','GOLD']),('DurabilityExchange','durabilityExchange','durability_exchange',['EMERALD','EMERALD','DIAMOND'])]:
 for rel in [f'block/spells/exchange/{cls}Mahoujin.java',f'tile/exchange/{cls}MahoujinTileEntity.java']:
  p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'))
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {field}Mahoujin = plain("mahoujin_{name}",new stepsword.mahoutsukai.block.spells.exchange.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(src/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.exchange.{cls}MahoujinTileEntity>> {field} = register("mahoujin_{name}",stepsword.mahoutsukai.tile.exchange.{cls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init()')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {field} = makeRecipe('+','.join('POWDERED_'+c for c in cats)+f');\n      recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},false);')
 client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/MahouFabricClient.java'
 edit(client,'        stepsword.mahoutsukai.Keybinds.init();',f'        stepsword.mahoutsukai.Keybinds.init();\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{field}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 shutil.copyfile(root/f'recovered/assets/mahoutsukai/blockstates/mahoujin_{name}.json',res/f'assets/mahoutsukai/blockstates/mahoujin_{name}.json')
p=src/'config/MTConfig.java';original=(orig/'config/MTConfig.java').read_text();fields=original[original.index('   public static int ALCHEMICAL_EXCHANGE_BLOCK_CYCLE'):original.index('   public static int DAMAGE_EXCHANGE_MANA_COST')];fields=fields.replace('Arrays.asList','java.util.Arrays.asList').replace('List<','java.util.List<').replace('new ArrayList<>','new java.util.ArrayList<>').replace('DURABILITY_EXCHANGE_MANA_GAIN_CAP = 50','DURABILITY_EXCHANGE_MANA_GAIN_CAP = 10');fields+='    public static java.util.List<Double> DURABILITY_EXCHANGE_TAX_BRACKETS = java.util.List.of();\n    public static java.util.List<Double> DURABILITY_EXCHANGE_TAX_RATES = java.util.List.of();\n';edit(p,'    private static final com.google.gson.Gson JSON',fields+'    private static final com.google.gson.Gson JSON')
edit(p,'                        if (field.getName().equals("ACTIVE_MAGIC_CIRCLE_LIMITS")) {','''                        if (field.getName().startsWith("DURABILITY_EXCHANGE_TAX_")) {
                            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("Numeric tax list required");
                            double number=element.getAsDouble();
                            if(!Double.isFinite(number) || number<0 || number>100000000) throw new IllegalArgumentException("Invalid tax value");
                            list.add(number);continue;
                        }
                        if (field.getName().equals("ACTIVE_MAGIC_CIRCLE_LIMITS")) {''')
edit(p,'case "LEY_DISTANCE", "ASCENSION_BLOCK_CYCLE" -> 1;','case "LEY_DISTANCE", "ASCENSION_BLOCK_CYCLE", "CATALYST_EXCHANGE_BLOCK_CYCLE", "DURABILITY_EXCHANGE_BLOCK_CYCLE" -> 1;')
p=src/'tile/exchange/DurabilityExchangeMahoujinTileEntity.java';s=p.read_text();s=re.sub(r'import net.neoforged[^\n]+\n','',s);s=s.replace('ResourceHandler<ItemResource> invCap = Utils.getInventory(this.level, this.worldPosition.offset(0, -1, 0));','var invCap = net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(this.level,this.worldPosition.below(),null);');s=s.replace('for (int i = 0; i < invCap.size(); i++) {\n                  ItemStack stack = ItemUtil.getStack(invCap, i);','for (var slot : invCap) {\n                  ItemStack stack = slot.getResource().toStack((int)Math.min(Integer.MAX_VALUE,slot.getAmount()));');s=s.replace('this.damageInventoryStack(invCap, i, stack, (int)tax)','this.damageInventoryStack(invCap, slot, stack, (int)tax)');start=s.index('   private ItemStack damageInventoryStack');end=s.index('   public static double computeTax',start);s=s[:start]+'''   private ItemStack damageInventoryStack(net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> inventory,
         net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> slot, ItemStack stack,int damage) {
      ItemStack damagedStack=stack.copy();
      damagedStack.hurtAndBreak(damage,SafeFakePlayer.createFakePlayerIfNotExists((ServerLevel)this.getCaster().level(),"faker",this.getCasterUUID()),EquipmentSlot.MAINHAND);
      try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()) {
         if(slot.extract(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(stack),stack.getCount(),tx)!=stack.getCount()) return stack;
         if(!damagedStack.isEmpty() && inventory.insert(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(damagedStack),damagedStack.getCount(),tx)!=damagedStack.getCount()) return stack;
         tx.commit();return damagedStack;
      }
   }

'''+s[end:];p.write_text(s)
p=src/'util/EffectUtil.java';edit(p,' public static boolean hasBuff', ''' public static boolean inEnchantBlacklist(Holder<net.minecraft.world.item.enchantment.Enchantment> enchant,List<? extends String> values) {
  return enchant.unwrapKey().map(key->values.contains(key.identifier().toString()) || values.contains(key.identifier().getNamespace())).orElse(false);
 }
 public static void tryChangeBlockState(boolean killTE,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.state.BlockState replacement,Level world,net.minecraft.world.entity.player.Player owner) {
  if(!(world instanceof net.minecraft.server.level.ServerLevel level) || unchangableBlock(world.getBlockState(pos).getBlock()) || !killTE && world.getBlockEntity(pos)!=null) return;
  var player=owner == null ? stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNull(level)
    : stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNotExists(level,"faker",owner.getUUID());
  var state=world.getBlockState(pos);
  if(net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world,player,pos,state,world.getBlockEntity(pos))) world.setBlockAndUpdate(pos,replacement);
 }
 public static boolean hasBuff''')
p=src/'util/Utils.java';edit(p,'    public static void debug(String message)', '''    public static net.minecraft.world.level.block.Block getBlockFromRegistryName(String name) {
        var id=net.minecraft.resources.Identifier.tryParse(name);
        return id==null ? net.minecraft.world.level.block.Blocks.AIR : net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(id);
    }
    public static Iterable<net.minecraft.core.Holder<net.minecraft.world.level.block.Block>> getBlockTagFromRegistryName(String name) {
        var id=net.minecraft.resources.Identifier.tryParse(name.startsWith("#") ? name.substring(1) : name);
        if(id==null) return java.util.List.of();
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,id))
            .map(tag->(Iterable<net.minecraft.core.Holder<net.minecraft.world.level.block.Block>>)tag).orElse(java.util.List.of());
    }
    public static void err(String message) { org.slf4j.LoggerFactory.getLogger("mahoutsukai").error(message); }
    private static java.util.Random random;
    public static java.util.Random getRandom(Level level) { if(random==null) random=new java.util.Random(level.getDefaultClockTime());return random; }
    public static void debug(String message)''')
p=src/'fabric/MahouFabric.java';edit(p,'                    .resolve("serverconfig/mahoutsukai-fabric-economy.json"));','                    .resolve("serverconfig/mahoutsukai-fabric-economy.json"));\n                stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity.init();')
p=src/'block/spells/Mahoujin.java';edit(p,'               IMahou mahou = Utils.getPlayerMahou(playerIn);', '''               if(mte instanceof stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity chronal) chronal.setTime(0L);
               if(mte instanceof stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity alchemical)
                  alchemical.setPlacedTime(Math.floorMod(Math.floorMod(worldIn.getOverworldClockTime(),24000)/20*20+100,24000));
               IMahou mahou = Utils.getPlayerMahou(playerIn);''')
p=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/render/tile/MahoujinRenderer.java';edit(p,'|| entity instanceof stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity);','|| entity instanceof stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity\n    || entity instanceof stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity\n    || entity instanceof stepsword.mahoutsukai.tile.exchange.CatalystExchangeMahoujinTileEntity\n    || entity instanceof stepsword.mahoutsukai.tile.exchange.DurabilityExchangeMahoujinTileEntity\n    || entity instanceof stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity);')
print('Adapted four original exchange circles')
