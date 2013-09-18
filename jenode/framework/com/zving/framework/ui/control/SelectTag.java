package com.zving.framework.ui.control;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.DataCollection;
import com.zving.framework.data.DataTable;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.CodeSource;
import com.zving.framework.ui.CodeSourceUI;
import com.zving.framework.ui.html.HtmlSelect;
import com.zving.framework.ui.util.TagUtil;
import com.zving.framework.utility.FastStringBuilder;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String name;
  private String onChange;
  private String style;
  private int listWidth;
  private int listHeight;
  private String listURL;
  private String verify;
  private String condition;
  private String value;
  private String valueText;
  private String className;
  private boolean disabled;
  private boolean input;
  private String code;
  private String conditionField;
  private String conditionValue;
  private boolean autowidth;
  private boolean showValue;
  private boolean lazy;
  private boolean defaultblank;
  private String method;
  private int selectedIndex = 0;

  private int optionCount = 0;
  protected String options;
  public static final Pattern POption = Pattern.compile("<(span|option).*?value=(\\\"|\\')(.*?)\\2.*?>(.*?)</(span|option)>", 
    34);

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "select";
  }

  public void init() throws ExpressionException {
    super.init();
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody();
    try {
      getPreviousOut().print(getHtml(content));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public int doEndTag() throws TemplateRuntimeException {
    if (StringUtil.isEmpty(getBody())) {
      this.pageContext.getOut().print(getHtml(""));
    }
    return 6;
  }

  public String getHtml(String content)
  {
    String script = getScriptHtml(content);

    if (ObjectUtil.empty(this.id)) {
      this.id = TagUtil.getTagID(this.pageContext, "Combox");
    }
    if (StringUtil.isEmpty(this.name)) {
      this.name = this.id;
    }

    FastStringBuilder sb = new FastStringBuilder();
    sb.append("<div id=\"").append(this.id).append("_outer\" ztype=\"select\"");
    if (StringUtil.isNotEmpty(this.className)) {
      sb.append(" class=\"").append(this.className);
      sb.append(" z-combox\"");
      sb.append(" _class=\"").append(this.className).append("\"");
    } else {
      sb.append(" class=\"z-combox\"");
    }
    sb.append(" style=\"display:inline-block; *zoom: 1;*display: inline;vertical-align:middle;").append(
      "height:auto;width:auto;position:relative;border:none 0;margin:0;padding:0;white-space: nowrap;");
    if (StringUtil.isNotEmpty(this.style)) {
      sb.append("\" _style=\"").append(this.style);
    }
    sb.append("\">");

    sb.append("<input type=\"text\" ztype=\"select\" id=\"").append(this.id).append("\" name=\"" + this.name + "\"")
      .append(" tabindex=\"-1\" autocomplete=\"off\" class=\"inputText\"");
    if (StringUtil.isNotEmpty(this.style))
      sb.append(" style=\"").append(this.style).append(";position:absolute;z-index:-1;\"");
    else {
      sb.append(" style=\"position:absolute;z-index:-1;\"");
    }

    if (StringUtil.isNotEmpty(this.onChange)) {
      sb.append(" onchange=\"").append(this.onChange).append("\"");
    }
    if (this.disabled) {
      sb.append(" disabled=\"").append(Boolean.valueOf(this.disabled)).append("\"");
    }
    if (this.listWidth > 0) {
      sb.append(" listwidth=\"").append(this.listWidth).append("\"");
    }
    if (this.listHeight > 0) {
      sb.append(" listheight=\"").append(this.listHeight).append("\"");
    }
    if (StringUtil.isNotEmpty(this.listURL)) {
      sb.append(" listurl=\"").append(this.listURL).append("\"");
    }
    if (StringUtil.isNotEmpty(this.verify)) {
      sb.append(" verify=\"").append(this.verify).append("\"");
    }
    if (StringUtil.isNotEmpty(this.condition)) {
      sb.append(" condition=\"").append(this.condition).append("\"");
    }
    if (this.input) {
      sb.append(" input=\"").append(Boolean.valueOf(this.input)).append("\"");
    }
    if (this.lazy) {
      sb.append(" lazy=\"").append(Boolean.valueOf(this.lazy)).append("\"");
    }
    if (this.autowidth) {
      sb.append(" autowidth=\"").append(Boolean.valueOf(this.autowidth)).append("\"");
    }
    if (this.showValue) {
      sb.append(" showvalue=\"").append(Boolean.valueOf(this.showValue)).append("\"");
    }
    if (StringUtil.isNotEmpty(this.code)) {
      sb.append(" code=\"").append(this.code).append("\"");
    }
    if (StringUtil.isNotEmpty(this.method)) {
      sb.append(" method=\"").append(this.method).append("\"");
    }
    sb.append(" defaultblank=\"").append(Boolean.valueOf(this.defaultblank)).append("\"");
    if (StringUtil.isNotEmpty(this.conditionField)) {
      sb.append(" conditionField=\"").append(this.conditionField).append("\"");
    }
    if (StringUtil.isNotEmpty(this.conditionValue)) {
      sb.append(" conditionValue=\"").append(this.conditionValue).append("\"");
    }
    if (StringUtil.isNotEmpty(this.value)) {
      sb.append(" value=\"").append(this.value).append("\"");
      sb.append(" startvalue=\"").append(this.value).append("\"");
    }
    sb.append("/>");

    sb.append("<input type=\"text\" id=\"").append(this.id).append("_textField\" autocomplete=\"off\"");

    sb.append(" class=\"inputText\"");
    if (StringUtil.isNotEmpty(this.condition)) {
      sb.append(" condition=\"").append(this.condition).append("\"");
    }
    if (StringUtil.isNotEmpty(this.style))
      sb.append(" style=\"vertical-align:middle; cursor:default;").append(this.style).append("\"");
    else {
      sb.append(" style=\"vertical-align:middle; cursor:default;\"");
    }
    this.valueText = LangUtil.get(this.valueText);
    sb.append(" value=\"" + (this.valueText == null ? "" : this.valueText) + "\" />");

    sb.append("<img class=\"arrowimg\" src=\"").append(Config.getContextPath())
      .append("framework/images/blank.gif\" width=\"18\" height=\"20\" id=\"").append(this.id)
      .append("_arrow\" style=\"position:relative; left:-18px; margin-right:-19px; cursor:pointer; ")
      .append("width:18px; height:20px;vertical-align:middle;\"/>");
    sb.append("<div id=\"").append(this.id).append("_list\" class=\"optgroup\" style=\"text-align:left;display:none;\">");
    sb.append("<div id=\"").append(this.id).append("_ul\" style=\"left:-1px; width:-1px;\">");

    sb.append(script);

    sb.append("</div></div></div>");
    return sb.toString();
  }

  public static void getOptionHtml(FastStringBuilder sb, String text, String value, boolean flag) {
    text = LangUtil.get(text);
    sb.append("<a href=\"javascript:void(0);\" onclick=\"Zving.Selector.onItemClick(this);\"")
      .append(" onmouseover=\"Zving.Selector.onItemMouseOver(this)\" ").append(flag ? "selected=\"true\"" : "")
      .append(" hidefocus value=\"").append(value).append("\">").append(text).append("</a>");
  }

  private String getScriptHtml(String content) {
    if (content.indexOf("<select") >= 0) {
      HtmlSelect select = new HtmlSelect();
      try {
        select.parseHtml(content);
        if (StringUtil.isEmpty(this.id)) {
          this.id = select.getID();
        }
        if (StringUtil.isEmpty(this.className)) {
          this.className = select.getClassName();
        }
        if (StringUtil.isEmpty(this.style)) {
          this.style = select.getStyle();
        }
        if (StringUtil.isEmpty(this.code)) {
          this.code = select.getAttribute("code");
        }
        if (StringUtil.isEmpty(this.code)) {
          this.code = select.getAttribute("code");
        }
        if (StringUtil.isEmpty(this.condition)) {
          this.condition = select.getAttribute("condition");
        }
        if (StringUtil.isEmpty(this.conditionField)) {
          this.conditionField = select.getAttribute("conditionfield");
        }
        if (StringUtil.isEmpty(this.conditionValue)) {
          this.conditionValue = select.getAttribute("conditionvalue");
        }
        if (StringUtil.isEmpty(this.method)) {
          this.method = select.getAttribute("method");
        }
        if ("true".equals(select.getAttribute("disabled"))) {
          this.disabled = true;
        }
        if ("true".equals(select.getAttribute("input"))) {
          this.input = true;
        }
        if ("true".equals(select.getAttribute("defaultblank"))) {
          this.defaultblank = true;
        }
        if ("true".equals(select.getAttribute("showvalue"))) {
          this.showValue = true;
        }
        if ("true".equals(select.getAttribute("lazy"))) {
          this.lazy = true;
        }
        if ("true".equals(select.getAttribute("autowidth")))
          this.autowidth = true;
        try
        {
          if (Integer.parseInt(select.getAttribute("listwidth")) > 0)
            this.listWidth = Integer.parseInt(select.getAttribute("listwidth"));
        }
        catch (Exception localException1) {
        }
        try {
          if (Integer.parseInt(select.getAttribute("listheight")) > 0)
            this.listHeight = Integer.parseInt(select.getAttribute("listheight"));
        }
        catch (Exception localException2) {
        }
        if (StringUtil.isEmpty(this.value)) {
          this.value = select.getAttribute("value");
        }
        if (StringUtil.isEmpty(this.verify)) {
          this.verify = select.getAttribute("verify");
        }
        if (StringUtil.isEmpty(this.onChange)) {
          this.onChange = select.getAttribute("onchange");
        }
        if (StringUtil.isEmpty(this.listURL)) {
          this.listURL = select.getAttribute("listurl");
        }
        if (StringUtil.isEmpty(this.options)) {
          this.options = select.getAttribute("options");
        }
        content = select.getInnerHTML();
      } catch (Exception e) {
        if (StringUtil.isEmpty(this.id)) {
          throw new RuntimeException("Must set a ID value for <z:select> or <select>");
        }
      }
    }
    FastStringBuilder sb = new FastStringBuilder();
    Matcher m = POption.matcher(content);
    int lastIndex = 0;
    int i = 0;
    sb.append("<script>");
    sb.append("Combox_").append(this.id).append("_Init=function(){");
    sb.append("var _el=Zving.getDom('").append(this.id).append("');if(_el.compId){return true;}");
    sb.append("var  _data=[];");
    sb.append("var  _DataSource;");

    if (this.defaultblank) {
      sb.append("_data.push(['','']);");
    }

    while (m.find(lastIndex)) {
      if (this.showValue)
        sb.append("_data.push(['").append(m.group(3)).append("','").append(m.group(3))
          .append(StringUtil.isNotEmpty(m.group(4)) ? "-" + m.group(4) : "").append("']);");
      else {
        sb.append("_data.push(['").append(m.group(3)).append("','").append(m.group(4)).append("']);");
      }
      if (m.group().toLowerCase().substring(0, m.group().indexOf(">")).indexOf("selected") > 0) {
        this.value = m.group(3);
        this.selectedIndex = i;
      }
      if (m.group(3).equals(this.value)) {
        this.selectedIndex = i;
      }
      lastIndex = m.end();
      i++;
    }
    String codeData = "";
    if ((StringUtil.isNotEmpty(this.code)) || (StringUtil.isNotEmpty(this.method)) || (StringUtil.isNotEmpty(this.options))) {
      codeData = codeData + getScript4Data();
      sb.append(codeData);
    }
    sb.append("var combox_").append(this.id).append(" = new Selector({el:_el,data:_data,DataSource:_DataSource,");
    if (StringUtil.isNotEmpty(this.listURL)) {
      sb.append("url:'").append(this.listURL).append("',");
    }
    if (StringUtil.isNotEmpty(this.value)) {
      sb.append("value:'").append(this.value.replaceAll("'", "\\\\'")).append("',");
    }
    if (this.selectedIndex != -1) {
      sb.append("selectedIndex:").append(this.selectedIndex);
    }
    sb.append("});");

    sb.append("};");
    sb.append("if(Zving.Page.isReady){").append("Combox_").append(this.id).append("_Init();}else{Zving.Page.onReady(Combox_").append(this.id)
      .append("_Init);}");
    sb.append("</script>");
    content = sb.toString();
    return content;
  }

  private String getScript4Data() {
    if (this.lazy) {
      return "";
    }
    Mapx params = Current.getRequest();
    if (StringUtil.isNotEmpty(this.conditionField)) {
      params.put("ConditionField", this.conditionField);
      params.put("ConditionValue", this.conditionValue);
    } else {
      params.put("ConditionField", "1");
      params.put("ConditionValue", "1");
    }
    DataTable dt = getCodeData(this.code, this.method, this.options, this.pageContext, params);
    FastStringBuilder sb = new FastStringBuilder();
    if (dt != null) {
      for (int i = 0; i < dt.getRowCount(); i++) {
        if (dt.getString(i, 0).equals(this.value)) {
          this.selectedIndex = this.optionCount;
        }
        if (!this.showValue)
          sb.append("_data.push(['").append(dt.getString(i, 0)).append("','").append(dt.getString(i, 1)).append("']);");
        else {
          sb.append("_data.push(['").append(dt.getString(i, 0)).append("','").append(dt.getString(i, 0)).append("-")
            .append(dt.getString(i, 1)).append("']);");
        }
      }
      if (dt.getColumnCount() > 2) {
        sb.append(DataCollection.dataTableToJS(dt));
        sb.append("_DataSource = new DataTable();");
        sb.append("_DataSource.init(_Zving_Cols,_Zving_Values);");
      }
    }
    return sb.toString();
  }

  public static DataTable getCodeData(String code, String method, String options, AbstractExecuteContext pageContext, Mapx<String, Object> params)
  {
    DataTable dt = null;
    if (ObjectUtil.notEmpty(options)) {
      dt = new DataTable();
      dt.insertColumn("Value");
      dt.insertColumn("Key");
      String[] arr = StringUtil.splitEx(options, ",");
      for (String str : arr) {
        String[] arr2 = StringUtil.splitEx(str, ":");
        String k = arr2[0];
        k = StringUtil.replaceEx(k, "\\,", ",");
        k = StringUtil.replaceEx(k, "\\:", ":");
        String v = arr2[1];
        v = StringUtil.replaceEx(v, "\\,", ",");
        v = StringUtil.replaceEx(v, "\\:", ":");
        k = LangUtil.get(k);
        dt.insertRow(new String[] { v, k });
      }
      return dt;
    }
    if ((ObjectUtil.empty(method)) && (code.startsWith("#"))) {
      method = code.substring(1);
    }
    if (ObjectUtil.notEmpty(method))
      try {
        IMethodLocator m = MethodLocatorUtil.find(method);
        PrivCheck.check(m);
        Object o = m.execute(new Object[0]);
        if (o == null) {
          o = new DataTable();
        }
        dt = (DataTable)o;
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(method + " must return DataTable");
      }
    else {
      dt = CodeSourceUI.getCodeSourceInstance().getCodeData(code, params);
    }
    LangUtil.convertDataTable(dt, User.getLanguage());
    return dt;
  }

  public String getCondition() {
    return this.condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getListWidth() {
    return this.listWidth;
  }

  public void setListWidth(int listWidth) {
    this.listWidth = listWidth;
  }

  public String getOnChange() {
    return this.onChange;
  }

  public void setOnChange(String onChange) {
    this.onChange = onChange;
  }

  public String getStyle() {
    return this.style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getVerify() {
    return this.verify;
  }

  public void setVerify(String verify) {
    this.verify = verify;
  }

  public int getListHeight() {
    return this.listHeight;
  }

  public void setListHeight(int listHeight) {
    this.listHeight = listHeight;
  }

  public String getListURL() {
    return this.listURL;
  }

  public void setListURL(String listURL) {
    this.listURL = listURL;
  }

  public String getClassName() {
    return this.className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public boolean getDisabled() {
    return this.disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isInput() {
    return this.input;
  }

  public void setInput(boolean input) {
    this.input = input;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getConditionField() {
    return this.conditionField;
  }

  public void setConditionField(String conditionField) {
    this.conditionField = conditionField;
  }

  public String getConditionValue() {
    return this.conditionValue;
  }

  public void setConditionValue(String conditionValue) {
    this.conditionValue = conditionValue;
  }

  public boolean getShowValue() {
    return this.showValue;
  }

  public void setShowValue(boolean showValue) {
    this.showValue = showValue;
  }

  public boolean getInput() {
    return this.input;
  }

  public boolean isLazy() {
    return this.lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  public boolean isAutowidth() {
    return this.autowidth;
  }

  public void setAutowidth(boolean autowidth) {
    this.autowidth = autowidth;
  }

  public boolean isDefaultblank() {
    return this.defaultblank;
  }

  public void setDefaultblank(boolean defaultblank) {
    this.defaultblank = defaultblank;
  }

  public boolean isDefaultBlank() {
    return this.defaultblank;
  }

  public void setDefaultBlank(boolean defaultblank) {
    this.defaultblank = defaultblank;
  }

  public String getMethod() {
    return this.method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getOnchange() {
    return this.onChange;
  }

  public void setOnchange(String onchange) {
    this.onChange = onchange;
  }

  public String getOptions() {
    return this.options;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  public String getValueText() {
    return this.valueText;
  }

  public void setValueText(String valueText) {
    this.valueText = valueText;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("autoWidth", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("defaultBlank", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("input", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("showValue", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("listHeight", 8));
    list.add(new TagAttr("listWidth", 8));
    list.add(new TagAttr("optionCount", 8));
    list.add(new TagAttr("selectedIndex", 8));
    list.add(new TagAttr("className"));
    list.add(new TagAttr("code"));
    list.add(new TagAttr("condition"));
    list.add(new TagAttr("conditionField"));
    list.add(new TagAttr("conditionValue"));
    list.add(new TagAttr("listURL"));
    list.add(new TagAttr("method"));
    list.add(new TagAttr("name"));
    list.add(new TagAttr("onChange"));
    list.add(new TagAttr("options"));
    list.add(new TagAttr("style"));
    list.add(new TagAttr("value"));
    list.add(new TagAttr("valueText"));
    list.add(new TagAttr("verify"));
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