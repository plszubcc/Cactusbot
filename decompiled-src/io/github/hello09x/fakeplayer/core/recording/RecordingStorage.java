package io.github.hello09x.fakeplayer.core.recording;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class RecordingStorage {

    private static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9_-]+$");

    public RecordingStorage() {
        super();
    }

    public boolean isValidName(@Nullable String name) {
        return name != null && RecordingStorage.VALID_NAME.matcher(name).matches();
    }

    public void save(@NotNull RecordingData data) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("name", data.name());
        yaml.set("source-player", data.sourcePlayer());
        yaml.set("recorded-at", data.recordedAt());
        yaml.set("sample-count", data.sampleCount());
        ArrayList<Map<String, Object>> frames = new ArrayList(data.frames().size());

        for (RecordedFrame frame : data.frames()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap();

            map.put("tick", frame.tick());
            map.put("x", frame.x());
            map.put("y", frame.y());
            map.put("z", frame.z());
            map.put("yaw", frame.yaw());
            map.put("pitch", frame.pitch());
            map.put("forward", frame.forwardInput());
            map.put("strafe", frame.strafeInput());
            map.put("held-item", frame.heldItem());
            map.put("attack", frame.attack());
            map.put("use", frame.useItem());
            map.put("jump", frame.jump());
            map.put("glide", frame.glide());
            map.put("sprint", frame.sprint());
            map.put("sneak", frame.sneak());
            map.put("target-distance", frame.targetDistance());
            map.put("target-angle", frame.targetAngle());
            map.put("target-pitch-angle", frame.targetPitchAngle());
            frames.add(map);
        }

        yaml.set("frames", frames);
        yaml.save(this.fileOf(data.name()));
    }

    public @Nullable RecordingData load(@NotNull String name) {
        File file = this.fileOf(name);

        if (!file.isFile()) {
            return null;
        } else {
            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                List<Map<?, ?>> frameMaps = yaml.getMapList("frames");
                ArrayList<RecordedFrame> frames = new ArrayList(frameMaps.size());

                for (Map<?, ?> map : frameMaps) {
                    frames.add(new RecordedFrame(this.asInt(map.get("tick")), this.asDouble(map.get("x")), this.asDouble(map.get("y")), this.asDouble(map.get("z")), (float) this.asDouble(map.get("yaw")), (float) this.asDouble(map.get("pitch")), (float) this.asDouble(map.get("forward")), (float) this.asDouble(map.get("strafe")), this.asString(map.get("held-item")), this.asBoolean(map.get("attack")), this.asBoolean(map.get("use")), this.asBoolean(map.get("jump")), this.asBoolean(map.get("glide")), this.asBoolean(map.get("sprint")), this.asBoolean(map.get("sneak")), this.asNullableDouble(map.get("target-distance")), this.asNullableFloat(map.get("target-angle")), this.asNullableFloat(map.get("target-pitch-angle"))));
                }

                return new RecordingData(yaml.getString("name", name), yaml.getString("source-player", ""), yaml.getLong("recorded-at", System.currentTimeMillis()), yaml.getInt("sample-count", frames.size()), List.copyOf(frames));
            } catch (Exception exception) {
                return null;
            }
        }
    }

    public @NotNull List<String> listNames() {
        File folder = this.folder();
        File[] files = folder.listFiles((dirx, namex) -> {
            return namex.toLowerCase(Locale.ROOT).endsWith(".yml");
        });

        if (files != null && files.length != 0) {
            ArrayList<String> names = new ArrayList(files.length);

            for (File file : files) {
                String name = file.getName();

                if (name.endsWith(".yml")) {
                    names.add(name.substring(0, name.length() - 4));
                }
            }

            names.sort(String::compareToIgnoreCase);
            return List.copyOf(names);
        } else {
            return List.of();
        }
    }

    private @NotNull File fileOf(@NotNull String name) {
        return new File(this.folder(), name + ".yml");
    }

    private @NotNull File folder() {
        File folder = new File(Main.getInstance().getDataFolder(), "recordings");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    private int asInt(@Nullable Object value) {
        int i;

        if (value instanceof Number number) {
            i = number.intValue();
        } else {
            i = 0;
        }

        return i;
    }

    private double asDouble(@Nullable Object value) {
        double d0;

        if (value instanceof Number number) {
            d0 = number.doubleValue();
        } else {
            d0 = 0.0D;
        }

        return d0;
    }

    private @Nullable Double asNullableDouble(@Nullable Object value) {
        Double odouble;

        if (value instanceof Number number) {
            odouble = number.doubleValue();
        } else {
            odouble = null;
        }

        return odouble;
    }

    private @Nullable Float asNullableFloat(@Nullable Object value) {
        Float ofloat;

        if (value instanceof Number number) {
            ofloat = number.floatValue();
        } else {
            ofloat = null;
        }

        return ofloat;
    }

    private boolean asBoolean(@Nullable Object value) {
        boolean flag;

        if (value instanceof Boolean bool) {
            if (bool) {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    private @Nullable String asString(@Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            String text = value.toString().trim();

            return text.isEmpty() ? null : text;
        }
    }
}
