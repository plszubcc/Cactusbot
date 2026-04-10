package io.github.hello09x.fakeplayer.core.entity.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.entity.action.impl.DropInventoryAction;
import io.github.hello09x.fakeplayer.core.entity.action.impl.DropItemAction;
import io.github.hello09x.fakeplayer.core.entity.action.impl.DropStackAction;
import io.github.hello09x.fakeplayer.core.entity.action.impl.JumpAction;
import io.github.hello09x.fakeplayer.core.entity.action.impl.LookAtEntityAction;
import lombok.Generated;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public abstract class BaseActionTicker implements ActionTicker {

    protected final NMSBridge bridge;
    protected @UnknownNullability Action action;
    protected @NotNull ActionSetting setting;

    public BaseActionTicker(NMSBridge nms, @NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        super();
        this.bridge = nms;
        this.setting = setting;
        Object object;

        switch (action) {
            case JUMP:
                object = new JumpAction(nms.fromPlayer(player));
                break;
            case LOOK_AT_NEAREST_ENTITY:
                object = new LookAtEntityAction(nms.fromPlayer(player));
                break;
            case DROP_ITEM:
                object = new DropItemAction(nms.fromPlayer(player));
                break;
            case DROP_STACK:
                object = new DropStackAction(nms.fromPlayer(player));
                break;
            case DROP_INVENTORY:
                object = new DropInventoryAction(nms.fromPlayer(player));
                break;
            default:
                object = null;
        }

        this.action = (Action) object;
    }

    public boolean tick() {
        if (this.setting.equals(ActionSetting.stop())) {
            this.action.stop();
            return true;
        } else if (this.setting.wait > 0) {
            --this.setting.wait;
            this.inactiveTick();
            return false;
        } else if (this.setting.remains == 0) {
            this.inactiveTick();
            return true;
        } else {
            try {
                if (this.action.tick() && this.setting.remains > 0) {
                    --this.setting.remains;
                }
            } finally {
                this.setting.wait = this.setting.interval;
            }

            return false;
        }
    }

    public void inactiveTick() {
        this.action.inactiveTick();
    }

    public void stop() {
        this.action.stop();
    }

    @Generated
    public @NotNull ActionSetting getSetting() {
        return this.setting;
    }
}
