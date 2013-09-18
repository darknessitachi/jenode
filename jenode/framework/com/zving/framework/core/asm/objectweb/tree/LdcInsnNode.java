package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class LdcInsnNode extends AbstractInsnNode
{
  public Object cst;

  public LdcInsnNode(Object cst)
  {
    super(18);
    this.cst = cst;
  }

  public int getType()
  {
    return 9;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitLdcInsn(this.cst);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new LdcInsnNode(this.cst);
  }
}