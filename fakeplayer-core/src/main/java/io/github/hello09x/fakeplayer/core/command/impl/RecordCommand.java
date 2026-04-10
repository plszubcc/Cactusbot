package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.recording.PlayerRecordingManager;
import io.github.hello09x.fakeplayer.core.recording.RecordingData;
import io.github.hello09x.fakeplayer.core.recording.RecordingStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RecordCommand extends AbstractCommand {

    private final PlayerRecordingManager recordingManager;
    private final RecordingStorage storage;

    @Inject
    public RecordCommand(PlayerRecordingManager recordingManager, RecordingStorage storage) {
        super();
        this.recordingManager = recordingManager;
        this.storage = storage;
    }

    public void record(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        String name = String.valueOf(args.get("recording"));
        String action = String.valueOf(args.get("action"));

        if (!this.storage.isValidName(name)) {
            throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.record.error.invalid-name"), TranslatorUtils.getLocale(sender)));
        } else if ("start".equalsIgnoreCase(action)) {
            if (!this.recordingManager.start(sender, name)) {
                throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.record.error.already"), TranslatorUtils.getLocale(sender)));
            } else {
                sender.sendMessage(Component.translatable("fakeplayer.command.record.start.success", new ComponentLike[]{Component.text(name)}));
            }
        } else {
            try {
                RecordingData data = this.recordingManager.stop(sender);

                if (data != null && data.name().equalsIgnoreCase(name)) {
                    sender.sendMessage(Component.translatable("fakeplayer.command.record.stop.success", new ComponentLike[]{Component.text(data.name()), Component.text(data.sampleCount())}));
                } else {
                    throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.record.error.none"), TranslatorUtils.getLocale(sender)));
                }
            } catch (WrapperCommandSyntaxException wrappercommandsyntaxexception) {
                throw wrappercommandsyntaxexception;
            } catch (Exception exception) {
                throw CommandAPI.failWithString(ComponentUtils.toString(Component.translatable("fakeplayer.command.record.error.save-failed"), TranslatorUtils.getLocale(sender)));
            }
        }
    }
}
