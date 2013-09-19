package com.zving.framework.core.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.zving.framework.Current;
import com.zving.framework.UIFacade;
import com.zving.framework.core.Dispatcher;
import com.zving.framework.core.exception.UIMethodException;
import com.zving.framework.core.exception.UIMethodInvokeException;

public class FacadeMemberMethodLocator implements IMethodLocator {
	private Method m;

	public FacadeMemberMethodLocator(Method m) {
		this.m = m;
		if (!UIFacade.class.isAssignableFrom(m.getDeclaringClass())) {
			throw new UIMethodException("Method " + m.getName()
					+ " 's declaring class " + m.getDeclaringClass().getName()
					+ " not inherit from UIFacade");
		}
		if (Modifier.isStatic(m.getModifiers())) {
			throw new UIMethodException("Method " + m.getName()
					+ " in declaring class " + m.getDeclaringClass().getName()
					+ " has modifier 'static'");
		}
		if (!Modifier.isPublic(m.getModifiers()))
			throw new UIMethodException("Method " + m.getName()
					+ " in declaring class " + m.getDeclaringClass().getName()
					+ " should has modifier 'public'");
	}

	public Object execute(Object[] args) {
		UIFacade ui = createFacadeInstance();
		try {
			return this.m.invoke(ui, UIMethodBinder.convertArg(this.m, args));
		} catch (IllegalArgumentException e) {
			throw new UIMethodInvokeException(e);
		} catch (IllegalAccessException e) {
			throw new UIMethodInvokeException(e);
		} catch (InvocationTargetException e) {
			if ((e.getCause() != null)
					&& ((e.getCause() instanceof Dispatcher.DispatchException))) {
				throw ((Dispatcher.DispatchException) e.getCause());
			}
			throw new UIMethodInvokeException(e);
		}
	}

	private UIFacade createFacadeInstance() {
		Class c = null;
		c = this.m.getDeclaringClass();
		if ((Current.getUIFacade() != null)
				&& (Current.getUIFacade().getClass() == c))
			return Current.getUIFacade();
		try {
			return (UIFacade) c.newInstance();
		} catch (Exception e) {
			throw new UIMethodInvokeException(e);
		}
	}

	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationClass) {
		return this.m.isAnnotationPresent(annotationClass);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return this.m.getAnnotation(annotationClass);
	}

	public String getName() {
		return this.m.getDeclaringClass().getName() + "." + this.m.getName();
	}
}