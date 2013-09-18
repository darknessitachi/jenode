package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class JumpInsnNode extends AbstractInsnNode
{
  public LabelNode label;

  public JumpInsnNode(int opcode, LabelNode label)
  {
    super(opcode);
    this.label = label;
  }

  public void setOpcode(int opcode)
  {
    this.opcode = opcode;
  }

  public int getType()
  {
    return 7;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitJumpInsn(this.opcode, this.label.getLabel());
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new JumpInsnNode(this.opcode, clone(this.label, labels));
  }
}