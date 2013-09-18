package com.zving.framework.ui.tag;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.Operators;
import com.zving.framework.utility.Primitives;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WhenTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String value;
  private String out;
  private boolean other;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "when";
  }

  public int doStartTag() throws TemplateRuntimeException {
    AbstractTag tag = getParent();
    if (!(tag instanceof ChooseTag)) {
      throw new RuntimeException("tag when must in tag choose");
    }
    ChooseTag parent = (ChooseTag)tag;
    Object v1 = parent.getValue();
    Object v2 = this.value;
    if ((!this.other) && 
      (this.value == null)) {
      throw new RuntimeException("tag when's other and value can't be empty at the same time");
    }

    if (this.other) {
      if (!parent.isMatched()) {
        output(parent);
        return 2;
      }
      return 0;
    }if (Primitives.getBoolean(Operators.eq(v1, v2))) {
      output(parent);
      return 2;
    }
    return 0;
  }

  private void output(ChooseTag parent)
  {
    if (StringUtil.isNotEmpty(this.out)) {
      this.pageContext.getOut().print(this.out);
    }
    parent.setMatched(true);
  }

  public int doAfterBody() throws TemplateRuntimeException {
    getPreviousOut().print(getBody());
    return 6;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getOut() {
    return this.out;
  }

  public void setOut(String out) {
    this.out = out;
  }

  public boolean isOther() {
    return this.other;
  }

  public void setOther(boolean other) {
    this.other = other;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("value"));
    list.add(new TagAttr("out"));
    list.add(new TagAttr("other", TagAttr.BOOL_OPTIONS));
    return list;
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public String getName()
  {
    return "@{Framework.WhenTag.Name}";
  }

  public String getDescription()
  {
    return null;
  }
}