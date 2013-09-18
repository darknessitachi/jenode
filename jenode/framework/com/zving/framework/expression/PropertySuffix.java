package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public class PropertySuffix extends ArraySuffix
{
  String mName;

  public String getName()
  {
    return this.mName;
  }
  public void setName(String pName) { this.mName = pName; }


  public PropertySuffix(String pName)
  {
    super(null);
    this.mName = pName;
  }

  Object evaluateIndex(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    return this.mName;
  }

  String getOperatorSymbol()
  {
    return ".";
  }

  public String getExpressionString()
  {
    return "." + StringLiteral.toIdentifierToken(this.mName);
  }
}