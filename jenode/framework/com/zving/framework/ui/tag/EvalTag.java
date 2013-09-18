package com.zving.framework.ui.tag;

import com.zving.framework.i18n.LangUtil;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EvalTag extends ParamTag
{
  String expression;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "eval";
  }

  public int doStartTag() throws TemplateRuntimeException {
    try {
      Object value = this.pageContext.evalExpression(this.expression);
      if (value != null) {
        String v = String.valueOf(value);
        v = LangUtil.get(v);
        this.pageContext.getOut().write(v);
      } else {
        this.pageContext.getOut().write("null");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public String getExpression() {
    return this.expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("expression"));
    return list;
  }
}