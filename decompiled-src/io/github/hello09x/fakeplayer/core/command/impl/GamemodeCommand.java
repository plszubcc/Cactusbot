package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import io.github.hello09x.fakeplayer.core.manager.pvp.GamemodeManager;
import io.github.hello09x.fakeplayer.core.manager.pvp.QueueManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class GamemodeCommand extends AbstractCommand {

    @Inject
    private GamemodeManager gamemodes;
    @Inject
    private QueueManager queues;

    public GamemodeCommand() {
        super();
    }

    public void create(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("name")).trim();

        if (name.isBlank()) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.invalid-name").color(NamedTextColor.RED));
        } else {
            try {
                this.gamemodes.create(name);
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.create.success", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
            } catch (IllegalStateException illegalstateexception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.already-exists", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } catch (Exception exception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.save-failed").color(NamedTextColor.RED));
            }

        }
    }

    public void seticon(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            String name = ((String) args.get("name")).trim();
            GamemodeDefinition gm = (GamemodeDefinition) this.config.getGamemodes().get(name);

            if (gm == null) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.not-found", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } else {
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item != null && item.getType() != Material.AIR) {
                    try {
                        this.gamemodes.setIcon(name, item.getType());
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.seticon.success", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                    } catch (Exception exception) {
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.save-failed").color(NamedTextColor.RED));
                    }

                } else {
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.no-item").color(NamedTextColor.RED));
                }
            }
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.player-only").color(NamedTextColor.RED));
        }
    }

    public void addarena(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("name")).trim();
        String arena = ((String) args.get("arena")).trim();

        if (!this.config.getPvpArenas().containsKey(arena)) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.arena-not-found", Component.text(arena, NamedTextColor.WHITE)).color(NamedTextColor.RED));
        } else {
            try {
                this.gamemodes.addArena(name, arena);
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.addarena.success", Component.text(name, NamedTextColor.WHITE), Component.text(arena, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
            } catch (IllegalStateException illegalstateexception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.not-found", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } catch (Exception exception) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.save-failed").color(NamedTextColor.RED));
            }

        }
    }

    public void queue(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            String name = ((String) args.get("name")).trim();

            if (!this.config.getGamemodes().containsKey(name)) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.not-found", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } else {
                switch (this.queues.queue(player, name)) {
                    case JOINED:
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.queue.joined", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                        break;
                    case ALREADY_QUEUED:
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.queue.already", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
                        break;
                    case INVALID_GAMEMODE:
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.not-found", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
                }

            }
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.player-only").color(NamedTextColor.RED));
        }
    }

    public void leave(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player player) {
            String name = ((String) args.get("name")).trim();

            if (!this.config.getGamemodes().containsKey(name)) {
                sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.not-found", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
            } else {
                switch (this.queues.leave(player, name)) {
                    case LEFT_QUEUE:
                    case LEFT_MATCH:
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.queue.left", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                        break;
                    case NOT_QUEUED:
                        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.queue.not-in", Component.text(name, NamedTextColor.WHITE)).color(NamedTextColor.RED));
                }

            }
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.gamemode.error.player-only").color(NamedTextColor.RED));
        }
    }
}
