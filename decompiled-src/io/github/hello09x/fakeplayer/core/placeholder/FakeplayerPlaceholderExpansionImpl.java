package io.github.hello09x.fakeplayer.core.placeholder;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.translation.PluginTranslator;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class FakeplayerPlaceholderExpansionImpl extends PlaceholderExpansion implements FakeplayerPlaceholderExpansion {

    private final FakeplayerManager manager;
    private final ActionManager actionManager;
    private final PluginTranslator translator;

    @Inject
    public FakeplayerPlaceholderExpansionImpl(FakeplayerManager manager, ActionManager actionManager, PluginTranslator translator) {
        super();
        this.manager = manager;
        this.actionManager = actionManager;
        this.translator = translator;
    }

    public @NotNull String getIdentifier() {
        return Main.getInstance().getName();
    }

    public @NotNull String getAuthor() {
        return (String) Iterables.getFirst(Main.getInstance().getPluginMeta().getAuthors(), "hello09x");
    }

    public @NotNull String getVersion() {
        return "1.0";
    }

    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        return params.equalsIgnoreCase("total") ? String.valueOf(this.manager.getSize()) : (params.equalsIgnoreCase("creator") && player != null && this.manager.isFake(player) ? (String) Optional.ofNullable(this.manager.getCreatorName(player)).orElse(params) : (params.equalsIgnoreCase("actions") && player != null && this.manager.isFake(player) ? (String) this.actionManager.getActiveActions(player).stream().map(Enum::name).collect(Collectors.joining("|")) : (params.equalsIgnoreCase("actions_translated") && player != null && this.manager.isFake(player) ? (String) this.actionManager.getActiveActions(player).stream().map((actionTypex) -> {
            MessageFormat format = this.translator.translate(actionTypex.translationKey(), (Locale) null);

            return format == null ? "" : format.format(new Object[0]);
        }).collect(Collectors.joining("|")) : params)));
    }

    @EventHandler
    public void unregister(@NotNull PluginDisableEvent event) {
        if (event.getPlugin() == Main.getInstance()) {
            this.unregister();
        }

    }
}
