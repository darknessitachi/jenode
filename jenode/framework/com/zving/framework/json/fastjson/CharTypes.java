package com.zving.framework.json.fastjson;

public final class CharTypes
{
  public static final boolean[] firstIdentifierFlags = new boolean[256];
  public static final boolean[] identifierFlags;
  public static final boolean[] specicalFlags_doubleQuotes;
  public static final boolean[] specicalFlags_singleQuotes;
  public static final char[] replaceChars;

  static
  {
    for (char c = '\000'; c < firstIdentifierFlags.length; c = (char)(c + '\001')) {
      if ((c >= 'A') && (c <= 'Z'))
        firstIdentifierFlags[c] = true;
      else if ((c >= 'a') && (c <= 'z'))
        firstIdentifierFlags[c] = true;
      else if (c == '_') {
        firstIdentifierFlags[c] = true;
      }

    }

    identifierFlags = new boolean[256];

    for (char c = '\000'; c < identifierFlags.length; c = (char)(c + '\001')) {
      if ((c >= 'A') && (c <= 'Z'))
        identifierFlags[c] = true;
      else if ((c >= 'a') && (c <= 'z'))
        identifierFlags[c] = true;
      else if (c == '_')
        identifierFlags[c] = true;
      else if ((c >= '0') && (c <= '9')) {
        identifierFlags[c] = true;
      }

    }

    specicalFlags_doubleQuotes = new boolean[93];
    specicalFlags_singleQuotes = new boolean[93];

    replaceChars = new char[93];

    specicalFlags_doubleQuotes[8] = true;
    specicalFlags_doubleQuotes[10] = true;
    specicalFlags_doubleQuotes[12] = true;
    specicalFlags_doubleQuotes[13] = true;
    specicalFlags_doubleQuotes[34] = true;
    specicalFlags_doubleQuotes[92] = true;

    specicalFlags_singleQuotes[8] = true;
    specicalFlags_singleQuotes[10] = true;
    specicalFlags_singleQuotes[12] = true;
    specicalFlags_singleQuotes[13] = true;
    specicalFlags_singleQuotes[39] = true;
    specicalFlags_singleQuotes[92] = true;

    replaceChars[8] = 'b';
    replaceChars[10] = 'n';
    replaceChars[12] = 'f';
    replaceChars[13] = 'r';
    replaceChars[34] = '"';
    replaceChars[39] = '\'';
    replaceChars[92] = '\\';
    replaceChars[9] = 't';
  }

  public static boolean isSpecial_doubleQuotes(char ch)
  {
    return (ch < specicalFlags_doubleQuotes.length) && (specicalFlags_doubleQuotes[ch] != 0);
  }
}