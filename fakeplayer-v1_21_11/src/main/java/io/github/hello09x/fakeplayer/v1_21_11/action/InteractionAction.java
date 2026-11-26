package io.github.hello09x.fakeplayer.v1_21_11.action;

import io.github.hello09x.fakeplayer.api.spi.PlaceSide;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IFluidSource;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.jetbrains.annotations.Nullable;

public final class InteractionAction {
   private InteractionAction() {
   }

   public static boolean placeHeldBlock(EntityPlayer player, double requestedRange, PlaceSide sidePreference) {
      double range = clampRange(player, requestedRange);
      if (!(player.fx().h() instanceof ItemBlock)) {
         return false;
      } else {
         InteractionAction.PlaceTarget target = findNearestPlaceableTop(player, range, sidePreference);
         if (target == null) {
            return false;
         } else {
            lookAt(player, target.hitLocation());
            WorldServer world = player.A();
            BlockPosition pos = target.basePos();
            EnumDirection side = EnumDirection.b;
            if (pos.v() < world.aw() - (side == EnumDirection.b ? 1 : 0) && world.a(player, pos)) {
               MovingObjectPositionBlock hit = new MovingObjectPositionBlock(target.hitLocation(), EnumDirection.b, pos, false);
               EnumInteractionResult result = player.h.a(player, world, player.fx(), EnumHand.a, hit);
               if (!result.a()) {
                  return false;
               } else {
                  player.a(EnumHand.a);
                  player.J();
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static boolean placeCrystal(EntityPlayer player, double requestedRange) {
      double range = clampRange(player, requestedRange);
      BlockPosition basePos = findNearestCrystalBase(player, range);
      if (basePos == null) {
         return false;
      } else if (!player.fx().a(Items.xu)) {
         return false;
      } else {
         BlockPosition above = basePos.d();
         if (!player.A().a_(above).l()) {
            return false;
         } else {
            Vec3D hitLocation = basePos.b().b(0.0, 1.0, 0.0);
            lookAt(player, hitLocation);
            MovingObjectPositionBlock placeHit = new MovingObjectPositionBlock(hitLocation, EnumDirection.b, basePos, false);
            EnumInteractionResult result = player.h.a(player, player.A(), player.fx(), EnumHand.a, placeHit);
            if (!result.a()) {
               return false;
            } else {
               player.a(EnumHand.a);
               player.J();
               return true;
            }
         }
      }
   }

   public static boolean breakCrystal(EntityPlayer player, double requestedRange) {
      double range = clampRange(player, requestedRange);
      EntityEnderCrystal crystal = findNearestCrystalOnBase(player, range);
      if (crystal == null) {
         return false;
      } else {
         lookAt(player, crystal.dj().f());
         player.e(crystal);
         player.a(EnumHand.a);
         player.hf();
         player.J();
         return true;
      }
   }

   public static boolean useLavaBucketOnTarget(EntityPlayer player, EntityPlayer target, double requestedRange) {
      double range = clampRange(player, requestedRange);
      if (!player.fx().a(Items.sm)) {
         return false;
      } else {
         InteractionAction.LavaTarget candidate = findLavaTarget(player, target, range);
         if (candidate == null) {
            return false;
         } else {
            lookAt(player, candidate.hitLocation());
            EnumInteractionResult result = player.h.a(player, player.A(), player.fx(), EnumHand.a, candidate.hitResult());
            if (!result.a()) {
               return false;
            } else {
               player.a(EnumHand.a);
               player.J();
               return true;
            }
         }
      }
   }

   public static boolean pickupFluid(EntityPlayer player, double requestedRange) {
      double range = clampRange(player, requestedRange);
      if (!player.fx().a(Items.sk)) {
         return false;
      } else {
         InteractionAction.FluidPickupTarget target = findNearestPickupTarget(player, range);
         if (target == null) {
            return false;
         } else {
            lookAt(player, target.hitLocation());
            EnumInteractionResult result = player.h.a(player, player.A(), player.fx(), EnumHand.a, target.hitResult());
            if (!result.a()) {
               return false;
            } else {
               player.a(EnumHand.a);
               player.J();
               return true;
            }
         }
      }
   }

   private static double clampRange(EntityPlayer player, double requestedRange) {
      double vanillaMax = player.h.e() ? 5.0 : 4.5;
      return requestedRange <= 0.0 ? vanillaMax : Math.min(requestedRange, vanillaMax);
   }

   @Nullable
   private static InteractionAction.PlaceTarget findNearestPlaceableTop(EntityPlayer player, double range, PlaceSide sidePreference) {
      BlockPosition origin = player.dK();
      Vec3D eye = player.bV();
      Vec3D forward = flatten(player.h(1.0F));
      if (forward.h() < 1.0E-6) {
         forward = new Vec3D(0.0, 0.0, 1.0);
      }

      forward = forward.d();
      Vec3D right = new Vec3D(-forward.i, 0.0, forward.g);
      int radius = (int)Math.ceil(range);
      InteractionAction.PlaceTarget nearest = null;
      double bestScore = Double.NEGATIVE_INFINITY;

      for (BlockPosition pos : BlockPosition.c(origin.b(-radius, -radius, -radius), origin.b(radius, radius, radius))) {
         BlockPosition immutable = pos.j();
         if (isValidPlaceBase(player, immutable)) {
            Vec3D hitLocation = immutable.b().b(0.0, 1.0, 0.0);
            Vec3D offset = hitLocation.d(eye);
            double distance = offset.h();
            if (!(distance > range * range) && !(distance < 0.25)) {
               Vec3D horizontalOffset = flatten(offset);
               if (!(horizontalOffset.h() < 1.0E-6)) {
                  Vec3D direction = horizontalOffset.d();
                  double forwardScore = forward.b(direction);
                  double rightScore = right.b(direction);
                  if (matchesSide(sidePreference, forwardScore, rightScore)) {
                     double score = switch (sidePreference) {
                        case LEFT -> -rightScore * 1000.0 + forwardScore * 150.0 - Math.abs(forwardScore) * 20.0 - Math.sqrt(distance);
                        case RIGHT -> rightScore * 1000.0 + forwardScore * 150.0 - Math.abs(forwardScore) * 20.0 - Math.sqrt(distance);
                        case FRONT -> forwardScore * 1000.0 - Math.abs(rightScore) * 180.0 - Math.sqrt(distance);
                     };
                     if (score > bestScore) {
                        nearest = new InteractionAction.PlaceTarget(immutable, hitLocation);
                        bestScore = score;
                     }
                  }
               }
            }
         }
      }

      return nearest;
   }

   @Nullable
   private static BlockPosition findNearestCrystalBase(EntityPlayer player, double range) {
      BlockPosition origin = player.dK();
      Vec3D eye = player.bV();
      int radius = (int)Math.ceil(range);
      BlockPosition nearest = null;
      double nearestDistance = Double.MAX_VALUE;

      for (BlockPosition pos : BlockPosition.c(origin.b(-radius, -radius, -radius), origin.b(radius, radius, radius))) {
         BlockPosition immutable = pos.j();
         if (isValidCrystalBase(player, immutable)) {
            Vec3D target = immutable.b().b(0.0, 1.0, 0.0);
            double distance = eye.g(target);
            if (!(distance > range * range) && !(distance >= nearestDistance)) {
               nearest = immutable;
               nearestDistance = distance;
            }
         }
      }

      return nearest;
   }

   private static boolean isValidCrystalBase(EntityPlayer player, BlockPosition pos) {
      IBlockData state = player.A().a_(pos);
      if (!state.a(Blocks.cK) && !state.a(Blocks.I)) {
         return false;
      } else {
         BlockPosition above = pos.d();
         return player.A().a_(above).l();
      }
   }

   private static boolean isValidPlaceBase(EntityPlayer player, BlockPosition pos) {
      IBlockData state = player.A().a_(pos);
      if (state.l()) {
         return false;
      } else if (!player.A().a_(pos.d()).v()) {
         return false;
      } else {
         AxisAlignedBB placementSpace = new AxisAlignedBB(pos.u(), pos.v() + 1, pos.w(), pos.u() + 1, pos.v() + 2, pos.w() + 1);
         return player.A().a_(player, placementSpace).isEmpty();
      }
   }

   @Nullable
   private static InteractionAction.LavaTarget findLavaTarget(EntityPlayer player, EntityPlayer target, double range) {
      WorldServer level = player.A();
      Vec3D eye = player.bV();
      BlockPosition[] candidates = new BlockPosition[]{target.dK(), target.dK().e(), target.dK().d()};

      for (BlockPosition pos : candidates) {
         if (level.a_(pos).s()) {
            BlockPosition placePos = pos.d();
            if (level.a_(placePos).v()) {
               Vec3D hitLocation = pos.b().b(0.0, 1.0, 0.0);
               if (!(eye.g(hitLocation) > range * range)) {
                  return new InteractionAction.LavaTarget(new MovingObjectPositionBlock(hitLocation, EnumDirection.b, pos, false), hitLocation);
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static InteractionAction.FluidPickupTarget findNearestPickupTarget(EntityPlayer player, double range) {
      WorldServer level = player.A();
      BlockPosition origin = player.dK();
      Vec3D eye = player.bV();
      int radius = (int)Math.ceil(range);
      InteractionAction.FluidPickupTarget nearest = null;
      double nearestDistance = Double.MAX_VALUE;

      for (BlockPosition pos : BlockPosition.c(origin.b(-radius, -radius, -radius), origin.b(radius, radius, radius))) {
         BlockPosition immutable = pos.j();
         IBlockData state = level.a_(immutable);
         if (state.y().b() && (state.b() instanceof IFluidSource || state.b() instanceof BlockFluids)) {
            Vec3D hitLocation = immutable.b();
            double distance = eye.g(hitLocation);
            if (!(distance > range * range) && !(distance >= nearestDistance)) {
               MovingObjectPositionBlock hitResult = new MovingObjectPositionBlock(hitLocation, EnumDirection.b, immutable, false);
               nearest = new InteractionAction.FluidPickupTarget(hitResult, hitLocation);
               nearestDistance = distance;
            }
         }
      }

      return nearest;
   }

   private static boolean matchesSide(PlaceSide sidePreference, double forwardScore, double rightScore) {
      return switch (sidePreference) {
         case LEFT -> forwardScore >= -0.1 && rightScore <= -0.25;
         case RIGHT -> forwardScore >= -0.1 && rightScore >= 0.25;
         case FRONT -> forwardScore >= 0.2;
      };
   }

   private static Vec3D flatten(Vec3D vector) {
      return new Vec3D(vector.g, 0.0, vector.i);
   }

   @Nullable
   private static EntityEnderCrystal findNearestCrystalOnBase(EntityPlayer player, double range) {
      Vec3D eye = player.bV();
      AxisAlignedBB search = new AxisAlignedBB(eye.g - range, eye.h - range, eye.i - range, eye.g + range, eye.h + range, eye.i + range);
      EntityEnderCrystal nearest = null;
      double nearestDistance = Double.MAX_VALUE;

      for (EntityEnderCrystal crystal : player.A().a(EntityEnderCrystal.class, search)) {
         BlockPosition basePos = crystal.dK().e();
         IBlockData baseState = player.A().a_(basePos);
         if (baseState.a(Blocks.cK) || baseState.a(Blocks.I)) {
            Vec3D target = crystal.dj().f();
            double distance = eye.g(target);
            if (!(distance > range * range) && !(distance >= nearestDistance)) {
               nearest = crystal;
               nearestDistance = distance;
            }
         }
      }

      return nearest;
   }

   private static void lookAt(EntityPlayer player, Vec3D target) {
      Vec3D eye = player.bV();
      Vec3D delta = target.d(eye);
      double horizontal = Math.sqrt(delta.g * delta.g + delta.i * delta.i);
      float yaw = (float)Math.toDegrees(Math.atan2(-delta.g, delta.i));
      float pitch = (float)Math.toDegrees(-Math.atan2(delta.h, horizontal));
      player.v(yaw);
      player.r(yaw);
      player.bF = yaw;
      player.bC = yaw;
      player.bD = yaw;
      player.ab = yaw;
      player.w(pitch);
      player.ac = pitch;
      CraftPlayer bukkit = player.getBukkitEntity();
      Location loc = bukkit.getLocation();
      loc.setYaw(yaw);
      loc.setPitch(pitch);
      bukkit.teleport(loc);
   }

   private record FluidPickupTarget(MovingObjectPositionBlock hitResult, Vec3D hitLocation) {
   }

   private record LavaTarget(MovingObjectPositionBlock hitResult, Vec3D hitLocation) {
   }

   private record PlaceTarget(BlockPosition basePos, Vec3D hitLocation) {
   }
}
