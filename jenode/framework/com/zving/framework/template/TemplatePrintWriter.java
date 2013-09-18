package com.zving.framework.template;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TemplatePrintWriter extends PrintWriter
{
  public TemplatePrintWriter()
  {
    super(new StringWriter());
  }

  public String getResult() {
    return this.out.toString();
  }

  public void clear() {
    this.out = new StringWriter();
  }

  public void print(boolean b) {
    write(b ? "true" : "false");
  }

  public void print(char c) {
    write(c);
  }

  public void print(int i) {
    write(String.valueOf(i));
  }

  public void print(long l) {
    write(String.valueOf(l));
  }

  public void print(float f) {
    write(String.valueOf(f));
  }

  public void print(double d) {
    write(String.valueOf(d));
  }

  public void print(char[] s) {
    write(s);
  }

  public void print(String s) {
    if (s == null) {
      s = "null";
    }
    write(s);
  }

  public void print(Object obj) {
    write(String.valueOf(obj));
  }

  public void println(boolean x) {
    print(x);
    println();
  }

  public void println(char x) {
    print(x);
    println();
  }

  public void println(int x) {
    print(x);
    println();
  }

  public void println(long x) {
    print(x);
    println();
  }

  public void println(float x) {
    print(x);
    println();
  }

  public void println(double x) {
    print(x);
    println();
  }

  public void println(char[] x) {
    print(x);
    println();
  }

  public void println(String x) {
    print(x);
    println();
  }

  public void println(Object x) {
    print(x);
    println();
  }

  public PrintWriter append(CharSequence csq) {
    if (csq == null)
      write("null");
    else
      write(csq.toString());
    return this;
  }
}