package com.zving.framework.json.fastjson;

import com.zving.framework.json.JSONException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class IOUtils
{
  static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 
    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

  static final char[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', 
    '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', 
    '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', 
    '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', 
    '9', '9', '9', '9', '9' };

  static final char[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', 
    '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', 
    '5', '6', '7', '8', '9' };

  static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, 2147483647 };

  static final int[] byte_len_array = new int[256];

  static { for (int i = -128; i <= 127; i++) {
      int size = i < 0 ? stringSize(-i) + 1 : stringSize(i);
      byte_len_array[(i & 0xFF)] = size;
    }
  }

  public static int stringSize(long x)
  {
    long p = 10L;
    for (int i = 1; i < 19; i++) {
      if (x < p)
        return i;
      p *= 10L;
    }
    return 19;
  }

  public static void getChars(long i, int index, char[] buf)
  {
    int charPos = index;
    char sign = '\000';

    if (i < 0L) {
      sign = '-';
      i = -i;
    }

    while (i > 2147483647L) {
      long q = i / 100L;

      int r = (int)(i - ((q << 6) + (q << 5) + (q << 2)));
      i = q;
      buf[(--charPos)] = DigitOnes[r];
      buf[(--charPos)] = DigitTens[r];
    }

    int i2 = (int)i;
    while (i2 >= 65536) {
      int q2 = i2 / 100;

      int r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
      i2 = q2;
      buf[(--charPos)] = DigitOnes[r];
      buf[(--charPos)] = DigitTens[r];
    }

    do
    {
      int q2 = i2 * 52429 >>> 19;
      int r = i2 - ((q2 << 3) + (q2 << 1));
      buf[(--charPos)] = digits[r];
      i2 = q2;
    }while (i2 != 0);

    if (sign != 0)
      buf[(--charPos)] = sign;
  }

  public static void getChars(int i, int index, char[] buf)
  {
    int charPos = index;
    char sign = '\000';

    if (i < 0) {
      sign = '-';
      i = -i;
    }

    while (i >= 65536) {
      int q = i / 100;

      int r = i - ((q << 6) + (q << 5) + (q << 2));
      i = q;
      buf[(--charPos)] = DigitOnes[r];
      buf[(--charPos)] = DigitTens[r];
    }

    do
    {
      int q = i * 52429 >>> 19;
      int r = i - ((q << 3) + (q << 1));
      buf[(--charPos)] = digits[r];
      i = q;
    }while (i != 0);

    if (sign != 0)
      buf[(--charPos)] = sign;
  }

  public static void getChars(byte b, int index, char[] buf)
  {
    int i = b;

    int charPos = index;
    char sign = '\000';

    if (i < 0) {
      sign = '-';
      i = -i;
    }

    do
    {
      int q = i * 52429 >>> 19;
      int r = i - ((q << 3) + (q << 1));
      buf[(--charPos)] = digits[r];
      i = q;
    }while (i != 0);

    if (sign != 0)
      buf[(--charPos)] = sign;
  }

  static int stringSize(int x)
  {
    for (int i = 0; ; i++)
      if (x <= sizeTable[i])
        return i + 1;
  }

  public static int stringSize(byte i)
  {
    return byte_len_array[(i & 0xFF)];
  }

  public static void decode(CharsetDecoder charsetDecoder, ByteBuffer byteBuf, CharBuffer charByte) {
    try {
      CoderResult cr = charsetDecoder.decode(byteBuf, charByte, true);

      if (!cr.isUnderflow()) {
        cr.throwException();
      }

      cr = charsetDecoder.flush(charByte);

      if (!cr.isUnderflow()) {
        cr.throwException();
      }
    }
    catch (CharacterCodingException x)
    {
      throw new JSONException(x.getMessage(), x);
    }
  }
}