package com.zving.framework.core.asm.objectweb;

public abstract class FieldVisitor
{
  protected final int api;
  protected FieldVisitor fv;

  public FieldVisitor(int api)
  {
    this(api, null);
  }

  public FieldVisitor(int api, FieldVisitor fv)
  {
    if (api != 262144) {
      throw new IllegalArgumentException();
    }
    this.api = api;
    this.fv = fv;
  }

  public AnnotationVisitor visitAnnotation(String desc, boolean visible)
  {
    if (this.fv != null) {
      return this.fv.visitAnnotation(desc, visible);
    }
    return null;
  }

  public void visitAttribute(Attribute attr)
  {
    if (this.fv != null)
      this.fv.visitAttribute(attr);
  }

  public void visitEnd()
  {
    if (this.fv != null)
      this.fv.visitEnd();
  }
}