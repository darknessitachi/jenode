package com.zving.platform.log.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.utility.LogAppender;
import com.zving.framework.utility.StringUtil;

@Alias("RealTimeLog")
public class RealTimeLogUI extends UIFacade
{
  @Priv
  public void getMessage()
  {
    String id = $V("ID");
    if (StringUtil.isEmpty(id)) {
      id = "0";
    }
    $S("Log", LogAppender.getLog(Long.parseLong(id)).toString());
    $S("LastID", Long.valueOf(LogAppender.getMaxId()));
  }
}