package stepsword.mahoutsukai.item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
public class MahouTsukaiGuidebook extends ItemBase {
 public static java.util.function.Consumer<Player> openClient=player->{};
 public MahouTsukaiGuidebook(){super("guidebook");}
 @Override public InteractionResult use(Level world,Player player,InteractionHand hand){if(world.isClientSide())openClient.accept(player);return InteractionResult.SUCCESS;}
}
