package com.zving.framework.expression;

public class StringLiteral extends Literal
{
  StringLiteral(Object pValue)
  {
    super(pValue);
  }

  public static StringLiteral fromToken(String pToken)
  {
    return new StringLiteral(getValueFromToken(pToken));
  }

  public static StringLiteral fromLiteralValue(String pValue)
  {
    return new StringLiteral(pValue);
  }

  public static String getValueFromToken(String pToken)
  {
    StringBuffer buf = new StringBuffer();
    int len = pToken.length() - 1;
    boolean escaping = false;
    for (int i = 1; i < len; i++) {
      char ch = pToken.charAt(i);
      if (escaping) {
        buf.append(ch);
        escaping = false;
      }
      else if (ch == '\\') {
        escaping = true;
      }
      else {
        buf.append(ch);
      }
    }
    return buf.toString();
  }

  public static String toStringToken(String pValue)
  {
    if ((pValue.indexOf('"') < 0) && 
      (pValue.indexOf('\\') < 0)) {
      return "\"" + pValue + "\"";
    }

    StringBuffer buf = new StringBuffer();
    buf.append('"');
    int len = pValue.length();
    for (int i = 0; i < len; i++) {
      char ch = pValue.charAt(i);
      if (ch == '\\') {
        buf.append('\\');
        buf.append('\\');
      }
      else if (ch == '"') {
        buf.append('\\');
        buf.append('"');
      }
      else {
        buf.append(ch);
      }
    }
    buf.append('"');
    return buf.toString();
  }

  public static String toIdentifierToken(String pValue)
  {
    if (isJavaIdentifier(pValue)) {
      return pValue;
    }

    return toStringToken(pValue);
  }

  static boolean isJavaIdentifier(String pValue)
  {
    int len = pValue.length();
    if (len == 0) {
      return false;
    }

    if (!Character.isJavaIdentifierStart(pValue.charAt(0))) {
      return false;
    }

    for (int i = 1; i < len; i++) {
      if (!Character.isJavaIdentifierPart(pValue.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public String getExpressionString()
  {
    return toStringToken((String)getValue());
  }
}