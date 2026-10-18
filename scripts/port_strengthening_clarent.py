from pathlib import Path
import re,json,shutil
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
def copy(rel,client=False):
 p=(root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai' if client else src)/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'),encoding='utf-8');return p
def span(s,needle):
 a=s.index(needle);a=s.rfind('   public static ',0,a+1) if s.rfind('   public static ',0,a+1)>=0 else a
 brace=s.index('{',s.index(needle));depth=1;b=brace+1
 while depth:
  depth+= (s[b]=='{')-(s[b]=='}');b+=1
 return a,b
for rel in ['datacomponents/strengthening/StrengtheningMahou.java','datacomponents/sword/SwordMahou.java','item/clarent/Clarent.java','potion/WoundedPotion.java','item/spells/projection/StrengtheningSpellScroll.java','tile/projection/StrengtheningMahoujinTileEntity.java','block/spells/projection/StrengtheningMahoujin.java']:copy(rel)
p=src/'tile/projection/StrengtheningMahoujinTileEntity.java';edit(p,'ModItems.strengtheningScroll.get()','ModItems.REGISTERED.get("scroll_strengthening")')
p=copy('effects/projection/StrengtheningSpellEffect.java');s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^\n]+\n','',s)
for needle in ['strengtheningUseEvent(Stop event)','strengtheningUseEvent(UseItemOnBlockEvent event)']:
 a,b=span(s,needle);s=s[:a]+s[b:]
s=s.replace('strengtheningLivingHurt(LivingIncomingDamageEvent event)','strengtheningLivingHurt(LivingEntity entity, DamageSource source,float amount)').replace('public static void strengtheningLivingHurt','public static float strengtheningLivingHurt').replace('event.getEntity()','entity').replace('         DamageSource source = event.getSource();\n','').replace('event.getAmount()','amount').replace('Utils.damageSourceAny(source, DamageTypes.PLAYER_ATTACK)','source.is(DamageTypes.PLAYER_ATTACK)').replace('event.setAmount(amount + 4.0F);','amount += 4.0F;')
a,b=span(s,'strengtheningLivingHurt(');s=s[:b-1]+'      return amount;\n   '+s[b-1:]
s=s.replace('strengtheningHarvestCheck(HarvestCheck event)','strengtheningHarvestCheck(ItemStack s,BlockState target,boolean canHarvest)').replace('public static void strengtheningHarvestCheck','public static boolean strengtheningHarvestCheck').replace('import net.minecraft.world.level.block.Block;','import net.minecraft.world.level.block.Block;\nimport net.minecraft.world.level.block.state.BlockState;')
a,b=span(s,'strengtheningHarvestCheck(');part=s[a:b].replace('      Player player = entity;\n      ItemStack s = player.getMainHandItem();\n','').replace('event.getTargetBlock()','target').replace('event.setCanHarvest(true);','canHarvest=true;');part=part[:-1]+'      return canHarvest;\n   }';s=s[:a]+part+s[b:]
s=s.replace('strengtheningArmorEvent(LivingIncomingDamageEvent event)','strengtheningArmorEvent(LivingEntity entity)').replace('      LivingEntity living = entity;','      LivingEntity living = entity;')
s=s.replace('strengtheningBreakEvent(BreakBlockEvent event)','strengtheningBreakEvent(Player player)').replace('event.getPlayer()','player').replace('         Player player = player;\n','')
s=s.replace('strengtheningBreakSpeed(BreakSpeed event)','strengtheningBreakSpeed(Player player,float speed)').replace('public static void strengtheningBreakSpeed','public static float strengtheningBreakSpeed')
a,b=span(s,'strengtheningBreakSpeed(');part=s[a:b].replace('      Player player = entity;\n','').replace('event.setNewSpeed(event.getNewSpeed() + 3.0F);','speed += 3.0F;');part=part[:-1]+'      return speed;\n   }';s=s[:a]+part+s[b:]
s=s.replace('Utils.getDefaultMahou(stack, stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT, null)','stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT)');p.write_text(s,encoding='utf-8')
p=copy('item/GrowSwordItem.java');s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('import stepsword.mahoutsukai.item.caliburn.Caliburn;\n','').replace('import stepsword.mahoutsukai.item.morgan.Morgan;\n','').replace('DataComponents.SWORD_COMPONENT.value()','DataComponents.SWORD_COMPONENT')
a,b=span(s,'adjustAttributeModifiers(');s=s[:a]+'''   /** Original attribute replacement at Fabric's native modifier enumeration point. */
   public static ItemAttributeModifiers attributes(ItemStack stack,ItemAttributeModifiers base) {
      if(!(stack.getItem() instanceof GrowSwordItem sword)) return base;
      String name=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
      float damage=(name.equals("caliburn") || name.equals("morgan")) ? sword.getCappedAttack(stack) : sword.getAttack(stack);
      return base.withModifierAdded(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
   }
'''+s[b:]
a,b=span(s,'anvilNoRepair(');s=s[:a]+s[b:]
s=s.replace('ModItems.morgan.get() == stack.getItem() || ModItems.caliburn.get() == stack.getItem()','net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().equals("morgan") || net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().equals("caliburn")');p.write_text(s,encoding='utf-8')
p=src/'item/clarent/Clarent.java';edit(p,'ItemAttributeModifiers map = s.getAttributeModifiers();','ItemAttributeModifiers map = GrowSwordItem.attributes(s,s.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,ItemAttributeModifiers.EMPTY));')
p=copy('effects/projection/ClarentEffect.java');s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('ModItems.clarent.get()','ModItems.REGISTERED.get("clarent")')
s=s.replace('clarentLivingHurt(LivingIncomingDamageEvent event)','clarentLivingHurt(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,float amount)').replace('public static void clarentLivingHurt','public static float clarentLivingHurt').replace('event.getSource()','source').replace('event.getEntity()','victim').replace('event.getAmount()','amount').replace('event.setAmount(d1);','amount=d1;')
a,b=span(s,'clarentLivingHurt(');s=s[:b-1]+'      return amount;\n   '+s[b-1:]
s=s.replace('clarentLivingAttack(LivingIncomingDamageEvent event)','clarentLivingAttack(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,float amount)')
# The native 26.1 blocking query requires a shield component. Clarent's original block-use stance has no shield component.
s=s.replace('target.isBlocking() && target.getUseItem().getItem() instanceof Clarent','target.isUsingItem() && target.getUseItem().getItem() instanceof Clarent')
s=s.replace('((MahouTrigger)ModTriggers.CLARENT.get()).trigger((ServerPlayer)player);','ModTriggers.CLARENT.trigger((ServerPlayer)player);');p.write_text(s,encoding='utf-8')
p=src/'potion/WoundedPotion.java';edit(p,'import stepsword.mahoutsukai.handlers.ServerHandler;','');edit(p,'ServerHandler.tickCounter % 10L','player.level().getGameTime() % 10L')
p=src/'datacomponents/DataComponents.java';edit(p,'    public static void init() {','''    public static final Codec<stepsword.mahoutsukai.datacomponents.sword.SwordMahou> SWORD_CODEC=RecordCodecBuilder.create(instance->instance.group(
        Codec.FLOAT.fieldOf("attackDamage").forGetter(stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getAttackDamage),Codec.DOUBLE.fieldOf("innateCap").forGetter(stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getInnateCap)).apply(instance,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,stepsword.mahoutsukai.datacomponents.sword.SwordMahou> SWORD_STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.FLOAT,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getAttackDamage,ByteBufCodecs.DOUBLE,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getInnateCap,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::new);
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.sword.SwordMahou> SWORD_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.sword.SwordMahou>builder().persistent(SWORD_CODEC).networkSynchronized(SWORD_STREAM_CODEC).build();
    public static final Codec<stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou> STRENGTHENING_CODEC=RecordCodecBuilder.create(instance->instance.group(Codec.BOOL.fieldOf("strengthened").forGetter(stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::isUnbreakable),Codec.INT.fieldOf("uses").forGetter(stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::getUses)).apply(instance,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou> STRENGTHENING_STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::isUnbreakable,ByteBufCodecs.INT,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::getUses,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::new);
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou> STRENGTHENING_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou>builder().persistent(STRENGTHENING_CODEC).networkSynchronized(STRENGTHENING_STREAM_CODEC).build();
    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","sword_mahou"),SWORD_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","strengthening_mahou"),STRENGTHENING_COMPONENT);''')
p=src/'util/Utils.java';edit(p,'    public static void debug(String message)', '''    public static stepsword.mahoutsukai.datacomponents.sword.SwordMahou getSwordMahou(ItemStack stack) {
        var type=stepsword.mahoutsukai.datacomponents.DataComponents.SWORD_COMPONENT;var value=stack.get(type);
        if(value==null) {value=new stepsword.mahoutsukai.datacomponents.sword.SwordMahou();stack.set(type,value);}return value.copy();
    }
    public static void setSwordMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.sword.SwordMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.SWORD_COMPONENT,value);}
    public static stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou getStrengtheningMahou(ItemStack stack) {
        var value=stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT);
        return value==null ? new stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou() : value.copy();
    }
    public static void setStrengtheningMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT,value);}
    public static boolean hasStrengtheningMahou(ItemStack stack) {return stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT);}
    public static boolean isTool(ItemStack stack) {return stack.getItem().components().has(net.minecraft.core.component.DataComponents.TOOL);}
    public static boolean isWeapon(net.minecraft.world.item.Item item) {return item.components().has(net.minecraft.core.component.DataComponents.WEAPON);}
    public static void debug(String message)''')
edit(src/'item/ModItems.java','    static {','    static {\n        register("clarent",stepsword.mahoutsukai.item.clarent.Clarent::new);\n        register("scroll_strengthening",stepsword.mahoutsukai.item.spells.projection.StrengtheningSpellScroll::new);')
edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain','    public static final Supplier<Block> strengtheningMahoujin = plain("mahoujin_strengthening",new stepsword.mahoutsukai.block.spells.projection.StrengtheningMahoujin(),false);\n    private static Supplier<Block> plain')
edit(src/'tile/ModTileEntities.java','    public static void init()','    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.projection.StrengtheningMahoujinTileEntity>> strengthening = register("mahoujin_strengthening",stepsword.mahoutsukai.tile.projection.StrengtheningMahoujinTileEntity::new,ModBlocks.strengtheningMahoujin.get());\n    public static void init()')
edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {','   public static void init() {\n      var strengthening=makeRecipe(POWDERED_DIAMOND,POWDERED_DIAMOND,POWDERED_IRON);recipes.put(strengthening,(BlockBase)ModBlocks.strengtheningMahoujin.get());needsCloth.put(strengthening,true);')
edit(src/'potion/ModEffects.java',' public static void init()',' public static final Holder<MobEffect> WOUNDED=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","wounded"),new WoundedPotion());\n public static void init()')
edit(src/'advancements/ModTriggers.java','    public static void init()', '    public static final MahouTrigger CLARENT=register("clarent");\n    public static void init()')
# Scalar and list settings, with exact original builder defaults and ranges.
original=(orig/'config/MTConfig.java').read_text();p=src/'config/MTConfig.java';existing=set(re.findall(r'public static [\w.<>? ]+ (\w+) =',p.read_text(encoding='utf-8')));needed=set()
for rel in ['effects/projection/ClarentEffect.java','item/clarent/Clarent.java','item/GrowSwordItem.java','effects/projection/StrengtheningSpellEffect.java','item/spells/projection/StrengtheningSpellScroll.java','potion/WoundedPotion.java']:needed.update(re.findall(r'MTConfig\.(\w+)',(src/rel).read_text(encoding='utf-8')))
fields=[]
for name in sorted(needed-existing):
 match=re.search(r'   public static (int|double|boolean|List<\? extends (?:String|Double)>) '+name+r' = (.*?);',original,re.S);assert match,name
 typ,val=match.groups();typ=typ.replace('List<','java.util.List<');val=val.replace('new ArrayList<>()','new java.util.ArrayList<>()').replace('Arrays.asList','java.util.Arrays.asList');default=re.search(r'"'+name+r'",\s*(-?[\d.E]+|true|false)(?:,|\s*\))',original)
 if default:val=default.group(1)
 fields.append(f'    public static {typ} {name} = {val};\n')
edit(p,'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES')
s=p.read_text(encoding='utf-8').replace('field.getName().startsWith("DURABILITY_EXCHANGE_TAX_")','field.getGenericType().getTypeName().contains("Double")');rows=[]
for name in re.findall(r'public static (?:int|double) (\w+) =',s):
 m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',original);assert m,name
 _,low,high=m.groups();rows.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}})')
a=s.index('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES');b=s.index('    private static final com.google.gson.Gson JSON',a);p.write_text(s[:a]+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+',\n'.join(rows)+'\n    );\n'+s[b:],encoding='utf-8')
print('Adapted Strengthening and Clarent original source and registration; native hooks/rendering next')
