package com.zving.framework.template.command;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.ITemplateCommand;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;

public class ExpressionCommand
  implements ITemplateCommand
{
  private String expr;

  public ExpressionCommand(String str)
  {
    this.expr = str;
  }

  public int execute(AbstractExecuteContext context) throws TemplateRuntimeException {
    if (context.isKeepExpression())
      context.getOut().print(this.expr);
    else {
      try {
        Object v = context.evalExpression(this.expr);
        if (v == null) {
          v = "";
        }
        if ((v != null) && ((v instanceof String))) {
          v = LangUtil.get(v.toString());
        }
        context.getOut().print(v);
      } catch (ExpressionException e) {
        throw new TemplateRuntimeException(e.getMessage());
      }
    }
    return 6;
  }
}