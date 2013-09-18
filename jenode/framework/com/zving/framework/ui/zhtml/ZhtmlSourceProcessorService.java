package com.zving.framework.ui.zhtml;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.template.ITemplateSourceProcessor;

public class ZhtmlSourceProcessorService extends AbstractExtendService<ITemplateSourceProcessor>
{
  private static ZhtmlSourceProcessorService instance;

  public static ZhtmlSourceProcessorService getInstance()
  {
    if (instance == null) {
      synchronized (ZhtmlSourceProcessorService.class) {
        if (instance == null) {
          instance = (ZhtmlSourceProcessorService)findInstance(ZhtmlSourceProcessorService.class);
        }
      }
    }
    return instance;
  }
}