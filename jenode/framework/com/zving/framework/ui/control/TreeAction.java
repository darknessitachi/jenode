package com.zving.framework.ui.control;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.impl.DefaultFunctionMapper;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.html.HtmlDt;
import com.zving.framework.ui.html.HtmlScript;
import com.zving.framework.ui.util.HttpVariableResolverEx;
import com.zving.framework.ui.util.PlaceHolder;
import com.zving.framework.ui.util.PlaceHolderUtil;
import com.zving.framework.ui.zhtml.ZhtmlManagerContext;
import com.zving.framework.utility.FastStringBuilder;
import com.zving.framework.utility.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class TreeAction
{
  private String rootIcon = "icons/extra/icon_tree10.gif";

  private String leafIcon = "icons/extra/icon_tree09.gif";

  private String branchIcon = "icons/extra/icon_tree09.gif";
  private String onClick;
  private String onMouseOver;
  private String onMouseOut;
  private String onContextMenu;
  private ArrayList<TreeItem> items = new ArrayList();
  private DataTable DataSource;
  private String IdentifierColumnName = "ID";

  private String ParentIdentifierColumnName = "ParentID";
  private String rootText;
  private String ID;
  private int level;
  private int parentLevel;
  private boolean lazy;
  private boolean customscrollbar;
  private boolean lazyLoad;
  private boolean expand;
  private String style;
  private String TagBody;
  private TreeItem root;
  private int pageSize;
  private int pageIndex;
  private int total;
  protected Mapx<String, Object> Params = new Mapx();
  private List<PlaceHolder> list;
  private String method;

  public String getMethod()
  {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Mapx<String, Object> getParams() {
    return this.Params;
  }

  public void setParams(Mapx<String, Object> params) {
    this.Params = params;
  }

  public String getParam(String key)
  {
    return this.Params.getString(key);
  }

  public void setTemplate(HtmlDt p) {
    this.onMouseOver = p.getAttribute("onMouseOver");
    this.onMouseOut = p.getAttribute("onMouseOut");
    this.onClick = p.getAttribute("onClick");
    this.onContextMenu = p.getAttribute("onContextMenu");
    p.removeAttribute("onClick");
    p.removeAttribute("onContextMenu");
    this.list = PlaceHolderUtil.parse(p.getOuterHtml());
  }

  public ArrayList<TreeItem> getItemList() {
    return this.items;
  }

  public TreeItem getItem(int index)
  {
    return (TreeItem)this.items.get(index);
  }

  public int getItemSize()
  {
    return this.items.size();
  }

  public void addItem(TreeItem item)
  {
    this.items.add(item);
  }

  public void addItem(TreeItem item, int index)
  {
    this.items.add(index, item);
  }

  public void bindData(DataTable dt) {
    this.DataSource = dt;
    try {
      bindData();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void bindData(DAOSet<?> set) {
    bindData(set.toDataTable());
  }

  private void bindData() throws Exception {
    if (this.DataSource == null) {
      throw new RuntimeException("DataSource can't be empty");
    }

    this.items.clear();

    this.root = new TreeItem();
    this.root.setID("_TreeRoot");
    this.root.setParentID("");
    this.root.setRoot(true);
    this.root.setText(this.rootText);
    this.root.setAction(this);
    this.root.setLevel(0);

    this.root.setAttribute("onMouseOver", this.onMouseOver);
    this.root.setAttribute("onContextMenu", this.onContextMenu);
    this.root.setAttribute("onClick", this.onClick);
    this.root.setAttribute("onMouseOut", this.onMouseOut);
    this.root.setAttribute("treenodetype", "root");

    this.items.add(this.root);

    Mapx map = new Mapx();
    for (int i = 0; i < this.DataSource.getRowCount(); i++) {
      String id = this.DataSource.getString(i, this.IdentifierColumnName);
      String pid = this.DataSource.getString(i, this.ParentIdentifierColumnName);
      map.put(id, pid);
    }
    try {
      TreeItem last = null;
      for (int i = 0; i < this.DataSource.getRowCount(); i++) {
        DataRow dr = this.DataSource.getDataRow(i);
        String id = dr.getString(this.IdentifierColumnName);
        String parentID = dr.getString(this.ParentIdentifierColumnName);
        if ((StringUtil.isEmpty(parentID)) || (!map.containsKey(parentID)) || (parentID.equals(id))) {
          TreeItem item = new TreeItem();
          item.setData(dr);
          item.parseHtml(getItemInnerHtml(dr));
          item.setAction(this);
          item.setID(dr.getString(this.IdentifierColumnName));
          item.setParentID(parentID);
          if (this.lazyLoad) {
            item.setLevel(this.parentLevel + 1);
            item.setLevelStr((String)getParams().get("LevelStr"));
          } else {
            item.setLevel(1);
          }
          item.setParent(this.root);
          this.items.add(item);
          addChild(item);
          last = item;
        }
      }

      if (last != null)
        last.setLast(true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addChild(TreeItem parent) throws Exception {
    boolean childFlag = false;
    TreeItem last = null;
    int index = -1;
    for (int i = this.items.size() - 1; i >= 0; i--) {
      if (this.items.get(i) == parent) {
        index = i;
        break;
      }
    }
    ArrayList list = new ArrayList();
    for (int i = 0; i < this.DataSource.getRowCount(); i++) {
      DataRow dr = this.DataSource.getDataRow(i);
      String pid = dr.getString(this.ParentIdentifierColumnName);
      String id = dr.getString(this.IdentifierColumnName);
      if ((parent.getID().equals(pid)) && (!StringUtil.isEmpty(id)) && (!id.equals(pid))) {
        childFlag = true;
        if ((parent.getLevel() >= this.level) && ((!this.lazyLoad) || (!this.expand))) {
          parent.setLazy(this.lazy);
          parent.setExpanded(false);
          parent.setBranch(childFlag);
          return;
        }
        TreeItem item = new TreeItem();
        item.setData(dr);
        item.parseHtml(getItemInnerHtml(dr));
        item.setAction(this);
        item.setID(dr.getString(this.IdentifierColumnName));
        item.setParentID(parent.getID());
        item.setLevel(parent.getLevel() + 1);
        item.setParent(parent);
        if (this.lazyLoad) {
          item.setLevelStr((String)getParams().get("LevelStr"));
        }
        list.add(item);
        this.items.add(index + list.size(), item);
        last = item;
      }
    }
    for (int i = 0; i < list.size(); i++) {
      addChild((TreeItem)list.get(i));
    }
    if (last != null) {
      last.setLast(true);
    }
    if ((!this.lazy) && (parent.getLevel() + 1 == this.level)) {
      parent.setExpanded(false);
    }
    parent.setBranch(childFlag);
  }

  public String getItemInnerHtml(DataRow dr) {
    FastStringBuilder sb = new FastStringBuilder();
    HttpVariableResolverEx vr = new HttpVariableResolverEx();
    vr.setData(dr);
    for (PlaceHolder p : this.list) {
      if (p.isString)
        sb.append(p.Value);
      else {
        try {
          Object v = ZhtmlManagerContext.getInstance().getEvaluator()
            .evaluate(p.Value, Object.class, vr, DefaultFunctionMapper.getInstance());
          if (v == null)
            sb.append(p.Value);
          else
            sb.append(v);
        }
        catch (ExpressionException e) {
          e.printStackTrace();
        }
      }
    }

    return sb.toStringAndClose();
  }

  public String getHtml() {
    StringBuilder htmlSB = new StringBuilder();
    StringBuffer jsDataSB = new StringBuffer();
    String styleStr = "";
    if (StringUtil.isNotEmpty(this.style)) {
      styleStr = styleStr + this.style;
    }
    if (!this.lazyLoad) {
      htmlSB.append("<div id='").append(this.ID).append("_outer' class='treeContainer' style='-moz-user-select:none;")
        .append(styleStr).append("'><div ztype='_Tree' onselectstart='stopEvent(event);' id='").append(this.ID)
        .append("' method='").append(this.method).append("' class='z-tree'");
      if (this.customscrollbar)
        htmlSB.append(" customscrollbar='true'");
      else {
        htmlSB.append(" customscrollbar='false'");
      }
      htmlSB.append("><table><tr><td>");
    }

    for (int i = 0; i < this.items.size(); i++) {
      if ((!this.lazyLoad) || (getItem(i).getLevel() > this.parentLevel))
      {
        if ((i != 0) && (getItem(i).getLevel() > getItem(i - 1).getLevel())) {
          if ((getItem(i).getLevel() == this.level) && (!this.lazyLoad) && (!this.lazy))
            htmlSB.append("<dd style=\"display:none\">");
          else {
            htmlSB.append("<dd>");
          }
          jsDataSB.append(",children:[");
        }

        htmlSB.append("<dl>");
        jsDataSB.append("{");

        htmlSB.append(((TreeItem)this.items.get(i)).getOuterHtml());
        jsDataSB.append("text:\\\"" + StringUtil.javaEncode(((TreeItem)this.items.get(i)).InnerHTML) + "\\\"");

        if ((i != this.items.size() - 1) && (getItem(i).getLevel() == getItem(i + 1).getLevel())) {
          htmlSB.append("<dd style=\"display:none\"></dd>");
          htmlSB.append("</dl>");
          jsDataSB.append("}");
        } else if ((i == this.items.size() - 1) || (getItem(i).getLevel() > getItem(i + 1).getLevel())) {
          htmlSB.append("<dd style=\"display:none\"></dd>");
          htmlSB.append("</dl>");
          jsDataSB.append("}");
        }
        if ((i != this.items.size() - 1) && (getItem(i).getLevel() > getItem(i + 1).getLevel())) {
          for (int j = 0; j < getItem(i).getLevel() - getItem(i + 1).getLevel(); j++) {
            htmlSB.append("</dd>");
            jsDataSB.append("]");
            htmlSB.append("</dl>");
            jsDataSB.append("}");
          }
        } else if (i == this.items.size() - 1) {
          int j = 0; for (int len = getItem(i).getLevel() - this.parentLevel; j < len; j++) {
            htmlSB.append("</dd>");
            jsDataSB.append("]");
            if ((!this.lazyLoad) || (j + 1 != len))
            {
              htmlSB.append("</dl>");
              jsDataSB.append("}");
            }
          }
        }
      }
    }

    if (!this.lazyLoad) {
      htmlSB.append("</td></tr></table></div></div>\n\r");
      HtmlScript script = new HtmlScript();
      if (!this.lazyLoad)
        script.setInnerHTML(getScript(jsDataSB.toString()));
      else {
        script.setInnerHTML(getScript4lazyLoad(jsDataSB.toString()));
      }
      htmlSB.append(script.getOuterHtml());
    }
    return htmlSB.toString();
  }

  public String getScript(String jsData) {
    Boolean isXMLHttpRequest = Boolean.valueOf(false);
    if (this.Params.get("_ZVING_SIZE") != null)
    {
      isXMLHttpRequest = Boolean.valueOf(true);
    }

    StringBuilder sb = new StringBuilder();

    if (!isXMLHttpRequest.booleanValue()) {
      sb.append("function Tree_").append(this.ID).append("_Init(afterInit){");
      sb.append("var tree = new Zving.Tree(document.getElementById('").append(this.ID).append("'));");
      for (String k : this.Params.keySet()) {
        Object v = this.Params.get(k);
        if ((!k.equals("_ZVING_TAGBODY")) && (v != null)) {
          sb.append("tree.setParam('").append(k).append("',\"").append(StringUtil.javaEncode(v.toString())).append("\");");
        }
      }
      if (this.style != null) {
        sb.append("tree.setParam('").append("_ZVING_TREE_STYLE").append("',\"").append(this.style).append("\");");
      }
      sb.append("tree.setParam('").append("_ZVING_TREE_PARENTCOLUMN").append("','").append(this.ParentIdentifierColumnName)
        .append("');");
      sb.append("tree.setParam('").append("_ZVING_TREE_LEVEL").append("',").append(this.level).append(");");
      sb.append("tree.setParam('").append("_ZVING_TREE_LAZY").append("',\"").append(this.lazy).append("\");");
      sb.append("tree.setParam('").append("_ZVING_TREE_EXPAND").append("',\"").append(this.expand).append("\");");
      sb.append("var _tagBody = \"").append(StringUtil.htmlEncode(getTagBody().replaceAll("\\s+", " "))).append("\";");
      sb.append("tree.setParam('_ZVING_TAGBODY', _tagBody);");

      StringUtil.isNotEmpty(jsData);

      sb.append("if(afterInit){afterInit();}");
      sb.append("}");
    }

    sb.append("function Tree_").append(this.ID).append("_Update(){");
    sb.append("var tree = $('#").append(this.ID).append("').getComponent();");
    sb.append("if(!tree){return;}");

    sb.append("tree.setParam('").append("_ZVING_TREE_PARENTCOLUMN").append("','").append(this.ParentIdentifierColumnName).append("');");
    sb.append("tree.setParam('").append("_ZVING_TREE_LEVEL").append("',").append(this.level).append(");");
    sb.append("tree.setParam('").append("_ZVING_TREE_LAZY").append("',\"").append(this.lazy).append("\");");

    sb.append("tree.setParam('").append("_ZVING_PAGEINDEX").append("',").append(this.pageIndex).append(");");
    sb.append("tree.setParam('").append("_ZVING_PAGETOTAL").append("',").append(this.total).append(");");
    sb.append("tree.setParam('").append("_ZVING_SIZE").append("',").append(this.pageSize).append(");");
    if ((this.pageIndex == 0) && (this.pageSize > 0) && (this.total > this.pageSize)) {
      sb.append("tree.loadOtherPages();");
    }
    sb.append("}");

    sb.append("Tree_").append(this.ID).append("_Update();");

    if (!isXMLHttpRequest.booleanValue()) {
      sb.append("Zving.Page.onLoad(function(){Tree_").append(this.ID).append("_Init(Tree_").append(this.ID).append("_Update);}, 7);");
    }

    String content = sb.toString();
    content = PlaceHolderUtil.prepare4Script(content);
    return content;
  }

  public String getScript4lazyLoad(String jsData) {
    StringBuilder sb = new StringBuilder();

    sb.append("Tree_").append(this.ID).append("_Load = function(afterInit){");
    sb.append("var tree = Node.getComponent(document.getElementById('").append(this.ID).append("'));");

    StringUtil.isNotEmpty(jsData);

    sb.append("}");

    sb.append("Tree_").append(this.ID).append("_Load();");

    String content = sb.toString();
    content = PlaceHolderUtil.prepare4Script(content);
    return content;
  }

  public String getRootText() {
    return this.rootText;
  }

  public void setRootText(String rootText)
  {
    this.rootText = rootText;
  }

  public void setRootIcon(String iconFileName)
  {
    this.rootIcon = iconFileName;
  }

  public void setLeafIcon(String iconFileName)
  {
    this.leafIcon = iconFileName;
  }

  public void setBranchIcon(String iconFileName)
  {
    this.branchIcon = iconFileName;
  }

  public String getBranchIcon()
  {
    return this.branchIcon;
  }

  public String getLeafIcon() {
    return this.leafIcon;
  }

  public String getRootIcon() {
    return this.rootIcon;
  }

  public String getIdentifierColumnName() {
    return this.IdentifierColumnName;
  }

  public void setIdentifierColumnName(String identifierColumnName)
  {
    this.IdentifierColumnName = identifierColumnName;
  }

  public String getParentIdentifierColumnName() {
    return this.ParentIdentifierColumnName;
  }

  public void setParentIdentifierColumnName(String parentIdentifierColumnName)
  {
    this.ParentIdentifierColumnName = parentIdentifierColumnName;
  }

  public boolean isLazy() {
    return this.lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  public boolean isCustomscrollbar() {
    return this.customscrollbar;
  }

  public void setCustomscrollbar(boolean customscrollbar) {
    this.customscrollbar = customscrollbar;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String id) {
    this.ID = id;
  }

  public String getOnClick() {
    return this.onClick;
  }

  public void setOnClick(String onClick) {
    this.onClick = onClick;
  }

  public String getOnContextMenu() {
    return this.onContextMenu;
  }

  public void setOnContextMenu(String onContextMenu) {
    this.onContextMenu = onContextMenu;
  }

  public String getOnMouseOut() {
    return this.onMouseOut;
  }

  public void setOnMouseOut(String onMouseOut) {
    this.onMouseOut = onMouseOut;
  }

  public String getOnMouseOver() {
    return this.onMouseOver;
  }

  public void setOnMouseOver(String onMouseOver) {
    this.onMouseOver = onMouseOver;
  }

  public String getStyle() {
    return this.style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getTagBody() {
    return this.TagBody;
  }

  public void setTagBody(String tagBody) {
    this.TagBody = tagBody;
  }

  public boolean isLazyLoad() {
    return this.lazyLoad;
  }

  public void setLazyLoad(boolean lazyLoad) {
    this.lazyLoad = lazyLoad;
  }

  public int getParentLevel() {
    return this.parentLevel;
  }

  public void setParentLevel(int parentLevel) {
    this.parentLevel = parentLevel;
  }

  public boolean isExpand() {
    return this.expand;
  }

  public void setExpand(boolean expand) {
    this.expand = expand;
  }

  public DataTable getDataSource() {
    return this.DataSource;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public int getPageIndex() {
    return this.pageIndex;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getTotal() {
    return this.total;
  }
}