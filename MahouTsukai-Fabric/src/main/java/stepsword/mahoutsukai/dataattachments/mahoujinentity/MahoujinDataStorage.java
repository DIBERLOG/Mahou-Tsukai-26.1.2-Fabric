package stepsword.mahoutsukai.dataattachments.mahoujinentity;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MahoujinDataStorage {
   public static void read(MahoujinData data, ValueInput input) {
      data.setCasterUuid((UUID)input.read("MAHOUTSUKAI_CASTER", UUIDUtil.CODEC).orElse(null));
      data.setLife(input.getIntOr("MAHOUTSUKAI_LIFE", 0));
      data.setRotationYaw(input.getFloatOr("MAHOUTSUKAI_ROTATION_YAW", 0.0F));
      data.setRotationRoll(input.getFloatOr("MAHOUTSUKAI_ROTATION_ROLL", 0.0F));
      data.setRotationPitch(input.getFloatOr("MAHOUTSUKAI_ROTATION_PITCH", 0.0F));
      data.setRotationSpeed(input.getFloatOr("MAHOUTSUKAI_ROTATION_SPEED", 0.0F));
      data.setCircleSize(input.getFloatOr("MAHOUTSUKAI_CIRCLE_SIZE", 0.0F));
      data.setColorR(input.getFloatOr("MAHOUTSUKAI_COLOR_R", 1.0F));
      data.setColorG(input.getFloatOr("MAHOUTSUKAI_COLOR_G", 1.0F));
      data.setColorB(input.getFloatOr("MAHOUTSUKAI_COLOR_B", 1.0F));
      data.setColorA(input.getFloatOr("MAHOUTSUKAI_COLOR_A", 1.0F));
      data.setColorR2(input.getFloatOr("MAHOUTSUKAI_COLOR_R2", 1.0F));
      data.setColorG2(input.getFloatOr("MAHOUTSUKAI_COLOR_G2", 1.0F));
      data.setColorB2(input.getFloatOr("MAHOUTSUKAI_COLOR_B2", 1.0F));
      data.setColorA2(input.getFloatOr("MAHOUTSUKAI_COLOR_A2", 1.0F));
   }

   public static void write(MahoujinData data, ValueOutput output) {
      if (data.getCasterUuid().isPresent()) {
         output.store("MAHOUTSUKAI_CASTER", UUIDUtil.CODEC, data.getCasterUuid().get());
      }

      output.putInt("MAHOUTSUKAI_LIFE", data.getLife());
      output.putFloat("MAHOUTSUKAI_ROTATION_YAW", data.getRotationYaw());
      output.putFloat("MAHOUTSUKAI_ROTATION_ROLL", data.getRotationRoll());
      output.putFloat("MAHOUTSUKAI_ROTATION_PITCH", data.getRotationPitch());
      output.putFloat("MAHOUTSUKAI_ROTATION_SPEED", data.getRotationSpeed());
      output.putFloat("MAHOUTSUKAI_CIRCLE_SIZE", data.getCircleSize());
      output.putFloat("MAHOUTSUKAI_COLOR_R", data.getColorR());
      output.putFloat("MAHOUTSUKAI_COLOR_G", data.getColorG());
      output.putFloat("MAHOUTSUKAI_COLOR_B", data.getColorB());
      output.putFloat("MAHOUTSUKAI_COLOR_A", data.getColorA());
      output.putFloat("MAHOUTSUKAI_COLOR_R2", data.getColorR2());
      output.putFloat("MAHOUTSUKAI_COLOR_G2", data.getColorG2());
      output.putFloat("MAHOUTSUKAI_COLOR_B2", data.getColorB2());
      output.putFloat("MAHOUTSUKAI_COLOR_A2", data.getColorA2());
   }
}
