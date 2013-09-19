package com.zving.framework.core.asm.objectweb;

class MethodWriter extends MethodVisitor
{
  static final int ACC_CONSTRUCTOR = 524288;
  static final int SAME_FRAME = 0;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  static final int RESERVED = 128;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  static final int CHOP_FRAME = 248;
  static final int SAME_FRAME_EXTENDED = 251;
  static final int APPEND_FRAME = 252;
  static final int FULL_FRAME = 255;
  private static final int FRAMES = 0;
  private static final int MAXS = 1;
  private static final int NOTHING = 2;
  final ClassWriter cw;
  private int access;
  private final int name;
  private final int desc;
  private final String descriptor;
  String signature;
  int classReaderOffset;
  int classReaderLength;
  int exceptionCount;
  int[] exceptions;
  private ByteVector annd;
  private AnnotationWriter anns;
  private AnnotationWriter ianns;
  private AnnotationWriter[] panns;
  private AnnotationWriter[] ipanns;
  private int synthetics;
  private Attribute attrs;
  private ByteVector code = new ByteVector();
  private int maxStack;
  private int maxLocals;
  private int currentLocals;
  private int frameCount;
  private ByteVector stackMap;
  private int previousFrameOffset;
  private int[] previousFrame;
  private int[] frame;
  private int handlerCount;
  private Handler firstHandler;
  private Handler lastHandler;
  private int localVarCount;
  private ByteVector localVar;
  private int localVarTypeCount;
  private ByteVector localVarType;
  private int lineNumberCount;
  private ByteVector lineNumber;
  private Attribute cattrs;
  private boolean resize;
  private int subroutines;
  private final int compute;
  private Label labels;
  private Label previousBlock;
  private Label currentBlock;
  private int stackSize;
  private int maxStackSize;

  MethodWriter(ClassWriter cw, int access, String name, String desc, String signature, String[] exceptions, boolean computeMaxs, boolean computeFrames)
  {
    super(262144);
    if (cw.firstMethod == null)
      cw.firstMethod = this;
    else {
      cw.lastMethod.mv = this;
    }
    cw.lastMethod = this;
    this.cw = cw;
    this.access = access;
    if ("<init>".equals(name)) {
      this.access |= 524288;
    }
    this.name = cw.newUTF8(name);
    this.desc = cw.newUTF8(desc);
    this.descriptor = desc;

    this.signature = signature;

    if ((exceptions != null) && (exceptions.length > 0)) {
      this.exceptionCount = exceptions.length;
      this.exceptions = new int[this.exceptionCount];
      for (int i = 0; i < this.exceptionCount; i++) {
        this.exceptions[i] = cw.newClass(exceptions[i]);
      }
    }
    this.compute = (computeMaxs ? 1 : computeFrames ? 0 : 2);
    if ((computeMaxs) || (computeFrames))
    {
      int size = Type.getArgumentsAndReturnSizes(this.descriptor) >> 2;
      if ((access & 0x8) != 0) {
        size--;
      }
      this.maxLocals = size;
      this.currentLocals = size;

      this.labels = new Label();
      this.labels.status |= 8;
      visitLabel(this.labels);
    }
  }

  public AnnotationVisitor visitAnnotationDefault()
  {
    this.annd = new ByteVector();
    return new AnnotationWriter(this.cw, false, this.annd, null, 0);
  }

  public AnnotationVisitor visitAnnotation(String desc, boolean visible)
  {
    ByteVector bv = new ByteVector();

    bv.putShort(this.cw.newUTF8(desc)).putShort(0);
    AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, 2);
    if (visible) {
      aw.next = this.anns;
      this.anns = aw;
    } else {
      aw.next = this.ianns;
      this.ianns = aw;
    }
    return aw;
  }

  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
  {
    ByteVector bv = new ByteVector();
    if ("Ljava/lang/Synthetic;".equals(desc))
    {
      this.synthetics = Math.max(this.synthetics, parameter + 1);
      return new AnnotationWriter(this.cw, false, bv, null, 0);
    }

    bv.putShort(this.cw.newUTF8(desc)).putShort(0);
    AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, 2);
    if (visible) {
      if (this.panns == null) {
        this.panns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length];
      }
      aw.next = this.panns[parameter];
      this.panns[parameter] = aw;
    } else {
      if (this.ipanns == null) {
        this.ipanns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length];
      }
      aw.next = this.ipanns[parameter];
      this.ipanns[parameter] = aw;
    }
    return aw;
  }

  public void visitAttribute(Attribute attr)
  {
    if (attr.isCodeAttribute()) {
      attr.next = this.cattrs;
      this.cattrs = attr;
    } else {
      attr.next = this.attrs;
      this.attrs = attr;
    }
  }

  public void visitCode()
  {
  }

  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
  {
    if (this.compute == 0) {
      return;
    }

    if (type == -1) {
      if (this.previousFrame == null) {
        visitImplicitFirstFrame();
      }
      this.currentLocals = nLocal;
      int frameIndex = startFrame(this.code.length, nLocal, nStack);
      for (int i = 0; i < nLocal; i++) {
        if ((local[i] instanceof String))
          this.frame[(frameIndex++)] = 
            (0x1700000 | 
            this.cw.addType((String)local[i]));
        else if ((local[i] instanceof Integer))
          this.frame[(frameIndex++)] = ((Integer)local[i]).intValue();
        else {
          this.frame[(frameIndex++)] = 
            (0x1800000 | 
            this.cw.addUninitializedType("", 
            ((Label)local[i]).position));
        }
      }
      for (int i = 0; i < nStack; i++) {
        if ((stack[i] instanceof String))
          this.frame[(frameIndex++)] = 
            (0x1700000 | 
            this.cw.addType((String)stack[i]));
        else if ((stack[i] instanceof Integer))
          this.frame[(frameIndex++)] = ((Integer)stack[i]).intValue();
        else {
          this.frame[(frameIndex++)] = 
            (0x1800000 | 
            this.cw.addUninitializedType("", 
            ((Label)stack[i]).position));
        }
      }
      endFrame();
    }
    else
    {
      int delta;
      if (this.stackMap == null) {
        this.stackMap = new ByteVector();
        delta = this.code.length;
      } else {
        delta = this.code.length - this.previousFrameOffset - 1;
        if (delta < 0) {
          if (type == 3) {
            return;
          }
          throw new IllegalStateException();
        }

      }

      switch (type) {
      case 0:
        this.currentLocals = nLocal;
        this.stackMap.putByte(255).putShort(delta).putShort(nLocal);
        for (int i = 0; i < nLocal; i++) {
          writeFrameType(local[i]);
        }
        this.stackMap.putShort(nStack);
        for (int i = 0; i < nStack; i++) {
          writeFrameType(stack[i]);
        }
        break;
      case 1:
        this.currentLocals += nLocal;
        this.stackMap.putByte(251 + nLocal).putShort(delta);
        for (int i = 0; i < nLocal; i++) {
          writeFrameType(local[i]);
        }
        break;
      case 2:
        this.currentLocals -= nLocal;
        this.stackMap.putByte(251 - nLocal).putShort(delta);
        break;
      case 3:
        if (delta < 64)
          this.stackMap.putByte(delta);
        else {
          this.stackMap.putByte(251).putShort(delta);
        }
        break;
      case 4:
        if (delta < 64)
          this.stackMap.putByte(64 + delta);
        else {
          this.stackMap.putByte(247)
            .putShort(delta);
        }
        writeFrameType(stack[0]);
      }

      this.previousFrameOffset = this.code.length;
      this.frameCount += 1;
    }

    this.maxStack = Math.max(this.maxStack, nStack);
    this.maxLocals = Math.max(this.maxLocals, this.currentLocals);
  }

  public void visitInsn(int opcode)
  {
    this.code.putByte(opcode);

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, 0, null, null);
      }
      else {
        int size = this.stackSize + Frame.SIZE[opcode];
        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }

      if (((opcode >= 172) && (opcode <= 177)) || 
        (opcode == 191))
        noSuccessor();
    }
  }

  public void visitIntInsn(int opcode, int operand)
  {
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, operand, null, null);
      } else if (opcode != 188)
      {
        int size = this.stackSize + 1;
        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    if (opcode == 17)
      this.code.put12(opcode, operand);
    else
      this.code.put11(opcode, operand);
  }

  public void visitVarInsn(int opcode, int var)
  {
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, var, null, null);
      }
      else if (opcode == 169)
      {
        this.currentBlock.status |= 256;

        this.currentBlock.inputStackTop = this.stackSize;
        noSuccessor();
      } else {
        int size = this.stackSize + Frame.SIZE[opcode];
        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    if (this.compute != 2)
    {
      int n;
      if ((opcode == 22) || (opcode == 24) || 
        (opcode == 55) || (opcode == 57))
        n = var + 2;
      else {
        n = var + 1;
      }
      if (n > this.maxLocals) {
        this.maxLocals = n;
      }
    }

    if ((var < 4) && (opcode != 169))
    {
      int opt;
      if (opcode < 54)
      {
        opt = 26 + (opcode - 21 << 2) + var;
      }
      else {
        opt = 59 + (opcode - 54 << 2) + var;
      }
      this.code.putByte(opt);
    } else if (var >= 256) {
      this.code.putByte(196).put12(opcode, var);
    } else {
      this.code.put11(opcode, var);
    }
    if ((opcode >= 54) && (this.compute == 0) && (this.handlerCount > 0))
      visitLabel(new Label());
  }

  public void visitTypeInsn(int opcode, String type)
  {
    Item i = this.cw.newClassItem(type);

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, this.code.length, this.cw, i);
      } else if (opcode == 187)
      {
        int size = this.stackSize + 1;
        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    this.code.put12(opcode, i.index);
  }

  public void visitFieldInsn(int opcode, String owner, String name, String desc)
  {
    Item i = this.cw.newFieldItem(owner, name, desc);

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, 0, this.cw, i);
      }
      else
      {
        char c = desc.charAt(0);
        int size;
        switch (opcode) {
        case 178:
          size = this.stackSize + ((c == 'D') || (c == 'J') ? 2 : 1);
          break;
        case 179:
          size = this.stackSize + ((c == 'D') || (c == 'J') ? -2 : -1);
          break;
        case 180:
          size = this.stackSize + ((c == 'D') || (c == 'J') ? 1 : 0);
          break;
        default:
          size = this.stackSize + ((c == 'D') || (c == 'J') ? -3 : -2);
        }

        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    this.code.put12(opcode, i.index);
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    boolean itf = opcode == 185;
    Item i = this.cw.newMethodItem(owner, name, desc, itf);
    int argSize = i.intVal;

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, 0, this.cw, i);
      }
      else
      {
        if (argSize == 0)
        {
          argSize = Type.getArgumentsAndReturnSizes(desc);

          i.intVal = argSize;
        }
        int size;
        if (opcode == 184)
          size = this.stackSize - (argSize >> 2) + (argSize & 0x3) + 1;
        else {
          size = this.stackSize - (argSize >> 2) + (argSize & 0x3);
        }

        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    if (itf) {
      if (argSize == 0) {
        argSize = Type.getArgumentsAndReturnSizes(desc);
        i.intVal = argSize;
      }
      this.code.put12(185, i.index).put11(argSize >> 2, 0);
    } else {
      this.code.put12(opcode, i.index);
    }
  }

  public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object[] bsmArgs)
  {
    Item i = this.cw.newInvokeDynamicItem(name, desc, bsm, bsmArgs);
    int argSize = i.intVal;

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(186, 0, this.cw, i);
      }
      else
      {
        if (argSize == 0)
        {
          argSize = Type.getArgumentsAndReturnSizes(desc);

          i.intVal = argSize;
        }
        int size = this.stackSize - (argSize >> 2) + (argSize & 0x3) + 1;

        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    this.code.put12(186, i.index);
    this.code.putShort(0);
  }

  public void visitJumpInsn(int opcode, Label label)
  {
    Label nextInsn = null;

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(opcode, 0, null, null);

        label.getFirst().status |= 16;

        addSuccessor(0, label);
        if (opcode != 167)
        {
          nextInsn = new Label();
        }
      }
      else if (opcode == 168) {
        if ((label.status & 0x200) == 0) {
          label.status |= 512;
          this.subroutines += 1;
        }
        this.currentBlock.status |= 128;
        addSuccessor(this.stackSize + 1, label);

        nextInsn = new Label();
      }
      else
      {
        this.stackSize += Frame.SIZE[opcode];
        addSuccessor(this.stackSize, label);
      }

    }

    if (((label.status & 0x2) != 0) && 
      (label.position - this.code.length < -32768))
    {
      if (opcode == 167) {
        this.code.putByte(200);
      } else if (opcode == 168) {
        this.code.putByte(201);
      }
      else
      {
        if (nextInsn != null) {
          nextInsn.status |= 16;
        }
        this.code.putByte(opcode <= 166 ? (opcode + 1 ^ 0x1) - 1 : 
          opcode ^ 0x1);
        this.code.putShort(8);
        this.code.putByte(200);
      }
      label.put(this, this.code, this.code.length - 1, true);
    }
    else
    {
      this.code.putByte(opcode);
      label.put(this, this.code, this.code.length - 1, false);
    }
    if (this.currentBlock != null) {
      if (nextInsn != null)
      {
        visitLabel(nextInsn);
      }
      if (opcode == 167)
        noSuccessor();
    }
  }

  public void visitLabel(Label label)
  {
    this.resize |= label.resolve(this, this.code.length, this.code.data);

    if ((label.status & 0x1) != 0) {
      return;
    }
    if (this.compute == 0) {
      if (this.currentBlock != null) {
        if (label.position == this.currentBlock.position)
        {
          this.currentBlock.status |= label.status & 0x10;
          label.frame = this.currentBlock.frame;
          return;
        }

        addSuccessor(0, label);
      }

      this.currentBlock = label;
      if (label.frame == null) {
        label.frame = new Frame();
        label.frame.owner = label;
      }

      if (this.previousBlock != null) {
        if (label.position == this.previousBlock.position) {
          this.previousBlock.status |= label.status & 0x10;
          label.frame = this.previousBlock.frame;
          this.currentBlock = this.previousBlock;
          return;
        }
        this.previousBlock.successor = label;
      }
      this.previousBlock = label;
    } else if (this.compute == 1) {
      if (this.currentBlock != null)
      {
        this.currentBlock.outputStackMax = this.maxStackSize;
        addSuccessor(this.stackSize, label);
      }

      this.currentBlock = label;

      this.stackSize = 0;
      this.maxStackSize = 0;

      if (this.previousBlock != null) {
        this.previousBlock.successor = label;
      }
      this.previousBlock = label;
    }
  }

  public void visitLdcInsn(Object cst)
  {
    Item i = this.cw.newConstItem(cst);

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(18, 0, this.cw, i);
      }
      else
      {
        int size;
        if ((i.type == 5) || (i.type == 6))
          size = this.stackSize + 2;
        else {
          size = this.stackSize + 1;
        }

        if (size > this.maxStackSize) {
          this.maxStackSize = size;
        }
        this.stackSize = size;
      }
    }

    int index = i.index;
    if ((i.type == 5) || (i.type == 6))
      this.code.put12(20, index);
    else if (index >= 256)
      this.code.put12(19, index);
    else
      this.code.put11(18, index);
  }

  public void visitIincInsn(int var, int increment)
  {
    if ((this.currentBlock != null) && 
      (this.compute == 0)) {
      this.currentBlock.frame.execute(132, var, null, null);
    }

    if (this.compute != 2)
    {
      int n = var + 1;
      if (n > this.maxLocals) {
        this.maxLocals = n;
      }
    }

    if ((var > 255) || (increment > 127) || (increment < -128))
      this.code.putByte(196).put12(132, var)
        .putShort(increment);
    else
      this.code.putByte(132).put11(var, increment);
  }

  public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
  {
    int source = this.code.length;
    this.code.putByte(170);
    this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
    dflt.put(this, this.code, source, true);
    this.code.putInt(min).putInt(max);
    for (int i = 0; i < labels.length; i++) {
      labels[i].put(this, this.code, source, true);
    }

    visitSwitchInsn(dflt, labels);
  }

  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
  {
    int source = this.code.length;
    this.code.putByte(171);
    this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
    dflt.put(this, this.code, source, true);
    this.code.putInt(labels.length);
    for (int i = 0; i < labels.length; i++) {
      this.code.putInt(keys[i]);
      labels[i].put(this, this.code, source, true);
    }

    visitSwitchInsn(dflt, labels);
  }

  private void visitSwitchInsn(Label dflt, Label[] labels)
  {
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(171, 0, null, null);

        addSuccessor(0, dflt);
        dflt.getFirst().status |= 16;
        for (int i = 0; i < labels.length; i++) {
          addSuccessor(0, labels[i]);
          labels[i].getFirst().status |= 16;
        }
      }
      else {
        this.stackSize -= 1;

        addSuccessor(this.stackSize, dflt);
        for (int i = 0; i < labels.length; i++) {
          addSuccessor(this.stackSize, labels[i]);
        }
      }

      noSuccessor();
    }
  }

  public void visitMultiANewArrayInsn(String desc, int dims)
  {
    Item i = this.cw.newClassItem(desc);

    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(197, dims, this.cw, i);
      }
      else
      {
        this.stackSize += 1 - dims;
      }
    }

    this.code.put12(197, i.index).putByte(dims);
  }

  public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
  {
    this.handlerCount += 1;
    Handler h = new Handler();
    h.start = start;
    h.end = end;
    h.handler = handler;
    h.desc = type;
    h.type = (type != null ? this.cw.newClass(type) : 0);
    if (this.lastHandler == null)
      this.firstHandler = h;
    else {
      this.lastHandler.next = h;
    }
    this.lastHandler = h;
  }

  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
  {
    if (signature != null) {
      if (this.localVarType == null) {
        this.localVarType = new ByteVector();
      }
      this.localVarTypeCount += 1;
      this.localVarType.putShort(start.position)
        .putShort(end.position - start.position)
        .putShort(this.cw.newUTF8(name)).putShort(this.cw.newUTF8(signature))
        .putShort(index);
    }
    if (this.localVar == null) {
      this.localVar = new ByteVector();
    }
    this.localVarCount += 1;
    this.localVar.putShort(start.position)
      .putShort(end.position - start.position)
      .putShort(this.cw.newUTF8(name)).putShort(this.cw.newUTF8(desc))
      .putShort(index);
    if (this.compute != 2)
    {
      char c = desc.charAt(0);
      int n = index + ((c == 'J') || (c == 'D') ? 2 : 1);
      if (n > this.maxLocals)
        this.maxLocals = n;
    }
  }

  public void visitLineNumber(int line, Label start)
  {
    if (this.lineNumber == null) {
      this.lineNumber = new ByteVector();
    }
    this.lineNumberCount += 1;
    this.lineNumber.putShort(start.position);
    this.lineNumber.putShort(line);
  }

  public void visitMaxs(int maxStack, int maxLocals)
  {
    if (this.compute == 0)
    {
      Handler handler = this.firstHandler;
      while (handler != null) {
        Label l = handler.start.getFirst();
        Label h = handler.handler.getFirst();
        Label e = handler.end.getFirst();

        String t = handler.desc == null ? "java/lang/Throwable" : 
          handler.desc;
        int kind = 0x1700000 | this.cw.addType(t);

        h.status |= 16;

        while (l != e)
        {
          Edge b = new Edge();
          b.info = kind;
          b.successor = h;

          b.next = l.successors;
          l.successors = b;

          l = l.successor;
        }
        handler = handler.next;
      }

      Frame f = this.labels.frame;
      Type[] args = Type.getArgumentTypes(this.descriptor);
      f.initInputFrame(this.cw, this.access, args, this.maxLocals);
      visitFrame(f);

      int max = 0;
      Label changed = this.labels;
      Edge e;
      for (; changed != null; )
//        e != null)
      {
        Label l = changed;
        changed = changed.next;
        l.next = null;
        f = l.frame;

        if ((l.status & 0x10) != 0) {
          l.status |= 32;
        }

        l.status |= 64;

        int blockMax = f.inputStack.length + l.outputStackMax;
        if (blockMax > max) {
          max = blockMax;
        }

        e = l.successors;
        continue;
//        Label n = e.successor.getFirst();
//        boolean change = f.merge(this.cw, n.frame, e.info);
//        if ((change) && (n.next == null))
//        {
//          n.next = changed;
//          changed = n;
//        }
//        e = e.next;
      }

      Label l = this.labels;
      while (l != null) {
        f = l.frame;
        if ((l.status & 0x20) != 0) {
          visitFrame(f);
        }
        if ((l.status & 0x40) == 0)
        {
          Label k = l.successor;
          int start = l.position;
          int end = (k == null ? this.code.length : k.position) - 1;

          if (end >= start) {
            max = Math.max(max, 1);

            for (int i = start; i < end; i++) {
              this.code.data[i] = 0;
            }
            this.code.data[end] = -65;

            int frameIndex = startFrame(start, 0, 1);
            this.frame[frameIndex] = 
              (0x1700000 | 
              this.cw.addType("java/lang/Throwable"));
            endFrame();

            this.firstHandler = Handler.remove(this.firstHandler, l, k);
          }
        }
        l = l.successor;
      }

      handler = this.firstHandler;
      this.handlerCount = 0;
      while (handler != null) {
        this.handlerCount += 1;
        handler = handler.next;
      }

      this.maxStack = max;
    } else if (this.compute == 1)
    {
      Handler handler = this.firstHandler;
      while (handler != null) {
        Label l = handler.start;
        Label h = handler.handler;
        Label e = handler.end;

        while (l != e)
        {
          Edge b = new Edge();
          b.info = 2147483647;
          b.successor = h;

          if ((l.status & 0x80) == 0) {
            b.next = l.successors;
            l.successors = b;
          }
          else
          {
            b.next = l.successors.next.next;
            l.successors.next.next = b;
          }

          l = l.successor;
        }
        handler = handler.next;
      }

      if (this.subroutines > 0)
      {
        int id = 0;
        this.labels.visitSubroutine(null, 1L, this.subroutines);

        Label l = this.labels;
        while (l != null) {
          if ((l.status & 0x80) != 0)
          {
            Label subroutine = l.successors.next.successor;

            if ((subroutine.status & 0x400) == 0)
            {
              id++;
              subroutine.visitSubroutine(null, id / 32L << 32 | 
                1L << id % 32, this.subroutines);
            }
          }
          l = l.successor;
        }

        l = this.labels;
        while (l != null) {
          if ((l.status & 0x80) != 0) {
            Label L = this.labels;
            while (L != null) {
              L.status &= -2049;
              L = L.successor;
            }

            Label subroutine = l.successors.next.successor;
            subroutine.visitSubroutine(l, 0L, this.subroutines);
          }
          l = l.successor;
        }

      }

      int max = 0;
      Label stack = this.labels;
      Edge b;
      for (; stack != null; )
//        b != null)
      {
        Label l = stack;
        stack = stack.next;

        int start = l.inputStackTop;
        int blockMax = start + l.outputStackMax;

        if (blockMax > max) {
          max = blockMax;
        }

        b = l.successors;
        if ((l.status & 0x80) != 0)
        {
          b = b.next;

          continue;
//          l = b.successor;
//
//          if ((l.status & 0x8) == 0)
//          {
//            l.inputStackTop = (b.info == 2147483647 ? 1 : start + 
//              b.info);
//
//            l.status |= 8;
//            l.next = stack;
//            stack = l;
//          }
//          b = b.next;
        }
      }
      this.maxStack = Math.max(maxStack, max);
    } else {
      this.maxStack = maxStack;
      this.maxLocals = maxLocals;
    }
  }

  public void visitEnd()
  {
  }

  private void addSuccessor(int info, Label successor)
  {
    Edge b = new Edge();
    b.info = info;
    b.successor = successor;

    b.next = this.currentBlock.successors;
    this.currentBlock.successors = b;
  }

  private void noSuccessor()
  {
    if (this.compute == 0) {
      Label l = new Label();
      l.frame = new Frame();
      l.frame.owner = l;
      l.resolve(this, this.code.length, this.code.data);
      this.previousBlock.successor = l;
      this.previousBlock = l;
    } else {
      this.currentBlock.outputStackMax = this.maxStackSize;
    }
    this.currentBlock = null;
  }

  private void visitFrame(Frame f)
  {
    int nTop = 0;
    int nLocal = 0;
    int nStack = 0;
    int[] locals = f.inputLocals;
    int[] stacks = f.inputStack;

    for (int i = 0; i < locals.length; i++) {
      int t = locals[i];
      if (t == 16777216) {
        nTop++;
      } else {
        nLocal += nTop + 1;
        nTop = 0;
      }
      if ((t == 16777220) || (t == 16777219)) {
        i++;
      }

    }

    for (int i = 0; i < stacks.length; i++) {
      int t = stacks[i];
      nStack++;
      if ((t == 16777220) || (t == 16777219)) {
        i++;
      }
    }

    int frameIndex = startFrame(f.owner.position, nLocal, nStack);
    for (int i = 0; nLocal > 0; nLocal--) {
      int t = locals[i];
      this.frame[(frameIndex++)] = t;
      if ((t == 16777220) || (t == 16777219))
        i++;
      i++;
    }

    for (int i = 0; i < stacks.length; i++) {
      int t = stacks[i];
      this.frame[(frameIndex++)] = t;
      if ((t == 16777220) || (t == 16777219)) {
        i++;
      }
    }
    endFrame();
  }

  private void visitImplicitFirstFrame()
  {
    int frameIndex = startFrame(0, this.descriptor.length() + 1, 0);
    if ((this.access & 0x8) == 0) {
      if ((this.access & 0x80000) == 0)
        this.frame[(frameIndex++)] = (0x1700000 | this.cw.addType(this.cw.thisName));
      else {
        this.frame[(frameIndex++)] = 6;
      }
    }
    int i = 1;
    while (true) {
      int j = i;
      switch (this.descriptor.charAt(i++)) {
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        this.frame[(frameIndex++)] = 1;
        break;
      case 'F':
        this.frame[(frameIndex++)] = 2;
        break;
      case 'J':
        this.frame[(frameIndex++)] = 4;
        break;
      case 'D':
        this.frame[(frameIndex++)] = 3;
        break;
      case '[':
        while (this.descriptor.charAt(i) == '[') {
          i++;
        }
        if (this.descriptor.charAt(i) == 'L') {
          i++;
          while (this.descriptor.charAt(i) != ';') {
            i++;
          }
        }
        this.frame[(frameIndex++)] = 
          (0x1700000 | 
          this.cw.addType(this.descriptor.substring(j, ++i)));
        break;
      case 'L':
        while (this.descriptor.charAt(i) != ';') {
          i++;
        }
        this.frame[(frameIndex++)] = 
          (0x1700000 | 
          this.cw.addType(this.descriptor.substring(j + 1, i++)));
      }

    }

//    this.frame[1] = (frameIndex - 3);
//    endFrame();
  }

  private int startFrame(int offset, int nLocal, int nStack)
  {
    int n = 3 + nLocal + nStack;
    if ((this.frame == null) || (this.frame.length < n)) {
      this.frame = new int[n];
    }
    this.frame[0] = offset;
    this.frame[1] = nLocal;
    this.frame[2] = nStack;
    return 3;
  }

  private void endFrame()
  {
    if (this.previousFrame != null) {
      if (this.stackMap == null) {
        this.stackMap = new ByteVector();
      }
      writeFrame();
      this.frameCount += 1;
    }
    this.previousFrame = this.frame;
    this.frame = null;
  }

  private void writeFrame()
  {
    int clocalsSize = this.frame[1];
    int cstackSize = this.frame[2];
    if ((this.cw.version & 0xFFFF) < 50) {
      this.stackMap.putShort(this.frame[0]).putShort(clocalsSize);
      writeFrameTypes(3, 3 + clocalsSize);
      this.stackMap.putShort(cstackSize);
      writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize);
      return;
    }
    int localsSize = this.previousFrame[1];
    int type = 255;
    int k = 0;
    int delta;
    if (this.frameCount == 0)
      delta = this.frame[0];
    else {
      delta = this.frame[0] - this.previousFrame[0] - 1;
    }
    if (cstackSize == 0) {
      k = clocalsSize - localsSize;
      switch (k) {
      case -3:
      case -2:
      case -1:
        type = 248;
        localsSize = clocalsSize;
        break;
      case 0:
        type = delta < 64 ? 0 : 251;
        break;
      case 1:
      case 2:
      case 3:
        type = 252;
      default:
        break;
      } } else if ((clocalsSize == localsSize) && (cstackSize == 1)) {
      type = delta < 63 ? 64 : 
        247;
    }
    if (type != 255)
    {
      int l = 3;
      for (int j = 0; j < localsSize; j++) {
        if (this.frame[l] != this.previousFrame[l]) {
          type = 255;
          break;
        }
        l++;
      }
    }
    switch (type) {
    case 0:
      this.stackMap.putByte(delta);
      break;
    case 64:
      this.stackMap.putByte(64 + delta);
      writeFrameTypes(3 + clocalsSize, 4 + clocalsSize);
      break;
    case 247:
      this.stackMap.putByte(247).putShort(
        delta);
      writeFrameTypes(3 + clocalsSize, 4 + clocalsSize);
      break;
    case 251:
      this.stackMap.putByte(251).putShort(delta);
      break;
    case 248:
      this.stackMap.putByte(251 + k).putShort(delta);
      break;
    case 252:
      this.stackMap.putByte(251 + k).putShort(delta);
      writeFrameTypes(3 + localsSize, 3 + clocalsSize);
      break;
    default:
      this.stackMap.putByte(255).putShort(delta).putShort(clocalsSize);
      writeFrameTypes(3, 3 + clocalsSize);
      this.stackMap.putShort(cstackSize);
      writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize);
    }
  }

  private void writeFrameTypes(int start, int end)
  {
    for (int i = start; i < end; i++) {
      int t = this.frame[i];
      int d = t & 0xF0000000;
      if (d == 0) {
        int v = t & 0xFFFFF;
        switch (t & 0xFF00000) {
        case 24117248:
          this.stackMap.putByte(7).putShort(
            this.cw.newClass(this.cw.typeTable[v].strVal1));
          break;
        case 25165824:
          this.stackMap.putByte(8).putShort(this.cw.typeTable[v].intVal);
          break;
        default:
          this.stackMap.putByte(v); break;
        }
      } else {
        StringBuffer buf = new StringBuffer();
        d >>= 28;
        while (d-- > 0) {
          buf.append('[');
        }
        if ((t & 0xFF00000) == 24117248) {
          buf.append('L');
          buf.append(this.cw.typeTable[(t & 0xFFFFF)].strVal1);
          buf.append(';');
        } else {
          switch (t & 0xF) {
          case 1:
            buf.append('I');
            break;
          case 2:
            buf.append('F');
            break;
          case 3:
            buf.append('D');
            break;
          case 9:
            buf.append('Z');
            break;
          case 10:
            buf.append('B');
            break;
          case 11:
            buf.append('C');
            break;
          case 12:
            buf.append('S');
            break;
          case 4:
          case 5:
          case 6:
          case 7:
          case 8:
          default:
            buf.append('J');
          }
        }
        this.stackMap.putByte(7).putShort(this.cw.newClass(buf.toString()));
      }
    }
  }

  private void writeFrameType(Object type) {
    if ((type instanceof String))
      this.stackMap.putByte(7).putShort(this.cw.newClass((String)type));
    else if ((type instanceof Integer))
      this.stackMap.putByte(((Integer)type).intValue());
    else
      this.stackMap.putByte(8).putShort(((Label)type).position);
  }

  final int getSize()
  {
    if (this.classReaderOffset != 0) {
      return 6 + this.classReaderLength;
    }
    if (this.resize)
    {
      resizeInstructions();
    }

    int size = 8;
    if (this.code.length > 0) {
      if (this.code.length > 65536) {
        throw new RuntimeException("Method code too large!");
      }
      this.cw.newUTF8("Code");
      size += 18 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null) {
        this.cw.newUTF8("LocalVariableTable");
        size += 8 + this.localVar.length;
      }
      if (this.localVarType != null) {
        this.cw.newUTF8("LocalVariableTypeTable");
        size += 8 + this.localVarType.length;
      }
      if (this.lineNumber != null) {
        this.cw.newUTF8("LineNumberTable");
        size += 8 + this.lineNumber.length;
      }
      if (this.stackMap != null) {
        boolean zip = (this.cw.version & 0xFFFF) >= 50;
        this.cw.newUTF8(zip ? "StackMapTable" : "StackMap");
        size += 8 + this.stackMap.length;
      }
      if (this.cattrs != null)
      {
        size = size + this.cattrs
          .getSize(this.cw, this.code.data, this.code.length, this.maxStack, 
          this.maxLocals);
      }
    }
    if (this.exceptionCount > 0) {
      this.cw.newUTF8("Exceptions");
      size += 8 + 2 * this.exceptionCount;
    }
    if (((this.access & 0x1000) != 0) && (
      ((this.cw.version & 0xFFFF) < 49) || 
      ((this.access & 0x40000) != 0))) {
      this.cw.newUTF8("Synthetic");
      size += 6;
    }

    if ((this.access & 0x20000) != 0) {
      this.cw.newUTF8("Deprecated");
      size += 6;
    }
    if (this.signature != null) {
      this.cw.newUTF8("Signature");
      this.cw.newUTF8(this.signature);
      size += 8;
    }
    if (this.annd != null) {
      this.cw.newUTF8("AnnotationDefault");
      size += 6 + this.annd.length;
    }
    if (this.anns != null) {
      this.cw.newUTF8("RuntimeVisibleAnnotations");
      size += 8 + this.anns.getSize();
    }
    if (this.ianns != null) {
      this.cw.newUTF8("RuntimeInvisibleAnnotations");
      size += 8 + this.ianns.getSize();
    }
    if (this.panns != null) {
      this.cw.newUTF8("RuntimeVisibleParameterAnnotations");
      size += 7 + 2 * (this.panns.length - this.synthetics);
      for (int i = this.panns.length - 1; i >= this.synthetics; i--) {
        size += (this.panns[i] == null ? 0 : this.panns[i].getSize());
      }
    }
    if (this.ipanns != null) {
      this.cw.newUTF8("RuntimeInvisibleParameterAnnotations");
      size += 7 + 2 * (this.ipanns.length - this.synthetics);
      for (int i = this.ipanns.length - 1; i >= this.synthetics; i--) {
        size += (this.ipanns[i] == null ? 0 : this.ipanns[i].getSize());
      }
    }
    if (this.attrs != null) {
      size += this.attrs.getSize(this.cw, null, 0, -1, -1);
    }
    return size;
  }

  final void put(ByteVector out)
  {
    int FACTOR = 64;
    int mask = 0xE0000 | 
      (this.access & 0x40000) / 64;
    out.putShort(this.access & (mask ^ 0xFFFFFFFF)).putShort(this.name).putShort(this.desc);
    if (this.classReaderOffset != 0) {
      out.putByteArray(this.cw.cr.b, this.classReaderOffset, this.classReaderLength);
      return;
    }
    int attributeCount = 0;
    if (this.code.length > 0) {
      attributeCount++;
    }
    if (this.exceptionCount > 0) {
      attributeCount++;
    }
    if (((this.access & 0x1000) != 0) && (
      ((this.cw.version & 0xFFFF) < 49) || 
      ((this.access & 0x40000) != 0))) {
      attributeCount++;
    }

    if ((this.access & 0x20000) != 0) {
      attributeCount++;
    }
    if (this.signature != null) {
      attributeCount++;
    }
    if (this.annd != null) {
      attributeCount++;
    }
    if (this.anns != null) {
      attributeCount++;
    }
    if (this.ianns != null) {
      attributeCount++;
    }
    if (this.panns != null) {
      attributeCount++;
    }
    if (this.ipanns != null) {
      attributeCount++;
    }
    if (this.attrs != null) {
      attributeCount += this.attrs.getCount();
    }
    out.putShort(attributeCount);
    if (this.code.length > 0) {
      int size = 12 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null) {
        size += 8 + this.localVar.length;
      }
      if (this.localVarType != null) {
        size += 8 + this.localVarType.length;
      }
      if (this.lineNumber != null) {
        size += 8 + this.lineNumber.length;
      }
      if (this.stackMap != null) {
        size += 8 + this.stackMap.length;
      }
      if (this.cattrs != null)
      {
        size = size + this.cattrs
          .getSize(this.cw, this.code.data, this.code.length, this.maxStack, 
          this.maxLocals);
      }
      out.putShort(this.cw.newUTF8("Code")).putInt(size);
      out.putShort(this.maxStack).putShort(this.maxLocals);
      out.putInt(this.code.length).putByteArray(this.code.data, 0, this.code.length);
      out.putShort(this.handlerCount);
      if (this.handlerCount > 0) {
        Handler h = this.firstHandler;
        while (h != null) {
          out.putShort(h.start.position).putShort(h.end.position)
            .putShort(h.handler.position).putShort(h.type);
          h = h.next;
        }
      }
      attributeCount = 0;
      if (this.localVar != null) {
        attributeCount++;
      }
      if (this.localVarType != null) {
        attributeCount++;
      }
      if (this.lineNumber != null) {
        attributeCount++;
      }
      if (this.stackMap != null) {
        attributeCount++;
      }
      if (this.cattrs != null) {
        attributeCount += this.cattrs.getCount();
      }
      out.putShort(attributeCount);
      if (this.localVar != null) {
        out.putShort(this.cw.newUTF8("LocalVariableTable"));
        out.putInt(this.localVar.length + 2).putShort(this.localVarCount);
        out.putByteArray(this.localVar.data, 0, this.localVar.length);
      }
      if (this.localVarType != null) {
        out.putShort(this.cw.newUTF8("LocalVariableTypeTable"));
        out.putInt(this.localVarType.length + 2).putShort(this.localVarTypeCount);
        out.putByteArray(this.localVarType.data, 0, this.localVarType.length);
      }
      if (this.lineNumber != null) {
        out.putShort(this.cw.newUTF8("LineNumberTable"));
        out.putInt(this.lineNumber.length + 2).putShort(this.lineNumberCount);
        out.putByteArray(this.lineNumber.data, 0, this.lineNumber.length);
      }
      if (this.stackMap != null) {
        boolean zip = (this.cw.version & 0xFFFF) >= 50;
        out.putShort(this.cw.newUTF8(zip ? "StackMapTable" : "StackMap"));
        out.putInt(this.stackMap.length + 2).putShort(this.frameCount);
        out.putByteArray(this.stackMap.data, 0, this.stackMap.length);
      }
      if (this.cattrs != null) {
        this.cattrs.put(this.cw, this.code.data, this.code.length, this.maxLocals, this.maxStack, out);
      }
    }
    if (this.exceptionCount > 0) {
      out.putShort(this.cw.newUTF8("Exceptions")).putInt(
        2 * this.exceptionCount + 2);
      out.putShort(this.exceptionCount);
      for (int i = 0; i < this.exceptionCount; i++) {
        out.putShort(this.exceptions[i]);
      }
    }
    if (((this.access & 0x1000) != 0) && (
      ((this.cw.version & 0xFFFF) < 49) || 
      ((this.access & 0x40000) != 0))) {
      out.putShort(this.cw.newUTF8("Synthetic")).putInt(0);
    }

    if ((this.access & 0x20000) != 0) {
      out.putShort(this.cw.newUTF8("Deprecated")).putInt(0);
    }
    if (this.signature != null) {
      out.putShort(this.cw.newUTF8("Signature")).putInt(2)
        .putShort(this.cw.newUTF8(this.signature));
    }
    if (this.annd != null) {
      out.putShort(this.cw.newUTF8("AnnotationDefault"));
      out.putInt(this.annd.length);
      out.putByteArray(this.annd.data, 0, this.annd.length);
    }
    if (this.anns != null) {
      out.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(out);
    }
    if (this.ianns != null) {
      out.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(out);
    }
    if (this.panns != null) {
      out.putShort(this.cw.newUTF8("RuntimeVisibleParameterAnnotations"));
      AnnotationWriter.put(this.panns, this.synthetics, out);
    }
    if (this.ipanns != null) {
      out.putShort(this.cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
      AnnotationWriter.put(this.ipanns, this.synthetics, out);
    }
    if (this.attrs != null)
      this.attrs.put(this.cw, null, 0, -1, -1, out);
  }

  private void resizeInstructions()
  {
    byte[] b = this.code.data;

    int[] allIndexes = new int[0];
    int[] allSizes = new int[0];

    boolean[] resize = new boolean[this.code.length];

    int state = 3;
    do {
      if (state == 3) {
        state = 2;
      }
      int u = 0;
      while (u < b.length) {
        int opcode = b[u] & 0xFF;
        int insert = 0;

        switch (ClassWriter.TYPE[opcode]) {
        case 0:
        case 4:
          u++;
          break;
        case 9:
          int label;
          if (opcode > 201)
          {
            opcode = opcode < 218 ? opcode - 49 : opcode - 20;
            label = u + readUnsignedShort(b, u + 1);
          } else {
            label = u + readShort(b, u + 1);
          }
          int newOffset = getNewOffset(allIndexes, allSizes, u, label);
          if (((newOffset < -32768) || 
            (newOffset > 32767)) && 
            (resize[u] == false)) {
            if ((opcode == 167) || (opcode == 168))
            {
              insert = 2;
            }
            else
            {
              insert = 5;
            }
            resize[u] = true;
          }

          u += 3;
          break;
        case 10:
          u += 5;
          break;
        case 14:
          if (state == 1)
          {
             newOffset = getNewOffset(allIndexes, allSizes, 0, u);
            insert = -(newOffset & 0x3);
          } else if (resize[u] == false)
          {
            insert = u & 0x3;
            resize[u] = true;
          }

          u = u + 4 - (u & 0x3);
          u += 4 * (readInt(b, u + 8) - readInt(b, u + 4) + 1) + 12;
          break;
        case 15:
          if (state == 1)
          {
             newOffset = getNewOffset(allIndexes, allSizes, 0, u);
            insert = -(newOffset & 0x3);
          } else if (resize[u] == false)
          {
            insert = u & 0x3;
            resize[u] = true;
          }

          u = u + 4 - (u & 0x3);
          u += 8 * readInt(b, u + 4) + 8;
          break;
        case 17:
          opcode = b[(u + 1)] & 0xFF;
          if (opcode == 132)
            u += 6;
          else {
            u += 4;
          }
          break;
        case 1:
        case 3:
        case 11:
          u += 2;
          break;
        case 2:
        case 5:
        case 6:
        case 12:
        case 13:
          u += 3;
          break;
        case 7:
        case 8:
          u += 5;
          break;
        case 16:
        default:
          u += 4;
        }

        if (insert != 0)
        {
          int[] newIndexes = new int[allIndexes.length + 1];
          int[] newSizes = new int[allSizes.length + 1];
          System.arraycopy(allIndexes, 0, newIndexes, 0, 
            allIndexes.length);
          System.arraycopy(allSizes, 0, newSizes, 0, allSizes.length);
          newIndexes[allIndexes.length] = u;
          newSizes[allSizes.length] = insert;
          allIndexes = newIndexes;
          allSizes = newSizes;
          if (insert > 0) {
            state = 3;
          }
        }
      }
      if (state < 3)
        state--;
    }
    while (state != 0);

    ByteVector newCode = new ByteVector(this.code.length);

    int u = 0;
    while (u < this.code.length) {
      int opcode = b[u] & 0xFF;
      switch (ClassWriter.TYPE[opcode]) {
      case 0:
      case 4:
        newCode.putByte(opcode);
        u++;
        break;
      case 9:
        int label;
        if (opcode > 201)
        {
          opcode = opcode < 218 ? opcode - 49 : opcode - 20;
          label = u + readUnsignedShort(b, u + 1);
        } else {
          label = u + readShort(b, u + 1);
        }
        int newOffset = getNewOffset(allIndexes, allSizes, u, label);
        if (resize[u] != false)
        {
          if (opcode == 167) {
            newCode.putByte(200);
          } else if (opcode == 168) {
            newCode.putByte(201);
          } else {
            newCode.putByte(opcode <= 166 ? (opcode + 1 ^ 0x1) - 1 : 
              opcode ^ 0x1);
            newCode.putShort(8);
            newCode.putByte(200);

            newOffset -= 3;
          }
          newCode.putInt(newOffset);
        } else {
          newCode.putByte(opcode);
          newCode.putShort(newOffset);
        }
        u += 3;
        break;
      case 10:
         label = u + readInt(b, u + 1);
         newOffset = getNewOffset(allIndexes, allSizes, u, label);
        newCode.putByte(opcode);
        newCode.putInt(newOffset);
        u += 5;
        break;
      case 14:
        int v = u;
        u = u + 4 - (v & 0x3);

        newCode.putByte(170);
        newCode.putByteArray(null, 0, (4 - newCode.length % 4) % 4);
         label = v + readInt(b, u);
        u += 4;
         newOffset = getNewOffset(allIndexes, allSizes, v, label);
        newCode.putInt(newOffset);
        int j = readInt(b, u);
        u += 4;
        newCode.putInt(j);
        j = readInt(b, u) - j + 1;
        u += 4;
        newCode.putInt(readInt(b, u - 4));
        for (; j > 0; j--) {
          label = v + readInt(b, u);
          u += 4;
          newOffset = getNewOffset(allIndexes, allSizes, v, label);
          newCode.putInt(newOffset);
        }
        break;
      case 15:
         v = u;
        u = u + 4 - (v & 0x3);

        newCode.putByte(171);
        newCode.putByteArray(null, 0, (4 - newCode.length % 4) % 4);
         label = v + readInt(b, u);
        u += 4;
         newOffset = getNewOffset(allIndexes, allSizes, v, label);
        newCode.putInt(newOffset);
         j = readInt(b, u);
        u += 4;
        newCode.putInt(j);
        for (; j > 0; j--) {
          newCode.putInt(readInt(b, u));
          u += 4;
          label = v + readInt(b, u);
          u += 4;
          newOffset = getNewOffset(allIndexes, allSizes, v, label);
          newCode.putInt(newOffset);
        }
        break;
      case 17:
        opcode = b[(u + 1)] & 0xFF;
        if (opcode == 132) {
          newCode.putByteArray(b, u, 6);
          u += 6;
        } else {
          newCode.putByteArray(b, u, 4);
          u += 4;
        }
        break;
      case 1:
      case 3:
      case 11:
        newCode.putByteArray(b, u, 2);
        u += 2;
        break;
      case 2:
      case 5:
      case 6:
      case 12:
      case 13:
        newCode.putByteArray(b, u, 3);
        u += 3;
        break;
      case 7:
      case 8:
        newCode.putByteArray(b, u, 5);
        u += 5;
        break;
      case 16:
      default:
        newCode.putByteArray(b, u, 4);
        u += 4;
      }

    }

    if (this.frameCount > 0) {
      if (this.compute == 0) {
        this.frameCount = 0;
        this.stackMap = null;
        this.previousFrame = null;
        this.frame = null;
        Frame f = new Frame();
        f.owner = this.labels;
        Type[] args = Type.getArgumentTypes(this.descriptor);
        f.initInputFrame(this.cw, this.access, args, this.maxLocals);
        visitFrame(f);
        Label l = this.labels;
        while (l != null)
        {
          u = l.position - 3;
          if (((l.status & 0x20) != 0) || ((u >= 0) && (resize[u] != false))) {
            getNewOffset(allIndexes, allSizes, l);

            visitFrame(l.frame);
          }
          l = l.successor;
        }

      }
      else
      {
        this.cw.invalidFrames = true;
      }
    }

    Handler h = this.firstHandler;
    while (h != null) {
      getNewOffset(allIndexes, allSizes, h.start);
      getNewOffset(allIndexes, allSizes, h.end);
      getNewOffset(allIndexes, allSizes, h.handler);
      h = h.next;
    }

    for (int i = 0; i < 2; i++) {
      ByteVector bv = i == 0 ? this.localVar : this.localVarType;
      if (bv != null) {
        b = bv.data;
        u = 0;
        while (u < bv.length) {
          int label = readUnsignedShort(b, u);
          int newOffset = getNewOffset(allIndexes, allSizes, 0, label);
          writeShort(b, u, newOffset);
          label += readUnsignedShort(b, u + 2);
          newOffset = getNewOffset(allIndexes, allSizes, 0, label) - 
            newOffset;
          writeShort(b, u + 2, newOffset);
          u += 10;
        }
      }
    }
    if (this.lineNumber != null) {
      b = this.lineNumber.data;
      u = 0;
      while (u < this.lineNumber.length) {
        writeShort(
          b, 
          u, 
          getNewOffset(allIndexes, allSizes, 0, 
          readUnsignedShort(b, u)));
        u += 4;
      }
    }

    Attribute attr = this.cattrs;
    while (attr != null) {
      Label[] labels = attr.getLabels();
      if (labels != null) {
        for (int i = labels.length - 1; i >= 0; i--) {
          getNewOffset(allIndexes, allSizes, labels[i]);
        }
      }
      attr = attr.next;
    }

    this.code = newCode;
  }

  static int readUnsignedShort(byte[] b, int index)
  {
    return (b[index] & 0xFF) << 8 | b[(index + 1)] & 0xFF;
  }

  static short readShort(byte[] b, int index)
  {
    return (short)((b[index] & 0xFF) << 8 | b[(index + 1)] & 0xFF);
  }

  static int readInt(byte[] b, int index)
  {
    return (b[index] & 0xFF) << 24 | (b[(index + 1)] & 0xFF) << 16 | 
      (b[(index + 2)] & 0xFF) << 8 | b[(index + 3)] & 0xFF;
  }

  static void writeShort(byte[] b, int index, int s)
  {
    b[index] = ((byte)(s >>> 8));
    b[(index + 1)] = ((byte)s);
  }

  static int getNewOffset(int[] indexes, int[] sizes, int begin, int end)
  {
    int offset = end - begin;
    for (int i = 0; i < indexes.length; i++) {
      if ((begin < indexes[i]) && (indexes[i] <= end))
      {
        offset += sizes[i];
      } else if ((end < indexes[i]) && (indexes[i] <= begin))
      {
        offset -= sizes[i];
      }
    }
    return offset;
  }

  static void getNewOffset(int[] indexes, int[] sizes, Label label)
  {
    if ((label.status & 0x4) == 0) {
      label.position = getNewOffset(indexes, sizes, 0, label.position);
      label.status |= 4;
    }
  }
}