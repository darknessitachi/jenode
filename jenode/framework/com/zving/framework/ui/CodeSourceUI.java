package com.zving.framework.ui;

import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.config.CodeSourceClass;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.DataTable;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;

public class CodeSourceUI extends UIFacade
{
  private static CodeSource codeSourceInstance;
  private static Object mutex = new Object();

  @Priv(login=false)
  public void getData() {
    String codeType = $V("CodeType");
    if (StringUtil.isEmpty($V("ConditionField"))) {
      this.Request.put("ConditionField", "1");
      this.Request.put("ConditionValue", "1");
    }
    DataTable dt = null;
    String method = $V("Method");
    if ((StringUtil.isEmpty(method)) && (codeType.startsWith("#"))) {
      method = codeType.substring(1);
    }
    if (StringUtil.isNotEmpty(method)) {
      try {
        IMethodLocator m = MethodLocatorUtil.find(method);
        PrivCheck.check(m);
        Object o = m.execute(new Object[0]);
        dt = (DataTable)o;
      } catch (Exception e) {
        throw new RuntimeException(method + " must return DataTable");
      }
    } else {
      CodeSource cs = getCodeSourceInstance();
      dt = cs.getCodeData(codeType, this.Request);
    }
    $S("DataTable", dt);
  }

  public static void initCodeSource() {
    if (codeSourceInstance == null)
      synchronized (mutex) {
        if (codeSourceInstance == null) {
          String className = CodeSourceClass.getValue();
          if (StringUtil.isEmpty(className)) {
            LogUtil.warn("CodeSource class not found");
            return;
          }
          try {
            Class c = Class.forName(className);
            Object o = c.newInstance();
            codeSourceInstance = (CodeSource)o;
          } catch (Exception e) {
            throw new RuntimeException("Load CodeSource class failed:" + e.getMessage());
          }
        }
      }
  }

  public static CodeSource getCodeSourceInstance()
  {
    initCodeSource();
    return codeSourceInstance;
  }
}