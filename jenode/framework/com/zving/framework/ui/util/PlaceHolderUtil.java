package com.zving.framework.ui.util;

import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.expression.impl.DefaultFunctionMapper;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.ui.zhtml.ZhtmlManagerContext;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.FastStringBuilder;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlaceHolderUtil {
	public static List<PlaceHolder> parse(String html) {
		List list = new ArrayList();
		int lastIndex = 0;
		int pos = 0;
		while ((pos = html.indexOf("${", lastIndex)) >= 0) {
			if ((pos != 0) && (lastIndex != pos)) {
				PlaceHolder p = new PlaceHolder();
				p.isString = true;
				p.Value = html.substring(lastIndex, pos);
				list.add(p);
			}
			char lastStringChar = '\000';
			boolean escapeFlag = false;
			boolean continueFlag = false;
			for (int i = pos + 2; i < html.length(); i++) {
				char c = html.charAt(i);
				if (c == '\\') {
					escapeFlag = true;
				} else if (!escapeFlag) {
					if ((c == '"') || (c == '\'')) {
						if (lastStringChar == c) {
							lastStringChar = '\000';
						} else
							lastStringChar = c;
					} else {
						if ((c == '}') && (lastStringChar == 0)) {
							String v = html.substring(pos, i + 1);
							PlaceHolder p = new PlaceHolder();
							p.isExpression = true;
							p.Value = v;
							list.add(p);
							lastIndex = i + 1;
							continueFlag = true;
							break;
						}
						if (c == '\n') {
							lastIndex = i + 1;
							continueFlag = true;
							break;
						}
					}
				} else
					escapeFlag = false;

			}

			if (!continueFlag) {
				break;
			}
		}
		if (lastIndex != html.length()) {
			PlaceHolder p = new PlaceHolder();
			p.isString = true;
			p.Value = html.substring(lastIndex);
			list.add(p);
		}
		return list;
	}

	public static String join(List<PlaceHolder> list) {
		FastStringBuilder sb = new FastStringBuilder();
		for (PlaceHolder p : list) {
			sb.append(p.Value);
		}
		return sb.toStringAndClose();
	}

	public static String replacePlaceHolder(String html,
			AbstractExecuteContext context, boolean blankFlag, boolean spaceFlag)
			throws ExpressionException {
		if ((html == null) || (context == null)) {
			return "";
		}
		String blank = "";
		if (spaceFlag) {
			blank = "&nbsp;";
		}
		List<PlaceHolder> list = parse(html);
		FastStringBuilder sb = new FastStringBuilder();
		for (PlaceHolder p : list) {
			if (p.isString) {
				sb.append(p.Value);
			}
			if (p.isExpression) {
				Object v = context.evalExpression(p.Value);
				if (v != null) {
					if ((v == null) || (v.equals(""))) {
						sb.append(blank);
					} else if ((v instanceof String)) {
						String str = (String) v;
						str = LangUtil.get(str);
						sb.append(str);
					} else if ((v instanceof Date)) {
						String str = DateUtil.toDateTimeString((Date) v);
						sb.append(str);
					} else {
						sb.append(v);
					}
				} else if (blankFlag)
					sb.append("");
				else {
					sb.append(p.Value);
				}
			}
		}
		return sb.toStringAndClose();
	}

	public static String replacePlaceHolder(String html, Map<?, ?> map,
			boolean blankFlag, boolean spaceFlag) throws ExpressionException {
		if ((html == null) || (map == null)) {
			return "";
		}
		String blank = "";
		if (spaceFlag) {
			blank = "&nbsp;";
		}
		List<PlaceHolder> list = parse(html);
		FastStringBuilder sb = new FastStringBuilder();
		HttpVariableResolverEx vr = new HttpVariableResolverEx();
		vr.setData(map);
		for (PlaceHolder p : list) {
			if (p.isString) {
				sb.append(p.Value);
			}
			if (p.isExpression) {
				Object v = ZhtmlManagerContext
						.getInstance()
						.getEvaluator()
						.evaluate(p.Value, Object.class, vr,
								DefaultFunctionMapper.getInstance());
				if (v != null) {
					if ((v == null) || (v.equals(""))) {
						sb.append(blank);
					} else if ((v instanceof String)) {
						String str = (String) v;
						str = LangUtil.get(str);
						sb.append(str);
					} else if ((v instanceof Date)) {
						String str = DateUtil.toDateTimeString((Date) v);
						sb.append(str);
					} else {
						sb.append(v);
					}
				} else if (blankFlag)
					sb.append("");
				else {
					sb.append(p.Value);
				}
			}
		}
		return sb.toStringAndClose();
	}

	public static String replacePlaceHolder(String html,
			IVariableResolver data, boolean blankFlag, boolean spaceFlag)
			throws ExpressionException {
		if ((html == null) || (data == null)) {
			return "";
		}
		String blank = "";
		if (spaceFlag) {
			blank = "&nbsp;";
		}
		List<PlaceHolder> list = parse(html);
		FastStringBuilder sb = new FastStringBuilder();
		HttpVariableResolverEx vr = new HttpVariableResolverEx();
		vr.setData(data);
		for (PlaceHolder p : list) {
			if (p.isString) {
				sb.append(p.Value);
			}
			if (p.isExpression) {
				Object v = ZhtmlManagerContext
						.getInstance()
						.getEvaluator()
						.evaluate(p.Value, Object.class, vr,
								DefaultFunctionMapper.getInstance());
				if (v != null) {
					if ((v == null) || (v.equals(""))) {
						sb.append(blank);
					} else if ((v instanceof String)) {
						String str = (String) v;
						str = LangUtil.get(str);
						sb.append(str);
					} else if ((v instanceof Date)) {
						String str = DateUtil.toDateTimeString((Date) v);
						sb.append(str);
					} else {
						sb.append(v);
					}
				} else if (blankFlag)
					sb.append("");
				else {
					sb.append(p.Value);
				}
			}
		}
		return sb.toStringAndClose();
	}

	public static String replaceWithDataTable(String html, DataTable dt,
			boolean blankFlag, boolean spaceFlag) throws ExpressionException {
		if ((html == null) || (dt == null)) {
			return "";
		}
		String blank = "";
		if (spaceFlag) {
			blank = "&nbsp;";
		}
		 List<PlaceHolder> list = parse(html);
		FastStringBuilder sb = new FastStringBuilder();
		HttpVariableResolverEx vr = new HttpVariableResolverEx();
		
		for (DataRow dr : dt) {
			vr.setData(dr);
			
			for (PlaceHolder p : list) {
				if (p.isString) {
					sb.append(p.Value);
				}
				if (p.isExpression) {
					Object v = ZhtmlManagerContext
							.getInstance()
							.getEvaluator()
							.evaluate(p.Value, Object.class, vr,
									DefaultFunctionMapper.getInstance());
					if (v != null) {
						if ((v == null) || (v.equals(""))) {
							sb.append(blank);
						} else if ((v instanceof String)) {
							String str = (String) v;
							str = LangUtil.get(str);
							sb.append(str);
						} else if ((v instanceof Date)) {
							String str = DateUtil.toDateTimeString((Date) v);
							sb.append(str);
						} else {
							sb.append(v);
						}
					} else if (blankFlag)
						sb.append("");
					else {
						sb.append(p.Value);
					}
				}	
			}
		}

		return sb.toStringAndClose();
	}

	public static String prepare4Script(String content) {
		 List<PlaceHolder> list = parse(content);
		FastStringBuilder sb = new FastStringBuilder();
		for (PlaceHolder p : list) {
			if (p.isString) {
				sb.append(p.Value);
			}
			if (p.isExpression) {
				sb.append(p.Value.substring(0, 1) + "\\" + p.Value.substring(1));
			}
		}
		return sb.toStringAndClose();
	}

	public static void main(String[] args) throws Exception {
		String html = "${A} This is a ${substring(Type,0,\"ABC}DEF\")} variable! ${B}${C}${ ${";
		 List<PlaceHolder> list = parse(html);
		for (PlaceHolder p : list)
			System.out.println(p.Value);
	}
}