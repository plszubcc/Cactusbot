package io.github.hello09x.fakeplayer.core.command.impl;

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

    public void queue(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }

        String gamemode = ((String) args.get("gamemode")).trim();
        switch (this.queues.queue(player, gamemode)) {
            case JOINED -> QueueManager.playJoinFeedback(player, gamemode);
            case QUEUED_WAITING -> {
                QueueManager.playJoinFeedback(player, gamemode);
                QueueManager.playQueueFullFeedback(player);
            }
            case ALREADY_QUEUED -> player.sendMessage(Component.text("You are already in a queue or live match.", NamedTextColor.RED));
            case INVALID_GAMEMODE -> player.sendMessage(Component.text("That gamemode does not exist.", NamedTextColor.RED));
            case NO_ARENAS_CONFIGURED -> QueueManager.playNoArenaFeedback(player);
        }
    }

    public void leave(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }

        switch (this.queues.leave(player)) {
            case LEFT_QUEUE, LEFT_MATCH -> QueueManager.playLeaveFeedback(player);
            case NOT_QUEUED -> player.sendMessage(Component.text("You are not in a queue.", NamedTextColor.RED));
        }
    }
}
