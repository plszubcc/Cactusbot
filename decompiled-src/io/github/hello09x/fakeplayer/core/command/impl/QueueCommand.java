package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.manager.pvp.QueueManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class QueueCommand extends AbstractCommand {

    @Inject
    private QueueManager queues;

    public QueueCommand() {
        super();
    }

    public void queue(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String gamemode = ((String) args.get("gamemode")).trim();

            switch (this.queues.queue(player, gamemode)) {
                case JOINED:
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.success", Component.text(gamemode, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
                    break;
                case ALREADY_QUEUED:
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.already", Component.text(gamemode, NamedTextColor.WHITE)).color(NamedTextColor.RED));
                    break;
                case INVALID_GAMEMODE:
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.error.failed").color(NamedTextColor.RED));
            }

        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.error.player-only").color(NamedTextColor.RED));
        }
    }

    public void leave(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            switch (this.queues.leave(player)) {
                case LEFT_QUEUE:
                case LEFT_MATCH:
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.leave.success").color(NamedTextColor.GRAY));
                    break;
                case NOT_QUEUED:
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.leave.not-in-queue").color(NamedTextColor.RED));
            }

        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.queue.error.player-only").color(NamedTextColor.RED));
        }
    }
}
