package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class VarInsnNode extends AbstractInsnNode
{
  public int var;

  public VarInsnNode(int opcode, int var)
  {
    super(opcode);
    this.var = var;
  }

  public void setOpcode(int opcode)
  {
    this.opcode = opcode;
  }

  public int getType()
  {
    return 2;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitVarInsn(this.opcode, this.var);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new VarInsnNode(this.opcode, this.var);
  }
}