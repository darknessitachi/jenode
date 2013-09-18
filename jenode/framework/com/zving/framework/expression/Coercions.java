package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.operator.ArithmeticOperator;
import com.zving.framework.expression.operator.EqualityOperator;
import com.zving.framework.expression.operator.RelationalOperator;
import com.zving.framework.utility.Operators;
import com.zving.framework.utility.Primitives;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Coercions
{
  private static final Number ZERO = new Integer(0);

  public static Object coerce(Object pValue, Class<?> pClass, Logger pLogger)
    throws ExpressionException
  {
    if (pClass == String.class)
      return coerceToString(pValue, pLogger);
    if (isNumberClass(pClass))
      return coerceToPrimitiveNumber(pValue, pClass, pLogger);
    if ((pClass == Character.class) || (pClass == Character.TYPE))
      return coerceToCharacter(pValue, pLogger);
    if ((pClass == Boolean.class) || (pClass == Boolean.TYPE)) {
      return coerceToBoolean(pValue, pLogger);
    }
    return coerceToObject(pValue, pClass, pLogger);
  }

  static boolean isNumberClass(Class<?> pClass)
  {
    return (pClass == Byte.class) || (pClass == Byte.TYPE) || (pClass == Short.class) || (pClass == Short.TYPE) || (pClass == Integer.class) || 
      (pClass == Integer.TYPE) || (pClass == Long.class) || (pClass == Long.TYPE) || (pClass == Float.class) || (pClass == Float.TYPE) || 
      (pClass == Double.class) || (pClass == Double.TYPE) || (pClass == BigInteger.class) || (pClass == BigDecimal.class);
  }

  public static String coerceToString(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    if (pValue == null)
      return "";
    if ((pValue instanceof String))
      return (String)pValue;
    try
    {
      return pValue.toString();
    } catch (Exception exc) {
      if (pLogger.isLoggingError())
        pLogger.logError(Constants.TOSTRING_EXCEPTION, exc, pValue.getClass().getName());
    }
    return "";
  }

  public static Number coerceToPrimitiveNumber(Object pValue, Class<?> pClass, Logger pLogger)
    throws ExpressionException
  {
    if ((pValue == null) || ("".equals(pValue)))
      return coerceToPrimitiveNumber(ZERO, pClass);
    if ((pValue instanceof Character)) {
      char val = ((Character)pValue).charValue();
      return coerceToPrimitiveNumber(new Short((short)val), pClass);
    }if ((pValue instanceof Boolean)) {
      if (pLogger.isLoggingError()) {
        pLogger.logError(Constants.BOOLEAN_TO_NUMBER, pValue, pClass.getName());
      }
      return coerceToPrimitiveNumber(ZERO, pClass);
    }if (pValue.getClass() == pClass)
      return (Number)pValue;
    if ((pValue instanceof Number))
      return coerceToPrimitiveNumber((Number)pValue, pClass);
    if ((pValue instanceof String)) {
      try {
        return coerceToPrimitiveNumber((String)pValue, pClass);
      } catch (Exception exc) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.STRING_TO_NUMBER_EXCEPTION, (String)pValue, pClass.getName());
        }
        return coerceToPrimitiveNumber(ZERO, pClass);
      }
    }
    if (pLogger.isLoggingError()) {
      pLogger.logError(Constants.COERCE_TO_NUMBER, pValue.getClass().getName(), pClass.getName());
    }
    return coerceToPrimitiveNumber(0L, pClass);
  }

  public static Integer coerceToInteger(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    if (pValue == null)
      return null;
    if ((pValue instanceof Character))
      return PrimitiveObjects.getInteger(((Character)pValue).charValue());
    if ((pValue instanceof Boolean)) {
      if (pLogger.isLoggingWarning()) {
        pLogger.logWarning(Constants.BOOLEAN_TO_NUMBER, pValue, Integer.class.getName());
      }
      return PrimitiveObjects.getInteger(((Boolean)pValue).booleanValue() ? 1 : 0);
    }if ((pValue instanceof Integer))
      return (Integer)pValue;
    if ((pValue instanceof Number))
      return PrimitiveObjects.getInteger(((Number)pValue).intValue());
    if ((pValue instanceof String)) {
      try {
        return Integer.valueOf((String)pValue);
      } catch (Exception exc) {
        if (pLogger.isLoggingWarning()) {
          pLogger.logWarning(Constants.STRING_TO_NUMBER_EXCEPTION, (String)pValue, Integer.class.getName());
        }
        return null;
      }
    }
    if (pLogger.isLoggingWarning()) {
      pLogger.logWarning(Constants.COERCE_TO_NUMBER, pValue.getClass().getName(), Integer.class.getName());
    }
    return null;
  }

  static Number coerceToPrimitiveNumber(long pValue, Class<?> pClass)
    throws ExpressionException
  {
    if ((pClass == Byte.class) || (pClass == Byte.TYPE))
      return PrimitiveObjects.getByte((byte)(int)pValue);
    if ((pClass == Short.class) || (pClass == Short.TYPE))
      return PrimitiveObjects.getShort((short)(int)pValue);
    if ((pClass == Integer.class) || (pClass == Integer.TYPE))
      return PrimitiveObjects.getInteger((int)pValue);
    if ((pClass == Long.class) || (pClass == Long.TYPE))
      return PrimitiveObjects.getLong(pValue);
    if ((pClass == Float.class) || (pClass == Float.TYPE))
      return PrimitiveObjects.getFloat((float)pValue);
    if ((pClass == Double.class) || (pClass == Double.TYPE)) {
      return PrimitiveObjects.getDouble(pValue);
    }
    return PrimitiveObjects.getInteger(0);
  }

  static Number coerceToPrimitiveNumber(double pValue, Class<?> pClass)
    throws ExpressionException
  {
    if ((pClass == Byte.class) || (pClass == Byte.TYPE))
      return PrimitiveObjects.getByte((byte)(int)pValue);
    if ((pClass == Short.class) || (pClass == Short.TYPE))
      return PrimitiveObjects.getShort((short)(int)pValue);
    if ((pClass == Integer.class) || (pClass == Integer.TYPE))
      return PrimitiveObjects.getInteger((int)pValue);
    if ((pClass == Long.class) || (pClass == Long.TYPE))
      return PrimitiveObjects.getLong(()pValue);
    if ((pClass == Float.class) || (pClass == Float.TYPE))
      return PrimitiveObjects.getFloat((float)pValue);
    if ((pClass == Double.class) || (pClass == Double.TYPE)) {
      return PrimitiveObjects.getDouble(pValue);
    }
    return PrimitiveObjects.getInteger(0);
  }

  static Number coerceToPrimitiveNumber(Number pValue, Class<?> pClass)
    throws ExpressionException
  {
    if ((pClass == Byte.class) || (pClass == Byte.TYPE))
      return PrimitiveObjects.getByte(pValue.byteValue());
    if ((pClass == Short.class) || (pClass == Short.TYPE))
      return PrimitiveObjects.getShort(pValue.shortValue());
    if ((pClass == Integer.class) || (pClass == Integer.TYPE))
      return PrimitiveObjects.getInteger(pValue.intValue());
    if ((pClass == Long.class) || (pClass == Long.TYPE))
      return PrimitiveObjects.getLong(pValue.longValue());
    if ((pClass == Float.class) || (pClass == Float.TYPE))
      return PrimitiveObjects.getFloat(pValue.floatValue());
    if ((pClass == Double.class) || (pClass == Double.TYPE))
      return PrimitiveObjects.getDouble(pValue.doubleValue());
    if (pClass == BigInteger.class) {
      if ((pValue instanceof BigDecimal)) {
        return ((BigDecimal)pValue).toBigInteger();
      }
      return BigInteger.valueOf(pValue.longValue());
    }if (pClass == BigDecimal.class) {
      if ((pValue instanceof BigInteger)) {
        return new BigDecimal((BigInteger)pValue);
      }
      return new BigDecimal(pValue.doubleValue());
    }
    return PrimitiveObjects.getInteger(0);
  }

  static Number coerceToPrimitiveNumber(String pValue, Class<?> pClass)
    throws ExpressionException
  {
    if ((pClass == Byte.class) || (pClass == Byte.TYPE))
      return Byte.valueOf(pValue);
    if ((pClass == Short.class) || (pClass == Short.TYPE))
      return Short.valueOf(pValue);
    if ((pClass == Integer.class) || (pClass == Integer.TYPE))
      return Integer.valueOf(pValue);
    if ((pClass == Long.class) || (pClass == Long.TYPE))
      return Long.valueOf(pValue);
    if ((pClass == Float.class) || (pClass == Float.TYPE))
      return Float.valueOf(pValue);
    if ((pClass == Double.class) || (pClass == Double.TYPE))
      return Double.valueOf(pValue);
    if (pClass == BigInteger.class)
      return new BigInteger(pValue);
    if (pClass == BigDecimal.class) {
      return new BigDecimal(pValue);
    }
    return PrimitiveObjects.getInteger(0);
  }

  public static Character coerceToCharacter(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    if ((pValue == null) || ("".equals(pValue)))
      return PrimitiveObjects.getCharacter('\000');
    if ((pValue instanceof Character))
      return (Character)pValue;
    if ((pValue instanceof Boolean)) {
      if (pLogger.isLoggingError()) {
        pLogger.logError(Constants.BOOLEAN_TO_CHARACTER, pValue);
      }
      return PrimitiveObjects.getCharacter('\000');
    }if ((pValue instanceof Number))
      return PrimitiveObjects.getCharacter((char)((Number)pValue).shortValue());
    if ((pValue instanceof String)) {
      String str = (String)pValue;
      return PrimitiveObjects.getCharacter(str.charAt(0));
    }
    if (pLogger.isLoggingError()) {
      pLogger.logError(Constants.COERCE_TO_CHARACTER, pValue.getClass().getName());
    }
    return PrimitiveObjects.getCharacter('\000');
  }

  public static Boolean coerceToBoolean(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    return Boolean.valueOf(Primitives.getBoolean(pValue));
  }

  public static Object coerceToObject(Object pValue, Class<?> pClass, Logger pLogger)
    throws ExpressionException
  {
    if (pValue == null)
      return null;
    if (pClass.isAssignableFrom(pValue.getClass()))
      return pValue;
    if ((pValue instanceof String)) {
      String str = (String)pValue;
      PropertyEditor pe = PropertyEditorManager.findEditor(pClass);
      if (pe == null) {
        if ("".equals(str)) {
          return null;
        }
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.NO_PROPERTY_EDITOR, str, pClass.getName());
        }
        return null;
      }
      try
      {
        pe.setAsText(str);
        return pe.getValue();
      } catch (IllegalArgumentException exc) {
        if ("".equals(str)) {
          return null;
        }
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.PROPERTY_EDITOR_ERROR, exc, pValue, pClass.getName());
        }
        return null;
      }
    }

    if (pLogger.isLoggingError()) {
      pLogger.logError(Constants.COERCE_TO_OBJECT, pValue.getClass().getName(), pClass.getName());
    }
    return null;
  }

  public static Object applyArithmeticOperator(Object pLeft, Object pRight, ArithmeticOperator pOperator, Logger pLogger)
    throws ExpressionException
  {
    if ((pLeft == null) && (pRight == null)) {
      if (pLogger.isLoggingWarning()) {
        pLogger.logWarning(Constants.ARITH_OP_NULL, pOperator.getOperatorSymbol());
      }
      return PrimitiveObjects.getInteger(0);
    }

    if ((isBigDecimal(pLeft)) || (isBigDecimal(pRight))) {
      BigDecimal left = (BigDecimal)coerceToPrimitiveNumber(pLeft, BigDecimal.class, pLogger);
      BigDecimal right = (BigDecimal)coerceToPrimitiveNumber(pRight, BigDecimal.class, pLogger);
      return pOperator.apply(left, right);
    }

    if ((isFloatingPointType(pLeft)) || (isFloatingPointType(pRight)) || (isFloatingPointString(pLeft)) || (isFloatingPointString(pRight))) {
      if ((isBigInteger(pLeft)) || (isBigInteger(pRight))) {
        BigDecimal left = (BigDecimal)coerceToPrimitiveNumber(pLeft, BigDecimal.class, pLogger);
        BigDecimal right = (BigDecimal)coerceToPrimitiveNumber(pRight, BigDecimal.class, pLogger);
        return pOperator.apply(left, right);
      }
      double left = coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
      double right = coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
      return PrimitiveObjects.getDouble(pOperator.apply(left, right));
    }

    if ((isBigInteger(pLeft)) || (isBigInteger(pRight))) {
      BigInteger left = (BigInteger)coerceToPrimitiveNumber(pLeft, BigInteger.class, pLogger);
      BigInteger right = (BigInteger)coerceToPrimitiveNumber(pRight, BigInteger.class, pLogger);
      return pOperator.apply(left, right);
    }

    long left = coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
    long right = coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
    return PrimitiveObjects.getLong(pOperator.apply(left, right));
  }

  public static Object applyRelationalOperator(Object pLeft, Object pRight, RelationalOperator pOperator, Logger pLogger)
    throws ExpressionException
  {
    if ((isBigDecimal(pLeft)) || (isBigDecimal(pRight))) {
      BigDecimal left = (BigDecimal)coerceToPrimitiveNumber(pLeft, BigDecimal.class, pLogger);
      BigDecimal right = (BigDecimal)coerceToPrimitiveNumber(pRight, BigDecimal.class, pLogger);
      return PrimitiveObjects.getBoolean(pOperator.apply(left, right));
    }

    if ((isFloatingPointType(pLeft)) || (isFloatingPointType(pRight))) {
      double left = coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
      double right = coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
      return PrimitiveObjects.getBoolean(pOperator.apply(left, right));
    }

    if ((isBigInteger(pLeft)) || (isBigInteger(pRight))) {
      BigInteger left = (BigInteger)coerceToPrimitiveNumber(pLeft, BigInteger.class, pLogger);
      BigInteger right = (BigInteger)coerceToPrimitiveNumber(pRight, BigInteger.class, pLogger);
      return PrimitiveObjects.getBoolean(pOperator.apply(left, right));
    }

    if ((isIntegerType(pLeft)) || (isIntegerType(pRight))) {
      long left = coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
      long right = coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
      return PrimitiveObjects.getBoolean(pOperator.apply(left, right));
    }

    if (((pLeft instanceof String)) || ((pRight instanceof String))) {
      String left = coerceToString(pLeft, pLogger);
      String right = coerceToString(pRight, pLogger);
      return PrimitiveObjects.getBoolean(pOperator.apply(left, right));
    }

    if ((pLeft instanceof Comparable)) {
      try
      {
        int result = ((Comparable)pLeft).compareTo(pRight);
        return PrimitiveObjects.getBoolean(pOperator.apply(result, -result));
      } catch (Exception exc) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.COMPARABLE_ERROR, exc, pLeft.getClass().getName(), pRight == null ? "null" : pRight
            .getClass().getName(), pOperator.getOperatorSymbol());
        }
        return Boolean.FALSE;
      }
    }

    if ((pRight instanceof Comparable)) {
      try
      {
        int result = ((Comparable)pRight).compareTo(pLeft);
        return PrimitiveObjects.getBoolean(pOperator.apply(-result, result));
      } catch (Exception exc) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(Constants.COMPARABLE_ERROR, exc, pRight.getClass().getName(), pLeft == null ? "null" : pLeft
            .getClass().getName(), pOperator.getOperatorSymbol());
        }
        return Boolean.FALSE;
      }

    }

    if (pLogger.isLoggingError()) {
      pLogger.logError(Constants.ARITH_OP_BAD_TYPE, pOperator.getOperatorSymbol(), pLeft.getClass().getName(), pRight.getClass()
        .getName());
    }
    return Boolean.FALSE;
  }

  public static Object applyEqualityOperator(Object pLeft, Object pRight, EqualityOperator pOperator, Logger pLogger)
    throws ExpressionException
  {
    return PrimitiveObjects.getBoolean(pOperator.apply(Primitives.getBoolean(Operators.eq(pLeft, pRight)), pLogger));
  }

  public static boolean isFloatingPointType(Object pObject)
  {
    return (pObject != null) && (isFloatingPointType(pObject.getClass()));
  }

  public static boolean isFloatingPointType(Class<?> pClass)
  {
    return (pClass == Float.class) || (pClass == Float.TYPE) || (pClass == Double.class) || (pClass == Double.TYPE);
  }

  public static boolean isFloatingPointString(Object pObject)
  {
    if ((pObject instanceof String)) {
      String str = (String)pObject;
      int len = str.length();
      for (int i = 0; i < len; i++) {
        char ch = str.charAt(i);
        if ((ch == '.') || (ch == 'e') || (ch == 'E')) {
          return true;
        }
      }
      return false;
    }
    return false;
  }

  public static boolean isIntegerType(Object pObject)
  {
    return (pObject != null) && (isIntegerType(pObject.getClass()));
  }

  public static boolean isIntegerType(Class<?> pClass)
  {
    return (pClass == Byte.class) || (pClass == Byte.TYPE) || (pClass == Short.class) || (pClass == Short.TYPE) || (pClass == Character.class) || 
      (pClass == Character.TYPE) || (pClass == Integer.class) || (pClass == Integer.TYPE) || (pClass == Long.class) || 
      (pClass == Long.TYPE);
  }

  public static boolean isBigInteger(Object pObject)
  {
    return (pObject != null) && ((pObject instanceof BigInteger));
  }

  public static boolean isBigDecimal(Object pObject)
  {
    return (pObject != null) && ((pObject instanceof BigDecimal));
  }
}