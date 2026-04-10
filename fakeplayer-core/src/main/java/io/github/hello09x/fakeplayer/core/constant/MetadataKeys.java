package io.github.hello09x.fakeplayer.core.constant;

import com.google.common.collect.Iterables;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MetadataKeys {

    String SELECTION = "fakeplayer:selection";
    String REPLENISH = "fakeplayer:replenish";
    String SPAWNED_AT = "fakeplayer:spawned_at";
    String AUTOFISH = "fakeplayer:autofish";
    String AUTO_TOTEM = "fakeplayer:auto_totem";
    String PRESET = "fakeplayer:preset";
    String PRESET_REACH = "fakeplayer:preset_reach";
    String FORCE_CRIT = "fakeplayer:force_crit";
    String MACE_MODE = "fakeplayer:mace_mode";
    String ARENA_NAME = "fakeplayer:arena_name";
    String ARENA_RETURN = "fakeplayer:arena_return";

    static @Nullable Integer getSpawnedAt(@NotNull Player player) {
        MetadataValue value = (MetadataValue) Iterables.getFirst(player.getMetadata("fakeplayer:spawned_at"), (Object) null);

        return value == null ? null : value.asInt();
    }
}
