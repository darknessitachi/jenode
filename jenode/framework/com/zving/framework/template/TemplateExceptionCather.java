package com.zving.framework.template;

import com.zving.framework.core.IExceptionCatcher;
import com.zving.framework.template.exception.TemplateException;
import com.zving.framework.utility.LogUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateExceptionCather implements IExceptionCatcher {
	public String getID() {
		return "com.zving.framework.template.TemplateExceptionCather";
	}

	public String getName() {
		return "Default Template Exception Catcher";
	}

	public Class<?>[] getTargetExceptionClass() {
		return new Class[] { TemplateException.class };
	}

	public void doCatch(RuntimeException e, HttpServletRequest request,
			HttpServletResponse response) {
		LogUtil.error("TemplateException found in " + request.getRequestURL());
		throw e;
	}
}