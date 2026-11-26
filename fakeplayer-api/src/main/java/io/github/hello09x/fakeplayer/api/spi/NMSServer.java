package io.github.hello09x.fakeplayer.api.spi;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface NMSServer {
   @NotNull
   NMSServerPlayer newPlayer(@NotNull UUID var1, @NotNull String var2);
}
