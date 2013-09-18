package com.zving.framework.xml;

import com.zving.framework.utility.FileUtil;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class XMLMultiLoader
{
  private XMLElement root = new XMLElement("_ROOT");

  public void load(String path) {
    File f = new File(path);
    load(f);
  }

  public void load(File f) {
    if (f.isFile()) {
      loadOneFile(f);
    } else {
      File[] fs = f.listFiles();
      for (int i = 0; i < fs.length; i++) {
        f = fs[i];
        if ((f.isFile()) && ((f.getName().toLowerCase().endsWith(".xml")) || (f.getName().toLowerCase().endsWith(".plugin"))))
          loadOneFile(f);
      }
    }
  }

  public void load(InputStream is)
  {
    loadOneFile(is);
  }

  private void loadOneFile(File f) {
    loadOneFile(FileUtil.readText(f));
  }

  public void clear() {
    this.root = new XMLElement("_ROOT");
  }

  private void loadOneFile(InputStream is) {
    XMLParser parser = new XMLParser(is);
    try {
      parser.parse();
      XMLElement singleRoot = parser.getDocument().getRoot();
      singleRoot.setParent(this.root);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void loadOneFile(String xml) {
    XMLParser parser = new XMLParser(xml);
    parser.parse();
    XMLElement singleRoot = parser.getDocument().getRoot();
    singleRoot.setParent(this.root);
  }

  public XMLElement getRoot() {
    return this.root;
  }

  public XMLElement getElement(String path, String attrName, String attrValue) {
    List list = this.root.elementsByAttribute(path, attrName, attrValue);
    if ((list == null) || (list.size() == 0)) {
      return null;
    }
    return (XMLElement)list.get(0);
  }

  public List<XMLElement> getElements(String path) {
    return this.root.elements(path);
  }
}