package io.github.hello09x.fakeplayer.core.manager.invsee;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface InvseeManager extends Listener {
   boolean invsee(@NotNull Player var1, @NotNull Player var2);
}
