package com.zving.framework.xml;

import com.zving.framework.utility.FastStringBuilder;

public final class XMLText extends XMLNode
{
  String text;

  public XMLText(String text)
  {
    this.text = text;
  }

  public void toString(String prefix, FastStringBuilder sb) {
    if (this.text != null)
      encode(this.text, sb);
  }

  public String getText()
  {
    return this.text;
  }

  public int getType() {
    return 2;
  }
}