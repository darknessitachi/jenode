package com.zving.framework.core.method;

import com.zving.framework.Current;
import com.zving.framework.UIFacade;
import com.zving.framework.core.exception.UIMethodInvokeException;
import java.lang.reflect.Constructor;

public class FacadeInnerClassLocator extends OuterClassLocator
{
  public FacadeInnerClassLocator(Class<? extends UIMethod> clazz)
  {
    super(clazz);
  }

  public Object execute(Object[] args) {
    UIFacade ui = createFacadeInstance();
    try {
      UIMethod instance = (UIMethod)this.clazz.getConstructors()[0].newInstance(new Object[] { ui });
      UIMethodBinder.bind(instance, args);
      instance.execute();
    } catch (Exception e) {
      throw new UIMethodInvokeException(e);
    }
    return null;
  }

  private UIFacade createFacadeInstance() {
    Class c = null;
    c = this.clazz.getDeclaringClass();
    if ((Current.getUIFacade() != null) && (Current.getUIFacade().getClass() == c))
      return Current.getUIFacade();
    try
    {
      return (UIFacade)c.newInstance();
    } catch (Exception e) {
      throw new UIMethodInvokeException(e);
    }
  }
}