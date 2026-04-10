package io.github.hello09x.fakeplayer.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OneEntity;
import dev.jorel.commandapi.executors.ExecutorType;
import io.github.hello09x.devtools.command.Commands;
import io.github.hello09x.devtools.command.HelpCommand;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.command.impl.ActionCommand;
import io.github.hello09x.fakeplayer.core.command.impl.ArenaCommand;
import io.github.hello09x.fakeplayer.core.command.impl.AutoTotemCommand;
import io.github.hello09x.fakeplayer.core.command.impl.BucketCommand;
import io.github.hello09x.fakeplayer.core.command.impl.CmdCommand;
import io.github.hello09x.fakeplayer.core.command.impl.ConfigCommand;
import io.github.hello09x.fakeplayer.core.command.impl.CritCommand;
import io.github.hello09x.fakeplayer.core.command.impl.CrystalCommand;
import io.github.hello09x.fakeplayer.core.command.impl.CustomActionCommand;
import io.github.hello09x.fakeplayer.core.command.impl.DebugCommand;
import io.github.hello09x.fakeplayer.core.command.impl.DistanceCommand;
import io.github.hello09x.fakeplayer.core.command.impl.ExpmeCommand;
import io.github.hello09x.fakeplayer.core.command.impl.GamemodeCommand;
import io.github.hello09x.fakeplayer.core.command.impl.GlideCommand;
import io.github.hello09x.fakeplayer.core.command.impl.HoldCommand;
import io.github.hello09x.fakeplayer.core.command.impl.InvseeCommand;
import io.github.hello09x.fakeplayer.core.command.impl.KillCommand;
import io.github.hello09x.fakeplayer.core.command.impl.KillallCommand;
import io.github.hello09x.fakeplayer.core.command.impl.LavaCommand;
import io.github.hello09x.fakeplayer.core.command.impl.ListCommand;
import io.github.hello09x.fakeplayer.core.command.impl.MaceCommand;
import io.github.hello09x.fakeplayer.core.command.impl.MoveCommand;
import io.github.hello09x.fakeplayer.core.command.impl.PlaceCommand;
import io.github.hello09x.fakeplayer.core.command.impl.PlayCommand;
import io.github.hello09x.fakeplayer.core.command.impl.PresetCommand;
import io.github.hello09x.fakeplayer.core.command.impl.QueueCommand;
import io.github.hello09x.fakeplayer.core.command.impl.RecordCommand;
import io.github.hello09x.fakeplayer.core.command.impl.ReloadCommand;
import io.github.hello09x.fakeplayer.core.command.impl.RespawnCommand;
import io.github.hello09x.fakeplayer.core.command.impl.RideCommand;
import io.github.hello09x.fakeplayer.core.command.impl.RotationCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SelectCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SetCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SkinCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SleepCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SneakCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SpawnCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SprintCommand;
import io.github.hello09x.fakeplayer.core.command.impl.StatusCommand;
import io.github.hello09x.fakeplayer.core.command.impl.StopCommand;
import io.github.hello09x.fakeplayer.core.command.impl.SwapCommand;
import io.github.hello09x.fakeplayer.core.command.impl.TeleportCommand;
import io.github.hello09x.fakeplayer.core.command.impl.UseRecordingCommand;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

@Singleton
public class CommandRegistry {

    @Inject
    private ActionCommand actionCommand;
    @Inject
    private CmdCommand cmdCommand;
    @Inject
    private ConfigCommand configCommand;
    @Inject
    private DistanceCommand distanceCommand;
    @Inject
    private ExpmeCommand expmeCommand;
    @Inject
    private CritCommand critCommand;
    @Inject
    private AutoTotemCommand autoTotemCommand;
    @Inject
    private GlideCommand glideCommand;
    @Inject
    private MaceCommand maceCommand;
    @Inject
    private LavaCommand lavaCommand;
    @Inject
    private BucketCommand bucketCommand;
    @Inject
    private PlaceCommand placeCommand;
    @Inject
    private CrystalCommand crystalCommand;
    @Inject
    private CustomActionCommand customActionCommand;
    @Inject
    private RecordCommand recordCommand;
    @Inject
    private UseRecordingCommand useRecordingCommand;
    @Inject
    private HoldCommand holdCommand;
    @Inject
    private InvseeCommand invseeCommand;
    @Inject
    private KillCommand killCommand;
    @Inject
    private KillallCommand killallCommand;
    @Inject
    private ListCommand listCommand;
    @Inject
    private MoveCommand moveCommand;
    @Inject
    private ReloadCommand reloadCommand;
    @Inject
    private RespawnCommand respawnCommand;
    @Inject
    private RideCommand rideCommand;
    @Inject
    private RotationCommand rotationCommand;
    @Inject
    private SelectCommand selectCommand;
    @Inject
    private SetCommand setCommand;
    @Inject
    private SkinCommand skinCommand;
    @Inject
    private SleepCommand sleepCommand;
    @Inject
    private SneakCommand sneakCommand;
    @Inject
    private SprintCommand sprintCommand;
    @Inject
    private SpawnCommand spawnCommand;
    @Inject
    private StatusCommand statusCommand;
    @Inject
    private SwapCommand swapCommand;
    @Inject
    private TeleportCommand teleportCommand;
    @Inject
    private DebugCommand debugCommand;
    @Inject
    private StopCommand stopCommand;
    @Inject
    private PresetCommand presetCommand;
    @Inject
    private ArenaCommand arenaCommand;
    @Inject
    private QueueCommand queueCommand;
    @Inject
    private GamemodeCommand gamemodeCommand;
    @Inject
    private PlayCommand playCommand;
    @Inject
    private FakeplayerConfig config;

    public CommandRegistry() {
        super();
    }

    public void register() {
        CommandAPICommand commandapicommand = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("cactusbot").withAliases(new String[]{"cb"})).withHelp(ComponentUtils.toString(Component.translatable("fakeplayer.command.fp.short-description"), (Locale) null), ComponentUtils.toString(Component.translatable("fakeplayer.command.fp.full-description"), (Locale) null))).withUsage(new String[]{"type cb ? for more information", "hello09x [汤姆]"})).withPermission("cactusbot.command.spawn");
        CommandAPICommand[] acommandapicommand = new CommandAPICommand[56];
        CommandAPICommand commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("select").withPermission("cactusbot.command.select")).withShortDescription("fakeplayer.command.select.description")).withRequirement(CommandSupports::needSelect)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        SelectCommand selectcommand = this.selectCommand;

        Objects.requireNonNull(this.selectCommand);
        acommandapicommand[0] = (CommandAPICommand) commandapicommand1.executesPlayer(selectcommand::select);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("selection").withPermission("cactusbot.command.select")).withShortDescription("fakeplayer.command.selection.description")).withRequirement(CommandSupports::needSelect);
        selectcommand = this.selectCommand;
        Objects.requireNonNull(this.selectCommand);
        acommandapicommand[1] = (CommandAPICommand) commandapicommand1.executesPlayer(selectcommand::selection);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("spawn").withPermission("cactusbot.command.spawn")).withShortDescription("fakeplayer.command.spawn.description")).withOptionalArguments(new Argument[]{Commands.text("arg1"), Commands.text("arg2"), Commands.text("arg3"), Commands.text("arg4"), Commands.text("arg5")});
        SpawnCommand spawncommand = this.spawnCommand;

        Objects.requireNonNull(this.spawnCommand);
        acommandapicommand[2] = (CommandAPICommand) commandapicommand1.executes(spawncommand::spawn, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("preset").withPermission("cactusbot.command.preset")).withShortDescription("fakeplayer.command.preset.description");
        CommandAPICommand[] acommandapicommand1 = new CommandAPICommand[4];
        CommandAPICommand commandapicommand2 = (CommandAPICommand) Commands.command("create").withArguments(new Argument[]{Commands.text("preset")});
        PresetCommand presetcommand = this.presetCommand;

        Objects.requireNonNull(this.presetCommand);
        acommandapicommand1[0] = (CommandAPICommand) commandapicommand2.executes(presetcommand::create, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("delete").withArguments(new Argument[]{Commands.text("preset")});
        presetcommand = this.presetCommand;
        Objects.requireNonNull(this.presetCommand);
        acommandapicommand1[1] = (CommandAPICommand) commandapicommand2.executes(presetcommand::delete, new ExecutorType[0]);
        commandapicommand2 = Commands.command("list");
        presetcommand = this.presetCommand;
        Objects.requireNonNull(this.presetCommand);
        acommandapicommand1[2] = (CommandAPICommand) commandapicommand2.executes(presetcommand::list, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("inventory").withArguments(new Argument[]{Commands.text("preset"), Commands.text("action")});
        presetcommand = this.presetCommand;
        Objects.requireNonNull(this.presetCommand);
        acommandapicommand1[3] = (CommandAPICommand) commandapicommand2.executes(presetcommand::inventorySet, new ExecutorType[0]);
        acommandapicommand[3] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand1);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("arena").withPermission("cactusbot.command.arena")).withShortDescription("fakeplayer.command.arena.description");
        acommandapicommand1 = new CommandAPICommand[10];
        commandapicommand2 = (CommandAPICommand) Commands.command("create").withArguments(new Argument[]{Commands.text("arena")});
        ArenaCommand arenacommand = this.arenaCommand;

        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[0] = (CommandAPICommand) commandapicommand2.executes(arenacommand::create, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("delete").withArguments(new Argument[]{Commands.text("arena")});
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[1] = (CommandAPICommand) commandapicommand2.executes(arenacommand::delete, new ExecutorType[0]);
        commandapicommand2 = Commands.command("list");
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[2] = (CommandAPICommand) commandapicommand2.executes(arenacommand::list, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("setpreset").withArguments(new Argument[]{Commands.text("arena"), Commands.text("preset")});
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[3] = (CommandAPICommand) commandapicommand2.executes(arenacommand::setpreset, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("setbot").withArguments(new Argument[]{Commands.text("arena"), Commands.text("bot"), Commands.text("skin")});
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[4] = (CommandAPICommand) commandapicommand2.executes(arenacommand::setbot, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("setspawn").withArguments(new Argument[]{Commands.text("arena"), Commands.text("which")});
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[5] = (CommandAPICommand) commandapicommand2.executes(arenacommand::setspawn, new ExecutorType[0]);
        commandapicommand2 = Commands.command("setlobby");
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[6] = (CommandAPICommand) commandapicommand2.executes(arenacommand::setLobby, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("playerinventory").withArguments(new Argument[]{Commands.text("arena"), Commands.text("action")});
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[7] = (CommandAPICommand) commandapicommand2.executes(arenacommand::setPlayerInventory, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("setmatch").withArguments(new Argument[]{Commands.text("arena"), Commands.int32("rounds", 1)});
        arenacommand = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand1[8] = (CommandAPICommand) commandapicommand2.executes(arenacommand::setmatch, new ExecutorType[0]);
        commandapicommand2 = Commands.command("set");
        CommandAPICommand[] acommandapicommand2 = new CommandAPICommand[3];
        CommandAPICommand commandapicommand3 = (CommandAPICommand) Commands.command("playerinventory").withArguments(new Argument[]{Commands.text("arena"), Commands.text("action")});
        ArenaCommand arenacommand1 = this.arenaCommand;

        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand2[0] = (CommandAPICommand) commandapicommand3.executes(arenacommand1::setPlayerInventory, new ExecutorType[0]);
        commandapicommand3 = (CommandAPICommand) Commands.command("match").withArguments(new Argument[]{Commands.text("arena"), Commands.int32("rounds", 1)});
        arenacommand1 = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand2[1] = (CommandAPICommand) commandapicommand3.executes(arenacommand1::setmatch, new ExecutorType[0]);
        commandapicommand3 = Commands.command("lobby");
        arenacommand1 = this.arenaCommand;
        Objects.requireNonNull(this.arenaCommand);
        acommandapicommand2[2] = (CommandAPICommand) commandapicommand3.executes(arenacommand1::setLobby, new ExecutorType[0]);
        acommandapicommand1[9] = (CommandAPICommand) commandapicommand2.withSubcommands(acommandapicommand2);
        acommandapicommand[4] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand1);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("gamemode").withPermission("cactusbot.command.gamemode")).withShortDescription("fakeplayer.command.gamemode.description");
        acommandapicommand1 = new CommandAPICommand[5];
        commandapicommand2 = (CommandAPICommand) Commands.command("create").withArguments(new Argument[]{Commands.text("name")});
        GamemodeCommand gamemodecommand = this.gamemodeCommand;

        Objects.requireNonNull(this.gamemodeCommand);
        acommandapicommand1[0] = (CommandAPICommand) commandapicommand2.executes(gamemodecommand::create, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("seticon").withArguments(new Argument[]{Commands.text("name")});
        gamemodecommand = this.gamemodeCommand;
        Objects.requireNonNull(this.gamemodeCommand);
        acommandapicommand1[1] = (CommandAPICommand) commandapicommand2.executes(gamemodecommand::seticon, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("addarena").withArguments(new Argument[]{Commands.text("name"), Commands.text("arena")});
        gamemodecommand = this.gamemodeCommand;
        Objects.requireNonNull(this.gamemodeCommand);
        acommandapicommand1[2] = (CommandAPICommand) commandapicommand2.executes(gamemodecommand::addarena, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("queue").withArguments(new Argument[]{Commands.text("name")});
        gamemodecommand = this.gamemodeCommand;
        Objects.requireNonNull(this.gamemodeCommand);
        acommandapicommand1[3] = (CommandAPICommand) commandapicommand2.executes(gamemodecommand::queue, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) Commands.command("leave").withArguments(new Argument[]{Commands.text("name")});
        gamemodecommand = this.gamemodeCommand;
        Objects.requireNonNull(this.gamemodeCommand);
        acommandapicommand1[4] = (CommandAPICommand) commandapicommand2.executes(gamemodecommand::leave, new ExecutorType[0]);
        acommandapicommand[5] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand1);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("queue").withPermission("cactusbot.command.queue")).withShortDescription("fakeplayer.command.queue.description")).withArguments(new Argument[]{Commands.text("gamemode")});
        QueueCommand queuecommand = this.queueCommand;

        Objects.requireNonNull(this.queueCommand);
        commandapicommand1 = (CommandAPICommand) commandapicommand1.executes(queuecommand::queue, new ExecutorType[0]);
        CommandAPICommand[] acommandapicommand3 = new CommandAPICommand[1];

        commandapicommand2 = (CommandAPICommand) Commands.command("leave").withShortDescription("fakeplayer.command.queue.leave.description");
        QueueCommand queuecommand1 = this.queueCommand;

        Objects.requireNonNull(this.queueCommand);
        acommandapicommand3[0] = (CommandAPICommand) commandapicommand2.executes(queuecommand1::leave, new ExecutorType[0]);
        acommandapicommand[6] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand3);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("play").withPermission("cactusbot.command.queue")).withShortDescription("fakeplayer.command.play.description");
        PlayCommand playcommand = this.playCommand;

        Objects.requireNonNull(this.playCommand);
        acommandapicommand[7] = (CommandAPICommand) commandapicommand1.executesPlayer(playcommand::play);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("kill").withPermission("cactusbot.command.kill")).withShortDescription("fakeplayer.command.kill.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayers("names")});
        KillCommand killcommand = this.killCommand;

        Objects.requireNonNull(this.killCommand);
        acommandapicommand[8] = (CommandAPICommand) commandapicommand1.executes(killcommand::kill, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("list").withPermission("cactusbot.command.list")).withRequirement(CommandSupports::hasFakeplayer)).withShortDescription("fakeplayer.command.list.description")).withOptionalArguments(new Argument[]{Commands.int32("page", 1), Commands.int32("size", 1)});
        ListCommand listcommand = this.listCommand;

        Objects.requireNonNull(this.listCommand);
        acommandapicommand[9] = (CommandAPICommand) commandapicommand1.executes(listcommand::list, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("distance").withPermission("cactusbot.command.distance")).withShortDescription("fakeplayer.command.distance.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        DistanceCommand distancecommand = this.distanceCommand;

        Objects.requireNonNull(this.distanceCommand);
        acommandapicommand[10] = (CommandAPICommand) commandapicommand1.executesPlayer(distancecommand::distance);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("skin").withPermission("cactusbot.command.skin")).withShortDescription("fakeplayer.command.skin.description")).withRequirement(CommandSupports::hasFakeplayer)).withArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.text("skin")})).withOptionalArguments(new Argument[]{Commands.literals("variant", List.of("classic", "slim"))});
        SkinCommand skincommand = this.skinCommand;

        Objects.requireNonNull(this.skinCommand);
        acommandapicommand[11] = (CommandAPICommand) commandapicommand1.executes(skincommand::skin, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("invsee").withPermission("cactusbot.command.invsee")).withShortDescription("fakeplayer.command.invsee.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        InvseeCommand invseecommand = this.invseeCommand;

        Objects.requireNonNull(this.invseeCommand);
        acommandapicommand[12] = (CommandAPICommand) commandapicommand1.executesPlayer(invseecommand::invsee);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("hold").withPermission("cactusbot.command.hold")).withShortDescription("fakeplayer.command.hold.description")).withRequirement(CommandSupports::hasFakeplayer)).withArguments(new Argument[]{Commands.text("selection")})).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        HoldCommand holdcommand = this.holdCommand;

        Objects.requireNonNull(this.holdCommand);
        acommandapicommand[13] = (CommandAPICommand) commandapicommand1.executes(holdcommand::hold, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("status").withPermission("cactusbot.command.status")).withShortDescription("fakeplayer.command.status.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        StatusCommand statuscommand = this.statusCommand;

        Objects.requireNonNull(this.statusCommand);
        acommandapicommand[14] = (CommandAPICommand) commandapicommand1.executes(statuscommand::status, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("respawn").withRequirement(CommandSupports::hasFakeplayerForRespawn)).withShortDescription("fakeplayer.command.respawn.description")).withPermission("cactusbot.command.respawn")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name", Entity::isDead)});
        RespawnCommand respawncommand = this.respawnCommand;

        Objects.requireNonNull(this.respawnCommand);
        acommandapicommand[15] = (CommandAPICommand) commandapicommand1.executes(respawncommand::respawn, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("set").withRequirement(CommandSupports::hasFakeplayer)).withShortDescription("fakeplayer.command.set.description")).withPermission("cactusbot.command.set")).withArguments(new Argument[]{CommandSupports.configKey("feature", Feature::hasModifier), CommandSupports.configValue("feature", "option")})).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        SetCommand setcommand = this.setCommand;

        Objects.requireNonNull(this.setCommand);
        acommandapicommand[16] = (CommandAPICommand) commandapicommand1.executes(setcommand::set, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("config").withPermission("cactusbot.command.config")).withShortDescription("fakeplayer.command.config.description");
        CommandAPICommand[] acommandapicommand4 = new CommandAPICommand[2];

        commandapicommand2 = (CommandAPICommand) Commands.command("set").withArguments(new Argument[]{CommandSupports.configKey("feature"), CommandSupports.configValue("feature", "option")});
        ConfigCommand configcommand = this.configCommand;

        Objects.requireNonNull(this.configCommand);
        acommandapicommand4[0] = (CommandAPICommand) commandapicommand2.executesPlayer(configcommand::setConfig);
        commandapicommand2 = Commands.command("list");
        configcommand = this.configCommand;
        Objects.requireNonNull(this.configCommand);
        acommandapicommand4[1] = (CommandAPICommand) commandapicommand2.executesPlayer(configcommand::listConfig);
        commandapicommand1 = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand4);
        ConfigCommand configcommand1 = this.configCommand;

        Objects.requireNonNull(this.configCommand);
        acommandapicommand[17] = (CommandAPICommand) commandapicommand1.executesPlayer(configcommand1::listConfig);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("expme").withPermission("cactusbot.command.expme")).withShortDescription("fakeplayer.command.expme.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        ExpmeCommand expmecommand = this.expmeCommand;

        Objects.requireNonNull(this.expmeCommand);
        acommandapicommand[18] = (CommandAPICommand) commandapicommand1.executesPlayer(expmecommand::expme);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("tp").withAliases(new String[]{"tpto"})).withPermission("cactusbot.command.tp")).withShortDescription("fakeplayer.command.tp.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        TeleportCommand teleportcommand = this.teleportCommand;

        Objects.requireNonNull(this.teleportCommand);
        acommandapicommand[19] = (CommandAPICommand) commandapicommand1.executesPlayer(teleportcommand::tp);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("tphere").withPermission("cactusbot.command.tphere")).withShortDescription("fakeplayer.command.tphere.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        teleportcommand = this.teleportCommand;
        Objects.requireNonNull(this.teleportCommand);
        acommandapicommand[20] = (CommandAPICommand) commandapicommand1.executesPlayer(teleportcommand::tphere);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("tps").withPermission("cactusbot.command.tps")).withShortDescription("fakeplayer.command.tphere.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        teleportcommand = this.teleportCommand;
        Objects.requireNonNull(this.teleportCommand);
        acommandapicommand[21] = (CommandAPICommand) commandapicommand1.executesPlayer(teleportcommand::tps);
        acommandapicommand[22] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("attack").withPermission("cactusbot.command.attack")).withShortDescription("fakeplayer.command.attack.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.ATTACK))).executes(this.actionCommand.action(ActionType.ATTACK, ActionSetting.once()), new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("crit").withPermission("cactusbot.command.crit")).withShortDescription("fakeplayer.command.crit.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        CritCommand critcommand = this.critCommand;

        Objects.requireNonNull(this.critCommand);
        acommandapicommand[23] = (CommandAPICommand) commandapicommand1.executes(critcommand::crit, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("mace").withPermission("cactusbot.command.mace")).withShortDescription("fakeplayer.command.mace.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.literals("state", List.of("on", "off"))});
        MaceCommand macecommand = this.maceCommand;

        Objects.requireNonNull(this.maceCommand);
        acommandapicommand[24] = (CommandAPICommand) commandapicommand1.executes(macecommand::mace, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("action").withPermission("cactusbot.command.action")).withShortDescription("fakeplayer.command.action.description")).withRequirement(CommandSupports::hasFakeplayer)).withArguments(new Argument[]{Commands.text("action"), CommandSupports.fakeplayer("name"), Commands.literals("execute", List.of("execute"))});
        CustomActionCommand customactioncommand = this.customActionCommand;

        Objects.requireNonNull(this.customActionCommand);
        acommandapicommand[25] = (CommandAPICommand) commandapicommand1.executes(customactioncommand::execute, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("record").withPermission("cactusbot.command.record")).withShortDescription("fakeplayer.command.record.description")).withArguments(new Argument[]{Commands.text("recording"), Commands.literals("action", List.of("start", "stop"))});
        RecordCommand recordcommand = this.recordCommand;

        Objects.requireNonNull(this.recordCommand);
        acommandapicommand[26] = (CommandAPICommand) commandapicommand1.executesPlayer(recordcommand::record);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("userec").withPermission("cactusbot.command.userec")).withShortDescription("fakeplayer.command.userec.description")).withRequirement(CommandSupports::hasFakeplayer)).withArguments(new Argument[]{Commands.text("recording"), CommandSupports.fakeplayer("name")});
        UseRecordingCommand userecordingcommand = this.useRecordingCommand;

        Objects.requireNonNull(this.useRecordingCommand);
        acommandapicommand[27] = (CommandAPICommand) commandapicommand1.executes(userecordingcommand::use, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("place").withPermission("cactusbot.command.place")).withShortDescription("fakeplayer.command.place.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.text("range"), Commands.literals("side", List.of("front", "left", "right"))});
        PlaceCommand placecommand = this.placeCommand;

        Objects.requireNonNull(this.placeCommand);
        acommandapicommand[28] = (CommandAPICommand) commandapicommand1.executes(placecommand::place, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("crystal").withPermission("cactusbot.command.crystal")).withShortDescription("fakeplayer.command.crystal.description")).withRequirement(CommandSupports::hasFakeplayer);
        CommandAPICommand[] acommandapicommand5 = new CommandAPICommand[2];

        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("place").withShortDescription("fakeplayer.command.crystal.place.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.text("range")});
        CrystalCommand crystalcommand = this.crystalCommand;

        Objects.requireNonNull(this.crystalCommand);
        acommandapicommand5[0] = (CommandAPICommand) commandapicommand2.executes(crystalcommand::place, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("break").withShortDescription("fakeplayer.command.crystal.break.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.text("range")});
        crystalcommand = this.crystalCommand;
        Objects.requireNonNull(this.crystalCommand);
        acommandapicommand5[1] = (CommandAPICommand) commandapicommand2.executes(crystalcommand::breakCrystal, new ExecutorType[0]);
        acommandapicommand[29] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand5);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("glide").withPermission("cactusbot.command.glide")).withShortDescription("fakeplayer.command.glide.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        GlideCommand glidecommand = this.glideCommand;

        Objects.requireNonNull(this.glideCommand);
        acommandapicommand[30] = (CommandAPICommand) commandapicommand1.executes(glidecommand::glide, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("lava").withPermission("cactusbot.command.lava")).withShortDescription("fakeplayer.command.lava.description")).withRequirement(CommandSupports::hasFakeplayer);
        CommandAPICommand[] acommandapicommand6 = new CommandAPICommand[1];

        commandapicommand2 = (CommandAPICommand) Commands.command("above").withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.text("range")});
        LavaCommand lavacommand = this.lavaCommand;

        Objects.requireNonNull(this.lavaCommand);
        acommandapicommand6[0] = (CommandAPICommand) commandapicommand2.executes(lavacommand::above, new ExecutorType[0]);
        acommandapicommand[31] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand6);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("bucket").withPermission("cactusbot.command.bucket")).withShortDescription("fakeplayer.command.bucket.description")).withRequirement(CommandSupports::hasFakeplayer);
        acommandapicommand6 = new CommandAPICommand[1];
        commandapicommand2 = (CommandAPICommand) Commands.command("pickup").withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.text("range")});
        BucketCommand bucketcommand = this.bucketCommand;

        Objects.requireNonNull(this.bucketCommand);
        acommandapicommand6[0] = (CommandAPICommand) commandapicommand2.executes(bucketcommand::pickup, new ExecutorType[0]);
        acommandapicommand[32] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand6);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("autototem").withPermission("cactusbot.command.autototem")).withShortDescription("fakeplayer.command.autototem.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        AutoTotemCommand autototemcommand = this.autoTotemCommand;

        Objects.requireNonNull(this.autoTotemCommand);
        acommandapicommand[33] = (CommandAPICommand) commandapicommand1.executes(autototemcommand::autototem, new ExecutorType[0]);
        acommandapicommand[34] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("mine").withPermission("cactusbot.command.mine")).withShortDescription("fakeplayer.command.mine.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.MINE))).executes(this.actionCommand.action(ActionType.MINE, ActionSetting.once()), new ExecutorType[0]);
        acommandapicommand[35] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("use").withPermission("cactusbot.command.use")).withShortDescription("fakeplayer.command.use.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.USE))).executes(this.actionCommand.action(ActionType.USE, ActionSetting.once()), new ExecutorType[0]);
        acommandapicommand[36] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("jump").withPermission("cactusbot.command.jump")).withShortDescription("fakeplayer.command.jump.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.JUMP))).executes(this.actionCommand.action(ActionType.JUMP, ActionSetting.once()), new ExecutorType[0]);
        acommandapicommand[37] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("drop").withPermission("cactusbot.command.drop")).withShortDescription("fakeplayer.command.drop.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.DROP_ITEM))).executes(this.actionCommand.action(ActionType.DROP_ITEM, ActionSetting.once()), new ExecutorType[0]);
        acommandapicommand[38] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("dropstack").withPermission("cactusbot.command.dropstack")).withShortDescription("fakeplayer.command.dropstack.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.DROP_STACK))).executes(this.actionCommand.action(ActionType.DROP_STACK, ActionSetting.once()), new ExecutorType[0]);
        acommandapicommand[39] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("dropinv").withPermission("cactusbot.command.dropinv")).withShortDescription("fakeplayer.command.dropinv.description")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(CommandSupports.newActionCommands(ActionType.DROP_INVENTORY))).executes(this.actionCommand.action(ActionType.DROP_INVENTORY, ActionSetting.once()), new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("sneak").withPermission("cactusbot.command.sneak")).withShortDescription("fakeplayer.command.sneak.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        SneakCommand sneakcommand = this.sneakCommand;

        Objects.requireNonNull(this.sneakCommand);
        acommandapicommand[40] = (CommandAPICommand) commandapicommand1.executes(sneakcommand::sneak, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("unsneak").withPermission("cactusbot.command.sneak")).withShortDescription("fakeplayer.command.sneak.description")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        sneakcommand = this.sneakCommand;
        Objects.requireNonNull(this.sneakCommand);
        acommandapicommand[41] = (CommandAPICommand) commandapicommand1.executes(sneakcommand::unsneak, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("sprint").withPermission("cactusbot.command.sprint")).withShortDescription("fakeplayer.command.sprint.description")).withRequirement(CommandSupports::hasFakeplayer)).withArguments(new Argument[]{CommandSupports.fakeplayer("name"), Commands.literals("state", List.of("on", "off"))});
        SprintCommand sprintcommand = this.sprintCommand;

        Objects.requireNonNull(this.sprintCommand);
        acommandapicommand[42] = (CommandAPICommand) commandapicommand1.executes(sprintcommand::sprint, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("look").withPermission("cactusbot.command.look")).withShortDescription("fakeplayer.command.look.description")).withRequirement(CommandSupports::hasFakeplayer);
        CommandAPICommand[] acommandapicommand7 = new CommandAPICommand[]{(CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("north").withShortDescription("fakeplayer.command.look.north.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.look(Direction.NORTH), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("south").withShortDescription("fakeplayer.command.look.south.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.look(Direction.SOUTH), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("west").withShortDescription("fakeplayer.command.look.west.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.look(Direction.WEST), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("east").withShortDescription("fakeplayer.command.look.east.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.look(Direction.EAST), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("up").withShortDescription("fakeplayer.command.look.up.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.look(Direction.UP), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("down").withShortDescription("fakeplayer.command.look.down.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.look(Direction.DOWN), new ExecutorType[0]), null, null, null, null};

        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("at").withShortDescription("fakeplayer.command.look.at.description")).withArguments(new Argument[]{Commands.location("location")})).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        RotationCommand rotationcommand = this.rotationCommand;

        Objects.requireNonNull(this.rotationCommand);
        acommandapicommand7[6] = (CommandAPICommand) commandapicommand2.executes(rotationcommand::lookAt, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("me").withShortDescription("fakeplayer.command.look.me.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        rotationcommand = this.rotationCommand;
        Objects.requireNonNull(this.rotationCommand);
        acommandapicommand7[7] = (CommandAPICommand) commandapicommand2.executesPlayer(rotationcommand::lookMe);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("target").withShortDescription("fakeplayer.command.look.target.description")).withArguments(new Argument[]{new OneEntity("entity")})).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        rotationcommand = this.rotationCommand;
        Objects.requireNonNull(this.rotationCommand);
        acommandapicommand7[8] = (CommandAPICommand) commandapicommand2.executes(rotationcommand::lookEntity, new ExecutorType[0]);
        acommandapicommand7[9] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("entity").withShortDescription("fakeplayer.command.look.entity.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).withSubcommands(CommandSupports.newActionCommands(ActionType.LOOK_AT_NEAREST_ENTITY));
        acommandapicommand[43] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand7);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("turn").withPermission("cactusbot.command.turn")).withShortDescription("fakeplayer.command.turn.description")).withRequirement(CommandSupports::hasFakeplayer);
        acommandapicommand7 = new CommandAPICommand[]{(CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("left").withShortDescription("fakeplayer.command.turn.left.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.turn(-90.0F, 0.0F), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("right").withShortDescription("fakeplayer.command.turn.right.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.turn(90.0F, 0.0F), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("back").withShortDescription("fakeplayer.command.turn.back.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.rotationCommand.turn(180.0F, 0.0F), new ExecutorType[0]), null};
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("to").withShortDescription("fakeplayer.command.turn.to.description")).withArguments(new Argument[]{Commands.rotation("rotation")})).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        rotationcommand = this.rotationCommand;
        Objects.requireNonNull(this.rotationCommand);
        acommandapicommand7[3] = (CommandAPICommand) commandapicommand2.executes(rotationcommand::turnTo, new ExecutorType[0]);
        acommandapicommand[44] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand7);
        acommandapicommand[45] = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("move").withShortDescription("fakeplayer.command.move.description")).withPermission("cactusbot.command.move")).withRequirement(CommandSupports::hasFakeplayer)).withSubcommands(new CommandAPICommand[]{(CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("forward").withShortDescription("fakeplayer.command.move.forward.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.moveCommand.move(1.0F, 0.0F), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("backward").withShortDescription("fakeplayer.command.move.backward.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.moveCommand.move(-1.0F, 0.0F), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("left").withShortDescription("fakeplayer.command.move.left.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.moveCommand.move(0.0F, 1.0F), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("right").withShortDescription("fakeplayer.command.move.right.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")})).executes(this.moveCommand.move(0.0F, -1.0F), new ExecutorType[0])})).executes(this.moveCommand.move(1.0F, 0.0F), new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("ride").withShortDescription("fakeplayer.command.ride.description")).withPermission("cactusbot.command.ride")).withRequirement(CommandSupports::hasFakeplayer);
        acommandapicommand7 = new CommandAPICommand[6];
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("me").withShortDescription("fakeplayer.command.ride.me.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        RideCommand ridecommand = this.rideCommand;

        Objects.requireNonNull(this.rideCommand);
        acommandapicommand7[0] = (CommandAPICommand) commandapicommand2.executesPlayer(ridecommand::rideMe);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("target").withShortDescription("fakeplayer.command.ride.target.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        ridecommand = this.rideCommand;
        Objects.requireNonNull(this.rideCommand);
        acommandapicommand7[1] = (CommandAPICommand) commandapicommand2.executes(ridecommand::rideTarget, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("anything").withShortDescription("fakeplayer.command.ride.anything.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        ridecommand = this.rideCommand;
        Objects.requireNonNull(this.rideCommand);
        acommandapicommand7[2] = (CommandAPICommand) commandapicommand2.executes(ridecommand::rideAnything, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("vehicle").withShortDescription("fakeplayer.command.ride.vehicle.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        ridecommand = this.rideCommand;
        Objects.requireNonNull(this.rideCommand);
        acommandapicommand7[3] = (CommandAPICommand) commandapicommand2.executes(ridecommand::rideVehicle, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("entity").withShortDescription("fakeplayer.command.ride.entity.description")).withArguments(new Argument[]{new OneEntity("entity")})).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        ridecommand = this.rideCommand;
        Objects.requireNonNull(this.rideCommand);
        acommandapicommand7[4] = (CommandAPICommand) commandapicommand2.executes(ridecommand::rideEntity, new ExecutorType[0]);
        commandapicommand2 = (CommandAPICommand) ((CommandAPICommand) Commands.command("stop").withShortDescription("fakeplayer.command.ride.stop.description")).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        ridecommand = this.rideCommand;
        Objects.requireNonNull(this.rideCommand);
        acommandapicommand7[5] = (CommandAPICommand) commandapicommand2.executes(ridecommand::stopRiding, new ExecutorType[0]);
        acommandapicommand[46] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand7);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("swap").withShortDescription("fakeplayer.command.swap.description")).withPermission("cactusbot.command.swap")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        SwapCommand swapcommand = this.swapCommand;

        Objects.requireNonNull(this.swapCommand);
        acommandapicommand[47] = (CommandAPICommand) commandapicommand1.executes(swapcommand::swap, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("sleep").withShortDescription("fakeplayer.command.sleep.description")).withPermission("cactusbot.command.sleep")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name", (px) -> {
                    return !px.isSleeping();
                })});
        SleepCommand sleepcommand = this.sleepCommand;

        Objects.requireNonNull(this.sleepCommand);
        acommandapicommand[48] = (CommandAPICommand) commandapicommand1.executes(sleepcommand::sleep, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("wakeup").withShortDescription("fakeplayer.command.wakeup.description")).withPermission("cactusbot.command.wakeup")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name", LivingEntity::isSleeping)});
        sleepcommand = this.sleepCommand;
        Objects.requireNonNull(this.sleepCommand);
        acommandapicommand[49] = (CommandAPICommand) commandapicommand1.executes(sleepcommand::wakeup, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("stop").withShortDescription("fakeplayer.command.stop.description")).withPermission("cactusbot.command.stop")).withRequirement(CommandSupports::hasFakeplayer)).withOptionalArguments(new Argument[]{CommandSupports.fakeplayer("name")});
        StopCommand stopcommand = this.stopCommand;

        Objects.requireNonNull(this.stopCommand);
        acommandapicommand[50] = (CommandAPICommand) commandapicommand1.executes(stopcommand::stop, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("cmd").withShortDescription("fakeplayer.command.cmd.description")).withRequirement(CommandSupports::isCmdAvailable)).withArguments(new Argument[]{CommandSupports.fakeplayer("name"), new FakePlayerCommandArgument("command")});
        CmdCommand cmdcommand = this.cmdCommand;

        Objects.requireNonNull(this.cmdCommand);
        acommandapicommand[51] = (CommandAPICommand) commandapicommand1.executes(cmdcommand::cmd, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("killall").withShortDescription("fakeplayer.command.killall.description")).withPermission(CommandPermission.OP);
        KillallCommand killallcommand = this.killallCommand;

        Objects.requireNonNull(this.killallCommand);
        acommandapicommand[52] = (CommandAPICommand) commandapicommand1.executes(killallcommand::killall, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("reload").withShortDescription("fakeplayer.command.reload.description")).withPermission(CommandPermission.OP);
        ReloadCommand reloadcommand = this.reloadCommand;

        Objects.requireNonNull(this.reloadCommand);
        acommandapicommand[53] = (CommandAPICommand) commandapicommand1.executes(reloadcommand::reload, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("reload-translation").withShortDescription("fakeplayer.command.reload-translation.description")).withPermission(CommandPermission.OP);
        reloadcommand = this.reloadCommand;
        Objects.requireNonNull(this.reloadCommand);
        acommandapicommand[54] = (CommandAPICommand) commandapicommand1.executes(reloadcommand::reloadTranslation, new ExecutorType[0]);
        commandapicommand1 = (CommandAPICommand) ((CommandAPICommand) Commands.command("debug").withPermission(CommandPermission.OP)).withRequirement((senderx) -> {
            return this.config.isDebug();
        });
        CommandAPICommand[] acommandapicommand8 = new CommandAPICommand[1];

        commandapicommand2 = (CommandAPICommand) Commands.command("send-plugin-message").withArguments(new Argument[]{Commands.player("player"), Commands.text("channel"), Commands.text("message")});
        DebugCommand debugcommand = this.debugCommand;

        Objects.requireNonNull(this.debugCommand);
        acommandapicommand8[0] = (CommandAPICommand) commandapicommand2.executes(debugcommand::sendPluginMessage, new ExecutorType[0]);
        acommandapicommand[55] = (CommandAPICommand) commandapicommand1.withSubcommands(acommandapicommand8);
        CommandAPICommand root = (CommandAPICommand) commandapicommand.withSubcommands(acommandapicommand);

        HelpCommand.generateHelpCommand(root, true);
        root.register();
    }
}
