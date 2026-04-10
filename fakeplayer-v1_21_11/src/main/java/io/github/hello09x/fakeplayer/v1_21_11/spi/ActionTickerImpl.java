package io.github.hello09x.fakeplayer.v1_21_11.spi;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.entity.action.BaseActionTicker;
import io.github.hello09x.fakeplayer.v1_21_11.action.AttackAction;
import io.github.hello09x.fakeplayer.v1_21_11.action.MineAction;
import io.github.hello09x.fakeplayer.v1_21_11.action.UseAction;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionTickerImpl extends BaseActionTicker implements ActionTicker {

    public ActionTickerImpl(@NotNull NMSBridge nms, @NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        super(nms, player, action, setting);
        if (this.action == null) {
            Object object;

            switch (action) {
                case ATTACK:
                    object = new AttackAction(((CraftPlayer) player).getHandle());
                    break;
                case MINE:
                    object = new MineAction(((CraftPlayer) player).getHandle());
                    break;
                case USE:
                    object = new UseAction(((CraftPlayer) player).getHandle());
                    break;
                case JUMP:
                case LOOK_AT_NEAREST_ENTITY:
                case DROP_INVENTORY:
                case DROP_STACK:
                case DROP_ITEM:
                    throw new UnsupportedOperationException();
                default:
                    throw new MatchException((String) null, (Throwable) null);
            }

            this.action = (Action) object;
        }

    }
}
