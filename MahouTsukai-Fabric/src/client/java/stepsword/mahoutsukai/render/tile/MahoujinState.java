package stepsword.mahoutsukai.render.tile;
import java.util.ArrayList;
public class MahoujinState extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
 public ArrayList<String> order = new ArrayList<>();
 public boolean fay, on, cloth, itemPreview;
 public long time;
 public boolean cup;
 public float cupY;
 public String display;
 public net.minecraft.world.entity.Entity scry;
 public net.minecraft.world.phys.Vec3 scryTargetPos;
 public final java.util.ArrayList<net.minecraft.world.level.block.state.BlockState> scryBlocks=new java.util.ArrayList<>();
 public float partialTicks;

}
