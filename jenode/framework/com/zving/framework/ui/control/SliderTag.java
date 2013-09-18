package com.zving.framework.ui.control;

import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.util.TagUtil;
import com.zving.framework.utility.NumberUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SliderTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String name;
  private String onChange;
  private String value;
  private int defaultValue;
  private int min = 0;

  private int max = 100;
  private boolean disabled;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "slider";
  }

  public int doEndTag() throws TemplateRuntimeException {
    try {
      if (ObjectUtil.empty(this.id)) {
        this.id = TagUtil.getTagID(this.pageContext, "Slider");
      }
      String html = getHtml();
      getPreviousOut().print(html);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public String getHtml() {
    this.name = (StringUtil.isNull(this.name) ? "" : this.name);
    int value = this.defaultValue;
    if (NumberUtil.isInt(this.value)) {
      value = Integer.valueOf(this.value).intValue();
    }
    String disabledStr = this.disabled ? "disabled" : "";

    StringBuilder sb = new StringBuilder();
    sb.append("<table>")
      .append("<tr>")
      .append("<td height=36>")
      .append("<div style=\"margin-top:2px;\">")
      .append("<input id=\"").append(this.id).append("\" ").append("name=\"").append(this.name).append("\"")
      .append(" onchange=\"").append(this.onChange).append("\" ").append(disabledStr)
      .append(" value=\"").append(value).append("\" ztype=\"Number\" min=\"" + this.min + "\" max=\"" + this.max + "\" type=\"text\" style=\"width:40px;\">")
      .append("</div>")
      .append("</td>")
      .append("<td>&nbsp;</td>")
      .append("<td width=310>")
      .append("\t<div id=\"").append(this.id).append("_slider\" style=\"width:300px;\"></div>")
      .append("\t<div style=\"overflow:hidden; margin-top:-3px;font-size: 10px;\">")
      .append("\t\t<div style=\"float:left;\">").append(this.min).append("</div>")
      .append("\t\t<div style=\"float:right;\">").append(this.max).append("</div>")
      .append("\t</div>")
      .append("</td></tr></table>");

    sb.append("<script>");
    sb.append("Page.onReady(function(){var slider = new Slider({")
      .append("target:'" + this.id + "',")
      .append("min:" + this.min + ",")
      .append("max:" + this.max + ",")
      .append("value:" + value)
      .append("});")
      .append("slider.render(getDom(\"").append(this.id).append("_slider\"));")
      .append("});");
    sb.append("</script>");
    return sb.toString();
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
    list.add(new TagAttr("name"));
    list.add(new TagAttr("value"));
    list.add(new TagAttr("onChange"));
    list.add(new TagAttr("defaultValue"));
    list.add(new TagAttr("min"));
    list.add(new TagAttr("max"));
    list.add(new TagAttr("disabled"));
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

  public String getOnChange()
  {
    return this.onChange;
  }

  public void setOnChange(String onChange) {
    this.onChange = onChange;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(int defaultValue) {
    this.defaultValue = defaultValue;
  }

  public int getMin() {
    return this.min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return this.max;
  }

  public void setMax(int max) {
    this.max = max;
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
}