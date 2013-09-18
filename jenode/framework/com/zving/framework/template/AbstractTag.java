package com.zving.framework.template;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.extend.IExtendItem;
import com.zving.framework.template.exception.TemplateCompileException;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.NumberUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;

public abstract class AbstractTag implements IExtendItem, Cloneable {
	public static final int SKIP_PAGE = 5;
	public static final int EVAL_BODY_BUFFERED = 2;
	public static final int EVAL_BODY_AGAIN = 2;
	public static final int SKIP_BODY = 0;
	public static final int EVAL_BODY_INCLUDE = 1;
	public static final int EVAL_PAGE = 6;
	protected AbstractExecuteContext pageContext;
	protected AbstractExecuteContext context;
	protected int startLineNo;
	protected int startCharIndex;
	protected AbstractTag parent;
	protected TemplatePrintWriter bodyContent;
	protected Mapx<String, String> holderAttributes = new CaseIgnoreMapx();
	protected Mapx<String, String> attributes = new CaseIgnoreMapx();
	protected Mapx<String, Object> variables = new CaseIgnoreMapx();

	private static CaseIgnoreMapx<String, CaseIgnoreMapx<String, Method>> AllTagMethods = new CaseIgnoreMapx();

	public void init() throws ExpressionException {
		for (Entry e : this.holderAttributes.entrySet())
			try {
				String v = (String) e.getValue();
				if (!this.pageContext.isKeepExpression()) {
					Object obj = this.pageContext.evalExpression(v);
					v = obj == null ? "" : String.valueOf(obj);
				}
				if (v == null) {
					v = "";
				}
				setAttributeInternal((String) e.getKey(), v);
			} catch (NullPointerException t) {
				t.printStackTrace();
			}
	}

	public void setPageContext(AbstractExecuteContext pageContext) {
		this.pageContext = (this.context = pageContext);
	}

	public String getID() {
		return getPrefix() + ":" + getTagName();
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getPluginID();

	public abstract String getPrefix();

	public abstract String getTagName();

	public abstract List<TagAttr> getTagAttrs();

	public int doStartTag() throws TemplateRuntimeException {
		return 2;
	}

	public int doEndTag() throws TemplateRuntimeException {
		return 6;
	}

	public int doAfterBody() throws TemplateRuntimeException {
		return 0;
	}

	public AbstractTag getParent() {
		return this.parent;
	}

	public PrintWriter getPreviousOut() {
		if (this.parent == null) {
			return this.pageContext.getRootWriter();
		}
		if (this.parent.bodyContent != null) {
			return this.parent.bodyContent;
		}
		return this.parent.getPreviousOut();
	}

	public String getBody() {
		return this.bodyContent == null ? "" : this.bodyContent.getResult();
	}

	public Object clone() {
		try {
			AbstractTag obj = (AbstractTag) super.clone();
			obj.holderAttributes = this.holderAttributes.clone();
			obj.attributes = this.attributes.clone();
			obj.variables = this.variables.clone();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getStartLineNo() {
		return this.startLineNo;
	}

	private static CaseIgnoreMapx<String, Method> getMethods(AbstractTag tag) {
		CaseIgnoreMapx methods = (CaseIgnoreMapx) AllTagMethods
				.get(tag.getID());
		if (methods == null) {
			synchronized (tag.getClass()) {
				methods = (CaseIgnoreMapx) AllTagMethods.get(tag.getID());
				if (methods == null) {
					methods = new CaseIgnoreMapx();
					Class Clazz = tag.getClass();

					for (Method m : Clazz.getMethods()) {
						String name = m.getName();

						if (name.startsWith("set")) {
							if (m.getModifiers() == 1) {
								if ((m.getParameterTypes() != null)
										&& (m.getParameterTypes().length == 1)) {
									name = name.substring(3);
									methods.put(name, m);
								}
							}
						}
					}
					AllTagMethods.put(tag.getID(), methods);
				}
			}
		}
		return methods;
	}

	public boolean hasAttribute(String name) {
		for (TagAttr tag : getTagAttrs()) {
			if (tag.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean isValidAttributeValue(String attr, String value) {
		List<TagAttr> all = getTagAttrs();
		if (all == null) {
			return true;
		}
		for (TagAttr ta : all) {
			if (ta.getName().equals(attr)) {
				if ((ta.isMandatory()) && (StringUtil.isEmpty(value))) {
					return false;
				}

				if (ta.getDataType() == 8) {
					return NumberUtil.isInt(value);
				}
				if (ta.getDataType() != 7)
					break;
				return NumberUtil.isLong(value);
			}

		}

		return true;
	}

	public void setAttribute(String attr, String value)
			throws TemplateRuntimeException {
		if ((value == null) || (value.indexOf("${") < 0))
			setAttributeInternal(attr, value);
		else {
			this.holderAttributes.put(attr, value);
		}
		this.attributes.put(attr, value);
	}

	public String getAttribute(String attr) {
		return (String) this.attributes.get(attr);
	}

	public void setAttributeInternal(String attr, String value)
			throws TemplateRuntimeException {
		try {
			if (!isValidAttributeValue(attr, value))
				throw new TemplateRuntimeException("标签" + getName() + "的属性"
						+ attr + "的值不是正确的类型：" + value);
		} catch (TemplateCompileException e) {
			throw new TemplateRuntimeException(e.getMessage());
		}
		Method m = (Method) getMethods(this).get(attr);
		if (m == null) {
			return;
		}
		Class cls = m.getParameterTypes()[0];
		try {
			if ((cls == Integer.class) || (cls == Integer.TYPE))
				m.invoke(
						this,
						new Object[] { Integer.valueOf(Integer.parseInt(value)) });
			else if ((cls == Long.class) || (cls == Long.TYPE))
				m.invoke(this,
						new Object[] { Long.valueOf(Long.parseLong(value)) });
			else if ((cls == Float.class) || (cls == Float.TYPE))
				m.invoke(this,
						new Object[] { Float.valueOf(Float.parseFloat(value)) });
			else if ((cls == Double.class) || (cls == Double.TYPE))
				m.invoke(this, new Object[] { Double.valueOf(Double
						.parseDouble(value)) });
			else if ((cls == Boolean.class) || (cls == Boolean.TYPE))
				m.invoke(this,
						new Object[] { Boolean.valueOf("true".equals(value)) });
			else
				m.invoke(
						this,
						new Object[] { value == null ? null : String
								.valueOf(value) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setStartLineNo(int startLineNo) {
		this.startLineNo = startLineNo;
	}

	public void setParent(AbstractTag parent) {
		this.parent = parent;
	}

	public PrintWriter getBodyWriter() {
		return this.bodyContent;
	}

	public void createBodyBuffer(int depth) {
		this.bodyContent = this.pageContext.createBodyBuffer(depth);
	}

	public AbstractExecuteContext getContext() {
		return this.pageContext;
	}

	public Object getVariable(String key) {
		return this.variables.get(key);
	}

	public void setVariable(String key, Object value) {
		this.variables.put(key, value);
	}

	public int getStartCharIndex() {
		return this.startCharIndex;
	}

	public void setStartCharIndex(int startCharIndex) {
		this.startCharIndex = startCharIndex;
	}
}