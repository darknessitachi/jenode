package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class TypeInsnNode extends AbstractInsnNode
{
  public String desc;

  public TypeInsnNode(int opcode, String desc)
  {
    super(opcode);
    this.desc = desc;
  }

  public void setOpcode(int opcode)
  {
    this.opcode = opcode;
  }

  public int getType()
  {
    return 3;
  }

  public void accept(MethodVisitor mv)
  {
    mv.visitTypeInsn(this.opcode, this.desc);
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return new TypeInsnNode(this.opcode, this.desc);
  }
}