package com.zving.framework.ui.control;

import com.zving.framework.Config;
import com.zving.framework.Constant;
import com.zving.framework.ResponseData;
import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.collection.Treex;
import com.zving.framework.collection.Treex.TreeIterator;
import com.zving.framework.collection.Treex.TreeNode;
import com.zving.framework.data.DBUtil;
import com.zving.framework.data.DataCollection;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.impl.DefaultFunctionMapper;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.HttpVariableResolver;
import com.zving.framework.ui.IPageEnableAction;
import com.zving.framework.ui.html.HtmlScript;
import com.zving.framework.ui.html.HtmlTD;
import com.zving.framework.ui.html.HtmlTR;
import com.zving.framework.ui.html.HtmlTable;
import com.zving.framework.ui.util.HttpVariableResolverEx;
import com.zving.framework.ui.util.PlaceHolder;
import com.zving.framework.ui.util.PlaceHolderUtil;
import com.zving.framework.ui.zhtml.ZhtmlManagerContext;
import com.zving.framework.utility.FastStringBuilder;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.Primitives;
import com.zving.framework.utility.StringFormat;
import com.zving.framework.utility.StringUtil;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataGridAction
  implements IPageEnableAction
{
  public static final String TreeLevelField = "_TreeLevel";
  public static final String TreeNodeHasChild = "_hasChild";
  protected String ID;
  private boolean MultiSelect = true;

  private boolean autoFill = true;

  private boolean autoPageSize = false;

  private boolean Scroll = true;

  private boolean Lazy = false;
  private int cacheSize;
  protected HtmlTable Template;
  protected int PageSize;
  protected int PageIndex;
  private int Total;
  private boolean PageFlag;
  protected boolean SortFlag;
  private boolean treeLazyLoad;
  protected boolean TreeFlag;
  protected int TreeStartLevel = 999;
  protected boolean TreeLazy;
  private int parentLevel;
  private String ParentID;
  protected String IdentifierColumnName = "ID";
  protected String ParentIdentifierColumnName = "ParentID";

  protected StringBuilder SortString = new StringBuilder();
  protected String Method;
  protected String TagBody;
  protected DataTable DataSource;
  protected HtmlTable Table;
  protected Mapx<String, Object> Params = new Mapx();
  public ResponseData Response;
  String style1;
  String style2;
  String class1;
  String class2;
  String templateHtml;
  HtmlTR headTR = null;

  HtmlTR templateTR = null;

  HtmlTR editTR = null;

  HtmlTR pageBarTR = null;

  boolean isSimplePageBar = false;

  boolean TotalFlag = false;

  private static Pattern sortPattern = Pattern.compile("[\\w\\,\\s]*", 34);

  public void parse()
  {
    this.Table = new HtmlTable();
    this.Table.setAttributes(this.Template.getAttributes());

    for (int i = 0; i < this.Template.Children.size(); i++) {
      HtmlTR tr = (HtmlTR)this.Template.Children.get(i);

      if ("Head".equalsIgnoreCase(tr.getAttribute("ztype"))) {
        this.headTR = tr;
      } else if ("PageBar".equalsIgnoreCase(tr.getAttribute("ztype"))) {
        this.pageBarTR = tr;
      } else if ("SimplePageBar".equalsIgnoreCase(tr.getAttribute("ztype"))) {
        this.pageBarTR = tr;
        this.isSimplePageBar = true;
      } else if ("Edit".equalsIgnoreCase(tr.getAttribute("ztype"))) {
        this.editTR = tr;
        this.editTR.setAttribute("style", "display:none");
      } else {
        this.style1 = tr.getAttribute("style1");
        this.style2 = tr.getAttribute("style2");
        this.class1 = tr.getAttribute("class1");
        this.class2 = tr.getAttribute("class2");
        tr.removeAttribute("style1");
        tr.removeAttribute("style2");
        tr.removeAttribute("class1");
        tr.removeAttribute("class2");
        this.templateTR = tr;
      }
    }
    if (this.headTR == null) {
      this.headTR = this.Template.getTR(0);
    }
    this.headTR.setAttribute("onselectstart", "return false;");
    this.Table.addTR(this.headTR);

    String str = this.Params.getString("_ZVING_SORTSTRING");
    boolean firstSortFieldFlag = true;
    boolean emptyFlag = true;
    Mapx sortMap = new Mapx();
    if ((StringUtil.isNotEmpty(str)) && (StringUtil.verify(str, "String"))) {
      if ("_ZVING_NULL".equals(str)) {
        str = "";
      }
      this.SortString.append(str);
      String[] arr = str.split("\\,");
      for (int i = 0; i < arr.length; i++) {
        if (arr[i].indexOf(' ') > 0) {
          String[] arr2 = arr[i].split("\\s");
          sortMap.put(arr2[0].trim().toLowerCase(), arr2[1].trim());
        } else {
          sortMap.put(arr[i].trim().toLowerCase(), "");
        }
      }
      emptyFlag = false;
    }
    for (int i = 0; i < this.headTR.Children.size(); i++) {
      HtmlTD td = this.headTR.getTD(i);

      String ztype = td.getAttribute("ztype");
      if ("Tree".equalsIgnoreCase(ztype)) {
        this.TreeFlag = true;
        try {
          this.TreeStartLevel = Integer.parseInt(td.getAttribute("startLevel"));
          if (this.TreeStartLevel <= 0)
            this.TreeStartLevel = 999;
        }
        catch (Exception localException) {
        }
        try {
          this.TreeLazy = "true".equals(td.getAttribute("treelazy"));
          this.Table.setAttribute("treelazy", "true");
        } catch (Exception localException1) {
        }
        try {
          String idcolumn = td.getAttribute("idcolumn");
          if (StringUtil.isNotEmpty(idcolumn)) {
            this.IdentifierColumnName = idcolumn;
          }
          String parentidcolumn = td.getAttribute("parentidcolumn");
          if (StringUtil.isNotEmpty(parentidcolumn))
            this.ParentIdentifierColumnName = parentidcolumn;
        }
        catch (Exception localException2) {
        }
      }
      String sortField = td.getAttribute("sortField");
      String direction = td.getAttribute("direction");
      if (StringUtil.isNotEmpty(sortField)) {
        this.SortFlag = true;
        if (emptyFlag) {
          if (StringUtil.isNotEmpty(direction)) {
            if (!firstSortFieldFlag) {
              this.SortString.append(",");
            }
            this.SortString.append(sortField);
            this.SortString.append(" ");
            this.SortString.append(direction);
            firstSortFieldFlag = false;
          } else {
            direction = "";
          }
        } else {
          direction = (String)sortMap.get(sortField.toLowerCase());
          if (StringUtil.isEmpty(direction)) {
            direction = "";
          }
          td.setAttribute("direction", direction);
        }
      }
      if (StringUtil.isNotEmpty(sortField)) {
        td.setAttribute("class", "dg_sortTh");
        td.setAttribute("onClick", "Zving.DataGrid.onSort(this);");
        StringBuilder sb = new StringBuilder();
        sb.append("<span style='float:left'>");
        sb.append(td.getInnerHTML());
        sb.append("</span>");
        sb.append("<img src='");
        sb.append(Config.getContextPath());
        sb.append("framework/images/blank.gif'");
        sb.append(" class='fr icon_sort");
        sb.append(direction.toUpperCase());
        sb.append("' width='12' height='12'>");
        td.setInnerHTML(sb.toString());
      }
    }

    this.Table.setAttribute("SortString", this.SortString.toString());

    this.Table.setAttribute("id", this.ID);
    this.Table.setAttribute("page", this.PageFlag+"");
    this.Table.setAttribute("size", this.PageSize+"");
    this.Table.setAttribute("method", this.Method+"");
    this.Table.setAttribute("multiselect", this.MultiSelect+"");
    this.Table.setAttribute("autofill", this.autoFill+"");
    this.Table.setAttribute("autopagesize", this.autoPageSize+"");
    this.Table.setAttribute("scroll", this.Scroll+"");
    this.Table.setAttribute("lazy", this.Lazy+"");
    this.Table.setAttribute("cachesize", this.cacheSize+"");

    this.templateHtml = this.templateTR.getOuterHtml();
    if (this.templateHtml == null)
      throw new RuntimeException("Template row not found");
  }

  private void bindData() throws Exception
  {
    if (!this.PageFlag) {
      this.Total = this.DataSource.getRowCount();
    }

    if ((this.TreeFlag) && 
      (this.DataSource.getDataColumn(this.ParentIdentifierColumnName) != null) && (this.DataSource.getDataColumn(this.IdentifierColumnName) != null)) {
      if (this.treeLazyLoad)
        this.DataSource = sortTreeDataTable(this.DataSource, this.IdentifierColumnName, this.ParentIdentifierColumnName, this.TreeStartLevel, this.ParentID);
      else {
        this.DataSource = sortTreeDataTable(this.DataSource, this.IdentifierColumnName, this.ParentIdentifierColumnName, this.TreeStartLevel);
      }

    }

    if (this.DataSource == null) {
      throw new RuntimeException("DataSource must set in bindData()");
    }

    if (this.DataSource.getDataColumn("_RowNo") == null) {
      this.DataSource.insertColumn(new DataColumn("_RowNo", 8));
      for (int j = 0; j < this.DataSource.getRowCount(); j++) {
        int rowNo = this.PageIndex * this.PageSize + j + 1;
        this.DataSource.set(j, "_RowNo", new Integer(rowNo));
      }
    }

    HttpVariableResolverEx vr = new HttpVariableResolverEx();
    LangUtil.convertDataTable(this.DataSource, User.getLanguage());
    List<PlaceHolder> list = PlaceHolderUtil.parse(this.templateHtml);
    for (int i = 0; i < this.DataSource.getRowCount(); i++) {
      vr.setData(this.DataSource.getDataRow(i));
      FastStringBuilder sb = new FastStringBuilder();
      for (PlaceHolder p : list) {
        if (p.isString) {
          sb.append(p.Value);
        }
        if (p.isExpression) {
          Object v = ZhtmlManagerContext.getInstance().getEvaluator()
            .evaluate(p.Value, Object.class, vr, DefaultFunctionMapper.getInstance());
          if (v != null)
            sb.append(v);
          else {
            sb.append("");
          }
        }
      }
      HtmlTR tr = new HtmlTR(this.Table);
      tr.parseHtml(sb.toStringAndClose());
      String className = "dg_row";
      tr.setAttribute("class", className);
      if (i % 2 == 1) {
        if (this.style1 != null) {
          tr.setAttribute("style", this.style1);
        }
        if (this.class1 != null)
          tr.setAttribute("class", className + " " + this.class1);
      }
      else {
        if (this.style2 != null) {
          tr.setAttribute("style", this.style2);
        }
        if (this.class2 != null) {
          tr.setAttribute("class", className + " " + this.class2);
        }
      }
      String clickEvent = tr.getAttribute("onclick");
      if (StringUtil.isEmpty(clickEvent)) {
        clickEvent = "";
      }
      tr.setAttribute("onclick", clickEvent);
      String dblEvent = tr.getAttribute("ondblclick");
      if (StringUtil.isNotEmpty(dblEvent)) {
        tr.setAttribute("ondblclick", dblEvent);
      }

      this.Table.addTR(tr);
    }
    if (this.editTR != null) {
      this.Table.setAttribute("editinrow", "true");
    }

    for (int i = 0; i < this.headTR.Children.size(); i++)
    {
      boolean rowNoFlag = "RowNo".equalsIgnoreCase(this.headTR.getTD(i).getAttribute("ztype"));
      if (rowNoFlag) {
        this.headTR.getTD(i).setAttribute("disabledresize", "true");
      }
      for (int j = 1; j < this.Table.Children.size(); j++) {
        int rowNo = this.PageIndex * this.PageSize + j;
        if (rowNoFlag) {
          this.Table.getTR(j).getTD(i).setInnerHTML(rowNo+"");
          this.Table.getTR(j).getTD(i).setAttribute("rowno", rowNo+"");
          this.Table.getTR(j).getTD(i).setAttribute("class", "rowNo");
        }

      }

      if ("Selector".equalsIgnoreCase(this.headTR.getTD(i).getAttribute("ztype"))) {
        this.headTR.getTD(i).setAttribute("disabledresize", "true");
        String field = this.headTR.getTD(i).getAttribute("field");
        String onSelect = this.headTR.getTD(i).getAttribute("onselect");
        if (onSelect == null) {
          onSelect = "";
        }
        if (this.MultiSelect) {
          this.headTR.getTD(i).setInnerHTML(
            "<input type='checkbox' value='*' id='" + this.ID + "_AllCheck' onclick=\"Zving.DataGrid.onAllCheckClick('" + this.ID + 
            "', this)\" autoComplete=\"off\"/>");
        }
        String type = this.MultiSelect ? "checkbox" : "radio";
        for (int j = 1; j < this.Table.Children.size(); j++) {
          this.Table.getTR(j)
            .getTD(i)
            .setInnerHTML(
            "<input type='" + type + "' name='" + this.ID + "_RowCheck' id='" + this.ID + "_RowCheck" + j + "' value='" + 
            this.DataSource.getString(j - 1, field) + "' autoComplete='off'/>");
          this.Table.getTR(j).getTD(i).setAttribute("class", "selector");
          this.Table.getTR(j).getTD(i).setAttribute("onclick", onSelect);
          this.Table.getTR(j).getTD(i).setAttribute("ondblclick", "stopEvent(event);" + onSelect);
        }
      }

      if ("Checkbox".equalsIgnoreCase(this.headTR.getTD(i).getAttribute("ztype"))) {
        this.headTR.getTD(i).setAttribute("disabledresize", "true");
        String field = this.headTR.getTD(i).getAttribute("field");
        String checkedvalue = this.headTR.getTD(i).getAttribute("checkedvalue");
        String disabled = this.headTR.getTD(i).getAttribute("disabled");
        if (checkedvalue == null) {
          checkedvalue = "Y";
        }
        if ((disabled == null) || (disabled.equalsIgnoreCase("true")))
          disabled = "disabled";
        else {
          disabled = "";
        }
        for (int j = 1; j < this.Table.Children.size(); j++) {
          String checked = checkedvalue.equals(this.DataSource.getString(j - 1, field)) ? "checked" : "";
          this.Table.getTR(j)
            .getTD(i)
            .setInnerHTML(
            "<input type='checkbox' " + disabled + " name='" + this.ID + "_" + field + "_Checkbox' id='" + this.ID + "_" + 
            field + "_Checkbox" + j + "' value='" + checkedvalue + "' " + checked + 
            " autocomplete=\"off\">");
        }
      }

      if ("DropDownList".equalsIgnoreCase(this.headTR.getTD(i).getAttribute("ztype"))) {
        String field = this.headTR.getTD(i).getAttribute("field");
        String sql = this.headTR.getTD(i).getAttribute("sql");
        String zstyle = this.headTR.getTD(i).getAttribute("zstyle");
        if (StringUtil.isEmpty(zstyle)) {
          zstyle = "width:100px";
        }
        DataTable dt = new QueryBuilder(sql, new Object[0]).executeDataTable();
        for (int j = 1; j < this.Table.Children.size(); j++) {
          StringBuilder sb = new StringBuilder();
          sb.append("<div ztype='select' disabled='true' style='display:none;").append(zstyle).append(";' name='").append(this.ID)
            .append("_").append(field).append("_DropDownList").append(j).append("' id='").append(this.ID).append("_")
            .append(field).append("_DropDownList").append(j).append("' >");
          for (int k = 0; k < dt.getRowCount(); k++) {
            String value = this.DataSource.getString(j - 1, field);
            String selected = "";
            if (value.equals(dt.getString(k, 0))) {
              selected = "selected='true'";
            }
            sb.append("<span value='").append(dt.getString(k, 0)).append("' ").append(selected).append(">")
              .append(dt.getString(k, 1)).append("</span>");
          }
          sb.append("</div>");
          this.Table.getTR(j).getTD(i).setInnerHTML(sb.toString());
        }
      }

      if ("Tree".equalsIgnoreCase(this.headTR.getTD(i).getAttribute("ztype")))
      {
        String field = this.headTR.getTD(i).getAttribute("field");
        String checkedExpr = this.headTR.getTD(i).getAttribute("checked");

        HttpVariableResolver context = new HttpVariableResolver();
        IFunctionMapper fm = ZhtmlManagerContext.getInstance().getFunctionMapper();

        for (int j = 1; j < this.Table.Children.size(); j++) {
          int level = this.DataSource.getInt(j - 1, "_TreeLevel");
          boolean hasChild = this.DataSource.getString(j - 1, "_hasChild").equals("true");

          boolean checked = false;
          if (ObjectUtil.notEmpty(checkedExpr)) {
            context.addMap(this.DataSource.getDataRow(j - 1).toCaseIgnoreMapx(), "List");
            Object checkedFlag = ZhtmlManagerContext.getInstance().getEvaluator()
              .evaluate(checkedExpr, Boolean.class, context, fm);
            checked = Primitives.getBoolean(checkedFlag);
          }

          StringBuilder sb = new StringBuilder();
          for (int k = 1; k < level; k++) {
            sb.append("<q style='padding:0 10px'></q>");
          }

          if (hasChild) {
            if ((this.treeLazyLoad) || ((!this.treeLazyLoad) && (this.TreeStartLevel == level)))
              sb.append("<img src='").append(Config.getContextPath())
                .append("framework/images/butCollapse.gif' onclick='DataGrid.treeClick(this)'/>&nbsp;");
            else
              sb.append("<img src='").append(Config.getContextPath())
                .append("framework/images/butExpand.gif' onclick='DataGrid.treeClick(this)'/>&nbsp;");
          }
          else {
            sb.append("<img src='").append(Config.getContextPath()).append("framework/images/butNoChild.gif'/>&nbsp;");
          }
          if (field != null)
          {
            String treeID = this.DataSource.getString(j - 1, field);
            sb.append("<input type='checkbox'  name='").append(this.ID).append("_TreeRowCheck' id='").append(this.ID)
              .append("_TreeRowCheck_").append(j).append("' value='").append(treeID)
              .append(checked ? "' checked='true'" : "'").append(" level='").append(level)
              .append("' onClick='treeCheckBoxClick(this);'>");
          }

          sb.append(this.Table.getTR(j).getTD(i).getInnerHTML());
          this.Table.getTR(j).setAttribute("treenodeid", this.DataSource.getString(j - 1, this.IdentifierColumnName));
          this.Table.getTR(j).getTD(i).setInnerHTML(sb.toString());
          this.Table.getTR(j).setAttribute("level", level+"");
        }

      }

      if ("true".equalsIgnoreCase(this.headTR.getTD(i).getAttribute("drag"))) {
        for (int j = 1; j < this.Table.Children.size(); j++) {
          HtmlTD td = this.Table.getTR(j).getTD(i);
          String style = td.getAttribute("style");
          if (style != null) {
            td.setAttribute("style", style);
          }
          td.setAttribute("class", "z-draggable");
        }

      }

    }

    for (int i = 0; i < this.Table.getChildren().size(); i++) {
      HtmlTR tr = this.Table.getTR(i);
      for (int j = 0; j < tr.getChildren().size(); j++) {
        HtmlTD td = tr.getTD(j);
        if (StringUtil.isEmpty(td.getInnerHTML()))
          td.setInnerHTML("&nbsp;");
      }
    }
  }

  public String getHtml()
  {
    HtmlScript script = new HtmlScript();
    script.setAttribute("ztype", "DataGrid");
    script.setInnerHTML(getScript());
    this.Table.addChild(script);
    String html = null;
    if (this.Scroll) {
      html = scrollWrap();
    }
    else {
      if ((!this.TreeFlag) && (this.PageFlag) && (this.pageBarTR != null)) {
        dealPageBar();
        this.Table.addTR(this.pageBarTR);
      }
      html = this.Table.getOuterHtml();
    }
    return html;
  }

  public String getBodyHtml() {
    HtmlScript script = new HtmlScript();
    script.setAttribute("ztype", "DataGrid");
    if (this.treeLazyLoad)
      script.setInnerHTML(getScript4lazyLoad());
    else {
      script.setInnerHTML(getScript());
    }
    this.Table.addChild(script);
    String html = null;
    if (this.treeLazyLoad) {
      this.Table.removeTR(0);
      html = this.Table.getInnerHTML();
    } else {
      html = this.Table.getOuterHtml();
    }

    return html;
  }

  public String scrollWrap()
  {
    for (int i = 0; i < this.Table.getTR(0).getChildren().size(); i++) {
      this.Table.getTR(0).getTD(i).setHead(true);
    }
    this.Table.removeAttribute("class");
    this.Table.getTR(0).removeAttribute("class");

    StringBuilder sb = new StringBuilder();
    String fw = this.Table.getAttribute("fixedWidth");
    String fh = this.Table.getAttribute("fixedHeight");

    sb.append("<div id='").append(this.ID).append("_wrap' class='z-datagrid dg_scrollable dg_nobr' ztype='_DataGridWrapper'");
    if (StringUtil.isNotEmpty(fw)) {
      sb.append(" style='width:").append(fw).append(";'");
    }
    sb.append(">");
    sb.append("<div id='").append(this.ID).append("_dock' class='dg_dock'><div id='").append(this.ID)
      .append("_dock_trigger' class='dock_trigger'></div><div id='").append(this.ID)
      .append("_dock_station' class='dock_station'></div></div>");
    sb.append("<div id='").append(this.ID).append("_wrap_head' class='dg_head'>");
    HtmlTable tmpTable = (HtmlTable)this.Table.clone();
    for (int i = tmpTable.Children.size() - 1; i > 0; i--) {
      tmpTable.removeTR(i);
    }
    tmpTable.setID(this.ID + "_head");
    tmpTable.setClassName("dg_headTable");
    tmpTable.getTR(0).setClassName("dg_headTr");
    for (int i = 0; i < tmpTable.getTR(0).getChildren().size(); i++) {
      HtmlTD td = tmpTable.getTR(0).getTD(i);
      td.InnerHTML = ("<div id='dataTable0_th" + i + "' class='dg_th'>" + td.InnerHTML + "</div>");
    }
    sb.append(tmpTable.getOuterHtml());
    sb.append("</div>");

    sb.append("<div id='").append(this.ID).append("_wrap_body' class='dg_body' style='");
    if ((StringUtil.isNotEmpty(fw)) && (fw.indexOf("%") < 0)) {
      sb.append("width:").append(fw).append(";");
    }
    if (StringUtil.isNotEmpty(fh)) {
      sb.append("height:").append(fh).append(";");
    }
    sb.append("'>");
    sb.append(this.Table.getOuterHtml());
    sb.append("</div>");

    if ((!this.TreeFlag) && (this.PageFlag) && (this.pageBarTR != null)) {
      dealPageBar();

      HtmlTable footTable = new HtmlTable();
      footTable.setAttribute("width", "100%");
      footTable.addTR(this.pageBarTR);

      sb.append("<div class='dg_foot'>");
      sb.append(footTable.getOuterHtml());
      sb.append("</div>");
    }
    sb.append("</div>");
    return sb.toString();
  }

  private void dealPageBar() {
    try {
      for (int i = this.pageBarTR.Children.size() - 1; i > 0; i--) {
        this.pageBarTR.removeTD(i);
      }
      String html = getPageBarHtmlByType();
      this.pageBarTR.getTD(0).setInnerHTML(html);
      this.pageBarTR.getTD(0).setColSpan(this.headTR.Children.size()+"");
      this.pageBarTR.getTD(0).setID("_PageBar_" + this.ID);
      this.pageBarTR.getTD(0).setAttribute("pagebartype", this.pageBarTR.getAttribute("pagebartype"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getScript() {
    Boolean isXMLHttpRequest = Boolean.valueOf(false);
    if (this.Params.get("_ZVING_SIZE") != null)
    {
      isXMLHttpRequest = Boolean.valueOf(true);
    }

    StringBuilder sb = new StringBuilder();

    if (!isXMLHttpRequest.booleanValue()) {
      sb.append("function DataGrid_").append(this.ID).append("_Init(afterInit){");
      sb.append("var dg = new Zving.DataGrid(document.getElementById('").append(this.ID).append("'));");
      sb.append("var _Zving_Arr = [];");
      HtmlTR tr = new HtmlTR(this.Table);
      try {
        tr.parseHtml(this.templateHtml);
      } catch (Exception e) {
        e.printStackTrace();
      }
      for (int i = 0; i < tr.getChildren().size(); i++) {
        sb.append("_Zving_Arr.push(\"")
          .append(StringUtil.javaEncode(tr.getTD(i).getInnerHTML()).replaceAll("script", "scr\"+\"ipt")).append("\");");
      }
      sb.append("dg.TemplateArray = _Zving_Arr;");
      if (this.editTR != null) {
        sb.append("_Zving_Arr = [];\n");
        for (int i = 0; i < this.editTR.getChildren().size(); i++) {
          sb.append("_Zving_Arr.push(\"").append(StringUtil.javaEncode(this.editTR.getTD(i).getInnerHTML())).append("\");");
        }
        sb.append("dg.EditArray = _Zving_Arr;");
      }
      sb.append("dg.setParam('").append("_ZVING_ID").append("','").append(this.ID).append("');");
      for (String key : this.Params.keySet()) {
        Object v = this.Params.get(key);

        if ((!key.equals("_ZVING_TAGBODY")) && (v != null)) {
          if (((v instanceof DataTable)) || ((v instanceof DAOSet))) {
            DataTable dt = null;
            if ((v instanceof DAOSet))
              dt = ((DAOSet)v).toDataTable();
            else {
              dt = (DataTable)v;
            }
            sb.append(DataCollection.dataTableToJS(dt));
            sb.append("var _TmpDt = new DataTable();");
            sb.append("_TmpDt.init(_Zving_Cols,_Zving_Values);");
            sb.append("dg.setParam('").append(key).append("',_TmpDt);");
          } else {
            sb.append("dg.setParam('").append(key).append("',\"").append(StringUtil.javaEncode(v.toString())).append("\");");
          }
        }
      }
      if (this.SortFlag) {
        sb.append("dg.setParam('").append("_ZVING_SORTSTRING").append("','").append(this.SortString).append("');");
      }
      sb.append("dg.setParam('").append("_ZVING_MULTISELECT").append("','" + this.MultiSelect).append("');");
      sb.append("dg.setParam('").append("_ZVING_AUTOFILL").append("','").append(this.autoFill).append("');");
      sb.append("dg.setParam('").append("_ZVING_SCROLL").append("','").append(this.Scroll).append("');");
      sb.append("dg.setParam('").append("_ZVING_LAZY").append("','").append(this.Lazy).append("');");
      if (this.cacheSize > 0) {
        sb.append("dg.setParam('").append("_ZVING_CACHESIZE").append("','").append(this.cacheSize).append("');");
      }
      sb.append("dg.setParam('").append("_ZVING_METHOD").append("','").append(this.Method).append("');");
      sb.append("dg.setParam('").append("_ZVING_PAGE").append("','").append(this.PageFlag).append("');");

      sb.append("var TagBody = \"").append(StringUtil.htmlEncode(getTagBody().replaceAll("\\s+", " "))).append("\";");
      sb.append("dg.setParam('_ZVING_TAGBODY', TagBody);");
      sb.append("if(afterInit){afterInit();}");

      sb.append("}");
    }

    sb.append("function DataGrid_").append(this.ID).append("_Update(){");
    sb.append("var dg = $('#").append(this.ID).append("').getComponent();");
    sb.append("if(!dg){return;}");
    if (this.DataSource != null) {
      if (this.PageFlag) {
        sb.append("dg.setParam('").append("_ZVING_PAGEINDEX").append("',").append(this.PageIndex).append(");");
        sb.append("dg.setParam('").append("_ZVING_PAGETOTAL").append("'," + this.Total).append(");");
        sb.append("dg.setParam('").append("_ZVING_SIZE").append("',").append(this.PageSize).append(");");
      }
      sb.append(DataCollection.dataTableToJS(this.DataSource));
      sb.append("var dt = dg.DataSource = new Zving.DataTable();");
      sb.append("dt.init(_Zving_Cols,_Zving_Values);");
    }
    sb.append("dg.restore();");
    sb.append("}");

    sb.append("DataGrid_").append(this.ID).append("_Update();");

    if (!isXMLHttpRequest.booleanValue()) {
      sb.append("Zving.Page.onLoad(function(){DataGrid_").append(this.ID).append("_Init(DataGrid_").append(this.ID).append("_Update);}, 9);");
    }

    String content = sb.toString();
    content = PlaceHolderUtil.prepare4Script(content);

    return content;
  }

  public String getScript4lazyLoad()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("function DataGrid_").append(this.ID).append("_Update(){");
    sb.append("var dg = $('#").append(this.ID).append("').getComponent();");
    sb.append("if(!dg){return;}");
    if (this.DataSource != null) {
      if (this.PageFlag) {
        sb.append("dg.setParam('").append("_ZVING_PAGEINDEX").append("',").append(this.PageIndex).append(");");
        sb.append("dg.setParam('").append("_ZVING_PAGETOTAL").append("'," + this.Total).append(");");
        sb.append("dg.setParam('").append("_ZVING_SIZE").append("',").append(this.PageSize).append(");");
      }
      sb.append(DataCollection.dataTableToJS(this.DataSource));
      sb.append("var dt = new Zving.DataTable();");
      sb.append("dt.init(_Zving_Cols,_Zving_Values);");
    }
    sb.append("dg.activateChildNodes('" + this.ParentID + "',dt);");
    sb.append("}");

    sb.append("DataGrid_").append(this.ID).append("_Update();");

    String content = sb.toString();
    Matcher matcher = Constant.PatternField.matcher(content);
    sb = new StringBuilder();
    int lastEndIndex = 0;
    while (matcher.find(lastEndIndex)) {
      sb.append(content.substring(lastEndIndex, matcher.start()));
      sb.append("$\\{");
      sb.append(matcher.group(1));
      sb.append("}");
      lastEndIndex = matcher.end();
    }
    sb.append(content.substring(lastEndIndex));

    content = sb.toString();
    matcher = Constant.PatternSpeicalField.matcher(content);
    sb = new StringBuilder();
    lastEndIndex = 0;
    while (matcher.find(lastEndIndex)) {
      sb.append(content.substring(lastEndIndex, matcher.start()));
      sb.append("${#");
      sb.append(matcher.group(1));
      sb.append("}");
      lastEndIndex = matcher.end();
    }
    sb.append(content.substring(lastEndIndex));
    return sb.toString();
  }

  public HtmlTable getTemplate() {
    return this.Template;
  }

  public void setTemplate(HtmlTable table) {
    this.Template = table;
  }

  public DataTable getDataSource() {
    return this.DataSource;
  }

  public void bindData(QueryBuilder qb) {
    bindData(qb, this.PageFlag);
  }

  public void bindData(QueryBuilder qb, boolean pageFlag) {
    if (pageFlag) {
      if (!this.TotalFlag) {
        setTotal(DBUtil.getCount(qb));
      }
      bindData(qb.executePagedDataTable(this.PageSize, this.PageIndex));
    } else {
      bindData(qb.executeDataTable());
    }
  }

  public void bindData(DataTable dt)
  {
    if ("1".equals(this.Params.get("_ExcelFlag"))) {
      String[] columnNames = (String[])this.Params.get("_ColumnNames");
      String[] widths = (String[])this.Params.get("_Widths");
      String[] columnIndexes = (String[])this.Params.get("_ColumnIndexes");
      int[] indexes = new int[columnIndexes.length];
      int i = 0;
      for (String str : columnIndexes) {
        indexes[(i++)] = Integer.parseInt(str);
      }
      Arrays.sort(indexes);
      for (i = indexes.length - 1; i >= 0; i++)
        dt.deleteColumn(indexes[i]);
      try
      {
        Class clazz = Class.forName("com.zving.framework.data.DataTableUtil");
        Method dataTableToExcel = clazz.getMethod("dataTableToExcel", new Class[] { DataTable.class, OutputStream.class, 
          String[].class, String[].class });
        dataTableToExcel.invoke(null, new Object[] { dt, (OutputStream)this.Params.get("_OutputStream"), columnNames, widths });
      } catch (Exception localException1) {
      }
    } else {
      this.DataSource = dt;
      try {
        bindData();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void bindData(DAOSet<?> set) {
    bindData(set.toDataTable());
  }

  public String getPageBarHtmlByType() {
    String pagebartypeString = this.pageBarTR.getAttribute("pagebartype");
    if (StringUtil.isEmpty(pagebartypeString)) {
      pagebartypeString = "0";
    }
    int pagebartype = Integer.valueOf(pagebartypeString).intValue();
    return getPageBarHtml(this.ID, this.Params, this.Total, this.PageIndex, this.PageSize, this.isSimplePageBar, pagebartype);
  }

  public String getPageBarHtml(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize) {
    return getPageBarHtml(id, params, total, pageIndex, pageSize, false, 0);
  }

  public String getPageBarHtml(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize, boolean simpleFlag, int type)
  {
    StringBuilder sb = new StringBuilder();
    int totalPages = new Double(Math.ceil(total * 1.0D / pageSize)).intValue();

    params.put("_ZVING_PAGETOTAL", total);
    params.remove("_ZVING_PAGEINDEX");

    String first = LangMapping.get("Framework.DataGrid.FirstPage");
    String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
    String next = LangMapping.get("Framework.DataGrid.NextPage");
    String last = LangMapping.get("Framework.DataGrid.LastPage");
    String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
    String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
    String error = LangMapping.get("Framework.DataGrid.ErrorPage");
    String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
    String pagebar = LangMapping.get("Framework.DataGrid.PageBar");
    String records = LangMapping.get("Framework.DataGrid.Records");

    sb.append("<div class='tfoot-fr' style='float:right;'><div class='pagebar'>&nbsp;");

    sb.append("<span class='first'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.firstPage('").append(id)
      .append("');\">").append(first).append("</a>").append("</span>");
    sb.append("<span class='previous'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.previousPage('").append(id)
      .append("');\">").append(prev).append("</a>").append("</span>");

    sb.append("<span class='next'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.nextPage('").append(id).append("');\">")
      .append(next).append("</a>").append("</span>");
    sb.append("<span class='last'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.lastPage('").append(id).append("');\">")
      .append(last).append("</a>").append("</span>");

    if (!simpleFlag) {
      sb.append("&nbsp;&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(id)
        .append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1).append("\"");
      sb.append(
        " onKeyUp=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Zving.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
        .append(id).append("').onclick();\">");
      sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
      sb.append(gotoEnd).append("&nbsp;");
      sb.append("<input type='button' id='_PageBar_JumpBtn_").append(id)
        .append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(id)
        .append("').value,t=Number(Zving.DataGrid.getParam('").append(id)
        .append("', Constant.PageTotal)),s=Number(Zving.DataGrid.getParam('").append(id)
        .append("', Constant.Size)),l=Math.floor(t/s)+(t%s===0?0:1);if(!/^\\d+$/.test(v)").append("||v<1||v>l){alert('")
        .append(error).append("');document.getElementById('_PageBar_Index_").append(id)
        .append("').focus();}else{var pageIndex = ($V('_PageBar_Index_").append(id).append("')-1)>0?$V('_PageBar_Index_")
        .append(id).append("')-1:0;DataGrid.gotoPage('").append(id).append("',pageIndex);}\" value='' title='")
        .append(gotoButton).append("'>");
    }
    sb.append("</div></div>");
    sb.append("<div style=\"float:left;\" class=\"tfoot-fl\">");
    sb.append("<span class=\"pageInfo_trigger\" style=\"display:none;\">");
    if (type == 2)
      sb.append("&gl;&gl;");
    else {
      sb.append(new StringFormat(records, new Object[] { "<span class=\"js_total\">" + total + "</span>" }));
    }
    sb.append("</span>");
    sb.append("<span class=\"pageInfo\">");
    sb.append(new StringFormat(pagebar, new Object[] { "<span class=\"js_total\">" + total + "</span>", "<span class=\"inline-block pageSizeGroup\"><ul class=\"sizeList\" style=\"position:absolute;\"><li onclick=\"Zving.DataGrid.changePageSize('" + 
      id + 
      "',10)\">10</li>" + "<li onclick=\"Zving.DataGrid.changePageSize('" + id + "',15)\">15</li>" + 
      "<li onclick=\"Zving.DataGrid.changePageSize('" + id + "',20)\">20</li>" + 
      "<li onclick=\"Zving.DataGrid.changePageSize('" + id + "',30)\">30</li>" + 
      "<li onclick=\"Zving.DataGrid.changePageSize('" + id + "',50)\">50</li>" + "</ul>" + "<span class=\"js_pageSize\">" + 
      pageSize + "</span>" + "</span>", "<span class=\"js_pageIndex\">" + (totalPages == 0 ? 0 : pageIndex + 1) + "</span>", 
      "<span class=\"js_totalPages\">" + totalPages + "</span>" }));
    sb.append("</span>");
    sb.append("</div><div style=\"clear:both;height:0;overflow:hidden;\"></div>");

    return sb.toString();
  }

  public int getPageIndex() {
    return this.PageIndex;
  }

  public void setPageIndex(int pageIndex) {
    this.PageIndex = pageIndex;
  }

  public int getPageSize() {
    return this.PageSize;
  }

  public void setPageSize(int pageSize) {
    this.PageSize = pageSize;
  }

  public String getParam(String key) {
    return this.Params.getString(key);
  }

  public Mapx<String, Object> getParams() {
    return this.Params;
  }

  public void setParams(Mapx<String, Object> params) {
    this.Params = params;
  }

  public boolean isPageFlag() {
    return this.PageFlag;
  }

  public void setPageFlag(boolean pageFlag) {
    this.PageFlag = pageFlag;
  }

  public boolean isSortFlag() {
    return this.SortFlag;
  }

  public void setSortFlag(boolean sortFlag) {
    this.SortFlag = sortFlag;
  }

  public HtmlTable getTable()
  {
    return this.Table;
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String id) {
    this.ID = id;
  }

  public boolean isTreeFlag() {
    return this.TreeFlag;
  }

  protected void setTreeFlag(boolean treeFlag) {
    this.TreeFlag = treeFlag;
  }

  public String getMethod() {
    return this.Method;
  }

  public void setMethod(String method) {
    this.Method = method;
  }

  public int getTotal() {
    return this.Total;
  }

  public void setTotal(int total) {
    this.Total = total;
    if (this.PageIndex > Math.ceil(this.Total * 1.0D / this.PageSize)) {
      this.PageIndex = new Double(Math.floor(this.Total * 1.0D / this.PageSize)).intValue();
    }
    this.TotalFlag = true;
  }

  public void setTotal(QueryBuilder qb) {
    if (this.PageIndex == 0)
      setTotal(DBUtil.getCount(qb));
  }

  public String getSortString()
  {
    if (this.SortString.length() == 0) {
      return "";
    }
    String str = this.SortString.toString();
    if (sortPattern.matcher(str).matches()) {
      return " order by " + this.SortString.toString();
    }
    return "";
  }

  public String getTagBody() {
    return this.TagBody;
  }

  public void setTagBody(String tagBody) {
    this.TagBody = tagBody;
  }

  public boolean isMultiSelect() {
    return this.MultiSelect;
  }

  public void setMultiSelect(boolean multiSelect) {
    this.MultiSelect = multiSelect;
  }

  public boolean isAutoFill() {
    return this.autoFill;
  }

  public void setAutoFill(boolean autoFill) {
    this.autoFill = autoFill;
  }

  public boolean isAutoPageSize() {
    return this.autoPageSize;
  }

  public void setAutoPageSize(boolean autoPageSize) {
    this.autoPageSize = autoPageSize;
  }

  public boolean isScroll() {
    return this.Scroll;
  }

  public void setScroll(boolean scroll) {
    this.Scroll = scroll;
  }

  public int getCacheSize() {
    return this.cacheSize;
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public boolean isLazy() {
    return this.Lazy;
  }

  public void setLazy(boolean lazy) {
    this.Lazy = lazy;
  }

  public boolean isTreeLazyLoad() {
    return this.treeLazyLoad;
  }

  public void setTreeLazyLoad(boolean treeLazyLoad) {
    this.treeLazyLoad = treeLazyLoad;
  }

  public int getParentLevel() {
    return this.parentLevel;
  }

  public void setParentLevel(int parentLevel) {
    this.parentLevel = parentLevel;
  }

  public String getParentID() {
    return this.ParentID;
  }

  public void setParentID(String parentID) {
    this.ParentID = parentID;
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

  public static DataTable sortTreeDataTable(DataTable dt, String identifierColumnName, String parentIdentifierColumnName, int treeStartLevel, String parentID)
  {
    if ((dt == null) || (dt.getRowCount() == 0)) {
      return dt;
    }
    if (dt.getDataColumn(identifierColumnName) == null) {
      LogUtil.warn("DataGridAction.sortTreeDataTable():ID column not found:" + identifierColumnName);
    }
    if (dt.getDataColumn(parentIdentifierColumnName) == null) {
      LogUtil.warn("DataGridAction.sortTreeDataTable():Parent column not found:" + parentIdentifierColumnName);
    }
    if (dt.getDataColumn("_TreeLevel") != null) {
      dt.deleteColumn("_TreeLevel");
    }
    dt.insertColumn(new DataColumn("_TreeLevel", 8));
    if (dt.getDataColumn("_hasChild") != null) {
      dt.deleteColumn("_hasChild");
    }
    dt.insertColumn(new DataColumn("_hasChild", 1));
    Treex tree = Treex.dataTableToTree(dt, identifierColumnName, parentIdentifierColumnName);
    Treex.TreeIterator ti = tree.iterator();
    DataTable dest = new DataTable(dt.getDataColumns(), null);
    while (ti.hasNext()) {
      Treex.TreeNode node = ti.next();
      DataRow dr = (DataRow)node.getData();
      if (dr != null) {
        dr.set("_TreeLevel", Integer.valueOf(node.getLevel()));
        dr.set("_hasChild", Boolean.valueOf(node.hasChild()));

        if (parentID != null)
        {
          if ((dr.getString(parentIdentifierColumnName).equalsIgnoreCase(parentID)) && 
            (!dr.getString(identifierColumnName).equalsIgnoreCase(parentID)))
            dest.insertRow(dr);
        }
        else if (node.getLevel() <= treeStartLevel)
        {
          dest.insertRow(dr);
        }
      }
    }
    return dest;
  }

  public static DataTable sortTreeDataTable(DataTable dt, String identifierColumnName, String parentIdentifierColumnName) {
    return sortTreeDataTable(dt, identifierColumnName, parentIdentifierColumnName, 999, null);
  }

  public static DataTable sortTreeDataTable(DataTable dt, String identifierColumnName, String parentIdentifierColumnName, int treeStartLevel)
  {
    return sortTreeDataTable(dt, identifierColumnName, parentIdentifierColumnName, treeStartLevel, null);
  }
}