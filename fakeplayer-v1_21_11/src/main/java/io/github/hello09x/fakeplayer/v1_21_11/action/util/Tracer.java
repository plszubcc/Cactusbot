package io.github.hello09x.fakeplayer.v1_21_11.action.util;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.RayTrace.BlockCollisionOption;
import net.minecraft.world.level.RayTrace.FluidCollisionOption;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Tracer {
   public Tracer() {
   }

   @Nullable
   public static MovingObjectPosition rayTrace(@NotNull Entity source, float partialTicks, double reach, boolean fluids) {
      MovingObjectPositionBlock blockHit = rayTraceBlocks(source, partialTicks, reach, fluids);
      double maxSqDist = reach * reach;
      if (blockHit != null) {
         maxSqDist = blockHit.g().g(source.n(partialTicks));
      }

      MovingObjectPositionEntity entityHit = rayTraceEntities(source, partialTicks, reach, maxSqDist);
      return (MovingObjectPosition)(entityHit == null ? blockHit : entityHit);
   }

   @Nullable
   public static MovingObjectPositionBlock rayTraceBlocks(@NotNull Entity source, float partialTicks, double reach, boolean fluids) {
      Vec3D pos = source.n(partialTicks);
      Vec3D rotation = source.h(partialTicks);
      Vec3D reachEnd = pos.b(rotation.g * reach, rotation.h * reach, rotation.i * reach);
      return source.ao().a(new RayTrace(pos, reachEnd, BlockCollisionOption.b, fluids ? FluidCollisionOption.c : FluidCollisionOption.a, source));
   }

   @Nullable
   public static MovingObjectPositionEntity rayTraceEntities(@NotNull Entity source, float partialTicks, double reach, double maxSqDist) {
      Vec3D pos = source.n(partialTicks);
      Vec3D reachVec = source.h(partialTicks).c(reach);
      AxisAlignedBB box = source.dj().b(reachVec).g(1.0);
      return rayTraceEntities(source, pos, pos.e(reachVec), box, e -> !e.au() && e.bX(), maxSqDist);
   }

   @Nullable
   public static MovingObjectPositionEntity rayTraceEntities(
      @NotNull Entity source, @NotNull Vec3D start, @NotNull Vec3D end, @NotNull AxisAlignedBB box, @NotNull Predicate<Entity> predicate, double maxSqDistance
   ) {
      World world = source.ao();
      double targetDistance = maxSqDistance;
      Entity target = null;
      Vec3D targetHitPos = null;

      for (Entity current : world.a(source, box, predicate)) {
         AxisAlignedBB currentBox = current.dj().g(current.ci());
         Optional<Vec3D> currentHit = currentBox.b(start, end);
         if (currentBox.d(start)) {
            if (targetDistance >= 0.0) {
               target = current;
               targetHitPos = currentHit.orElse(start);
               targetDistance = 0.0;
            }
         } else if (currentHit.isPresent()) {
            Vec3D currentHitPos = currentHit.get();
            double currentDistance = start.g(currentHitPos);
            if (currentDistance < targetDistance || targetDistance == 0.0) {
               if (current.du() == source.du()) {
                  if (targetDistance == 0.0) {
                     target = current;
                     targetHitPos = currentHitPos;
                  }
               } else {
                  target = current;
                  targetHitPos = currentHitPos;
                  targetDistance = currentDistance;
               }
            }
         }
      }

      return target == null ? null : new MovingObjectPositionEntity(target, targetHitPos);
   }
}
