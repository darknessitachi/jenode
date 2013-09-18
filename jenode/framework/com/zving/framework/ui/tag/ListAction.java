package com.zving.framework.ui.tag;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DBUtil;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.ui.IPageEnableAction;

public class ListAction
  implements IPageEnableAction
{
  private DataTable dataSource;
  private Mapx<String, Object> params;
  private int pageSize;
  private int pageIndex;
  private boolean page;
  private String ID;
  private String method;
  private ListTag tag;
  private int total;
  private String queryString;

  public void bindData(QueryBuilder qb)
  {
    if (this.total == 0) {
      this.total = DBUtil.getCount(qb);
    }
    this.dataSource = qb.executePagedDataTable(this.pageSize, this.pageIndex);
  }

  public void bindData(DataTable dt) {
    this.dataSource = dt;
  }

  public void bindData(DAOSet<?> set) {
    bindData(set.toDataTable());
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void setTotal(QueryBuilder qb) {
    this.total = DBUtil.getCount(qb);
  }

  public int getTotal() {
    return this.total;
  }

  public DataRow getParentCurrentDataRow()
  {
    AbstractTag p = this.tag.getParent();
    if ((p instanceof ListTag)) {
      return ((ListTag)p).getCurrentDataRow();
    }
    return null;
  }

  public DataTable getParentData() {
    AbstractTag p = this.tag.getParent();
    if ((p instanceof ListTag)) {
      return ((ListTag)p).getData();
    }
    return null;
  }

  public String getParam(String key) {
    return this.params.getString(key);
  }

  public DataTable getDataSource() {
    return this.dataSource;
  }

  public Mapx<String, Object> getParams() {
    return this.params;
  }

  public void setParams(Mapx<String, Object> params) {
    this.params = params;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getPageIndex() {
    return this.pageIndex;
  }

  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public boolean isPage() {
    return this.page;
  }

  public void setPage(boolean pageEnable) {
    this.page = pageEnable;
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String iD) {
    this.ID = iD;
  }

  public String getMethod() {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setTag(ListTag tag) {
    this.tag = tag;
  }

  public String getQueryString() {
    return this.queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }
}