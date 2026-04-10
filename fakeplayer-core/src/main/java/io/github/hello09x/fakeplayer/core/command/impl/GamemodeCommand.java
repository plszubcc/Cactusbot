package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import io.github.hello09x.fakeplayer.core.manager.pvp.GamemodeManager;
import io.github.hello09x.fakeplayer.core.manager.pvp.QueueManager;
import java.util.ArrayList;
import java.util.Map;
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

    public void create(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("name")).trim();
        if (name.isBlank()) {
            sender.sendMessage(Component.text("Invalid gamemode name.", NamedTextColor.RED));
            return;
        }

        try {
            this.gamemodes.create(name);
            sender.sendMessage(Component.text("Created gamemode " + name + ".", NamedTextColor.GRAY));
        } catch (IllegalStateException exception) {
            sender.sendMessage(Component.text("Gamemode " + name + " already exists.", NamedTextColor.RED));
        } catch (Exception exception) {
            sender.sendMessage(Component.text("Could not save the gamemode.", NamedTextColor.RED));
        }
    }

    public void delete(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("name")).trim();

        try {
            this.gamemodes.delete(name);
            sender.sendMessage(Component.text("Deleted gamemode " + name + ".", NamedTextColor.GRAY));
        } catch (IllegalStateException exception) {
            sender.sendMessage(Component.text("Gamemode " + name + " was not found.", NamedTextColor.RED));
        } catch (Exception exception) {
            sender.sendMessage(Component.text("Could not delete the gamemode.", NamedTextColor.RED));
        }
    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<String, GamemodeDefinition> entry : this.config.getGamemodes().entrySet()) {
            lines.add(entry.getKey() + " (" + entry.getValue().arenas().size() + " arenas)");
        }
        lines.sort(String.CASE_INSENSITIVE_ORDER);

        if (lines.isEmpty()) {
            sender.sendMessage(Component.text("No gamemodes are configured.", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Component.text(String.join(", ", lines), NamedTextColor.GRAY));
    }

    public void seticon(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }

        String name = ((String) args.get("name")).trim();
        GamemodeDefinition definition = this.config.getGamemodes().get(name);
        if (definition == null) {
            sender.sendMessage(Component.text("Gamemode " + name + " was not found.", NamedTextColor.RED));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(Component.text("Hold an item first.", NamedTextColor.RED));
            return;
        }

        try {
            this.gamemodes.setIcon(name, item.getType());
            sender.sendMessage(Component.text("Updated icon for " + name + ".", NamedTextColor.GRAY));
        } catch (Exception exception) {
            sender.sendMessage(Component.text("Could not save the gamemode icon.", NamedTextColor.RED));
        }
    }

    public void addarena(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("name")).trim();
        String arena = ((String) args.get("arena")).trim();
        if (!this.config.getPvpArenas().containsKey(arena)) {
            sender.sendMessage(Component.text("Arena " + arena + " was not found.", NamedTextColor.RED));
            return;
        }

        try {
            this.gamemodes.addArena(name, arena);
            sender.sendMessage(Component.text("Added arena " + arena + " to gamemode " + name + ".", NamedTextColor.GRAY));
        } catch (IllegalStateException exception) {
            sender.sendMessage(Component.text("Gamemode " + name + " was not found.", NamedTextColor.RED));
        } catch (Exception exception) {
            sender.sendMessage(Component.text("Could not save the gamemode.", NamedTextColor.RED));
        }
    }

    public void removearena(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        String name = ((String) args.get("name")).trim();
        String arena = ((String) args.get("arena")).trim();

        try {
            this.gamemodes.removeArena(name, arena);
            sender.sendMessage(Component.text("Removed arena " + arena + " from gamemode " + name + ".", NamedTextColor.GRAY));
        } catch (IllegalStateException exception) {
            sender.sendMessage(Component.text("Gamemode " + name + " was not found.", NamedTextColor.RED));
        } catch (Exception exception) {
            sender.sendMessage(Component.text("Could not save the gamemode.", NamedTextColor.RED));
        }
    }

    public void queue(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }

        String name = ((String) args.get("name")).trim();
        switch (this.queues.queue(player, name)) {
            case JOINED -> QueueManager.playJoinFeedback(player, name);
            case QUEUED_WAITING -> {
                QueueManager.playJoinFeedback(player, name);
                QueueManager.playQueueFullFeedback(player);
            }
            case ALREADY_QUEUED -> player.sendMessage(Component.text("You are already in a queue or live match.", NamedTextColor.RED));
            case INVALID_GAMEMODE -> player.sendMessage(Component.text("Gamemode " + name + " was not found.", NamedTextColor.RED));
            case NO_ARENAS_CONFIGURED -> QueueManager.playNoArenaFeedback(player);
        }
    }

    public void leave(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }

        String name = ((String) args.get("name")).trim();
        if (!this.config.getGamemodes().containsKey(name)) {
            sender.sendMessage(Component.text("Gamemode " + name + " was not found.", NamedTextColor.RED));
            return;
        }

        switch (this.queues.leave(player, name)) {
            case LEFT_QUEUE, LEFT_MATCH -> QueueManager.playLeaveFeedback(player);
            case NOT_QUEUED -> player.sendMessage(Component.text("You are not in that queue.", NamedTextColor.RED));
        }
    }
}
