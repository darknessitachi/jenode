package com.zving.framework.expression.impl;

import com.zving.framework.collection.Mapx;
import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Constants;
import com.zving.framework.expression.Expression;
import com.zving.framework.expression.ExpressionString;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.core.IExpression;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.expression.parser.ELParseException;
import com.zving.framework.expression.parser.ELParser;
import com.zving.framework.expression.parser.Token;
import com.zving.framework.expression.parser.TokenMgrError;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Map;

public class CachedEvaluator implements IEvaluator {
	static Mapx<String, Object> cachedExpressionStrings = new Mapx(50000);

	static Mapx<Class<?>, Mapx<String, Object>> cachedExpectedTypes = new Mapx();

	static Logger sLogger = new Logger(System.out);
	boolean mBypassCache;

	public CachedEvaluator() {
	}

	public CachedEvaluator(boolean pBypassCache) {
		this.mBypassCache = pBypassCache;
	}

	public Object evaluate(String pExpressionString, Class<?> pExpectedType,
			IVariableResolver pResolver, IFunctionMapper functions)
			throws ExpressionException {
		return evaluate(pExpressionString, pExpectedType, pResolver, functions,
				sLogger);
	}

	public IExpression parseExpression(String expression,
			Class<?> expectedType, IFunctionMapper fMapper)
			throws ExpressionException {
		parseExpressionString(expression);

		return new JSTLExpression(this, expression, expectedType, fMapper);
	}

	Object evaluate(String pExpressionString, Class<?> pExpectedType,
			IVariableResolver pResolver, IFunctionMapper functions,
			Logger pLogger) throws ExpressionException {
		if (pExpressionString == null) {
			throw new ExpressionException(Constants.NULL_EXPRESSION_STRING);
		}

		Object parsedValue = parseExpressionString(pExpressionString);

		if ((parsedValue instanceof String)) {
			String strValue = (String) parsedValue;
			return convertStaticValueToExpectedType(strValue, pExpectedType,
					pLogger);
		}

		if ((parsedValue instanceof Expression)) {
			Object value = ((Expression) parsedValue).evaluate(pResolver,
					functions, pLogger);
			return convertToExpectedType(value, pExpectedType, pLogger);
		}

		if ((parsedValue instanceof ExpressionString)) {
			String strValue = ((ExpressionString) parsedValue).evaluate(
					pResolver, functions, pLogger);
			return convertToExpectedType(strValue, pExpectedType, pLogger);
		}

		return null;
	}

	public Object parseExpressionString(String pExpressionString)
			throws ExpressionException {
		if (pExpressionString.length() == 0) {
			return "";
		}

		Object ret = this.mBypassCache ? null : cachedExpressionStrings
				.get(pExpressionString);

		if (ret == null) {
			Reader r = new StringReader(pExpressionString);
			ELParser parser = new ELParser(r);
			try {
				ret = parser.ExpressionString();
				cachedExpressionStrings.put(pExpressionString, ret);
			} catch (ELParseException exc) {
				throw new ExpressionException(formatParseException(
						pExpressionString, exc));
			} catch (TokenMgrError exc) {
				throw new ExpressionException(exc.getMessage());
			}
		}
		return ret;
	}

	Object convertToExpectedType(Object pValue, Class<?> pExpectedType,
			Logger pLogger) throws ExpressionException {
		return Coercions.coerce(pValue, pExpectedType, pLogger);
	}

	Object convertStaticValueToExpectedType(String pValue,
			Class<?> pExpectedType, Logger pLogger) throws ExpressionException {
		if ((pExpectedType == String.class) || (pExpectedType == Object.class)) {
			return pValue;
		}

		Map valueByString = getOrCreateExpectedTypeMap(pExpectedType);
		if ((!this.mBypassCache) && (valueByString.containsKey(pValue))) {
			return valueByString.get(pValue);
		}

		Object ret = Coercions.coerce(pValue, pExpectedType, pLogger);
		valueByString.put(pValue, ret);
		return ret;
	}

	static Map<String, Object> getOrCreateExpectedTypeMap(Class<?> pExpectedType) {
		Mapx ret = (Mapx) cachedExpectedTypes.get(pExpectedType);
		if (ret == null) {
			ret = new Mapx();
			cachedExpectedTypes.put(pExpectedType, ret);
		}
		return ret;
	}

	static String formatParseException(String pExpressionString,
			ELParseException pExc) {
		StringBuffer expectedBuf = new StringBuffer();
		int maxSize = 0;
		boolean printedOne = false;

		if (pExc.expectedTokenSequences == null) {
			return pExc.toString();
		}
		for (int i = 0; i < pExc.expectedTokenSequences.length; i++) {
			if (maxSize < pExc.expectedTokenSequences[i].length) {
				maxSize = pExc.expectedTokenSequences[i].length;
			}
			for (int j = 0; j < pExc.expectedTokenSequences[i].length; j++) {
				if (printedOne) {
					expectedBuf.append(", ");
				}
				expectedBuf
						.append(pExc.tokenImage[pExc.expectedTokenSequences[i][j]]);
				printedOne = true;
			}
		}
		String expected = expectedBuf.toString();

		StringBuffer encounteredBuf = new StringBuffer();
		Token tok = pExc.currentToken.next;
		for (int i = 0; i < maxSize; i++) {
			if (i != 0)
				encounteredBuf.append(" ");
			if (tok.kind == 0) {
				encounteredBuf.append(pExc.tokenImage[0]);
				break;
			}
			encounteredBuf.append(addEscapes(tok.image));
			tok = tok.next;
		}
		String encountered = encounteredBuf.toString();

		return MessageFormat.format(Constants.PARSE_EXCEPTION, new Object[] {
				expected, encountered });
	}

	static String addEscapes(String str) {
		StringBuffer retval = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case '\000':
				break;
			case '\b':
				retval.append("\\b");
				break;
			case '\t':
				retval.append("\\t");
				break;
			case '\n':
				retval.append("\\n");
				break;
			case '\f':
				retval.append("\\f");
				break;
			case '\r':
				retval.append("\\r");
				break;
			case '\001':
			case '\002':
			case '\003':
			case '\004':
			case '\005':
			case '\006':
			case '\007':
			case '\013':
			default:
				char ch;
				if (((ch = str.charAt(i)) < ' ') || (ch > '~')) {
					String s = "0000" + Integer.toString(ch, 16);
					retval.append("\\u"
							+ s.substring(s.length() - 4, s.length()));
				} else {
					retval.append(ch);
				}
				break;
			}
		}
		return retval.toString();
	}

	public String parseAndRender(String pExpressionString)
			throws ExpressionException {
		Object val = parseExpressionString(pExpressionString);
		if ((val instanceof String))
			return (String) val;
		if ((val instanceof Expression))
			return "${" + ((Expression) val).getExpressionString() + "}";
		if ((val instanceof ExpressionString)) {
			return ((ExpressionString) val).getExpressionString();
		}
		return "";
	}

	private class JSTLExpression implements IExpression {
		private CachedEvaluator evaluator;
		private String expression;
		private Class<?> expectedType;
		private IFunctionMapper fMapper;

		public JSTLExpression(CachedEvaluator evaluator, String expression, Class<?> expectedType,
				IFunctionMapper fMapper) {
			this.evaluator = evaluator;
			this.expression = expression;
			this.expectedType = expectedType;
			this.fMapper = fMapper;
		}

		public Object evaluate(IVariableResolver vResolver)
				throws ExpressionException {
			return this.evaluator.evaluate(this.expression, this.expectedType,
					vResolver, this.fMapper);
		}
	}
}