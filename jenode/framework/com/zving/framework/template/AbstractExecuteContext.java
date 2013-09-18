package com.zving.framework.template;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IEvaluator;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExecuteContext
  implements Cloneable
{
  protected List<TemplatePrintWriter> writerList = new ArrayList(8);
  protected Mapx<String, Object> variables = new CaseIgnoreMapx();
  protected AbstractTag currentTag;
  protected boolean keepExpression = false;
  protected int pageTotal;
  protected int pageSize;
  protected int pageIndex;
  protected long startTime;
  protected PrintWriter rootWriter;
  public static final String PREVIEW_ATTR = "_ZVING_PREVIEW_ATTR";
  public static final String INTERACTIVE_ATTR = "_ZVING_INTERACTIVE_ATTR";

  public AbstractExecuteContext()
  {
    this.startTime = System.currentTimeMillis();
    this.variables.put("TimeMillis", Long.valueOf(this.startTime));
  }

  public TemplatePrintWriter createBodyBuffer(int depth) {
    if (this.writerList.size() < depth) {
      this.writerList.add(new TemplatePrintWriter());
    }
    TemplatePrintWriter pw = (TemplatePrintWriter)this.writerList.get(depth - 1);
    pw.clear();
    return pw;
  }

  public PrintWriter getOut() {
    if (this.currentTag == null) {
      return getRootWriter();
    }
    PrintWriter pw = this.currentTag.getBodyWriter();
    if (pw == null) {
      pw = this.currentTag.getPreviousOut();
    }
    return pw;
  }

  public AbstractTag getCurrentTag() {
    return this.currentTag;
  }

  public void setCurrentTag(AbstractTag currentTag) {
    this.currentTag = currentTag;
  }

  public Object evalExpression(String expression) throws ExpressionException {
    return evalExpression(expression, Object.class);
  }

  public Object evalExpression(String expression, Class<?> expectedClass) throws ExpressionException {
    IFunctionMapper fm = getManagerContext().getFunctionMapper();
    return getManagerContext().getEvaluator().evaluate(expression, expectedClass, getVariableResolver(), fm);
  }

  public boolean isKeepExpression() {
    return this.keepExpression;
  }

  public void setKeepExpression(boolean keepExpression) {
    this.keepExpression = keepExpression;
  }

  public int evalInt(String expression) {
    try {
      if (!expression.startsWith("${")) {
        expression = "${" + expression + "}";
      }
      Object obj = evalExpression(expression);
      if (obj == null) {
        return 0;
      }
      if ((obj instanceof Integer)) {
        return ((Integer)obj).intValue();
      }
      return Integer.parseInt(obj.toString());
    }
    catch (ExpressionException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public long evalLong(String expression)
  {
    try {
      if (!expression.startsWith("${")) {
        expression = "${" + expression + "}";
      }
      Object obj = evalExpression(expression);
      if (obj == null) {
        return 0L;
      }
      if ((obj instanceof Long)) {
        return ((Long)obj).longValue();
      }
      return Long.parseLong(obj.toString());
    }
    catch (ExpressionException e) {
      e.printStackTrace();
    }
    return 0L;
  }

  public String eval(String expression) {
    try {
      if (!expression.startsWith("${")) {
        expression = "${" + expression + "}";
      }
      Object obj = evalExpression(expression, Object.class);
      return obj == null ? null : String.valueOf(obj);
    } catch (ExpressionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Mapx<String, Object> evalMap(String expression)
  {
    try {
      if (!expression.startsWith("${")) {
        expression = "${" + expression + "}";
      }
      Object obj = evalExpression(expression);
      if (obj == null) {
        return null;
      }
      if ((obj instanceof Mapx)) {
        return (Mapx)obj;
      }
      return null;
    }
    catch (ExpressionException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public String replaceHolder(String expression) {
    try {
      return String.valueOf(evalExpression(expression));
    } catch (ExpressionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void addDataVariable(String key, Object value) {
    if (this.currentTag == null)
      addRootVariable(key, value);
    else
      this.currentTag.setVariable(key, value);
  }

  public PrintWriter getRootWriter()
  {
    return this.rootWriter;
  }

  public void setRootWriter(PrintWriter writer) {
    this.rootWriter = writer;
  }

  public int getPageTotal() {
    return this.pageTotal;
  }

  public void setPageTotal(int pageTotal) {
    this.pageTotal = pageTotal;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getPageIndex() {
    return this.pageIndex;
  }

  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public long getExecuteCostTime() {
    return System.currentTimeMillis() - this.startTime;
  }

  public abstract AbstractExecuteContext getIncludeContext();

  public abstract IVariableResolver getVariableResolver();

  public abstract ITemplateManagerContext getManagerContext();

  public boolean isPreview()
  {
    return ObjectUtil.isTrue(getRootVariable("_ZVING_PREVIEW_ATTR"));
  }

  public boolean isInteractive() {
    return ObjectUtil.isTrue(getRootVariable("_ZVING_INTERACTIVE_ATTR"));
  }

  public void setPreview(boolean flag) {
    addRootVariable("_ZVING_PREVIEW_ATTR", Boolean.valueOf(flag));
  }

  public void setInteractive(boolean flag) {
    addRootVariable("_ZVING_INTERACTIVE_ATTR", Boolean.valueOf(flag));
  }

  public void addRootVariable(String name, Object value) {
    this.variables.put(name, value);
  }

  public Object getRootVariable(String key) {
    return this.variables.get(key);
  }

  public void setAttribute(String name, Object value) {
    this.variables.put(name, value);
  }

  public Object getAttribute(String key) {
    return this.variables.get(key);
  }

  public Object removeAttribute(String key) {
    return this.variables.remove(key);
  }

  public Object removeRootVariable(String key) {
    return this.variables.remove(key);
  }
}