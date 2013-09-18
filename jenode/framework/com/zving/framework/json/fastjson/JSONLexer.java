package com.zving.framework.json.fastjson;

import java.math.BigDecimal;
import java.util.Calendar;

public abstract interface JSONLexer
{
  public abstract void nextToken();

  public abstract void nextToken(int paramInt);

  public abstract int token();

  public abstract int pos();

  public abstract String stringVal();

  public abstract Number integerValue();

  public abstract void nextTokenWithColon(int paramInt);

  public abstract BigDecimal decimalValue();

  public abstract double doubleValue();

  public abstract float floatValue();

  public abstract void config(Feature paramFeature, boolean paramBoolean);

  public abstract boolean isEnabled(Feature paramFeature);

  public abstract String numberString();

  public abstract boolean isEOF();

  public abstract String symbol(SymbolTable paramSymbolTable);

  public abstract boolean isBlankInput();

  public abstract char getCurrent();

  public abstract void skipWhitespace();

  public abstract void incrementBufferPosition();

  public abstract String scanSymbol(SymbolTable paramSymbolTable);

  public abstract String scanSymbol(SymbolTable paramSymbolTable, char paramChar);

  public abstract void resetStringPosition();

  public abstract String scanSymbolUnQuoted(SymbolTable paramSymbolTable);

  public abstract void scanString();

  public abstract void scanNumber();

  public abstract boolean scanISO8601DateIfMatch();

  public abstract Calendar getCalendar();

  public abstract int intValue()
    throws NumberFormatException;

  public abstract long longValue()
    throws NumberFormatException;
}