package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MaceCommand extends AbstractCommand {

    public MaceCommand() {
        super();
    }

    public void mace(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Object state = args.get("state");
        boolean enabled = !"off".equalsIgnoreCase(state == null ? "" : state.toString());
        boolean currentlyEnabled = fake.getMetadata("fakeplayer:mace_mode").stream().anyMatch((vx) -> {
            return vx.getOwningPlugin() == Main.getInstance() && vx.asBoolean();
        });

        if (enabled != currentlyEnabled) {
            if (enabled) {
                fake.setMetadata("fakeplayer:mace_mode", new FixedMetadataValue(Main.getInstance(), true));
            } else {
                fake.removeMetadata("fakeplayer:mace_mode", Main.getInstance());
            }

            sender.sendMessage(MessageUtils.translatableWithPrefix(enabled ? "fakeplayer.command.mace.success.enabled" : "fakeplayer.command.mace.success.disabled", Component.text(fake.getName(), NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        }
    }
}
