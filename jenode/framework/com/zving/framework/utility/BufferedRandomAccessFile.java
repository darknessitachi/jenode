package com.zving.framework.utility;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferedRandomAccessFile extends RandomAccessFile
{
  private String fileName;

  public BufferedRandomAccessFile(String fileName, String mode)
    throws IOException
  {
    super(fileName, mode);
    this.fileName = fileName;
  }

  public void delete() {
    FileUtil.delete(this.fileName);
  }
}