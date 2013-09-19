package com.zving.framework.json.fastjson;

import com.zving.framework.json.JSONException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;

public class JSONScanner implements JSONLexer {
	public static final byte EOI = 26;
	private final char[] buf;
	private int bp;
	private final int buflen;
	private int eofPos;
	private char ch;
	private int pos;
	private char[] sbuf;
	private int sp;
	private int np;
	private int token;
	private Keywords keywods = Keywords.DEFAULT_KEYWORDS;

	private static final ThreadLocal<char[]> sbufRef = new ThreadLocal();

	private int features = 0;

	private Calendar calendar = null;

	private static boolean[] whitespaceFlags = new boolean[256];
	boolean hasSpecial;
	public static final int NOT_MATCH = -1;
	public static final int NOT_MATCH_NAME = -2;
	public static final int UNKOWN = 0;
	public static final int OBJECT = 1;
	public static final int ARRAY = 2;
	public static final int VALUE = 3;
	public static final int END = 4;
	public int matchStat = 0;
	private static final long MULTMIN_RADIX_TEN = -922337203685477580L;
	private static final long N_MULTMAX_RADIX_TEN = -922337203685477580L;
	private static final int INT_MULTMIN_RADIX_TEN = -214748364;
	private static final int INT_N_MULTMAX_RADIX_TEN = -214748364;
	private static final int[] digits;
	public final int ISO8601_LEN_0 = "0000-00-00".length();
	public final int ISO8601_LEN_1 = "0000-00-00T00:00:00".length();
	public final int ISO8601_LEN_2 = "0000-00-00T00:00:00.000".length();

	static {
		whitespaceFlags[32] = true;
		whitespaceFlags[10] = true;
		whitespaceFlags[13] = true;
		whitespaceFlags[9] = true;
		whitespaceFlags[12] = true;
		whitespaceFlags[8] = true;

		digits = new int[103];

		for (int i = 48; i <= 57; i++) {
			digits[i] = (i - 48);
		}

		for (int i = 97; i <= 102; i++) {
			digits[i] = (i - 97 + 10);
		}
		for (int i = 65; i <= 70; i++)
			digits[i] = (i - 65 + 10);
	}

	public JSONScanner(String input) {
		this(input, 0);
	}

	public JSONScanner(String input, int features) {
		this(input.toCharArray(), input.length(), features);
	}

	public JSONScanner(char[] input, int inputLength) {
		this(input, inputLength, 0);
	}

	public JSONScanner(char[] input, int inputLength, int features) {
		this.features = features;

		this.sbuf = ((char[]) sbufRef.get());
		if (this.sbuf == null) {
			this.sbuf = new char[64];
			sbufRef.set(this.sbuf);
		}

		this.eofPos = inputLength;

		if (inputLength == input.length) {
			if ((input.length > 0) && (isWhitespace(input[(input.length - 1)]))) {
				inputLength--;
			} else {
				char[] newInput = new char[inputLength + 1];
				System.arraycopy(input, 0, newInput, 0, input.length);
				input = newInput;
			}
		}
		this.buf = input;
		this.buflen = inputLength;
		this.buf[this.buflen] = '\032';
		this.bp = -1;

		this.ch = this.buf[(++this.bp)];
	}

	public final int getBufferPosition() {
		return this.bp;
	}

	public void reset(int mark, char mark_ch, int token) {
		this.bp = mark;
		this.ch = mark_ch;
		this.token = token;
	}

	public boolean isBlankInput() {
		for (int i = 0; i < this.buflen; i++) {
			if (!isWhitespace(this.buf[i])) {
				return false;
			}
		}

		return true;
	}

	public static final boolean isWhitespace(char ch) {
		return (ch == ' ') || (ch == '\n') || (ch == '\r') || (ch == '\t')
				|| (ch == '\f') || (ch == '\b');
	}

	private void lexError(String key, Object[] args) {
		this.token = 1;
	}

	public final int token() {
		return this.token;
	}

	public final void skipWhitespace() {
		while (whitespaceFlags[this.ch] != false)
			this.ch = this.buf[(++this.bp)];
	}

	public final char getCurrent() {
		return this.ch;
	}

	public final void nextTokenWithColon() {
		while (true) {
			if (this.ch == ':') {
				this.ch = this.buf[(++this.bp)];
				nextToken();
				return;
			}

			if ((this.ch != ' ') && (this.ch != '\n') && (this.ch != '\r')
					&& (this.ch != '\t') && (this.ch != '\f')
					&& (this.ch != '\b'))
				break;
			this.ch = this.buf[(++this.bp)];
		}

		throw new JSONException("not match ':'");
	}

	public final void nextTokenWithColon(int expect) {
		// while (true) {
		// if (this.ch == ':') {
		// this.ch = this.buf[(++this.bp)];
		// break label75;
		// }
		//
		// if (!isWhitespace(this.ch)) break;
		// this.ch = this.buf[(++this.bp)];
		// }
		//
		// throw new JSONException("not match ':'");
		// while (true)
		// {
		// label75: if (expect == 2) {
		// if ((this.ch >= '0') && (this.ch <= '9')) {
		// this.sp = 0;
		// this.pos = this.bp;
		// scanNumber();
		// return;
		// }
		//
		// if (this.ch == '"') {
		// this.sp = 0;
		// this.pos = this.bp;
		// scanString();
		// }
		// }
		// else if (expect == 4) {
		// if (this.ch == '"') {
		// this.sp = 0;
		// this.pos = this.bp;
		// scanString();
		// return;
		// }
		//
		// if ((this.ch >= '0') && (this.ch <= '9')) {
		// this.sp = 0;
		// this.pos = this.bp;
		// scanNumber();
		// }
		//
		// }
		// else if (expect == 12) {
		// if (this.ch == '{') {
		// this.token = 12;
		// this.ch = this.buf[(++this.bp)];
		// return;
		// }
		// if (this.ch == '[') {
		// this.token = 14;
		// this.ch = this.buf[(++this.bp)];
		// }
		// }
		// else if (expect == 14) {
		// if (this.ch == '[') {
		// this.token = 14;
		// this.ch = this.buf[(++this.bp)];
		// return;
		// }
		//
		// if (this.ch == '{') {
		// this.token = 12;
		// this.ch = this.buf[(++this.bp)];
		// return;
		// }
		// }
		//
		// if (!isWhitespace(this.ch)) break;
		// this.ch = this.buf[(++this.bp)];
		// }

		// nextToken();
	}

	public final void incrementBufferPosition() {
		this.ch = this.buf[(++this.bp)];
	}

	public final void resetStringPosition() {
		this.sp = 0;
	}

	public void nextToken(int expect) {
		while (true) {
			switch (expect) {
			case 12:
				if (this.ch == '{') {
					this.token = 12;
					this.ch = this.buf[(++this.bp)];
					return;
				}
				if (this.ch == '[') {
					this.token = 14;
					this.ch = this.buf[(++this.bp)];
					return;
				}
				break;
			case 16:
				if (this.ch == ',') {
					this.token = 16;
					this.ch = this.buf[(++this.bp)];
					return;
				}

				if (this.ch == '}') {
					this.token = 13;
					this.ch = this.buf[(++this.bp)];
					return;
				}

				if (this.ch == ']') {
					this.token = 15;
					this.ch = this.buf[(++this.bp)];
					return;
				}

				if (this.ch == '\032') {
					this.token = 20;
					return;
				}
				break;
			case 2:
				if ((this.ch >= '0') && (this.ch <= '9')) {
					this.sp = 0;
					this.pos = this.bp;
					scanNumber();
					return;
				}

				if (this.ch == '"') {
					this.sp = 0;
					this.pos = this.bp;
					scanString();
					return;
				}

				if (this.ch == '[') {
					this.token = 14;
					this.ch = this.buf[(++this.bp)];
					return;
				}

				if (this.ch == '{') {
					this.token = 12;
					this.ch = this.buf[(++this.bp)];
					return;
				}
				break;
			case 4:
				if (this.ch == '"') {
					this.sp = 0;
					this.pos = this.bp;
					scanString();
					return;
				}

				if ((this.ch >= '0') && (this.ch <= '9')) {
					this.sp = 0;
					this.pos = this.bp;
					scanNumber();
					return;
				}

				if (this.ch == '[') {
					this.token = 14;
					this.ch = this.buf[(++this.bp)];
					return;
				}

				if (this.ch == '{') {
					this.token = 12;
					this.ch = this.buf[(++this.bp)];
					return;
				}
				break;
			case 14:
				if (this.ch == '[') {
					this.token = 14;
					this.ch = this.buf[(++this.bp)];
					return;
				}

				if (this.ch == '{') {
					this.token = 12;
					this.ch = this.buf[(++this.bp)];
					return;
				}
				break;
			case 15:
				if (this.ch == ']') {
					this.token = 15;
					this.ch = this.buf[(++this.bp)];
					return;
				}
			case 20:
				if (this.ch == '\032') {
					this.token = 20;
					return;
				}
				break;
			}

			if ((this.ch != ' ') && (this.ch != '\n') && (this.ch != '\r')
					&& (this.ch != '\t') && (this.ch != '\f')
					&& (this.ch != '\b'))
				break;
			this.ch = this.buf[(++this.bp)];
		}

		nextToken();
	}

	public final void nextToken() {
		this.sp = 0;
		while (true) {
			this.pos = this.bp;

			if (this.ch == '"') {
				scanString();
				return;
			}

			if (this.ch == ',') {
				this.ch = this.buf[(++this.bp)];
				this.token = 16;
				return;
			}

			if ((this.ch >= '0') && (this.ch <= '9')) {
				scanNumber();
				return;
			}

			if (this.ch == '-') {
				scanNumber();
				return;
			}

			switch (this.ch) {
			case '\'':
				if (!isEnabled(Feature.AllowSingleQuotes)) {
					throw new JSONException(
							"Feature.AllowSingleQuotes is false");
				}
				scanStringSingleQuote();
				return;
			case '\b':
			case '\t':
			case '\n':
			case '\f':
			case '\r':
			case ' ':
				this.ch = this.buf[(++this.bp)];
			case 't':
			case 'f':
			case 'n':
			case 'D':
			case '(':
			case ')':
			case '[':
			case ']':
			case '{':
			case '}':
			case ':':
			}
		}
//		scanTrue();
//		return;
//
//		scanFalse();
//		return;
//
//		scanNullOrNew();
//		return;
//
//		scanIdent();
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 10;
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 11;
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 14;
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 15;
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 12;
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 13;
//		return;
//
//		this.ch = this.buf[(++this.bp)];
//		this.token = 17;
//		return;
//
//		if ((this.bp == this.buflen)
//				|| ((this.ch == '\032') && (this.bp + 1 == this.buflen))) {
//			if (this.token == 20) {
//				throw new JSONException("EOF error");
//			}
//
//			this.token = 20;
//			this.pos = (this.bp = this.eofPos);
//		} else {
//			lexError("illegal.char", new Object[] { String.valueOf(this.ch) });
//			this.ch = this.buf[(++this.bp)];
//		}
	}

	public final void scanStringSingleQuote() {
		this.np = this.bp;
		this.hasSpecial = false;
		while (true) {
			char ch = this.buf[(++this.bp)];

			if (ch == '\'') {
				break;
			}
			if (ch == '\032') {
				throw new JSONException("unclosed single-quote string");
			}

			if (ch == '\\') {
				if (!this.hasSpecial) {
					this.hasSpecial = true;

					if (this.sp > this.sbuf.length) {
						char[] newsbuf = new char[this.sp * 2];
						System.arraycopy(this.sbuf, 0, newsbuf, 0,
								this.sbuf.length);
						this.sbuf = newsbuf;
					}

					System.arraycopy(this.buf, this.np + 1, this.sbuf, 0,
							this.sp);
				}

				ch = this.buf[(++this.bp)];

				switch (ch) {
				case '"':
					putChar('"');
					break;
				case '\\':
					putChar('\\');
					break;
				case '/':
					putChar('/');
					break;
				case '\'':
					putChar('\'');
					break;
				case 'b':
					putChar('\b');
					break;
				case 'F':
				case 'f':
					putChar('\f');
					break;
				case 'n':
					putChar('\n');
					break;
				case 'r':
					putChar('\r');
					break;
				case 't':
					putChar('\t');
					break;
				case 'u':
					char c1 = ch = this.buf[(++this.bp)];
					char c2 = ch = this.buf[(++this.bp)];
					char c3 = ch = this.buf[(++this.bp)];
					char c4 = ch = this.buf[(++this.bp)];
					int val = Integer.parseInt(new String(new char[] { c1, c2,
							c3, c4 }), 16);
					putChar((char) val);
					break;
				default:
					this.ch = ch;
					throw new JSONException("unclosed single-quote string");
				}

			} else if (!this.hasSpecial) {
				this.sp += 1;
			} else if (this.sp == this.sbuf.length) {
				putChar(ch);
			} else {
				this.sbuf[(this.sp++)] = ch;
			}
		}
		char ch;
		this.token = 4;
		this.ch = this.buf[(++this.bp)];
	}

	public final void scanString() {
		this.np = this.bp;
		this.hasSpecial = false;
		while (true) {
			char ch = this.buf[(++this.bp)];

			if (ch == '"') {
				break;
			}
			if (ch == '\\') {
				if (!this.hasSpecial) {
					this.hasSpecial = true;

					if (this.sp >= this.sbuf.length) {
						int newCapcity = this.sbuf.length * 2;
						if (this.sp > newCapcity) {
							newCapcity = this.sp;
						}
						char[] newsbuf = new char[newCapcity];
						System.arraycopy(this.sbuf, 0, newsbuf, 0,
								this.sbuf.length);
						this.sbuf = newsbuf;
					}

					System.arraycopy(this.buf, this.np + 1, this.sbuf, 0,
							this.sp);
				}

				ch = this.buf[(++this.bp)];

				switch (ch) {
				case '"':
					putChar('"');
					break;
				case '\\':
					putChar('\\');
					break;
				case '/':
					putChar('/');
					break;
				case 'b':
					putChar('\b');
					break;
				case 'F':
				case 'f':
					putChar('\f');
					break;
				case 'n':
					putChar('\n');
					break;
				case 'r':
					putChar('\r');
					break;
				case 't':
					putChar('\t');
					break;
				case 'x':
					char x1 = ch = this.buf[(++this.bp)];
					char x2 = ch = this.buf[(++this.bp)];

					int x_val = digits[x1] * 16 + digits[x2];
					char x_char = (char) x_val;
					putChar(x_char);
					break;
				case 'u':
					char u1 = ch = this.buf[(++this.bp)];
					char u2 = ch = this.buf[(++this.bp)];
					char u3 = ch = this.buf[(++this.bp)];
					char u4 = ch = this.buf[(++this.bp)];
					int val = Integer.parseInt(new String(new char[] { u1, u2,
							u3, u4 }), 16);
					putChar((char) val);
					break;
				default:
					this.ch = ch;
					throw new JSONException("unclosed string : " + ch);
				}

			} else if (!this.hasSpecial) {
				this.sp += 1;
			} else if (this.sp == this.sbuf.length) {
				putChar(ch);
			} else {
				this.sbuf[(this.sp++)] = ch;
			}
		}
		this.token = 4;
		this.ch = this.buf[(++this.bp)];
	}

	public final String scanSymbolUnQuoted(SymbolTable symbolTable) {
		boolean[] firstIdentifierFlags = CharTypes.firstIdentifierFlags;
		char first = this.ch;

		boolean firstFlag = (this.ch >= firstIdentifierFlags.length)
				|| (firstIdentifierFlags[first] != false);
		if (!firstFlag) {
			throw new JSONException("illegal identifier : " + this.ch);
		}

		boolean[] identifierFlags = CharTypes.identifierFlags;

		int hash = first;

		this.np = this.bp;
		this.sp = 1;
		while (true) {
			char ch = this.buf[(++this.bp)];

			if ((ch < identifierFlags.length) && (identifierFlags[ch] == false)) {
				break;
			}

			hash = 31 * hash + ch;

			this.sp += 1;
		}
		char ch;
		this.ch = this.buf[this.bp];
		this.token = 18;

		return symbolTable.addSymbol(this.buf, this.np, this.sp, hash);
	}

	public boolean matchField(char[] fieldName) {
		int fieldNameLength = fieldName.length;
		for (int i = 0; i < fieldNameLength; i++) {
			if (fieldName[i] != this.buf[(this.bp + i)]) {
				return false;
			}
		}

		this.bp += fieldNameLength;
		this.ch = this.buf[this.bp];

		if (this.ch == '{') {
			this.ch = this.buf[(++this.bp)];
			this.token = 12;
		} else if (this.ch == '[') {
			this.ch = this.buf[(++this.bp)];
			this.token = 14;
		} else {
			nextToken();
		}

		return true;
	}

	public String scanFieldString(char[] fieldName) {
		this.matchStat = 0;

		int fieldNameLength = fieldName.length;
		for (int i = 0; i < fieldNameLength; i++) {
			if (fieldName[i] != this.buf[(this.bp + i)]) {
				this.matchStat = -2;
				return null;
			}
		}

		int index = this.bp + fieldNameLength;

		char ch = this.buf[(index++)];
		if (ch != '"') {
			this.matchStat = -1;
			return null;
		}

		int start = index;
		do {
			ch = this.buf[(index++)];
			if (ch == '"') {
				this.bp = index;
				this.ch = (ch = this.buf[this.bp]);
				String strVal = new String(this.buf, start, index - start - 1);
				break;
			}
		} while (ch != '\\');
		this.matchStat = -1;
		return null;
//		String strVal;
//		if (ch == ',') {
//			this.ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			return strVal;
//		}
//		if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return null;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return null;
//		}
//
//		return strVal;
	}

	public String scanFieldSymbol(char[] fieldName, SymbolTable symbolTable) {
		this.matchStat = 0;

		int fieldNameLength = fieldName.length;
		for (int i = 0; i < fieldNameLength; i++) {
			if (fieldName[i] != this.buf[(this.bp + i)]) {
				this.matchStat = -2;
				return null;
			}
		}

		int index = this.bp + fieldNameLength;

		char ch = this.buf[(index++)];
		if (ch != '"') {
			this.matchStat = -1;
			return null;
		}

		int start = index;
		int hash = 0;
		do {
			ch = this.buf[(index++)];
			if (ch == '"') {
				this.bp = index;
				this.ch = (ch = this.buf[this.bp]);
				String strVal = symbolTable.addSymbol(this.buf, start, index
						- start - 1, hash);
				break;
			}

			hash = 31 * hash + ch;
		} while (ch != '\\');
		this.matchStat = -1;
		return null;
//		String strVal;
//		if (ch == ',') {
//			this.ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			return strVal;
//		}
//		if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return null;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return null;
//		}
//
//		return strVal;
	}

	public ArrayList<String> scanFieldStringArray(char[] fieldName) {
		this.matchStat = 0;

		ArrayList list = new ArrayList();

		int fieldNameLength = fieldName.length;
		for (int i = 0; i < fieldNameLength; i++) {
			if (fieldName[i] != this.buf[(this.bp + i)]) {
				this.matchStat = -2;
				return null;
			}
		}

		int index = this.bp + fieldNameLength;

		char ch = this.buf[(index++)];

		if (ch != '[') {
			this.matchStat = -1;
			return null;
		}

		ch = this.buf[(index++)];
		while (true) {
			if (ch != '"') {
				this.matchStat = -1;
				return null;
			}

			int start = index;
			do {
				ch = this.buf[(index++)];
				if (ch == '"') {
					String strVal = new String(this.buf, start, index - start
							- 1);
					list.add(strVal);
					ch = this.buf[(index++)];
					break;
				}
			} while (ch != '\\');
			this.matchStat = -1;
			return null;
//			String strVal;
//			if (ch != ',')
//				break;
//			ch = this.buf[(index++)];
		}

//		if (ch == ']') {
//			ch = this.buf[(index++)];
//		} else {
//			this.matchStat = -1;
//			return null;
//		}
//
//		this.bp = index;
//		if (ch == ',') {
//			this.ch = this.buf[this.bp];
//			this.matchStat = 3;
//			return list;
//		}
//		if (ch == '}') {
//			ch = this.buf[this.bp];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//				this.ch = ch;
//			} else {
//				this.matchStat = -1;
//				return null;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return null;
//		}
//
//		return list;
	}

	public int scanFieldInt(char[] fieldName) {
//		this.matchStat = 0;
//
//		int fieldNameLength = fieldName.length;
//		for (int i = 0; i < fieldNameLength; i++) {
//			if (fieldName[i] != this.buf[(this.bp + i)]) {
//				this.matchStat = -2;
//				return 0;
//			}
//		}
//
//		int index = this.bp + fieldNameLength;
//
//		char ch = this.buf[(index++)];
//
//		if ((ch >= '0') && (ch <= '9')) {
//			int value = digits[ch];
//			while (true) {
//				ch = this.buf[(index++)];
//				if ((ch < '0') || (ch > '9'))
//					break;
//				value = value * 10 + digits[ch];
//			}
//			if (ch == '.') {
//				this.matchStat = -1;
//				return 0;
//			}
//			this.bp = (index - 1);
//
//			if (value < 0) {
//				this.matchStat = -1;
//				return 0;
//			}
//		} else {
//			this.matchStat = -1;
//			return 0;
//		}
//		int value;
//		if (ch == ',') {
//			ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			this.token = 16;
////			return value;
//		}
//
//		if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return 0;
//			}
//			this.matchStat = 4;
//		}
//
//		return value;
		return 0;
	}

	public boolean scanFieldBoolean(char[] fieldName) {
//		this.matchStat = 0;
//
//		int fieldNameLength = fieldName.length;
//		for (int i = 0; i < fieldNameLength; i++) {
//			if (fieldName[i] != this.buf[(this.bp + i)]) {
//				this.matchStat = -2;
//				return false;
//			}
//		}
//
//		int index = this.bp + fieldNameLength;
//
//		char ch = this.buf[(index++)];
//		boolean value;
//		if (ch == 't') {
//			if (this.buf[(index++)] != 'r') {
//				this.matchStat = -1;
//				return false;
//			}
//			if (this.buf[(index++)] != 'u') {
//				this.matchStat = -1;
//				return false;
//			}
//			if (this.buf[(index++)] != 'e') {
//				this.matchStat = -1;
//				return false;
//			}
//
//			this.bp = index;
//			ch = this.buf[this.bp];
//			value = true;
//		} else {
//			if (ch == 'f') {
//				if (this.buf[(index++)] != 'a') {
//					this.matchStat = -1;
//					return false;
//				}
//				if (this.buf[(index++)] != 'l') {
//					this.matchStat = -1;
//					return false;
//				}
//				if (this.buf[(index++)] != 's') {
//					this.matchStat = -1;
//					return false;
//				}
//				if (this.buf[(index++)] != 'e') {
//					this.matchStat = -1;
//					return false;
//				}
//
//				this.bp = index;
//				ch = this.buf[this.bp];
//				value = false;
//			} else {
//				this.matchStat = -1;
//				return false;
//			}
//		}
//		boolean value;
//		if (ch == ',') {
//			ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			this.token = 16;
//		} else if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return false;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return false;
//		}
//
//		return value;
		return false;
	}

	public long scanFieldLong(char[] fieldName) {
//		this.matchStat = 0;
//
//		int fieldNameLength = fieldName.length;
//		for (int i = 0; i < fieldNameLength; i++) {
//			if (fieldName[i] != this.buf[(this.bp + i)]) {
//				this.matchStat = -2;
//				return 0L;
//			}
//		}
//
//		int index = this.bp + fieldNameLength;
//
//		char ch = this.buf[(index++)];
//
//		if ((ch >= '0') && (ch <= '9')) {
//			long value = digits[ch];
//			while (true) {
//				ch = this.buf[(index++)];
//				if ((ch < '0') || (ch > '9'))
//					break;
//				value = value * 10L + digits[ch];
//			}
//			if (ch == '.') {
//				this.token = -1;
//				return 0L;
//			}
//			this.bp = (index - 1);
//
//			if (value < 0L) {
//				this.matchStat = -1;
//				return 0L;
//			}
//		} else {
//			this.matchStat = -1;
//			return 0L;
//		}
//		long value;
//		if (ch == ',') {
//			ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			this.token = 16;
//			return value;
//		}
//		if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return 0L;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return 0L;
//		}
//
//		return value;
		return 0;
	}

	public float scanFieldFloat(char[] fieldName) {
//		this.matchStat = 0;
//
//		int fieldNameLength = fieldName.length;
//		for (int i = 0; i < fieldNameLength; i++) {
//			if (fieldName[i] != this.buf[(this.bp + i)]) {
//				this.matchStat = -2;
//				return 0.0F;
//			}
//		}
//
//		int index = this.bp + fieldNameLength;
//
//		char ch = this.buf[(index++)];
//		float value;
//		if ((ch >= '0') && (ch <= '9')) {
//			int start = index - 1;
//			do
//				ch = this.buf[(index++)];
//			while ((ch >= '0') && (ch <= '9'));
//
//			if (ch == '.') {
//				ch = this.buf[(index++)];
//				if ((ch >= '0') && (ch <= '9')) {
//					while (true) {
//						ch = this.buf[(index++)];
//						if ((ch < '0') || (ch > '9')) {
//							break;
//						}
//					}
//				}
//
//				this.matchStat = -1;
//				return 0.0F;
//			}
//
//			this.bp = (index - 1);
//			String text = new String(this.buf, start, index - start - 1);
//			value = Float.parseFloat(text);
//		} else {
//			this.matchStat = -1;
//			return 0.0F;
//		}
//		float value;
//		if (ch == ',') {
//			ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			this.token = 16;
//			return value;
//		}
//		if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return 0.0F;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return 0.0F;
//		}
//
//		return value;
		return 0;
	}

	public double scanFieldDouble(char[] fieldName) {
//		this.matchStat = 0;
//
//		int fieldNameLength = fieldName.length;
//		for (int i = 0; i < fieldNameLength; i++) {
//			if (fieldName[i] != this.buf[(this.bp + i)]) {
//				this.matchStat = -2;
//				return 0.0D;
//			}
//		}
//
//		int index = this.bp + fieldNameLength;
//
//		char ch = this.buf[(index++)];
//		double value;
//		if ((ch >= '0') && (ch <= '9')) {
//			int start = index - 1;
//			do
//				ch = this.buf[(index++)];
//			while ((ch >= '0') && (ch <= '9'));
//
//			if (ch == '.') {
//				ch = this.buf[(index++)];
//				if ((ch >= '0') && (ch <= '9')) {
//					while (true) {
//						ch = this.buf[(index++)];
//						if ((ch < '0') || (ch > '9')) {
//							break;
//						}
//					}
//				}
//
//				this.matchStat = -1;
//				return 0.0D;
//			}
//
//			this.bp = (index - 1);
//			String text = new String(this.buf, start, index - start - 1);
//			value = Double.parseDouble(text);
//		} else {
//			this.matchStat = -1;
//			return 0.0D;
//		}
//		double value;
//		if (ch == ',') {
//			ch = this.buf[(++this.bp)];
//			this.matchStat = 3;
//			this.token = 16;
//		} else if (ch == '}') {
//			ch = this.buf[(++this.bp)];
//			if (ch == ',') {
//				this.token = 16;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == ']') {
//				this.token = 15;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '}') {
//				this.token = 13;
//				this.ch = this.buf[(++this.bp)];
//			} else if (ch == '\032') {
//				this.token = 20;
//			} else {
//				this.matchStat = -1;
//				return 0.0D;
//			}
//			this.matchStat = 4;
//		} else {
//			this.matchStat = -1;
//			return 0.0D;
//		}
//
//		return value;
		return 0;
	}

	public String scanSymbol(SymbolTable symbolTable) {
		skipWhitespace();

		if (this.ch == '"') {
			return scanSymbol(symbolTable, '"');
		}

		if (this.ch == '\'') {
			if (!isEnabled(Feature.AllowSingleQuotes)) {
				throw new JSONException("syntax error");
			}

			return scanSymbol(symbolTable, '\'');
		}

		if (this.ch == '}') {
			this.ch = this.buf[(++this.bp)];
			this.token = 13;
			return null;
		}

		if (this.ch == ',') {
			this.ch = this.buf[(++this.bp)];
			this.token = 16;
			return null;
		}

		if (this.ch == '\032') {
			this.token = 20;
			return null;
		}

		if (!isEnabled(Feature.AllowUnQuotedFieldNames)) {
			throw new JSONException("syntax error");
		}

		return scanSymbolUnQuoted(symbolTable);
	}

	public final String scanSymbol(SymbolTable symbolTable, char quote) {
		int hash = 0;

		this.np = this.bp;
		this.sp = 0;
		boolean hasSpecial = false;
		while (true) {
			char ch = this.buf[(++this.bp)];

			if (ch == quote) {
				break;
			}
			if (ch == '\032') {
				throw new JSONException("unclosed.str");
			}

			if (ch == '\\') {
				if (!hasSpecial) {
					hasSpecial = true;

					if (this.sp >= this.sbuf.length) {
						int newCapcity = this.sbuf.length * 2;
						if (this.sp > newCapcity) {
							newCapcity = this.sp;
						}
						char[] newsbuf = new char[newCapcity];
						System.arraycopy(this.sbuf, 0, newsbuf, 0,
								this.sbuf.length);
						this.sbuf = newsbuf;
					}

					System.arraycopy(this.buf, this.np + 1, this.sbuf, 0,
							this.sp);
				}

				ch = this.buf[(++this.bp)];

				switch (ch) {
				case '"':
					hash = 31 * hash + 34;
					putChar('"');
					break;
				case '\\':
					hash = 31 * hash + 92;
					putChar('\\');
					break;
				case '/':
					hash = 31 * hash + 47;
					putChar('/');
					break;
				case 'b':
					hash = 31 * hash + 8;
					putChar('\b');
					break;
				case 'F':
				case 'f':
					hash = 31 * hash + 12;
					putChar('\f');
					break;
				case 'n':
					hash = 31 * hash + 10;
					putChar('\n');
					break;
				case 'r':
					hash = 31 * hash + 13;
					putChar('\r');
					break;
				case 't':
					hash = 31 * hash + 9;
					putChar('\t');
					break;
				case 'u':
					char c1 = ch = this.buf[(++this.bp)];
					char c2 = ch = this.buf[(++this.bp)];
					char c3 = ch = this.buf[(++this.bp)];
					char c4 = ch = this.buf[(++this.bp)];
					int val = Integer.parseInt(new String(new char[] { c1, c2,
							c3, c4 }), 16);
					hash = 31 * hash + val;
					putChar((char) val);
					break;
				default:
					this.ch = ch;
					throw new JSONException("unclosed.str.lit");
				}
			} else {
				hash = 31 * hash + ch;

				if (!hasSpecial) {
					this.sp += 1;
				} else if (this.sp == this.sbuf.length)
					putChar(ch);
				else
					this.sbuf[(this.sp++)] = ch;
			}
		}
		char ch;
		this.token = 4;
		this.ch = this.buf[(++this.bp)];

		if (!hasSpecial) {
			return symbolTable.addSymbol(this.buf, this.np + 1, this.sp, hash);
		}
		return symbolTable.addSymbol(this.sbuf, 0, this.sp, hash);
	}

	public void scanTrue() {
		if (this.buf[(this.bp++)] != 't') {
			throw new JSONException("error parse true");
		}
		if (this.buf[(this.bp++)] != 'r') {
			throw new JSONException("error parse true");
		}
		if (this.buf[(this.bp++)] != 'u') {
			throw new JSONException("error parse true");
		}
		if (this.buf[(this.bp++)] != 'e') {
			throw new JSONException("error parse true");
		}

		this.ch = this.buf[this.bp];

		if ((this.ch == ' ') || (this.ch == ',') || (this.ch == '}')
				|| (this.ch == ']') || (this.ch == '\n') || (this.ch == '\r')
				|| (this.ch == '\t') || (this.ch == '\032')
				|| (this.ch == '\f') || (this.ch == '\b'))
			this.token = 6;
		else
			throw new JSONException("scan true error");
	}

	public void scanNullOrNew() {
		if (this.buf[(this.bp++)] != 'n') {
			throw new JSONException("error parse null or new");
		}

		if (this.buf[this.bp] == 'u') {
			this.bp += 1;
			if (this.buf[(this.bp++)] != 'l') {
				throw new JSONException("error parse true");
			}
			if (this.buf[(this.bp++)] != 'l') {
				throw new JSONException("error parse true");
			}
			this.ch = this.buf[this.bp];

			if ((this.ch == ' ') || (this.ch == ',') || (this.ch == '}')
					|| (this.ch == ']') || (this.ch == '\n')
					|| (this.ch == '\r') || (this.ch == '\t')
					|| (this.ch == '\032') || (this.ch == '\f')
					|| (this.ch == '\b'))
				this.token = 8;
			else {
				throw new JSONException("scan true error");
			}
			return;
		}

		if (this.buf[this.bp] != 'e') {
			throw new JSONException("error parse e");
		}

		this.bp += 1;
		if (this.buf[(this.bp++)] != 'w') {
			throw new JSONException("error parse w");
		}
		this.ch = this.buf[this.bp];

		if ((this.ch == ' ') || (this.ch == ',') || (this.ch == '}')
				|| (this.ch == ']') || (this.ch == '\n') || (this.ch == '\r')
				|| (this.ch == '\t') || (this.ch == '\032')
				|| (this.ch == '\f') || (this.ch == '\b'))
			this.token = 9;
		else
			throw new JSONException("scan true error");
	}

	public void scanFalse() {
		if (this.buf[(this.bp++)] != 'f') {
			throw new JSONException("error parse false");
		}
		if (this.buf[(this.bp++)] != 'a') {
			throw new JSONException("error parse false");
		}
		if (this.buf[(this.bp++)] != 'l') {
			throw new JSONException("error parse false");
		}
		if (this.buf[(this.bp++)] != 's') {
			throw new JSONException("error parse false");
		}
		if (this.buf[(this.bp++)] != 'e') {
			throw new JSONException("error parse false");
		}

		this.ch = this.buf[this.bp];

		if ((this.ch == ' ') || (this.ch == ',') || (this.ch == '}')
				|| (this.ch == ']') || (this.ch == '\n') || (this.ch == '\r')
				|| (this.ch == '\t') || (this.ch == '\032')
				|| (this.ch == '\f') || (this.ch == '\b'))
			this.token = 7;
		else
			throw new JSONException("scan false error");
	}

	public void scanIdent() {
		this.np = (this.bp - 1);
		this.hasSpecial = false;
		do {
			this.sp += 1;

			this.ch = this.buf[(++this.bp)];
		} while (Character.isLetterOrDigit(this.ch));

		String ident = stringVal();

		Integer tok = this.keywods.getKeyword(ident);
		if (tok != null)
			this.token = tok.intValue();
		else
			this.token = 18;
	}

	public void scanNumber() {
		this.np = this.bp;

		if (this.ch == '-') {
			this.sp += 1;
			this.ch = this.buf[(++this.bp)];
		}

		while ((this.ch >= '0') && (this.ch <= '9')) {
			this.sp += 1;

			this.ch = this.buf[(++this.bp)];
		}

		boolean isDouble = false;

		if (this.ch == '.') {
			this.sp += 1;
			this.ch = this.buf[(++this.bp)];
			isDouble = true;

			while ((this.ch >= '0') && (this.ch <= '9')) {
				this.sp += 1;

				this.ch = this.buf[(++this.bp)];
			}
		}

		if ((this.ch == 'e') || (this.ch == 'E')) {
			this.sp += 1;
			this.ch = this.buf[(++this.bp)];

			if ((this.ch == '+') || (this.ch == '-')) {
				this.sp += 1;
				this.ch = this.buf[(++this.bp)];
			}

			while ((this.ch >= '0') && (this.ch <= '9')) {
				this.sp += 1;

				this.ch = this.buf[(++this.bp)];
			}

			isDouble = true;
		}

		if (isDouble)
			this.token = 3;
		else
			this.token = 2;
	}

	private final void putChar(char ch) {
		if (this.sp == this.sbuf.length) {
			char[] newsbuf = new char[this.sbuf.length * 2];
			System.arraycopy(this.sbuf, 0, newsbuf, 0, this.sbuf.length);
			this.sbuf = newsbuf;
		}
		this.sbuf[(this.sp++)] = ch;
	}

	public final int pos() {
		return this.pos;
	}

	public final String stringVal() {
		if (!this.hasSpecial) {
			return new String(this.buf, this.np + 1, this.sp);
		}
		return new String(this.sbuf, 0, this.sp);
	}

	public final String symbol(SymbolTable symbolTable) {
		if (symbolTable == null) {
			if (!this.hasSpecial) {
				return new String(this.buf, this.np + 1, this.sp);
			}
			return new String(this.sbuf, 0, this.sp);
		}

		if (!this.hasSpecial) {
			return symbolTable.addSymbol(this.buf, this.np + 1, this.sp);
		}
		return symbolTable.addSymbol(this.sbuf, 0, this.sp);
	}

	public Number integerValue() throws NumberFormatException {
		long result = 0L;
		boolean negative = false;
		int i = this.np;
		int max = this.np + this.sp;
		long limit;
		if (this.buf[this.np] == '-') {
			negative = true;
			 limit = -9223372036854775808L;
			i++;
		} else {
			limit = -9223372036854775807L;
		}
		long multmin = negative ? -922337203685477580L : -922337203685477580L;
		if (i < max) {
			int digit = digits[this.buf[(i++)]];
			result = -digit;
		}
		while (i < max) {
			int digit = digits[this.buf[(i++)]];
			if (result < multmin) {
				return new BigInteger(numberString());
			}
			result *= 10L;
			if (result < limit + digit) {
				return new BigInteger(numberString());
			}
			result -= digit;
		}

		if (negative) {
			if (i > this.np + 1) {
				if (result >= -2147483648L) {
					return Integer.valueOf((int) result);
				}
				return Long.valueOf(result);
			}
			throw new NumberFormatException(numberString());
		}

		result = -result;
		if (result <= 2147483647L) {
			return Integer.valueOf((int) result);
		}
		return Long.valueOf(result);
	}

	public long longValue() throws NumberFormatException {
		long result = 0L;
		boolean negative = false;
		int i = this.np;
		int max = this.np + this.sp;
		long limit;
		if (this.buf[this.np] == '-') {
			negative = true;
			 limit = -9223372036854775808L;
			i++;
		} else {
			limit = -9223372036854775807L;
		}
		long multmin = negative ? -922337203685477580L : -922337203685477580L;
		if (i < max) {
			int digit = digits[this.buf[(i++)]];
			result = -digit;
		}
		while (i < max) {
			int digit = digits[this.buf[(i++)]];
			if (result < multmin) {
				throw new NumberFormatException(numberString());
			}
			result *= 10L;
			if (result < limit + digit) {
				throw new NumberFormatException(numberString());
			}
			result -= digit;
		}

		if (negative) {
			if (i > this.np + 1) {
				return result;
			}
			throw new NumberFormatException(numberString());
		}

		return -result;
	}

	public int intValue() {
		int result = 0;
		boolean negative = false;
		int i = this.np;
		int max = this.np + this.sp;
		int limit;
		if (this.buf[this.np] == '-') {
			negative = true;
			 limit = -2147483648;
			i++;
		} else {
			limit = -2147483647;
		}
		int multmin = negative ? -214748364 : -214748364;
		if (i < max) {
			int digit = digits[this.buf[(i++)]];
			result = -digit;
		}
		while (i < max) {
			int digit = digits[this.buf[(i++)]];
			if (result < multmin) {
				throw new NumberFormatException(numberString());
			}
			result *= 10;
			if (result < limit + digit) {
				throw new NumberFormatException(numberString());
			}
			result -= digit;
		}

		if (negative) {
			if (i > this.np + 1) {
				return result;
			}
			throw new NumberFormatException(numberString());
		}

		return -result;
	}

	public final String numberString() {
		return new String(this.buf, this.np, this.sp);
	}

	public float floatValue() {
		return Float.parseFloat(numberString());
	}

	public double doubleValue() {
		return Double.parseDouble(numberString());
	}

	public BigDecimal decimalValue() {
		return new BigDecimal(this.buf, this.np, this.sp);
	}

	public void config(Feature feature, boolean state) {
		this.features = Feature.config(this.features, feature, state);
	}

	public boolean isEnabled(Feature feature) {
		return Feature.isEnabled(this.features, feature);
	}

	public boolean scanISO8601DateIfMatch() {
		int rest = this.buflen - this.bp;

		if (rest < this.ISO8601_LEN_0) {
			return false;
		}

		char y0 = this.buf[this.bp];
		char y1 = this.buf[(this.bp + 1)];
		char y2 = this.buf[(this.bp + 2)];
		char y3 = this.buf[(this.bp + 3)];
		if ((y0 != '1') && (y0 != '2')) {
			return false;
		}
		if ((y1 < '0') || (y1 > '9')) {
			return false;
		}
		if ((y2 < '0') || (y2 > '9')) {
			return false;
		}
		if ((y3 < '0') || (y3 > '9')) {
			return false;
		}

		if (this.buf[(this.bp + 4)] != '-') {
			return false;
		}

		char M0 = this.buf[(this.bp + 5)];
		char M1 = this.buf[(this.bp + 6)];
		if (M0 == '0') {
			if ((M1 < '1') || (M1 > '9'))
				return false;
		} else if (M0 == '1') {
			if ((M1 != '0') && (M1 != '1') && (M1 != '2'))
				return false;
		} else {
			return false;
		}

		if (this.buf[(this.bp + 7)] != '-') {
			return false;
		}

		char d0 = this.buf[(this.bp + 8)];
		char d1 = this.buf[(this.bp + 9)];
		if (d0 == '0') {
			if ((d1 < '1') || (d1 > '9'))
				return false;
		} else if ((d0 == '1') || (d0 == '2')) {
			if ((d1 < '0') || (d1 > '9'))
				return false;
		} else if (d0 == '3') {
			if ((d1 != '0') && (d1 != '1'))
				return false;
		} else {
			return false;
		}

		this.calendar = Calendar.getInstance();
		int year = digits[y0] * 1000 + digits[y1] * 100 + digits[y2] * 10
				+ digits[y3];
		int month = digits[M0] * 10 + digits[M1] - 1;
		int day = digits[d0] * 10 + digits[d1];
		this.calendar.set(1, year);
		this.calendar.set(2, month);
		this.calendar.set(5, day);

		char t = this.buf[(this.bp + 10)];
		if (t == 'T') {
			if (rest < this.ISO8601_LEN_1)
				return false;
		} else {
			this.calendar.set(11, 0);
			this.calendar.set(12, 0);
			this.calendar.set(13, 0);
			this.calendar.set(14, 0);

			this.ch = this.buf[(this.bp += 10)];

			this.token = 5;
			return true;
		}

		char h0 = this.buf[(this.bp + 11)];
		char h1 = this.buf[(this.bp + 12)];
		if (h0 == '0') {
			if ((h1 < '0') || (h1 > '9'))
				return false;
		} else if (h0 == '1') {
			if ((h1 < '0') || (h1 > '9'))
				return false;
		} else if (h0 == '2') {
			if ((h1 < '0') || (h1 > '4'))
				return false;
		} else {
			return false;
		}

		if (this.buf[(this.bp + 13)] != ':') {
			return false;
		}

		char m0 = this.buf[(this.bp + 14)];
		char m1 = this.buf[(this.bp + 15)];
		if ((m0 >= '0') && (m0 <= '5')) {
			if ((m1 < '0') || (m1 > '9'))
				return false;
		} else if (m0 == '6') {
			if (m1 != '0')
				return false;
		} else {
			return false;
		}

		if (this.buf[(this.bp + 16)] != ':') {
			return false;
		}

		char s0 = this.buf[(this.bp + 17)];
		char s1 = this.buf[(this.bp + 18)];
		if ((s0 >= '0') && (s0 <= '5')) {
			if ((s1 < '0') || (s1 > '9'))
				return false;
		} else if (s0 == '6') {
			if (s1 != '0')
				return false;
		} else {
			return false;
		}

		int hour = digits[h0] * 10 + digits[h1];
		int minute = digits[m0] * 10 + digits[m1];
		int seconds = digits[s0] * 10 + digits[s1];
		this.calendar.set(11, hour);
		this.calendar.set(12, minute);
		this.calendar.set(13, seconds);

		char dot = this.buf[(this.bp + 19)];
		if (dot == '.') {
			if (rest < this.ISO8601_LEN_2)
				return false;
		} else {
			this.calendar.set(14, 0);

			this.ch = this.buf[(this.bp += 19)];

			this.token = 5;
			return true;
		}

		char S0 = this.buf[(this.bp + 20)];
		char S1 = this.buf[(this.bp + 21)];
		char S2 = this.buf[(this.bp + 22)];
		if ((S0 < '0') || (S0 > '9')) {
			return false;
		}
		if ((S1 < '0') || (S1 > '9')) {
			return false;
		}
		if ((S2 < '0') || (S2 > '9')) {
			return false;
		}

		int millis = digits[S0] * 100 + digits[S1] * 10 + digits[S2];
		this.calendar.set(14, millis);

		this.ch = this.buf[(this.bp += 23)];

		this.token = 5;
		return true;
	}

	public Calendar getCalendar() {
		return this.calendar;
	}

	public boolean isEOF() {
		switch (this.token) {
		case 20:
			return true;
		case 1:
			return false;
		case 13:
			return false;
		}
		return false;
	}
}