package com.zving.framework.ui.control;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.html.HtmlScript;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TabTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private boolean lazy = true;
  private boolean cachedom = false;
  public static final String TabTagKey = "_ZVING_TABTAGKEY";

  public void init()
  {
    this.pageContext.setAttribute("_ZVING_TABTAGKEY", new ArrayList());
  }

  public String getPrefix() {
    return "z";
  }

  public String getTagName() {
    return "tab";
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody();
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" id=\"js_layoutTable\" class=\"js_layoutTable\">");
      sb.append("<tr><td height=\"33\" valign=\"top\" style=\"_position:relative\">");
      sb.append("<div class=\"z-tabpanel\"><div class=\"z-tabpanel-ct\"><div class=\"z-tabpanel-overflow\"><div class=\"z-tabpanel-nowrap\"");
      if ((!this.lazy) || (this.cachedom)) {
        sb.append(" data-cache-dom=\"true\"");
      }
      sb.append(">");
      sb.append(content);
      sb.append("</div></div></div></div>");
      sb.append("</td></tr>");
      String selectedID = "";

      ArrayList<String[]> children = (ArrayList)this.pageContext.getAttribute("_ZVING_TABTAGKEY");
      if (ObjectUtil.notEmpty(children)) {
        for (String[] arr : children) {
          String id = arr[0];
          String selected = arr[2];
          if ("true".equals(selected)) {
            selectedID = id;
            break;
          }
        }
        if (ObjectUtil.empty(selectedID)) {
          selectedID = ((String[])children.get(0))[0];
        }
      }

      if (ObjectUtil.notEmpty(children)) {
        sb.append("<tr><td height=\"*\" valign=\"top\">");
        sb.append("<div");
        if ((!this.lazy) || (this.cachedom)) {
          sb.append(" data-cache-dom=\"true\"");
        }
        sb.append("><div data-role=\"page\" data-external-page=\"false\">&nbsp;</div></div>");
        HtmlScript script = new HtmlScript();
        String scriptString = "Zving.Page.onReady(function(){Zving.TabPage.init(";
        if (this.lazy)
          scriptString = scriptString + "true, ";
        else {
          scriptString = scriptString + "false, ";
        }
        scriptString = scriptString + "\"" + selectedID + "\");},10);";
        script.setInnerHTML(scriptString);

        sb.append(script.getOuterHtml());
        sb.append("</td></tr>");
      }
      sb.append("</table>");
      getPreviousOut().print(sb);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public boolean isLazy() {
    return this.lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  public boolean isCachedom() {
    return this.cachedom;
  }

  public void setCachedom(boolean cachedom) {
    this.cachedom = cachedom;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("cachedom", TagAttr.BOOL_OPTIONS));
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