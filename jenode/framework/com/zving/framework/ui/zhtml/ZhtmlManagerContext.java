package com.zving.framework.ui.zhtml;

import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.impl.CachedEvaluator;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.ITemplateManager;
import com.zving.framework.template.ITemplateManagerContext;
import com.zving.framework.template.ITemplateSourceProcessor;
import java.util.List;

public class ZhtmlManagerContext
  implements ITemplateManagerContext
{
  private static ZhtmlManagerContext instance;
  private ITemplateManager templateManager = new ZhtmlManager();

  public static ZhtmlManagerContext getInstance() {
    if (instance == null) {
      synchronized (ZhtmlManagerContext.class) {
        if (instance == null) {
          ZhtmlManagerContext mc = new ZhtmlManagerContext();
          instance = mc;
        }
      }
    }
    return instance;
  }

  public IEvaluator getEvaluator()
  {
    return new CachedEvaluator();
  }

  public IFunctionMapper getFunctionMapper() {
    return ZhtmlFunctionService.getFunctionMappper();
  }

  public List<ITemplateSourceProcessor> getSourceProcessors() {
    return ZhtmlSourceProcessorService.getInstance().getAll();
  }

  public List<AbstractTag> getTags() {
    return ZhtmlTagService.getInstance().getAll();
  }

  public AbstractTag getTag(String prefix, String tagName) {
    for (AbstractTag tag : getTags()) {
      if ((tag.getPrefix().equals(prefix)) && (tag.getTagName().equals(tagName))) {
        return (AbstractTag)tag.clone();
      }
    }
    return null;
  }

  public ITemplateManager getTemplateManager() {
    return this.templateManager;
  }
}