package com.zving.platform.log.type;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.platform.ILogType;

public class SecurityLog
  implements ILogType
{
  public static final String ID = "SecurityLog";
  public static final String SubType_PrivCheck = "PrivCheck";
  public static final String SubType_Verify = "Verify";
  public static final String SubType_XSS = "XSS";
  private Mapx<String, String> map;

  public String getID()
  {
    return "SecurityLog";
  }

  public String getName() {
    return "@{Logs.SecurityLogMenu}";
  }

  public Mapx<String, String> getSubTypes() {
    if (this.map == null) {
      this.map = new Mapx();
      this.map.put("PrivCheck", "@{Platform.Unauthorizedaccess}");
      this.map.put("Verify", "@{Platform.SQLInjection}");
      this.map.put("XSS", "@{Platform.ScriptAttack}");
    }
    return this.map;
  }

  public void decodeMessage(DataTable dt)
  {
  }
}