package io.github.hello09x.fakeplayer.core.util.update;

import com.google.gson.annotations.SerializedName;
import lombok.Generated;

public class Release {

    @SerializedName("tag_name")
    private String tagName;
    private String body;

    @Generated
    public String getTagName() {
        return this.tagName;
    }

    @Generated
    public String getBody() {
        return this.body;
    }

    @Generated
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Generated
    public void setBody(String body) {
        this.body = body;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Release)) {
            return false;
        } else {
            Release other = (Release) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$tagName = this.getTagName();
                Object other$tagName = other.getTagName();

                if (this$tagName == null) {
                    if (other$tagName != null) {
                        return false;
                    }
                } else if (!this$tagName.equals(other$tagName)) {
                    return false;
                }

                Object this$body = this.getBody();
                Object other$body = other.getBody();

                if (this$body == null) {
                    if (other$body != null) {
                        return false;
                    }
                } else if (!this$body.equals(other$body)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof Release;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $tagName = this.getTagName();

        result = result * 59 + ($tagName == null ? 43 : $tagName.hashCode());
        Object $body = this.getBody();

        result = result * 59 + ($body == null ? 43 : $body.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String s = this.getTagName();

        return "Release(tagName=" + s + ", body=" + this.getBody() + ")";
    }

    @Generated
    public Release(String tagName, String body) {
        super();
        this.tagName = tagName;
        this.body = body;
    }

    @Generated
    public Release() {
        super();
    }
}
