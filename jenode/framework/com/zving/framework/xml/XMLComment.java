package com.zving.framework.xml;

import com.zving.framework.utility.FastStringBuilder;

public class XMLComment extends XMLNode
{
  String comment;

  public XMLComment(XMLElement parent, String comment)
  {
    this.comment = comment;
  }

  public void toString(String prefix, FastStringBuilder sb) {
    sb.append(prefix).append("<!--").append(this.comment).append("-->");
  }

  public String getText() {
    return "";
  }

  public int getType() {
    return 5;
  }
}