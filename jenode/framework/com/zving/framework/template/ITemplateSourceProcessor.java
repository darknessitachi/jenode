package com.zving.framework.template;

import com.zving.framework.extend.IExtendItem;

public abstract interface ITemplateSourceProcessor extends IExtendItem
{
  public abstract void process(TemplateParser paramTemplateParser);
}