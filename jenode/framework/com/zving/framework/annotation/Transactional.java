package com.zving.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE,
		java.lang.annotation.ElementType.METHOD })
public @interface Transactional {
	public abstract Isolation isolation() default Isolation.DEFAULT;

	public abstract Propagation propagation() default Propagation.DEFAULT;

	public static enum Isolation {
		DEFAULT,

		READ_UNCOMMITED,

		READ_COMMITED,

		REPEATABLE_READ,

		SERIALIZABLE;
	}

	public static enum Propagation {
		DEFAULT,

		NOTSUPPORT,

		ALONE,

		SAVEPOINT;
	}
}