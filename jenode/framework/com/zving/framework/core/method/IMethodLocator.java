package com.zving.framework.core.method;

import java.lang.annotation.Annotation;

public abstract interface IMethodLocator
{
  public abstract Object execute(Object[] paramArrayOfObject);

  public abstract boolean isAnnotationPresent(Class<? extends Annotation> paramClass);

  public abstract <T extends Annotation> T getAnnotation(Class<T> paramClass);

  public abstract String getName();
}