package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataTable;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.html.HtmlTable;
import com.zving.framework.utility.HtmlUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CheckboxTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  protected String code;
  protected String id;
  protected String method;
  protected String name;
  protected boolean tableLayout;
  protected String tableWidth;
  protected int column;
  protected String onChange;
  protected String onClick;
  protected String value;
  protected String disabled;
  protected String defaultCheck;
  protected String type;
  protected String theme;
  protected String options;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "checkbox";
  }

  public void init() throws ExpressionException {
    super.init();
    this.type = "checkbox";
  }

  public int doStartTag() throws TemplateRuntimeException {
    this.pageContext.getOut().print(getHtml());
    return 0;
  }

  public String getHtml() {
    Mapx map = Current.getRequest();
    DataTable dt = SelectTag.getCodeData(this.code, this.method, this.options, this.pageContext, map);
    if ((dt == null) || (dt.getRowCount() == 0)) {
      return "";
    }
    if (StringUtil.isEmpty(this.id)) {
      this.id = "_ZVING_NOID_";
    }
    if (StringUtil.isEmpty(this.name)) {
      this.name = this.id;
    }
    if ((StringUtil.isEmpty(this.value)) || (this.value.startsWith("${"))) {
      this.value = null;
    }
    if (this.value != null) {
      this.value = ("," + this.value + ",");
    }
    boolean disabledFlag = "true".equals(this.disabled);
    dt.getDataColumn(0).setColumnName("Key");
    dt.getDataColumn(1).setColumnName("Value");
    dt.insertColumn("RowNo");
    dt.insertColumn("Checked");
    dt.insertColumn("OnClick");
    dt.insertColumn("Disabled");
    if (StringUtil.isNotNull(this.defaultCheck)) {
      this.defaultCheck = ("," + this.defaultCheck + ",");
    }
    for (int i = 0; i < dt.getRowCount(); i++) {
      dt.set(i, "RowNo", i);
      String v = "," + dt.getString(i, "Key") + ",";
      dt.set(i, "Checked", "");
      dt.set(i, "OnClick", "");
      dt.set(i, "Disabled", "");
      if (this.value != null) {
        if ((StringUtil.isNotNull(this.value)) && (this.value.indexOf(v) >= 0)) {
          dt.set(i, "Checked", "checked=\"true\"");
        }
      }
      else if ((StringUtil.isNotNull(this.defaultCheck)) && (this.defaultCheck.indexOf(v) >= 0)) {
        dt.set(i, "Checked", "checked=\"true\"");
      }

      if (this.onClick != null) {
        dt.set(i, "OnClick", this.onClick);
      }
      if (disabledFlag) {
        dt.set(i, "Disabled", "disabled=\"disabled\"");
      }
    }
    String hasRowNo = "_${RowNo}";
    if (dt.getRowCount() == 1) {
      hasRowNo = "";
    }
    String jsClassName = this.type.replaceFirst("checkbox", "Checkbox").replaceFirst("radio", "Radio");
    String html = "<input type=\"" + this.type + "\" ${Disabled} ${Checked} id=\"" + this.name + hasRowNo + "\" name=\"" + this.name + 
      "\" value=\"${Key}\" onclick=\"${OnClick}\"";
    if (StringUtil.isNotEmpty(this.theme)) {
      html = html + " class=\"z-" + this.theme + "-hide\"";
      html = html + "><label for=\"" + this.name + hasRowNo + "\"";
      html = html + " class=\"z-" + this.theme + "-label\"";
      html = html + ">${Value}</label>";
      html = html + "<script>if(Zving." + jsClassName + "){new Zving." + jsClassName + "('" + this.name + hasRowNo + "');}</script>";
    } else {
      html = html + "><label for=\"" + this.name + hasRowNo + "\"";
      html = html + ">${Value}</label>";
    }
    if (this.tableLayout) {
      if (this.column < 1) {
        this.column = 1;
      }
      if (StringUtil.isNotEmpty(this.value)) {
        this.value = ("," + this.value + ",");
      }
      HtmlTable table = HtmlUtil.dataTableToHtmlTable(dt, html, this.column);
      table.setAttribute("width", this.tableWidth);
      html = table.getOuterHtml();
      return html;
    }
    return HtmlUtil.replaceWithDataTable(dt, html, false);
  }

  public String getCode()
  {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMethod() {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isTableLayout() {
    return this.tableLayout;
  }

  public void setTableLayout(boolean tableLayout) {
    this.tableLayout = tableLayout;
  }

  public String getTableWidth() {
    return this.tableWidth;
  }

  public void setTableWidth(String tableWidth) {
    this.tableWidth = tableWidth;
  }

  public int getColumn() {
    return this.column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public String getOnChange() {
    return this.onChange;
  }

  public void setOnChange(String onChange) {
    this.onChange = onChange;
  }

  public String getOnClick() {
    return this.onClick;
  }

  public void setOnClick(String onClick) {
    this.onClick = onClick;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDisabled() {
    return this.disabled;
  }

  public void setDisabled(String disabled) {
    this.disabled = disabled;
  }

  public String getDefaultCheck() {
    return this.defaultCheck;
  }

  public void setDefaultCheck(String defaultCheck) {
    this.defaultCheck = defaultCheck;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTheme() {
    return this.theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getOptions() {
    return this.options;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  public static long getSerialversionuid() {
    return 1L;
  }

  public String getOnclick() {
    return this.onClick;
  }

  public void setOnclick(String onClick) {
    this.onClick = onClick;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("code"));
    list.add(new TagAttr("defaultCheck"));
    list.add(new TagAttr("method"));
    list.add(new TagAttr("name"));
    list.add(new TagAttr("onchange"));
    list.add(new TagAttr("onclick"));
    list.add(new TagAttr("options"));
    list.add(new TagAttr("tableWidth"));
    list.add(new TagAttr("theme"));
    list.add(new TagAttr("type"));
    list.add(new TagAttr("value"));
    list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("tableLayout", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("column", 8));
    return list;
  }

  public String getDescription()
  {
    return "@{Framework.ZcheckBoxTagDescription}";
  }

  public String getName() {
    return "@{Framework.ZcheckBoxTagName}";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}