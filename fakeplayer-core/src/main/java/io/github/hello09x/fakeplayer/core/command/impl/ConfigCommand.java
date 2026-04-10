package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.feature.FakeplayerFeatureManager;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigCommand extends AbstractCommand {

    private final FakeplayerFeatureManager featureManager;

    @Inject
    public ConfigCommand(FakeplayerFeatureManager featureManager) {
        super();
        this.featureManager = featureManager;
    }

    public void setConfig(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Feature feature = (Feature) Objects.requireNonNull(args.get("feature"));

        if (!feature.testPermissions(sender)) {
            throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.config.set.error.no-permission"), TranslatorUtils.getLocale(sender)));
        } else {
            String option = (String) Objects.requireNonNull(args.get("option"));

            this.featureManager.setFeature(sender, feature, option);
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.config.set.success", Component.translatable(feature.translationKey(), NamedTextColor.GOLD), Component.text(option, NamedTextColor.WHITE)).color(NamedTextColor.GRAY));
        }
    }

    public void listConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        CompletableFuture.runAsync(() -> {
            List<TextComponent> lines = this.featureManager.getFeatures(sender).values().stream().map((featurex) -> {
                return Component.textOfChildren(new ComponentLike[]{Component.translatable(featurex.key(), NamedTextColor.GOLD), Component.text(": ", NamedTextColor.GRAY), Component.join(JoinConfiguration.separator(Component.space()), featurex.key().getOptions().stream().map((optionx) -> {
                            Style style = optionx.equals(featurex.value()) ? Style.style(NamedTextColor.GREEN, new TextDecoration[]{TextDecoration.UNDERLINED}) : Style.style(NamedTextColor.GRAY);
                            TextComponent textcomponent = (TextComponent) Component.text("[" + optionx + "]").style(style);
                            String s1 = String.valueOf(featurex.key());

                            return (TextComponent) textcomponent.clickEvent(ClickEvent.runCommand("/cb config set " + s1 + " " + optionx));
                        }).toList())});
            }).toList();
            Component message = Component.join(JoinConfiguration.separator(Component.newline()), lines);

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                sender.sendMessage(message);
            });
        });
    }
}
