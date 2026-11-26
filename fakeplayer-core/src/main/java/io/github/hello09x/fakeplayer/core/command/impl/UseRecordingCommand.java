package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.recording.RecordingReplayManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class UseRecordingCommand extends AbstractCommand {
   private final RecordingReplayManager replayManager;

   @Inject
   public UseRecordingCommand(RecordingReplayManager replayManager) {
      this.replayManager = replayManager;
   }

   public void use(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      String recordingName = String.valueOf(args.get("recording"));
      Player fake = this.getFakeplayer(sender, args);
      if (!this.replayManager.useRecording(recordingName, fake)) {
         throw CommandAPI.failWithString(
            ComponentUtils.toString(
               Component.translatable("fakeplayer.command.userec.error.not-found", new ComponentLike[]{Component.text(recordingName)}),
               TranslatorUtils.getLocale(sender)
            )
         );
      } else {
         sender.sendMessage(
            Component.translatable("fakeplayer.command.userec.success", new ComponentLike[]{Component.text(recordingName), Component.text(fake.getName())})
         );
      }
   }
}
