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
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Release other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$tagName = this.getTagName();
         Object other$tagName = other.getTagName();
         if (this$tagName == null ? other$tagName == null : this$tagName.equals(other$tagName)) {
            Object this$body = this.getBody();
            Object other$body = other.getBody();
            return this$body == null ? other$body == null : this$body.equals(other$body);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof Release;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $tagName = this.getTagName();
      result = result * 59 + ($tagName == null ? 43 : $tagName.hashCode());
      Object $body = this.getBody();
      return result * 59 + ($body == null ? 43 : $body.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "Release(tagName=" + this.getTagName() + ", body=" + this.getBody() + ")";
   }

   @Generated
   public Release(String tagName, String body) {
      this.tagName = tagName;
      this.body = body;
   }

   @Generated
   public Release() {
   }
}
