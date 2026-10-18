package stepsword.mahoutsukai.effects;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.util.EffectUtil;

public class Leylines {
   public static Leylines.LeyLineShape leyOrientation(BlockPos p) {
      boolean x = correctXZval(p, true);
      boolean z = correctXZval(p, false);
      int diag = diagXZval(p);
      if (x && z) {
         return Leylines.LeyLineShape.STAR;
      } else if (x) {
         return Leylines.LeyLineShape.VERT;
      } else if (z) {
         return Leylines.LeyLineShape.HORIZ;
      } else if (diag == 3) {
         return Leylines.LeyLineShape.CROSS;
      } else if (diag == 2) {
         return Leylines.LeyLineShape.NWD;
      } else {
         return diag == 1 ? Leylines.LeyLineShape.NED : Leylines.LeyLineShape.NONE;
      }
   }

   public static boolean correctXZval(BlockPos p, boolean x) {
      int xmod = MTConfig.LEY_DISTANCE;
      int xoffset = MTConfig.LEY_OFFSET;
      xoffset %= xmod;
      int xval = x ? p.getX() % xmod : p.getZ() % xmod;
      if (xoffset < 0) {
         xoffset += xmod;
      }

      if (xval < 0) {
         xval += xmod;
      }

      return xval == xoffset;
   }

   public static int diagXZval(BlockPos p) {
      int xmod = MTConfig.LEY_DISTANCE;
      int xoffset = MTConfig.LEY_OFFSET;
      xoffset %= xmod;
      int xval = p.getX() - xoffset;
      int zval = p.getZ() - xoffset;
      xval %= xmod;
      zval %= xmod;
      if (xval == 0) {
         return 0;
      }

      if (xval < 0) {
         xval += xmod;
      }

      if (zval < 0) {
         zval += xmod;
      }

      if (xval == zval && xmod - xval == zval) {
         return 3;
      } else if (xval == zval) {
         return 2;
      } else {
         return xmod - xval == zval ? 1 : 0;
      }
   }

   public static int nearbyLeyValue(BlockPos p) {
      int range = 3;
      int ret = 0;
      Stream<BlockPos> lst2 = BlockPos.betweenClosedStream(p.offset(-range, 0, -range), p.offset(range, 0, range));
      Iterator<BlockPos> lst = lst2.iterator();

      while (lst.hasNext()) {
         BlockPos z = lst.next();
         Leylines.LeyLineShape shape = leyOrientation(z);
         if (shape != Leylines.LeyLineShape.NONE) {
            if (shape == Leylines.LeyLineShape.STAR) {
               ret += 3;
            } else if (shape == Leylines.LeyLineShape.CROSS) {
               ret += 2;
            } else {
               ret++;
            }
         }
      }

      return ret;
   }

   public static boolean leyDimensionValid(Identifier dimension) {
      boolean blacklist = MTConfig.LEY_BLACKLIST;
      List<? extends String> dims = MTConfig.LEY_DIMENSION_LIST;
      boolean found = EffectUtil.inGenericBlacklist(dimension.toString(), dims) || EffectUtil.inGenericBlacklist(dimension.getNamespace(), dims);
      if (found && blacklist) {
         return false;
      } else {
         return found ? true : blacklist;
      }
   }

   public enum LeyLineShape {
      STAR,
      CROSS,
      NWD,
      NED,
      HORIZ,
      VERT,
      NONE;
   }
}
