package com.zving.framework.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarResourceEntry
{
  private String jarFileName;
  private long lastModified;
  private String path;
  private boolean isDirectory = false;
  private long length;

  public boolean isDirectory()
  {
    return this.isDirectory;
  }

  public boolean isFile() {
    return !this.isDirectory;
  }

  public JarResourceEntry(String jarFileName, long lastModified, String path, boolean dirFlag, long length) {
    this.jarFileName = jarFileName;
    this.lastModified = lastModified;
    this.path = path;
    this.isDirectory = dirFlag;
    this.length = length;
  }

  public InputStream toStream() throws IOException {
    JarFile jf = new JarFile(this.jarFileName);
    ZipEntry ze = jf.getEntry(this.path);
    if (ze != null) {
      URL url = new URL("jar:file:" + (this.jarFileName.startsWith("/") ? "" : "/") + this.jarFileName + "!/" + this.path);
      return url.openStream();
    }
    return null;
  }

  public List<JarResourceEntry> listFiles() {
    return JarResourceRoot.listFiles(this.path);
  }

  public String getJarFileName() {
    return this.jarFileName;
  }

  public void setJarFileName(String jarFileName) {
    this.jarFileName = jarFileName;
  }

  public long getLastModified() {
    return this.lastModified;
  }

  public String getFullPath() {
    return this.path;
  }

  public long length() {
    return this.length;
  }

  public String getName() {
    if (this.path.indexOf("/") >= 0) {
      return this.path.substring(this.path.lastIndexOf("/") + 1);
    }
    return this.path;
  }
}