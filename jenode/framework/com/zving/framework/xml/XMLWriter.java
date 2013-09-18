package com.zving.framework.xml;

import com.zving.framework.utility.FileUtil;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

public class XMLWriter
{
  public static void writeTo(XMLDocument doc, File f)
  {
    FileUtil.writeText(f.getAbsolutePath(), doc.toString(), doc.getEncoding());
  }

  public static void writeTo(XMLDocument doc, OutputStream os) {
    try {
      os.write(doc.toString().getBytes(doc.getEncoding()));
    } catch (Exception e) {
      throw new XMLWriteException(e);
    }
  }

  public static void writeTo(XMLDocument doc, Writer writer) {
    try {
      writer.write(doc.toString());
    } catch (Exception e) {
      throw new XMLWriteException(e);
    }
  }
}