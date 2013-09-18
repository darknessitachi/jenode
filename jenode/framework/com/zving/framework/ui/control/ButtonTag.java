package com.zving.framework.ui.control;

import com.zving.framework.i18n.LangUtil;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.util.TagUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ButtonTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String name;
  private String onClick;
  private String href;
  private String target;
  private String priv;
  private String type;
  private String theme;
  private String menu;
  private boolean disabled;
  private boolean checked;
  public static final Pattern PImg = Pattern.compile("<img .*?src\\=.*?>", 34);

  private static Pattern p = Pattern.compile(" src\\=\"[^\"]*icons\\/([^\"\\/]+)\\.png\"", 34);

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "button";
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody();
    try {
      Matcher matcher = PImg.matcher(content);
      String img = null;
      String text = null;
      if (matcher.find()) {
        img = content.substring(matcher.start(), matcher.end());
        img = getImgSpirite(img);
        text = content.substring(matcher.end());
      }
      if (ObjectUtil.empty(this.id)) {
        this.id = TagUtil.getTagID(this.pageContext, "Button");
      }
      String html = getHtml(this.id, this.name, this.type, this.theme, this.menu, this.onClick, this.href, this.target, this.priv, img, text, this.checked, this.disabled);
      getPreviousOut().print(html);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  private static String getImgSpirite(String imgTag)
  {
    Matcher matcher = p.matcher(imgTag);

    if (matcher.find()) {
      String fileName = matcher.group(1);
      imgTag = imgTag.replaceAll(fileName, "icon000").replaceFirst("<img", "<img class=\"" + fileName + "\"");
    }
    return imgTag;
  }

  public static String getHtml(String onclick, String priv, String img, String text) {
    return getHtml(null, null, null, null, null, onclick, null, null, priv, img, text, false, false);
  }

  public static String getHtml(String name, String theme, String onclick, String priv, String img, String text, boolean disabled)
  {
    return getHtml(null, name, null, theme, null, onclick, null, null, priv, img, text, false, disabled);
  }

  public static String getHtml(String id, String name, String type, String theme, String menu, String onclick, String href, String target, String priv, String img, String text, boolean checked, boolean disabled)
  {
    StringBuilder sb = new StringBuilder();
    if (ObjectUtil.empty(type)) {
      type = "push";
    }
    sb.append("<a href='").append(ObjectUtil.notEmpty(href) ? href : "javascript:void(0);").append("'");
    if (ObjectUtil.notEmpty(target)) {
      sb.append(" target='" + target + "'");
    }
    sb.append(" ztype='button'");
    sb.append(" buttontype='" + type + "'");
    sb.append(" id='" + id + "'");
    sb.append(" name='" + name + "'");
    if ("checkbox".equals(type)) {
      sb.append(!checked ? " " : " checked='true'");
    }
    if (ObjectUtil.notEmpty(menu)) {
      sb.append(" menu='" + menu + "'");
    }
    sb.append(" class='z-btn");
    if ("menu".equals(type))
      sb.append(" z-btn-menu");
    else if (("split".equals(type)) || ("select".equals(type))) {
      sb.append(" z-btn-split");
    }
    if ("checkbox".equals(type)) {
      sb.append(" z-btn-checkable");
    }
    if (StringUtil.isNotEmpty(theme)) {
      sb.append(" z-btn-" + theme);
    }
    if (disabled) {
      sb.append(" z-btn-disabled");
    }
    sb.append("'");
    sb.append(" onselectstart='return false'");

    if (onclick != null) {
      if (disabled)
        sb.append(" _onclick_bak=\"").append(onclick).append(";return false;\"");
      else {
        sb.append(" onclick=\"").append(onclick).append(";return false;\"");
      }
    }
    if (priv != null) {
      sb.append(" priv=\"").append(priv).append("\"");
    }
    sb.append(">");
    sb.append(img);
    text = LangUtil.get(text);
    sb.append("<b>").append(text).append("<i></i></b>");
    sb.append("</a>");
    sb.append("<script> new Zving.Button('").append(id).append("');</script>");
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

  public String getHref() {
    return this.href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getTarget() {
    return this.target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getPriv() {
    return this.priv;
  }

  public void setPriv(String priv) {
    this.priv = priv;
  }

  public boolean isChecked() {
    return this.checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMenu() {
    return this.menu;
  }

  public void setMenu(String menu) {
    this.menu = menu;
  }

  public String getTheme() {
    return this.theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("checked", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("href"));
    list.add(new TagAttr("menu"));
    list.add(new TagAttr("name"));
    list.add(new TagAttr("onclick"));
    list.add(new TagAttr("priv"));
    list.add(new TagAttr("target"));
    list.add(new TagAttr("theme"));
    list.add(new TagAttr("type"));
    return list;
  }

  public String getDescription()
  {
    return "";
  }

  public String getName()
  {
    return "<" + getPrefix() + ":" + getTagName() + ">";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}