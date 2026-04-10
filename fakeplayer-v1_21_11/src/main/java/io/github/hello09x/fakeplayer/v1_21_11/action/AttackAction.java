package io.github.hello09x.fakeplayer.v1_21_11.action;

import io.github.hello09x.fakeplayer.core.Main;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPosition.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;

public class AttackAction extends TraceAction {

    private final EntityPlayer player;
    private final CriticalAttackAction criticalAttackAction;

    public AttackAction(EntityPlayer player) {
        super(player);
        this.player = player;
        this.criticalAttackAction = new CriticalAttackAction(player);
    }

    public boolean tick() {
        CraftPlayer bukkit = this.player.getBukkitEntity();
        MovingObjectPosition hit = this.getTarget();

        if (hit == null) {
            return false;
        } else if (hit.d() != EnumMovingObjectType.c) {
            return false;
        } else {
            MovingObjectPositionEntity entityHit = (MovingObjectPositionEntity) hit;
            boolean maceMode = bukkit.getMetadata("fakeplayer:mace_mode").stream().anyMatch((vx) -> {
                return vx.getOwningPlugin() == Main.getInstance() && vx.asBoolean();
            });

            if (maceMode) {
                return (new MaceAttackAction(this.player)).attack(entityHit.a());
            } else {
                boolean forceCrit = bukkit.getMetadata("fakeplayer:force_crit").stream().anyMatch((vx) -> {
                    return vx.getOwningPlugin() == Main.getInstance() && vx.asBoolean();
                });

                if (forceCrit) {
                    bukkit.removeMetadata("fakeplayer:force_crit", Main.getInstance());
                    return this.criticalAttackAction.attack(entityHit.a());
                } else {
                    this.player.e(entityHit.a());
                    this.player.a(EnumHand.a);
                    this.player.hf();
                    this.player.J();
                    return true;
                }
            }
        }
    }

    public void inactiveTick() {}

    public void stop() {}
}
