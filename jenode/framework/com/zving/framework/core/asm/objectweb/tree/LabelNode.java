package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.Label;
import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.Map;

public class LabelNode extends AbstractInsnNode
{
  private Label label;

  public LabelNode()
  {
    super(-1);
  }

  public LabelNode(Label label) {
    super(-1);
    this.label = label;
  }

  public int getType()
  {
    return 8;
  }

  public Label getLabel() {
    if (this.label == null) {
      this.label = new Label();
    }
    return this.label;
  }

  public void accept(MethodVisitor cv)
  {
    cv.visitLabel(getLabel());
  }

  public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
  {
    return (AbstractInsnNode)labels.get(this);
  }

  public void resetLabel() {
    this.label = null;
  }
}