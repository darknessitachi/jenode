package com.zving.framework.ui.html;

import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HtmlElement
  implements Cloneable
{
  public static final Pattern PAttr = Pattern.compile("\\s+?(\\w+?)\\s*?=\\s*?(\\\"|\\')(.*?)\\2", 
    34);

  public static final Pattern PAttr2 = Pattern.compile("\\s+?(\\w+?)\\s*?=\\s*?([\\S&&[^\\\"\\']]*?)(\\s|>)", 
    34);
  public static final String TABLE = "TABLE";
  public static final String TR = "TR";
  public static final String TD = "TD";
  public static final String SCRIPT = "SCRIPT";
  public static final String DIV = "DIV";
  public static final String SPAN = "SPAN";
  public static final String P = "P";
  public static final String DT = "DT";
  public static final String SELECT = "SELECT";
  protected String ElementType;
  protected String TagName;
  public Mapx<String, Object> Attributes = new Mapx();
  protected HtmlElement ParentElement;
  public ArrayList<HtmlElement> Children = new ArrayList();
  public String InnerHTML;
  protected static Mapx<String, Pattern> PatternMap = new Mapx();

  public String getOuterHtml()
  {
    return getOuterHtml("");
  }

  public String getOuterHtml(String prefix) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    sb.append("<");
    sb.append(this.TagName);
    for (String k : this.Attributes.keySet()) {
      Object v = this.Attributes.get(k);
      if (v != null) {
        sb.append(" ");
        sb.append(k);
        sb.append("=\"");
        sb.append(v);
        sb.append("\"");
      }
    }
    sb.append(">");
    if (!ObjectUtil.empty(this.InnerHTML))
      sb.append(this.InnerHTML);
    else {
      for (int i = 0; i < this.Children.size(); i++) {
        sb.append("\n");
        sb.append(((HtmlElement)this.Children.get(i)).getOuterHtml(prefix + "\t"));
      }
    }
    if (this.Children.size() > 0) {
      sb.append("\n");
    }
    sb.append(prefix);
    sb.append("</");
    sb.append(this.TagName);
    sb.append(">");
    return sb.toString();
  }

  public String getAttribute(String attrName) {
    return this.Attributes.getString(attrName.toLowerCase());
  }

  public void setAttribute(String attrName, String attrValue) {
    this.Attributes.put(attrName.toLowerCase(), attrValue);
  }

  public void removeAttribute(String attrName) {
    this.Attributes.remove(attrName.toLowerCase());
  }

  public Mapx<String, Object> getAttributes() {
    return this.Attributes;
  }

  public void setAttributes(Mapx<String, Object> attributes) {
    this.Attributes.clear();
    this.Attributes.putAll(attributes);
  }

  public ArrayList<HtmlElement> getChildren() {
    return this.Children;
  }

  public void addChild(HtmlElement child) {
    this.Children.add(child);
    child.setParentElement(this);
  }

  public String getClassName() {
    return this.Attributes.getString("class");
  }

  public void setClassName(String className) {
    this.Attributes.put("class", className);
  }

  public String getElementType() {
    return this.ElementType;
  }

  public String getID() {
    return this.Attributes.getString("id");
  }

  public void setID(String id) {
    this.Attributes.put("id", id);
  }

  public HtmlElement getParentElement() {
    return this.ParentElement;
  }

  protected void setParentElement(HtmlElement parentElement) {
    this.ParentElement = parentElement;
  }

  public String getStyle() {
    return this.Attributes.getString("style");
  }

  public void setAttributes(String attrs)
  {
    boolean stringFlag = false;
    int lastNameIndex = 0;
    int lastValueIndex = 0;
    String name = null;
    attrs = attrs.trim();
    Mapx map = new Mapx();
    for (int i = 0; i < attrs.length(); i++) {
      char c = attrs.charAt(i);
      if (c == '\\') {
        i++;
      }
      else {
        if ((c == ' ') || (i == attrs.length() - 1)) {
          lastNameIndex = i + 1;
          String value = attrs.substring(lastValueIndex, i);
          if (i == attrs.length() - 1) {
            value = attrs.substring(lastValueIndex);
          }
          if ((value.startsWith("\"")) && (value.endsWith("\""))) {
            value = value.substring(1, value.length() - 1);
          }
          if ((value.startsWith("'")) && (value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
          }
          value = StringUtil.javaDecode(value);
          map.put(name, value);
        }
        if (c == '=') {
          lastValueIndex = i + 1;
          name = attrs.substring(lastNameIndex, i);
        }
        if ((c == '"') && 
          (stringFlag)) {
          stringFlag = false;
        }
      }
    }
    this.Attributes.putAll(map);
  }

  public void setStyle(String style) {
    this.Attributes.put("style", style);
  }

  public String getTagName() {
    return this.TagName;
  }

  public String getInnerHTML() {
    if (this.Children.size() > 0) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < this.Children.size(); i++) {
        sb.append(((HtmlElement)this.Children.get(i)).getOuterHtml());
      }
      return sb.toString();
    }
    return this.InnerHTML;
  }

  public void setInnerHTML(String innerHTML)
  {
    this.InnerHTML = innerHTML;
  }

  public static Mapx<String, Object> parseAttr(String attrs)
  {
    Matcher m = PAttr.matcher(attrs);
    int lastEndIndex = 0;
    Mapx map = new Mapx();
    while (m.find(lastEndIndex)) {
      map.put(m.group(1).toLowerCase(), m.group(3));
      lastEndIndex = m.end();
    }
    return map;
  }

  private Pattern getParrtenByTagName()
  {
    Pattern o = (Pattern)PatternMap.get(this.TagName);
    if (o == null) {
      Pattern pattern = Pattern.compile("<" + this.TagName + "(.*?)>(.*?)</" + this.TagName + ">", 
        34);
      PatternMap.put(this.TagName, pattern);
      return pattern;
    }
    return o;
  }

  public void parseHtml(String html) throws Exception {
    Pattern pattern = getParrtenByTagName();
    Matcher m = pattern.matcher(html);
    if (!m.find()) {
      throw new Exception(this.TagName + " not found in html:" + html);
    }
    String attrs = m.group(1);

    this.Attributes.clear();
    this.Children.clear();

    this.Attributes = parseAttr(attrs);
    this.InnerHTML = m.group(2).trim();
  }

  public Object clone() {
    HtmlElement ele = null;
    try {
      ele = (HtmlElement)getClass().newInstance();
      ele.setAttributes(this.Attributes.clone());
      for (int i = 0; i < this.Children.size(); i++) {
        ele.addChild((HtmlElement)this.Children.get(i));
      }
      ele.InnerHTML = this.InnerHTML;
      ele.TagName = this.TagName;
      ele.ElementType = this.ElementType;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ele;
  }

  public String toString() {
    return getOuterHtml();
  }
}