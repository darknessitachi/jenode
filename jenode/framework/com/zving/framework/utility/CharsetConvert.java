package com.zving.framework.utility;

import com.zving.framework.utility.commons.ArrayUtils;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharsetConvert {
	static Pattern GBKPattern1 = Pattern.compile("charset\\s*\\=\\s*gbk", 34);

	static Pattern GBKPattern2 = Pattern
			.compile("charset\\s*\\=\\s*gb2312", 34);

	static Pattern UTF8Pattern = Pattern.compile("charset\\s*\\=\\s*utf\\-8",
			34);

	public static byte[] GBKToUTF8(String str, boolean bomFlag) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			String s = str.substring(i, i + 1);
			if (s.charAt(0) > '') {
				byte[] bytes = (byte[]) null;
				try {
					bytes = s.getBytes("Unicode");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				String binaryStr = "";
				for (int j = 2; j < bytes.length; j += 2) {
					String hexStr = getHexString(bytes[(j + 1)]);
					String binStr = getBinaryString(Integer.valueOf(hexStr, 16)
							.intValue());
					binaryStr = binaryStr + binStr;
					hexStr = getHexString(bytes[j]);
					binStr = getBinaryString(Integer.valueOf(hexStr, 16)
							.intValue());
					binaryStr = binaryStr + binStr;
				}
				String s1 = "1110" + binaryStr.substring(0, 4);
				String s2 = "10" + binaryStr.substring(4, 10);
				String s3 = "10" + binaryStr.substring(10, 16);
				byte[] bs = new byte[3];
				bs[0] = Integer.valueOf(s1, 2).byteValue();
				bs[1] = Integer.valueOf(s2, 2).byteValue();
				bs[2] = Integer.valueOf(s3, 2).byteValue();
				try {
					sb.append(new String(bs, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				sb.append(s);
			}
		}
		byte[] bs = (byte[]) null;
		try {
			bs = sb.toString().getBytes("UTF-8");
			if (bomFlag)
				bs = ArrayUtils.addAll(StringUtil.BOM, bs);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bs;
	}

	private static String getHexString(byte b) {
		String hexStr = Integer.toHexString(b);
		int m = hexStr.length();
		if (m < 2)
			hexStr = "0" + hexStr;
		else {
			hexStr = hexStr.substring(m - 2);
		}
		return hexStr;
	}

	private static String getBinaryString(int i) {
		String binaryStr = Integer.toBinaryString(i);
		int length = binaryStr.length();
		for (int l = 0; l < 8 - length; l++) {
			binaryStr = "0" + binaryStr;
		}
		return binaryStr;
	}

	public static byte[] UTF8ToGBK(String str) {
		try {
			byte[] bs = str.getBytes("UTF-8");
			if (StringUtil.hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals(
					"efbbbf")) {
				bs = ArrayUtils.subarray(bs, 3, bs.length);
			}
			str = new String(bs, "UTF-8");
			return new String(str.getBytes(), "GBK").getBytes();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void dirGBKToUTF8(String src) {
		convertDir(src, "UTF-8");
	}

	public static void dirUTF8ToGBK(String src) {
		convertDir(src, "GBK");
	}

	public static void convertDir(String src, String charset) {
		File f = new File(src);
		File[] fs = f.listFiles();

		for (int i = 0; (fs != null) && (i < fs.length); i++) {
			f = fs[i];
			String name = f.getName().toLowerCase();
			if (!name.equals(".svn")) {
				if (!name.equals("WEB-INF")) {
					if (f.isDirectory()) {
						convertDir(f.getAbsolutePath(), charset);
					} else {
						byte[] bs = convertFile(FileUtil.readByte(f),
								f.getAbsolutePath(), charset);
						FileUtil.writeByte(f, bs);
					}
				}
			}
		}
	}

	public static byte[] convertFile(byte[] bs, String name, String charset) {
		String oldCharset = "UTF-8".equals(charset) ? "GBK" : "UTF-8";

		String ext = FileUtil.getExtension(name);
		if (ObjectUtil.in(new Object[] { ext, "exe", "db", "dat", "zdt", "jpg",
				"jpeg", "gif", "bmp", "zip", "rar", "png", "swf", "flv", "rar",
				"doc", "docx", "xls", "xlsx", "ppt", "psd", "pptx", "pdf",
				"tif", "dat", "mp3", "ra", "asx", "avi", "wmv", "mp4", "mov",
				"asf", "mpg", "rm", "rmvb" })) {
			return bs;
		}
		if ((name.endsWith(".template")) || (name.endsWith(".xml"))) {
			String txt = null;
			String fileCharset = charset;
			try {
				txt = new String(bs, oldCharset);
				if (name.endsWith(".xml")) {
					int index = txt.indexOf("encoding=");
					fileCharset = txt.substring(index + 10,
							txt.indexOf("\"", index + 11));
					if (!oldCharset.equals(fileCharset))
						txt = new String(bs, fileCharset);
				} else {
					fileCharset = "UTF-8";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if ((name.endsWith(".xml")) && (name.indexOf("WEB-INF") >= 0))
				txt = StringUtil.replaceEx(txt, ">" + oldCharset + "<", ">"
						+ charset + "<");
			try {
				return txt.getBytes(fileCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if ((name.endsWith(".html")) || (name.endsWith(".shtml"))
				|| (name.endsWith(".htm")) || (name.endsWith(".zhtml"))
				|| (name.endsWith(".jspx")) || (name.endsWith(".jsp"))
				|| (name.endsWith(".js")) || (name.endsWith(".css"))) {
			if (!name.startsWith("fckeditorcode")) {
				if (charset.equals("UTF-8")) {
					return webFileGBKToUTF8(bs);
				}
				return webFileUTF8ToGBK(bs);
			}
		} else if (name.endsWith(".java")) {
			try {
				if (charset.equals("UTF-8")) {
					if (StringUtil.isUTF8(bs))
						return bs;
					bs = StringUtil.GBKToUTF8(new String(bs, "GBK"));
					if (name.endsWith(".java"))
						;
					return ArrayUtils.addAll(StringUtil.BOM, bs);
				}

				if (!StringUtil.isUTF8(bs))
					return bs;
				return StringUtil.UTF8ToGBK(new String(bs, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (charset.equals("UTF-8")) {
					if (!StringUtil.isUTF8(bs)) {
						return StringUtil.GBKToUTF8(new String(bs, "GBK"));
					}
				} else if (StringUtil.isUTF8(bs))
					return StringUtil.UTF8ToGBK(new String(bs, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		label737: return bs;
	}

	public static byte[] webFileGBKToUTF8(byte[] bs) {
		if (!StringUtil.isUTF8(bs)) {
			String txt = null;
			try {
				txt = new String(bs, "GBK");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			txt = GBKPattern1.matcher(txt).replaceAll("charset=utf-8");
			txt = GBKPattern2.matcher(txt).replaceAll("charset=utf-8");
			txt = StringUtil.replaceEx(txt, "\"GBK\"", "\"UTF-8\"");
			return StringUtil.GBKToUTF8(txt);
		}
		return bs;
	}

	public static byte[] webFileUTF8ToGBK(byte[] bs) {
		if (StringUtil.isUTF8(bs)) {
			String txt = null;
			try {
				txt = new String(bs, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			txt = UTF8Pattern.matcher(txt).replaceAll("charset=GBK");
			txt = StringUtil.replaceEx(txt, "\"UTF-8\"", "\"GBK\"");
			return StringUtil.UTF8ToGBK(txt);
		}
		return bs;
	}
}