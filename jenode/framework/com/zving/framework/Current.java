package com.zving.framework;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.Dispatcher;
import com.zving.framework.core.IURLHandler;
import com.zving.framework.data.Transaction;
import com.zving.framework.ui.HttpVariableResolver;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.utility.StringUtil;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class Current
{
  private static ThreadLocal<CurrentData> current = new ThreadLocal();

  public static void clear()
  {
    if (current.get() != null)
      current.set(null);
  }

  public static void put(String key, Object value)
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      data.Values = new Mapx();
      current.set(data);
    } else if (data.Values == null) {
      data.Values = new Mapx();
    }
    if ((value instanceof Map)) {
      Map vmap = (Map)value;
      for (Iterator localIterator = vmap.keySet().iterator(); localIterator.hasNext(); ) { Object k = localIterator.next();
        data.Values.put(key + "." + k, vmap.get(k));
      }
    }
    data.Values.put(key, value);
  }

  public static Object get(String key)
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Values.get(key);
  }

  public static Mapx<String, Object> getValues()
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Values;
  }

  public static boolean contains(String key)
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return false;
    }
    return data.Values.containsKey(key);
  }

  public static int getInt(String key)
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return 0;
    }
    return data.Values.getInt(key);
  }

  public static long getLong(String key)
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return 0L;
    }
    return data.Values.getLong(key);
  }

  public static String getString(String key)
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Values.getString(key);
  }

  public static void prepareData(HttpServletRequest request) {
    RequestData dc = getRequest();
    if (dc == null) {
      String data = request.getParameter("_ZVING_DATA");
      dc = new RequestData();
      dc.setRequest(request);
      dc.setQueryString(request.getQueryString());
      dc.putAll(ServletUtil.getParameterMap(request));
      if (StringUtil.isNotEmpty(data)) {
        dc.setURL(request.getParameter("_ZVING_URL"));
        dc.remove("_ZVING_DATA");
        dc.remove("_ZVING_URL");
        dc.remove("_ZVING_METHOD");
        dc.remove("_ZVING_DATA_FORMAT");
        if (data.startsWith("<?xml"))
          dc.parseXML(data);
        else if ((data.startsWith("{")) || (data.startsWith("[")))
          dc.parseJSON(data);
      }
      else {
        dc.setURL(request.getRequestURL() + "?" + request.getQueryString());
      }
      dc.setClientIP(ServletUtil.getRealIP(request));
      dc.setServerName(request.getServerName());
      dc.setPort(request.getServerPort());
      dc.setScheme(request.getScheme());

      Enumeration e = request.getHeaderNames();
      while (e.hasMoreElements()) {
        String k = e.nextElement().toString();
        dc.getHeaders().put(k, request.getHeader(k));
      }
      CookieData cookie = getCookies();
      if (cookie == null) {
        cookie = new CookieData(request);
      }
      dc.setCookies(cookie);
      CurrentData cd = (CurrentData)current.get();
      if (cd == null) {
        cd = new CurrentData();
        current.set(cd);
      }
      cd.Request = dc;
      cd.Cookies = cookie;
      cd.Response = new ResponseData();
      cd.Values = new Mapx();
      cd.Errorx = new Errorx();
      cd.VariableResolver = HttpVariableResolver.getInstance(null, request);
    }
  }

  public static Transaction getTransaction() {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    Transaction tran = data.Transaction;
    if (tran == null) {
      tran = new Transaction();
      data.Transaction = tran;
    }
    return tran;
  }

  public static RequestData getRequest()
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Request;
  }

  public static UIFacade getUIFacade()
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.UIFacade;
  }

  public static ResponseData getResponse()
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Response;
  }

  public static CookieData getCookies()
  {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Cookies;
  }

  public static String getCookie(String name)
  {
    CookieData cc = getCookies();
    if (cc == null) {
      return null;
    }
    return cc.getCookie(name);
  }

  public static HttpVariableResolver getVariableResolver() {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.VariableResolver;
  }

  public static void setVariableResolver(HttpVariableResolver context) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.VariableResolver = context;
  }

  public static void setRequest(RequestData request) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.Request = request;
  }

  public static void setResponse(ResponseData response) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.Response = response;
  }

  public static void setCookies(CookieData cc) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.Cookies = cc;
  }

  public static void setUser(User.UserData ud) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.User = ud;
  }

  public static User.UserData getUser() {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.User;
  }

  public static void setDispatcher(Dispatcher d) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.Dispatcher = d;
  }

  public static Dispatcher getDispatcher() {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.Dispatcher;
  }

  public static void setURLHandler(IURLHandler handler) {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    data.URLHandler = handler;
  }

  public static IURLHandler getURLHandler() {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      return null;
    }
    return data.URLHandler;
  }

  public static Errorx getErrorx() {
    CurrentData data = (CurrentData)current.get();
    if (data == null) {
      data = new CurrentData();
      current.set(data);
    }
    if (data.Errorx == null) {
      data.Errorx = new Errorx();
    }
    return data.Errorx;
  }

  public static CurrentData getCurrentData() {
    return (CurrentData)current.get();
  }

  public static class CurrentData
  {
    public RequestData Request;
    public ResponseData Response;
    public UIFacade UIFacade;
    public CookieData Cookies;
    public Transaction Transaction;
    public User.UserData User;
    public HttpVariableResolver VariableResolver;
    public Dispatcher Dispatcher;
    public Mapx<String, Object> Values;
    public Errorx Errorx;
    public IURLHandler URLHandler;
  }
}