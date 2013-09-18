package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import java.util.List;

public class ComplexValue extends Expression
{
  Expression mPrefix;
  List<ValueSuffix> mSuffixes;

  public Expression getPrefix()
  {
    return this.mPrefix;
  }

  public void setPrefix(Expression pPrefix) {
    this.mPrefix = pPrefix;
  }

  public List<ValueSuffix> getSuffixes()
  {
    return this.mSuffixes;
  }

  public void setSuffixes(List<ValueSuffix> pSuffixes) {
    this.mSuffixes = pSuffixes;
  }

  public ComplexValue(Expression pPrefix, List<ValueSuffix> pSuffixes)
  {
    this.mPrefix = pPrefix;
    this.mSuffixes = pSuffixes;
  }

  public String getExpressionString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append(this.mPrefix.getExpressionString());

    for (int i = 0; (this.mSuffixes != null) && (i < this.mSuffixes.size()); i++) {
      ValueSuffix suffix = (ValueSuffix)this.mSuffixes.get(i);
      buf.append(suffix.getExpressionString());
    }

    return buf.toString();
  }

  public Object evaluate(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    Object ret = this.mPrefix.evaluate(pResolver, functions, pLogger);

    for (int i = 0; (this.mSuffixes != null) && (i < this.mSuffixes.size()); i++) {
      ValueSuffix suffix = (ValueSuffix)this.mSuffixes.get(i);
      ret = suffix.evaluate(ret, pResolver, functions, pLogger);
    }

    return ret;
  }
}