package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.List;
import java.util.Map;

public abstract class AbstractInsnNode
{
  public static final int INSN = 0;
  public static final int INT_INSN = 1;
  public static final int VAR_INSN = 2;
  public static final int TYPE_INSN = 3;
  public static final int FIELD_INSN = 4;
  public static final int METHOD_INSN = 5;
  public static final int INVOKE_DYNAMIC_INSN = 6;
  public static final int JUMP_INSN = 7;
  public static final int LABEL = 8;
  public static final int LDC_INSN = 9;
  public static final int IINC_INSN = 10;
  public static final int TABLESWITCH_INSN = 11;
  public static final int LOOKUPSWITCH_INSN = 12;
  public static final int MULTIANEWARRAY_INSN = 13;
  public static final int FRAME = 14;
  public static final int LINE = 15;
  protected int opcode;
  AbstractInsnNode prev;
  AbstractInsnNode next;
  int index;

  protected AbstractInsnNode(int opcode)
  {
    this.opcode = opcode;
    this.index = -1;
  }

  public int getOpcode()
  {
    return this.opcode;
  }

  public abstract int getType();

  public AbstractInsnNode getPrevious()
  {
    return this.prev;
  }

  public AbstractInsnNode getNext()
  {
    return this.next;
  }

  public abstract void accept(MethodVisitor paramMethodVisitor);

  public abstract AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap);

  static LabelNode clone(LabelNode label, Map<LabelNode, LabelNode> map)
  {
    return (LabelNode)map.get(label);
  }

  static LabelNode[] clone(List<LabelNode> labels, Map<LabelNode, LabelNode> map)
  {
    LabelNode[] clones = new LabelNode[labels.size()];
    for (int i = 0; i < clones.length; i++) {
      clones[i] = ((LabelNode)map.get(labels.get(i)));
    }
    return clones;
  }
}