package com.zving.framework.utility;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IOUtil
{
  public static byte[] getBytesFromStream(InputStream is)
    throws IOException
  {
    return getBytesFromStream(is, 2147483647);
  }

  public static byte[] getBytesFromStream(InputStream is, int max) throws IOException {
    byte[] buffer = new byte[1024];
    int read = -1;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] data = (byte[])null;
    try {
      while ((read = is.read(buffer)) != -1) {
        if (bos.size() > max) {
          throw new IOException("InputStream length is out of range,max=" + max);
        }
        if (read > 0) {
          byte[] chunk = (byte[])null;
          if (read == 1024) {
            chunk = buffer;
          } else {
            chunk = new byte[read];
            System.arraycopy(buffer, 0, chunk, 0, read);
          }
          bos.write(chunk);
        }
      }
      data = bos.toByteArray();
    } finally {
      if (bos != null) {
        bos.close();
        bos = null;
      }
    }
    return data;
  }

  public static void download(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream is) {
    try {
      setDownloadFileName(request, response, fileName);
      if (is == null) {
        return;
      }
      OutputStream os = response.getOutputStream();
      byte[] buffer = new byte[1024];
      int read = -1;
      try {
        while ((read = is.read(buffer)) != -1)
          if (read > 0) {
            byte[] chunk = (byte[])null;
            if (read == 1024) {
              chunk = buffer;
            } else {
              chunk = new byte[read];
              System.arraycopy(buffer, 0, chunk, 0, read);
            }
            os.write(chunk);
            os.flush();
          }
      }
      finally {
        is.close();
      }
      os.flush();
      os.close();
    } catch (IOException e) {
      LogUtil.warn("IOUtil.download:IO ends by user!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void setDownloadFileName(HttpServletRequest request, HttpServletResponse response, String fileName)
  {
    try
    {
      request.setCharacterEncoding(Config.getGlobalCharset());
      response.reset();
      response.setContentType("application/octet-stream");
      String userAgent = request.getHeader("User-Agent");
      if ((StringUtil.isNotEmpty(userAgent)) && (userAgent.toLowerCase().indexOf("msie") >= 0))
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
      else
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static byte[] mapToBytes(Mapx<String, Object> map)
  {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    try {
      for (String k : map.keyArray()) {
        Object v = map.get(k);
        int type = 0;
        byte[] bs = (byte[])null;
        if ((v instanceof String)) {
          type = 1;
          bs = ((String)v).getBytes(Config.getGlobalCharset());
        } else if ((v instanceof byte[])) {
          type = 2;
          bs = (byte[])v; } else {
          if (!(v instanceof Serializable)) continue;
          type = 0;
          bs = FileUtil.serialize((Serializable)v);
        }

        bo.write(NumberUtil.toBytes(k.length()));
        bo.write(k.getBytes());
        bo.write(NumberUtil.toBytes(type));
        bo.write(NumberUtil.toBytes(bs.length));
        bo.write(bs);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return bo.toByteArray();
  }

  public static Mapx<String, Object> bytesToMap(byte[] src) {
    Mapx map = new Mapx();
    if ((src == null) || (src.length == 0)) {
      return map;
    }
    ByteArrayInputStream bis = new ByteArrayInputStream(src);
    try {
      while (true) {
        byte[] bs = new byte[4];
        if (bis.read(bs) == -1) {
          break;
        }
        int len = NumberUtil.toInt(bs);

        bs = new byte[len];
        bis.read(bs);
        String k = new String(bs);

        bs = new byte[4];
        bis.read(bs);
        int type = NumberUtil.toInt(bs);

        bis.read(bs);
        len = NumberUtil.toInt(bs);

        bs = new byte[len];
        bis.read(bs);

        if (type == 1)
          map.put(k, new String(bs, Config.getGlobalCharset()));
        else if (type == 2)
          map.put(k, bs);
        else if (type == 0)
          map.put(k, FileUtil.unserialize(bs));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return map;
  }
}