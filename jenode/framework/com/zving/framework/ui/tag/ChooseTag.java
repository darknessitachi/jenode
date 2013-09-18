package com.zving.framework.ui.tag;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ChooseTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String variable;
  private Object value;
  private boolean matched;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "choose";
  }

  public int doStartTag() throws TemplateRuntimeException {
    try {
      this.value = this.pageContext.evalExpression(this.variable);
    } catch (ExpressionException e) {
      e.printStackTrace();
    }
    return 2;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    getPreviousOut().write(content);
    return 6;
  }

  public String getVariable() {
    return this.variable;
  }

  public void setVariable(String variable) {
    this.variable = variable;
  }

  public Object getValue() {
    return this.value;
  }

  public boolean isMatched() {
    return this.matched;
  }

  public void setMatched(boolean matched) {
    this.matched = matched;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("variable", true));
    return list;
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public String getName()
  {
    return "@{Framework.ChooseTag.Name}";
  }

  public String getDescription()
  {
    return null;
  }
}