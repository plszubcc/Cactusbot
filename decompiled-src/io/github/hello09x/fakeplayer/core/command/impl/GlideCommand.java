package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.manager.GlideEquipmentManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class GlideCommand extends AbstractCommand {

    private final GlideEquipmentManager glideEquipmentManager;

    @Inject
    public GlideCommand(GlideEquipmentManager glideEquipmentManager) {
        super();
        this.glideEquipmentManager = glideEquipmentManager;
    }

    public void glide(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);

        this.glideEquipmentManager.equipElytraForGlide(fake);
        this.bridge.fromPlayer(fake).startFallFlying();
    }
}
