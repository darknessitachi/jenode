package com.zving.framework.template;

public abstract interface ITemplateManager
{
  public abstract TemplateExecutor getExecutor(String paramString);

  public abstract boolean execute(String paramString, AbstractExecuteContext paramAbstractExecuteContext);
}