package io.github.hello09x.devtools.core.version;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class Version implements Comparable<Version> {

    private static final Map<String, Integer> STAGES = Map.of("a", 1000, "alpha", 1000, "b", 2000, "beta", 2000, "rc", 3000);
    private int major;
    private int minor;
    private int patch;
    private int stage;

    public Version(int major, int minor, int patch) {
        super();
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.stage = 2147483647;
    }

    public static Version parse(@NotNull String s) throws InvalidVersionException {
        try {
            String[] components = s.split("[.-]");

            if (components.length == 3) {
                return new Version(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2]));
            }

            if (components.length == 5) {
                int major = Integer.parseInt(components[0]);
                int minor = Integer.parseInt(components[1]);
                int patch = Integer.parseInt(components[2]);
                int stage = (Integer) Version.STAGES.get(components[3]) + Integer.parseInt(components[4]);

                return new Version(major, minor, patch, stage);
            }
        } catch (Exception exception) {
            throw new InvalidVersionException();
        }

        throw new InvalidVersionException();
    }

    public int compareTo(@NotNull Version other) {
        return this == other ? 0 : (this.major > other.major ? 1 : (this.major < other.major ? -1 : (this.minor > other.major ? 1 : (this.minor < other.minor ? -1 : (this.patch > other.patch ? 1 : (this.patch < other.patch ? -1 : Integer.compare(this.stage, other.stage)))))));
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    public int getStage() {
        return this.stage;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Version)) {
            return false;
        } else {
            Version other = (Version) o;

            return !other.canEqual(this) ? false : (this.getMajor() != other.getMajor() ? false : (this.getMinor() != other.getMinor() ? false : (this.getPatch() != other.getPatch() ? false : this.getStage() == other.getStage())));
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Version;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;

        result = result * 59 + this.getMajor();
        result = result * 59 + this.getMinor();
        result = result * 59 + this.getPatch();
        result = result * 59 + this.getStage();
        return result;
    }

    public String toString() {
        int i = this.getMajor();

        return "Version(major=" + i + ", minor=" + this.getMinor() + ", patch=" + this.getPatch() + ", stage=" + this.getStage() + ")";
    }

    public Version(int major, int minor, int patch, int stage) {
        super();
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.stage = stage;
    }
}
