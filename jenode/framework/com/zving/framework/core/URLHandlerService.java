package com.zving.framework.core;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.utility.ObjectUtil;
import java.util.Comparator;

public class URLHandlerService extends AbstractExtendService<IURLHandler>
{
  public static URLHandlerService getInstance()
  {
    return (URLHandlerService)findInstance(URLHandlerService.class);
  }

  protected void prepareItemList() {
    super.prepareItemList();
    this.itemList = ObjectUtil.sort(this.itemList, new Comparator() {
      public int compare(IURLHandler o1, IURLHandler o2) {
        return o1.getOrder() - o2.getOrder();
      }
    });
  }
}