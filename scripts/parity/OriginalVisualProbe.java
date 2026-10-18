package local.mahouparity;

import java.nio.file.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.client.Minecraft;

/** Captures the actual original renderer, without replacing any original code. */
@Mod("mahou_visual_probe")
public final class OriginalVisualProbe {
    public OriginalVisualProbe(){NeoForge.EVENT_BUS.addListener(this::tick);}
    private void tick(ClientTickEvent.Post event){
        var client=Minecraft.getInstance();var request=Path.of("visual-capture.request");
        if(client.level==null || !Files.isRegularFile(request))return;
        try{
            String name=Files.readString(request).strip();Files.delete(request);
            if(!name.matches("[a-z0-9-]+"))throw new IllegalArgumentException("Invalid capture name");
            if(name.equals("quit")){client.stop();return;}
            if(name.equals("close-screen")){client.setScreen(null);return;}
            if(name.equals("open-projector")||name.equals("open-fog")){
                var pos=name.equals("open-projector")?new net.minecraft.core.BlockPos(0,101,3):new net.minecraft.core.BlockPos(2,101,3);
                client.gameMode.useItemOn(client.player,net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos),net.minecraft.core.Direction.UP,pos,false));return;
            }
            net.minecraft.client.Screenshot.grab(client.gameDirectory,name+".png",client.getMainRenderTarget(),1,
                message->org.slf4j.LoggerFactory.getLogger("original-mahou-visual").info("CAPTURE: {}",message.getString()));
        }catch(java.io.IOException e){throw new RuntimeException(e);}
    }
}
