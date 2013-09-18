package com.zving.framework.utility;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.collection.Mapx;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtil {
	public static String getHomeURL(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort();
	}

	public static Mapx<String, String> getParameterMap(
			HttpServletRequest request) {
		Mapx map = new Mapx();
		 Map<String, Object> tmap = request.getParameterMap();
		for (String key : tmap.keySet()) {
			Object value = tmap.get(key);
			map.put(key, getParameterValue(value));
		}
		Enumeration e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = request.getAttribute(key);
			map.put(key, getParameterValue(value));
		}
		return map;
	}

	private static String getParameterValue(Object value) {
		if (value == null) {
			return null;
		}
		if (!value.getClass().isArray()) {
			return String.valueOf(value);
		}
		Object[] arr = ObjectUtil.toObjectArray(value);
		if (arr.length == 1) {
			return String.valueOf(arr[0]);
		}
		return StringUtil.join(arr);
	}

	public static Mapx<String, String> getMapFromQueryString(String url) {
		Mapx map = new Mapx();
		int index = url.indexOf('?');
		if (index < 0) {
			return map;
		}
		String str = url.substring(url.indexOf('?') + 1);
		int index2 = str.lastIndexOf('#');
		if (index2 != -1) {
			str = str.substring(0, index2);
		}
		String[] arr = str.split("\\&");
		for (int i = 0; i < arr.length; i++) {
			String[] arr2 = arr[i].split("\\=");
			if (arr2.length == 2)
				map.put(arr2[0], arr2[1]);
			else if (arr2.length > 0) {
				map.put(arr2[0], "");
			}
		}
		return map;
	}

	public static String getQueryStringFromMap(Mapx<?, ?> map) {
		return getQueryStringFromMap(map, false);
	}

	public static String getQueryStringFromMap(Mapx<?, ?> map,
			boolean encodeFlag) {
		FastStringBuilder sb = new FastStringBuilder();
		sb.append("?");
		boolean first = true;
		for (Iterator localIterator = map.keySet().iterator(); localIterator
				.hasNext();) {
			Object k = localIterator.next();
			Object v = map.get(k);
			if (v != null) {
				if (!first) {
					sb.append("&");
				}
				sb.append(k);
				sb.append("=");
				if (encodeFlag)
					sb.append(StringUtil.urlEncode(v.toString()));
				else {
					sb.append(v.toString());
				}
				first = false;
			}
		}
		return sb.toStringAndClose();
	}

	public static String getURLContent(String url) throws Exception {
		return getURLContent(url, null);
	}

	public static String getURLContent(String url, String encoding)
			throws Exception {
		FastStringBuilder sb = new FastStringBuilder();
		InputStreamReader isr = null;
		if (StringUtil.isNotEmpty(encoding))
			isr = new InputStreamReader(new URL(url).openStream(), encoding);
		else {
			isr = new InputStreamReader(new URL(url).openStream());
		}
		BufferedReader br = new BufferedReader(isr);
		String s = null;
		while ((s = br.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}
		br.close();
		return sb.toStringAndClose();
	}

	public static String postURLContent(String url,
			Mapx<String, String> params, String encode) {
		FastStringBuilder sb = null;
		String result = null;
		HttpURLConnection conn = null;
		try {
			for (String key : params.keySet()) {
				if (sb == null)
					sb = new FastStringBuilder();
				else {
					sb.append('&');
				}
				sb.append(key).append('=');
				sb.append(URLEncoder.encode((String) params.get(key), encode));
			}
			byte[] entity = sb.toStringAndClose().getBytes(encode);
			URL _url = new URL(url);
			conn = (HttpURLConnection) _url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entity);
			outStream.flush();
			outStream.close();
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				result = FileUtil.readText(is, encode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return result;
	}

	public static String getCookieValue(HttpServletRequest request,
			String cookieName) {
		return getCookieValue(request, cookieName, "");
	}

	public static String getCookieValue(HttpServletRequest request,
			String cookieName, String defaultValue) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; (cookies != null) && (i < cookies.length); i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equalsIgnoreCase(cookie.getName()))
				try {
					return URLDecoder.decode(cookie.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return defaultValue;
				}
		}
		return defaultValue;
	}

	public static void setCookieValue(HttpServletRequest request,
			HttpServletResponse response, String cookieName, String cValue) {
		setCookieValue(request, response, cookieName, -1080887296, cValue);
	}

	public static void setCookieValue(HttpServletRequest request,
			HttpServletResponse response, String cookieName, int maxAge,
			String cValue) {
		Cookie[] cookies = request.getCookies();
		boolean cookieexistflag = false;
		String contextPath = Config.getContextPath();
		contextPath = contextPath.substring(0, contextPath.length() - 1);
		try {
			cValue = URLEncoder.encode(cValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; (cookies != null) && (i < cookies.length); i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equalsIgnoreCase(cookie.getName())) {
				cookieexistflag = true;
				cookie.setValue(cValue);
				cookie.setPath(contextPath);
				cookie.setMaxAge(maxAge);
				response.addCookie(cookie);
			}
		}
		if (!cookieexistflag) {
			Cookie cookie = new Cookie(cookieName, cValue);
			cookie.setPath(contextPath);
			cookie.setMaxAge(maxAge);
			response.addCookie(cookie);
		}
	}

	public static String getUrlExtension(String url) {
		if (StringUtil.isEmpty(url)) {
			return "";
		}
		int index = url.indexOf('?');
		if (index > 0) {
			url = url.substring(0, index);
		}
		int i1 = url.lastIndexOf('/');
		int i2 = url.lastIndexOf('.');
		if (i1 >= i2) {
			return "";
		}
		return url.substring(i2).toLowerCase();
	}

	public static String getFileName(String url) {
		int index1 = url.indexOf('?');
		int index2 = url.indexOf('#');
		int index3 = url.lastIndexOf('/');
		if (index2 > 0) {
			url = url.substring(0, index2);
		}
		if (index1 > 0) {
			url = url.substring(0, index1);
		}
		if (index3 < 8) {
			return "";
		}
		return url.substring(index3 + 1);
	}

	public static String getHost(String url) {
		if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
			LogUtil.error("Invalid URL：" + url);
			return null;
		}
		int index = url.indexOf("//") + 2;
		int index2 = url.indexOf("/", index);
		if (index2 <= 0) {
			index2 = url.length();
		}
		return url.substring(index, index2);
	}

	public static String getRealIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if ((StringUtil.isEmpty(ip)) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((StringUtil.isEmpty(ip)) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((StringUtil.isEmpty(ip)) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if ((StringUtil.isEmpty(ip)) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if ((StringUtil.isEmpty(ip)) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getChineseParameter(String queryString, String key) {
		if (queryString == null) {
			return null;
		}
		Mapx map = StringUtil.splitToMapx(queryString, "&", "=");
		String v = map.getString(key);
		if ((v != null) && (v.indexOf("%") >= 0)) {
			v = StringUtil.replaceEx(v, "+", "%20");

			FastStringBuilder sb = new FastStringBuilder();
			int escapeIndex = -1;
			for (int i = 0; i < v.length(); i++) {
				char c = v.charAt(i);
				if (c == '%') {
					escapeIndex = i;
				} else if ((escapeIndex >= 0) && (i - escapeIndex == 3)) {
					if (c == '%')
						escapeIndex = i;
					else {
						escapeIndex = -1;
					}
				}

				if (escapeIndex < 0)
					sb.append("%").append(
							StringUtil.hexEncode(new byte[] { (byte) c }));
				else {
					sb.append(c);
				}
			}
			v = sb.toStringAndClose();
			byte[] bs = StringUtil.hexDecode(StringUtil.replaceEx(v, "%", ""));
			try {
				String result = null;
				if ((bs.length >= 3) && (StringUtil.isUTF8(bs))) {
					result = new String(bs, "UTF-8");
					if (result.indexOf("?") >= 0)
						result = new String(bs, "GBK");
				} else {
					result = new String(bs, "GBK");
					if (result.indexOf("?") >= 0) {
						result = new String(bs, "UTF-8");
					}
				}
				v = result;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return v;
	}

	public static String getChineseParameter(String key) {
		RequestData req = Current.getRequest();
		if (req != null) {
			String v = getChineseParameter(req.getQueryString(), key);
			if (v == null) {
				v = req.getString(key);
			}
			if (v == null) {
				v = Current.getResponse().getString(key);
			}
			return v;
		}
		return null;
	}
}