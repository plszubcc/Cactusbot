package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.command.Page;
import io.github.hello09x.fakeplayer.core.util.Mth;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ListCommand extends AbstractCommand {

    public ListCommand() {
        super();
    }

    private static String toLocationString(@NotNull Location location) {
        String s = location.getWorld().getName();

        return s + ": " + StringUtils.joinWith(", ", new Object[]{Mth.floor(location.getX(), 0.5D), Mth.floor(location.getY(), 0.5D), Mth.floor(location.getZ(), 0.5D)});
    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        int page = (Integer) args.getOptional("page").orElse(1);
        int size = (Integer) args.getOptional("size").orElse(10);
        List<Player> fakes = sender.isOp() ? this.manager.getAll() : this.manager.getAll(sender);
        Page<Player> p = Page.<Player>of(fakes, page, size);
        boolean allowsTp = sender instanceof Player && sender.hasPermission("cactusbot.command.tp");

        sender.sendMessage(p.asComponent(Component.translatable("fakeplayer.command.list.title", NamedTextColor.AQUA, new TextDecoration[]{TextDecoration.BOLD}), (fakeplayerx) -> {
            TextComponent partTp = allowsTp ? Component.textOfChildren(new ComponentLike[]{Component.space(), Component.translatable("fakeplayer.command.list.button.teleport", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/cb tp " + fakeplayerx.getName()))}) : Component.empty();
            TextComponent partKill = (TextComponent) Component.textOfChildren(new ComponentLike[]{Component.space(), Component.translatable("fakeplayer.command.list.button.kill", NamedTextColor.RED)}).clickEvent(ClickEvent.runCommand("/cb kill " + fakeplayerx.getName()));

            return Component.textOfChildren(new ComponentLike[]{Component.text(fakeplayerx.getName() + " (" + this.manager.getCreatorName(fakeplayerx) + ")", NamedTextColor.GOLD), Component.text(" - ", NamedTextColor.GRAY), Component.text(toLocationString(fakeplayerx.getLocation()), NamedTextColor.WHITE), partTp, partKill});
        }, (ix) -> {
            return "/cb list " + ix + " " + size;
        }));
    }
}
