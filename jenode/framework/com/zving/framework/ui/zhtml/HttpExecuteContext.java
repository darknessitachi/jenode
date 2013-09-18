package com.zving.framework.ui.zhtml;

import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.ITemplateManager;
import com.zving.framework.template.ITemplateManagerContext;
import com.zving.framework.ui.HttpVariableResolver;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpExecuteContext extends AbstractExecuteContext
{
  private HttpServletRequest request;
  private HttpServletResponse response;
  private ITemplateManagerContext managerContext;

  public HttpExecuteContext(ITemplateManagerContext managerContext, HttpServletRequest request, HttpServletResponse response)
  {
    this.request = request;
    this.response = response;
    this.managerContext = managerContext;
    try {
      this.rootWriter = response.getWriter();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public HttpServletRequest getRequest() {
    return this.request;
  }

  public HttpServletResponse getResponse() {
    return this.response;
  }

  public String getSessionID() {
    return this.request.getSession().getId();
  }

  public IVariableResolver getVariableResolver() {
    HttpVariableResolver context = HttpVariableResolver.getInstance(this.currentTag, this.request);
    return context;
  }

  public ITemplateManagerContext getManagerContext() {
    return this.managerContext;
  }

  public boolean execute(String url) {
    return this.managerContext.getTemplateManager().execute(url, this);
  }

  public PrintWriter getRootWriter()
  {
    return this.rootWriter;
  }

  public HttpExecuteContext getIncludeContext()
  {
    HttpExecuteContext context = new HttpExecuteContext(this.managerContext, this.request, this.response);
    context.currentTag = null;
    context.variables = this.variables;
    context.rootWriter = getOut();
    context.writerList = new ArrayList(8);
    return context;
  }
}