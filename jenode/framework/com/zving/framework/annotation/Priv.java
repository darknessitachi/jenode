package com.zving.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.TYPE })
public @interface Priv {
	public abstract boolean login() default true;

	public abstract LoginType loginType() default LoginType.User;

	public abstract String userType() default "";

	public abstract String value() default "";

	public static enum LoginType {
		User, Member;
	}
}