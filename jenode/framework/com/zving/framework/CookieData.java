package com.zving.framework;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zving.framework.CookieData.CookieObject;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;

public class CookieData extends ArrayList<CookieObject> {
	private static final long serialVersionUID = 1L;
	public static final String CookieCharset = "UTF-8";

	public CookieData() {
	}

	public CookieData(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			CookieObject c = new CookieObject();
			c.name = cookie.getName();
			try {
				c.value = URLDecoder.decode(cookie.getValue(), "UTF-8");
			} catch (Exception e) {
				LogUtil.warn("Invalide cookie value:" + cookie.getValue());
			}
			c.domain = cookie.getDomain();
			c.maxAge = cookie.getMaxAge();
			c.path = cookie.getPath();
			c.path = normalizePath(c.path);
			c.secure = cookie.getSecure();
			c.comment = cookie.getComment();
			c.version = cookie.getVersion();
			add(c);
		}
	}

	public void setCookie(String name, String value, String domain, int maxAge,
			String path, boolean secure, String comment) {
		path = normalizePath(path);
		for (int i = 0; i < size(); i++) {
			CookieObject c = (CookieObject) get(i);
			if ((c.name.equals(name))
					&& ((c.domain == null) || (c.domain.equals(domain)))
					&& ((c.path == null) || (c.path.equals(path)))
					&& (c.secure == secure) && (c.value != null)
					&& (!c.value.equals(value))) {
				c.changed = true;
				c.value = value;
				c.comment = comment;
				c.maxAge = maxAge;
				return;
			}
		}

		CookieObject c = new CookieObject();
		c.name = name;
		c.value = value;
		c.comment = comment;
		c.domain = domain;
		c.maxAge = maxAge;
		c.path = path;
		c.secure = secure;
		c.version = 0;
		c.changed = true;
		add(c);
	}

	public CookieObject[] getArray() {
		CookieObject[] cos = new CookieObject[size()];
		for (int i = 0; i < cos.length; i++) {
			cos[i] = ((CookieObject) get(i));
		}
		return cos;
	}

	public void setCookie(String name, String value, int maxAge) {
		setCookie(name, value, null, maxAge, getDefaultPath(), false, null);
	}

	public void setCookie(String name, String value) {
		setCookie(name, value, null, 2147483647, getDefaultPath(), false, null);
	}

	public void setCookie(String name, String value, String path) {
		setCookie(name, value, null, 2147483647, getDefaultPath(), false, null);
	}

	public void setCookie(String name, String value, String path, int maxAge) {
		setCookie(name, value, null, maxAge, getDefaultPath(), false, null);
	}

	public String getCookie(String name) {
		return getCookie(name, null);
	}

	private static String getDefaultPath() {
		String path = Config.getContextPath();
		return normalizePath(path);
	}

	private static String normalizePath(String path) {
		if (path == null) {
			path = "/";
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		return path;
	}

	public String getCookie(String name, String path) {
		path = normalizePath(path);
		ArrayList arr = new ArrayList();
		for (int i = 0; i < size(); i++) {
			CookieObject c = (CookieObject) get(i);
			if (c.name.equals(name)) {
				if (path != null) {
					if (c.path.equals(path))
						arr.add(c.value);
				} else {
					arr.add(c.value);
				}
			}
		}
		if (arr.size() == 0) {
			return null;
		}
		if (arr.size() == 1) {
			return arr.get(0) == null ? null : String.valueOf(arr.get(0));
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			if (arr.get(i) == null)
				sb.append("");
			else {
				sb.append((String) arr.get(i));
			}
		}
		return sb.toString();
	}

	public void write(HttpServletRequest request, HttpServletResponse response) {
		try {
			for (int j = 0; j < size(); j++) {
				CookieObject co = (CookieObject) get(j);
				if (co.changed) {
					Cookie[] cs = request.getCookies();
					boolean flag = true;
					for (int i = 0; (cs != null) && (i < cs.length); i++) {
						String path = normalizePath(cs[i].getPath());
						if ((cs[i].getName().equals(co.name))
								&& ((path == null) || (path.equals(co.path)))) {
							if (StringUtil.isNotEmpty(co.value)) {
								cs[i].setValue(URLEncoder.encode(co.value,
										"UTF-8"));
							}
							cs[i].setMaxAge(co.maxAge);
							if (co.domain != null) {
								cs[i].setDomain(co.domain);
							}
							response.addCookie(cs[i]);
							flag = false;
							break;
						}
					}
					if (flag) {
						String v = "";
						if (co.value != null) {
							v = URLEncoder.encode(co.value, "UTF-8");
						}
						Cookie cookie = new Cookie(co.name, v);
						cookie.setMaxAge(co.maxAge);
						cookie.setPath(co.path);
						if (co.domain != null) {
							cookie.setDomain(co.domain);
						}
						cookie.setSecure(co.secure);
						response.addCookie(cookie);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (CookieObject c : this) {
			sb.append(c.name);
			sb.append("=");
			sb.append(c.value);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static class CookieObject {
		public String name;
		public String value;
		public String comment;
		public String domain;
		public int maxAge = -1;
		public String path;
		public boolean secure;
		public int version = 0;

		public boolean changed = false;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getComment() {
			return this.comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getDomain() {
			return this.domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public int getMaxAge() {
			return this.maxAge;
		}

		public void setMaxAge(int maxAge) {
			this.maxAge = maxAge;
		}

		public String getPath() {
			return this.path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public boolean isSecure() {
			return this.secure;
		}

		public void setSecure(boolean secure) {
			this.secure = secure;
		}

		public int getVersion() {
			return this.version;
		}

		public void setVersion(int version) {
			this.version = version;
		}

		public boolean isChanged() {
			return this.changed;
		}

		public void setChanged(boolean changed) {
			this.changed = changed;
		}
	}
}