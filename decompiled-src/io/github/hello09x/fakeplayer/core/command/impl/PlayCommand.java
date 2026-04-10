package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.gui.PlayGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayCommand extends AbstractCommand {

    @Inject
    private PlayGui playGui;

    public PlayCommand() {
        super();
    }

    public void play(@NotNull Player sender, @NotNull CommandArguments args) {
        this.playGui.open(sender);
    }
}
