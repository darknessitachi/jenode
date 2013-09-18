package com.zving.framework.xml;

import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.FastStringBuilder;
import com.zving.framework.utility.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public final class XMLElement extends XMLNode
{
  Mapx<String, String> attributes = null;
  String QName;
  List<XMLNode> children;
  int pos;

  public XMLElement(String QName)
  {
    this.QName = QName;
  }

  public XMLElement(String QName, int pos) {
    this.QName = QName;
    this.pos = pos;
  }

  public XMLElement addElement(String QName) {
    XMLElement node = new XMLElement(QName);
    node.setParent(this);
    return node;
  }

  public void addText(String text) {
    XMLText node = new XMLText(text);
    node.setParent(this);
  }

  public void addCDATA(String text) {
    XMLCDATA node = new XMLCDATA(text);
    node.setParent(this);
  }

  public XMLElement getParent() {
    return this.parent;
  }

  public void setParent(XMLElement parent) {
    this.parent = parent;
    if (parent.children == null) {
      parent.children = new ArrayList();
    }
    if (!parent.children.contains(this))
      parent.children.add(this);
  }

  public void addComment(String comment)
  {
    new XMLComment(this, comment);
  }

  public void addInstruction(String instruction) {
    new XMLInstruction(this, instruction);
  }

  public Mapx<String, String> getAttributes() {
    if (this.attributes == null) {
      this.attributes = new Mapx();
    }
    return this.attributes;
  }

  public Mapx<String, String> attributes() {
    if (this.attributes == null) {
      this.attributes = new Mapx();
    }
    return this.attributes;
  }

  public String attributeValue(String attrName) {
    if (this.attributes == null) {
      return null;
    }
    return (String)this.attributes.get(attrName);
  }

  public void addAttribute(String attrName, String attrValue) {
    if (this.attributes == null) {
      this.attributes = new Mapx(8);
    }
    this.attributes.put(attrName, attrValue);
  }

  public String getQName() {
    return this.QName;
  }

  public int getPosition()
  {
    return this.pos;
  }

  public void setText(String text) {
    if (this.children == null)
      this.children = new ArrayList();
    else {
      this.children.clear();
    }
    addText(text);
  }

  public String getText() {
    if (this.children == null) {
      return "";
    }
    FastStringBuilder sb = new FastStringBuilder();
    for (XMLNode node : this.children) {
      if (ObjectUtil.in(new Object[] { Integer.valueOf(node.getType()), Integer.valueOf(2), Integer.valueOf(1) })) {
        sb.append(node.getText());
      }
    }
    return sb.toStringAndClose();
  }

  public List<XMLElement> elements() {
    List elements = new ArrayList();
    if (this.children == null) {
      return elements;
    }
    for (XMLNode node : this.children) {
      if (node.getType() == 3) {
        elements.add((XMLElement)node);
      }
    }
    return elements;
  }

  public List<XMLElement> getElements() {
    return elements();
  }

  private List<XMLElement> elements(List<XMLElement> list, String prefix) {
    List result = new ArrayList();
    for (XMLElement node : list) {
      List<XMLElement> elems = node.elements();
      if (elems != null)
      {
        for (XMLElement node2 : elems) {
          if (("*".equals(prefix)) || (node2.getQName().equalsIgnoreCase(prefix)))
            result.add(node2);
        }
      }
    }
    return result;
  }

  public List<XMLElement> elements(String path) {
    String[] arr = path.split("\\.");
    List list = new ArrayList();
    list.add(this);
    for (int i = 0; i < arr.length; i++) {
      list = elements(list, arr[i]);
      if (list == null) {
        break;
      }
    }
    return list;
  }

  public List<XMLElement> elements(XMLQName qname) {
    return elements(qname.getQualifiedName());
  }

  public String elementText(String path) {
    XMLElement child = element(path);
    if (child == null) {
      return "";
    }
    return child.getText();
  }

  public String elementText(XMLQName qname) {
    return elementText(qname.getQualifiedName());
  }

  public XMLElement element(String path) {
    List nodes = elements(path);
    return (nodes == null) || (nodes.size() == 0) ? null : (XMLElement)nodes.get(0);
  }

  public XMLElement element(XMLQName qname) {
    return element(qname.getQualifiedName());
  }

  public Object elementAttribute(String path, String attrName) {
    List nodes = elements(path);
    if (nodes.size() == 0) {
      return null;
    }
    return ((XMLElement)nodes.get(0)).getAttributes().get(attrName);
  }

  public List<XMLElement> elementsByAttribute(String attrName, String attrValue) {
    return elementsByAttribute(null, attrName, attrValue);
  }

  public List<XMLElement> elementsByAttribute(String path, String attrName, String attrValue) {
    List<XMLElement> nodes = elements(path);
    if ((nodes == null) || (nodes.size() == 0)) {
      return null;
    }
    List<XMLElement> result = new ArrayList();
    for (XMLElement n : nodes) {
      if (attrValue == null) {
        if (n.getAttributes().get(attrName) == null)
          result.add(n);
      }
      else if (attrValue.equals(n.getAttributes().get(attrName))) {
        result.add(n);
      }
    }
    return result;
  }

  public String toString() {
    FastStringBuilder sb = new FastStringBuilder();
    toString("", sb);
    return sb.toStringAndClose();
  }

  public void toString(String prefix, FastStringBuilder sb) {
    sb.append(prefix);
    sb.append("<");
    sb.append(this.QName);
    if (this.attributes != null) {
      for (Entry entry : this.attributes.entrySet()) {
        sb.append(" ");
        sb.append((String)entry.getKey());
        sb.append("=\"");
        encode((String)entry.getValue(), sb);
        sb.append("\"");
      }
    }
    if ((this.children == null) || (this.children.size() == 0)) {
      sb.append(" />");
    } else {
      sb.append(">");
      if ((this.children.size() == 1) && (((XMLNode)this.children.get(0)).getType() == 2)) {
        ((XMLNode)this.children.get(0)).toString("", sb);
        sb.append("</");
        sb.append(this.QName);
        sb.append(">");
      } else {
        for (XMLNode child : this.children) {
          sb.append("\n");
          child.toString(prefix + "\t", sb);
        }
        sb.append("\n");
        sb.append(prefix);
        sb.append("</");
        sb.append(this.QName);
        sb.append(">");
      }
    }
  }

  public int getType() {
    return 3;
  }

  public boolean remove(XMLElement ele) {
    for (int i = 0; i < this.children.size(); i++) {
      if (this.children.get(i) == ele) {
        this.children.remove(i);
        return true;
      }
    }
    return false;
  }
}