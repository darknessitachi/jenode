package com.zving.framework.annotation.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface Column {
	
	public abstract String name() default "";

	public abstract int type() default 0;

	public abstract int length() default 0;

	public abstract int precision() default 0;

	public abstract boolean mandatory() default false;

	public abstract boolean pk() default false;
}