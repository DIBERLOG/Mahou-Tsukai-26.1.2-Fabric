package stepsword.mahoutsukai.tickets;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.TicketType;
public final class ModTickets {
 public static final Supplier<TicketType> EQUIV_DISP_TICKET=register("eq_disp_ticket");
 private static Supplier<TicketType> register(String name) {
  var type=Registry.register(BuiltInRegistries.TICKET_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai",name),new TicketType(0L,6));return ()->type;
 }
 public static final Supplier<TicketType> FAMILIAR_TICKET=register("familiar_ticket");
 public static void init() {}
}
