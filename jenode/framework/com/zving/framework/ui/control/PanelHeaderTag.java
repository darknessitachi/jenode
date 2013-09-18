package com.zving.framework.ui.control;

import com.zving.framework.i18n.LangUtil;
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

public class PanelHeaderTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String onClick;
  private boolean collapsible;
  private boolean collapsed;
  public static final Pattern PImg = Pattern.compile("^<img .*?src\\=.*?>", 34);

  public String getPrefix() {
    return "z";
  }

  public String getTagName() {
    return "panelheader";
  }

  public void init() {
    this.collapsible = true;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    try {
      Matcher matcher = PImg.matcher(content);
      String img = null;
      String text = null;
      if (ObjectUtil.empty(this.id)) {
        this.id = TagUtil.getTagID(this.pageContext, "PanelHeader");
      }
      if (matcher.find()) {
        img = content.substring(matcher.start(), matcher.end());
        text = content.substring(matcher.end());
        getPreviousOut().print(getHtml(this.id, img, text, this.collapsible, this.collapsed));
      } else {
        text = content;
        getPreviousOut().print(getHtml(this.id, text, this.collapsible, this.collapsed));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public static String getHtml(String id, String img, String text, boolean collapsible, boolean collapsed) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"z-panel-header\" id=\"").append(id).append("\">").append("<div class=\"z-panel-header-ct\">");
    if (collapsible) {
      sb.append("<a class=\"z-tool-toggle\" href=\"#;\">&nbsp;</a>");
    }
    text = LangUtil.get(text);
    sb.append("<b class=\"z-panel-header-text\">").append(text).append("</b>");
    sb.append("</div>").append("</div>");
    sb.append("<script>$(function(){new PanelHeader({el:'").append(id).append("',collapsed:").append(collapsed)
      .append("});});</script>");
    return sb.toString();
  }

  public static String getHtml(String id, String html, boolean collapsible, boolean collapsed) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"z-panel-header\" id=\"").append(id).append("\">").append("<div class=\"z-panel-header-ct\">");
    if (collapsible) {
      sb.append("<a class=\"z-tool-toggle\" href=\"#;\">&nbsp;</a>");
    }
    html = LangUtil.get(html);
    sb.append("<b class=\"z-panel-header-html\">").append(html).append("</b>");
    sb.append("</div>").append("</div>");
    sb.append("<script>$(function(){new PanelHeader({el:'").append(id).append("',collapsed:").append(collapsed)
      .append("});});</script>");
    return sb.toString();
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOnClick() {
    return this.onClick;
  }

  public void setOnClick(String onClick) {
    this.onClick = onClick;
  }

  public boolean isCollapsible() {
    return this.collapsible;
  }

  public void setCollapsible(boolean collapsible) {
    this.collapsible = collapsible;
  }

  public boolean isCollapsed() {
    return this.collapsed;
  }

  public void setCollapsed(boolean collapsed) {
    this.collapsed = collapsed;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("collapsed", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("collapsible", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("onClick"));
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