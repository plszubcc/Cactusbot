package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SprintCommand extends AbstractCommand {

    public SprintCommand() {
        super();
    }

    public void sprint(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        try {
            Player fake = super.getFakeplayer(sender, args);
            Optional optional = args.getOptional("state");

            Objects.requireNonNull(String.class);
            String state = (String) optional.map(String.class::cast).orElse("");
            boolean sprinting = "on".equalsIgnoreCase(state);

            fake.setSprinting(sprinting);
        } catch (WrapperCommandSyntaxException wrappercommandsyntaxexception) {
            throw wrappercommandsyntaxexception;
        } catch (Throwable throwable) {
            Logger logger = SprintCommand.log;
            String s = throwable.getClass().getSimpleName();

            logger.warning("Sprint command failed: " + s + ": " + throwable.getMessage());
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.invalid-arguments").color(NamedTextColor.GRAY));
        }

    }
}
