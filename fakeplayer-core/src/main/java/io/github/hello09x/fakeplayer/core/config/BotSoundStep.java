package io.github.hello09x.fakeplayer.core.config;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public record BotSoundStep(@NotNull Sound sound, float volume, float pitch, int delayTicks) {
}
