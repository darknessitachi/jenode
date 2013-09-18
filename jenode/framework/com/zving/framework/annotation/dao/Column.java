package com.zving.framework.annotation.dao;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Column
{
  public abstract String name();

  public abstract int type();

  public abstract int length();

  public abstract int precision();

  public abstract boolean mandatory();

  public abstract boolean pk();
}