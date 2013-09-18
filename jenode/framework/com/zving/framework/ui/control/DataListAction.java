package com.zving.framework.ui.control;

import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DBUtil;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.IPageEnableAction;
import com.zving.framework.ui.html.HtmlScript;
import com.zving.framework.ui.util.PlaceHolderUtil;
import com.zving.framework.utility.HtmlUtil;
import com.zving.framework.utility.StringUtil;

public class DataListAction
  implements IPageEnableAction
{
  private DataTable DataSource;
  private String ID;
  private String TagBody;
  private boolean page;
  protected Mapx<String, Object> Params = new Mapx();
  private String method;
  private int total;
  private int pageIndex;
  private int pageSize;
  private boolean autoFill;
  private boolean autoPageSize;
  private String dragClass;
  private String listNodes;
  private String sortEnd;
  boolean TotalFlag = false;

  public String getMethod() {
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

  public String getParam(String key) {
    return this.Params.getString(key);
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
    if (this.DataSource.getDataColumn("_RowNo") == null) {
      this.DataSource.insertColumn(new DataColumn("_RowNo", 8));
    }
    for (int j = 0; j < this.DataSource.getRowCount(); j++) {
      int rowNo = this.pageIndex * this.pageSize + j + 1;
      this.DataSource.set(j, "_RowNo", new Integer(rowNo));
    }
    LangUtil.convertDataTable(this.DataSource, User.getLanguage());
  }

  public void bindData(QueryBuilder qb) {
    bindData(qb, this.page);
  }

  public void bindData(QueryBuilder qb, boolean pageFlag) {
    if (pageFlag) {
      if (!this.TotalFlag) {
        setTotal(DBUtil.getCount(qb));
      }
      bindData(qb.executePagedDataTable(this.pageSize, this.pageIndex));
    } else {
      bindData(qb.executeDataTable());
    }
  }

  public String getHtml() {
    StringBuilder sb = new StringBuilder();
    sb.append("<!--_ZVING_DATALIST_START_").append(this.ID).append("-->");
    sb.append("<input type=\"hidden\" id=\"").append(this.ID).append("\" method=\"").append(this.method).append("\"");
    if (this.page) {
      sb.append(" page=\"true\"");
    }
    if (this.pageSize > 0) {
      sb.append(" size=\"").append(this.pageSize).append("\"");
    }
    if (this.autoFill) {
      sb.append(" autofill=\"true\"");
    }
    if (this.autoPageSize) {
      sb.append(" autopagesize=\"true\"");
    }
    if (this.dragClass != null) {
      sb.append(" dragclass=\"").append(this.dragClass).append("\"");
    }
    if (this.listNodes != null) {
      sb.append(" listnodes=\"").append(this.listNodes).append("\"");
    }
    if (this.sortEnd != null) {
      sb.append(" sortend=\"").append(this.sortEnd).append("\"");
    }
    sb.append("/>");
    sb.append(HtmlUtil.replaceWithDataTable(this.DataSource, this.TagBody, true, false));
    HtmlScript script = new HtmlScript();
    script.setAttribute("ztype", "DataList");
    script.setInnerHTML(getScript());
    sb.append(script.getOuterHtml());
    sb.append("<!--_ZVING_DATALIST_END_").append(this.ID).append("-->");
    return sb.toString();
  }

  public String getScript() {
    Boolean isXMLHttpRequest = Boolean.valueOf(false);
    if (this.Params.get("_ZVING_SIZE") != null)
    {
      isXMLHttpRequest = Boolean.valueOf(true);
    }

    StringBuilder sb = new StringBuilder();

    if (!isXMLHttpRequest.booleanValue()) {
      sb.append("function DataList_").append(this.ID).append("_Init(afterInit){");

      sb.append("var dl = new Zving.DataList(document.getElementById('").append(this.ID).append("'));");
      sb.append("dl.setParam('").append("_ZVING_ID").append("','").append(this.ID).append("');");
      for (String k : this.Params.keySet()) {
        Object v = this.Params.get(k);
        if ((!k.equals("_ZVING_TAGBODY")) && (v != null)) {
          sb.append("dl.setParam('").append(k).append("',\"").append(StringUtil.javaEncode(v.toString())).append("\");");
        }

      }

      if (this.page) {
        PageBarTag tag = new PageBarTag();
        tag.action = this;
      }
      sb.append("dl.setParam('").append("_ZVING_PAGE").append("',").append(this.page).append(");");
      if (StringUtil.isNotEmpty(this.dragClass)) {
        sb.append("dl.setParam('").append("_ZVING_DRAGCLASS").append("','").append(this.dragClass).append("');");
      }
      if (StringUtil.isNotEmpty(this.sortEnd)) {
        sb.append("dl.setParam('").append("_ZVING_SORTEND").append("','").append(this.sortEnd).append("');");
      }
      sb.append("var TagBody = \"").append(StringUtil.htmlEncode(getTagBody().replaceAll("\\s+", " "))).append("\";");
      sb.append("dl.setParam('_ZVING_TAGBODY', TagBody);");
      sb.append("if(afterInit){afterInit();}");

      sb.append("}");
    }

    sb.append("function DataList_").append(this.ID).append("_Update(){");
    sb.append("var dl = $('#").append(this.ID).append("').getComponent();");
    sb.append("if(!dl){return;}");

    sb.append("dl.setParam('").append("_ZVING_PAGEINDEX").append("',").append(this.pageIndex).append(");");
    sb.append("dl.setParam('").append("_ZVING_PAGETOTAL").append("',").append(this.total).append(");");
    sb.append("dl.setParam('").append("_ZVING_SIZE").append("',").append(this.pageSize).append(");");

    sb.append("}");

    sb.append("DataList_").append(this.ID).append("_Update();");

    if (!isXMLHttpRequest.booleanValue()) {
      sb.append("Zving.Page.onLoad(function(){DataList_").append(this.ID).append("_Init(DataList_").append(this.ID).append("_Update);}, 8);");
    }

    String content = sb.toString();
    content = PlaceHolderUtil.prepare4Script(content);
    return content;
  }

  public String getTagBody() {
    return this.TagBody;
  }

  public void setTagBody(String tagBody) {
    this.TagBody = tagBody;
  }

  public DataTable getDataSource() {
    return this.DataSource;
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String id) {
    this.ID = id;
  }

  public boolean isPage() {
    return this.page;
  }

  public void setPage(boolean page) {
    this.page = page;
  }

  public int getTotal() {
    return this.total;
  }

  public void setTotal(int total) {
    this.total = total;
    if (this.pageIndex > Math.ceil(total * 1.0D / this.pageSize)) {
      this.pageIndex = new Double(Math.floor(total * 1.0D / this.pageSize)).intValue();
    }
    this.TotalFlag = true;
  }

  public void setTotal(QueryBuilder qb) {
    if (this.pageIndex == 0)
      setTotal(DBUtil.getCount(qb));
  }

  public int getPageIndex()
  {
    return this.pageIndex;
  }

  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public int getPageSize()
  {
    return this.pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
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

  public String getDragClass() {
    return this.dragClass;
  }

  public void setDragClass(String dragClass) {
    this.dragClass = dragClass;
  }

  public String getListNodes() {
    return this.listNodes;
  }

  public void setListNodes(String listNodes) {
    this.listNodes = listNodes;
  }

  public String getSortEnd() {
    return this.sortEnd;
  }

  public void setSortEnd(String sortEnd) {
    this.sortEnd = sortEnd;
  }
}