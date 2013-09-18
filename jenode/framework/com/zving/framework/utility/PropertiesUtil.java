package com.zving.framework.utility;

import com.zving.framework.collection.Mapx;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class PropertiesUtil {
	public static Mapx<String, String> read(InputStream is) {
		String text = FileUtil.readText(is, "UTF-8");
		Mapx map = new Mapx();
		for (String str : StringUtil.splitEx(text, "\n")) {
			str = str.trim();
			if ((!ObjectUtil.empty(str)) && (!str.startsWith("#"))) {
				int index = str.indexOf("=");
				if (index >= 0) {
					String k = str.substring(0, index).trim();
					String v = StringUtil.rightTrim(str.substring(index + 1));
					v = StringUtil.javaDecode(v);
					map.put(k, v);
				}
			}
		}
		return map;
	}

	public static Mapx<String, String> read(File f) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		try {
			return read(fis);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void write(File f, Mapx<String, String> map) {
		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(f);
			write(fis, map);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(OutputStream os, Mapx<String, String> map) {
		FastStringBuilder sb = new FastStringBuilder();
		for (String k : map.keyArray()) {
			if (sb.length() != 0) {
				sb.append("\n");
			}
			sb.append(k);
			sb.append("=");
			sb.append(StringUtil.javaEncode((String) map.get(k)));
		}
		try {
			os.write(sb.toStringAndClose().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}