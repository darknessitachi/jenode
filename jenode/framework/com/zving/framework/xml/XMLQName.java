package com.zving.framework.xml;

public class XMLQName
{
  String name;
  XMLNamespace namespace;

  public XMLQName(String name, XMLNamespace ns)
  {
    this.namespace = ns;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public XMLNamespace getNamespace() {
    return this.namespace;
  }

  public String getQualifiedName() {
    if (this.namespace == null) {
      return this.name;
    }
    return this.namespace.getPrefix() + ":" + this.name;
  }

  public String toString()
  {
    return getQualifiedName();
  }
}