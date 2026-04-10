package com.google.inject;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RestrictedBindingSource {

    String explanation();

    Class<? extends Annotation>[] permits();

    String exemptModules() default "";

    RestrictedBindingSource.RestrictionLevel restrictionLevel() default RestrictedBindingSource.RestrictionLevel.ERROR;

    public static enum RestrictionLevel {

        WARNING, ERROR;

        private RestrictionLevel() {}
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE})
    public @interface Permit {
    }
}
