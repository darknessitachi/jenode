package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class InsnNode extends AbstractInsnNode
{
  public InsnNode(int opcode)
  {
    super(opcode);
  }

  public int getType()
  {
    return 0;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitInsn(this.opcode);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new InsnNode(this.opcode);
  }
}