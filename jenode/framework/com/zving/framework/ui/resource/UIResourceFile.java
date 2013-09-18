package com.zving.framework.ui.resource;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.FileUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class UIResourceFile
{
  private File file = null;
  private JarResourceEntry re = null;
  private String path;

  public UIResourceFile(String path)
  {
    path = FileUtil.normalizePath(path);
    if (path.startsWith(Config.getContextRealPath())) {
      path = path.substring(Config.getContextRealPath().length());
    }
    this.path = path;
    File f = new File(Config.getContextRealPath() + path);
    if (f.exists()) {
      this.file = f;
      return;
    }
    this.re = JarResourceRoot.getFile(path);
  }

  public boolean isFile()
  {
    return !isDirectory();
  }

  public boolean isDirectory() {
    if ((this.file != null) && (this.file.isDirectory())) {
      return true;
    }
    if ((this.re != null) && (this.re.isDirectory())) {
      return true;
    }
    return false;
  }

  public boolean isFromJar() {
    return this.re != null;
  }

  public String getFullPath() {
    return this.path;
  }

  public String getName() {
    if (this.path.indexOf("/") >= 0) {
      return this.path.substring(this.path.lastIndexOf("/") + 1);
    }
    return this.path;
  }

  public List<UIResourceFile> listFiles() {
    if (isFile()) {
      return null;
    }
    Mapx map = new Mapx();
    if (this.re != null) {
      for (JarResourceEntry re2 : this.re.listFiles()) {
        map.put(re2.getName(), new UIResourceFile(re2.getFullPath()));
      }
    }
    if (this.file != null) {
      for (File f : this.file.listFiles()) {
        map.put(f.getName(), new UIResourceFile(f.getAbsolutePath()));
      }
    }
    return map.valueArray();
  }

  public boolean exists() {
    return (this.file != null) || (this.re != null);
  }

  public long lastModified() {
    if (this.file != null) {
      return this.file.lastModified();
    }
    if (this.re != null) {
      return this.re.getLastModified();
    }
    return 0L;
  }

  public InputStream toStream() throws IOException {
    if (this.file != null)
      return new FileInputStream(this.file);
    if (this.re != null) {
      return this.re.toStream();
    }
    return new ByteArrayInputStream(new byte[0]);
  }

  public byte[] readByte()
  {
    InputStream is = null;
    try {
      is = toStream();
      return FileUtil.readByte(is);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public long length() {
    if (this.file != null) {
      return this.file.length();
    }
    if (this.re != null) {
      return this.re.length();
    }
    return 0L;
  }

  public String readText() {
    return readText(Config.getGlobalCharset());
  }

  public String readText(String encoding) {
    try {
      return new String(readByte(), encoding);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}