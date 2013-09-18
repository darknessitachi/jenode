package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public class NamedValue extends Expression
{
  String mName;

  public String getName()
  {
    return this.mName;
  }

  public NamedValue(String pName)
  {
    this.mName = pName;
  }

  public String getExpressionString()
  {
    return StringLiteral.toIdentifierToken(this.mName);
  }

  public Object evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    if (pResolver == null) {
      return null;
    }
    return pResolver.resolveVariable(this.mName);
  }
}