package com.zving.framework.expression.parser;

public abstract interface ELParserConstants
{
  public static final int EOF = 0;
  public static final int NON_EXPRESSION_TEXT = 1;
  public static final int START_EXPRESSION = 2;
  public static final int INTEGER_LITERAL = 7;
  public static final int FLOATING_POINT_LITERAL = 8;
  public static final int EXPONENT = 9;
  public static final int STRING_LITERAL = 10;
  public static final int BADLY_ESCAPED_STRING_LITERAL = 11;
  public static final int TRUE = 12;
  public static final int FALSE = 13;
  public static final int NULL = 14;
  public static final int END_EXPRESSION = 15;
  public static final int DOT = 16;
  public static final int GT1 = 17;
  public static final int GT2 = 18;
  public static final int LT1 = 19;
  public static final int LT2 = 20;
  public static final int EQ1 = 21;
  public static final int EQ2 = 22;
  public static final int LE1 = 23;
  public static final int LE2 = 24;
  public static final int GE1 = 25;
  public static final int GE2 = 26;
  public static final int NE1 = 27;
  public static final int NE2 = 28;
  public static final int LPAREN = 29;
  public static final int RPAREN = 30;
  public static final int COMMA = 31;
  public static final int COLON = 32;
  public static final int LBRACKET = 33;
  public static final int RBRACKET = 34;
  public static final int PLUS = 35;
  public static final int MINUS = 36;
  public static final int MULTIPLY = 37;
  public static final int DIVIDE1 = 38;
  public static final int DIVIDE2 = 39;
  public static final int MODULUS1 = 40;
  public static final int MODULUS2 = 41;
  public static final int NOT1 = 42;
  public static final int NOT2 = 43;
  public static final int AND1 = 44;
  public static final int AND2 = 45;
  public static final int OR1 = 46;
  public static final int OR2 = 47;
  public static final int EMPTY = 48;
  public static final int COND = 49;
  public static final int IDENTIFIER = 50;
  public static final int IMPL_OBJ_START = 51;
  public static final int LETTER = 52;
  public static final int DIGIT = 53;
  public static final int ILLEGAL_CHARACTER = 54;
  public static final int DEFAULT = 0;
  public static final int IN_EXPRESSION = 1;
  public static final String[] tokenImage = { 
    "<EOF>", 
    "<NON_EXPRESSION_TEXT>", 
    "\"${\"", 
    "\" \"", 
    "\"\\t\"", 
    "\"\\n\"", 
    "\"\\r\"", 
    "<INTEGER_LITERAL>", 
    "<FLOATING_POINT_LITERAL>", 
    "<EXPONENT>", 
    "<STRING_LITERAL>", 
    "<BADLY_ESCAPED_STRING_LITERAL>", 
    "\"true\"", 
    "\"false\"", 
    "\"null\"", 
    "\"}\"", 
    "\".\"", 
    "\">\"", 
    "\"gt\"", 
    "\"<\"", 
    "\"lt\"", 
    "\"==\"", 
    "\"eq\"", 
    "\"<=\"", 
    "\"le\"", 
    "\">=\"", 
    "\"ge\"", 
    "\"!=\"", 
    "\"ne\"", 
    "\"(\"", 
    "\")\"", 
    "\",\"", 
    "\":\"", 
    "\"[\"", 
    "\"]\"", 
    "\"+\"", 
    "\"-\"", 
    "\"*\"", 
    "\"/\"", 
    "\"div\"", 
    "\"%\"", 
    "\"mod\"", 
    "\"not\"", 
    "\"!\"", 
    "\"and\"", 
    "\"&&\"", 
    "\"or\"", 
    "\"||\"", 
    "\"empty\"", 
    "\"?\"", 
    "<IDENTIFIER>", 
    "\"#\"", 
    "<LETTER>", 
    "<DIGIT>", 
    "<ILLEGAL_CHARACTER>" };
}