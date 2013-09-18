package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.expression.operator.BinaryOperator;
import java.util.List;

public class BinaryOperatorExpression extends Expression
{
  Expression mExpression;
  List<Expression> mExpressions;
  List<BinaryOperator> mOperators;

  public Expression getExpression()
  {
    return this.mExpression;
  }

  public void setExpression(Expression pExpression) {
    this.mExpression = pExpression;
  }

  public List<BinaryOperator> getOperators()
  {
    return this.mOperators;
  }

  public void setOperators(List<BinaryOperator> pOperators) {
    this.mOperators = pOperators;
  }

  public List<Expression> getExpressions() {
    return this.mExpressions;
  }

  public void setExpressions(List<Expression> pExpressions) {
    this.mExpressions = pExpressions;
  }

  public BinaryOperatorExpression(Expression pExpression, List<BinaryOperator> pOperators, List<Expression> pExpressions)
  {
    this.mExpression = pExpression;
    this.mOperators = pOperators;
    this.mExpressions = pExpressions;
  }

  public String getExpressionString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("(");
    buf.append(this.mExpression.getExpressionString());
    for (int i = 0; i < this.mOperators.size(); i++) {
      BinaryOperator operator = (BinaryOperator)this.mOperators.get(i);
      Expression expression = (Expression)this.mExpressions.get(i);
      buf.append(" ");
      buf.append(operator.getOperatorSymbol());
      buf.append(" ");
      buf.append(expression.getExpressionString());
    }
    buf.append(")");

    return buf.toString();
  }

  public Object evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    Object value = this.mExpression.evaluate(pResolver, functions, pLogger);
    for (int i = 0; i < this.mOperators.size(); i++) {
      BinaryOperator operator = (BinaryOperator)this.mOperators.get(i);

      if (operator.shouldCoerceToBoolean()) {
        value = Coercions.coerceToBoolean(value, pLogger);
      }

      if (operator.shouldEvaluate(value)) {
        Expression expression = (Expression)this.mExpressions.get(i);
        Object nextValue = expression.evaluate(pResolver, functions, pLogger);

        value = operator.apply(value, nextValue, pLogger);
      }
    }
    return value;
  }
}