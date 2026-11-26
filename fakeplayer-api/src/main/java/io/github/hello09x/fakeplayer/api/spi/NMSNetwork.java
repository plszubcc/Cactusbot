package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSNetwork {
   @NotNull
   NMSServerGamePacketListener placeNewPlayer(@NotNull Server var1, @NotNull Player var2);

   @NotNull
   NMSServerGamePacketListener getServerGamePacketListener() throws IllegalStateException;
}
