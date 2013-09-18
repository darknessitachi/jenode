package com.zving.framework.core.asm.objectweb;

class Context
{
  Attribute[] attrs;
  int flags;
  char[] buffer;
  int[] bootstrapMethods;
  int access;
  String name;
  String desc;
  int offset;
  int mode;
  int localCount;
  int localDiff;
  Object[] local;
  int stackCount;
  Object[] stack;
}