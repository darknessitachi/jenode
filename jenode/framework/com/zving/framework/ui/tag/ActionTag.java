package com.zving.framework.ui.tag;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.handler.ActionHandler;
import com.zving.framework.extend.ExtendItemConfig;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.zhtml.HttpExecuteContext;
import com.zving.framework.utility.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class ActionTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String method;

  public String getMethod()
  {
    return this.method;
  }

  public String getPrefix() {
    return "z";
  }

  public String getTagName() {
    return "action";
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public int doStartTag() throws TemplateRuntimeException {
    try {
      if ((this.pageContext instanceof HttpExecuteContext)) {
        HttpExecuteContext httpContext = (HttpExecuteContext)this.pageContext;
        HttpServletRequest request = httpContext.getRequest();
        if (ObjectUtil.notEmpty(this.method)) {
          String requestURI = request.getRequestURI();
          String context = request.getContextPath();
          String url = requestURI.substring(context.length(), requestURI.length());
          ActionHandler up = (ActionHandler)
            ((ExtendItemConfig)PluginManager.getInstance().getPluginConfig("com.zving.framework").getExtendItems()
            .get("com.zving.framework.core.ActionHandler")).getInstance();
          up.handle(url, request, httpContext.getResponse());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 2;
  }

  public int doEndTag() throws TemplateRuntimeException {
    if ((this.pageContext instanceof HttpExecuteContext)) {
      HttpExecuteContext httpContext = (HttpExecuteContext)this.pageContext;
      HttpServletRequest request = httpContext.getRequest();
      if (ObjectUtil.equal("true", request.getAttribute("ZACTION_SKIPPAGE"))) {
        return 5;
      }
    }
    return 6;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("method", true));
    return list;
  }

  public String getName()
  {
    return "<" + getPrefix() + ":" + getTagName() + ">";
  }

  public String getDescription()
  {
    return "";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}