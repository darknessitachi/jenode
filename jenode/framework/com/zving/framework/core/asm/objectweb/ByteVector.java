package com.zving.framework.core.asm.objectweb;

public class ByteVector
{
  byte[] data;
  int length;

  public ByteVector()
  {
    this.data = new byte[64];
  }

  public ByteVector(int initialSize)
  {
    this.data = new byte[initialSize];
  }

  public ByteVector putByte(int b)
  {
    int length = this.length;
    if (length + 1 > this.data.length) {
      enlarge(1);
    }
    this.data[(length++)] = ((byte)b);
    this.length = length;
    return this;
  }

  ByteVector put11(int b1, int b2)
  {
    int length = this.length;
    if (length + 2 > this.data.length) {
      enlarge(2);
    }
    byte[] data = this.data;
    data[(length++)] = ((byte)b1);
    data[(length++)] = ((byte)b2);
    this.length = length;
    return this;
  }

  public ByteVector putShort(int s)
  {
    int length = this.length;
    if (length + 2 > this.data.length) {
      enlarge(2);
    }
    byte[] data = this.data;
    data[(length++)] = ((byte)(s >>> 8));
    data[(length++)] = ((byte)s);
    this.length = length;
    return this;
  }

  ByteVector put12(int b, int s)
  {
    int length = this.length;
    if (length + 3 > this.data.length) {
      enlarge(3);
    }
    byte[] data = this.data;
    data[(length++)] = ((byte)b);
    data[(length++)] = ((byte)(s >>> 8));
    data[(length++)] = ((byte)s);
    this.length = length;
    return this;
  }

  public ByteVector putInt(int i)
  {
    int length = this.length;
    if (length + 4 > this.data.length) {
      enlarge(4);
    }
    byte[] data = this.data;
    data[(length++)] = ((byte)(i >>> 24));
    data[(length++)] = ((byte)(i >>> 16));
    data[(length++)] = ((byte)(i >>> 8));
    data[(length++)] = ((byte)i);
    this.length = length;
    return this;
  }

  public ByteVector putLong(long l)
  {
    int length = this.length;
    if (length + 8 > this.data.length) {
      enlarge(8);
    }
    byte[] data = this.data;
    int i = (int)(l >>> 32);
    data[(length++)] = ((byte)(i >>> 24));
    data[(length++)] = ((byte)(i >>> 16));
    data[(length++)] = ((byte)(i >>> 8));
    data[(length++)] = ((byte)i);
    i = (int)l;
    data[(length++)] = ((byte)(i >>> 24));
    data[(length++)] = ((byte)(i >>> 16));
    data[(length++)] = ((byte)(i >>> 8));
    data[(length++)] = ((byte)i);
    this.length = length;
    return this;
  }

  public ByteVector putUTF8(String s)
  {
    int charLength = s.length();
    int len = this.length;
    if (len + 2 + charLength > this.data.length) {
      enlarge(2 + charLength);
    }
    byte[] data = this.data;

    data[(len++)] = ((byte)(charLength >>> 8));
    data[(len++)] = ((byte)charLength);
    for (int i = 0; i < charLength; i++) {
      char c = s.charAt(i);
      if ((c >= '\001') && (c <= '')) {
        data[(len++)] = ((byte)c);
      } else {
        int byteLength = i;
        for (int j = i; j < charLength; j++) {
          c = s.charAt(j);
          if ((c >= '\001') && (c <= ''))
            byteLength++;
          else if (c > '߿')
            byteLength += 3;
          else {
            byteLength += 2;
          }
        }
        data[this.length] = ((byte)(byteLength >>> 8));
        data[(this.length + 1)] = ((byte)byteLength);
        if (this.length + 2 + byteLength > data.length) {
          this.length = len;
          enlarge(2 + byteLength);
          data = this.data;
        }
        for (int j = i; j < charLength; j++) {
          c = s.charAt(j);
          if ((c >= '\001') && (c <= '')) {
            data[(len++)] = ((byte)c);
          } else if (c > '߿') {
            data[(len++)] = ((byte)(0xE0 | c >> '\f' & 0xF));
            data[(len++)] = ((byte)(0x80 | c >> '\006' & 0x3F));
            data[(len++)] = ((byte)(0x80 | c & 0x3F));
          } else {
            data[(len++)] = ((byte)(0xC0 | c >> '\006' & 0x1F));
            data[(len++)] = ((byte)(0x80 | c & 0x3F));
          }
        }
        break;
      }
    }
    this.length = len;
    return this;
  }

  public ByteVector putByteArray(byte[] b, int off, int len)
  {
    if (this.length + len > this.data.length) {
      enlarge(len);
    }
    if (b != null) {
      System.arraycopy(b, off, this.data, this.length, len);
    }
    this.length += len;
    return this;
  }

  private void enlarge(int size)
  {
    int length1 = 2 * this.data.length;
    int length2 = this.length + size;
    byte[] newData = new byte[length1 > length2 ? length1 : length2];
    System.arraycopy(this.data, 0, newData, 0, this.length);
    this.data = newData;
  }
}