package com.zving.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD })
public @interface Verify {
	public abstract boolean ignoreAll() default false;

	public abstract String ignoredKeys() default "";

	public abstract String[] value() default "";
}