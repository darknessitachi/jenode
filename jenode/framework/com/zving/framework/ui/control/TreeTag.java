package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.html.HtmlDt;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TreeTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String method;
  private String style;
  private boolean lazy;
  private boolean customscrollbar;
  private boolean expand;
  private int level;
  private int size;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "tree";
  }

  public void init() {
    this.customscrollbar = true;
  }

  public int doStartTag() throws TemplateRuntimeException {
    this.pageContext.setKeepExpression(true);
    return 2;
  }

  public int doEndTag() throws TemplateRuntimeException {
    this.pageContext.setKeepExpression(false);
    return 6;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    content = content.replaceAll("<p\\b", "<dt").replaceAll("</p>", "</dt>");
    try {
      if ((this.method == null) || (this.method.equals(""))) {
        throw new RuntimeException("Tree's method can't be empty");
      }

      TreeAction ta = new TreeAction();
      ta.setTagBody(content);
      ta.setMethod(this.method);

      HtmlDt p = new HtmlDt();
      p.parseHtml(content);
      ta.setTemplate(p);

      ta.setID(this.id);
      ta.setLazy(this.lazy);
      ta.setCustomscrollbar(this.customscrollbar);
      ta.setExpand(this.expand);
      if (this.level <= 0) {
        this.level = 999;
      }
      ta.setLevel(this.level);
      ta.setStyle(this.style);
      ta.setPageSize(this.size);

      IMethodLocator m = MethodLocatorUtil.find(this.method);
      PrivCheck.check(m);

      ta.setParams(Current.getRequest());
      m.execute(new Object[] { ta });

      getPreviousOut().write(ta.getHtml());
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return 6;
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public boolean isLazy() {
    return this.lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  public boolean isCustomscrollbar() {
    return this.customscrollbar;
  }

  public void setCustomscrollbar(boolean customscrollbar) {
    this.customscrollbar = customscrollbar;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMethod() {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getStyle() {
    return this.style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public boolean isExpand() {
    return this.expand;
  }

  public void setExpand(boolean expand) {
    this.expand = expand;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("customscrollbar", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("expand", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("level", 8));
    list.add(new TagAttr("size", 8));
    list.add(new TagAttr("style"));
    list.add(new TagAttr("method"));
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