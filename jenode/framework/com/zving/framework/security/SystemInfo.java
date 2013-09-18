package com.zving.framework.security;

import com.zving.framework.Config;
import com.zving.framework.utility.FileUtil;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemInfo
{
  public static final String getMacAddress()
  {
    String os = System.getProperty("os.name").toLowerCase();
    String output = null;
    try {
      String cmd = "ipconfig /all";
      if (os.indexOf("windows") < 0) {
        cmd = "ifconfig";
      }
      Process proc = Runtime.getRuntime().exec(cmd);
      InputStream is = proc.getInputStream();
      output = FileUtil.readText(is, Config.getFileEncode());
    } catch (Exception ex) {
      String cmd = "ipconfig /all";
      if (os.indexOf("windows") < 0)
        cmd = "/sbin/ifconfig";
      try
      {
        Process proc = Runtime.getRuntime().exec(cmd);
        InputStream is = proc.getInputStream();
        output = FileUtil.readText(is, Config.getFileEncode());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Pattern p = Pattern.compile("([0-9a-zA-z]{1,2}[\\:\\-]){5}[0-9a-zA-z]{2}", 32);
    Matcher m = p.matcher(output);
    int lastIndex = 0;
    StringBuilder sb = new StringBuilder();
    while (m.find(lastIndex)) {
      if (lastIndex != 0) {
        sb.append(",");
      }
      sb.append(m.group(0));
      lastIndex = m.end();
    }
    return sb.toString();
  }
}