package com.zving.framework.expression.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class SimpleCharStream
{
  public static final boolean staticFlag = false;
  int bufsize;
  int available;
  int tokenBegin;
  public int bufpos = -1;
  protected int[] bufline;
  protected int[] bufcolumn;
  protected int column = 0;
  protected int line = 1;

  protected boolean prevCharIsCR = false;
  protected boolean prevCharIsLF = false;
  protected Reader inputStream;
  protected char[] buffer;
  protected int maxNextCharInd = 0;
  protected int inBuf = 0;
  protected int tabSize = 8;

  protected void setTabSize(int i) { this.tabSize = i; } 
  protected int getTabSize(int i) { return this.tabSize; }


  protected void ExpandBuff(boolean wrapAround)
  {
    char[] newbuffer = new char[this.bufsize + 2048];
    int[] newbufline = new int[this.bufsize + 2048];
    int[] newbufcolumn = new int[this.bufsize + 2048];
    try
    {
      if (wrapAround)
      {
        System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
        System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
        this.buffer = newbuffer;

        System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
        System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
        this.bufline = newbufline;

        System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
        System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
        this.bufcolumn = newbufcolumn;

        this.maxNextCharInd = (this.bufpos += this.bufsize - this.tokenBegin);
      }
      else
      {
        System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
        this.buffer = newbuffer;

        System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
        this.bufline = newbufline;

        System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
        this.bufcolumn = newbufcolumn;

        this.maxNextCharInd = (this.bufpos -= this.tokenBegin);
      }
    }
    catch (Throwable t)
    {
      throw new Error(t.getMessage());
    }

    this.bufsize += 2048;
    this.available = this.bufsize;
    this.tokenBegin = 0;
  }

  protected void FillBuff() throws IOException
  {
    if (this.maxNextCharInd == this.available)
    {
      if (this.available == this.bufsize)
      {
        if (this.tokenBegin > 2048)
        {
          this.bufpos = (this.maxNextCharInd = 0);
          this.available = this.tokenBegin;
        }
        else if (this.tokenBegin < 0) {
          this.bufpos = (this.maxNextCharInd = 0);
        } else {
          ExpandBuff(false);
        }
      } else if (this.available > this.tokenBegin)
        this.available = this.bufsize;
      else if (this.tokenBegin - this.available < 2048)
        ExpandBuff(true);
      else
        this.available = this.tokenBegin;
    }
    try
    {
      int i;
      if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1)
      {
        this.inputStream.close();
        throw new IOException();
      }

      this.maxNextCharInd += i;
      return;
    }
    catch (IOException e) {
      this.bufpos -= 1;
      backup(0);
      if (this.tokenBegin == -1)
        this.tokenBegin = this.bufpos;
      throw e;
    }
  }

  public char BeginToken()
    throws IOException
  {
    this.tokenBegin = -1;
    char c = readChar();
    this.tokenBegin = this.bufpos;

    return c;
  }

  protected void UpdateLineColumn(char c)
  {
    this.column += 1;

    if (this.prevCharIsLF)
    {
      this.prevCharIsLF = false;
      this.line += (this.column = 1);
    }
    else if (this.prevCharIsCR)
    {
      this.prevCharIsCR = false;
      if (c == '\n')
      {
        this.prevCharIsLF = true;
      }
      else {
        this.line += (this.column = 1);
      }
    }
    switch (c)
    {
    case '\r':
      this.prevCharIsCR = true;
      break;
    case '\n':
      this.prevCharIsLF = true;
      break;
    case '\t':
      this.column -= 1;
      this.column += this.tabSize - this.column % this.tabSize;
      break;
    case '\013':
    case '\f':
    }

    this.bufline[this.bufpos] = this.line;
    this.bufcolumn[this.bufpos] = this.column;
  }

  public char readChar()
    throws IOException
  {
    if (this.inBuf > 0)
    {
      this.inBuf -= 1;

      if (++this.bufpos == this.bufsize) {
        this.bufpos = 0;
      }
      return this.buffer[this.bufpos];
    }

    if (++this.bufpos >= this.maxNextCharInd) {
      FillBuff();
    }
    char c = this.buffer[this.bufpos];

    UpdateLineColumn(c);
    return c;
  }

  @Deprecated
  public int getColumn()
  {
    return this.bufcolumn[this.bufpos];
  }

  @Deprecated
  public int getLine()
  {
    return this.bufline[this.bufpos];
  }

  public int getEndColumn()
  {
    return this.bufcolumn[this.bufpos];
  }

  public int getEndLine()
  {
    return this.bufline[this.bufpos];
  }

  public int getBeginColumn()
  {
    return this.bufcolumn[this.tokenBegin];
  }

  public int getBeginLine()
  {
    return this.bufline[this.tokenBegin];
  }

  public void backup(int amount)
  {
    this.inBuf += amount;
    if ((this.bufpos -= amount) < 0)
      this.bufpos += this.bufsize;
  }

  public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize)
  {
    this.inputStream = dstream;
    this.line = startline;
    this.column = (startcolumn - 1);

    this.available = (this.bufsize = buffersize);
    this.buffer = new char[buffersize];
    this.bufline = new int[buffersize];
    this.bufcolumn = new int[buffersize];
  }

  public SimpleCharStream(Reader dstream, int startline, int startcolumn)
  {
    this(dstream, startline, startcolumn, 4096);
  }

  public SimpleCharStream(Reader dstream)
  {
    this(dstream, 1, 1, 4096);
  }

  public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize)
  {
    this.inputStream = dstream;
    this.line = startline;
    this.column = (startcolumn - 1);

    if ((this.buffer == null) || (buffersize != this.buffer.length))
    {
      this.available = (this.bufsize = buffersize);
      this.buffer = new char[buffersize];
      this.bufline = new int[buffersize];
      this.bufcolumn = new int[buffersize];
    }
    this.prevCharIsLF = (this.prevCharIsCR = false);
    this.tokenBegin = (this.inBuf = this.maxNextCharInd = 0);
    this.bufpos = -1;
  }

  public void ReInit(Reader dstream, int startline, int startcolumn)
  {
    ReInit(dstream, startline, startcolumn, 4096);
  }

  public void ReInit(Reader dstream)
  {
    ReInit(dstream, 1, 1, 4096);
  }

  public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
    throws UnsupportedEncodingException
  {
    this(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
  }

  public SimpleCharStream(InputStream dstream, int startline, int startcolumn, int buffersize)
  {
    this(new InputStreamReader(dstream), startline, startcolumn, buffersize);
  }

  public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn)
    throws UnsupportedEncodingException
  {
    this(dstream, encoding, startline, startcolumn, 4096);
  }

  public SimpleCharStream(InputStream dstream, int startline, int startcolumn)
  {
    this(dstream, startline, startcolumn, 4096);
  }

  public SimpleCharStream(InputStream dstream, String encoding)
    throws UnsupportedEncodingException
  {
    this(dstream, encoding, 1, 1, 4096);
  }

  public SimpleCharStream(InputStream dstream)
  {
    this(dstream, 1, 1, 4096);
  }

  public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
    throws UnsupportedEncodingException
  {
    ReInit(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
  }

  public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize)
  {
    ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
  }

  public void ReInit(InputStream dstream, String encoding)
    throws UnsupportedEncodingException
  {
    ReInit(dstream, encoding, 1, 1, 4096);
  }

  public void ReInit(InputStream dstream)
  {
    ReInit(dstream, 1, 1, 4096);
  }

  public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn)
    throws UnsupportedEncodingException
  {
    ReInit(dstream, encoding, startline, startcolumn, 4096);
  }

  public void ReInit(InputStream dstream, int startline, int startcolumn)
  {
    ReInit(dstream, startline, startcolumn, 4096);
  }

  public String GetImage()
  {
    if (this.bufpos >= this.tokenBegin) {
      return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
    }
    return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + 
      new String(this.buffer, 0, this.bufpos + 1);
  }

  public char[] GetSuffix(int len)
  {
    char[] ret = new char[len];

    if (this.bufpos + 1 >= len) {
      System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
    }
    else {
      System.arraycopy(this.buffer, this.bufsize - (len - this.bufpos - 1), ret, 0, 
        len - this.bufpos - 1);
      System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
    }

    return ret;
  }

  public void Done()
  {
    this.buffer = null;
    this.bufline = null;
    this.bufcolumn = null;
  }

  public void adjustBeginLineColumn(int newLine, int newCol)
  {
    int start = this.tokenBegin;
    int len;
    if (this.bufpos >= this.tokenBegin)
    {
      len = this.bufpos - this.tokenBegin + this.inBuf + 1;
    }
    else
    {
      len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
    }

    int i = 0; int j = 0; int k = 0;
    int nextColDiff = 0; int columnDiff = 0;

    while ((i < len) && (this.bufline[(j = start % this.bufsize)] == this.bufline[(k = ++start % this.bufsize)]))
    {
      this.bufline[j] = newLine;
      nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
      this.bufcolumn[j] = (newCol + columnDiff);
      columnDiff = nextColDiff;
      i++;
    }

    if (i < len)
    {
      this.bufline[j] = (newLine++);
      this.bufcolumn[j] = (newCol + columnDiff);

      while (i++ < len)
      {
        if (this.bufline[(j = start % this.bufsize)] != this.bufline[(++start % this.bufsize)])
          this.bufline[j] = (newLine++);
        else {
          this.bufline[j] = newLine;
        }
      }
    }
    this.line = this.bufline[j];
    this.column = this.bufcolumn[j];
  }
}