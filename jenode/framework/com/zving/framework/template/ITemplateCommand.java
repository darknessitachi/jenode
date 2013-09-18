package com.zving.framework.template;

import com.zving.framework.template.exception.TemplateRuntimeException;

public abstract interface ITemplateCommand
{
  public abstract int execute(AbstractExecuteContext paramAbstractExecuteContext)
    throws TemplateRuntimeException;
}