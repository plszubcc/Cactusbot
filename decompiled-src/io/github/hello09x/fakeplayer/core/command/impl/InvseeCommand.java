package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.manager.invsee.InvseeManager;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class InvseeCommand extends AbstractCommand {

    private final InvseeManager invseeManager;

    @Inject
    public InvseeCommand(InvseeManager invseeManager) {
        super();
        this.invseeManager = invseeManager;
    }

    public void invsee(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = super.getFakeplayer(sender, args);

        if (!Objects.equals(sender.getLocation().getWorld(), fake.getLocation().getWorld())) {
            throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.invsee.error.not-the-same-world"), TranslatorUtils.getLocale(sender)));
        } else {
            this.invseeManager.invsee(sender, fake);
        }
    }
}
