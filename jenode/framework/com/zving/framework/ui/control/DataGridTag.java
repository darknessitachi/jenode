package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.DataTable;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.html.HtmlTable;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataGridTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String method;
  private String sql;
  private String id;
  private boolean page = true;
  private int size;
  private boolean multiSelect = true;

  private boolean autoFill = true;

  private boolean autoPageSize = false;

  private boolean scroll = true;

  private boolean lazy = false;
  private int cacheSize;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "datagrid";
  }

  public int doStartTag() throws TemplateRuntimeException {
    this.pageContext.setKeepExpression(true);
    return 2;
  }

  public int doEndTag() throws TemplateRuntimeException {
    this.pageContext.setKeepExpression(false);
    return 6;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    try {
      if ((StringUtil.isEmpty(this.method)) && (StringUtil.isEmpty(this.sql))) {
        throw new RuntimeException("DataGrid's action and SQL cann't be empty at the same time");
      }

      DataGridAction dga = new DataGridAction();
      dga.setMethod(this.method);
      dga.setTagBody(content);

      dga.setID(this.id);
      dga.setPageFlag(this.page);
      dga.setMultiSelect(this.multiSelect);
      dga.setAutoFill(this.autoFill);
      dga.setAutoPageSize(this.autoPageSize);
      dga.setScroll(this.scroll);
      dga.setCacheSize(this.cacheSize);
      dga.setLazy(this.lazy);

      HtmlTable table = new HtmlTable();
      table.parseHtml(content);
      dga.setTemplate(table);
      dga.parse();

      if (this.page) {
        dga.setPageIndex(0);
        if (StringUtil.isNotEmpty(dga.getParam("_ZVING_PAGEINDEX"))) {
          dga.setPageIndex(Integer.parseInt(dga.getParam("_ZVING_PAGEINDEX")));
        }
        if (dga.getPageIndex() < 0) {
          dga.setPageIndex(0);
        }
        if (this.autoPageSize) {
          this.size = 30;
        }
        dga.setPageSize(this.size);
      }

      if (this.lazy) {
        dga.bindData(new DataTable());
      } else {
        IMethodLocator m = MethodLocatorUtil.find(this.method);
        PrivCheck.check(m);

        dga.setParams(Current.getRequest());
        dga.Response = Current.getResponse();

        if (StringUtil.isNotEmpty(this.sql)) {
          this.method = "com.zving.framework.ui.control.DataGridPage.sqlBind";
          dga.getParams().put("_ZVING_DATAGRID_SQL", this.sql);
        }
        m.execute(new Object[] { dga });
      }
      this.pageContext.setAttribute(this.id + "_ZVING_ACTION", dga);
      getPreviousOut().write(dga.getHtml());
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return 6;
  }

  public String getMethod() {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSql() {
    return this.sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public boolean isPage() {
    return this.page;
  }

  public void setPage(boolean page) {
    this.page = page;
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public boolean isMultiSelect() {
    return this.multiSelect;
  }

  public void setMultiSelect(boolean multiSelect) {
    this.multiSelect = multiSelect;
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
    return this.scroll;
  }

  public void setScroll(boolean scroll) {
    this.scroll = scroll;
  }

  public int getCacheSize() {
    return this.cacheSize;
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
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
    list.add(new TagAttr("autoFill", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("autoPageSize", 8));
    list.add(new TagAttr("cacheSize", 8));
    list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("method"));
    list.add(new TagAttr("multiSelect", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("page", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("scroll", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("size", 8));
    list.add(new TagAttr("sql"));
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