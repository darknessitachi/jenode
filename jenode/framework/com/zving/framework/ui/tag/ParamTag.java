package com.zving.framework.ui.tag;

import com.zving.framework.collection.Mapx;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ParamTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String var;
  private String Default;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "param";
  }

  public int doStartTag() throws TemplateRuntimeException {
    if (this.attributes.getString("var").indexOf("${") < 0) {
      this.var = this.context.eval("${" + this.var + "}");
    }
    if (this.var != null) {
      String v = String.valueOf(this.var);
      v = LangUtil.get(v);
      this.pageContext.getOut().write(v);
    } else if (this.Default != null) {
      this.pageContext.getOut().write(this.Default);
    }
    return 0;
  }

  public String getVar() {
    return this.var;
  }

  public void setVar(String var) {
    this.var = var;
  }

  public String getDefault() {
    return this.Default;
  }

  public void setDefault(String default1) {
    this.Default = default1;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("var"));
    list.add(new TagAttr("default"));
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