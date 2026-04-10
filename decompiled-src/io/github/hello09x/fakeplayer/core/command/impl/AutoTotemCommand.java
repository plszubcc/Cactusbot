package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.manager.AutoTotemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class AutoTotemCommand extends AbstractCommand {

    @Inject
    private AutoTotemManager autoTotemManager;

    public AutoTotemCommand() {
        super();
    }

    public void autototem(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        boolean enabled = this.autoTotemManager.toggle(fake);

        sender.sendMessage(MessageUtils.translatableWithPrefix(enabled ? "fakeplayer.command.autototem.success.enabled" : "fakeplayer.command.autototem.success.disabled", Component.text(fake.getName(), NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
    }
}
