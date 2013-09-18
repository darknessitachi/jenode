package com.zving.framework.expression.parser;

import java.io.IOException;
import java.io.PrintStream;

public class ELParserTokenManager
  implements ELParserConstants
{
  public PrintStream debugStream = System.out;

  static final long[] jjbitVec0 = { 
    -2L, -1L, -1L, -1L };

  static final long[] jjbitVec2 = { 
    0, 0, -1L, -1L };

  static final long[] jjbitVec3 = { 
    2301339413881290750L, -16384L, 4294967295L, 432345564227567616L };

  static final long[] jjbitVec4 = { 
    0, 0, 0, -36028797027352577L };

  static final long[] jjbitVec5 = { 
    0, -1L, -1L, -1L };

  static final long[] jjbitVec6 = { 
    -1L, -1L, 65535L };

  static final long[] jjbitVec7 = { 
    -1L, -1L };

  static final long[] jjbitVec8 = { 
    70368744177663L };

  static final int[] jjnextStates = { 
    8, 9, 10, 15, 16, 28, 29, 31, 32, 33, 20, 21, 23, 24, 25, 20, 
    21, 23, 28, 29, 31, 3, 4, 13, 14, 17, 18, 24, 25, 32, 33 };

  public static final String[] jjstrLiteralImages = { 
    "", 0, "${", 
    0, 0, 0, 0, 0, 0, 0, 0, 0, "true", "false", "null", "}", ".", ">", "gt", 
    "<", "lt", "==", "eq", "<=", "le", ">=", "ge", 
    "!=", "ne", "(", ")", ",", ":", "[", "]", "+", "-", "*", 
    "/", "div", "%", "mod", "not", "!", "and", 
    "&&", "or", "||", "empty", "?" };

  public static final String[] lexStateNames = { 
    "DEFAULT", 
    "IN_EXPRESSION" };

  public static final int[] jjnewLexState = { 
    -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1 };

  static final long[] jjtoToken = { 
    20266198323166599L };

  static final long[] jjtoSkip = { 
    120L };
  protected SimpleCharStream input_stream;
  private final int[] jjrounds = new int[35];
  private final int[] jjstateSet = new int[70];
  protected char curChar;
  int curLexState = 0;
  int defaultLexState = 0;
  int jjnewStateCnt;
  int jjround;
  int jjmatchedPos;
  int jjmatchedKind;

  public void setDebugStream(PrintStream ds)
  {
    this.debugStream = ds;
  }
  private final int jjStopStringLiteralDfa_0(int pos, long active0) {
    switch (pos)
    {
    case 0:
      if ((active0 & 0x4) != 0L)
      {
        this.jjmatchedKind = 1;
        return 2;
      }
      return -1;
    }
    return -1;
  }

  private final int jjStartNfa_0(int pos, long active0)
  {
    return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
  }

  private int jjStopAtPos(int pos, int kind) {
    this.jjmatchedKind = kind;
    this.jjmatchedPos = pos;
    return pos + 1;
  }

  private int jjMoveStringLiteralDfa0_0() {
    switch (this.curChar)
    {
    case '$':
      return jjMoveStringLiteralDfa1_0(4L);
    }
    return jjMoveNfa_0(1, 0);
  }

  private int jjMoveStringLiteralDfa1_0(long active0) {
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
    }
    switch (this.curChar)
    {
    case '{':
      if ((active0 & 0x4) != 0L) {
        return jjStopAtPos(1, 2);
      }
      break;
    }

    return jjStartNfa_0(0, active0);
  }

  private int jjMoveNfa_0(int startState, int curPos)
  {
    int startsAt = 0;
    this.jjnewStateCnt = 3;
    int i = 1;
    this.jjstateSet[0] = startState;
    int kind = 2147483647;
    while (true)
    {
      if (++this.jjround == 2147483647)
        ReInitRounds();
      if (this.curChar < '@')
      {
        long l = 1L << this.curChar;
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 1:
            if ((0xFFFFFFFF & l) != 0L)
            {
              if (kind > 1)
                kind = 1;
              jjCheckNAdd(0);
            }
            else if (this.curChar == '$')
            {
              if (kind > 1)
                kind = 1;
              jjCheckNAdd(2);
            }
            break;
          case 0:
            if ((0xFFFFFFFF & l) != 0L)
            {
              if (kind > 1)
                kind = 1;
              jjCheckNAdd(0);
            }break;
          case 2:
            if ((0xFFFFFFFF & l) != 0L)
            {
              if (kind > 1)
                kind = 1;
              jjCheckNAdd(2);
            }break;
          }
        }
        while (i != startsAt);
      }
      else if (this.curChar < '')
      {
        long l = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0:
          case 1:
            if (kind > 1)
              kind = 1;
            jjCheckNAdd(0);
            break;
          case 2:
            if ((0xFFFFFFFF & l) != 0L)
            {
              if (kind > 1)
                kind = 1;
              this.jjstateSet[(this.jjnewStateCnt++)] = 2;
            }break;
          }
        }
        while (i != startsAt);
      }
      else
      {
        int hiByte = this.curChar >> '\b';
        int i1 = hiByte >> 6;
        long l1 = 1L << (hiByte & 0x3F);
        int i2 = (this.curChar & 0xFF) >> '\006';
        long l2 = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0:
          case 1:
            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
            {
              if (kind > 1)
                kind = 1;
              jjCheckNAdd(0);
            }break;
          case 2:
            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
            {
              if (kind > 1)
                kind = 1;
              this.jjstateSet[(this.jjnewStateCnt++)] = 2;
            }break;
          }
        }
        while (i != startsAt);
      }
      if (kind != 2147483647)
      {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = curPos;
        kind = 2147483647;
      }
      curPos++;
      if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt)))
        return curPos; try {
        this.curChar = this.input_stream.readChar(); } catch (IOException e) {  }
    }return curPos;
  }

  private final int jjStopStringLiteralDfa_1(int pos, long active0)
  {
    switch (pos)
    {
    case 0:
      if ((active0 & 0x10000) != 0L)
        return 1;
      if ((active0 & 0x15547000) != 0L)
      {
        this.jjmatchedKind = 50;
        return 6;
      }
      return -1;
    case 1:
      if ((active0 & 0x15540000) != 0L)
        return 6;
      if ((active0 & 0x7000) != 0L)
      {
        this.jjmatchedKind = 50;
        this.jjmatchedPos = 1;
        return 6;
      }
      return -1;
    case 2:
      if ((active0 & 0x0) != 0L)
        return 6;
      if ((active0 & 0x7000) != 0L)
      {
        this.jjmatchedKind = 50;
        this.jjmatchedPos = 2;
        return 6;
      }
      return -1;
    case 3:
      if ((active0 & 0x2000) != 0L)
      {
        this.jjmatchedKind = 50;
        this.jjmatchedPos = 3;
        return 6;
      }
      if ((active0 & 0x5000) != 0L)
        return 6;
      return -1;
    }
    return -1;
  }

  private final int jjStartNfa_1(int pos, long active0)
  {
    return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
  }

  private int jjMoveStringLiteralDfa0_1() {
    switch (this.curChar)
    {
    case '!':
      this.jjmatchedKind = 43;
      return jjMoveStringLiteralDfa1_1(134217728L);
    case '%':
      return jjStopAtPos(0, 40);
    case '&':
      return jjMoveStringLiteralDfa1_1(35184372088832L);
    case '(':
      return jjStopAtPos(0, 29);
    case ')':
      return jjStopAtPos(0, 30);
    case '*':
      return jjStopAtPos(0, 37);
    case '+':
      return jjStopAtPos(0, 35);
    case ',':
      return jjStopAtPos(0, 31);
    case '-':
      return jjStopAtPos(0, 36);
    case '.':
      return jjStartNfaWithStates_1(0, 16, 1);
    case '/':
      return jjStopAtPos(0, 38);
    case ':':
      return jjStopAtPos(0, 32);
    case '<':
      this.jjmatchedKind = 19;
      return jjMoveStringLiteralDfa1_1(8388608L);
    case '=':
      return jjMoveStringLiteralDfa1_1(2097152L);
    case '>':
      this.jjmatchedKind = 17;
      return jjMoveStringLiteralDfa1_1(33554432L);
    case '?':
      return jjStopAtPos(0, 49);
    case '[':
      return jjStopAtPos(0, 33);
    case ']':
      return jjStopAtPos(0, 34);
    case 'a':
      return jjMoveStringLiteralDfa1_1(17592186044416L);
    case 'd':
      return jjMoveStringLiteralDfa1_1(549755813888L);
    case 'e':
      return jjMoveStringLiteralDfa1_1(281474980904960L);
    case 'f':
      return jjMoveStringLiteralDfa1_1(8192L);
    case 'g':
      return jjMoveStringLiteralDfa1_1(67371008L);
    case 'l':
      return jjMoveStringLiteralDfa1_1(17825792L);
    case 'm':
      return jjMoveStringLiteralDfa1_1(2199023255552L);
    case 'n':
      return jjMoveStringLiteralDfa1_1(4398314962944L);
    case 'o':
      return jjMoveStringLiteralDfa1_1(70368744177664L);
    case 't':
      return jjMoveStringLiteralDfa1_1(4096L);
    case '|':
      return jjMoveStringLiteralDfa1_1(140737488355328L);
    case '}':
      return jjStopAtPos(0, 15);
    }
    return jjMoveNfa_1(0, 0);
  }

  private int jjMoveStringLiteralDfa1_1(long active0) {
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException e) {
      jjStopStringLiteralDfa_1(0, active0);
      return 1;
    }
    switch (this.curChar)
    {
    case '&':
      if ((active0 & 0x0) != 0L)
        return jjStopAtPos(1, 45);
      break;
    case '=':
      if ((active0 & 0x200000) != 0L)
        return jjStopAtPos(1, 21);
      if ((active0 & 0x800000) != 0L)
        return jjStopAtPos(1, 23);
      if ((active0 & 0x2000000) != 0L)
        return jjStopAtPos(1, 25);
      if ((active0 & 0x8000000) != 0L)
        return jjStopAtPos(1, 27);
      break;
    case 'a':
      return jjMoveStringLiteralDfa2_1(active0, 8192L);
    case 'e':
      if ((active0 & 0x1000000) != 0L)
        return jjStartNfaWithStates_1(1, 24, 6);
      if ((active0 & 0x4000000) != 0L)
        return jjStartNfaWithStates_1(1, 26, 6);
      if ((active0 & 0x10000000) != 0L)
        return jjStartNfaWithStates_1(1, 28, 6);
      break;
    case 'i':
      return jjMoveStringLiteralDfa2_1(active0, 549755813888L);
    case 'm':
      return jjMoveStringLiteralDfa2_1(active0, 281474976710656L);
    case 'n':
      return jjMoveStringLiteralDfa2_1(active0, 17592186044416L);
    case 'o':
      return jjMoveStringLiteralDfa2_1(active0, 6597069766656L);
    case 'q':
      if ((active0 & 0x400000) != 0L)
        return jjStartNfaWithStates_1(1, 22, 6);
      break;
    case 'r':
      if ((active0 & 0x0) != 0L)
        return jjStartNfaWithStates_1(1, 46, 6);
      return jjMoveStringLiteralDfa2_1(active0, 4096L);
    case 't':
      if ((active0 & 0x40000) != 0L)
        return jjStartNfaWithStates_1(1, 18, 6);
      if ((active0 & 0x100000) != 0L)
        return jjStartNfaWithStates_1(1, 20, 6);
      break;
    case 'u':
      return jjMoveStringLiteralDfa2_1(active0, 16384L);
    case '|':
      if ((active0 & 0x0) != 0L) {
        return jjStopAtPos(1, 47);
      }
      break;
    }

    return jjStartNfa_1(0, active0);
  }

  private int jjMoveStringLiteralDfa2_1(long old0, long active0) {
    if ((active0 &= old0) == 0L)
      return jjStartNfa_1(0, old0); try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException e) {
      jjStopStringLiteralDfa_1(1, active0);
      return 2;
    }
    switch (this.curChar)
    {
    case 'd':
      if ((active0 & 0x0) != 0L)
        return jjStartNfaWithStates_1(2, 41, 6);
      if ((active0 & 0x0) != 0L)
        return jjStartNfaWithStates_1(2, 44, 6);
      break;
    case 'l':
      return jjMoveStringLiteralDfa3_1(active0, 24576L);
    case 'p':
      return jjMoveStringLiteralDfa3_1(active0, 281474976710656L);
    case 't':
      if ((active0 & 0x0) != 0L)
        return jjStartNfaWithStates_1(2, 42, 6);
      break;
    case 'u':
      return jjMoveStringLiteralDfa3_1(active0, 4096L);
    case 'v':
      if ((active0 & 0x0) != 0L) {
        return jjStartNfaWithStates_1(2, 39, 6);
      }
      break;
    }

    return jjStartNfa_1(1, active0);
  }

  private int jjMoveStringLiteralDfa3_1(long old0, long active0) {
    if ((active0 &= old0) == 0L)
      return jjStartNfa_1(1, old0); try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException e) {
      jjStopStringLiteralDfa_1(2, active0);
      return 3;
    }
    switch (this.curChar)
    {
    case 'e':
      if ((active0 & 0x1000) != 0L)
        return jjStartNfaWithStates_1(3, 12, 6);
      break;
    case 'l':
      if ((active0 & 0x4000) != 0L)
        return jjStartNfaWithStates_1(3, 14, 6);
      break;
    case 's':
      return jjMoveStringLiteralDfa4_1(active0, 8192L);
    case 't':
      return jjMoveStringLiteralDfa4_1(active0, 281474976710656L);
    }

    return jjStartNfa_1(2, active0);
  }

  private int jjMoveStringLiteralDfa4_1(long old0, long active0) {
    if ((active0 &= old0) == 0L)
      return jjStartNfa_1(2, old0); try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException e) {
      jjStopStringLiteralDfa_1(3, active0);
      return 4;
    }
    switch (this.curChar)
    {
    case 'e':
      if ((active0 & 0x2000) != 0L)
        return jjStartNfaWithStates_1(4, 13, 6);
      break;
    case 'y':
      if ((active0 & 0x0) != 0L) {
        return jjStartNfaWithStates_1(4, 48, 6);
      }
      break;
    }

    return jjStartNfa_1(3, active0);
  }

  private int jjStartNfaWithStates_1(int pos, int kind, int state) {
    this.jjmatchedKind = kind;
    this.jjmatchedPos = pos;
    try { this.curChar = this.input_stream.readChar(); } catch (IOException e) {
      return pos + 1;
    }return jjMoveNfa_1(state, pos + 1);
  }

  private int jjMoveNfa_1(int startState, int curPos)
  {
    int startsAt = 0;
    this.jjnewStateCnt = 35;
    int i = 1;
    this.jjstateSet[0] = startState;
    int kind = 2147483647;
    while (true)
    {
      if (++this.jjround == 2147483647)
        ReInitRounds();
      if (this.curChar < '@')
      {
        long l = 1L << this.curChar;
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0:
            if ((0x0 & l) != 0L)
            {
              if (kind > 7)
                kind = 7;
              jjCheckNAddStates(0, 4);
            }
            else if ((0x0 & l) != 0L)
            {
              if (kind > 50)
                kind = 50;
              jjCheckNAdd(6);
            }
            else if (this.curChar == '\'') {
              jjCheckNAddStates(5, 9);
            } else if (this.curChar == '"') {
              jjCheckNAddStates(10, 14);
            } else if (this.curChar == '.') {
              jjCheckNAdd(1);
            }break;
          case 1:
            if ((0x0 & l) != 0L)
            {
              if (kind > 8)
                kind = 8;
              jjCheckNAddTwoStates(1, 2);
            }break;
          case 3:
            if ((0x0 & l) != 0L)
              jjCheckNAdd(4);
            break;
          case 4:
            if ((0x0 & l) != 0L)
            {
              if (kind > 8)
                kind = 8;
              jjCheckNAdd(4);
            }break;
          case 5:
            if ((0x0 & l) != 0L)
            {
              if (kind > 50)
                kind = 50;
              jjCheckNAdd(6);
            }break;
          case 6:
            if ((0x0 & l) != 0L)
            {
              if (kind > 50)
                kind = 50;
              jjCheckNAdd(6);
            }break;
          case 7:
            if ((0x0 & l) != 0L)
            {
              if (kind > 7)
                kind = 7;
              jjCheckNAddStates(0, 4);
            }break;
          case 8:
            if ((0x0 & l) != 0L)
            {
              if (kind > 7)
                kind = 7;
              jjCheckNAdd(8);
            }break;
          case 9:
            if ((0x0 & l) != 0L)
              jjCheckNAddTwoStates(9, 10);
            break;
          case 10:
            if (this.curChar == '.')
            {
              if (kind > 8)
                kind = 8;
              jjCheckNAddTwoStates(11, 12);
            }break;
          case 11:
            if ((0x0 & l) != 0L)
            {
              if (kind > 8)
                kind = 8;
              jjCheckNAddTwoStates(11, 12);
            }break;
          case 13:
            if ((0x0 & l) != 0L)
              jjCheckNAdd(14);
            break;
          case 14:
            if ((0x0 & l) != 0L)
            {
              if (kind > 8)
                kind = 8;
              jjCheckNAdd(14);
            }break;
          case 15:
            if ((0x0 & l) != 0L)
              jjCheckNAddTwoStates(15, 16);
            break;
          case 17:
            if ((0x0 & l) != 0L)
              jjCheckNAdd(18);
            break;
          case 18:
            if ((0x0 & l) != 0L)
            {
              if (kind > 8)
                kind = 8;
              jjCheckNAdd(18);
            }break;
          case 19:
            if (this.curChar == '"')
              jjCheckNAddStates(10, 14);
            break;
          case 20:
            if ((0xFFFFFFFF & l) != 0L)
              jjCheckNAddStates(15, 17);
            break;
          case 22:
            if (this.curChar == '"')
              jjCheckNAddStates(15, 17);
            break;
          case 23:
            if ((this.curChar == '"') && (kind > 10))
              kind = 10;
            break;
          case 24:
            if ((0xFFFFFFFF & l) != 0L)
              jjCheckNAddTwoStates(24, 25);
            break;
          case 26:
            if (((0xFFFFFFFF & l) != 0L) && (kind > 11))
              kind = 11;
            break;
          case 27:
            if (this.curChar == '\'')
              jjCheckNAddStates(5, 9);
            break;
          case 28:
            if ((0xFFFFFFFF & l) != 0L)
              jjCheckNAddStates(18, 20);
            break;
          case 30:
            if (this.curChar == '\'')
              jjCheckNAddStates(18, 20);
            break;
          case 31:
            if ((this.curChar == '\'') && (kind > 10))
              kind = 10;
            break;
          case 32:
            if ((0xFFFFFFFF & l) != 0L)
              jjCheckNAddTwoStates(32, 33);
            break;
          case 34:
            if (((0xFFFFFFFF & l) != 0L) && (kind > 11))
              kind = 11; break;
          case 2:
          case 12:
          case 16:
          case 21:
          case 25:
          case 29:
          case 33: }  } while (i != startsAt);
      }
      else if (this.curChar < '')
      {
        long l = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0:
          case 6:
            if ((0x87FFFFFE & l) != 0L)
            {
              if (kind > 50)
                kind = 50;
              jjCheckNAdd(6);
            }break;
          case 2:
            if ((0x20 & l) != 0L)
              jjAddStates(21, 22);
            break;
          case 12:
            if ((0x20 & l) != 0L)
              jjAddStates(23, 24);
            break;
          case 16:
            if ((0x20 & l) != 0L)
              jjAddStates(25, 26);
            break;
          case 20:
            if ((0xEFFFFFFF & l) != 0L)
              jjCheckNAddStates(15, 17);
            break;
          case 21:
            if (this.curChar == '\\')
              this.jjstateSet[(this.jjnewStateCnt++)] = 22;
            break;
          case 22:
            if (this.curChar == '\\')
              jjCheckNAddStates(15, 17);
            break;
          case 24:
            if ((0xEFFFFFFF & l) != 0L)
              jjAddStates(27, 28);
            break;
          case 25:
            if (this.curChar == '\\')
              this.jjstateSet[(this.jjnewStateCnt++)] = 26;
            break;
          case 26:
          case 34:
            if (((0xEFFFFFFF & l) != 0L) && (kind > 11))
              kind = 11;
            break;
          case 28:
            if ((0xEFFFFFFF & l) != 0L)
              jjCheckNAddStates(18, 20);
            break;
          case 29:
            if (this.curChar == '\\')
              this.jjstateSet[(this.jjnewStateCnt++)] = 30;
            break;
          case 30:
            if (this.curChar == '\\')
              jjCheckNAddStates(18, 20);
            break;
          case 32:
            if ((0xEFFFFFFF & l) != 0L)
              jjAddStates(29, 30);
            break;
          case 33:
            if (this.curChar == '\\')
              this.jjstateSet[(this.jjnewStateCnt++)] = 34; break;
          case 1:
          case 3:
          case 4:
          case 5:
          case 7:
          case 8:
          case 9:
          case 10:
          case 11:
          case 13:
          case 14:
          case 15:
          case 17:
          case 18:
          case 19:
          case 23:
          case 27:
          case 31: }  } while (i != startsAt);
      }
      else
      {
        int hiByte = this.curChar >> '\b';
        int i1 = hiByte >> 6;
        long l1 = 1L << (hiByte & 0x3F);
        int i2 = (this.curChar & 0xFF) >> '\006';
        long l2 = 1L << (this.curChar & 0x3F);
        do
        {
          switch (this.jjstateSet[(--i)])
          {
          case 0:
          case 6:
            if (jjCanMove_1(hiByte, i1, i2, l1, l2))
            {
              if (kind > 50)
                kind = 50;
              jjCheckNAdd(6);
            }break;
          case 20:
            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
              jjAddStates(15, 17);
            break;
          case 24:
            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
              jjAddStates(27, 28);
            break;
          case 26:
          case 34:
            if ((jjCanMove_0(hiByte, i1, i2, l1, l2)) && (kind > 11))
              kind = 11;
            break;
          case 28:
            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
              jjAddStates(18, 20);
            break;
          case 32:
            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
              jjAddStates(29, 30);
            break;
          }
        }
        while (i != startsAt);
      }
      if (kind != 2147483647)
      {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = curPos;
        kind = 2147483647;
      }
      curPos++;
      if ((i = this.jjnewStateCnt) == (startsAt = 35 - (this.jjnewStateCnt = startsAt)))
        return curPos; try {
        this.curChar = this.input_stream.readChar(); } catch (IOException e) {  }
    }return curPos;
  }

  private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
  {
    switch (hiByte)
    {
    case 0:
      return (jjbitVec2[i2] & l2) != 0L;
    }
    if ((jjbitVec0[i1] & l1) != 0L)
      return true;
    return false;
  }

  private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
  {
    switch (hiByte)
    {
    case 0:
      return (jjbitVec4[i2] & l2) != 0L;
    case 48:
      return (jjbitVec5[i2] & l2) != 0L;
    case 49:
      return (jjbitVec6[i2] & l2) != 0L;
    case 51:
      return (jjbitVec7[i2] & l2) != 0L;
    case 61:
      return (jjbitVec8[i2] & l2) != 0L;
    }
    if ((jjbitVec3[i1] & l1) != 0L)
      return true;
    return false;
  }

  public ELParserTokenManager(SimpleCharStream stream)
  {
    this.input_stream = stream;
  }

  public ELParserTokenManager(SimpleCharStream stream, int lexState)
  {
    this(stream);
    SwitchTo(lexState);
  }

  public void ReInit(SimpleCharStream stream)
  {
    this.jjmatchedPos = (this.jjnewStateCnt = 0);
    this.curLexState = this.defaultLexState;
    this.input_stream = stream;
    ReInitRounds();
  }

  private void ReInitRounds()
  {
    this.jjround = -2147483647;
    for (int i = 35; i-- > 0; )
      this.jjrounds[i] = -2147483648;
  }

  public void ReInit(SimpleCharStream stream, int lexState)
  {
    ReInit(stream);
    SwitchTo(lexState);
  }

  public void SwitchTo(int lexState)
  {
    if ((lexState >= 2) || (lexState < 0)) {
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
    }
    this.curLexState = lexState;
  }

  protected Token jjFillToken()
  {
    String im = jjstrLiteralImages[this.jjmatchedKind];
    String curTokenImage = im == null ? this.input_stream.GetImage() : im;
    int beginLine = this.input_stream.getBeginLine();
    int beginColumn = this.input_stream.getBeginColumn();
    int endLine = this.input_stream.getEndLine();
    int endColumn = this.input_stream.getEndColumn();
    Token t = Token.newToken(this.jjmatchedKind, curTokenImage);

    t.beginLine = beginLine;
    t.endLine = endLine;
    t.beginColumn = beginColumn;
    t.endColumn = endColumn;

    return t;
  }

  public Token getNextToken()
  {
    int curPos = 0;
    while (true)
    {
      try
      {
        this.curChar = this.input_stream.BeginToken();
      }
      catch (IOException e)
      {
        this.jjmatchedKind = 0;
        return jjFillToken();
      }

      switch (this.curLexState)
      {
      case 0:
        this.jjmatchedKind = 2147483647;
        this.jjmatchedPos = 0;
        curPos = jjMoveStringLiteralDfa0_0();
        break;
      case 1:
        try { this.input_stream.backup(0);
          do {
            this.curChar = this.input_stream.BeginToken();

            if (this.curChar > ' ') break;  } while ((0x2600 & 1L << this.curChar) != 0L);
        } catch (IOException e1) {
        }
        continue;
        this.jjmatchedKind = 2147483647;
        this.jjmatchedPos = 0;
        curPos = jjMoveStringLiteralDfa0_1();
        if ((this.jjmatchedPos == 0) && (this.jjmatchedKind > 54))
        {
          this.jjmatchedKind = 54;
        }

      default:
        if (this.jjmatchedKind == 2147483647)
          break label284;
        if (this.jjmatchedPos + 1 < curPos)
          this.input_stream.backup(curPos - this.jjmatchedPos - 1);
        if ((jjtoToken[(this.jjmatchedKind >> 6)] & 1L << (this.jjmatchedKind & 0x3F)) != 0L)
        {
          Token matchedToken = jjFillToken();
          if (jjnewLexState[this.jjmatchedKind] != -1)
            this.curLexState = jjnewLexState[this.jjmatchedKind];
          return matchedToken;
        }

        if (jjnewLexState[this.jjmatchedKind] != -1)
          this.curLexState = jjnewLexState[this.jjmatchedKind];
        break;
      }
    }
    label284: int error_line = this.input_stream.getEndLine();
    int error_column = this.input_stream.getEndColumn();
    String error_after = null;
    boolean EOFSeen = false;
    try { this.input_stream.readChar(); this.input_stream.backup(1);
    } catch (IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
      if ((this.curChar == '\n') || (this.curChar == '\r')) {
        error_line++;
        error_column = 0;
      }
      else {
        error_column++;
      }
    }
    if (!EOFSeen) {
      this.input_stream.backup(1);
      error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
    }
    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
  }

  private void jjCheckNAdd(int state)
  {
    if (this.jjrounds[state] != this.jjround)
    {
      this.jjstateSet[(this.jjnewStateCnt++)] = state;
      this.jjrounds[state] = this.jjround;
    }
  }

  private void jjAddStates(int start, int end) {
    do
      this.jjstateSet[(this.jjnewStateCnt++)] = jjnextStates[start];
    while (start++ != end);
  }

  private void jjCheckNAddTwoStates(int state1, int state2) {
    jjCheckNAdd(state1);
    jjCheckNAdd(state2);
  }

  private void jjCheckNAddStates(int start, int end)
  {
    do
      jjCheckNAdd(jjnextStates[start]);
    while (start++ != end);
  }
}