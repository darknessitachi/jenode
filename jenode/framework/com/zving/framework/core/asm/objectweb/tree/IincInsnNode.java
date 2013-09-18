package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class IincInsnNode extends AbstractInsnNode
{
  public int var;
  public int incr;

  public IincInsnNode(int var, int incr)
  {
    super(132);
    this.var = var;
    this.incr = incr;
  }

  public int getType()
  {
    return 10;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitIincInsn(this.var, this.incr);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new IincInsnNode(this.var, this.incr);
  }
}