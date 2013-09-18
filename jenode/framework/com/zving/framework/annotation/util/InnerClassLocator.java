package com.zving.framework.annotation.util;

import com.zving.framework.Config;
import com.zving.framework.core.FrameworkException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

public class InnerClassLocator
{
  private JarFile jf;

  public InnerClassLocator(JarFile jf)
  {
    this.jf = jf;
  }

  public InnerClassLocator() {
  }

  public InputStream getStream(String innerClass) throws IOException {
    if (this.jf != null) {
      return this.jf.getInputStream(this.jf.getEntry(innerClass + ".class"));
    }
    String fileName = Config.getPluginPath() + "classes/" + innerClass + ".class";
    File f = new File(fileName);
    if (!f.exists()) {
      throw new FrameworkException("Locate inner class failed:" + f);
    }
    return new FileInputStream(fileName);
  }
}