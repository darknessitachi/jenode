package com.zving.framework.template;

import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.core.IFunctionMapper;
import java.util.List;

public abstract interface ITemplateManagerContext
{
  public abstract List<ITemplateSourceProcessor> getSourceProcessors();

  public abstract List<? extends AbstractTag> getTags();

  public abstract AbstractTag getTag(String paramString1, String paramString2);

  public abstract ITemplateManager getTemplateManager();

  public abstract IFunctionMapper getFunctionMapper();

  public abstract IEvaluator getEvaluator();
}