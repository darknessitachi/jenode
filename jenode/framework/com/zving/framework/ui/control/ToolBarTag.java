package com.zving.framework.ui.control;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.util.TagUtil;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ToolBarTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String theme;
  private String id;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "toolbar";
  }

  public int doStartTag() throws TemplateRuntimeException {
    if (ObjectUtil.empty(this.id)) {
      this.id = TagUtil.getTagID(this.pageContext, "ToolBar");
    }
    this.pageContext.getOut().print("<div class=\"z-toolbar");
    if ("flat".equals(this.theme)) {
      this.pageContext.getOut().print(" z-toolbar-flat");
    }
    this.pageContext.getOut().print("\"");
    this.pageContext.getOut().print(" id=\"" + this.id + "\"");
    this.pageContext.getOut().print(">");
    this.pageContext.getOut().print("<div class=\"z-toolbar-ct\">");
    this.pageContext.getOut().print("<div class=\"z-toolbar-overflow\">");
    this.pageContext.getOut().print("<div class=\"z-toolbar-nowrap\" id=\"" + this.id + "_body\">");
    return 2;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    getPreviousOut().write(content);
    getPreviousOut().write("</div></div></div></div>");
    getPreviousOut().print("<script>");
    getPreviousOut().print("Zving.Page.onReady(function(){new Zving.Toolbar('" + this.id + "');});");
    getPreviousOut().print("</script>");
    return 6;
  }

  public String getTheme() {
    return this.theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("theme"));
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