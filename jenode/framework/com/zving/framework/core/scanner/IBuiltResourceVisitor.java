package com.zving.framework.core.scanner;

import com.zving.framework.core.asm.objectweb.tree.ClassNode;
import com.zving.framework.extend.IExtendItem;

public abstract interface IBuiltResourceVisitor extends IExtendItem
{
  public abstract boolean match(BuiltResource paramBuiltResource);

  public abstract void visitClass(BuiltResource paramBuiltResource, ClassNode paramClassNode);

  public abstract void visitInnerClass(BuiltResource paramBuiltResource, ClassNode paramClassNode1, ClassNode paramClassNode2);

  public abstract void visitResource(BuiltResource paramBuiltResource);
}