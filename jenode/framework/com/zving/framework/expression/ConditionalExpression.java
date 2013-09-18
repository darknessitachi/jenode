package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public class ConditionalExpression extends Expression
{
  Expression mCondition;
  Expression mTrueBranch;
  Expression mFalseBranch;

  public Expression getCondition()
  {
    return this.mCondition;
  }
  public void setCondition(Expression pCondition) { this.mCondition = pCondition; }


  public Expression getTrueBranch()
  {
    return this.mTrueBranch;
  }
  public void setTrueBranch(Expression pTrueBranch) { this.mTrueBranch = pTrueBranch; }


  public Expression getFalseBranch()
  {
    return this.mFalseBranch;
  }
  public void setFalseBranch(Expression pFalseBranch) { this.mFalseBranch = pFalseBranch; }


  public ConditionalExpression(Expression pCondition, Expression pTrueBranch, Expression pFalseBranch)
  {
    this.mCondition = pCondition;
    this.mTrueBranch = pTrueBranch;
    this.mFalseBranch = pFalseBranch;
  }

  public String getExpressionString()
  {
    return 
      "( " + this.mCondition.getExpressionString() + " ? " + 
      this.mTrueBranch.getExpressionString() + " : " + 
      this.mFalseBranch.getExpressionString() + " )";
  }

  public Object evaluate(IVariableResolver vr, IFunctionMapper f, Logger l)
    throws ExpressionException
  {
    boolean condition = 
      Coercions.coerceToBoolean(
      this.mCondition.evaluate(vr, f, l), l).booleanValue();

    if (condition) {
      return this.mTrueBranch.evaluate(vr, f, l);
    }
    return this.mFalseBranch.evaluate(vr, f, l);
  }
}