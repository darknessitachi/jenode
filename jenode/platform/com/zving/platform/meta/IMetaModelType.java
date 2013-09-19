package com.zving.platform.meta;

import com.zving.framework.extend.IExtendItem;
import java.util.List;

public abstract interface IMetaModelType extends IExtendItem
{
  public abstract String getID();

  public abstract String getName();

  public abstract boolean isSystemModel();

  public abstract List<IMetaModelTemplateType> getTemplateTypes();

  public abstract String getDefautlTemplateHtml();
}