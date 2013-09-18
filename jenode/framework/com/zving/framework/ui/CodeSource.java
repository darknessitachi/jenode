package com.zving.framework.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;

public abstract class CodeSource extends UIFacade
{
  public abstract DataTable getCodeData(String paramString, Mapx<String, Object> paramMapx);
}