package com.zving.framework.expression.parser;

import com.zving.framework.expression.ArraySuffix;
import com.zving.framework.expression.BinaryOperatorExpression;
import com.zving.framework.expression.BooleanLiteral;
import com.zving.framework.expression.ComplexValue;
import com.zving.framework.expression.ConditionalExpression;
import com.zving.framework.expression.Expression;
import com.zving.framework.expression.ExpressionString;
import com.zving.framework.expression.FloatingPointLiteral;
import com.zving.framework.expression.FunctionInvocation;
import com.zving.framework.expression.IntegerLiteral;
import com.zving.framework.expression.Literal;
import com.zving.framework.expression.NamedValue;
import com.zving.framework.expression.NullLiteral;
import com.zving.framework.expression.PropertySuffix;
import com.zving.framework.expression.StringLiteral;
import com.zving.framework.expression.UnaryOperatorExpression;
import com.zving.framework.expression.ValueSuffix;
import com.zving.framework.expression.operator.AndOperator;
import com.zving.framework.expression.operator.BinaryOperator;
import com.zving.framework.expression.operator.DivideOperator;
import com.zving.framework.expression.operator.EmptyOperator;
import com.zving.framework.expression.operator.EqualsOperator;
import com.zving.framework.expression.operator.GreaterThanOperator;
import com.zving.framework.expression.operator.GreaterThanOrEqualsOperator;
import com.zving.framework.expression.operator.LessThanOperator;
import com.zving.framework.expression.operator.LessThanOrEqualsOperator;
import com.zving.framework.expression.operator.MinusOperator;
import com.zving.framework.expression.operator.ModulusOperator;
import com.zving.framework.expression.operator.MultiplyOperator;
import com.zving.framework.expression.operator.NotEqualsOperator;
import com.zving.framework.expression.operator.NotOperator;
import com.zving.framework.expression.operator.OrOperator;
import com.zving.framework.expression.operator.PlusOperator;
import com.zving.framework.expression.operator.UnaryMinusOperator;
import com.zving.framework.expression.operator.UnaryOperator;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ELParser implements ELParserConstants {
	public ELParserTokenManager token_source;
	SimpleCharStream jj_input_stream;
	public Token token;
	public Token jj_nt;
	private int jj_ntk;
	private Token jj_scanpos;
	private Token jj_lastpos;
	private int jj_la;
	private int jj_gen;
	private final int[] jj_la1 = new int[35];
	private static int[] jj_la1_0;
	private static int[] jj_la1_1;
	private final JJCalls[] jj_2_rtns = new JJCalls[3];
	private boolean jj_rescan = false;
	private int jj_gc = 0;

	private final LookaheadSuccess jj_ls = new LookaheadSuccess();

	private List<int[]> jj_expentries = new ArrayList();
	private int[] jj_expentry;
	private int jj_kind = -1;
	private int[] jj_lasttokens = new int[100];
	private int jj_endpos;

	static {
		jj_la1_init_0();
		jj_la1_init_1();
	}

	public static void main(String[] args) throws ELParseException {
		ELParser parser = new ELParser(System.in);
		parser.ExpressionString();
	}

	public final Object ExpressionString() throws ELParseException {
		Object ret = "";
		List elems = null;

		switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
		case 1:
			ret = AttrValueString();
			break;
		case 2:
			ret = AttrValueExpression();
			break;
		default:
			this.jj_la1[0] = this.jj_gen;
			jj_consume_token(-1);
			throw new ELParseException();
		}
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 1:
			case 2:
				break;
			default:
				this.jj_la1[1] = this.jj_gen;
				break;
			}
			Object elem;
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 1:
				elem = AttrValueString();
				break;
			case 2:
				elem = AttrValueExpression();
				break;
			default:
				this.jj_la1[2] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			if (elems == null) {
				elems = new ArrayList();
				elems.add(ret);
			}
			elems.add(elem);
		}
//		return null;
//		if (elems != null) {
//			ret = new ExpressionString(elems.toArray());
//		}
//		return ret;
	}

	public final String AttrValueString() throws ELParseException {
		Token t = jj_consume_token(1);
		return t.image;
	}

	public final Expression AttrValueExpression() throws ELParseException {
		jj_consume_token(2);
		Expression exp = Expression();
		jj_consume_token(15);
		return exp;
	}

	public final Expression Expression() throws ELParseException {
		Expression ret;
		if (jj_2_1(2147483647)) {
			ret = ConditionalExpression();
		} else {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 7:
			case 8:
			case 10:
			case 12:
			case 13:
			case 14:
			case 29:
			case 36:
			case 42:
			case 43:
			case 48:
			case 50:
				ret = OrExpression();
				break;
			default:
				this.jj_la1[3] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
		}
		return ret;
	}

	public final Expression OrExpression() throws ELParseException {
		List operators = null;
		List expressions = null;
		Expression startExpression = AndExpression();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 46:
			case 47:
				break;
			default:
				this.jj_la1[4] = this.jj_gen;
				break;
			}
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 46:
				jj_consume_token(46);
				break;
			case 47:
				jj_consume_token(47);
				break;
			default:
				this.jj_la1[5] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			BinaryOperator operator = OrOperator.SINGLETON;
			Expression expression = AndExpression();
			if (operators == null) {
				operators = new ArrayList();
				expressions = new ArrayList();
			}
			operators.add(operator);
			expressions.add(expression);
		}
//		if (operators != null) {
//			return new BinaryOperatorExpression(startExpression, operators,
//					expressions);
//		}
//		return startExpression;
	}

	public final Expression AndExpression() throws ELParseException {
		List operators = null;
		List expressions = null;
		Expression startExpression = EqualityExpression();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 44:
			case 45:
				break;
			default:
				this.jj_la1[6] = this.jj_gen;
				break;
			}
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 44:
				jj_consume_token(44);
				break;
			case 45:
				jj_consume_token(45);
				break;
			default:
				this.jj_la1[7] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			BinaryOperator operator = AndOperator.SINGLETON;
			Expression expression = EqualityExpression();
			if (operators == null) {
				operators = new ArrayList();
				expressions = new ArrayList();
			}
			operators.add(operator);
			expressions.add(expression);
		}
//		if (operators != null) {
//			return new BinaryOperatorExpression(startExpression, operators,
//					expressions);
//		}
//		return startExpression;
	}

	public final Expression EqualityExpression() throws ELParseException {
		List operators = null;
		List expressions = null;
		Expression startExpression = RelationalExpression();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 21:
			case 22:
			case 27:
			case 28:
				break;
			case 23:
			case 24:
			case 25:
			case 26:
			default:
				this.jj_la1[8] = this.jj_gen;
				break;
			}
			BinaryOperator operator;
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 21:
			case 22:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 21:
					jj_consume_token(21);
					break;
				case 22:
					jj_consume_token(22);
					break;
				default:
					this.jj_la1[9] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = EqualsOperator.SINGLETON;
				break;
			case 27:
			case 28:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 27:
					jj_consume_token(27);
					break;
				case 28:
					jj_consume_token(28);
					break;
				default:
					this.jj_la1[10] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = NotEqualsOperator.SINGLETON;
				break;
			case 23:
			case 24:
			case 25:
			case 26:
			default:
				this.jj_la1[11] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			Expression expression = RelationalExpression();
			if (operators == null) {
				operators = new ArrayList();
				expressions = new ArrayList();
			}
			operators.add(operator);
			expressions.add(expression);
		}
//		if (operators != null) {
//			return new BinaryOperatorExpression(startExpression, operators,
//					expressions);
//		}
//		return startExpression;
	}

	public final Expression RelationalExpression() throws ELParseException {
		List operators = null;
		List expressions = null;
		Expression startExpression = AddExpression();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 17:
			case 18:
			case 19:
			case 20:
			case 23:
			case 24:
			case 25:
			case 26:
				break;
			case 21:
			case 22:
			default:
				this.jj_la1[12] = this.jj_gen;
				break;
			}
			BinaryOperator operator;
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 19:
			case 20:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 19:
					jj_consume_token(19);
					break;
				case 20:
					jj_consume_token(20);
					break;
				default:
					this.jj_la1[13] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = LessThanOperator.SINGLETON;
				break;
			case 17:
			case 18:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 17:
					jj_consume_token(17);
					break;
				case 18:
					jj_consume_token(18);
					break;
				default:
					this.jj_la1[14] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = GreaterThanOperator.SINGLETON;
				break;
			case 25:
			case 26:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 25:
					jj_consume_token(25);
					break;
				case 26:
					jj_consume_token(26);
					break;
				default:
					this.jj_la1[15] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = GreaterThanOrEqualsOperator.SINGLETON;
				break;
			case 23:
			case 24:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 23:
					jj_consume_token(23);
					break;
				case 24:
					jj_consume_token(24);
					break;
				default:
					this.jj_la1[16] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = LessThanOrEqualsOperator.SINGLETON;
				break;
			case 21:
			case 22:
			default:
				this.jj_la1[17] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			Expression expression = AddExpression();
			if (operators == null) {
				operators = new ArrayList();
				expressions = new ArrayList();
			}
			operators.add(operator);
			expressions.add(expression);
		}
//		if (operators != null) {
//			return new BinaryOperatorExpression(startExpression, operators,
//					expressions);
//		}
//		return startExpression;
	}

	public final Expression AddExpression() throws ELParseException {
		List operators = null;
		List expressions = null;
		Expression startExpression = MultiplyExpression();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 35:
			case 36:
				break;
			default:
				this.jj_la1[18] = this.jj_gen;
				break;
			}
			BinaryOperator operator;
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 35:
				jj_consume_token(35);
				operator = PlusOperator.SINGLETON;
				break;
			case 36:
				jj_consume_token(36);
				operator = MinusOperator.SINGLETON;
				break;
			default:
				this.jj_la1[19] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			Expression expression = MultiplyExpression();
			if (operators == null) {
				operators = new ArrayList();
				expressions = new ArrayList();
			}
			operators.add(operator);
			expressions.add(expression);
		}
//		if (operators != null) {
//			return new BinaryOperatorExpression(startExpression, operators,
//					expressions);
//		}
//		return startExpression;
	}

	public final Expression MultiplyExpression() throws ELParseException {
		List operators = null;
		List expressions = null;
		Expression startExpression = UnaryExpression();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
				break;
			default:
				this.jj_la1[20] = this.jj_gen;
				break;
			}
			BinaryOperator operator;
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 37:
				jj_consume_token(37);
				operator = MultiplyOperator.SINGLETON;
				break;
			case 38:
			case 39:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 38:
					jj_consume_token(38);
					break;
				case 39:
					jj_consume_token(39);
					break;
				default:
					this.jj_la1[21] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = DivideOperator.SINGLETON;
				break;
			case 40:
			case 41:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 40:
					jj_consume_token(40);
					break;
				case 41:
					jj_consume_token(41);
					break;
				default:
					this.jj_la1[22] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = ModulusOperator.SINGLETON;
				break;
			default:
				this.jj_la1[23] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			Expression expression = UnaryExpression();
			if (operators == null) {
				operators = new ArrayList();
				expressions = new ArrayList();
			}
			operators.add(operator);
			expressions.add(expression);
		}
//		if (operators != null) {
//			return new BinaryOperatorExpression(startExpression, operators,
//					expressions);
//		}
//		return startExpression;
	}

	public final Expression ConditionalExpression() throws ELParseException {
		Expression condition = OrExpression();
		jj_consume_token(49);
		Expression trueBranch = Expression();
		jj_consume_token(32);
		Expression falseBranch = Expression();
		return new ConditionalExpression(condition, trueBranch, falseBranch);
	}

	public final Expression UnaryExpression() throws ELParseException {
		UnaryOperator singleOperator = null;

		List operators = null;
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 36:
			case 42:
			case 43:
			case 48:
				break;
			default:
				this.jj_la1[24] = this.jj_gen;
				break;
			}
			UnaryOperator operator;
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 42:
			case 43:
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 42:
					jj_consume_token(42);
					break;
				case 43:
					jj_consume_token(43);
					break;
				default:
					this.jj_la1[25] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
				operator = NotOperator.SINGLETON;
				break;
			case 36:
				jj_consume_token(36);
				operator = UnaryMinusOperator.SINGLETON;
				break;
			case 48:
				jj_consume_token(48);
				operator = EmptyOperator.SINGLETON;
				break;
			default:
				this.jj_la1[26] = this.jj_gen;
				jj_consume_token(-1);
				throw new ELParseException();
			}
			if (singleOperator == null) {
				singleOperator = operator;
			} else if (operators == null) {
				operators = new ArrayList();
				operators.add(singleOperator);
				operators.add(operator);
			} else {
				operators.add(operator);
			}
		}
//		Expression expression = Value();
//		if (operators != null)
//			return new UnaryOperatorExpression(null, operators, expression);
//		if (singleOperator != null) {
//			return new UnaryOperatorExpression(singleOperator, null, expression);
//		}
//		return expression;
	}

	public final Expression Value() throws ELParseException {
		List suffixes = null;
		Expression prefix = ValuePrefix();
		while (true) {
			switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
			case 16:
			case 33:
				break;
			default:
				this.jj_la1[27] = this.jj_gen;
				break;
			}
			ValueSuffix suffix = ValueSuffix();
			if (suffixes == null) {
				suffixes = new ArrayList();
			}
			suffixes.add(suffix);
		}
//		if (suffixes == null) {
//			return prefix;
//		}
//		return new ComplexValue(prefix, suffixes);
	}

	public final Expression ValuePrefix() throws ELParseException {
		Expression ret;
		switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
		case 7:
		case 8:
		case 10:
		case 12:
		case 13:
		case 14:
			ret = Literal();
			break;
		case 29:
			jj_consume_token(29);
			 ret = Expression();
			jj_consume_token(30);
			break;
		default:
			this.jj_la1[28] = this.jj_gen;
			if (jj_2_2(2147483647)) {
				ret = FunctionInvocation();
			} else {
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 50:
					ret = NamedValue();
					break;
				default:
					this.jj_la1[29] = this.jj_gen;
					jj_consume_token(-1);
					throw new ELParseException();
				}
			}
			break;
		}
		return ret;
	}

	public final NamedValue NamedValue() throws ELParseException {
		Token t = jj_consume_token(50);
		return new NamedValue(t.image);
	}

	public final FunctionInvocation FunctionInvocation()
			throws ELParseException {
		List argumentList = new ArrayList();

		String qualifiedName = QualifiedName();
		jj_consume_token(29);
		switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
		case 7:
		case 8:
		case 10:
		case 12:
		case 13:
		case 14:
		case 29:
		case 36:
		case 42:
		case 43:
		case 48:
		case 50:
			Expression exp = Expression();
			argumentList.add(exp);
			while (true) {
				switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
				case 31:
					break;
				default:
					this.jj_la1[30] = this.jj_gen;
					break;
				}
				jj_consume_token(31);
				exp = Expression();
				argumentList.add(exp);
			}
		}

		this.jj_la1[31] = this.jj_gen;

		jj_consume_token(30);
		return new FunctionInvocation(qualifiedName, argumentList);
	}

	public final ValueSuffix ValueSuffix() throws ELParseException {
		ValueSuffix suffix;
		switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
		case 16:
			suffix = PropertySuffix();
			break;
		case 33:
			suffix = ArraySuffix();
			break;
		default:
			this.jj_la1[32] = this.jj_gen;
			jj_consume_token(-1);
			throw new ELParseException();
		}
		return suffix;
	}

	public final PropertySuffix PropertySuffix() throws ELParseException {
		jj_consume_token(16);
		String property = Identifier();
		return new PropertySuffix(property);
	}

	public final ArraySuffix ArraySuffix() throws ELParseException {
		jj_consume_token(33);
		Expression index = Expression();
		jj_consume_token(34);
		return new ArraySuffix(index);
	}

	public final Literal Literal() throws ELParseException {
		Literal ret;
		switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
		case 12:
		case 13:
			ret = BooleanLiteral();
			break;
		case 7:
			ret = IntegerLiteral();
			break;
		case 8:
			ret = FloatingPointLiteral();
			break;
		case 10:
			ret = StringLiteral();
			break;
		case 14:
			ret = NullLiteral();
			break;
		case 9:
		case 11:
		default:
			this.jj_la1[33] = this.jj_gen;
			jj_consume_token(-1);
			throw new ELParseException();
		}
		return ret;
	}

	public final BooleanLiteral BooleanLiteral() throws ELParseException {
		switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
		case 12:
			jj_consume_token(12);
			return BooleanLiteral.TRUE;
		case 13:
			jj_consume_token(13);
			return BooleanLiteral.FALSE;
		}
		this.jj_la1[34] = this.jj_gen;
		jj_consume_token(-1);
		throw new ELParseException();
	}

	public final StringLiteral StringLiteral() throws ELParseException {
		Token t = jj_consume_token(10);
		return StringLiteral.fromToken(t.image);
	}

	public final IntegerLiteral IntegerLiteral() throws ELParseException {
		Token t = jj_consume_token(7);
		return new IntegerLiteral(t.image);
	}

	public final FloatingPointLiteral FloatingPointLiteral()
			throws ELParseException {
		Token t = jj_consume_token(8);
		return new FloatingPointLiteral(t.image);
	}

	public final NullLiteral NullLiteral() throws ELParseException {
		jj_consume_token(14);
		return NullLiteral.SINGLETON;
	}

	public final String Identifier() throws ELParseException {
		Token t = jj_consume_token(50);
		return t.image;
	}

	public final String QualifiedName() throws ELParseException {
		String prefix = null;
		String localPart = null;
		if (jj_2_3(2147483647)) {
			prefix = Identifier();
			jj_consume_token(32);
		}
		localPart = Identifier();
		if (prefix == null) {
			return localPart;
		}
		return prefix + ":" + localPart;
	}

	private boolean jj_2_1(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = (this.jj_scanpos = this.token);
		try {
			return !jj_3_1();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(0, xla);
		}
	}

	private boolean jj_2_2(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = (this.jj_scanpos = this.token);
		try {
			return !jj_3_2();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(1, xla);
		}
	}

	private boolean jj_2_3(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = (this.jj_scanpos = this.token);
		try {
			return !jj_3_3();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(2, xla);
		}
	}

	private boolean jj_3R_69() {
		if (jj_scan_token(8))
			return true;
		return false;
	}

	private boolean jj_3R_18() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(46)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(47))
				return true;
		}
		if (jj_3R_17())
			return true;
		return false;
	}

	private boolean jj_3R_14() {
		if (jj_3R_17())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_18());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_72() {
		if (jj_scan_token(31))
			return true;
		if (jj_3R_15())
			return true;
		return false;
	}

	private boolean jj_3R_68() {
		if (jj_scan_token(7))
			return true;
		return false;
	}

	private boolean jj_3R_28() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(27)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(28))
				return true;
		}
		return false;
	}

	private boolean jj_3_1() {
		if (jj_3R_11())
			return true;
		return false;
	}

	private boolean jj_3R_27() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(21)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(22))
				return true;
		}
		return false;
	}

	private boolean jj_3R_24() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_27()) {
			this.jj_scanpos = xsp;
			if (jj_3R_28())
				return true;
		}
		if (jj_3R_23())
			return true;
		return false;
	}

	private boolean jj_3R_64() {
		if (jj_3R_15())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_72());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_70() {
		if (jj_scan_token(10))
			return true;
		return false;
	}

	private boolean jj_3R_20() {
		if (jj_3R_14())
			return true;
		return false;
	}

	private boolean jj_3R_46() {
		if (jj_scan_token(48))
			return true;
		return false;
	}

	private boolean jj_3R_19() {
		if (jj_3R_11())
			return true;
		return false;
	}

	private boolean jj_3R_21() {
		if (jj_3R_23())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_24());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_38() {
		if (jj_scan_token(36))
			return true;
		return false;
	}

	private boolean jj_3R_45() {
		if (jj_scan_token(36))
			return true;
		return false;
	}

	private boolean jj_3R_15() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_19()) {
			this.jj_scanpos = xsp;
			if (jj_3R_20())
				return true;
		}
		return false;
	}

	private boolean jj_3R_37() {
		if (jj_scan_token(35))
			return true;
		return false;
	}

	private boolean jj_3R_55() {
		if (jj_3R_12())
			return true;
		if (jj_scan_token(29)) {
			return true;
		}
		Token xsp = this.jj_scanpos;
		if (jj_3R_64())
			this.jj_scanpos = xsp;
		if (jj_scan_token(30))
			return true;
		return false;
	}

	private boolean jj_3R_44() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(42)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(43))
				return true;
		}
		return false;
	}

	private boolean jj_3R_30() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_37()) {
			this.jj_scanpos = xsp;
			if (jj_3R_38())
				return true;
		}
		if (jj_3R_29())
			return true;
		return false;
	}

	private boolean jj_3R_39() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_44()) {
			this.jj_scanpos = xsp;
			if (jj_3R_45()) {
				this.jj_scanpos = xsp;
				if (jj_3R_46())
					return true;
			}
		}
		return false;
	}

	private boolean jj_3R_74() {
		if (jj_scan_token(13))
			return true;
		return false;
	}

	private boolean jj_3R_35() {
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_39());
		this.jj_scanpos = xsp;

		if (jj_3R_40())
			return true;
		return false;
	}

	private boolean jj_3R_73() {
		if (jj_scan_token(12))
			return true;
		return false;
	}

	private boolean jj_3R_67() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_73()) {
			this.jj_scanpos = xsp;
			if (jj_3R_74())
				return true;
		}
		return false;
	}

	private boolean jj_3R_25() {
		if (jj_3R_29())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_30());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3_2() {
		if (jj_3R_12())
			return true;
		if (jj_scan_token(29))
			return true;
		return false;
	}

	private boolean jj_3R_56() {
		if (jj_scan_token(50))
			return true;
		return false;
	}

	private boolean jj_3R_63() {
		if (jj_3R_71())
			return true;
		return false;
	}

	private boolean jj_3R_62() {
		if (jj_3R_70())
			return true;
		return false;
	}

	private boolean jj_3R_61() {
		if (jj_3R_69())
			return true;
		return false;
	}

	private boolean jj_3R_60() {
		if (jj_3R_68())
			return true;
		return false;
	}

	private boolean jj_3R_59() {
		if (jj_3R_67())
			return true;
		return false;
	}

	private boolean jj_3R_52() {
		if (jj_3R_56())
			return true;
		return false;
	}

	private boolean jj_3R_54() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_59()) {
			this.jj_scanpos = xsp;
			if (jj_3R_60()) {
				this.jj_scanpos = xsp;
				if (jj_3R_61()) {
					this.jj_scanpos = xsp;
					if (jj_3R_62()) {
						this.jj_scanpos = xsp;
						if (jj_3R_63())
							return true;
					}
				}
			}
		}
		return false;
	}

	private boolean jj_3R_51() {
		if (jj_3R_55())
			return true;
		return false;
	}

	private boolean jj_3R_50() {
		if (jj_scan_token(29))
			return true;
		if (jj_3R_15())
			return true;
		if (jj_scan_token(30))
			return true;
		return false;
	}

	private boolean jj_3R_49() {
		if (jj_3R_54())
			return true;
		return false;
	}

	private boolean jj_3_3() {
		if (jj_3R_13())
			return true;
		if (jj_scan_token(32))
			return true;
		return false;
	}

	private boolean jj_3R_11() {
		if (jj_3R_14())
			return true;
		if (jj_scan_token(49))
			return true;
		if (jj_3R_15())
			return true;
		if (jj_scan_token(32))
			return true;
		if (jj_3R_15())
			return true;
		return false;
	}

	private boolean jj_3R_47() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_49()) {
			this.jj_scanpos = xsp;
			if (jj_3R_50()) {
				this.jj_scanpos = xsp;
				if (jj_3R_51()) {
					this.jj_scanpos = xsp;
					if (jj_3R_52())
						return true;
				}
			}
		}
		return false;
	}

	private boolean jj_3R_22() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(44)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(45))
				return true;
		}
		if (jj_3R_21())
			return true;
		return false;
	}

	private boolean jj_3R_16() {
		if (jj_3R_13())
			return true;
		if (jj_scan_token(32))
			return true;
		return false;
	}

	private boolean jj_3R_66() {
		if (jj_scan_token(33))
			return true;
		if (jj_3R_15())
			return true;
		if (jj_scan_token(34))
			return true;
		return false;
	}

	private boolean jj_3R_17() {
		if (jj_3R_21())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_22());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_34() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(23)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(24))
				return true;
		}
		return false;
	}

	private boolean jj_3R_33() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(25)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(26))
				return true;
		}
		return false;
	}

	private boolean jj_3R_32() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(17)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(18))
				return true;
		}
		return false;
	}

	private boolean jj_3R_12() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_16())
			this.jj_scanpos = xsp;
		if (jj_3R_13())
			return true;
		return false;
	}

	private boolean jj_3R_31() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(19)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(20))
				return true;
		}
		return false;
	}

	private boolean jj_3R_26() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_31()) {
			this.jj_scanpos = xsp;
			if (jj_3R_32()) {
				this.jj_scanpos = xsp;
				if (jj_3R_33()) {
					this.jj_scanpos = xsp;
					if (jj_3R_34())
						return true;
				}
			}
		}
		if (jj_3R_25())
			return true;
		return false;
	}

	private boolean jj_3R_65() {
		if (jj_scan_token(16))
			return true;
		if (jj_3R_13())
			return true;
		return false;
	}

	private boolean jj_3R_43() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(40)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(41))
				return true;
		}
		return false;
	}

	private boolean jj_3R_23() {
		if (jj_3R_25())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_26());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_42() {
		Token xsp = this.jj_scanpos;
		if (jj_scan_token(38)) {
			this.jj_scanpos = xsp;
			if (jj_scan_token(39))
				return true;
		}
		return false;
	}

	private boolean jj_3R_48() {
		if (jj_3R_53())
			return true;
		return false;
	}

	private boolean jj_3R_13() {
		if (jj_scan_token(50))
			return true;
		return false;
	}

	private boolean jj_3R_41() {
		if (jj_scan_token(37))
			return true;
		return false;
	}

	private boolean jj_3R_36() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_41()) {
			this.jj_scanpos = xsp;
			if (jj_3R_42()) {
				this.jj_scanpos = xsp;
				if (jj_3R_43())
					return true;
			}
		}
		if (jj_3R_35())
			return true;
		return false;
	}

	private boolean jj_3R_40() {
		if (jj_3R_47())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_48());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_58() {
		if (jj_3R_66())
			return true;
		return false;
	}

	private boolean jj_3R_71() {
		if (jj_scan_token(14))
			return true;
		return false;
	}

	private boolean jj_3R_29() {
		if (jj_3R_35())
			return true;
		Token xsp;
		do
			xsp = this.jj_scanpos;
		while (!jj_3R_36());
		this.jj_scanpos = xsp;

		return false;
	}

	private boolean jj_3R_57() {
		if (jj_3R_65())
			return true;
		return false;
	}

	private boolean jj_3R_53() {
		Token xsp = this.jj_scanpos;
		if (jj_3R_57()) {
			this.jj_scanpos = xsp;
			if (jj_3R_58())
				return true;
		}
		return false;
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] { 6, 6, 6, 536900992, 0, 0, 0, 0, 408944640,
				6291456, 402653184, 408944640, 127795200, 1572864, 393216,
				100663296, 25165824, 127795200, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				65536, 536900992, 0, -2147483648, 536900992, 65536, 30080,
				12288 };
	}

	private static void jj_la1_init_1() {
		jj_la1_1 = new int[] { 0, 0, 0, 330768, 49152, 49152, 12288, 12288, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 992, 192, 768, 992, 68624,
				3072, 68624, 2, 0, 262144, 0, 330768, 2 };
	}

	public ELParser(InputStream stream) {
		this(stream, null);
	}

	public ELParser(InputStream stream, String encoding) {
		try {
			this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		this.token_source = new ELParserTokenManager(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 35; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJCalls();
	}

	public void ReInit(InputStream stream) {
		ReInit(stream, null);
	}

	public void ReInit(InputStream stream, String encoding) {
		try {
			this.jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 35; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJCalls();
	}

	public ELParser(Reader stream) {
		this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
		this.token_source = new ELParserTokenManager(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 35; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJCalls();
	}

	public void ReInit(Reader stream) {
		this.jj_input_stream.ReInit(stream, 1, 1);
		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 35; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJCalls();
	}

	public ELParser(ELParserTokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 35; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJCalls();
	}

	public void ReInit(ELParserTokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 35; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJCalls();
	}

	private Token jj_consume_token(int kind) throws ELParseException {
		Token oldToken;
		if ((oldToken = this.token).next != null)
			this.token = this.token.next;
		else
			this.token = (this.token.next = this.token_source.getNextToken());
		this.jj_ntk = -1;
		if (this.token.kind == kind) {
			this.jj_gen += 1;
			if (++this.jj_gc > 100) {
				this.jj_gc = 0;
				for (int i = 0; i < this.jj_2_rtns.length; i++) {
					JJCalls c = this.jj_2_rtns[i];
					while (c != null) {
						if (c.gen < this.jj_gen)
							c.first = null;
						c = c.next;
					}
				}
			}
			return this.token;
		}
		this.token = oldToken;
		this.jj_kind = kind;
		throw generateParseException();
	}

	private boolean jj_scan_token(int kind) {
		if (this.jj_scanpos == this.jj_lastpos) {
			this.jj_la -= 1;
			if (this.jj_scanpos.next == null)
				this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next = this.token_source
						.getNextToken());
			else
				this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next);
		} else {
			this.jj_scanpos = this.jj_scanpos.next;
		}
		if (this.jj_rescan) {
			int i = 0;
			Token tok = this.token;
			while ((tok != null) && (tok != this.jj_scanpos)) {
				i++;
				tok = tok.next;
			}
			if (tok != null)
				jj_add_error_token(kind, i);
		}
		if (this.jj_scanpos.kind != kind)
			return true;
		if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos))
			throw this.jj_ls;
		return false;
	}

	public final Token getNextToken() {
		if (this.token.next != null)
			this.token = this.token.next;
		else
			this.token = (this.token.next = this.token_source.getNextToken());
		this.jj_ntk = -1;
		this.jj_gen += 1;
		return this.token;
	}

	public final Token getToken(int index) {
		Token t = this.token;
		for (int i = 0; i < index; i++) {
			if (t.next != null)
				t = t.next;
			else
				t = t.next = this.token_source.getNextToken();
		}
		return t;
	}

	private int jj_ntk() {
		if ((this.jj_nt = this.token.next) == null) {
			return this.jj_ntk = (this.token.next = this.token_source
					.getNextToken()).kind;
		}
		return this.jj_ntk = this.jj_nt.kind;
	}

	private void jj_add_error_token(int kind, int pos) {
		if (pos >= 100)
			return;
		if (pos == this.jj_endpos + 1) {
			this.jj_lasttokens[(this.jj_endpos++)] = kind;
		} else if (this.jj_endpos != 0) {
			this.jj_expentry = new int[this.jj_endpos];
			for (int i = 0; i < this.jj_endpos; i++) {
				this.jj_expentry[i] = this.jj_lasttokens[i];
			}
			for (Iterator it = this.jj_expentries.iterator(); it.hasNext();) {
				int[] oldentry = (int[]) it.next();
				if (oldentry.length == this.jj_expentry.length) {
					int i = 0;
					while (oldentry[i] == this.jj_expentry[i]) {
						i++;
						if (i >= this.jj_expentry.length) {
							this.jj_expentries.add(this.jj_expentry);
							if (pos != 0) {
								int tmp190_189 = pos;
								this.jj_endpos = tmp190_189;
								this.jj_lasttokens[(tmp190_189 - 1)] = kind;
							}
						}
					}
				}
			}
		}
	}

	public ELParseException generateParseException() {
		this.jj_expentries.clear();
		boolean[] la1tokens = new boolean[55];
		if (this.jj_kind >= 0) {
			la1tokens[this.jj_kind] = true;
			this.jj_kind = -1;
		}
		for (int i = 0; i < 35; i++) {
			if (this.jj_la1[i] == this.jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & 1 << j) != 0) {
						la1tokens[j] = true;
					}
					if ((jj_la1_1[i] & 1 << j) != 0) {
						la1tokens[(32 + j)] = true;
					}
				}
			}
		}
		for (int i = 0; i < 55; i++) {
			if (la1tokens[i] != false) {
				this.jj_expentry = new int[1];
				this.jj_expentry[0] = i;
				this.jj_expentries.add(this.jj_expentry);
			}
		}
		this.jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
		int[][] exptokseq = new int[this.jj_expentries.size()][];
		for (int i = 0; i < this.jj_expentries.size(); i++) {
			exptokseq[i] = ((int[]) this.jj_expentries.get(i));
		}
		return new ELParseException(this.token, exptokseq, tokenImage);
	}

	public final void enable_tracing() {
	}

	public final void disable_tracing() {
	}

	private void jj_rescan_token() {
		this.jj_rescan = true;
		for (int i = 0; i < 3; i++)
			try {
				JJCalls p = this.jj_2_rtns[i];
				do {
					if (p.gen > this.jj_gen) {
						this.jj_la = p.arg;
						this.jj_lastpos = (this.jj_scanpos = p.first);
						switch (i) {
						case 0:
							jj_3_1();
							break;
						case 1:
							jj_3_2();
							break;
						case 2:
							jj_3_3();
						}
					}

					p = p.next;
				}

				while (p != null);
			} catch (LookaheadSuccess localLookaheadSuccess) {
			}
		this.jj_rescan = false;
	}

	private void jj_save(int index, int xla) {
		JJCalls p = this.jj_2_rtns[index];
		while (p.gen > this.jj_gen) {
			if (p.next == null) {
				p = p.next = new JJCalls();
				break;
			}
			p = p.next;
		}
		p.gen = (this.jj_gen + xla - this.jj_la);
		p.first = this.token;
		p.arg = xla;
	}

	static final class JJCalls {
		int gen;
		Token first;
		int arg;
		JJCalls next;
	}

	private static final class LookaheadSuccess extends Error {
		private static final long serialVersionUID = 1L;
	}
}