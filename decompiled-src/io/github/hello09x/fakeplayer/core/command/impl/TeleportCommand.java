package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.utils.EntityUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class TeleportCommand extends AbstractCommand {

    public TeleportCommand() {
        super();
    }

    public void tp(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);

        this.teleport(sender, sender, fake);
    }

    public void tphere(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);

        this.teleport(sender, fake, sender);
    }

    public void tps(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Location l1 = sender.getLocation();
        Location l2 = fake.getLocation();

        EntityUtils.teleportAndSound(fake, l1);
        EntityUtils.teleportAndSound(sender, l2);
    }

    private void teleport(@NotNull CommandSender sender, @NotNull Player from, @NotNull Player to) {
        if (!EntityUtils.teleportAndSound(from, to.getLocation())) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.teleport.error.canceled", (TextColor) NamedTextColor.RED));
        }

    }
}
