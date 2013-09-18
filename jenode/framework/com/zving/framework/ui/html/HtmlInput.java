package com.zving.framework.ui.html;

public class HtmlInput extends HtmlElement
{
  public HtmlInput()
  {
    this.ElementType = "INPUT";
    this.TagName = "input";
  }

  public String getOuterHtml(String prefix) {
    String html = super.getOuterHtml(prefix);
    int index = html.lastIndexOf("</");
    if (index > 0) {
      html = html.substring(0, index).trim();
      html = html.substring(0, html.length() - 1) + " />";
    }
    return html;
  }
}