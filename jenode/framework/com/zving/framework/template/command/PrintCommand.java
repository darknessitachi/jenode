package com.zving.framework.template.command;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.ITemplateCommand;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.io.PrintWriter;

public class PrintCommand
  implements ITemplateCommand
{
  private String str;

  public PrintCommand(String str)
  {
    this.str = str;
  }

  public int execute(AbstractExecuteContext context) throws TemplateRuntimeException {
    context.getOut().print(this.str);
    return 6;
  }
}