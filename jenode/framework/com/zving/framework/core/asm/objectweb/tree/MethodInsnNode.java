package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class MethodInsnNode extends AbstractInsnNode
{
  public String owner;
  public String name;
  public String desc;

  public MethodInsnNode(int opcode, String owner, String name, String desc)
  {
    super(opcode);
    this.owner = owner;
    this.name = name;
    this.desc = desc;
  }

  public void setOpcode(int opcode)
  {
    this.opcode = opcode;
  }

  public int getType()
  {
    return 5;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitMethodInsn(this.opcode, this.owner, this.name, this.desc);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new MethodInsnNode(this.opcode, this.owner, this.name, this.desc);
  }
}