package com.zving.framework.core.asm.objectweb;

class Edge
{
  static final int NORMAL = 0;
  static final int EXCEPTION = 2147483647;
  int info;
  Label successor;
  Edge next;
}