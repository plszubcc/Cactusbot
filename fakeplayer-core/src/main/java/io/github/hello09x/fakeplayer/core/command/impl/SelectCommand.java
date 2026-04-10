package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SelectCommand extends AbstractCommand {

    public SelectCommand() {
        super();
    }

    public void select(@NotNull Player sender, @NotNull CommandArguments args) {
        Player target = this.getTargetNullable(sender, args);

        this.manager.setSelection(sender, target);
        if (target == null) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.select.success.clear", (TextColor) NamedTextColor.GRAY));
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.select.success.selected", Component.text(target.getName(), NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        }

    }

    public void selection(@NotNull Player sender, @NotNull CommandArguments args) {
        Player selection = this.manager.getSelection(sender);

        if (selection == null) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.selection.error.none", (TextColor) NamedTextColor.GRAY));
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.selection.success", Component.text(selection.getName(), NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        }

    }
}
