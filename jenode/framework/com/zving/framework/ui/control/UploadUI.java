package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.Verify;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.VerifyCheck;
import com.zving.framework.utility.LogUtil;
import java.util.ArrayList;

public class UploadUI extends UIFacade
{
  private static Object mutex = new Object();

  private static Mapx<String, TaskStatus> uploadTaskMap = new Mapx(5000);

  @Priv(login=false)
  @Verify(ignoreAll=true)
  public void submit()
  {
    String method = $V("_ZVING_METHOD");
    this.Request.remove("_ZVING_METHOD");

    IMethodLocator m = MethodLocatorUtil.find(method);
    PrivCheck.check(m);

    if (!VerifyCheck.check(m)) {
      String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
      LogUtil.warn(message);
      Current.getResponse().setFailedMessage(message);
      return;
    }
    m.execute(new Object[] { new UploadAction() });
  }

  @Priv(login=false)
  public void getTaskStatus() {
    String taskID = $V("TaskID");
    $S("Status", getTaskStatus(taskID));
  }

  public static String getTaskStatus(String taskID) {
    String Status = "";
    synchronized (mutex) {
      if (uploadTaskMap.get(taskID) != null)
        Status = ((TaskStatus)uploadTaskMap.get(taskID)).StatusStr;
      else {
        Status = LangMapping.get("Framework.Upload.Status");
      }
    }
    return Status;
  }

  public static void removeTask(String taskID) {
    synchronized (mutex) {
      uploadTaskMap.remove(taskID);
    }
  }

  public static void setTask(String taskID, String Status) {
    synchronized (mutex) {
      checkTimeout();
      if (uploadTaskMap.get(taskID) != null) {
        TaskStatus ts = (TaskStatus)uploadTaskMap.get(taskID);
        ts.LastTime = System.currentTimeMillis();
        ts.StatusStr = Status;
      } else {
        TaskStatus ts = new TaskStatus(null);
        ts.StatusStr = Status;
        ts.LastTime = System.currentTimeMillis();
        uploadTaskMap.put(taskID, ts);
      }
    }
  }

  private static void checkTimeout() {
    ArrayList arr = uploadTaskMap.keyArray();
    long yesterday = System.currentTimeMillis() - 86400000L;
    for (String id : arr) {
      TaskStatus ts = (TaskStatus)uploadTaskMap.get(id);
      if (ts != null)
      {
        if (ts.LastTime < yesterday)
          uploadTaskMap.remove(id);
      }
    }
  }

  private static class TaskStatus
  {
    public long LastTime;
    public String StatusStr;
  }
}