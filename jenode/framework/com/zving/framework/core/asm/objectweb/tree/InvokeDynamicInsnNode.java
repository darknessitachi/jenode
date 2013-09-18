package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.Handle;
import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class InvokeDynamicInsnNode extends AbstractInsnNode
{
  public String name;
  public String desc;
  public Handle bsm;
  public Object[] bsmArgs;

  public InvokeDynamicInsnNode(String name, String desc, Handle bsm, Object[] bsmArgs)
  {
    super(186);
    this.name = name;
    this.desc = desc;
    this.bsm = bsm;
    this.bsmArgs = bsmArgs;
  }

  public int getType()
  {
    return 6;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs);
  }
}