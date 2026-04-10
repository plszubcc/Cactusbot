package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RespawnCommand extends AbstractCommand {

    public RespawnCommand() {
        super();
    }

    public void respawn(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = super.getFakeplayer(sender, args, Entity::isDead);

        this.bridge.fromPlayer(fake).respawn();
        sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.generic.success"));
    }
}
