package com.zving.framework.ui.util;

import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;

public class TagUtil
{
  private static final String PageContextAttribte_TagID = "ZVING_TAGID_";

  public static String getTagID(AbstractExecuteContext pageContext, String prefix)
  {
    if (ObjectUtil.empty(pageContext.getRootVariable("ZVING_TAGID_"))) {
      pageContext.addRootVariable("ZVING_TAGID_", Integer.valueOf(0));
    }
    int tagid = Integer.valueOf(pageContext.getRootVariable("ZVING_TAGID_").toString()).intValue();
    pageContext.addRootVariable("ZVING_TAGID_", Integer.valueOf(++tagid));
    return (StringUtil.isEmpty(prefix) ? "ZVING_TAGID_" : prefix) + tagid;
  }
}