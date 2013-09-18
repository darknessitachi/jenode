package com.zving.framework.xml;

import com.zving.framework.utility.FileUtil;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class XMLParser
{
  XMLDocument doc = null;
  XMLElement parent = null;
  String xml;
  char[] cs;
  String doctype;
  String attrName;
  String attrValue;
  String tag;
  String cdata;
  String text;
  int lineNum;

  public XMLParser(String xml)
  {
    this.xml = xml;
  }

  public XMLParser(InputStream is) {
    byte[] bs = FileUtil.readByte(is);
    try {
      this.xml = new String(bs, "UTF-8");
      int start = this.xml.indexOf("encoding=");
      if (start > 0) {
        start += 9;
        boolean isFinalQuote = true;
        for (int i = start; i < this.xml.length(); i++) {
          char chr = this.xml.charAt(i);
          if (Character.isSpaceChar(chr)) {
            start++;
          }
          else if ((chr == '\'') || (chr == '"')) {
            start = i + 1;
            isFinalQuote = chr == '"';
            break;
          }
        }

        int end = isFinalQuote ? this.xml.indexOf('"', start) : this.xml.indexOf('\'', start);
        if (end > 0) {
          String encoding = this.xml.substring(start, end).trim();
          if (!encoding.equalsIgnoreCase("UTF-8"))
            this.xml = new String(bs, encoding);
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new XMLParseException(e);
    }
  }

  public XMLDocument parse() {
    this.lineNum = 0;
    this.doc = new XMLDocument();
    this.cs = this.xml.toCharArray();
    int end = expect(0, "<?xml");
    int pos = 0;
    if (end > 0) {
      pos = end;
      while ((end = expectAttribute(pos)) > 0) {
        if (this.attrName.equals("version")) {
          this.doc.setVersion(this.attrValue);
        }
        if (this.attrName.equals("encoding")) {
          this.doc.setEncoding(this.attrValue);
        }
        pos = end;
      }
      if ((end = expect(pos, "?>")) < 0) {
        throw new XMLParseException("Invalid xml prolog!");
      }
      pos = end;
    }
    end = expectOthers(pos);
    pos = end > 0 ? end : pos;
    end = expect(pos, "<!DOCTYPE");
    if (end > 0) {
      pos = end;
      end = expectGT(pos);
      if (end < 0) {
        throw new XMLParseException("DOCTYPE not complete correctly!");
      }
      this.doc.setDocType(this.xml.substring(pos, end - 1));
      pos = end;
    }
    end = expectOthers(pos);
    pos = end > 0 ? end : pos;
    end = expectElement(pos);
    if (end > 0) {
      pos = end;
    }
    expectOthers(pos);
    return this.doc;
  }

  private int expectElement(int start)
  {
    int index = expect(start, "<");
    if (index < 0) {
      return -1;
    }
    start = index;
    for (int i = start; i < this.cs.length; i++) {
      char c = this.cs[i];
      if ((isSpace(c)) || (c == '>')) {
        index = i;
        break;
      }
    }
    this.tag = this.xml.substring(start, index);
    if (this.tag.charAt(this.tag.length() - 1) == '/') {
      this.tag = this.tag.substring(0, this.tag.length() - 1);
      index--;
    }
    if ((index == start) || (isInvalidName(start, index))) {
      throw new XMLParseException("Element's name is invalid:" + this.tag + ",position:" + start);
    }
    XMLElement ele = null;
    if (this.parent == null) {
      ele = new XMLElement(this.tag, start);
      this.doc.setRoot(ele);
      this.parent = ele;
    } else {
      ele = new XMLElement(this.tag, start);
      ele.setParent(this.parent);
      this.parent = ele;
    }
    start = index;

    while ((index = expectAttribute(start)) > 0) {
      ele.addAttribute(this.attrName, this.attrValue);
      start = index;
    }
    if ((index = expect(start, "/>")) > 0) {
      this.parent = this.parent.getParent();
      return index;
    }
    if ((index = expect(start, ">")) < 0) {
      throw new XMLParseException("Element not complete correctly:" + this.tag + ",position:" + start);
    }

    while (expect(index, "</") <= 0)
    {
      int i = -1;
      i = expectOthers(index);
      if (i > 0) {
        index = i;
      }
      else {
        i = expectText(index);
        if (i > 0) {
          this.parent.addText(this.text);
          index = i;
        }
        else {
          i = expectCDATA(index);
          if (i > 0) {
            this.parent.addCDATA(this.cdata);
            index = i;
          }
          else {
            i = expectElement(index);
            if (i <= 0) break;
            index = i;
          }
        }
      }
    }
    start = index;

    if ((index = expect(start, "</" + ele.QName + ">")) < 0) {
      throw new XMLParseException("Element not complete correctly:" + ele.QName + ",position:" + start);
    }
    this.parent = this.parent.getParent();
    return index;
  }

  private boolean isInvalidName(int start, int end)
  {
    char first = this.cs[start];
    if (isSpace(first)) {
      return true;
    }
    if (Character.isDigit(first)) {
      return true;
    }
    if ((first == ':') || (first == '.') || (first == '-')) {
      return true;
    }
    for (int i = start; i < end; i++) {
      switch (this.cs[i]) {
      case '!':
      case '"':
      case '#':
      case '$':
      case '%':
      case '\'':
      case '(':
      case ')':
      case '*':
      case '+':
      case '/':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '`':
      case '{':
      case '|':
      case '}':
      case '~':
        return true;
      }

    }

    return false;
  }

  private int expectText(int start)
  {
    int i = this.xml.indexOf('<', start);
    if (i < 0) {
      throw new XMLParseException("Element not complete correctly:" + this.parent.QName + ",position:" + start);
    }
    if (i == start) {
      return -1;
    }
    String v = this.xml.substring(start, i).trim();
    if (v.length() == 0) {
      return -1;
    }
    this.text = decode(v);
    return i;
  }

  private int expectCDATA(int start)
  {
    int i = expect(start, "<![CDATA[");
    if (i < 0) {
      return -1;
    }
    start = i;
    i = this.xml.indexOf("]]>", start);
    if (i < 0) {
      throw new XMLParseException("CDATA not end correctly:" + this.parent.QName + ",position:" + start);
    }
    this.cdata = this.xml.substring(start, i);
    return i + 3;
  }

  private int expectAttribute(int start)
  {
    start = ignoreSpace(start);
    if ((this.cs[start] == '>') || (this.cs[start] == '/') || (this.cs[start] == '?')) {
      return -1;
    }
    int index = -1;
    for (int i = start; i < this.cs.length; i++) {
      if (this.cs[i] == '=') {
        index = i;
        break;
      }if (isSpace(this.cs[i])) {
        i = ignoreSpace(i + 1);
        if (this.cs[i] == '=') {
          index = i; break;
        }
        return -1;
      }

    }

    String name = this.xml.substring(start, index).trim();
    if (isInvalidName(start, start + name.length())) {
      throw new XMLParseException("XML attribute name is invalid: [" + name + "], position:" + start);
    }
    start = index + 1;
    start = ignoreSpace(start);
    char c = this.cs[start];
    if ((c != '"') && (c != '\'')) {
      throw new XMLParseException("XML attribute value not starts with \" or ', position:" + start);
    }
    start++;
    for (int i = start; i < this.cs.length; i++) {
      if (this.cs[i] == c) {
        index = i;
        break;
      }
      if ((this.cs[i] == '<') || (this.cs[i] == '\n')) {
        throw new XMLParseException("XML attribute value should not contains \\n and <, position:" + start);
      }
    }
    String value = this.xml.substring(start, index);
    value = decode(value);
    this.attrName = name;
    this.attrValue = value;
    return index + 1;
  }

  private String decode(String value)
  {
    char[] buf = new char[value.length()];
    int j = 0;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == '<') {
        throw new XMLParseException("XML attribute value is invalid: [" + value + "]");
      }
      if (c == '&') {
        if (value.indexOf("&quot;", i) == i) {
          buf[(j++)] = '"';
          i += 5;
        }
        else if (value.indexOf("&amp;", i) == i) {
          buf[(j++)] = '&';
          i += 4;
        }
        else if (value.indexOf("&lt;", i) == i) {
          buf[(j++)] = '<';
          i += 3;
        }
        else if (value.indexOf("&gt;", i) == i) {
          buf[(j++)] = '>';
          i += 3;
        }
        else if (value.indexOf("&apos;", i) == i) {
          buf[(j++)] = '\'';
          i += 5;
        }
        else if (value.charAt(i + 1) == '#') {
          int k = i + 2;
          boolean flag = false;
          for (; (k < i + 8) && (k < value.length()); k++) {
            if (!Character.isDigit(value.charAt(k))) {
              if (value.charAt(k) != ';') break;
              flag = true;

              break;
            }
          }
          if (flag) {
            char ch = (char)Integer.parseInt(value.substring(i + 2, k));
            buf[(j++)] = ch;
            i += k - 1;
          } else {
            throw new XMLParseException("Attribute value or element body contains not supported entity: [" + 
              value.substring(i, i + 10) + "]");
          }
        } else {
          throw new XMLParseException("Attribute value or element body contains not supported entity: [" + 
            value.substring(i, i + 10) + "]");
        }
      }
      else buf[(j++)] = c;
    }

    return new String(buf, 0, j);
  }

  private int expectOthers(int start)
  {
    int end = -1;
    int old = start;
    while (true)
    {
      end = expect(start, "<!--");
      if (end < 0) {
        end = expect(start, "<?");
        if (end < 0) {
          break;
        }
        start = end;
        end = this.xml.indexOf("?>", start);
        if (end < 0) {
          throw new XMLParseException("Processing instruction not complete correctly, position:" + start);
        }
        String instruction = this.xml.substring(start, end).trim();
        if (instruction.indexOf('?') >= 0) {
          throw new XMLParseException("Processing instruction contains illegal character:[?]");
        }
        if (instruction.indexOf('>') >= 0) {
          throw new XMLParseException("Processing instruction contains illegal character:[>]");
        }
        if (this.parent == null)
          this.doc.addInstruction(instruction);
        else {
          this.parent.addInstruction(instruction);
        }
        start = end + 2;
      } else {
        start = end;
        end = this.xml.indexOf("--", start);
        if (end < 0) {
          throw new XMLParseException("Comment not complete correctly, position:" + start);
        }
        end = expect(end + 2, ">");
        if (end < 0) {
          throw new XMLParseException("Comment not complete correctly, position:" + start);
        }
        String comment = this.xml.substring(start, end - 3);
        if (this.parent == null)
          this.doc.addComment(comment);
        else {
          this.parent.addComment(comment);
        }
        start = end;
      }
    }
    if (old == start) {
      return -1;
    }
    return start;
  }

  private int expectGT(int start)
  {
    int index = this.xml.indexOf('>', start);
    if (index < 0) {
      return -1;
    }
    for (int i = start; i < index; i++) {
      if (this.cs[i] == '<') {
        return -1;
      }
    }
    return index + 1;
  }

  private int ignoreSpace(int start) {
    for (int i = start; i < this.cs.length; i++) {
      if (!isSpace(this.cs[i])) break;
      start++;
    }

    return start;
  }

  private boolean isSpace(char c) {
    return (c == ' ') || (c == '\n') || (c == '\r') || (c == '\t') || (c == '\b') || (c == '\f');
  }

  private int expect(int start, String expected)
  {
    start = ignoreSpace(start);
    int end = start + expected.length();
    if (end > this.cs.length) {
      return -1;
    }
    for (int i = 0; i < expected.length(); i++) {
      if (this.cs[(start + i)] != expected.charAt(i)) {
        return -1;
      }
    }
    return end;
  }

  public XMLDocument getDocument()
  {
    return this.doc;
  }

  public static void main(String[] args) {
    String xml = FileUtil.readText(new File("G:/Workspace2.x/ZCMS2.x/UI/WEB-INF/weblogic.xml"));
    long t = System.currentTimeMillis();
    XMLParser p = new XMLParser(xml);
    for (int i = 0; i < 100000; i++) {
      p = new XMLParser(xml);
      p.parse();
    }
    System.out.println(System.currentTimeMillis() - t);
    System.out.println(p.getDocument());
  }
}