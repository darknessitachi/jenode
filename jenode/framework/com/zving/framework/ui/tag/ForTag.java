package com.zving.framework.ui.tag;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.TemplatePrintWriter;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ForTag extends AbstractTag
{
  int from;
  int to;
  int step;
  private int pos;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "for";
  }

  public int doStartTag() throws TemplateRuntimeException {
    if (this.step == 0) {
      this.step = 1;
    }
    if (((this.step > 0) && (this.from > this.to)) || ((this.step < 0) && (this.from < this.to))) {
      return 0;
    }
    this.pos = this.from;
    this.context.addDataVariable("j", Integer.valueOf(this.pos));
    return 2;
  }

  public int doAfterBody() throws TemplateRuntimeException
  {
    this.pos += 1;
    String body = getBody().trim();
    getPreviousOut().write(body);
    this.bodyContent.clear();
    if (((this.step > 0) && (this.pos < this.to)) || ((this.step < 0) && (this.pos > this.to))) {
      this.context.addDataVariable("j", Integer.valueOf(this.pos));
      return 2;
    }
    return 0;
  }

  public int getFrom()
  {
    return this.from;
  }

  public void setFrom(int from) {
    this.from = from;
  }

  public int getTo() {
    return this.to;
  }

  public void setTo(int to) {
    this.to = to;
  }

  public int getStep() {
    return this.step;
  }

  public void setStep(int step) {
    this.step = step;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("from", true, 8, "@{Framework.CycleFrom}"));
    list.add(new TagAttr("to", true, 8, "@{Framework.CycleEnd}"));
    list.add(new TagAttr("step", false, 8, "@{Framework.CycleStep}"));
    return list;
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }

  public String getDescription()
  {
    return "@{Framework.ZForTagDescription}";
  }

  public String getName() {
    return "@{Framework.ZForTagName}";
  }
}