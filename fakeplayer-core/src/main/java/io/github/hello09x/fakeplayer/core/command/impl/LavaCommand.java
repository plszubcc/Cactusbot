package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.manager.combat.CombatTargetResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class LavaCommand extends AbstractCommand {

    private final CombatTargetResolver targetResolver;

    @Inject
    public LavaCommand(CombatTargetResolver targetResolver) {
        super();
        this.targetResolver = targetResolver;
    }

    public void above(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Player target = this.targetResolver.resolve(fake);

        if (target != null) {
            this.bridge.useLavaBucketOnTarget(fake, target, this.getRange(args));
        }
    }

    private double getRange(@NotNull CommandArguments args) {
        Object value = args.get("range");

        if (value instanceof Number number) {
            return number.doubleValue();
        } else {
            if (value instanceof String text) {
                try {
                    return Double.parseDouble(text);
                } catch (NumberFormatException numberformatexception) {
                    ;
                }
            }

            return 4.5D;
        }
    }
}
