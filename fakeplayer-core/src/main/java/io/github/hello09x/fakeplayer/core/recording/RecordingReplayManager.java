package io.github.hello09x.fakeplayer.core.recording;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.combat.CombatTargetResolver;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class RecordingReplayManager implements Listener {

    private final RecordingStorage storage;
    private final FakeplayerManager fakeplayerManager;
    private final CombatTargetResolver targetResolver;
    private final NMSBridge bridge;
    private final Map<UUID, RecordingReplayManager.ReplaySession> sessions = new ConcurrentHashMap();

    @Inject
    public RecordingReplayManager(RecordingStorage storage, FakeplayerManager fakeplayerManager, CombatTargetResolver targetResolver, NMSBridge bridge) {
        super();
        this.storage = storage;
        this.fakeplayerManager = fakeplayerManager;
        this.targetResolver = targetResolver;
        this.bridge = bridge;
    }

    public boolean useRecording(@NotNull String name, @NotNull Player bot) {
        RecordingData data = this.storage.load(name);

        if (data != null && data.frames() != null && !data.frames().isEmpty()) {
            this.stop(bot);
            RecordingReplayManager.ReplaySession session = new RecordingReplayManager.ReplaySession(name, bot, data);

            if (data.frames().stream().anyMatch((framex) -> {
                return framex.heldItem() != null && framex.heldItem().equalsIgnoreCase("mace");
            })) {
                this.fakeplayerManager.dispatchCommand(bot, "cb mace " + bot.getName() + " on");
            } else {
                this.fakeplayerManager.dispatchCommand(bot, "cb mace " + bot.getName() + " off");
            }

            session.task = Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), () -> {
                this.tick(session);
            }, 0L, 1L);
            this.sessions.put(bot.getUniqueId(), session);
            return true;
        } else {
            return false;
        }
    }

    public void stop(@NotNull Player bot) {
        RecordingReplayManager.ReplaySession session = (RecordingReplayManager.ReplaySession) this.sessions.remove(bot.getUniqueId());

        if (session != null && session.task != null) {
            session.task.cancel();
        }

    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        this.stop(event.getPlayer());
    }

    private void tick(RecordingReplayManager.@NotNull ReplaySession session) {
        Player bot = session.bot;

        if (bot.isOnline() && !this.fakeplayerManager.isNotFake(bot)) {
            RecordedFrame sample = this.nextSample(session);

            if (sample != null) {
                bot.setSprinting(sample.sprint());
                bot.setSneaking(sample.sneak());
                NMSServerPlayer handle = this.bridge.fromPlayer(bot);

                if (sample.heldItem() != null && !sample.heldItem().equals(session.lastHeldItem) && session.tick - session.lastHoldTick >= 2) {
                    FakeplayerManager fakeplayermanager = this.fakeplayerManager;
                    String s = sample.heldItem();

                    fakeplayermanager.dispatchCommand(bot, "cb hold " + s + " " + bot.getName());
                    session.lastHeldItem = sample.heldItem();
                    session.lastHoldTick = session.tick;
                }

                Player currentTarget = this.targetResolver.resolve(bot);

                if (currentTarget != null && sample.targetAngle() != null && sample.targetPitchAngle() != null) {
                    Vector botEye = bot.getEyeLocation().toVector();
                    Vector targetEye = currentTarget.getEyeLocation().toVector();
                    Vector delta = targetEye.clone().subtract(botEye);
                    float desiredYaw = (float) Math.toDegrees(Math.atan2(-delta.getX(), delta.getZ())) + sample.targetAngle();
                    double horizontal = Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ());
                    float desiredPitch = (float) Math.toDegrees(-Math.atan2(delta.getY(), horizontal)) + sample.targetPitchAngle();

                    desiredYaw += this.randomFloat(-1.5F, 1.5F);
                    desiredPitch += this.randomFloat(-1.2F, 1.2F);
                    handle.setYRot(this.lerpAngle(handle.getYRot(), desiredYaw, 0.35F));
                    handle.setXRot(this.lerp(handle.getXRot(), desiredPitch, 0.35F));
                } else {
                    handle.setYRot(this.lerpAngle(handle.getYRot(), sample.yaw() + this.randomFloat(-1.0F, 1.0F), 0.2F));
                    handle.setXRot(this.lerp(handle.getXRot(), sample.pitch() + this.randomFloat(-0.8F, 0.8F), 0.2F));
                }

                handle.setZza(this.clamp(sample.forwardInput() + this.randomFloat(-0.06F, 0.06F)));
                handle.setXxa(this.clamp(sample.strafeInput() + this.randomFloat(-0.05F, 0.05F)));
                if (sample.jump() && handle.onGround() && session.tick - session.lastJumpTick >= 8) {
                    handle.jumpFromGround();
                    session.lastJumpTick = session.tick;
                }

                if (sample.glide() && session.tick - session.lastGlideTick >= 10) {
                    this.fakeplayerManager.dispatchCommand(bot, "cb glide " + bot.getName());
                    session.lastGlideTick = session.tick;
                }

                if (sample.useItem() && session.tick - session.lastUseTick >= 4) {
                    this.fakeplayerManager.dispatchCommand(bot, this.useCommandFor(bot, sample));
                    session.lastUseTick = session.tick;
                }

                if (sample.attack() && currentTarget != null && this.targetResolver.distance(bot, currentTarget) <= 4.5D && session.tick - session.lastAttackTick >= 8) {
                    this.fakeplayerManager.dispatchCommand(bot, "cb attack once " + bot.getName());
                    session.lastAttackTick = session.tick;
                }

                ++session.tick;
            }
        } else {
            this.stop(bot);
        }
    }

    private @Nullable RecordedFrame nextSample(RecordingReplayManager.@NotNull ReplaySession session) {
        List<RecordedFrame> frames = session.data.frames();

        if (frames.isEmpty()) {
            return null;
        } else {
            RecordedFrame sample = (RecordedFrame) frames.get(session.frameIndex);

            session.frameIndex = (session.frameIndex + 1) % frames.size();
            return sample;
        }
    }

    private float clamp(float value) {
        return Math.max(-1.0F, Math.min(1.0F, value));
    }

    private float lerp(float current, float target, float amount) {
        return current + (target - current) * amount;
    }

    private float lerpAngle(float current, float target, float amount) {
        return current + this.wrapAngle(target - current) * amount;
    }

    private float wrapAngle(float value) {
        float wrapped;

        for (wrapped = value; wrapped <= -180.0F; wrapped += 360.0F) {
            ;
        }

        while (wrapped > 180.0F) {
            wrapped -= 360.0F;
        }

        return wrapped;
    }

    private float randomFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble((double) min, (double) max);
    }

    private @NotNull String useCommandFor(@NotNull Player bot, @NotNull RecordedFrame sample) {
        String heldItem = sample.heldItem();

        if (heldItem == null) {
            return "cb use once " + bot.getName();
        } else {
            String s;

            switch (heldItem.toLowerCase(Locale.ROOT)) {
                case "obsidian":
                    s = "cb place " + bot.getName() + " 3.0 front";
                    break;
                case "end_crystal":
                    s = "cb crystal place " + bot.getName() + " 4.0";
                    break;
                default:
                    s = "cb use once " + bot.getName();
            }

            return s;
        }
    }

    private static final class ReplaySession {

        private final String name;
        private final Player bot;
        private final RecordingData data;
        private BukkitTask task;
        private int tick;
        private int frameIndex;
        private int lastAttackTick = -536870912;
        private int lastJumpTick = -536870912;
        private int lastUseTick = -536870912;
        private int lastGlideTick = -536870912;
        private int lastHoldTick = -536870912;
        private String lastHeldItem;

        private ReplaySession(String name, Player bot, RecordingData data) {
            super();
            this.name = name;
            this.bot = bot;
            this.data = data;
        }
    }
}
