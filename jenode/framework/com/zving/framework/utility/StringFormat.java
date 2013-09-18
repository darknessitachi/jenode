package com.zving.framework.utility;

import java.io.PrintStream;
import java.util.ArrayList;

public class StringFormat
{
  private String source;
  private ArrayList<String> params = new ArrayList();

  public StringFormat(String str) {
    this.source = str;
  }

  public StringFormat(String str, Object[] vs) {
    this.source = str;
    add(vs);
  }

  public static String format(String str, Object[] vs) {
    return new StringFormat(str, vs).toString();
  }

  public void add(Object obj) {
    add(new Object[] { obj });
  }

  public void add(Object[] vs) {
    if (vs == null) {
      return;
    }
    for (Object v : vs)
      if (v == null) {
        this.params.add(null);
      }
      else
        this.params.add(String.valueOf(v));
  }

  public String toString()
  {
    if (this.params.size() == 0) {
      return this.source;
    }
    FastStringBuilder sb = new FastStringBuilder();
    int lastIndex = 0;
    int i = 0; for (int j = 0; i < this.source.length(); i++) {
      if (this.source.charAt(i) == '?') {
        if ((i > 0) && (this.source.charAt(i - 1) == '\\')) {
          sb.append(this.source.substring(lastIndex, i - 1));
          sb.append("?");
          lastIndex = i + 1;
        }
        else {
          sb.append(this.source.substring(lastIndex, i));
          if (this.params.size() >= j)
            sb.append((String)this.params.get(j++));
          else {
            sb.append("null");
          }
          lastIndex = i + 1;
        } } else if (this.source.charAt(i) == '{') {
        if ((i > 0) && (this.source.charAt(i - 1) == '\\')) {
          sb.append(this.source.substring(lastIndex, i - 1));
          sb.append("{");
          lastIndex = i + 1;
        }
        else {
          for (int k = i + 1; k < this.source.length(); k++)
            if (!Character.isDigit(this.source.charAt(k))) {
              if ((this.source.charAt(k) == '}') && (k > i + 1)) {
                sb.append(this.source.substring(lastIndex, i));
                int index = Integer.parseInt(this.source.substring(i + 1, k));
                if (this.params.size() >= index)
                  sb.append((String)this.params.get(index));
                else {
                  sb.append("null");
                }
                lastIndex = k + 1;
              }
              i = k;
              break;
            }
        }
      }
    }
    sb.append(this.source.substring(lastIndex));
    return sb.toStringAndClose();
  }

  public static void main(String[] args) {
    long t = System.currentTimeMillis();
    for (int i = 0; i < 1000000; i++) {
      format("c \\?? \\{0} {1} b {2} a ", new Object[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) });
    }
    System.out.println(System.currentTimeMillis() - t);
  }
}