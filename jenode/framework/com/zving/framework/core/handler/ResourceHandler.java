package com.zving.framework.core.handler;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.IURLHandler;
import com.zving.framework.ui.resource.UIResourceFile;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.ServletUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceHandler
  implements IURLHandler
{
  private static Mapx<String, CachedResource> map = new Mapx(2000);
  public static final String LastModifiedFormat = "EEE, dd MMM yyyy HH:mm:ss";
  public static final String ID = "com.zving.framework.core.ResourceURLHandler";

  public String getID()
  {
    return "com.zving.framework.core.ResourceURLHandler";
  }

  public boolean match(String url) {
    String ext = ServletUtil.getUrlExtension(url);
    if (ObjectUtil.empty(ext)) {
      return false;
    }
    if (ObjectUtil.in(new Object[] { ext, ".gif", ".jpg", ".png", ".js", ".css", ".ico", ".swf", ".htm", ".html", ".shtml", ".xml", ".txt", 
      ".properties", ".jpeg" })) {
      return true;
    }
    return false;
  }

  public String getName() {
    return "Resource URL Processor";
  }

  public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
    String fileName = url;
    if (fileName.indexOf("?") > 0) {
      fileName = fileName.substring(0, fileName.indexOf("?"));
    }
    if (fileName.indexOf("#") > 0) {
      fileName = fileName.substring(0, fileName.indexOf("#"));
    }
    if (fileName.startsWith("/")) {
      fileName = fileName.substring(1);
    }
    fileName = FileUtil.normalizePath(fileName);
    CachedResource r = (CachedResource)map.get(fileName);
    if ((r != null) && (System.currentTimeMillis() - r.LastCheck < 3000L)) {
      String since = request.getHeader("If-Modified-Since");
      if (ObjectUtil.notEmpty(since))
        try {
          Date d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).parse(since);
          if ((d != null) && (d.getTime() <= r.LastModified)) {
            response.setStatus(304);
            return true;
          }
        }
        catch (ParseException localParseException) {
        }
      response.getOutputStream().write(r.Data);

      return true;
    }
    String fullFileName = Config.getContextRealPath() + fileName;
    File f = new File(fullFileName);
    if ((f.exists()) && (f.isFile())) {
      return false;
    }
    UIResourceFile rf = new UIResourceFile(fileName);
    long lastModified = rf.lastModified();
    if (lastModified != 0L) {
      String lastModifiedStr = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(lastModified));
      response.setHeader("Last-Modified", lastModifiedStr);
      String since = request.getHeader("If-Modified-Since");
      if (ObjectUtil.notEmpty(since)) {
        try {
          Date d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).parse(since);
          if ((d != null) && (d.getTime() <= lastModified)) {
            response.setStatus(304);
            response.getOutputStream().write(0);
            response.flushBuffer();
            if (r != null) {
              r.LastCheck = System.currentTimeMillis();
            }
            return true;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      String ext = ServletUtil.getUrlExtension(url);
      if (ObjectUtil.in(new Object[] { ext, ".shtml", ".html", "htm" })) {
        response.setHeader("Content-Type", "text/html");
        write(response, rf);
      } else if (ObjectUtil.in(new Object[] { ext, ".gif", ".jpg", ".jpeg", ".png", "bmp", "swf" })) {
        response.setHeader("Content-Type", "image/" + ext.substring(1));
        write(response, rf);
      } else if (ObjectUtil.in(new Object[] { ext, ".css" })) {
        if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3)) {
          response.setContentType("text/css;charset=" + Config.getGlobalCharset());
        } else {
          response.setHeader("Content-Type", "text/css");
          response.setCharacterEncoding(Config.getGlobalCharset());
        }
        write(response, rf);
      } else if (ObjectUtil.in(new Object[] { ext, ".js", ".txt" })) {
        String contentType = ext.equals(".js") ? "application/x-javascript" : "text/plain";
        if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3)) {
          response.setContentType(contentType + ";charset=" + Config.getGlobalCharset());
        } else {
          response.setHeader("Content-Type", contentType);
          response.setCharacterEncoding(Config.getGlobalCharset());
        }
        write(response, rf);
      } else {
        write(response, rf);
      }
      response.flushBuffer();
      if (rf.length() <= 512000L) {
        r = new CachedResource();
        r.LastCheck = System.currentTimeMillis();
        r.LastModified = rf.lastModified();
        r.Data = rf.readByte();
        map.put(fileName, r);
      }
    } else {
      return false;
    }

    return true;
  }

  private static void write(HttpServletResponse response, UIResourceFile rf) throws IOException {
    InputStream is = rf.toStream();
    if (is != null)
      try {
        OutputStream os = response.getOutputStream();
        int len = 0;
        byte[] bs = new byte[102400];
        while ((len = is.read(bs)) != -1)
          os.write(bs, 0, len);
      }
      finally
      {
        is.close();
      }
  }

  public void init()
  {
  }

  public void destroy()
  {
  }

  public int getOrder()
  {
    return 9999;
  }

  static class CachedResource
  {
    public long LastCheck;
    public long LastModified;
    public byte[] Data;
  }
}