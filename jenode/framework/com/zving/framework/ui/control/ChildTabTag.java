package com.zving.framework.ui.control;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.StringFormat;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ChildTabTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String onClick;
  private String beforeClick;
  private String afterClick;
  private String src;
  private boolean selected;
  private boolean disabled;
  private boolean visible = true;

  private boolean lazy = true;

  private static int No = 0;

  public String getPrefix() {
    return "z";
  }

  public String getTagName() {
    return "childtab";
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody();
    try {
      if (StringUtil.isEmpty(this.id)) {
        this.id = (No++);
      }
      if (this.onClick == null) {
        this.onClick = "";
      }
      if ((StringUtil.isNotEmpty(this.onClick)) && (!this.onClick.trim().endsWith(";"))) {
        this.onClick = (this.onClick.trim() + ";");
      }
      if (this.beforeClick == null) {
        this.beforeClick = "";
      }
      if (this.afterClick == null) {
        this.afterClick = "";
      }
      String type = "";
      if (this.selected)
        type = "Current";
      else if (this.disabled) {
        type = "Disabled";
      }
      StringBuilder sb = new StringBuilder();
      String vStr = "";
      if (!this.visible) {
        vStr = "style='display:none'";
      }
      sb.append("<a href='javascript:void(0);' ztype='tab'  hidefocus='true' ");
      if (this.src.indexOf("${") >= 0) {
        this.src = String.valueOf(this.pageContext.evalExpression(this.src));
      }
      if ("Disabled".equalsIgnoreCase(type)) {
        sb.append("id='").append(this.id).append("' ").append(vStr).append(" data-href='").append(this.src)
          .append("' class='z-tab z-tab-disabled'");
      } else {
        if (this.lazy)
          this.src = ("data-href='" + this.src + "' data-lazy='true'");
        else {
          this.src = ("data-href='" + this.src + "' data-lazy='false'");
        }
        StringFormat sf = new StringFormat(
          "id='?' ? class='z-tab?' ? onclick=\"?;Zving.TabPage.onChildTabClick(this);?;return false;\">");
        sf.add(this.id);
        sf.add(vStr);
        sf.add(type.equals("Current") ? " z-tab-current" : "");
        sf.add(this.src);
        sf.add(this.onClick);
        sf.add(this.afterClick);
        sb.append(sf.toString());
      }
      sb.append(content);
      sb.append("</a>");

      ArrayList children = (ArrayList)this.pageContext.getAttribute("_ZVING_TABTAGKEY");
      if (children != null) {
        children.add(new String[] { this.id, this.src, String.valueOf(this.selected) });
      }
      getPreviousOut().print(sb);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
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

  public String getBeforeClick() {
    return this.beforeClick;
  }

  public void setBeforeClick(String beforeClick) {
    this.beforeClick = beforeClick;
  }

  public String getAfterClick() {
    return this.afterClick;
  }

  public void setAfterClick(String afterClick) {
    this.afterClick = afterClick;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public boolean isSelected() {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getSrc() {
    return this.src;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isLazy() {
    return this.lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("selected", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("visible", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("afterClick"));
    list.add(new TagAttr("beforeClick"));
    list.add(new TagAttr("onClick"));
    list.add(new TagAttr("src"));
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