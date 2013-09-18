package com.zving.framework.core.bean;

import com.zving.framework.core.castor.CastorService;
import com.zving.framework.core.exception.BeanException;
import com.zving.framework.core.exception.BeanGetPropertyException;
import com.zving.framework.core.exception.BeanSetPropertyException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BeanProperty
{
  Method readMethod;
  Method writeMethod;
  String name;
  Field field;

  public Object read(Object bean)
  {
    try
    {
      if (this.field != null)
        return this.field.get(bean);
      if (this.readMethod != null)
        return this.readMethod.invoke(bean, new Object[0]);
    }
    catch (Exception e) {
      throw new BeanGetPropertyException(e);
    }
    throw new BeanException("Bean " + bean.getClass().getName() + " 's property [" + this.name + "] can't be read!");
  }

  public void write(Object bean, Object value) {
    try {
      if (this.field != null) {
        value = CastorService.toType(value, this.field.getType());
        this.field.set(bean, value);
      } else if (this.writeMethod != null) {
        value = CastorService.toType(value, this.writeMethod.getParameterTypes()[0]);
        this.writeMethod.invoke(bean, new Object[] { value });
      } else {
        throw new BeanException("Bean " + bean.getClass().getName() + " 's property [" + this.name + "] can't be be write!");
      }
    } catch (Exception e) {
      throw new BeanSetPropertyException(e);
    }
  }

  public BeanProperty(Method pReadMethod, Method pWriteMethod) {
    this.readMethod = pReadMethod;
    this.writeMethod = pWriteMethod;
    if (this.readMethod != null) {
      this.name = this.readMethod.getName();
      if (this.name.startsWith("get")) {
        this.name = this.name.substring(3);
      }
      if (this.name.startsWith("is"))
        this.name = this.name.substring(2);
    }
    else if (this.writeMethod != null) {
      this.name = this.writeMethod.getName();
      if (this.name.startsWith("set")) {
        this.name = this.name.substring(3);
      }
    }
    afterNameSet();
  }

  public Class<?> getPropertyType() {
    if (this.field != null)
      return this.field.getType();
    if (this.writeMethod != null)
      return this.writeMethod.getParameterTypes()[0];
    if (this.readMethod != null) {
      return this.readMethod.getReturnType();
    }
    throw new BeanException("Bean's property [" + this.name + "] not initalized!");
  }

  public BeanProperty(Field field) {
    this.field = field;
    field.setAccessible(true);
    this.name = field.getName();
    afterNameSet();
  }

  private void afterNameSet() {
    if ((Character.isUpperCase(this.name.charAt(0))) && (!Character.isLowerCase(this.name.charAt(1)))) {
      return;
    }
    this.name = (this.name.substring(0, 1).toLowerCase() + this.name.substring(1));
  }

  public String getName() {
    return this.name;
  }

  public boolean canRead() {
    return (this.field != null) || (this.readMethod != null);
  }

  public boolean canWrite() {
    return (this.field != null) || (this.writeMethod != null);
  }
}