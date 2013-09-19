package com.zving.framework.json.fastjson;

import com.zving.framework.json.JSONArray;
import com.zving.framework.json.JSONException;
import com.zving.framework.json.JSONObject;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public abstract class AbstractJSONParser
{
  public abstract void parseObject(Map paramMap);

  public JSONObject parseObject()
  {
    JSONObject object = new JSONObject();
    parseObject(object);
    return object;
  }

  public final void parseArray(Collection array)
  {
    JSONLexer lexer = getLexer();

    if (lexer.token() != 14) {
      throw new JSONException("syntax error, expect [, actual " + JSONToken.name(lexer.token()));
    }

    lexer.nextToken(4);
    while (true)
    {
      if (isEnabled(Feature.AllowArbitraryCommas))
        while (lexer.token() == 16)
          lexer.nextToken();
      Object value;
      switch (lexer.token()) {
      case 2:
         value = lexer.integerValue();
        lexer.nextToken(16);
        break;
      case 3:
        if (lexer.isEnabled(Feature.UseBigDecimal))
          value = lexer.decimalValue();
        else {
          value = Double.valueOf(lexer.doubleValue());
        }
        lexer.nextToken(16);
        break;
      case 4:
        String stringLiteral = lexer.stringVal();
        lexer.nextToken(16);
        if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
          JSONScanner iso8601Lexer = new JSONScanner(stringLiteral);
          if (iso8601Lexer.scanISO8601DateIfMatch())
            value = iso8601Lexer.getCalendar().getTime();
          else
            value = stringLiteral;
        }
        else {
          value = stringLiteral;
        }

        break;
      case 6:
         value = Boolean.TRUE;
        lexer.nextToken(16);
        break;
      case 7:
         value = Boolean.FALSE;
        lexer.nextToken(16);
        break;
      case 12:
        JSONObject object = new JSONObject();
        parseObject(object);
        value = object;
        break;
      case 14:
        Collection items = new JSONArray();
        parseArray(items);
        value = items;
        break;
      case 8:
         value = null;
        lexer.nextToken(4);
        break;
      case 15:
        lexer.nextToken(16);
        return;
      case 5:
      case 9:
      case 10:
      case 11:
      case 13:
      default:
        value = parse();
      }

      array.add(value);

      if (lexer.token() == 16)
        lexer.nextToken(4);
    }
  }

  public Object parse()
  {
    JSONLexer lexer = getLexer();
    switch (lexer.token()) {
    case 14:
      JSONArray array = new JSONArray();
      parseArray(array);
      return array;
    case 12:
      JSONObject object = new JSONObject();
      parseObject(object);
      return object;
    case 2:
      Number intValue = lexer.integerValue();
      lexer.nextToken();
      return intValue;
    case 3:
      Object value;
      if (isEnabled(Feature.UseBigDecimal))
        value = lexer.decimalValue();
      else {
        value = Double.valueOf(lexer.doubleValue());
      }
      lexer.nextToken();
      return value;
    case 4:
      String stringLiteral = lexer.stringVal();
      lexer.nextToken(16);

      if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
        JSONScanner iso8601Lexer = new JSONScanner(stringLiteral);
        if (iso8601Lexer.scanISO8601DateIfMatch()) {
          return iso8601Lexer.getCalendar().getTime();
        }
      }

      return stringLiteral;
    case 8:
      lexer.nextToken();
      return null;
    case 6:
      lexer.nextToken();
      return Boolean.TRUE;
    case 7:
      lexer.nextToken();
      return Boolean.FALSE;
    case 9:
      lexer.nextToken(18);

      if (lexer.token() != 18) {
        throw new JSONException("syntax error");
      }
      lexer.nextToken(10);

      accept(10);
      long time = lexer.integerValue().longValue();
      accept(2);

      accept(11);

      return new Date(time);
    case 20:
      if (lexer.isBlankInput())
        return null; break;
    case 5:
    case 10:
    case 11:
    case 13:
    case 15:
    case 16:
    case 17:
    case 18:
    case 19: } throw new JSONException("TODO " + JSONToken.name(lexer.token()) + " " + lexer.stringVal());
  }

  public void config(Feature feature, boolean state)
  {
    getLexer().config(feature, state);
  }

  public boolean isEnabled(Feature feature) {
    return getLexer().isEnabled(feature);
  }

  public abstract JSONLexer getLexer();

  public final void accept(int token) {
    JSONLexer lexer = getLexer();
    if (lexer.token() == token)
      lexer.nextToken();
    else
      throw new JSONException("syntax error, expect " + JSONToken.name(token) + ", actual " + JSONToken.name(lexer.token()));
  }

  public void close()
  {
    JSONLexer lexer = getLexer();

    if ((isEnabled(Feature.AutoCloseSource)) && 
      (!lexer.isEOF()))
      throw new JSONException("not close json text, token : " + JSONToken.name(lexer.token()));
  }
}