package com.zving.framework.ui.control;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.ui.html.HtmlDt;
import com.zving.framework.utility.StringUtil;

public class TreeItem extends HtmlDt
  implements Cloneable
{
  public static final String Blank_Image_Path = "framework/images/blank24x24.gif";
  public static final String Class_Branch = "branch";
  public static final String Class_Branch_NotLast_NotExpand = "branch-notlast-collapse";
  public static final String Class_Branch_Last_NotExpand = "branch-last-collapse";
  public static final String Class_Branch_NotLast_Expand = "branch-notlast-expand";
  public static final String Class_Branch_Last_Expand = "branch-last-expand";
  public static final String Class_Line_Vertical = "line-vertical";
  public static final String Class_Line_Null = "line-null";
  public static final String Class_Leaf_Last = "leaf-last";
  public static final String Class_Leaf_NotLast = "leaf-notlast";
  public static final String Branch_NotLast_NotExpand = "1";
  public static final String Branch_NotLast_Expand = "2";
  public static final String Branch_Last_NotExpand = "3";
  public static final String Branch_Last_Expand = "4";
  private String icon;
  private int level;
  private boolean isLast;
  private boolean isRoot;
  private boolean isBranch;
  private boolean isLeaf;
  private boolean lazy;
  private boolean resizeable;
  private TreeItem parent;
  private TreeAction action;
  private String ID;
  private String ParentID;
  private String levelStr;
  private boolean isExpanded = true;
  private DataRow data;

  public String getOuterHtml()
  {
    HtmlDt item = new HtmlDt();
    item.Attributes.putAll(this.Attributes);
    item.setAttribute("level", this.level+"");
    item.setAttribute("id", this.action.getID() + "_" + this.ID);
    item.setAttribute("parentID", this.ParentID);
    item.setAttribute("lazy", this.lazy ? "1" : "0");
    item.setAttribute("resizeable", this.resizeable ? "1" : "0");
    if (this.isLast) {
      item.setAttribute("islast", "true");
    }

    String onclick = getAttributeExt("onclick");
    if (!StringUtil.isEmpty(onclick)) {
      item.setAttribute("onclick", onclick);
    }
    String oncontextmenu = getAttributeExt("oncontextmenu");
    if (!StringUtil.isEmpty(oncontextmenu)) {
      item.setAttribute("oncontextmenu", getAttributeExt("oncontextmenu"));
    }
    String afterDrag = getAttribute("afterDrag");
    if (StringUtil.isNotEmpty(afterDrag)) {
      item.setAttribute("dragEnd", "Tree.dragEnd");
      item.setAttribute("onMouseUp", "DragManager.onMouseUp(event,this)");
      if (Current.getRequest().getHeaders().getString("user-agent").indexOf("msie") >= 0)
        item.setAttribute("onMouseEnter", "DragManager.onMouseOver(event,this)");
      else {
        item.setAttribute("onMouseOver", "DragManager.onMouseOver(event,this)");
      }
      item.setAttribute("dragOver", "Tree.dragOver");

      if (Current.getRequest().getHeaders().getString("user-agent").indexOf("msie") >= 0)
        item.setAttribute("onMouseLeave", "DragManager.onMouseOut(event,this)");
      else {
        item.setAttribute("onMouseOut", "DragManager.onMouseOut(event,this)");
      }
      item.setAttribute("dragOut", "Tree.dragOut");
    }

    String text = getText();
    String prefix = Config.getContextPath();
    text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
      " class='tree-skeleton-icon' style='background:url(" + 
      prefix + getIcon() + ") no-repeat center center;'>" + text;

    if (this.isBranch)
      item.setAttribute("treenodetype", "trunk");
    else if (this.isLeaf) {
      item.setAttribute("treenodetype", "leaf");
    }

    StringBuilder levelSb = new StringBuilder();

    if ((this.isBranch) && (this.isLast) && (this.isExpanded)) {
      text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
        " class='tree-skeleton-icon " + "branch branch-last-expand" + "'>" + text;
      if (this.lazy) {
        levelSb.insert(0, "0");
      }

      item.setAttribute("expand", "4");
    }
    if ((this.isBranch) && (this.isLast) && (!this.isExpanded)) {
      text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
        " class='tree-skeleton-icon " + "branch branch-last-collapse" + "'>" + text;
      if (this.lazy) {
        levelSb.insert(0, "0");
      }
      item.setAttribute("expand", "3");
    }
    if ((this.isBranch) && (!this.isLast) && (!this.isExpanded)) {
      text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
        " class='tree-skeleton-icon " + "branch branch-notlast-collapse" + "'>" + text;
      if (this.lazy) {
        levelSb.insert(0, "1");
      }
      item.setAttribute("expand", "1");
    }
    if ((this.isBranch) && (!this.isLast) && (this.isExpanded)) {
      text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
        " class='tree-skeleton-icon " + "branch branch-notlast-expand" + "'>" + text;
      if (this.lazy) {
        levelSb.insert(0, "1");
      }
      item.setAttribute("expand", "2");
    }
    if ((this.isLeaf) && (this.isLast)) {
      text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
        " class='tree-skeleton-icon " + "leaf-last" + "'>" + 
        text;
    }
    if ((this.isLeaf) && (!this.isLast)) {
      text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
        " class='tree-skeleton-icon " + "leaf-notlast" + 
        "'>" + text;
    }

    TreeItem pTI = this.parent;
    while ((pTI != null) && (!pTI.isRoot)) {
      if (pTI.isLast) {
        text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
          " class='tree-skeleton-icon " + "line-null" + 
          "'>" + text;
        if (this.lazy)
          levelSb.insert(0, "0");
      }
      else {
        text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
          " class='tree-skeleton-icon " + "line-vertical" + 
          "'>" + text;
        if (this.lazy) {
          levelSb.insert(0, "1");
        }
      }
      pTI = pTI.parent;
    }

    if (this.levelStr != null) {
      for (int j = this.levelStr.length() - 1; j >= 0; j--) {
        if (this.levelStr.charAt(j) == '0')
          text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
            " class='tree-skeleton-icon " + "line-null" + 
            "'>" + text;
        else {
          text = "<img src='" + prefix + "framework/images/blank24x24.gif" + "'" + 
            " class='tree-skeleton-icon " + "line-vertical" + 
            "'>" + text;
        }
      }
      if (this.lazy) {
        levelSb.insert(0, this.levelStr);
      }
    }
    if (this.lazy) {
      item.setAttribute("levelstr", levelSb.toString());
    }

    item.setInnerHTML(text);

    return item.getOuterHtml();
  }

  private String getAttributeExt(String attr) {
    String v = getAttribute(attr);
    if (StringUtil.isEmpty(v)) {
      if (this.parent != null) {
        return this.parent.getAttributeExt(attr);
      }
      return null;
    }

    return v;
  }

  public String getIcon() {
    if (this.icon == null) {
      if (this.isRoot) {
        return this.action.getRootIcon();
      }
      if (this.isLeaf) {
        return this.action.getLeafIcon();
      }
      if (this.isBranch) {
        return this.action.getBranchIcon();
      }
    }
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public boolean isBranch() {
    return this.isBranch;
  }

  public void setBranch(boolean isBranch) {
    this.isBranch = isBranch;
    this.isLeaf = (!isBranch);
  }

  public boolean isLeaf() {
    return this.isLeaf;
  }

  public void setLeaf(boolean isLeaf) {
    this.isLeaf = isLeaf;
    this.isBranch = (!isLeaf);
  }

  public boolean isRoot() {
    return this.isRoot;
  }

  public void setRoot(boolean isRoot) {
    this.isRoot = isRoot;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getOnClick() {
    return getAttributeExt("onclick");
  }

  public void setOnClick(String onClick) {
    setAttribute("onclick", onClick);
  }

  public String getOnContextMenu() {
    return getAttributeExt("oncontextmenu");
  }

  public void setOnContextMenu(String onContextMenu) {
    setAttribute("oncontextmenu", onContextMenu);
  }

  public String getOnMouseOut() {
    return getAttributeExt("onmouseout");
  }

  public void setOnMouseOut(String onMouseOut) {
    setAttribute("onmouseout", onMouseOut);
  }

  public String getOnMouseOver() {
    return getAttributeExt("onmouseover");
  }

  public void setOnMouseOver(String onMouseOver) {
    setAttribute("onmouseover", onMouseOver);
  }

  public TreeItem getParent() {
    return this.parent;
  }

  public void setParent(TreeItem parent) {
    this.parent = parent;
  }

  public String getText() {
    return getInnerHTML();
  }

  public void setText(String text) {
    setInnerHTML(text);
  }

  public TreeAction getAction() {
    return this.action;
  }

  public void setAction(TreeAction action) {
    this.action = action;
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String id) {
    this.ID = id;
  }

  public String getParentID() {
    return this.ParentID;
  }

  public void setParentID(String parentID) {
    this.ParentID = parentID;
  }

  public boolean isExpanded() {
    return this.isExpanded;
  }

  public void setExpanded(boolean isExpanded) {
    this.isExpanded = isExpanded;
  }

  public boolean isLast() {
    return this.isLast;
  }

  public void setLast(boolean isLast) {
    this.isLast = isLast;
  }

  public boolean isLazy() {
    return this.lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  public boolean isResizeable() {
    return this.resizeable;
  }

  public void setResizeable(boolean resizeable) {
    this.resizeable = resizeable;
  }

  public String getLevelStr() {
    return this.levelStr;
  }

  public void setLevelStr(String levelStr) {
    this.levelStr = levelStr;
  }

  public DataRow getData() {
    if ((this.isRoot) && (this.data == null)) {
      return null;
    }
    return this.data;
  }

  public void setData(DataRow data) {
    this.data = data;
  }
}