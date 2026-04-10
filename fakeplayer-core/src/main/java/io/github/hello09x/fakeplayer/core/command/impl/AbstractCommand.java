package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCommand {

    protected static final Logger log = Main.getInstance().getLogger();
    @Inject
    protected NMSBridge bridge;
    @Inject
    protected FakeplayerManager manager;
    @Inject
    protected FakeplayerConfig config;

    public AbstractCommand() {
        super();
    }

    protected @NotNull Player getFakeplayer(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        return this.getFakeplayer(sender, args, (Predicate) null);
    }

    protected @NotNull Player getFakeplayer(@NotNull CommandSender sender, @NotNull CommandArguments args, @Nullable Predicate<Player> predicate) throws WrapperCommandSyntaxException {
        Player fake = (Player) args.get("name");

        if (fake == null && sender instanceof Player p) {
            if (args.getRaw("name") == null) {
                fake = this.manager.getSelection(p);
            }
        }

        if (fake != null) {
            return fake;
        } else {
            Locale locale = TranslatorUtils.getLocale(sender);
            List<Player> all = this.manager.getAll(sender, predicate);
            int count = all.size();

            switch (count) {
                case 0:
                    if (predicate == null) {
                        throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.generic.error.non-fake-player"), locale));
                    }

                    throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.generic.error.non-matching-fake-player"), locale));
                case 1:
                    return (Player) all.get(0);
                default:
                    throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.generic.error.name-required"), locale));
            }
        }
    }

    protected @Nullable Player getTargetNullable(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        return (Player) args.get("name");
    }

    protected @NotNull List<Player> getFakeplayers(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        List<Player> players = (List) args.get("names");

        if (players == null || players.isEmpty()) {
            Player fake = this.manager.getSelection(sender);

            if (fake != null) {
                return Collections.singletonList(fake);
            }
        }

        if (players != null && !players.isEmpty()) {
            return players;
        } else {
            List<Player> fakeplayers = this.manager.getAll(sender);

            switch (fakeplayers.size()) {
                case 0:
                    throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.generic.error.non-fake-player"), TranslatorUtils.getLocale(sender)));
                case 1:
                    return fakeplayers;
                default:
                    throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.generic.error.name-required"), TranslatorUtils.getLocale(sender)));
            }
        }
    }
}
