package com.zving.framework.core.method;

import com.zving.framework.core.exception.UIMethodInvokeException;
import java.lang.annotation.Annotation;

public class OuterClassLocator
  implements IMethodLocator
{
  protected Class<? extends UIMethod> clazz;

  public OuterClassLocator(Class<? extends UIMethod> clazz)
  {
    this.clazz = clazz;
  }

  public Object execute(Object[] args) {
    try {
      UIMethod instance = (UIMethod)this.clazz.newInstance();
      UIMethodBinder.bind(instance, args);
      instance.execute();
    } catch (Exception e) {
      throw new UIMethodInvokeException(e);
    }
    return null;
  }

  public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return this.clazz.isAnnotationPresent(annotationClass);
  }

  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return this.clazz.getAnnotation(annotationClass);
  }

  public String getName() {
    return this.clazz.getName();
  }
}