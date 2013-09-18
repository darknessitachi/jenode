package com.zving.framework.utility;

import java.io.IOException;
import java.io.Reader;

public class FastStringReader extends Reader
{
  private String str;
  private int length;
  private int next = 0;
  private int mark = 0;

  public FastStringReader(String s) {
    this.str = s;
    this.length = s.length();
  }

  private void ensureOpen() throws IOException {
    if (this.str == null)
      throw new IOException("Stream closed");
  }

  public int read() throws IOException {
    ensureOpen();
    if (this.next >= this.length)
      return -1;
    return this.str.charAt(this.next++);
  }

  public int read(char[] cbuf, int off, int len) throws IOException {
    ensureOpen();
    if ((off < 0) || (off > cbuf.length) || (len < 0) || (off + len > cbuf.length) || (off + len < 0))
      throw new IndexOutOfBoundsException();
    if (len == 0) {
      return 0;
    }
    if (this.next >= this.length)
      return -1;
    int n = Math.min(this.length - this.next, len);
    this.str.getChars(this.next, this.next + n, cbuf, off);
    this.next += n;
    return n;
  }

  public long skip(long ns) throws IOException {
    ensureOpen();
    if (this.next >= this.length) {
      return 0L;
    }
    long n = Math.min(this.length - this.next, ns);
    n = Math.max(-this.next, n);
    this.next = ((int)(this.next + n));
    return n;
  }

  public boolean ready() throws IOException {
    ensureOpen();
    return true;
  }

  public boolean markSupported() {
    return true;
  }

  public void mark(int readAheadLimit) throws IOException {
    if (readAheadLimit < 0) {
      throw new IllegalArgumentException("Read-ahead limit < 0");
    }
    ensureOpen();
    this.mark = this.next;
  }

  public void reset() throws IOException {
    ensureOpen();
    this.next = this.mark;
  }

  public void close() {
    this.str = null;
  }
}