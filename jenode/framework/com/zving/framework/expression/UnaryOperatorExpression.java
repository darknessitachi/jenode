package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.expression.operator.UnaryOperator;
import java.util.List;

public class UnaryOperatorExpression extends Expression
{
  List<UnaryOperator> mOperators;
  UnaryOperator mOperator;
  Expression mExpression;

  public UnaryOperator getOperator()
  {
    return this.mOperator;
  }

  public void setOperator(UnaryOperator pOperator) {
    this.mOperator = pOperator;
  }

  public List<UnaryOperator> getOperators() {
    return this.mOperators;
  }

  public void setOperators(List<UnaryOperator> pOperators) {
    this.mOperators = pOperators;
  }

  public Expression getExpression()
  {
    return this.mExpression;
  }

  public void setExpression(Expression pExpression) {
    this.mExpression = pExpression;
  }

  public UnaryOperatorExpression(UnaryOperator pOperator, List<UnaryOperator> pOperators, Expression pExpression) {
    this.mOperator = pOperator;
    this.mOperators = pOperators;
    this.mExpression = pExpression;
  }

  public String getExpressionString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("(");
    if (this.mOperator != null) {
      buf.append(this.mOperator.getOperatorSymbol());
      buf.append(" ");
    } else {
      for (int i = 0; i < this.mOperators.size(); i++) {
        UnaryOperator operator = (UnaryOperator)this.mOperators.get(i);
        buf.append(operator.getOperatorSymbol());
        buf.append(" ");
      }
    }
    buf.append(this.mExpression.getExpressionString());
    buf.append(")");
    return buf.toString();
  }

  public Object evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    Object value = this.mExpression.evaluate(pResolver, functions, pLogger);
    if (this.mOperator != null)
      value = this.mOperator.apply(value, pLogger);
    else {
      for (int i = this.mOperators.size() - 1; i >= 0; i--) {
        UnaryOperator operator = (UnaryOperator)this.mOperators.get(i);
        value = operator.apply(value, pLogger);
      }
    }
    return value;
  }
}