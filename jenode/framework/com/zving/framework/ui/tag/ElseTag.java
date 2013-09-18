package com.zving.framework.ui.tag;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ElseTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String out;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "else";
  }

  public int doStartTag() throws TemplateRuntimeException {
    AbstractTag tag = (AbstractTag)this.pageContext.getAttribute("_IF_TAG_FALSE");
    AbstractTag parent = (AbstractTag)this.pageContext.getAttribute("_IF_PARENT_TAG_FALSE");
    if ((tag == null) || (parent != getParent())) {
      return 0;
    }
    this.pageContext.removeAttribute("_IF_TAG_FALSE");
    if (((IfTag)tag).isPass()) {
      return 0;
    }
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

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody().trim();
    getPreviousOut().write(content);
    return 6;
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
    list.add(new TagAttr("out"));
    return list;
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public String getDescription()
  {
    return "@{Framework.ZElseTagDescription}";
  }

  public String getName() {
    return "@{Framework.ZElseTagName}";
  }
}