package com.zving.framework.ui.tag;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.i18n.Lang;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.TemplatePrintWriter;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.HtmlUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ListTag extends AbstractTag
  implements IListTag
{
  private static final long serialVersionUID = 1L;
  public static final String ZListDataNameKey = "_Zving_ZList_Data";
  public static final String ZListItemNameKey = "_Zving_ZList_Item";
  String method;
  boolean page;
  int size;
  int rowIndex;
  String id;
  String data;
  String item;
  int count;
  int begin;
  DataRow currentRow;
  DataTable dataTable;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "list";
  }

  public int doStartTag() throws TemplateRuntimeException {
    try {
      if (ObjectUtil.notEmpty(this.method)) {
        IMethodLocator m = MethodLocatorUtil.find(this.method);
        PrivCheck.check(m);
        Mapx params = Current.getRequest();
        ListAction la = new ListAction();
        la.setParams(params);
        la.setPage(this.page);
        la.setMethod(this.method);
        la.setID(this.id);
        la.setPageSize(this.size);
        la.setTag(this);
        la.setQueryString(Current.getRequest().getQueryString());
        if (this.page) {
          la.setPageIndex(0);
          if (StringUtil.isNotEmpty(la.getParam("PageIndex"))) {
            la.setPageIndex(Integer.parseInt(la.getParam("PageIndex")) - 1);
          }
          if (la.getPageIndex() < 0) {
            la.setPageIndex(0);
          }
        }
        m.execute(new Object[] { la });
        this.dataTable = la.getDataSource();
        this.pageContext.setAttribute(this.id + "_ZVING_ACTION", la);
      } else {
        if (StringUtil.isEmpty(this.item)) {
          this.item = this.pageContext.eval("_Zving_ZList_Item");
        }
        if (StringUtil.isNotEmpty(this.data)) {
          Object obj = this.pageContext.evalExpression(this.data);
          if (obj == null) {
            return 0;
          }
          if ((obj instanceof DataTable))
            this.dataTable = ((DataTable)obj);
          else
            throw new RuntimeException(Lang.get("Framework.ZListDataAttributeMustBeDataTable"));
        }
        else {
          this.dataTable = ((DataTable)this.pageContext.evalExpression("${_Zving_ZList_Data}"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.begin = (this.begin < 0 ? 0 : this.begin);
    this.count = (this.count <= 0 ? 2147483647 : this.count);
    if (this.begin > 0) {
      this.rowIndex = (this.begin - 1);
    }
    if ((this.dataTable != null) && (this.dataTable.getRowCount() > 0) && (this.rowIndex < this.dataTable.getRowCount())) {
      if (this.dataTable.getDataColumn("_RowNo") == null) {
        this.dataTable.insertColumn(new DataColumn("_RowNo", 8));
      }
      for (int i = 0; i < this.dataTable.getRowCount(); i++) {
        this.dataTable.set(i, "_RowNo", Integer.valueOf(i + 1));
      }
      this.currentRow = this.dataTable.getDataRow(this.rowIndex++);
      if (ObjectUtil.notEmpty(this.item)) {
        this.context.addDataVariable(this.item, this.currentRow);
      }
      this.context.addDataVariable("i", Integer.valueOf(this.rowIndex - this.begin));
      return 2;
    }
    return 0;
  }

  public int doAfterBody() throws TemplateRuntimeException
  {
    String body = getBody().trim();
    if (this.pageContext.isKeepExpression()) {
      body = HtmlUtil.replaceWithDataRow(this.currentRow, body, false, false);
    }
    getPreviousOut().write(body);
    this.bodyContent.clear();
    if ((this.dataTable.getRowCount() > this.rowIndex) && (this.rowIndex - this.begin + 1 < this.count)) {
      this.currentRow = this.dataTable.getDataRow(this.rowIndex++);
      if (ObjectUtil.notEmpty(this.item)) {
        this.context.addDataVariable(this.item, this.currentRow);
      }
      this.context.addDataVariable("i", Integer.valueOf(this.rowIndex - this.begin));
      return 2;
    }
    return 0;
  }

  public String getMethod()
  {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
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

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DataRow getCurrentDataRow() {
    return this.currentRow;
  }

  public DataTable getData() {
    return this.dataTable;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("method", 1, "@{Framework.ListTag.Method}"));
    list.add(new TagAttr("data", 1, "@{Framework.ListTag.Data}"));
    list.add(new TagAttr("item", 1, "@{Framework.ListTag.Item}"));
    list.add(new TagAttr("size", 8, "@{Framework.ListTag.Size}"));
    list.add(new TagAttr("page", TagAttr.BOOL_OPTIONS, "@{Framework.ListTag.Page}"));
    list.add(new TagAttr("count", 8, "@{Framework.ListTag.Count}"));
    list.add(new TagAttr("begin", 8, "@{Framework.ListTag.Begin}"));
    return list;
  }

  public String getDescription()
  {
    return "@{Framework.ZListTagDescription}";
  }

  public String getName() {
    return "@{Framework.ZListTagName}";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public void setData(String data) {
    this.data = data;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setBegin(int begin) {
    this.begin = begin;
  }
}