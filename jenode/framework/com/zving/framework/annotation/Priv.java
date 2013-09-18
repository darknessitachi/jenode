package com.zving.framework.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface Priv
{
  public abstract boolean login();

  public abstract LoginType loginType();

  public abstract String userType();

  public abstract String value();

  public static enum LoginType
  {
    User, Member;
  }
}