package com.zving.framework.security;

import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.Serializable;

public class Privilege extends Mapx<String, Integer>
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final String OwnerType_Branch = "B";
  public static final String OwnerType_Role = "R";
  public static final String OwnerType_User = "U";
  public static final int Flag_Allow = 1;
  public static final int Flag_Deny = -1;
  public static final int Flag_NotSet = 0;
  public static final String Or = "||";

  public boolean hasPriv(String privID)
  {
    return getInt(privID) == 1;
  }

  public void put(int flag, String privID) {
    super.put(privID, Integer.valueOf(flag));
  }

  public void remove(String privID) {
    super.remove(privID);
  }

  public void union(Privilege p)
  {
    if (p == null) {
      return;
    }
    for (String id : p.keySet()) {
      int flag1 = getInt(id);
      int flag2 = p.getInt(id);
      if ((flag2 < 0) && (flag1 >= 0)) {
        put(id, Integer.valueOf(-1));
      }
      if ((flag2 > 0) && (flag1 >= 0))
        put(id, Integer.valueOf(1));
    }
  }

  public void parse(String str)
  {
    clear();
    if (ObjectUtil.empty(str)) {
      return;
    }
    for (String line : StringUtil.splitEx(str, "\n")) {
      String[] arr = line.trim().split("\\=");
      if (arr.length == 2)
        try {
          put(arr[0], Integer.valueOf(Integer.parseInt(arr[1])));
        } catch (Exception e) {
          LogUtil.error("Invalid privilege:" + line);
        }
    }
  }

  public void clear()
  {
    super.clear();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String id : keySet())
      if (!ObjectUtil.empty(id))
      {
        sb.append(id + "=" + StringUtil.javaEncode(getString(id)));
        sb.append("\n");
      }
    return sb.toString();
  }
}