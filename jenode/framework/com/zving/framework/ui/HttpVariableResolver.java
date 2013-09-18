package com.zving.framework.ui;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.Current.CurrentData;
import com.zving.framework.Member;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.User;
import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.bean.BeanDescription;
import com.zving.framework.core.bean.BeanManager;
import com.zving.framework.core.bean.BeanProperty;
import com.zving.framework.data.DataRow;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.ui.tag.IListTag;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class HttpVariableResolver
  implements IVariableResolver
{
  protected HttpServletRequest request;
  protected AbstractTag currentTag;
  protected Mapx<String, Object> allMap = new CaseIgnoreMapx();

  public static HttpVariableResolver getInstance(AbstractTag tag, HttpServletRequest request) {
    HttpVariableResolver context = Current.getVariableResolver();
    if (context != null) {
      if (context.currentTag == tag) {
        return context;
      }
      HttpVariableResolver context2 = new HttpVariableResolver();
      context2.request = context.request;
      context2.currentTag = tag;
      context2.allMap = context.allMap;
      return context2;
    }
    context = new HttpVariableResolver();
    context.request = request;
    context.currentTag = tag;

    context.init();
    Current.setVariableResolver(context);
    return context;
  }

  protected void init()
  {
    this.allMap.put("Request", Current.getRequest());
    this.allMap.put("Config", Config.getMapx());
    this.allMap.put("Current", Current.getCurrentData().Values);
    this.allMap.put("Response", Current.getResponse());
    this.allMap.put("Header", Current.getRequest().getHeaders());
    this.allMap.put("Cookie", Current.getCookies());
  }

  public Object resolveVariable(String holder)
  {
    if (holder == null) {
      return null;
    }

    this.allMap.put("User", User.getCurrent());
    this.allMap.put("Member", Member.getCurrent());
    this.allMap.put("Priv", User.getPrivilege());

    Object v = null;
    if (this.currentTag != null) {
      ListTagData ltd = new ListTagData(this.currentTag);
      if (ltd.hasData()) {
        if (ltd.getDataColumn(holder) != null)
          return getBeanProperty(ltd, holder);
        do
        {
          ltd = ltd.getParent();
          if ((ltd == null) || (!ltd.hasData()))
            break;
        }
        while (ltd.getDataColumn(holder) == null);
        return getBeanProperty(ltd, holder);
      }

      AbstractTag tag = this.currentTag;
      while (tag != null) {
        v = tag.getVariable(holder);
        if (v != null) {
          return v;
        }
        tag = tag.getParent();
      }
      v = this.currentTag.getContext().getRootVariable(holder);
      if (v != null) {
        return v;
      }
    }

    v = this.allMap.get(holder);
    if (v != null) {
      return v;
    }

    if ("List".equalsIgnoreCase(holder)) {
      if (this.currentTag != null)
        v = new ListTagData(this.currentTag);
    }
    else if ((this.currentTag != null) && ("Parent".equalsIgnoreCase(holder))) {
      AbstractTag parent = this.currentTag.getParent();
      if (parent != null) {
        v = new ListTagData(parent).getParent();
      }
    }
    int i = holder.indexOf(".");
    if (i > 0) {
      String prefix = holder.substring(0, i);
      String name = holder.substring(i + 1);
      Object obj = this.allMap.get(prefix);
      if ((obj != null) && 
        (v == null)) {
        v = getBeanProperty(obj, name);
      }
    }

    if (v == null) {
      v = Current.getRequest().get(holder);
    }
    if (v == null) {
      v = Current.getResponse().get(holder);
    }
    if (v == null) {
      v = Current.get(holder);
    }
    return v;
  }

  protected Object getBeanProperty(Object bean, String property) {
    try {
      Object v = null;
      if ((bean instanceof Map)) {
        v = ((Map)bean).get(property);
      }
      if ((v == null) && ((bean instanceof DataRow)))
        v = ((DataRow)bean).get(property);
      BeanProperty p;
      if (v == null) { p = BeanManager.getBeanDescription(bean.getClass()).getProperty(property);
        if (p == null); }
      return p.read(bean);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public void addMap(Mapx<String, Object> map, String prefix) {
    this.allMap.put(prefix, map);
  }

  public void setTag(AbstractTag tag) {
    this.currentTag = tag;
  }

  public void setVariable(String varName, Object value)
  {
    this.allMap.put(varName, value);
  }

  public static class ListTagData extends DataRow
  {
    private static final long serialVersionUID = 1L;
    private AbstractTag tag;

    public ListTagData(AbstractTag pTag)
    {
      super(null);
      if (pTag == null) {
        return;
      }
      this.tag = pTag;
      while (true) {
        if (this.tag == null) {
          return;
        }
        if ((this.tag instanceof IListTag)) {
          DataRow dr = ((IListTag)this.tag).getCurrentDataRow();
          if (dr == null) {
            break;
          }
          this.columns = dr.getDataColumns();
          this.values = dr.getDataValues();
          break;
        }
        this.tag = this.tag.getParent();
      }
    }

    public boolean hasData() {
      return this.columns != null;
    }

    public ListTagData getParent() {
      if ((this.tag == null) || (this.tag.getParent() == null)) {
        return null;
      }
      return new ListTagData(this.tag.getParent());
    }
  }
}