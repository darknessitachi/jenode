package com.zving.framework.ui.control;

import com.zving.framework.expression.core.ExpressionException;

public class RadioTag extends CheckboxTag
{
  private static final long serialVersionUID = 1L;

  public void init()
    throws ExpressionException
  {
    super.init();
    this.type = "radio";
  }

  public String getPrefix() {
    return "z";
  }

  public String getTagName() {
    return "radio";
  }

  public String getDescription()
  {
    return "@{Framework.RadioTag.Desc}";
  }

  public String getName() {
    return "@{Framework.RadioTag.Name}";
  }
}