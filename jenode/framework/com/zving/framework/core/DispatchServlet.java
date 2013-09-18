package com.zving.framework.core;

import com.zving.framework.Config;
import com.zving.framework.CookieData;
import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.SessionListener;
import com.zving.framework.User;
import com.zving.framework.User.UserData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.config.DefaultServletName;
import com.zving.framework.config.SetRequestEncoding;
import com.zving.framework.config.SetResponseEncoding;
import com.zving.framework.core.handler.ZhtmlHandler;
import com.zving.framework.data.DBConnPool;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.xml.XMLDocument;
import com.zving.framework.xml.XMLElement;
import com.zving.framework.xml.XMLParser;
import com.zving.preloader.PreClassLoader;
import com.zving.preloader.facade.HttpSessionListenerFacade;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DispatchServlet extends HttpServlet
{
  private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
  private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";
  private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";
  private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";
  private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";
  private static final long serialVersionUID = 1L;
  private String[] NoFilterPaths;
  protected static boolean InitFlag = true;
  private static ServletContext context = null;
  protected static Mapx<Thread, ClassLoader> httpThreads = new Mapx();
  protected static long lastThreadCheckTime = 0L;
  private String defaultServletName;
  private String[] welcomeFiles;

  public void init(ServletConfig config)
    throws ServletException
  {
    if (Config.ServletMajorVersion == 0) {
      context = config.getServletContext();
      Config.getMapx().put("System.ContainerInfo", context.getServerInfo());
      Config.setPluginContext(true);
      Config.getJBossInfo();
      Config.ServletMajorVersion = context.getMajorVersion();
      Config.ServletMinorVersion = context.getMinorVersion();
      Config.setValue("App.Uptime", System.currentTimeMillis());
      ExtendManager.getInstance().start();
      setServletContext(context);

      String xml = FileUtil.readText(Config.getWEBINFPath() + "web.xml");
      XMLParser p = new XMLParser(xml);
      List list = p.parse().elements("web-app.welcome-file-list.welcome-file");
      this.welcomeFiles = new String[list.size()];
      int i = 0;
      for (XMLElement ele : list) {
        this.welcomeFiles[(i++)] = ele.getText().trim();
      }

      LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): Filter Initialized----");
    }
    String paths = config.getInitParameter("noFilterPath");
    String path;
    if (StringUtil.isNotEmpty(paths)) {
      this.NoFilterPaths = paths.split(",");
      for (int i = 0; i < this.NoFilterPaths.length; i++) {
        path = this.NoFilterPaths[i];
        if (!path.startsWith("/")) {
          path = "/" + path;
        }
        if (!path.endsWith("/")) {
          path = path + "/";
        }
        this.NoFilterPaths[i] = path;
      }
    }
    for (IURLHandler up : URLHandlerService.getInstance().getAll())
      up.init();
  }

  public void setServletContext(ServletContext servletContext)
  {
    if (servletContext.getNamedDispatcher("default") != null)
      this.defaultServletName = "default";
    else if (servletContext.getNamedDispatcher("_ah_default") != null)
      this.defaultServletName = "_ah_default";
    else if (servletContext.getNamedDispatcher("resin-file") != null)
      this.defaultServletName = "resin-file";
    else if (servletContext.getNamedDispatcher("FileServlet") != null)
      this.defaultServletName = "FileServlet";
    else if (servletContext.getNamedDispatcher("SimpleFileServlet") != null)
      this.defaultServletName = "SimpleFileServlet";
    else if (ObjectUtil.notEmpty(DefaultServletName.getValue()))
      this.defaultServletName = DefaultServletName.getValue();
    else
      throw new IllegalStateException("Unable to locate the default servlet for serving static content. Please set the 'defaultServletName' property explicitly.");
  }

  public void forwardToDefaultServlet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    RequestDispatcher rd = context.getNamedDispatcher(this.defaultServletName);
    if (rd == null) {
      throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet '" + this.defaultServletName + 
        "'");
    }
    rd.forward(request, response);
  }

  public boolean isNoFilterPath(String url) {
    if (this.NoFilterPaths == null) {
      return false;
    }
    for (int i = 0; i < this.NoFilterPaths.length; i++) {
      if (url.indexOf(this.NoFilterPaths[i]) >= 0) {
        return true;
      }
    }
    return false;
  }

  public void service(ServletRequest req, ServletResponse rep) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)rep;
    String requestURI = request.getRequestURI();
    String context = request.getContextPath();
    String url = requestURI.substring(context.length(), requestURI.length());
    url = url.replaceAll("/+", "/");
    if (ObjectUtil.empty(url)) {
      response.sendRedirect(context + "/");
      return;
    }
    if (isNoFilterPath(url)) {
      forwardToDefaultServlet(request, response);
      return;
    }

    String contextPath = request.getContextPath();
    if (!contextPath.endsWith("/")) {
      contextPath = contextPath + "/";
    }
    if (Config.isComplexDepolyMode()) {
      User.setValue("App.ContextPath", contextPath);
    }
    if (InitFlag) {
      Config.setValue("App.ContextPath", contextPath);
      InitFlag = false;
    }

    if (SetRequestEncoding.getValue()) {
      request.setCharacterEncoding(Config.getGlobalCharset());
    }
    if (SetResponseEncoding.getValue()) {
      if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
        response.setContentType("text/html;charset=" + Config.getGlobalCharset());
      else {
        response.setCharacterEncoding(Config.getGlobalCharset());
      }
    }
    Current.prepareData(request);
    try {
      Current.setDispatcher(new Dispatcher());
      if (Thread.currentThread().getContextClassLoader() != PreClassLoader.getInstance()) {
        httpThreads.put(Thread.currentThread(), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(PreClassLoader.getInstance());
        if (System.currentTimeMillis() - lastThreadCheckTime > 300000L) {
          synchronized (ZhtmlHandler.class) {
            for (Thread t : httpThreads.keyArray()) {
              if (!t.isAlive()) {
                httpThreads.remove(t);
              }
            }
            lastThreadCheckTime = System.currentTimeMillis();
          }
        }

      }

      if (!"true".equals(request.getParameter("_ZVING_NOSESSION"))) {
        HttpSession session = request.getSession(true);
        if (session != null) {
          User.UserData u = SessionListener.getUserDataFromSession(session);
          if (u == null) {
            boolean flag = true;
            if (Config.isDebugMode()) {
              cs = request.getCookies();
              if (cs != null) {
                for (int i = 0; i < cs.length; i++) {
                  if (cs[i].getName().equals("JSESSIONID")) {
                    u = User.getCachedUser(cs[i].getValue());
                    if (u != null) {
                      flag = false;
                      HttpSessionListenerFacade.setSession(session.getId(), session);
                      break;
                    }
                  }
                }
              }
            }
            if (flag) {
              u = createUserData(request);
            }
          }
          u.setSessionID(session.getId());
          if (HttpSessionListenerFacade.getSession(session.getId()) == null) {
            HttpSessionListenerFacade.setSession(session.getId(), session);
          }
          User.setCurrent(u);
        } else {
          User.setCurrent(createUserData(request));
        }
      }

      handleURL(url, request, response);
      try
      {
        if ((!"true".equals(request.getParameter("_ZVING_NOSESSION"))) && (!"true".equals(request.getAttribute("_ZVING_NOSESSION"))) && 
          (request.getSession(false) != null)) {
          User.UserData ud = User.getCurrent();
          ud.setSessionID(request.getSession(false).getId());
          request.getSession(false).setAttribute("_ZVING_USER", ud);
        }
      }
      catch (Exception localException) {
      }
      if (!Errorx.hasDealed()) {
        LogUtil.warn("Error not dealed:" + Errorx.printString());
      }
      if (Current.getCookies() != null) {
        Current.getCookies().write(request, response);
      }
      if (Current.getResponse() != null) {
        ResponseData r = Current.getResponse();
        Mapx map = r.getHeaders();
        for (String key : map.keySet())
          response.setHeader(key, (String)map.get(key));
      }
    }
    catch (RuntimeException e) {
      boolean catched = false;
      int j;
      int i;
      for (Cookie[] cs = ExceptionCatcherService.getInstance().getAll().iterator(); cs.hasNext(); 
        i < j)
      {
        IExceptionCatcher ec = (IExceptionCatcher)cs.next();
        Class[] arrayOfClass;
        j = (arrayOfClass = ec.getTargetExceptionClass()).length; i = 0; continue; Class c = arrayOfClass[i];
        if (c.isInstance(e)) {
          ec.doCatch(e, request, response);
          catched = true;
        }
        i++;
      }

      if (!catched) {
        e.printStackTrace();
        throw e;
      }
    } finally {
      Current.clear();
    }
  }

  public static User.UserData createUserData(HttpServletRequest request) {
    User.UserData u = new User.UserData();
    try {
      String lang = LangUtil.getLanguage(request);
      Mapx map = LangUtil.getSupportedLanguages();
      if (map.size() < 1) {
        throw new RuntimeException("No supportd language found");
      }
      if (map.containsKey(lang))
        u.setLanguage(lang);
      else
        u.setLanguage(map.keySet().toArray()[0].toString());
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    return u;
  }

  private void handleURL(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      boolean flag = false;
      for (IURLHandler up : URLHandlerService.getInstance().getAll()) {
        if (up.match(url)) {
          Current.setURLHandler(up);
          if (up.handle(url, request, response)) {
            flag = true;
            return;
          }
        }
      }
      if (!flag)
      {
        File f = new File(context.getRealPath(url));
        if (f.exists()) {
          if (f.isFile()) {
            forwardToDefaultServlet(request, response);
          } else {
            if (!url.endsWith("/")) {
              response.sendRedirect(request.getContextPath() + url + "/");
              return;
            }
            for (String str : this.welcomeFiles)
              for (IURLHandler up : URLHandlerService.getInstance().getAll())
                if (up.match(url + str)) {
                  Current.setURLHandler(up);
                  if (up.handle(url + str, request, response)) {
                    flag = true;
                    return;
                  }
                }
          }
        }
      }
    }
    catch (Dispatcher.DispatchException e)
    {
      Dispatcher d = Current.getDispatcher();
      if (d != null)
        if (d.forwardURL != null) {
          url = d.forwardURL;
          d.forwardURL = null;
          handleURL(url, request, response);
        } else if (d.redirectURL != null) {
          url = d.redirectURL;
          d.redirectURL = null;
          response.sendRedirect(url);
        }
    }
  }

  public void destroy() {
    Thread t;
    try {
      for (IURLHandler up : URLHandlerService.getInstance().getAll())
        up.destroy();
    }
    finally {
      DBConnPool.cancelKeepAliveTimer();
      for (Thread t : httpThreads.keyArray())
        if (t.isAlive())
          try {
            t.setContextClassLoader((ClassLoader)httpThreads.get(t));
          }
          catch (Throwable localThrowable)
          {
          }
    }
  }
}