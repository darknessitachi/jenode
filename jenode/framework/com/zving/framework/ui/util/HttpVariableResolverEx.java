package com.zving.framework.ui.util;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.ui.HttpVariableResolver;
import java.util.Map;

public class HttpVariableResolverEx extends HttpVariableResolver
{
  protected DataRow dr;
  protected IVariableResolver vr;
  protected Map<?, ?> map;

  public void setData(DataRow dr)
  {
    this.dr = dr;
  }

  public void setData(IVariableResolver vr) {
    this.vr = vr;
  }

  public void setData(Map<?, ?> map) {
    if ((map instanceof CaseIgnoreMapx))
      this.map = map;
    else
      this.map = new CaseIgnoreMapx(map);
  }

  public Object resolveVariable(String holder)
  {
    Object v = null;
    if (this.map != null) {
      v = this.map.get(holder);
    }
    if ((v == null) && (this.dr != null)) {
      v = this.dr.get(holder);
    }
    if ((v == null) && (this.vr != null)) {
      try {
        v = this.vr.resolveVariable(holder);
      } catch (ExpressionException e) {
        e.printStackTrace();
      }
    }
    if (v == null) {
      v = super.resolveVariable(holder);
    }
    if ((v != null) && ((v instanceof String))) {
      v = LangUtil.get((String)v);
    }
    return v;
  }
}