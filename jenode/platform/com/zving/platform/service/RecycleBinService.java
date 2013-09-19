package com.zving.platform.service;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.IRecyclableItem;

public class RecycleBinService extends AbstractExtendService<IRecyclableItem>
{
  public static RecycleBinService getInstance()
  {
    return (RecycleBinService)findInstance(RecycleBinService.class);
  }
  public static int getDelContentCount(String catalogInnerCode, String contentType) {
    int count = 0;
    IRecyclableItem item = (IRecyclableItem)getInstance().get(contentType);
    if (ObjectUtil.notEmpty(item)) {
      count += item.getDeleteCount(catalogInnerCode);
    }
    return count;
  }
}