package com.zving.framework.utility;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.security.VerifyRule;
import com.zving.framework.utility.commons.ArrayUtils;
import com.zving.framework.utility.commons.StringEscapeUtils;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class StringUtil {
	public static final byte[] BOM = { -17, -69, -65 };

	public static final char[] HexDigits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static final Pattern PTitle = Pattern.compile(
			"<title>(.+?)</title>", 34);

	public static Pattern patternHtmlTag = Pattern.compile("<[^<>]+>", 32);

	public static String regEx_comment = "<\\!\\-\\-.*?\\-\\->";
	public static String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
	public static String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
	public static String regEx_html = "<[^>]+>";

	public static final Pattern PLetterOrDigit = Pattern.compile("^\\w*$", 34);

	public static final Pattern PLetter = Pattern.compile("^[A-Za-z]*$", 34);

	public static final Pattern PDigit = Pattern.compile("^\\d*$", 34);

	private static Pattern chinesePattern = Pattern.compile("[^一-龥]+", 34);

	private static Pattern idPattern = Pattern
			.compile("[\\w\\s\\_\\.\\,]*", 34);

	public static byte[] md5(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			return md5.digest(src.getBytes());
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] md5(byte[] src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			return md5.digest(src);
		} catch (Exception e) {
		}
		return null;
	}

	public static String md5Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md = md5.digest(src.getBytes());
			return hexEncode(md);
		} catch (Exception e) {
		}
		return null;
	}

	public static String sha1Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("SHA1");
			byte[] md = md5.digest(src.getBytes());
			return hexEncode(md);
		} catch (Exception e) {
		}
		return null;
	}

	public static String md5Base64(String str) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			return base64Encode(md5.digest(str.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String md5Base64FromHex(String md5str) {
		char[] cs = md5str.toCharArray();
		byte[] bs = new byte[16];
		for (int i = 0; i < bs.length; i++) {
			char c1 = cs[(i * 2)];
			char c2 = cs[(i * 2 + 1)];
			byte m1 = 0;
			byte m2 = 0;
			for (byte k = 0; k < 16; k = (byte) (k + 1)) {
				if (HexDigits[k] == c1) {
					m1 = k;
				}
				if (HexDigits[k] == c2) {
					m2 = k;
				}
			}
			bs[i] = ((byte) (m1 << 4 | 0 + m2));
		}

		String newstr = base64Encode(bs);
		return newstr;
	}

	public static String md5HexFromBase64(String base64str) {
		return hexEncode(base64Decode(base64str));
	}

	public static String hexEncode(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		int i = 0;
		for (int j = 0; i < l; i++) {
			out[(j++)] = HexDigits[((0xF0 & data[i]) >>> 4)];
			out[(j++)] = HexDigits[(0xF & data[i])];
		}
		return new String(out);
	}

	public static byte[] hexDecode(String str) {
		char[] data = str.toCharArray();
		int len = data.length;
		if ((len & 0x1) != 0) {
			throw new UtilityException(
					"StringUtil.hexEncode:Odd number of characters.");
		}

		byte[] out = new byte[len >> 1];
		int i = 0;
		for (int j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f |= toDigit(data[j], j);
			j++;
			out[i] = ((byte) (f & 0xFF));
		}

		return out;
	}

	private static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new UtilityException(
					"StringUtil.hexDecode:Illegal hexadecimal character " + ch
							+ " at index " + index);
		}
		return digit;
	}

	public static String byteToBin(byte[] bs) {
		char[] cs = new char[bs.length * 9];
		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			int j = i * 9;
			cs[j] = ((b >>> 7 & 0x1) == 1 ? 49 : '0');
			cs[(j + 1)] = ((b >>> 6 & 0x1) == 1 ? 49 : '0');
			cs[(j + 2)] = ((b >>> 5 & 0x1) == 1 ? 49 : '0');
			cs[(j + 3)] = ((b >>> 4 & 0x1) == 1 ? 49 : '0');
			cs[(j + 4)] = ((b >>> 3 & 0x1) == 1 ? 49 : '0');
			cs[(j + 5)] = ((b >>> 2 & 0x1) == 1 ? 49 : '0');
			cs[(j + 6)] = ((b >>> 1 & 0x1) == 1 ? 49 : '0');
			cs[(j + 7)] = ((b & 0x1) == 1 ? 49 : '0');
			cs[(j + 8)] = ',';
		}
		return new String(cs);
	}

	public static String byteArrayToHexString(byte[] b) {
		FastStringBuilder resultSb = new FastStringBuilder();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
			resultSb.append(" ");
		}
		return resultSb.toStringAndClose();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n += 256;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return (HexDigits[d1] + HexDigits[d2]) + "";
	}

	public static boolean isUTF8(byte[] bs) {
		if (hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf")) {
			return true;
		}
		int encodingBytesCount = 0;
		for (int i = 0; i < bs.length; i++) {
			byte c = bs[i];
			if (encodingBytesCount == 0) {
				if ((c & 0x80) != 0) {
					if ((c & 0xC0) == 192) {
						encodingBytesCount = 1;
						c = (byte) (c << 2);

						while ((c & 0x80) == 128) {
							c = (byte) (c << 1);
							encodingBytesCount++;
						}
					} else {
						return false;
					}
				}
			} else if ((c & 0xC0) == 128)
				encodingBytesCount--;
			else {
				return false;
			}
		}

		if (encodingBytesCount != 0) {
			return false;
		}
		return true;
	}

	public static String base64Encode(byte[] b) {
		if (b == null) {
			return null;
		}
		return new BASE64Encoder().encode(b);
	}

	public static byte[] base64Decode(String s) {
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				return decoder.decodeBuffer(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String javaEncode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "\\", "\\\\");
		txt = replaceEx(txt, "\n", "\\n");
		txt = replaceEx(txt, "\r\n", "\n");
		txt = replaceEx(txt, "\r", "\\r");
		txt = replaceEx(txt, "\t", "\\t");
		txt = replaceEx(txt, "\f", "\\f");
		txt = replaceEx(txt, "\"", "\\\"");
		txt = replaceEx(txt, "'", "\\'");
		return txt;
	}

	public static String javaDecode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		FastStringBuilder sb = new FastStringBuilder();
		int lastIndex = 0;
		while (true) {
			int index = txt.indexOf("\\", lastIndex);
			if (index < 0) {
				break;
			}
			sb.append(txt.substring(lastIndex, index));
			if (index < txt.length() - 1) {
				char c = txt.charAt(index + 1);
				if (c == 'n')
					sb.append("\n");
				else if (c == 'r')
					sb.append("\r");
				else if (c == 't')
					sb.append("\t");
				else if (c == 'f')
					sb.append("\f");
				else if (c == '\'')
					sb.append("'");
				else if (c == '"')
					sb.append("\"");
				else if (c == '\\') {
					sb.append("\\");
				}
				lastIndex = index + 2;
			} else {
				sb.append(txt.substring(index, index + 1));

				lastIndex = index + 1;
			}
		}
		sb.append(txt.substring(lastIndex));
		return sb.toStringAndClose();
	}

	public static String[] splitEx(String str, String spliter) {
		char escapeChar = '\\';
		if (spliter.equals("\\")) {
			escapeChar = '&';
		}
		return splitEx(str, spliter, escapeChar);
	}

	public static String[] splitEx(String str, String spliter, char escapeChar) {
		if (str == null) {
			return null;
		}
		ArrayList list = new ArrayList();
		if ((spliter == null) || (spliter.equals(""))
				|| (str.length() < spliter.length())) {
			return new String[] { str };
		}
		int length = spliter.length();
		int lastIndex = 0;
		int lastStart = 0;
		while (true) {
			int i = str.indexOf(spliter, lastIndex);
			if (i < 0)
				break;
			if ((i > 0) && (str.charAt(i - 1) == escapeChar)) {
				lastIndex = i + 1;
			} else {
				list.add(str.substring(lastStart, i));
				lastStart = lastIndex = i + length;
			}
		}
		if (lastStart <= str.length()) {
			list.add(str.substring(lastStart));
		}

		String[] arr = new String[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ((String) list.get(i));
		}
		return arr;
	}

	public static String replaceEx(String str, String subStr, String reStr) {
		if (str == null) {
			return null;
		}
		if ((subStr == null) || (subStr.equals(""))
				|| (subStr.length() > str.length()) || (reStr == null)) {
			return str;
		}
		FastStringBuilder sb = new FastStringBuilder();
		int lastIndex = 0;
		while (true) {
			int index = str.indexOf(subStr, lastIndex);
			if (index < 0) {
				break;
			}
			sb.append(str.substring(lastIndex, index));
			sb.append(reStr);

			lastIndex = index + subStr.length();
		}
		sb.append(str.substring(lastIndex));
		return sb.toStringAndClose();
	}

	public static String replaceAllIgnoreCase(String source, String oldstring,
			String newstring) {
		Pattern p = Pattern.compile(oldstring, 34);
		Matcher m = p.matcher(source);
		return m.replaceAll(newstring);
	}

	public static String urlEncode(String str) {
		return urlEncode(str, Config.getGlobalCharset());
	}

	public static String urlDecode(String str) {
		return urlDecode(str, Config.getGlobalCharset());
	}

	public static String urlEncode(String str, String charset) {
		try {
			return URLEncoder.encode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String urlDecode(String str, String charset) {
		try {
			return URLDecoder.decode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String htmlEncode(String txt) {
		return StringEscapeUtils.escapeHtml4(txt);
	}

	public static String htmlDecode(String txt) {
		txt = replaceEx(txt, "&#8226;", "·");
		return StringEscapeUtils.unescapeHtml4(txt);
	}

	public static String quotEncode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "&", "&amp;");
		txt = replaceEx(txt, "\"", "&quot;");
		return txt;
	}

	public static String quotDecode(String txt) {
		if ((txt == null) || (txt.length() == 0)) {
			return txt;
		}
		txt = replaceEx(txt, "&quot;", "\"");
		txt = replaceEx(txt, "&amp;", "&");
		return txt;
	}

	public static String escape(String src) {
		FastStringBuilder sb = new FastStringBuilder();
		for (int i = 0; i < src.length(); i++) {
			char j = src.charAt(i);
			if ((Character.isDigit(j)) || (Character.isLowerCase(j))
					|| (Character.isUpperCase(j))) {
				sb.append(j);
			} else if (j < 'Ā') {
				sb.append("%");
				if (j < '\020') {
					sb.append("0");
				}
				sb.append(Integer.toString(j, 16));
			} else {
				sb.append("%u");
				sb.append(Integer.toString(j, 16));
			}
		}
		return sb.toStringAndClose();
	}

	public static String unescape(String src) {
		FastStringBuilder sb = new FastStringBuilder();
		int lastPos = 0;
		int pos = 0;

		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					char ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					sb.append(ch);
					lastPos = pos + 6;
				} else {
					char ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					sb.append(ch);
					lastPos = pos + 3;
				}
			} else if (pos == -1) {
				sb.append(src.substring(lastPos));
				lastPos = src.length();
			} else {
				sb.append(src.substring(lastPos, pos));
				lastPos = pos;
			}
		}

		return sb.toStringAndClose();
	}

	public static String leftPad(String srcString, char c, int length) {
		if (srcString == null) {
			srcString = "";
		}
		int tLen = srcString.length();

		if (tLen >= length)
			return srcString;
		int iMax = length - tLen;
		FastStringBuilder sb = new FastStringBuilder();
		for (int i = 0; i < iMax; i++) {
			sb.append(c);
		}
		sb.append(srcString);
		return sb.toStringAndClose();
	}

	public static String subString(String src, int length) {
		if (src == null) {
			return null;
		}
		int i = src.length();
		if (i > length) {
			return src.substring(0, length);
		}
		return src;
	}

	public static String subStringEx(String src, int length) {
		if (src == null) {
			return null;
		}
		int m = 0;
		boolean unixFlag = false;
		String osname = System.getProperty("os.name").toLowerCase();
		if ((osname.indexOf("sunos") > 0)
				|| (osname.indexOf("solaris") > 0)
				|| (osname.indexOf("aix") > 0)
				|| ((osname.indexOf("windows") >= 0) && (osname.indexOf("7") > 0)))
			unixFlag = true;
		try {
			byte[] b = src.getBytes("Unicode");
			for (int i = 2; i < b.length; i += 2) {
				byte flag = b[(i + 1)];
				if (unixFlag) {
					flag = b[i];
				}
				if (flag == 0)
					m++;
				else {
					m += 2;
				}
				if (m > length)
					return src.substring(0, (i - 2) / 2);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("String.getBytes(\"Unicode\") failed");
		}
		return src;
	}

	public static int lengthEx(String src) {
		return lengthEx(src, false);
	}

	public static int lengthEx(String src, boolean UTF8Flag) {
		if (ObjectUtil.empty(src)) {
			return 0;
		}
		int length = 0;
		boolean bigFlag = true;
		try {
			byte[] b = src.getBytes("Unicode");
			if (b[0] == -2) {
				bigFlag = false;
			}
			for (int i = 2; i < b.length; i += 2) {
				byte flag = b[(i + 1)];
				if (!bigFlag) {
					flag = b[i];
				}
				if (flag == 0) {
					length++;
				} else if (UTF8Flag)
					length += 3;
				else
					length += 2;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("String.getBytes(\"Unicode\") failed");
		}
		return length;
	}

	public static String rightPad(String srcString, char c, int length) {
		if (srcString == null) {
			srcString = "";
		}
		int tLen = srcString.length();

		if (tLen >= length)
			return srcString;
		int iMax = length - tLen;
		FastStringBuilder sb = new FastStringBuilder();
		sb.append(srcString);
		for (int i = 0; i < iMax; i++) {
			sb.append(c);
		}
		return sb.toStringAndClose();
	}

	public static boolean verify(String value, String rule) {
		VerifyRule vr = new VerifyRule(rule);
		boolean flag = vr.verify(value);
		return flag;
	}

	public static String rightTrim(String src) {
		if (src != null) {
			char[] chars = src.toCharArray();
			for (int i = chars.length - 1; i >= 0; i--) {
				if ((chars[i] != ' ') && (chars[i] != '\t')
						&& (chars[i] != '\r')) {
					return src.substring(0, i + 1);
				}
			}
			return "";
		}
		return src;
	}

	public static void printStringWithAnyCharset(String str) {
		SortedMap<String, Charset> map = Charset.availableCharsets();

		for (String key1 : map.keySet()) {
			LogUtil.info(key1);
			for (String key2 : map.keySet()) {
				System.out.print("\t");
				try {
					System.out.println("From "
							+ key1
							+ " To "
							+ key2
							+ ":"
							+ new String(str.getBytes(key1.toString()), key2
									.toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static String toSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++)
			if (c[i] == ' ') {
				c[i] = '　';
			} else if (((c[i] <= '@') || (c[i] >= '['))
					&& ((c[i] <= '`') || (c[i] >= '{'))) {
				if (c[i] < '')
					c[i] = ((char) (c[i] + 65248));
			}
		return new String(c);
	}

	public static String toNSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '　';
			} else if (c[i] < '')
				c[i] = ((char) (c[i] + 65248));
		}
		return new String(c);
	}

	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '　') {
				c[i] = ' ';
			} else if ((c[i] > 65280) && (c[i] < 65375))
				c[i] = ((char) (c[i] - 65248));
		}
		return new String(c);
	}

	public static String getChineseFullSpelling(String cnStr) {
		if ((cnStr == null) || ("".equals(cnStr.trim()))) {
			return cnStr;
		}
		return ChineseSpelling.getSpelling(cnStr);
	}

	public static String getChinesePersonNameSpelling(String cnStr) {
		if ((cnStr == null) || ("".equals(cnStr.trim()))) {
			return cnStr;
		}
		return ChineseSpelling.getPersonNameSpelling(cnStr);
	}

	public static String getChineseCapitalizedSpelling(String cnStr) {
		if ((cnStr == null) || ("".equals(cnStr.trim()))) {
			return cnStr;
		}
		return ChineseSpelling.getCapitalizedSpelling(cnStr);
	}

	public static String getHtmlTitle(File f) {
		String html = FileUtil.readText(f);
		String title = getHtmlTitle(html);
		return title;
	}

	public static String getHtmlTitle(String html) {
		Matcher m = PTitle.matcher(html);
		if (m.find()) {
			return m.group(1).trim();
		}
		return null;
	}

	public static String clearHtmlTag(String htmlStr) {
		return clearHtmlTag(htmlStr, true);
	}

	public static String clearHtmlTag(String htmlStr, boolean cleanSpace) {
		Pattern p_script = Pattern.compile(regEx_script, 2);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll("");

		Pattern p_style = Pattern.compile(regEx_style, 2);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll("");

		Pattern p_html = Pattern.compile(regEx_html, 2);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll("");

		Pattern p_comment = Pattern.compile(regEx_comment, 2);
		Matcher m_comment = p_comment.matcher(htmlStr);
		htmlStr = m_comment.replaceAll("");

		htmlStr = htmlStr.trim();
		htmlStr = htmlDecode(htmlStr);

		if (cleanSpace) {
			htmlStr = htmlStr.replaceAll("[\\s　]{2,}", " ");
		}
		return htmlStr;
	}

	public static String htmlTagEncode(String str) {
		if (isEmpty(str))
			return str;
		while (true) {
			Matcher matcher = patternHtmlTag.matcher(str);
			if (!matcher.find())
				break;
			String content = matcher.group(0);
			str = matcher.replaceFirst(htmlEncode(content));
		}

		return str.replaceAll("[\\s　]{2,}", " ");
	}

	public static String capitalize(String str) {
		if ((str == null) || (str.length() == 0)) {
			return str;
		}
		return new FastStringBuilder()
				.append(Character.toTitleCase(str.charAt(0)))
				.append(str.substring(1)).toStringAndClose();
	}

	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isNull(String str) {
		return (isEmpty(str)) || ("null".equals(str));
	}

	public static boolean isNotNull(String str) {
		return (isNotEmpty(str)) && (!"null".equals(str));
	}

	public static final String noNull(String string, String defaultString) {
		return isEmpty(string) ? defaultString : string;
	}

	public static final String noNull(String string) {
		return noNull(string, "");
	}

	public static String join(Object[] arr) {
		return join(arr, ",");
	}

	public static String join(Object[][] arr) {
		return join(arr, "\n", ",");
	}

	public static String join(Object[] arr, String spliter) {
		if (arr == null) {
			return null;
		}
		FastStringBuilder sb = new FastStringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i != 0) {
				sb.append(spliter);
			}
			sb.append(arr[i]);
		}
		return sb.toStringAndClose();
	}

	public static String join(Object[][] arr, String spliter1, String spliter2) {
		if (arr == null) {
			return null;
		}
		FastStringBuilder sb = new FastStringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i != 0) {
				sb.append(spliter2);
			}
			sb.append(join(arr[i], spliter2));
		}
		return sb.toStringAndClose();
	}

	public static String join(List<?> list) {
		return join(list, ",");
	}

	public static String join(List<?> list, String spliter) {
		if (list == null) {
			return null;
		}
		FastStringBuilder sb = new FastStringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				sb.append(spliter);
			}
			sb.append(list.get(i));
		}
		return sb.toStringAndClose();
	}

	public static int count(String str, String findStr) {
		int lastIndex = 0;
		int length = findStr.length();
		int count = 0;
		int start = 0;
		while ((start = str.indexOf(findStr, lastIndex)) >= 0) {
			lastIndex = start + length;
			count++;
		}
		return count;
	}

	public static boolean isLetterOrDigit(String str) {
		return PLetterOrDigit.matcher(str).find();
	}

	public static boolean isLetter(String str) {
		return PLetter.matcher(str).find();
	}

	public static boolean isDigit(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return PDigit.matcher(str).find();
	}

	public static boolean containsChinese(String str) {
		if (!chinesePattern.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	public static boolean checkID(String str) {
		if (isEmpty(str)) {
			return true;
		}
		if (idPattern.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	public static Mapx<String, String> splitToMapx(String str,
			String entrySpliter, String keySpliter) {
		return splitToMapx(str, entrySpliter, keySpliter, '\\');
	}

	public static Mapx<String, String> splitToMapx(String str,
			String entrySpliter, String keySpliter, char escapeChar) {
		Mapx map = new Mapx();
		String[] list = splitEx(str, entrySpliter, escapeChar);
		for (int i = 0; i < list.length; i++) {
			String[] list2 = splitEx(list[i], keySpliter, escapeChar);
			String key = list2[0];
			if (!isEmpty(key)) {
				key = key.trim();
				String value = null;
				if (list2.length > 1) {
					value = list2[1];
				}
				map.put(key, value);
			}
		}
		return map;
	}

	public static String getURLExtName(String url) {
		if (isEmpty(url)) {
			return null;
		}
		int index1 = url.indexOf('?');
		if (index1 == -1) {
			index1 = url.length();
		}
		int index2 = url.lastIndexOf('.', index1);
		if (index2 == -1) {
			return null;
		}
		int index3 = url.indexOf('/', 8);
		if (index3 == -1) {
			return null;
		}
		String ext = url.substring(index2 + 1, index1);
		if (ext.matches("[^\\/\\\\]*")) {
			return ext;
		}
		return null;
	}

	public static String getURLFileName(String url) {
		if (isEmpty(url)) {
			return null;
		}
		int index1 = url.indexOf('?');
		if (index1 == -1) {
			index1 = url.length();
		}
		int index2 = url.lastIndexOf('/', index1);
		if ((index2 == -1) || (index2 < 8)) {
			return null;
		}
		String ext = url.substring(index2 + 1, index1);
		return ext;
	}

	public static byte[] GBKToUTF8(String chinese) {
		return GBKToUTF8(chinese, false);
	}

	public static byte[] GBKToUTF8(String chinese, boolean bomFlag) {
		return CharsetConvert.GBKToUTF8(chinese, bomFlag);
	}

	public static byte[] UTF8ToGBK(String chinese) {
		return CharsetConvert.UTF8ToGBK(chinese);
	}

	public static String clearForXML(String str) {
		char[] cs = str.toCharArray();
		char[] ncs = new char[cs.length];
		int j = 0;
		for (int i = 0; i < cs.length; i++)
			if (cs[i] <= 65533) {
				if (cs[i] < ' ') {
					if (((cs[i] != '\t' ? 1 : 0) & (cs[i] != '\n' ? 1 : 0) & (cs[i] != '\r' ? 1
							: 0)) != 0)
						;
				} else
					ncs[(j++)] = cs[i];
			}
		ncs = ArrayUtils.subarray(ncs, 0, j);
		return new String(ncs);
	}

	public static String format(String str, Object[] vs) {
		return StringFormat.format(str, vs);
	}

	public static String concat(Object[] args) {
		if (ObjectUtil.empty(args)) {
			return null;
		}
		FastStringBuilder sb = new FastStringBuilder();
		Object[] arrayOfObject = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			Object arg = arrayOfObject[i];
			if (ObjectUtil.notEmpty(arg)) {
				sb.append(arg);
			}
		}
		return sb.toStringAndClose();
	}
}