package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataListTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String method;
  private String id;
  private int size;
  private boolean page = true;

  private boolean autoFill = true;

  private boolean autoPageSize = false;
  private String dragClass;
  private String listNodes;
  private String sortEnd;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "datalist";
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
      if (StringUtil.isEmpty(this.method)) {
        throw new RuntimeException("DataList's method cann't be empty");
      }

      DataListAction dla = new DataListAction();
      dla.setTagBody(content);
      dla.setPage(this.page);
      dla.setAutoFill(this.autoFill);
      dla.setAutoPageSize(this.autoPageSize);
      dla.setMethod(this.method);
      dla.setID(this.id);
      dla.setDragClass(this.dragClass);
      dla.setListNodes(this.listNodes);
      dla.setSortEnd(this.sortEnd);

      dla.setPageSize(this.size);

      if (this.page) {
        dla.setPageIndex(0);
        if (StringUtil.isNotEmpty(dla.getParam("_ZVING_PAGEINDEX"))) {
          dla.setPageIndex(Integer.parseInt(dla.getParam("_ZVING_PAGEINDEX")));
        }
        if (dla.getPageIndex() < 0) {
          dla.setPageIndex(0);
        }
        dla.setPageSize(this.size);
      }

      IMethodLocator m = MethodLocatorUtil.find(this.method);
      PrivCheck.check(m);

      dla.setParams(Current.getRequest());
      m.execute(new Object[] { dla });

      this.pageContext.setAttribute(this.id + "_ZVING_ACTION", dla);
      getPreviousOut().write(dla.getHtml());
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

  public int getSize() {
    return this.size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public boolean isPage() {
    return this.page;
  }

  public void setPage(boolean page) {
    this.page = page;
  }

  public boolean isAutoFill()
  {
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

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("autoFill", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("autoPageSize", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("page", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("size", 8));
    list.add(new TagAttr("sortEnd"));
    list.add(new TagAttr("method"));
    list.add(new TagAttr("listNodes"));
    list.add(new TagAttr("dragClass"));
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