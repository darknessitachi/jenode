package com.zving.framework.ui.control;

import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.util.TagUtil;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollPanelTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String targetId;
  private String theme;
  private String overflow;
  private boolean adaptive;
  public static final Pattern PId = Pattern.compile("^\\s*<div[^<>]+id\\=['\"]([\\w-]+)['\"]", 10);

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "scrollpanel";
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    Matcher matcher = PId.matcher(content);
    this.targetId = null;
    if ((matcher.find()) && (matcher.group(1) != null)) {
      this.targetId = matcher.group(1);
      if (ObjectUtil.empty(this.id))
        this.id = (this.targetId + "_scrollpanel");
    }
    else {
      if (ObjectUtil.empty(this.id)) {
        this.targetId = TagUtil.getTagID(this.pageContext, "ScrollPanel");
        this.id = (this.targetId + "_scrollpanel");
      }
      content.replaceFirst("^\\s*<div\\b", "<div id=\"" + this.targetId + "\" ");
    }

    getPreviousOut().print(content);

    getPreviousOut().print("<script>");
    getPreviousOut().print("Zving.Page.onLoad(function(){");
    getPreviousOut().print("if(Zving.ScrollPanel)");
    getPreviousOut().print(" new Zving.ScrollPanel({");
    if (ObjectUtil.notEmpty(this.overflow)) {
      getPreviousOut().print("  overflow:'" + this.overflow + "',");
    }
    getPreviousOut().print("  target:'" + this.targetId + "'");
    getPreviousOut().print(" });");
    getPreviousOut().print("});");
    getPreviousOut().print("</script>");
    return 6;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTheme() {
    return this.theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getOverflow() {
    return this.overflow;
  }

  public void setOverflow(String overflow) {
    this.overflow = overflow;
  }

  public boolean isAdaptive() {
    return this.adaptive;
  }

  public void setAdaptive(boolean adaptive) {
    this.adaptive = adaptive;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("adaptive", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("overflow"));
    list.add(new TagAttr("targetID"));
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