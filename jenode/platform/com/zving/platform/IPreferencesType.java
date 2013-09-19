package com.zving.platform;

import com.zving.framework.data.DataCollection;
import com.zving.framework.extend.IExtendItem;
import com.zving.framework.extend.action.ZhtmlContext;
import com.zving.framework.json.JSONObject;

public abstract interface IPreferencesType extends IExtendItem
{
  public abstract void onPageExecute(ZhtmlContext paramZhtmlContext);

  public abstract JSONObject onSave(DataCollection paramDataCollection);
}