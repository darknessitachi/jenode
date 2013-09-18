package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public class ExpressionString
{
  Object[] mElements;

  public Object[] getElements()
  {
    return this.mElements;
  }
  public void setElements(Object[] pElements) { this.mElements = pElements; }


  public ExpressionString(Object[] pElements)
  {
    this.mElements = pElements;
  }

  public String evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < this.mElements.length; i++) {
      Object elem = this.mElements[i];
      if ((elem instanceof String)) {
        buf.append((String)elem);
      }
      else if ((elem instanceof Expression)) {
        Object val = 
          ((Expression)elem).evaluate(pResolver, 
          functions, 
          pLogger);
        if (val != null) {
          buf.append(val.toString());
        }
      }
    }
    return buf.toString();
  }

  public String getExpressionString()
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < this.mElements.length; i++) {
      Object elem = this.mElements[i];
      if ((elem instanceof String)) {
        buf.append((String)elem);
      }
      else if ((elem instanceof Expression)) {
        buf.append("${");
        buf.append(((Expression)elem).getExpressionString());
        buf.append("}");
      }
    }
    return buf.toString();
  }
}