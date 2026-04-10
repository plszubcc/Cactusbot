package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.utils.ExperienceUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ExpmeCommand extends AbstractCommand {

    public ExpmeCommand() {
        super();
    }

    public void expme(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        int exp = ExperienceUtils.getExp(fake);

        if (exp == 0) {
            sender.sendMessage(Component.translatable("fakeplayer.command.expme.error.non-experience", new ComponentLike[]{Component.text(fake.getName(), NamedTextColor.WHITE)}).color(NamedTextColor.GRAY));
        } else {
            ExperienceUtils.clean(fake);
            sender.giveExp(exp, false);
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.expme.success", Component.text(fake.getName(), NamedTextColor.WHITE), Component.text(exp, NamedTextColor.DARK_GREEN)).color(NamedTextColor.GRAY));
        }
    }
}
