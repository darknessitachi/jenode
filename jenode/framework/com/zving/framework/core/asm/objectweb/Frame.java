package com.zving.framework.core.asm.objectweb;

final class Frame
{
  static final int DIM = -268435456;
  static final int ARRAY_OF = 268435456;
  static final int ELEMENT_OF = -268435456;
  static final int KIND = 251658240;
  static final int TOP_IF_LONG_OR_DOUBLE = 8388608;
  static final int VALUE = 8388607;
  static final int BASE_KIND = 267386880;
  static final int BASE_VALUE = 1048575;
  static final int BASE = 16777216;
  static final int OBJECT = 24117248;
  static final int UNINITIALIZED = 25165824;
  private static final int LOCAL = 33554432;
  private static final int STACK = 50331648;
  static final int TOP = 16777216;
  static final int BOOLEAN = 16777225;
  static final int BYTE = 16777226;
  static final int CHAR = 16777227;
  static final int SHORT = 16777228;
  static final int INTEGER = 16777217;
  static final int FLOAT = 16777218;
  static final int DOUBLE = 16777219;
  static final int LONG = 16777220;
  static final int NULL = 16777221;
  static final int UNINITIALIZED_THIS = 16777222;
  static final int[] SIZE = null;//b;
  Label owner;
  int[] inputLocals;
  int[] inputStack;
  private int[] outputLocals;
  private int[] outputStack;
  private int outputStackTop;
  private int initializationCount;
  private int[] initializations;

  static
  {
    int[] b = new int['Ê'];
    String s = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";

    for (int i = 0; i < b.length; i++)
      b[i] = (s.charAt(i) - 'E');
  }

  private int get(int local)
  {
    if ((this.outputLocals == null) || (local >= this.outputLocals.length))
    {
      return 0x2000000 | local;
    }
    int type = this.outputLocals[local];
    if (type == 0)
    {
      type = this.outputLocals[local] = 0x2000000 | local;
    }
    return type;
  }

  private void set(int local, int type)
  {
    if (this.outputLocals == null) {
      this.outputLocals = new int[10];
    }
    int n = this.outputLocals.length;
    if (local >= n) {
      int[] t = new int[Math.max(local + 1, 2 * n)];
      System.arraycopy(this.outputLocals, 0, t, 0, n);
      this.outputLocals = t;
    }

    this.outputLocals[local] = type;
  }

  private void push(int type)
  {
    if (this.outputStack == null) {
      this.outputStack = new int[10];
    }
    int n = this.outputStack.length;
    if (this.outputStackTop >= n) {
      int[] t = new int[Math.max(this.outputStackTop + 1, 2 * n)];
      System.arraycopy(this.outputStack, 0, t, 0, n);
      this.outputStack = t;
    }

    this.outputStack[(this.outputStackTop++)] = type;

    int top = this.owner.inputStackTop + this.outputStackTop;
    if (top > this.owner.outputStackMax)
      this.owner.outputStackMax = top;
  }

  private void push(ClassWriter cw, String desc)
  {
    int type = type(cw, desc);
    if (type != 0) {
      push(type);
      if ((type == 16777220) || (type == 16777219))
        push(16777216);
    }
  }

  private static int type(ClassWriter cw, String desc)
  {
    int index = desc.charAt(0) == '(' ? desc.indexOf(')') + 1 : 0;
    switch (desc.charAt(index)) {
    case 'V':
      return 0;
    case 'B':
    case 'C':
    case 'I':
    case 'S':
    case 'Z':
      return 16777217;
    case 'F':
      return 16777218;
    case 'J':
      return 16777220;
    case 'D':
      return 16777219;
    case 'L':
      String t = desc.substring(index + 1, desc.length() - 1);
      return 0x1700000 | cw.addType(t);
    case 'E':
    case 'G':
    case 'H':
    case 'K':
    case 'M':
    case 'N':
    case 'O':
    case 'P':
    case 'Q':
    case 'R':
    case 'T':
    case 'U':
    case 'W':
    case 'X':
    case 'Y': } int dims = index + 1;
    while (desc.charAt(dims) == '[')
      dims++;
    int data;
    switch (desc.charAt(dims)) {
    case 'Z':
      data = 16777225;
      break;
    case 'C':
      data = 16777227;
      break;
    case 'B':
      data = 16777226;
      break;
    case 'S':
      data = 16777228;
      break;
    case 'I':
      data = 16777217;
      break;
    case 'F':
      data = 16777218;
      break;
    case 'J':
      data = 16777220;
      break;
    case 'D':
      data = 16777219;
      break;
    default:
      String t = desc.substring(dims + 1, desc.length() - 1);
      data = 0x1700000 | cw.addType(t);
    }
    return dims - index << 28 | data;
  }

  private int pop()
  {
    if (this.outputStackTop > 0) {
      return this.outputStack[(--this.outputStackTop)];
    }

    return 0;
//    return 0x3000000 | ---this.owner.inputStackTop;
  }

  private void pop(int elements)
  {
    if (this.outputStackTop >= elements) {
      this.outputStackTop -= elements;
    }
    else
    {
      this.owner.inputStackTop -= elements - this.outputStackTop;
      this.outputStackTop = 0;
    }
  }

  private void pop(String desc)
  {
    char c = desc.charAt(0);
    if (c == '(')
      pop((Type.getArgumentsAndReturnSizes(desc) >> 2) - 1);
    else if ((c == 'J') || (c == 'D'))
      pop(2);
    else
      pop(1);
  }

  private void init(int var)
  {
    if (this.initializations == null) {
      this.initializations = new int[2];
    }
    int n = this.initializations.length;
    if (this.initializationCount >= n) {
      int[] t = new int[Math.max(this.initializationCount + 1, 2 * n)];
      System.arraycopy(this.initializations, 0, t, 0, n);
      this.initializations = t;
    }

    this.initializations[(this.initializationCount++)] = var;
  }

  private int init(ClassWriter cw, int t)
  {
    int s;
    if (t == 16777222) {
      s = 0x1700000 | cw.addType(cw.thisName);
    }
    else
    {
      if ((t & 0xFFF00000) == 25165824) {
        String type = cw.typeTable[(t & 0xFFFFF)].strVal1;
        s = 0x1700000 | cw.addType(type);
      } else {
        return t;
      }
    }
    for (int j = 0; j < this.initializationCount; j++) {
      int u = this.initializations[j];
      int dim = u & 0xF0000000;
      int kind = u & 0xF000000;
      if (kind == 33554432)
        u = dim + this.inputLocals[(u & 0x7FFFFF)];
      else if (kind == 50331648) {
        u = dim + this.inputStack[(this.inputStack.length - (u & 0x7FFFFF))];
      }
      if (t == u) {
        return s;
      }
    }
    return t;
  }

  void initInputFrame(ClassWriter cw, int access, Type[] args, int maxLocals)
  {
    this.inputLocals = new int[maxLocals];
    this.inputStack = new int[0];
    int i = 0;
    if ((access & 0x8) == 0) {
      if ((access & 0x80000) == 0)
        this.inputLocals[(i++)] = (0x1700000 | cw.addType(cw.thisName));
      else {
        this.inputLocals[(i++)] = 16777222;
      }
    }
    for (int j = 0; j < args.length; j++) {
      int t = type(cw, args[j].getDescriptor());
      this.inputLocals[(i++)] = t;
      if ((t == 16777220) || (t == 16777219)) {
        this.inputLocals[(i++)] = 16777216;
      }
    }
    while (i < maxLocals)
      this.inputLocals[(i++)] = 16777216;
  }

  void execute(int opcode, int arg, ClassWriter cw, Item item)
  {
    switch (opcode) {
    case 0:
    case 116:
    case 117:
    case 118:
    case 119:
    case 145:
    case 146:
    case 147:
    case 167:
    case 177:
      break;
    case 1:
      push(16777221);
      break;
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    case 16:
    case 17:
    case 21:
      push(16777217);
      break;
    case 9:
    case 10:
    case 22:
      push(16777220);
      push(16777216);
      break;
    case 11:
    case 12:
    case 13:
    case 23:
      push(16777218);
      break;
    case 14:
    case 15:
    case 24:
      push(16777219);
      push(16777216);
      break;
    case 18:
      switch (item.type) {
      case 3:
        push(16777217);
        break;
      case 5:
        push(16777220);
        push(16777216);
        break;
      case 4:
        push(16777218);
        break;
      case 6:
        push(16777219);
        push(16777216);
        break;
      case 7:
        push(0x1700000 | cw.addType("java/lang/Class"));
        break;
      case 8:
        push(0x1700000 | cw.addType("java/lang/String"));
        break;
      case 16:
        push(0x1700000 | cw.addType("java/lang/invoke/MethodType"));
        break;
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      default:
        push(0x1700000 | cw.addType("java/lang/invoke/MethodHandle"));
      }
      break;
    case 25:
      push(get(arg));
      break;
    case 46:
    case 51:
    case 52:
    case 53:
      pop(2);
      push(16777217);
      break;
    case 47:
    case 143:
      pop(2);
      push(16777220);
      push(16777216);
      break;
    case 48:
      pop(2);
      push(16777218);
      break;
    case 49:
    case 138:
      pop(2);
      push(16777219);
      push(16777216);
      break;
    case 50:
      pop(1);
      int t1 = pop();
      push(-268435456 + t1);
      break;
    case 54:
    case 56:
    case 58:
//      int t1 = pop();
//      set(arg, t1);
      if (arg > 0) {
        int t2 = get(arg - 1);

        if ((t2 == 16777220) || (t2 == 16777219))
          set(arg - 1, 16777216);
        else if ((t2 & 0xF000000) != 16777216) {
          set(arg - 1, t2 | 0x800000);
        }
      }
      break;
    case 55:
    case 57:
      pop(1);
//      int t1 = pop();
//      set(arg, t1);
      set(arg + 1, 16777216);
      if (arg > 0) {
        int t2 = get(arg - 1);

        if ((t2 == 16777220) || (t2 == 16777219))
          set(arg - 1, 16777216);
        else if ((t2 & 0xF000000) != 16777216) {
          set(arg - 1, t2 | 0x800000);
        }
      }
      break;
    case 79:
    case 81:
    case 83:
    case 84:
    case 85:
    case 86:
      pop(3);
      break;
    case 80:
    case 82:
      pop(4);
      break;
    case 87:
    case 153:
    case 154:
    case 155:
    case 156:
    case 157:
    case 158:
    case 170:
    case 171:
    case 172:
    case 174:
    case 176:
    case 191:
    case 194:
    case 195:
    case 198:
    case 199:
      pop(1);
      break;
    case 88:
    case 159:
    case 160:
    case 161:
    case 162:
    case 163:
    case 164:
    case 165:
    case 166:
    case 173:
    case 175:
      pop(2);
      break;
    case 89:
//      int t1 = pop();
//      push(t1);
//      push(t1);
      break;
    case 90:
//      int t1 = pop();
      int t2 = pop();
//      push(t1);
//      push(t2);
//      push(t1);
      break;
    case 91:
//      int t1 = pop();
//      int t2 = pop();
      int t3 = pop();
//      push(t1);
//      push(t3);
//      push(t2);
//      push(t1);
      break;
    case 92:
//      int t1 = pop();
//      int t2 = pop();
//      push(t2);
//      push(t1);
//      push(t2);
//      push(t1);
      break;
    case 93:
//      int t1 = pop();
//      int t2 = pop();
//      int t3 = pop();
//      push(t2);
//      push(t1);
//      push(t3);
//      push(t2);
//      push(t1);
      break;
    case 94:
//      int t1 = pop();
//      int t2 = pop();
//      int t3 = pop();
      int t4 = pop();
//      push(t2);
//      push(t1);
//      push(t4);
//      push(t3);
//      push(t2);
//      push(t1);
      break;
    case 95:
//      int t1 = pop();
//      int t2 = pop();
//      push(t1);
//      push(t2);
      break;
    case 96:
    case 100:
    case 104:
    case 108:
    case 112:
    case 120:
    case 122:
    case 124:
    case 126:
    case 128:
    case 130:
    case 136:
    case 142:
    case 149:
    case 150:
      pop(2);
      push(16777217);
      break;
    case 97:
    case 101:
    case 105:
    case 109:
    case 113:
    case 127:
    case 129:
    case 131:
      pop(4);
      push(16777220);
      push(16777216);
      break;
    case 98:
    case 102:
    case 106:
    case 110:
    case 114:
    case 137:
    case 144:
      pop(2);
      push(16777218);
      break;
    case 99:
    case 103:
    case 107:
    case 111:
    case 115:
      pop(4);
      push(16777219);
      push(16777216);
      break;
    case 121:
    case 123:
    case 125:
      pop(3);
      push(16777220);
      push(16777216);
      break;
    case 132:
      set(arg, 16777217);
      break;
    case 133:
    case 140:
      pop(1);
      push(16777220);
      push(16777216);
      break;
    case 134:
      pop(1);
      push(16777218);
      break;
    case 135:
    case 141:
      pop(1);
      push(16777219);
      push(16777216);
      break;
    case 139:
    case 190:
    case 193:
      pop(1);
      push(16777217);
      break;
    case 148:
    case 151:
    case 152:
      pop(4);
      push(16777217);
      break;
    case 168:
    case 169:
      throw new RuntimeException(
        "JSR/RET are not supported with computeFrames option");
    case 178:
      push(cw, item.strVal3);
      break;
    case 179:
      pop(item.strVal3);
      break;
    case 180:
      pop(1);
      push(cw, item.strVal3);
      break;
    case 181:
      pop(item.strVal3);
      pop();
      break;
    case 182:
    case 183:
    case 184:
    case 185:
      pop(item.strVal3);
      if (opcode != 184) {
//        int t1 = pop();
        if ((opcode == 183) && 
          (item.strVal2.charAt(0) == '<')) {
//          init(t1);
        }
      }
      push(cw, item.strVal3);
      break;
    case 186:
      pop(item.strVal2);
      push(cw, item.strVal2);
      break;
    case 187:
      push(0x1800000 | cw.addUninitializedType(item.strVal1, arg));
      break;
    case 188:
      pop();
      switch (arg) {
      case 4:
        push(285212681);
        break;
      case 5:
        push(285212683);
        break;
      case 8:
        push(285212682);
        break;
      case 9:
        push(285212684);
        break;
      case 10:
        push(285212673);
        break;
      case 6:
        push(285212674);
        break;
      case 7:
        push(285212675);
        break;
      default:
        push(285212676);
      }

      break;
    case 189:
      String s = item.strVal1;
      pop();
      if (s.charAt(0) == '[')
        push(cw, '[' + s);
      else {
        push(0x11700000 | cw.addType(s));
      }
      break;
    case 192:
       s = item.strVal1;
      pop();
      if (s.charAt(0) == '[')
        push(cw, s);
      else {
        push(0x1700000 | cw.addType(s));
      }
      break;
    case 19:
    case 20:
    case 26:
    case 27:
    case 28:
    case 29:
    case 30:
    case 31:
    case 32:
    case 33:
    case 34:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 44:
    case 45:
    case 59:
    case 60:
    case 61:
    case 62:
    case 63:
    case 64:
    case 65:
    case 66:
    case 67:
    case 68:
    case 69:
    case 70:
    case 71:
    case 72:
    case 73:
    case 74:
    case 75:
    case 76:
    case 77:
    case 78:
    case 196:
    case 197:
    default:
      pop(arg);
      push(cw, item.strVal1);
    }
  }

  boolean merge(ClassWriter cw, Frame frame, int edge)
  {
    boolean changed = false;

    int nLocal = this.inputLocals.length;
    int nStack = this.inputStack.length;
    if (frame.inputLocals == null) {
      frame.inputLocals = new int[nLocal];
      changed = true;
    }

    for (int i = 0; i < nLocal; i++)
    {
      int t;
      if ((this.outputLocals != null) && (i < this.outputLocals.length)) {
        int s = this.outputLocals[i];
        if (s == 0) {
          t = this.inputLocals[i];
        } else {
          int dim = s & 0xF0000000;
          int kind = s & 0xF000000;
          if (kind == 16777216) {
            t = s;
          }
          else
          {
            if (kind == 33554432)
              t = dim + this.inputLocals[(s & 0x7FFFFF)];
            else {
              t = dim + this.inputStack[(nStack - (s & 0x7FFFFF))];
            }
            if (((s & 0x800000) != 0) && (
              (t == 16777220) || (t == 16777219)))
              t = 16777216;
          }
        }
      }
      else {
        t = this.inputLocals[i];
      }
      if (this.initializations != null) {
        t = init(cw, t);
      }
      changed |= merge(cw, t, frame.inputLocals, i);
    }

    if (edge > 0) {
      for (int i = 0; i < nLocal; i++) {
        int t = this.inputLocals[i];
        changed |= merge(cw, t, frame.inputLocals, i);
      }
      if (frame.inputStack == null) {
        frame.inputStack = new int[1];
        changed = true;
      }
      changed |= merge(cw, edge, frame.inputStack, 0);
      return changed;
    }

    int nInputStack = this.inputStack.length + this.owner.inputStackTop;
    if (frame.inputStack == null) {
      frame.inputStack = new int[nInputStack + this.outputStackTop];
      changed = true;
    }

    for (int i = 0; i < nInputStack; i++) {
      int t = this.inputStack[i];
      if (this.initializations != null) {
        t = init(cw, t);
      }
      changed |= merge(cw, t, frame.inputStack, i);
    }
    for (int i = 0; i < this.outputStackTop; i++) {
      int s = this.outputStack[i];
      int dim = s & 0xF0000000;
      int kind = s & 0xF000000;
      int t;
      if (kind == 16777216) {
        t = s;
      }
      else
      {
        if (kind == 33554432)
          t = dim + this.inputLocals[(s & 0x7FFFFF)];
        else {
          t = dim + this.inputStack[(nStack - (s & 0x7FFFFF))];
        }
        if (((s & 0x800000) != 0) && (
          (t == 16777220) || (t == 16777219))) {
          t = 16777216;
        }
      }
      if (this.initializations != null) {
        t = init(cw, t);
      }
      changed |= merge(cw, t, frame.inputStack, nInputStack + i);
    }
    return changed;
  }

  private static boolean merge(ClassWriter cw, int t, int[] types, int index)
  {
    int u = types[index];
    if (u == t)
    {
      return false;
    }
    if ((t & 0xFFFFFFF) == 16777221) {
      if (u == 16777221) {
        return false;
      }
      t = 16777221;
    }
    if (u == 0)
    {
      types[index] = t;
      return true;
    }
    int v;
    if (((u & 0xFF00000) == 24117248) || ((u & 0xF0000000) != 0))
    {
      if (t == 16777221)
      {
        return false;
      }
      if ((t & 0xFFF00000) == (u & 0xFFF00000))
      {
        if ((u & 0xFF00000) == 24117248)
        {
          v = t & 0xF0000000 | 0x1700000 | 
            cw.getMergedType(t & 0xFFFFF, u & 0xFFFFF);
        }
        else
        {
          v = 0x1700000 | cw.addType("java/lang/Object");
        }
      }
      else
      {
        if (((t & 0xFF00000) == 24117248) || ((t & 0xF0000000) != 0))
        {
          v = 0x1700000 | cw.addType("java/lang/Object");
        }
        else
          v = 16777216;
      }
    }
    else
    {
      if (u == 16777221)
      {
        v = ((t & 0xFF00000) == 24117248) || ((t & 0xF0000000) != 0) ? t : 16777216;
      }
      else
        v = 16777216;
    }
    if (u != v) {
      types[index] = v;
      return true;
    }
    return false;
  }
}