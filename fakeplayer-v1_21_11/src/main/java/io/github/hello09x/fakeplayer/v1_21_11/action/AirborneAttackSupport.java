package io.github.hello09x.fakeplayer.v1_21_11.action;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.NotNull;

final class AirborneAttackSupport {

    private AirborneAttackSupport() {
    }

    static boolean canUseSpecialAttack(@NotNull EntityPlayer player) {
        if (player.gj()) {
            return true;
        }

        if (player.aV()) {
            return false;
        }

        Vec3D velocity = player.dN();
        return Math.abs(velocity.h) > 0.08D || player.an > 0.0D;
    }
}
