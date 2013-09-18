package com.zving.framework.core.asm;

import com.zving.framework.core.asm.objectweb.AnnotationVisitor;

public class AccessAnnotationVisitor extends AnnotationVisitor
{
  AbstractAccess access;

  public AccessAnnotationVisitor(AbstractAccess access)
  {
    super(262144);
    this.access = access;
  }

  public void visit(String name, Object value) {
    this.access.addAlias(name, value);
  }

  public void visitEnum(String name, String desc, String value) {
  }

  public AnnotationVisitor visitAnnotation(String name, String desc) {
    return this;
  }

  public AnnotationVisitor visitArray(String name) {
    return this;
  }
}