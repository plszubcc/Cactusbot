package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.List;
import java.util.StringJoiner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class KillCommand extends AbstractCommand {

    public KillCommand() {
        super();
    }

    public void kill(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        List<Player> fakes = super.getFakeplayers(sender, args);

        if (fakes.isEmpty()) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.kill.error.non-removed", (TextColor) NamedTextColor.GRAY));
        } else {
            StringJoiner names = new StringJoiner(", ");

            for (Player fake : fakes) {
                if (this.manager.remove(fake.getName(), "command kill")) {
                    names.add(fake.getName());
                }
            }

            sender.sendMessage(Component.textOfChildren(new ComponentLike[]{MessageUtils.translatableWithPrefix("fakeplayer.command.kill.success.removed", (TextColor) NamedTextColor.GRAY), Component.space(), Component.text(names.toString())}));
        }
    }
}
