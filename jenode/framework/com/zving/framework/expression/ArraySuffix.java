package com.zving.framework.expression;

import com.zving.framework.core.bean.BeanDescription;
import com.zving.framework.core.bean.BeanManager;
import com.zving.framework.core.bean.BeanProperty;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class ArraySuffix extends ValueSuffix
{
  Expression mIndex;

  public Expression getIndex()
  {
    return this.mIndex;
  }

  public void setIndex(Expression pIndex) {
    this.mIndex = pIndex;
  }

  public ArraySuffix(Expression pIndex)
  {
    this.mIndex = pIndex;
  }

  Object evaluateIndex(IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    return this.mIndex.evaluate(pResolver, functions, pLogger);
  }

  String getOperatorSymbol()
  {
    return "[]";
  }

  public String getExpressionString()
  {
    return "[" + this.mIndex.getExpressionString() + "]";
  }

  public Object evaluate(Object pValue, IVariableResolver pResolver, IFunctionMapper functions, Logger pLogger)
    throws ExpressionException
  {
    if (pValue == null) {
      if (pLogger.isLoggingWarning()) {
        pLogger.logWarning(Constants.CANT_GET_INDEXED_VALUE_OF_NULL, getOperatorSymbol());
      }
      return null;
    }
    Object indexVal;
    if ((indexVal = evaluateIndex(pResolver, functions, pLogger)) == null) {
      if (pLogger.isLoggingWarning()) {
        pLogger.logWarning(Constants.CANT_GET_NULL_INDEX, getOperatorSymbol());
      }
      return null;
    }

    if (((pValue instanceof List)) || (pValue.getClass().isArray()) || ((pValue instanceof DataTable))) {
      Integer indexObj = Coercions.coerceToInteger(indexVal, pLogger);
      if (indexObj == null) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.BAD_INDEX_VALUE, getOperatorSymbol(), indexVal.getClass().getName());
        }
        return null;
      }if ((pValue instanceof DataTable))
        return ((DataTable)pValue).getDataRow(indexObj.intValue());
      if ((pValue instanceof List))
        try {
          return ((List)pValue).get(indexObj.intValue());
        } catch (ArrayIndexOutOfBoundsException exc) {
          if (pLogger.isLoggingWarning()) {
            pLogger.logWarning(Constants.EXCEPTION_ACCESSING_LIST, exc, indexObj);
          }
          return null;
        } catch (IndexOutOfBoundsException exc) {
          if (pLogger.isLoggingWarning()) {
            pLogger.logWarning(Constants.EXCEPTION_ACCESSING_LIST, exc, indexObj);
          }
          return null;
        } catch (Exception exc) {
          if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.EXCEPTION_ACCESSING_LIST, exc, indexObj);
          }
          return null;
        }
      try
      {
        return Array.get(pValue, indexObj.intValue());
      } catch (ArrayIndexOutOfBoundsException exc) {
        if (pLogger.isLoggingWarning()) {
          pLogger.logWarning(Constants.EXCEPTION_ACCESSING_ARRAY, exc, indexObj);
        }
        return null;
      } catch (IndexOutOfBoundsException exc) {
        if (pLogger.isLoggingWarning()) {
          pLogger.logWarning(Constants.EXCEPTION_ACCESSING_ARRAY, exc, indexObj);
        }
        return null;
      } catch (Exception exc) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.EXCEPTION_ACCESSING_ARRAY, exc, indexObj);
        }
        return null;
      }

    }

    if ((pValue instanceof Map)) {
      Map val = (Map)pValue;
      Object v = val.get(indexVal);
      if (v != null)
        return v;
    }
    String indexStr;
    if ((indexStr = Coercions.coerceToString(indexVal, pLogger)) == null)
      return null;
    if ((pValue instanceof DataRow)) {
      DataRow val = (DataRow)pValue;
      return val.get(indexStr);
    }
    BeanProperty property;
    if ((property = BeanManager.getBeanDescription(pValue.getClass()).getProperty(indexStr)) != null) {
      try {
        return property.read(pValue);
      } catch (Exception exc) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.ERROR_GETTING_PROPERTY, exc, indexStr, pValue.getClass().getName());
        }
        return null;
      }

    }

    return null;
  }
}