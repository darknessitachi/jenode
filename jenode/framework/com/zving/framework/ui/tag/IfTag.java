package com.zving.framework.ui.tag;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.Primitives;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class IfTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  public static final String IfTagInAttribute = "_IF_TAG_FALSE";
  public static final String IfParentTagInAttribute = "_IF_PARENT_TAG_FALSE";
  private String condition;
  private String out;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "if";
  }

  public int doStartTag() throws TemplateRuntimeException {
    if (Primitives.getBoolean(this.condition)) {
      if (this.out != null) {
        if (this.out.indexOf("${") >= 0) {
          try {
            this.out = String.valueOf(this.pageContext.evalExpression(this.out, String.class));
          } catch (ExpressionException e) {
            throw new TemplateRuntimeException(e.getMessage());
          }
        }
        this.pageContext.getOut().print(this.out);
      }
      return 2;
    }
    this.pageContext.setAttribute("_IF_TAG_FALSE", this);
    this.pageContext.setAttribute("_IF_PARENT_TAG_FALSE", getParent());
    return 0;
  }

  public boolean isPass()
  {
    return "true".equals(this.condition);
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    getPreviousOut().write(content);
    return 6;
  }

  public String getCondition() {
    return this.condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getTest() {
    return this.condition;
  }

  public void setTest(String condition) {
    this.condition = condition;
  }

  public String getOut() {
    return this.out;
  }

  public void setOut(String out) {
    this.out = out;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("condition", 1, "@{Framework.IfTag.Condition}"));
    list.add(new TagAttr("out", 1, "@{Framework.IfTag.Output}"));
    return list;
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public String getDescription()
  {
    return "@{Framework.ZIfTagDescription}";
  }

  public String getName() {
    return "@{Framework.ZIfTagName}";
  }
}