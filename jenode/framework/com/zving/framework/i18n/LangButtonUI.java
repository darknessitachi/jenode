package com.zving.framework.i18n;

import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.ui.tag.ListAction;

public class LangButtonUI extends UIFacade
{
  @Priv(login=false)
  public void bindLanguageList(ListAction la)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("Key");
    dt.insertColumn("Name");
    Mapx all = LangUtil.getSupportedLanguages();
    for (String key : all.keyArray()) {
      dt.insertRow(new Object[] { key, all.get(key) });
    }
    la.bindData(dt);
  }
}