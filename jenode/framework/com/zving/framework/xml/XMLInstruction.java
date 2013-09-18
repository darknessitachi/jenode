package com.zving.framework.xml;

import com.zving.framework.utility.FastStringBuilder;

public class XMLInstruction extends XMLNode
{
  String instruction;

  public XMLInstruction(XMLElement parent, String instruction)
  {
    this.instruction = instruction;
  }

  public void toString(String prefix, FastStringBuilder sb) {
    sb.append(prefix).append("<?").append(this.instruction).append(" ?>");
  }

  public String getText() {
    return "";
  }

  public int getType() {
    return 4;
  }
}