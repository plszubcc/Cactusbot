package io.github.hello09x.fakeplayer.api.spi;

import lombok.Generated;

public class ActionSetting implements Cloneable {

    public final int maximum;
    public int remains;
    public int interval;
    public int wait;

    public ActionSetting(int maximum, int interval) {
        this(maximum, interval, 0);
    }

    public ActionSetting(int maximum, int interval, int wait) {
        super();
        this.maximum = maximum;
        this.remains = maximum;
        this.interval = interval;
        this.wait = wait;
    }

    public static ActionSetting once() {
        return new ActionSetting(1, 0);
    }

    public static ActionSetting stop() {
        return new ActionSetting(0, 0);
    }

    public static ActionSetting interval(int interval) {
        return new ActionSetting(-1, interval);
    }

    public static ActionSetting continuous() {
        return new ActionSetting(-1, 0);
    }

    public ActionSetting clone() {
        try {
            return (ActionSetting) super.clone();
        } catch (CloneNotSupportedException clonenotsupportedexception) {
            throw new Error(clonenotsupportedexception);
        }
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ActionSetting)) {
            return false;
        } else {
            ActionSetting other = (ActionSetting) o;

            return !other.canEqual(this) ? false : (this.maximum != other.maximum ? false : (this.remains != other.remains ? false : (this.interval != other.interval ? false : this.wait == other.wait)));
        }
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ActionSetting;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;

        result = result * 59 + this.maximum;
        result = result * 59 + this.remains;
        result = result * 59 + this.interval;
        result = result * 59 + this.wait;
        return result;
    }
}
