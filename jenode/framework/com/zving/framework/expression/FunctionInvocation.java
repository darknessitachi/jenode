package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunction;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import java.util.Iterator;
import java.util.List;

public class FunctionInvocation extends Expression
{
  private String functionName;
  private List<Expression> argumentList;

  public String getFunctionName()
  {
    return this.functionName;
  }

  public void setFunctionName(String f) {
    this.functionName = f;
  }

  public List<Expression> getArgumentList() {
    return this.argumentList;
  }

  public void setArgumentList(List<Expression> l) {
    this.argumentList = l;
  }

  public FunctionInvocation(String functionName, List<Expression> argumentList) {
    this.functionName = functionName;
    this.argumentList = argumentList;
  }

  public String getExpressionString()
  {
    StringBuffer b = new StringBuffer();
    b.append(this.functionName);
    b.append("(");
    Iterator i = this.argumentList.iterator();
    while (i.hasNext()) {
      b.append(((Expression)i.next()).getExpressionString());
      if (i.hasNext())
        b.append(", ");
    }
    b.append(")");
    return b.toString();
  }

  public Object evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    if (functions == null) {
      pLogger.logError(Constants.UNKNOWN_FUNCTION, this.functionName);
    }

    String prefix = null;
    String localName = null;
    int index = this.functionName.indexOf(':');
    if (index == -1) {
      prefix = "";
      localName = this.functionName;
    } else {
      prefix = this.functionName.substring(0, index);
      localName = this.functionName.substring(index + 1);
    }

    IFunction target = functions.resolveFunction(prefix, localName);
    if (target == null) {
      pLogger.logError(Constants.UNKNOWN_FUNCTION, this.functionName);
    }
    Class[] params = target.getArgumentTypes();
    Object[] arguments = new Object[this.argumentList.size()];
    for (int i = 0; (i < params.length) && (i < arguments.length); i++) {
      arguments[i] = ((Expression)this.argumentList.get(i)).evaluate(pResolver, functions, pLogger);
      arguments[i] = Coercions.coerce(arguments[i], params[i], pLogger);
    }

    return target.execute(pResolver, arguments);
  }
}