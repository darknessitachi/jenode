package com.zving.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE,
		java.lang.annotation.ElementType.METHOD })
public @interface Alias {
	public abstract String value() default "";

	public abstract boolean alone() default false;

	public abstract boolean url() default false;
}