package com.zving.framework.ui.control;

import com.zving.framework.collection.Mapx;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.html.HtmlElement;
import com.zving.framework.ui.util.TagUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String onitemclick;
  private String type;
  public static final Pattern PItem = Pattern.compile("<(li|a)(.*?)onclick=(\\\"|\\')(.*?)\\2.*?>(.*?)</(li|a)>", 
    34);

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "menu";
  }

  public int doAfterBody() throws TemplateRuntimeException {
    try {
      getPreviousOut().print(getHtml(getBody()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public String getHtml(String content) {
    String items = parseItems(content);
    StringBuilder sb = new StringBuilder();
    if (ObjectUtil.empty(this.id)) {
      this.id = TagUtil.getTagID(this.pageContext, "Menu");
    }
    sb.append("<div id=\"" + this.id + "\" class=\"z-menu z-hidden");
    if ("flat".equals(this.type)) {
      sb.append(" z-menu-flat");
    }
    sb.append("\">");
    sb.append(items);
    sb.append("</div>");
    sb.append("<script>");
    sb.append("new Zving.DropMenu('" + this.id + "');");
    sb.append("</script>");
    return sb.toString();
  }

  private String parseItems(String content) {
    StringBuilder sb = new StringBuilder();
    Matcher m = PItem.matcher(content);
    int lastIndex = 0;
    int i = 0;

    while (m.find(lastIndex)) {
      String tmp = content.substring(lastIndex, m.start());
      StringUtil.isNotEmpty(tmp.trim());

      String attrs = m.group(1);
      String id = HtmlElement.parseAttr(attrs).getString("id");
      String innerText = m.group(5);
      innerText = LangUtil.get(innerText);
      String attr_onClick = m.group(4);
      sb.append("<a id=\"" + id + "\" href=\"javascript:void(0);\" class=\"z-menu-item\" onclick=\"" + attr_onClick + 
        ";return false;\"" + " hidefocus >" + innerText + "</a>");
      lastIndex = m.end();
      i++;
    }
    content.length();

    if (i != 0) {
      content = sb.toString();
    }
    return content;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOnitemclick() {
    return this.onitemclick;
  }

  public void setOnitemclick(String onitemclick) {
    this.onitemclick = onitemclick;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("type"));
    list.add(new TagAttr("onItemClick"));
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