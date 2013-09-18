package com.zving.framework.ui.control;

import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.utility.StringUtil;

public class LongTimeTaskUI extends UIFacade
{
  @Priv(login=false)
  public void getInfo()
  {
    long id = 0L;
    if (StringUtil.isNotEmpty($V("TaskID"))) {
      id = Long.parseLong($V("TaskID"));
    }
    LongTimeTask ltt = LongTimeTask.getInstanceById(id);
    if ((ltt != null) && (ltt.isAlive())) {
      $S("CurrentInfo", StringUtil.isNotEmpty(ltt.getCurrentInfo()) ? ltt.getCurrentInfo() + "..." : "");
      $S("Messages", StringUtil.join(ltt.getMessages()));
      $S("Percent", ltt.getPercent());
    } else {
      $S("CompleteFlag", "1");
      String finishInfo = LangMapping.get("Framework.TaskFinished");
      if (ltt != null) {
        String errors = ltt.getAllErrors();
        if (StringUtil.isNotEmpty(errors)) {
          $S("CurrentInfo", errors);
          $S("ErrorFlag", "1");
        } else {
          finishInfo = StringUtil.isNotEmpty(ltt.getFinishedInfo()) ? ltt.getFinishedInfo() : finishInfo;
          $S("CurrentInfo", finishInfo);
        }
      } else {
        $S("CurrentInfo", finishInfo);
      }
      LongTimeTask.removeInstanceById(id);
    }
  }

  @Priv(login=false)
  public void stop() {
    long id = Long.parseLong($V("TaskID"));
    LongTimeTask ltt = LongTimeTask.getInstanceById(id);
    if (ltt != null)
      ltt.stopTask();
  }

  @Priv(login=false)
  public void stopComplete()
  {
    long id = Long.parseLong($V("TaskID"));
    LongTimeTask ltt = LongTimeTask.getInstanceById(id);
    if ((ltt == null) || (!ltt.isAlive()))
      LongTimeTask.removeInstanceById(id);
    else
      this.Response.setStatus(0);
  }
}