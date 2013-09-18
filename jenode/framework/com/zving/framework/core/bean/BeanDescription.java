package com.zving.framework.core.bean;

import com.zving.framework.collection.Mapx;
import com.zving.framework.expression.Constants;
import com.zving.framework.utility.LogUtil;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BeanDescription {
	Class<?> beanClass;
	Mapx<String, BeanProperty> propertyMap;
	boolean initFlag;

	public Class<?> getBeanClass() {
		return this.beanClass;
	}

	BeanDescription(Class<?> clazz) {
		this.beanClass = clazz;
	}

	public Mapx<String, BeanProperty> getPropertyMap() {
		checkInitialized();
		return this.propertyMap;
	}

	void checkInitialized() {
		if (!this.initFlag)
			synchronized (this) {
				if (!this.initFlag) {
					initialize();
					this.initFlag = true;
				}
			}
	}

	void initialize() {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.beanClass);
			this.propertyMap = new Mapx();
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			Method writeMethod;
			for (int i = 0; (pds != null) && (i < pds.length); i++) {
				PropertyDescriptor pd = pds[i];
				Method readMethod = BeanManager.getPublicMethod(pd
						.getReadMethod());
				writeMethod = BeanManager.getPublicMethod(pd.getWriteMethod());
				if ((readMethod != null) || (writeMethod != null)) {
					BeanProperty property = new BeanProperty(readMethod,
							writeMethod);
					this.propertyMap.put(pd.getName(), property);
				}
			}
			Field[] fields = this.beanClass.getFields();
			for (int pd = 0; pd < fields.length; pd++) {
				Field f = fields[pd];
				if (Modifier.isPublic(f.getModifiers())) {
					if (!Modifier.isStatic(f.getModifiers())) {
						BeanProperty property = new BeanProperty(f);
						if (!this.propertyMap.containsKey(property.getName()))
							this.propertyMap.put(f.getName(), property);
					}
				}
			}
		} catch (IntrospectionException exc) {
			LogUtil.warn(Constants.EXCEPTION_GETTING_BEANINFO + ",Bean="
					+ this.beanClass.getName() + ":" + exc.getMessage());
		}
	}

	public BeanProperty getProperty(String name) {
		checkInitialized();
		BeanProperty bip = (BeanProperty) this.propertyMap.get(name);
		if (bip == null) {
			bip = (BeanProperty) this.propertyMap.get(name.substring(0, 1)
					.toLowerCase() + name.substring(1));
		}
		return bip;
	}
}