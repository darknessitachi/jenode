package com.zving.framework.cache;

import com.zving.framework.Config;
import com.zving.framework.ConfigLoader;
import com.zving.framework.collection.Mapx;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.security.SystemInfo;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.xml.XMLElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheRefreshServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private static String nodeID = null;
  private static String selfUrl = null;
  private static boolean clusteringEnable = false;
  private static Mapx<String, String> configMap = null;
  private static ArrayList<String> nodes = new ArrayList();

  private static void loadConfig()
  {
    if (configMap == null)
      try {
        configMap = new Mapx();
        List<XMLElement> datas = ConfigLoader.getElements("clustering.config");
        for (XMLElement data : datas) {
          configMap.put((String)data.getAttributes().get("name"), data.getText());
        }
        clusteringEnable = "true".equals(configMap.get("ClusteringEnable"));
        datas = ConfigLoader.getElements("clustering.node");
        if (datas != null) {
          for (XMLElement data : datas) {
            String url = (String)data.getAttributes().get("URL");
            if (!url.endsWith("/")) {
              url = url + "/";
            }
            nodes.add(url);
          }
        }
        LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + 
          "): Load Clustering Configuration----");
      } catch (Exception e) {
        e.printStackTrace();
        LogUtil.info("----" + Config.getAppCode() + "(" + Config.getAppName() + "): Load Clustering Configuration Failure----");
      }
  }

  public static boolean isClusteringEnable()
  {
    loadConfig();
    return clusteringEnable;
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (!isClusteringEnable()) {
      return;
    }
    response.setHeader("Pragma", "No-Cache");
    response.setHeader("Cache-Control", "No-Cache");
    response.setDateHeader("Expires", 0L);

    if (nodeID == null) {
      nodeID = StringUtil.md5Hex(SystemInfo.getMacAddress());
    }

    String p = request.getParameter("p");
    String t = request.getParameter("t");
    String k = request.getParameter("k");
    if (ObjectUtil.empty(p)) {
      return;
    }
    if (ObjectUtil.empty(t)) {
      return;
    }
    if (ObjectUtil.empty(k))
      CacheManager.removeType(p, t);
    else {
      CacheManager.remove(p, t, k);
    }

    response.getWriter().print(nodeID);
  }

  public static void refresh(final String p, final String t, final String k) {
    new Thread() {
      public void run() {
        for (String url : CacheRefreshServlet.nodes)
          if (!url.equals(CacheRefreshServlet.selfUrl))
          {
            StringBuilder sb = new StringBuilder(url);
            sb.append("cacheRefreshServlet.zhtml?");
            sb.append("p=");
            sb.append(p);
            sb.append("&t=");
            sb.append(t);
            if (k != null) {
              sb.append("&k=");
              sb.append(k);
            }
            try {
              String result = ServletUtil.getURLContent(sb.toString()).trim();
              if (result.equals(CacheRefreshServlet.nodeID))
                CacheRefreshServlet.selfUrl = url;
            }
            catch (Exception e) {
              LogUtil.error("Clustering cache refresh failed:" + sb);
            }
          }
      }
    }
    .start();
  }

  public static void refresh(String p, String t) {
    refresh(p, t, null);
  }

  public static void remove(String p, String t) {
    refresh(p, t, null);
  }

  public static void remove(String p, String t, String k) {
    refresh(p, t, k);
  }
}