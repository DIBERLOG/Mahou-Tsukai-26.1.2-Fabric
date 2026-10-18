package stepsword.mahoutsukai.item;
import net.minecraft.world.item.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.server.network.Filterable;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.config.*;
public class MahouTsukaiFunbook extends WrittenBookItem {
 public MahouTsukaiFunbook(){super(ItemBase.withRegisteredId(new Properties().stacksTo(64)));}
 @Override public InteractionResult use(Level world,Player player,InteractionHand hand){if(!world.isClientSide()){var pages=new java.util.ArrayList<Filterable<Component>>();pages.add(Filterable.passThrough(Component.translatable("mahoutsukai.book.fun1.name").append("\n\n").append(Component.translatable("mahoutsukai.book.fun1.desc"))));pages.add(Filterable.passThrough(Component.translatable("mahoutsukai.book.fun2.name").append("\n\n").append(Component.translatable("mahoutsukai.book.fun2.desc"))));for(var req:MandatoryFun.FUN_REQS.values())if(MTConfig.CALIBURN_MANDATORY_FUN_REQUIREMENTS.contains(req.name())){String key="mahoutsukai.book."+req.name().toLowerCase(java.util.Locale.ENGLISH);pages.add(Filterable.passThrough(Component.translatable(key+".name").append("\n\n").append(Component.translatable(MandatoryFun.meetsFunReq(player,req)?"mahoutsukai.book.fun_req_met.desc":"mahoutsukai.book.fun_req_not_met.desc")).append("\n\n").append(Component.translatable(key+".desc"))));}player.getItemInHand(hand).set(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(Filterable.passThrough("Mandatory Fun"),"stepsword",0,pages,true));}return super.use(world,player,hand);}
}
