package com.zving.framework.ui.tag;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.util.ArrayList;
import java.util.List;

public class VarTag extends AbstractTag
{
  String name;
  String value;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "var";
  }

  public int doStartTag() throws TemplateRuntimeException {
    try {
      Object v = this.pageContext.evalExpression(this.value);
      this.pageContext.addRootVariable(this.name, v);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("name", 1, "@{Framework.VarTag.Name}"));
    list.add(new TagAttr("value", 1, "@{Framework.VarTag.Value}"));
    return list;
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public String getDescription()
  {
    return "@{Framework.ZVarTagDescription}";
  }

  public String getName() {
    return "@{Framework.ZVarTagName}";
  }
}