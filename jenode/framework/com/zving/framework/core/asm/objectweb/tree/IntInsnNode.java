package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class IntInsnNode extends AbstractInsnNode
{
  public int operand;

  public IntInsnNode(int opcode, int operand)
  {
    super(opcode);
    this.operand = operand;
  }

  public void setOpcode(int opcode)
  {
    this.opcode = opcode;
  }

  public int getType()
  {
    return 1;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitIntInsn(this.opcode, this.operand);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new IntInsnNode(this.opcode, this.operand);
  }
}