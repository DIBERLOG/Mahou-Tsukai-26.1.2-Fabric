package stepsword.mahoutsukai.render.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import stepsword.mahoutsukai.render.Point;
import stepsword.mahoutsukai.util.Utils;

public class BookRender {
   public double cover_width = 1.0;
   public double cover_height = 1.4F;
   public double cover_thickness = 0.06F;
   public double cover_binding_max_curve = 0.5;
   public double cover_binding_width = 0.3F;
   public double front_cover_open_angle = 90.0;
   public double back_cover_open_angle = 90.0;
   public double front_cover_angle_sin;
   public double front_cover_angle_cos;
   public double back_cover_angle_sin;
   public double back_cover_angle_cos;
   public double front_shifted_width_x;
   public double front_shifted_width_z;
   public double front_shifted_thickness_x;
   public double front_shifted_thickness_z;
   public double back_shifted_width_x;
   public double back_shifted_width_z;
   public double back_shifted_thickness_x;
   public double back_shifted_thickness_z;
   public double circle_distance = 0.1F;
   public double magic_circle_width = 0.8F;
   public double front_circle_shifted_width_x;
   public double front_circle_shifted_width_z;
   public double front_circle_shifted_thickness_x;
   public double front_circle_shifted_thickness_z;
   public double back_circle_shifted_width_x;
   public double back_circle_shifted_width_z;
   public double back_circle_shifted_thickness_x;
   public double back_circle_shifted_thickness_z;
   public int binding_quality = 10;
   public int page_stack_quality = 10;
   public double percentage_page_open = 0.5;
   public double page_cover_border = 0.08F;
   public double page_width;
   public double page_height;
   public ArrayList<Point> frontCover;
   public ArrayList<Point> backCover;
   public ArrayList<Point> frontCircle;
   public ArrayList<Point> backCircle;
   public ArrayList<Point> binding;
   public ArrayList<Point> frontPageStack;
   public ArrayList<Point> backPageStack;
   public ArrayList<Float> turningPageAngles;
   public ArrayList<Point> frontCharacterLocations;
   public ArrayList<Point> backCharacterLocations;
   public ArrayList<ArrayList<Point>> frontPageFlipCharacterLocations;
   public ArrayList<ArrayList<Point>> backPageFlipCharacterLocations;
   public ArrayList<Integer> frontCharIndices;
   public ArrayList<Integer> backCharIndices;
   public ArrayList<ArrayList<Point>> turningPages;
   public ArrayList<ArrayList<Integer>> turningPageChars;
   float PAGE_TURN_DEGREES = 15.0F;
   public boolean renderingChars = false;
   public ConcurrentHashMap<BookRender.TRANSFORMABLE, BookRender.AnimateObject> animations = new ConcurrentHashMap<>();

   public BookRender() {
      this.calculatePoints();
   }

   public void calculatePoints() {
      this.front_cover_angle_sin = Math.sin(Math.toRadians(this.front_cover_open_angle));
      this.front_cover_angle_cos = Math.cos(Math.toRadians(this.front_cover_open_angle));
      this.back_cover_angle_sin = Math.sin(Math.toRadians(this.back_cover_open_angle));
      this.back_cover_angle_cos = Math.cos(Math.toRadians(this.back_cover_open_angle));
      this.front_shifted_width_x = this.cover_width * this.front_cover_angle_cos;
      this.front_shifted_width_z = -this.cover_width * this.front_cover_angle_sin;
      this.front_shifted_thickness_x = this.cover_thickness * this.front_cover_angle_sin;
      this.front_shifted_thickness_z = this.cover_thickness * this.front_cover_angle_cos;
      this.back_shifted_width_x = this.cover_width * this.back_cover_angle_cos;
      this.back_shifted_width_z = -this.cover_width * this.back_cover_angle_sin;
      this.back_shifted_thickness_x = this.cover_thickness * this.back_cover_angle_sin;
      this.back_shifted_thickness_z = this.cover_thickness * this.back_cover_angle_cos;
      this.front_circle_shifted_width_x = this.magic_circle_width * this.front_cover_angle_cos;
      this.front_circle_shifted_width_z = -this.magic_circle_width * this.front_cover_angle_sin;
      this.front_circle_shifted_thickness_x = this.circle_distance * this.front_cover_angle_sin;
      this.front_circle_shifted_thickness_z = this.circle_distance * this.front_cover_angle_cos;
      this.back_circle_shifted_width_x = this.magic_circle_width * this.back_cover_angle_cos;
      this.back_circle_shifted_width_z = -this.magic_circle_width * this.back_cover_angle_sin;
      this.back_circle_shifted_thickness_x = this.circle_distance * this.back_cover_angle_sin;
      this.back_circle_shifted_thickness_z = this.circle_distance * this.back_cover_angle_cos;
      this.page_height = this.cover_height - 2.0 * this.page_cover_border;
      this.page_width = this.cover_width - this.page_cover_border;
      this.frontCoverPoints();
      this.backCoverPoints();
      this.frontCirclePoints();
      this.backCirclePoints();
      this.bindingPoints();
      this.frontPageStackPoints();
      this.backPageStackPoints();
      this.pageTurnPoints();
      if (this.frontCharIndices == null) {
         this.frontCharIndices = generatePageOfChars();
      }

      if (this.backCharIndices == null) {
         this.backCharIndices = generatePageOfChars();
      }

      if (this.renderingChars) {
         this.frontCharacters();
         this.backCharacters();
         this.pageFlipCharacters();
      }
   }

   public static ArrayList<Integer> generatePageOfChars() {
      ArrayList<Integer> ret = new ArrayList<>();

      for (int k = 0; k < 100; k++) {
         ret.add(new Random().nextInt(24));
      }

      return ret;
   }

   public ArrayList<Point> coverPoints(double wx, double wz, double wtx, double wtz, double cbw) {
      ArrayList<Point> ret = new ArrayList<>();

      for (int i = 0; i < 8; i++) {
         ret.add(Point.point());
      }

      for (int m : new int[]{2, 3, 6, 7}) {
         ret.get(m).shiftY(this.cover_height);
      }

      for (int m : new int[]{1, 2, 5, 6}) {
         ret.get(m).shiftX(wx).shiftZ(wz);
      }

      for (int m : new int[]{4, 5, 6, 7}) {
         ret.get(m).shiftX(wtx).shiftZ(wtz);
      }

      for (Point p : ret) {
         p.shiftX(cbw / 2.0);
      }

      return ret;
   }

   public void frontCoverPoints() {
      this.frontCover = this.coverPoints(
         -this.front_shifted_width_x, -this.front_shifted_width_z, -this.front_shifted_thickness_x, -this.front_shifted_thickness_z, -this.cover_binding_width
      );
   }

   public void backCoverPoints() {
      this.backCover = this.coverPoints(
         this.back_shifted_width_x, -this.back_shifted_width_z, this.back_shifted_thickness_x, -this.back_shifted_thickness_z, this.cover_binding_width
      );
   }

   public ArrayList<Point> circlePoints(double wx, double wz, double wtx, double wtz, double cbw, double book_height, double circle_width) {
      ArrayList<Point> ret = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         ret.add(Point.point());
      }

      for (int m : new int[]{2, 3}) {
         ret.get(m).shiftY(circle_width);
      }

      for (int m : new int[]{1, 2}) {
         ret.get(m).shiftX(wx).shiftZ(wz);
      }

      for (int m : new int[]{0, 1, 2, 3}) {
         ret.get(m).shiftX(wtx).shiftZ(wtz).shiftY(book_height / 2.0 - circle_width / 2.0);
      }

      for (Point p : ret) {
         p.shiftX(cbw / 2.0);
      }

      return ret;
   }

   public void frontCirclePoints() {
      this.frontCircle = this.circlePoints(
         -this.front_circle_shifted_width_x,
         -this.front_circle_shifted_width_z,
         -this.front_circle_shifted_thickness_x - this.front_cover_angle_cos * (this.cover_width / 2.0 - this.magic_circle_width / 2.0),
         -this.front_circle_shifted_thickness_z + this.front_cover_angle_sin * (this.cover_width / 2.0 - this.magic_circle_width / 2.0),
         -this.cover_binding_width,
         this.cover_height,
         this.magic_circle_width
      );
   }

   public void backCirclePoints() {
      this.backCircle = this.circlePoints(
         this.back_circle_shifted_width_x,
         -this.back_circle_shifted_width_z,
         this.back_circle_shifted_thickness_x,
         -this.back_circle_shifted_thickness_z,
         this.cover_binding_width,
         this.cover_height,
         this.magic_circle_width
      );
   }

   public void bindingPoints() {
      this.binding = new ArrayList<>();
      this.binding
         .addAll(
            Arrays.asList(
               this.frontCover.get(0),
               this.frontCover.get(4),
               this.frontCover.get(3),
               this.frontCover.get(7),
               this.backCover.get(0),
               this.backCover.get(4),
               this.backCover.get(3),
               this.backCover.get(7)
            )
         );
      double theta = Math.toRadians(180.0F / this.binding_quality);

      for (int k = 0; k <= this.binding_quality; k++) {
         double sin = Math.sin(theta * k);
         double cos = Math.cos(theta * k);
         double inner_x = cos * this.cover_binding_width / 2.0;
         double inner_z = -sin * this.cover_binding_width / 2.0 * this.cover_binding_max_curve;
         double outer_x = cos * (this.cover_binding_width / 2.0 + this.cover_thickness);
         double outer_z = -sin * (this.cover_binding_width / 2.0 + this.cover_thickness) * this.cover_binding_max_curve;
         this.binding.add(Point.point().shiftX(inner_x).shiftZ(inner_z));
         this.binding.add(Point.point().shiftX(inner_x).shiftZ(inner_z).shiftY(this.cover_height));
         this.binding.add(Point.point().shiftX(outer_x).shiftZ(outer_z));
         this.binding.add(Point.point().shiftX(outer_x).shiftZ(outer_z).shiftY(this.cover_height));
      }
   }

   public void frontCharacters() {
      double shiftX = 0.002 * this.front_cover_angle_sin;
      double shiftZ = 0.002 * this.front_cover_angle_cos;
      ArrayList<Point> points = new ArrayList<>(
         List.of(
            this.frontPageStack.get(this.frontPageStack.size() - 10).clone().shiftX(shiftX).shiftZ(shiftZ),
            this.frontPageStack.get(this.frontPageStack.size() - 9).clone().shiftX(shiftX).shiftZ(shiftZ),
            this.frontPageStack.get(this.frontPageStack.size() - 6).clone().shiftX(shiftX).shiftZ(shiftZ),
            this.frontPageStack.get(this.frontPageStack.size() - 5).clone().shiftX(shiftX).shiftZ(shiftZ)
         )
      );
      this.frontCharacterLocations = this.characterStackPoints(points);
   }

   public void backCharacters() {
      double shiftX = 0.002 * this.back_cover_angle_sin * -1.0;
      double shiftZ = 0.002 * this.back_cover_angle_cos;
      ArrayList<Point> points = new ArrayList<>(
         List.of(
            this.backPageStack.get(this.backPageStack.size() - 6).clone().shiftX(shiftX).shiftZ(shiftZ),
            this.backPageStack.get(this.backPageStack.size() - 5).clone().shiftX(shiftX).shiftZ(shiftZ),
            this.backPageStack.get(this.backPageStack.size() - 10).clone().shiftX(shiftX).shiftZ(shiftZ),
            this.backPageStack.get(this.backPageStack.size() - 9).clone().shiftX(shiftX).shiftZ(shiftZ)
         )
      );
      this.backCharacterLocations = this.characterStackPoints(points);
   }

   public void pageFlipCharacters() {
      if (this.turningPages != null && this.turningPageAngles != null) {
         this.backPageFlipCharacterLocations = new ArrayList<>();
         this.frontPageFlipCharacterLocations = new ArrayList<>();

         for (int page = 0; page < this.turningPages.size(); page++) {
            float angle = this.turningPageAngles.get(page);
            ArrayList<Point> lst = this.turningPages.get(page);
            double sin = Math.sin(Math.toRadians(angle));
            double cos = Math.cos(Math.toRadians(angle));
            double shiftX = 0.01 * sin * -1.0;
            double shiftZ = 0.01 * cos * -1.0;
            ArrayList<Point> points_front = new ArrayList<>(
               List.of(
                  lst.get(lst.size() - 4).clone().shiftX(shiftX).shiftZ(shiftZ),
                  lst.get(lst.size() - 3).clone().shiftX(shiftX).shiftZ(shiftZ),
                  lst.get(lst.size() - 6).clone().shiftX(shiftX).shiftZ(shiftZ),
                  lst.get(lst.size() - 5).clone().shiftX(shiftX).shiftZ(shiftZ)
               )
            );
            ArrayList<Point> points_back = new ArrayList<>(
               List.of(
                  lst.get(lst.size() - 6).clone().shiftX(-shiftX).shiftZ(-shiftZ),
                  lst.get(lst.size() - 5).clone().shiftX(-shiftX).shiftZ(-shiftZ),
                  lst.get(lst.size() - 4).clone().shiftX(-shiftX).shiftZ(-shiftZ),
                  lst.get(lst.size() - 3).clone().shiftX(-shiftX).shiftZ(-shiftZ)
               )
            );
            this.backPageFlipCharacterLocations.add(this.characterStackPoints(points_back));
            this.frontPageFlipCharacterLocations.add(this.characterStackPoints(points_front));
         }
      }
   }

   public ArrayList<Point> characterStackPoints(ArrayList<Point> points) {
      int divisionsY = 8;
      int divisionsX = 12;
      ArrayList<Point> ret = new ArrayList<>();
      float char_height = 1.0F / divisionsY;
      float char_width = 1.0F / divisionsX;
      float xdiff = points.get(2).x - points.get(1).x;
      float zdiff = points.get(1).z - points.get(3).z;
      float ydiff = points.get(0).y - points.get(1).y;

      for (int i = 0; i < divisionsX; i++) {
         for (int j = 0; j < divisionsY; j++) {
            float startingX = char_width * xdiff;
            float startingZ = -char_width * zdiff;
            float startingY = -char_height * ydiff;
            Point starting = points.get(0).clone().shiftX(startingX * i).shiftZ(startingZ * i).shiftY(startingY * j);
            if (i > 0 && i < divisionsX - 1 && j > 0 && j < divisionsY - 1) {
               ret.add(starting);
               ret.add(starting.clone().shiftY(-char_height));
               ret.add(starting.clone().shiftX(startingX).shiftZ(startingZ).shiftY(-char_height));
               ret.add(starting.clone().shiftX(startingX).shiftZ(startingZ));
            }
         }
      }

      return ret;
   }

   public ArrayList<Point> pageStackPoints(
      double cover_angle_cos, double cover_angle_sin, double cover_open_angle, double page_open, double reverse, boolean isStack
   ) {
      ArrayList<Point> stack = new ArrayList<>();

      for (int m = 0; m < this.page_stack_quality; m++) {
         if (isStack) {
            stack.addAll(Arrays.asList(Point.point(), Point.point(), Point.point(), Point.point()));
         } else {
            stack.addAll(Arrays.asList(Point.point(), Point.point()));
         }
      }

      double zfighting = 1.0E-5;
      double starterX = -reverse * this.cover_binding_width / 2.0 - reverse * zfighting;
      double starterZ = 0.0 - reverse * zfighting;
      double starter_line_len = Math.sqrt(starterX * starterX + starterZ * starterZ);
      double starter_line_angle = Math.asin(starterZ / starter_line_len);
      double thickness_of_pagestack = page_open * this.cover_binding_width;
      double end_line_angle = Math.toRadians(90.0 - cover_open_angle);
      double one_q = (end_line_angle - starter_line_angle) / this.page_stack_quality;
      int k = 0;

      for (int q = 0; q < this.page_stack_quality; q++) {
         double current_angle = starter_line_angle + q * one_q;
         double sinq = Math.sin(current_angle);
         double cosq = Math.cos(current_angle);
         double zcomp = sinq * starter_line_len;
         double xcomp = reverse * cosq * starter_line_len;
         if (isStack) {
            Point tmp = stack.get(k);
            tmp.shiftX(starterX).shiftZ(starterZ).shiftY(this.page_cover_border);
            tmp = stack.get(++k);
            tmp.shiftX(starterX).shiftZ(starterZ).shiftY(this.page_height + this.page_cover_border);
            k++;
         }

         Point tmp = stack.get(k);
         tmp.shiftX(starterX + xcomp).shiftZ(starterZ + zcomp).shiftY(this.page_height + this.page_cover_border);
         tmp = stack.get(++k);
         tmp.shiftX(starterX + xcomp).shiftZ(starterZ + zcomp).shiftY(this.page_cover_border);
         k++;
      }

      double shifted_page_width = this.page_width - (90.0 - cover_open_angle) / 90.0 * thickness_of_pagestack;
      double shifted_x = starterX - shifted_page_width * cover_angle_cos * reverse + thickness_of_pagestack * cover_angle_sin * reverse;
      double shifted_z = starterZ + shifted_page_width * cover_angle_sin + thickness_of_pagestack * cover_angle_cos;
      if (isStack) {
         stack.add(
            Point.point()
               .shiftX(starterX - reverse * this.page_width * cover_angle_cos)
               .shiftZ(starterZ + this.page_width * cover_angle_sin)
               .shiftY(this.page_cover_border)
         );
         stack.add(
            Point.point()
               .shiftX(starterX - reverse * this.page_width * cover_angle_cos)
               .shiftZ(starterZ + this.page_width * cover_angle_sin)
               .shiftY(this.page_height + this.page_cover_border)
         );
      }

      stack.add(Point.point().shiftX(shifted_x).shiftZ(shifted_z).shiftY(this.page_height + this.page_cover_border));
      stack.add(Point.point().shiftX(shifted_x).shiftZ(shifted_z).shiftY(this.page_cover_border));
      stack.add(Point.point().shiftX(shifted_x).shiftZ(shifted_z).shiftY(this.page_cover_border));
      stack.add(Point.point().shiftX(shifted_x).shiftZ(shifted_z).shiftY(this.page_height + this.page_cover_border));
      if (isStack) {
         stack.add(
            Point.point()
               .shiftX(starterX - reverse * this.page_width * cover_angle_cos)
               .shiftZ(starterZ + this.page_width * cover_angle_sin)
               .shiftY(this.page_height + this.page_cover_border)
         );
         stack.add(
            Point.point()
               .shiftX(starterX - reverse * this.page_width * cover_angle_cos)
               .shiftZ(starterZ + this.page_width * cover_angle_sin)
               .shiftY(this.page_cover_border)
         );
      }

      return stack;
   }

   public void frontPageStackPoints() {
      this.frontPageStack = this.pageStackPoints(
         this.front_cover_angle_cos, this.front_cover_angle_sin, this.front_cover_open_angle, this.percentage_page_open, 1.0, true
      );
   }

   public void backPageStackPoints() {
      this.backPageStack = this.pageStackPoints(
         this.back_cover_angle_cos, this.back_cover_angle_sin, this.back_cover_open_angle, 1.0 - this.percentage_page_open, -1.0, true
      );
   }

   public void pageTurnPoints() {
      this.turningPages = new ArrayList<>();
      if (this.turningPageAngles != null) {
         for (Float f : this.turningPageAngles) {
            double reverse = 1.0;
            double angle = f.floatValue();
            if (f > 90.0F) {
               angle = 180.0F - f;
               reverse = -1.0;
            }

            angle = Math.clamp(angle, reverse > 0.0 ? this.front_cover_open_angle : this.back_cover_open_angle, 90.0);
            double sin = Math.sin(Math.toRadians(angle));
            double cos = Math.cos(Math.toRadians(angle));
            if (reverse > 0.0) {
               sin = Math.sin(Math.toRadians(angle));
               cos = Math.cos(Math.toRadians(angle));
            }

            this.turningPages.add(this.pageStackPoints(cos, sin, angle, this.percentage_page_open, reverse, false));
         }
      }
   }

   public static float[] darken(float[] rgba) {
      return new float[]{rgba[0] * 0.7F, rgba[1] * 0.7F, rgba[2] * 0.7F, rgba[3]};
   }

   public static float[] darken(float[] rgba, float z) {
      return new float[]{rgba[0] * z, rgba[1] * z, rgba[2] * z, rgba[3]};
   }

   public void startAnimation(BookRender.TRANSFORMABLE transformable, double start, double end, double step) {
      BookRender.AnimateObject obj = new BookRender.AnimateObject(transformable, start, end, step);
      if (Math.abs(start - end) > step) {
         this.animations.put(transformable, obj);
         if (transformable == BookRender.TRANSFORMABLE.open_percentage && this.turningPageChars == null) {
            this.turningPageChars = new ArrayList<>();
         }
      }
   }

   public void animateTick(long ticks) {
      double pageSpawnChance = 0.2;

      for (BookRender.TRANSFORMABLE t : this.animations.keySet()) {
         BookRender.AnimateObject anim = this.animations.get(t);
         switch (t) {
            case open_angle:
               this.front_cover_open_angle = anim.animateTowards(this.front_cover_open_angle);
               this.back_cover_open_angle = this.front_cover_open_angle;
               if (anim.reached(this.front_cover_open_angle)) {
                  this.animations.remove(t);
               }
               break;
            case open_percentage:
               if (this.turningPageAngles == null) {
                  this.turningPageAngles = new ArrayList<>();
               }

               if (!anim.reached(this.percentage_page_open)) {
                  boolean forward;
                  if (this.percentage_page_open < anim.goingTo) {
                     int k = (int)((anim.goingTo - this.percentage_page_open) / anim.step);
                     if (k > this.turningPageAngles.size() && Math.random() < pageSpawnChance) {
                        this.turningPageAngles.add((float)(180.0 - this.back_cover_open_angle));
                        this.turningPageChars.add(this.backCharIndices);
                        this.backCharIndices = generatePageOfChars();
                     }

                     forward = true;
                  } else {
                     int k = (int)((this.percentage_page_open - anim.goingTo) / anim.step);
                     if (k > this.turningPageAngles.size() && Math.random() < pageSpawnChance) {
                        this.turningPageAngles.add((float)this.front_cover_open_angle);
                        this.turningPageChars.add(this.frontCharIndices);
                        this.frontCharIndices = generatePageOfChars();
                     }

                     forward = false;
                  }

                  boolean one_finished = false;
                  ArrayList<Float> newAngles = new ArrayList<>();

                  for (Float f : this.turningPageAngles) {
                     if (forward) {
                        f = f - this.PAGE_TURN_DEGREES;
                        if (f.floatValue() < this.front_cover_open_angle) {
                           one_finished = true;
                        } else {
                           newAngles.add(f);
                        }
                     } else {
                        f = f + this.PAGE_TURN_DEGREES;
                        if (f.floatValue() > 180.0 - this.back_cover_open_angle) {
                           one_finished = true;
                        } else {
                           newAngles.add(f);
                        }
                     }
                  }

                  int sizediff = this.turningPageAngles.size() - newAngles.size();
                  this.turningPageAngles = newAngles;
                  if (one_finished) {
                     this.percentage_page_open = anim.animateTowards(this.percentage_page_open);
                     if (!this.turningPageChars.isEmpty()) {
                        for (int i = 0; i < sizediff; i++) {
                           if (!forward) {
                              this.backCharIndices = this.turningPageChars.removeFirst();
                           } else {
                              this.frontCharIndices = this.turningPageChars.removeFirst();
                           }
                        }
                     }
                  }
               } else {
                  this.animations.remove(t);
                  this.turningPageAngles = new ArrayList<>();
               }
         }

         this.calculatePoints();
      }
   }

   public BookRender clone() {
      BookRender ret = new BookRender();
      ret.cover_width = this.cover_width;
      ret.cover_height = this.cover_height;
      ret.cover_thickness = this.cover_thickness;
      ret.cover_binding_max_curve = this.cover_binding_max_curve;
      ret.cover_binding_width = this.cover_binding_width;
      ret.front_cover_open_angle = this.front_cover_open_angle;
      ret.back_cover_open_angle = this.back_cover_open_angle;
      ret.front_cover_angle_sin = this.front_cover_angle_sin;
      ret.front_cover_angle_cos = this.front_cover_angle_cos;
      ret.back_cover_angle_sin = this.back_cover_angle_sin;
      ret.back_cover_angle_cos = this.back_cover_angle_cos;
      ret.front_shifted_width_x = this.front_shifted_width_x;
      ret.front_shifted_width_z = this.front_shifted_width_z;
      ret.front_shifted_thickness_x = this.front_shifted_thickness_x;
      ret.front_shifted_thickness_z = this.front_shifted_thickness_z;
      ret.back_shifted_width_x = this.back_shifted_width_x;
      ret.back_shifted_width_z = this.back_shifted_width_z;
      ret.back_shifted_thickness_x = this.back_shifted_thickness_x;
      ret.back_shifted_thickness_z = this.back_shifted_thickness_z;
      ret.binding_quality = this.binding_quality;
      ret.page_stack_quality = this.page_stack_quality;
      ret.percentage_page_open = this.percentage_page_open;
      ret.page_cover_border = this.page_cover_border;
      ret.page_height = this.page_height;
      ret.turningPageAngles = new ArrayList<>();
      if (this.turningPageAngles != null) {
         ret.turningPageAngles.addAll(this.turningPageAngles);
      }

      ret.frontCharIndices = new ArrayList<>();
      if (this.frontCharIndices != null) {
         ret.frontCharIndices.addAll(this.frontCharIndices);
      }

      ret.backCharIndices = new ArrayList<>();
      if (this.backCharIndices != null) {
         ret.backCharIndices.addAll(this.backCharIndices);
      }

      ret.turningPageChars = new ArrayList<>();
      if (this.turningPageChars != null) {
         ret.turningPageChars.addAll(this.turningPageChars);
      }

      ret.calculatePoints();
      return ret;
   }

   public BookRender partial(BookRender last, float partial) {
      BookRender ret = this.clone();
      ret.renderingChars = true;
      if (last != null) {
         ret.front_cover_open_angle = Utils.yawPartial((float)last.front_cover_open_angle, (float)this.front_cover_open_angle, partial);
         ret.back_cover_open_angle = Utils.yawPartial((float)last.back_cover_open_angle, (float)this.back_cover_open_angle, partial);
         ret.percentage_page_open = last.percentage_page_open + (this.percentage_page_open - last.percentage_page_open) * partial;
         ret.turningPageAngles = new ArrayList<>();
         if (this.turningPageAngles != null && this.animations.containsKey(BookRender.TRANSFORMABLE.open_percentage)) {
            BookRender.AnimateObject anim = this.animations.get(BookRender.TRANSFORMABLE.open_percentage);

            for (Float f : this.turningPageAngles) {
               if (anim.goingTo < anim.startFrom) {
                  ret.turningPageAngles
                     .add(
                        (float)Math.clamp(
                           f - this.PAGE_TURN_DEGREES + this.PAGE_TURN_DEGREES * partial, this.front_cover_open_angle, 180.0 - this.back_cover_open_angle
                        )
                     );
               } else {
                  ret.turningPageAngles
                     .add(
                        (float)Math.clamp(
                           f + this.PAGE_TURN_DEGREES - this.PAGE_TURN_DEGREES * partial, this.front_cover_open_angle, 180.0 - this.back_cover_open_angle
                        )
                     );
               }
            }
         }

         ret.calculatePoints();
      }

      return ret;
   }

   public static class AnimateObject {
      public double startFrom;
      public double goingTo;
      public BookRender.TRANSFORMABLE targetProperty;
      public double step;

      public AnimateObject(BookRender.TRANSFORMABLE transformable, double start, double end, double step) {
         this.targetProperty = transformable;
         this.startFrom = start;
         this.goingTo = end;
         this.step = step;
      }

      public double animateTowards(double now) {
         if (this.startFrom < this.goingTo) {
            now += this.step;
            if (now >= this.goingTo) {
               now = this.goingTo;
            }
         } else {
            now -= this.step;
            if (now <= this.goingTo) {
               now = this.goingTo;
            }
         }

         return now;
      }

      public boolean reached(double now) {
         return Math.abs(this.goingTo - now) < this.step;
      }
   }

   public enum TRANSFORMABLE {
      open_angle,
      open_percentage;
   }
}
