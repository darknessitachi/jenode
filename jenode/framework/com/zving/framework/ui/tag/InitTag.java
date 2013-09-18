package com.zving.framework.ui.tag;

import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class InitTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String method;

  public String getMethod()
  {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPrefix() {
    return "z";
  }

  public String getTagName() {
    return "init";
  }

  public int doStartTag() throws TemplateRuntimeException {
    if (ObjectUtil.notEmpty(this.method)) {
      IMethodLocator m = MethodLocatorUtil.find(this.method);
      PrivCheck.check(m);
      m.execute(new Object[0]);
    }
    return 2;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody();
    try
    {
      getPreviousOut().print(content);
    } catch (Exception e) {
      e.printStackTrace();
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