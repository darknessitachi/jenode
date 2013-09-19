package com.zving.platform.meta;

import com.zving.framework.extend.IExtendItem;
import com.zving.schema.ZDMetaColumn;

public abstract interface IMetadataColumnControlType extends IExtendItem
{
  public abstract String getHtml(ZDMetaColumn paramZDMetaColumn, String paramString);
}