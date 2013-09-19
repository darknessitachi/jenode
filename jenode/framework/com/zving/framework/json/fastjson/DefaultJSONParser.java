package com.zving.framework.json.fastjson;

import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONArray;
import com.zving.framework.json.JSONException;
import com.zving.framework.json.JSONObject;
import java.util.Calendar;
import java.util.Map;

public class DefaultJSONParser extends AbstractJSONParser
{
  public static final int DEFAULT_PARSER_FEATURE ;
  protected final JSONLexer lexer;
  protected final Object input;
  protected final SymbolTable symbolTable;
  protected ParserConfig config;

  static
  {
    int features = 0;
    features |= Feature.AutoCloseSource.getMask();
    features |= Feature.InternFieldNames.getMask();
    features |= Feature.UseBigDecimal.getMask();
    features |= Feature.AllowUnQuotedFieldNames.getMask();
    features |= Feature.AllowSingleQuotes.getMask();
    features |= Feature.AllowArbitraryCommas.getMask();
    features |= Feature.SortFeidFastMatch.getMask();
    features |= Feature.IgnoreNotMatch.getMask();
    DEFAULT_PARSER_FEATURE = features;
  }

  public DefaultJSONParser(String input)
  {
    this(input, ParserConfig.getGlobalInstance(), DEFAULT_PARSER_FEATURE);
  }

  public DefaultJSONParser(String input, ParserConfig config) {
    this(input, new JSONScanner(input, DEFAULT_PARSER_FEATURE), config);
  }

  public DefaultJSONParser(String input, ParserConfig config, int features) {
    this(input, new JSONScanner(input, features), config);
  }

  public DefaultJSONParser(char[] input, int length, ParserConfig config, int features) {
    this(input, new JSONScanner(input, length, features), config);
  }

  public DefaultJSONParser(Object input, JSONLexer lexer, ParserConfig config) {
    this.input = input;
    this.lexer = lexer;
    this.config = config;
    this.symbolTable = config.getSymbolTable();

    lexer.nextToken(12);
  }

  public SymbolTable getSymbolTable() {
    return this.symbolTable;
  }

  public JSONLexer getLexer() {
    return this.lexer;
  }

  public String getInput() {
    if ((this.input instanceof char[])) {
      return new String((char[])this.input);
    }
    return this.input.toString();
  }

  public final void parseObject(Map object)
  {
    JSONScanner lexer = (JSONScanner)this.lexer;
    if (lexer.token() != 12)
      throw new JSONException("syntax error, expect {, actual " + lexer.token()); char ch;
    String key;
    while (true) {
      lexer.skipWhitespace();
      ch = lexer.getCurrent();
      if (isEnabled(Feature.AllowArbitraryCommas)) {
        while (ch == ',') {
          lexer.incrementBufferPosition();
          lexer.skipWhitespace();
          ch = lexer.getCurrent();
        }

      }

      if (ch == '"') {
         key = lexer.scanSymbol(this.symbolTable, '"');
        lexer.skipWhitespace();
        ch = lexer.getCurrent();
        if (ch != ':')
          throw new JSONException("expect ':' at " + lexer.pos() + ", name " + key);
      } else {
        if (ch == '}') {
          lexer.incrementBufferPosition();
          lexer.resetStringPosition();
          lexer.nextToken();
          return;
        }if (ch == '\'') {
          if (!isEnabled(Feature.AllowSingleQuotes)) {
            throw new JSONException("syntax error");
          }

           key = lexer.scanSymbol(this.symbolTable, '\'');
          lexer.skipWhitespace();
          ch = lexer.getCurrent();
          if (ch != ':')
            throw new JSONException("expect ':' at " + lexer.pos());
        } else {
          if (ch == '\032')
            throw new JSONException("syntax error");
          if (ch == ',') {
            throw new JSONException("syntax error");
          }
          if (!isEnabled(Feature.AllowUnQuotedFieldNames)) {
            throw new JSONException("syntax error");
          }

          key = lexer.scanSymbolUnQuoted(this.symbolTable);
          lexer.skipWhitespace();
          ch = lexer.getCurrent();
          if (ch != ':') {
            throw new JSONException("expect ':' at " + lexer.pos() + ", actual " + ch);
          }
        }
      }
      lexer.incrementBufferPosition();
      lexer.skipWhitespace();
      ch = lexer.getCurrent();

      lexer.resetStringPosition();

      if (ch == '"') {
        lexer.scanString();
        String strValue = lexer.stringVal();
        Object value = strValue;

        if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
          JSONScanner iso8601Lexer = new JSONScanner(strValue);
          if (iso8601Lexer.scanISO8601DateIfMatch()) {
            value = iso8601Lexer.getCalendar().getTime();
          }
        }

        object.put(key, value);
      } else if (((ch >= '0') && (ch <= '9')) || (ch == '-')) {
        lexer.scanNumber();
        Object value;
        if (lexer.token() == 2)
          value = lexer.integerValue();
        else {
          value = lexer.decimalValue();
        }

        object.put(key, value); } else {
        if (ch == '[') {
          lexer.nextToken();
          JSONArray list = new JSONArray();
          parseArray(list);
          Object value = list;
          object.put(key, value);

          if (lexer.token() == 13) {
            lexer.nextToken();
            return;
          }if (lexer.token() == 16) {
            continue;
          }
          throw new JSONException("syntax error");
        }
        if (ch == '{') {
          lexer.nextToken();
          JSONObject obj = new JSONObject();
          parseObject(obj);
          object.put(key, JSON.tryReverse(obj));

          if (lexer.token() == 13) {
            lexer.nextToken();
            return;
          }if (lexer.token() == 16) {
            continue;
          }
          throw new JSONException("syntax error");
        }

        lexer.nextToken();
        Object value = parse();
        object.put(key, value);

        if (lexer.token() == 13) {
          lexer.nextToken();
          return;
        }if (lexer.token() == 16) {
          continue;
        }
        throw new JSONException("syntax error, position at " + lexer.pos() + ", name " + key);
      }

      lexer.skipWhitespace();
      ch = lexer.getCurrent();
      if (ch != ',') break;
      lexer.incrementBufferPosition();
    }
    if (ch == '}') {
      lexer.incrementBufferPosition();
      lexer.resetStringPosition();
      lexer.nextToken();
      return;
    }
    throw new JSONException("syntax error, position at " + lexer.pos() + ", name " + key);
  }
}