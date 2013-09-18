package com.zving.framework.core.scanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

public class BuiltResource
{
  boolean isInJar;
  String JarEntryName;
  String FileName;

  public InputStream getInputStream()
  {
    try
    {
      if (this.isInJar) {
        JarFile jf = new JarFile(this.FileName);
        return jf.getInputStream(jf.getEntry(this.JarEntryName));
      }
      return new FileInputStream(this.FileName);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public InputStream getInnerInputStream(String name) {
    try {
      name = name.substring(name.lastIndexOf("$"));
      if (this.isInJar) {
        JarFile jf = new JarFile(this.FileName);
        String entryName = this.JarEntryName.substring(0, this.JarEntryName.lastIndexOf(".")) + name + ".class";
        return jf.getInputStream(jf.getEntry(entryName));
      }
      String innerFileName = this.FileName.substring(0, this.FileName.lastIndexOf(".")) + name + ".class";
      return new FileInputStream(innerFileName);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getFullName() {
    String fullName = this.FileName;
    if (this.isInJar) {
      fullName = this.FileName + "!" + this.JarEntryName;
    }
    return fullName.replace('\\', '/');
  }

  public boolean isClass() {
    if ((this.isInJar) && (this.JarEntryName.endsWith(".class"))) {
      return true;
    }
    if ((!this.isInJar) && (this.FileName.endsWith(".class"))) {
      return true;
    }
    return false;
  }
}