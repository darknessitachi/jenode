package com.zving.framework.ui.html;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.FrameworkException;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTable extends HtmlElement
{
  public static final Pattern PTable = Pattern.compile("^\\s*<table(.*?)>(.*)</table>\\s*$", 34);

  public static final Pattern PInnerTable = Pattern.compile("<table(.*?)>(.*?)</table>", 34);

  public static final Pattern PTR = Pattern.compile("^\\s*<tr(.*?)>(.*)</tr>\\s*$", 34);

  public static final Pattern PTRPre = Pattern.compile("<tr.*?>.*?</tr>", 34);

  public static final Pattern PTD = Pattern.compile("^\\s*<(td|th)(.*?)>(.*)</(td|th)>\\s*$", 34);

  public static final Pattern PTDPre = Pattern.compile("<(td|th).*?>.*?</(td|th)>", 34);
  public static final String ProtectedTableStart = "<!--_ZVING_INNERTABLE_PROTECTED_";
  private ArrayList<String> pList = null;

  private boolean hasTBody = false;

  private boolean hasTHead = false;

  public HtmlTable() {
    this.ElementType = "TABLE";
    this.TagName = "table";
  }

  public void addTR(HtmlTR tr) {
    addChild(tr);
  }

  public HtmlTR getTR(int index) {
    return (HtmlTR)this.Children.get(index);
  }

  public void removeTR(int index) {
    if ((index < 0) || (index > this.Children.size())) {
      throw new RuntimeException("Index out of range:" + index);
    }
    this.Children.remove(index);
  }

  public void removeColumn(int index) {
    for (int i = 0; i < this.Children.size(); i++) {
      HtmlTR tr = getTR(i);
      if (index < tr.Children.size())
        tr.removeTD(index);
    }
  }

  public void setWidth(int width)
  {
    this.Attributes.put("width", new Integer(width));
  }

  public int getWidth() {
    return ((Integer)this.Attributes.get("width")).intValue();
  }

  public void setHeight(int height) {
    this.Attributes.put("height", new Integer(height));
  }

  public int getHeight() {
    return ((Integer)this.Attributes.get("height")).intValue();
  }

  public void setAlign(String align) {
    this.Attributes.put("align", align);
  }

  public String getAlign() {
    return (String)this.Attributes.get("align");
  }

  public void setBgColor(String bgColor) {
    this.Attributes.put("bgColor", bgColor);
  }

  public String getBgColor() {
    return (String)this.Attributes.get("bgColor");
  }

  public void setBackgroud(String backgroud) {
    this.Attributes.put("backgroud", backgroud);
  }

  public String getBackgroud() {
    return (String)this.Attributes.get("backgroud");
  }

  public void setCellSpacing(String cellSpacing) {
    setAttribute("cellSpacing", cellSpacing);
  }

  public String getCellSpacing() {
    return getAttribute("cellSpacing");
  }

  public void setCellPadding(String cellPadding) {
    setAttribute("cellPadding", cellPadding);
  }

  public String getCellPadding() {
    return getAttribute("cellPadding");
  }

  public void parseHtml(String html) {
    Matcher m = PTable.matcher(html);
    if (!m.find()) {
      throw new FrameworkException("Pasre table failed:" + html);
    }
    String attrs = m.group(1);
    String trs = m.group(2).trim();

    this.Attributes.clear();
    this.Children.clear();

    this.Attributes = parseAttr(attrs);

    m = PInnerTable.matcher(trs);
    int lastEndIndex = 0;
    while (m.find(lastEndIndex)) {
      if (this.pList == null) {
        this.pList = new ArrayList();
      }
      this.pList.add(m.group(0));
      lastEndIndex = m.end();
    }
    if (this.pList != null) {
      for (int i = 0; i < this.pList.size(); i++) {
        trs = StringUtil.replaceEx(trs, ((String)this.pList.get(i)).toString(), "<!--_ZVING_INNERTABLE_PROTECTED_" + i + "-->");
      }

    }

    m = PTRPre.matcher(trs);
    lastEndIndex = 0;
    while (m.find(lastEndIndex)) {
      String t = trs.substring(m.start(), m.end());
      HtmlTR tr = new HtmlTR(this);
      tr.parseHtml(t);
      addTR(tr);
      lastEndIndex = m.end();
    }
  }

  public String restoreInnerTable(String html) {
    if ((this.pList == null) || (this.pList.size() == 0)) {
      return html;
    }
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (String str : StringUtil.splitEx(html, "<!--_ZVING_INNERTABLE_PROTECTED_")) {
      if (StringUtil.isNotEmpty(str)) {
        if (i != 0) {
          int index = Integer.parseInt(str.substring(0, str.indexOf("-")));
          sb.append(((String)this.pList.get(index)).toString());
          str = str.substring(str.indexOf(">") + 1);
        }
        sb.append(str);
      }
      i++;
    }
    return sb.toString();
  }

  public String getOuterHtml() {
    return getOuterHtml("");
  }

  public String getOuterHtml(String prefix) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    sb.append("<");
    sb.append(this.TagName);
    String trPrefix = prefix + "\t";
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

    sb.append(">\n");
    if ((getChildren().size() > 0) && (getTR(0).getChildren().size() > 0)) {
      if (this.hasTHead) {
        sb.append(trPrefix);
        sb.append("<thead>\n");
      }
      sb.append(getTR(0).getOuterHtml(trPrefix));
      if (this.hasTHead) {
        sb.append(trPrefix);
        sb.append("</thead>\n");
      }
      if (getChildren().size() > 1) {
        if ((this.hasTHead) || (this.hasTBody)) {
          sb.append(trPrefix);
          sb.append("<tbody>\n");
        }
        for (int i = 1; i < this.Children.size(); i++) {
          sb.append("\n");
          sb.append(((HtmlElement)this.Children.get(i)).getOuterHtml(trPrefix));
        }
        if ((this.hasTHead) || (this.hasTBody)) {
          sb.append(trPrefix);
          sb.append("</tbody>\n");
        }
      }
    }
    sb.append("\n");
    sb.append(prefix);
    sb.append("</");
    sb.append(this.TagName);
    sb.append(">");
    return sb.toString();
  }

  public boolean isHasTBody() {
    return this.hasTBody;
  }

  public void setHasTBody(boolean hasTBody) {
    this.hasTBody = hasTBody;
  }

  public boolean isHasTHead() {
    return this.hasTHead;
  }

  public void setHasTHead(boolean hasTHead) {
    this.hasTHead = hasTHead;
  }

  public static void test() {
    HtmlTable table = new HtmlTable();
    try {
      String html = FileUtil.readText("G:/Test.txt");
      table.parseHtml(html);
      System.out.println(table.getOuterHtml());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Pattern PTR = Pattern.compile("^\\s*<(tr|th)(.*?)>(.*)</(tr|th)>\\s*$", 34);
    Matcher m = PTR.matcher("<th>dsfsd</th>");
    if (m.find())
      System.out.println(m.group(3));
  }
}