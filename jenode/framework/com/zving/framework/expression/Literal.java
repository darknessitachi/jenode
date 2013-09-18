package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public abstract class Literal extends Expression
{
  Object mValue;

  public Object getValue()
  {
    return this.mValue;
  }
  public void setValue(Object pValue) { this.mValue = pValue; }


  public Literal(Object pValue)
  {
    this.mValue = pValue;
  }

  public Object evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    return this.mValue;
  }
}