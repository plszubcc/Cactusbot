package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.config.ArenaSpawn;
import io.github.hello09x.fakeplayer.core.manager.pvp.ArenaManager;
import io.github.hello09x.fakeplayer.core.preset.PresetInventoryCodec;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ArenaCommand extends AbstractCommand {

    @Inject
    private ArenaManager arenas;

    public ArenaCommand() {
        super();
    }

    public void create(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            String name = ((String) args.get("arena")).trim();

            if (name.isBlank()) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.invalid-name").color(NamedTextColor.RED));
            } else {
                try {
                    this.arenas.createArena(name, player.getLocation());
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.create.success", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                } catch (IllegalStateException illegalstateexception) {
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.already-exists", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
                } catch (Exception exception) {
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
                }

            }
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.player-only").color(NamedTextColor.RED));
        }
    }

    public void delete(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("arena")).trim();

        try {
            this.arenas.deleteArena(name);
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.delete.success", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        } catch (IllegalStateException illegalstateexception) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.not-found", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
        } catch (Exception exception) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
        }

    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        List<String> names = this.config.getPvpArenas().keySet().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();

        if (names.isEmpty()) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.list.empty").color(NamedTextColor.RED));
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.list.success", Component.text(String.join(", ", names), NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        }
    }

    public void setpreset(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String arena = ((String) args.get("arena")).trim();
        String preset = ((String) args.get("preset")).trim();

        if (!preset.isBlank() && !this.config.getPresets().containsKey(preset)) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.preset-not-found", Component.text(preset, NamedTextColor.WHITE)).color(NamedTextColor.RED));
        } else {
            try {
                this.arenas.writePreset(arena, preset);
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.preset.success", Component.text(arena, NamedTextColor.WHITE), Component.text(preset, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
            } catch (Exception exception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
            }

        }
    }

    public void setbot(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String arena = ((String) args.get("arena")).trim();
        String bot = ((String) args.get("bot")).trim();
        String skin = ((String) args.get("skin")).trim();

        try {
            this.arenas.writeBot(arena, bot, skin.isBlank() ? null : skin);
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.bot.success", Component.text(arena, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        } catch (Exception exception) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
        }

    }

    public void setspawn(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            String arena = ((String) args.get("arena")).trim();
            String which = ((String) args.get("which")).trim().toLowerCase();

            if (!which.equals("player") && !which.equals("bot")) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.invalid-which", Component.text(which, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } else {
                try {
                    this.arenas.writeSpawn(arena, which, ArenaSpawn.fromLocation(player.getLocation()));
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.setspawn.success", Component.text(arena, NamedTextColor.WHITE), Component.text(which, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                } catch (Exception exception) {
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
                }

            }
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.player-only").color(NamedTextColor.RED));
        }
    }

    public void setmatch(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String arena = ((String) args.get("arena")).trim();
        int rounds = (Integer) args.get("rounds");

        if (rounds <= 0) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.invalid-rounds").color(NamedTextColor.RED));
        } else {
            try {
                this.arenas.writeRounds(arena, rounds);
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.rounds.success", Component.text(arena, NamedTextColor.WHITE), Component.text(String.valueOf(rounds), NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
            } catch (Exception exception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
            }

        }
    }

    public void setPlayerInventory(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            String arena = ((String) args.get("arena")).trim();
            String action = ((String) args.get("action")).trim();

            if (!action.equalsIgnoreCase("set")) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.invalid-action", Component.text(action, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } else {
                try {
                    this.arenas.writePlayerInventory(arena, PresetInventoryCodec.capture(player));
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.playerinventory.success", Component.text(arena, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                } catch (Exception exception) {
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
                }

            }
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.player-only").color(NamedTextColor.RED));
        }
    }

    public void setLobby(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            try {
                this.arenas.writeLobby(ArenaSpawn.fromLocation(player.getLocation()));
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.lobby.success").color(NamedTextColor.GRAY));
            } catch (Exception exception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.save-failed").color(NamedTextColor.RED));
            }

        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.arena.error.player-only").color(NamedTextColor.RED));
        }
    }
}
