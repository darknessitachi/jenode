package com.zving.framework.ui.zhtml;

import com.zving.framework.Config;
import com.zving.framework.User;
import com.zving.framework.User.UserData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.DispatchServlet;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.ITemplateManager;
import com.zving.framework.template.TemplateCompiler;
import com.zving.framework.template.TemplateExecutor;
import com.zving.framework.template.exception.TemplateCompileException;
import com.zving.framework.template.exception.TemplateNotFoundException;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.resource.UIResourceFile;
import com.zving.framework.utility.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ZhtmlManager
  implements ITemplateManager
{
  private static Mapx<String, TemplateExecutor> map = new Mapx();

  public boolean execute(String url, AbstractExecuteContext context)
    throws TemplateRuntimeException, TemplateCompileException
  {
    String fileName = url;
    if (fileName.indexOf("?") > 0) {
      fileName = fileName.substring(0, fileName.indexOf("?"));
    }
    if (fileName.indexOf("#") > 0) {
      fileName = fileName.substring(0, fileName.indexOf("#"));
    }
    if (fileName.startsWith("/")) {
      fileName = fileName.substring(1);
    }
    TemplateExecutor je = getExecutor(fileName);
    if ((context instanceof HttpExecuteContext))
    {
      HttpServletRequest request = ((HttpExecuteContext)context).getRequest();
      HttpSession session = request.getSession(false);
      if ((session == null) && (je.isSessionFlag())) {
        User.UserData u = DispatchServlet.createUserData(request);
        session = request.getSession(true);
        u.setSessionID(session.getId());
        User.setCurrent(u);
      }
      if (!je.isSessionFlag()) {
        request.setAttribute("_ZVING_NOSESSION", "true");
      }
    }
    je.execute(context);
    return true;
  }

  public static String getSourceInJar(String fileName) {
    InputStream is = null;
    try {
      UIResourceFile urf = new UIResourceFile(fileName);
      if (!urf.exists())
        return null;
      UIResourceFile urf;
      is = urf.toStream();
      return new String(FileUtil.readByte(is), Config.getGlobalCharset());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (is != null)
          is.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static boolean needReload(TemplateExecutor je, String fileName)
    throws FileNotFoundException
  {
    if (je.isFromJar()) {
      return false;
    }
    if (System.currentTimeMillis() - je.getLastCheckTime() > 3000L) {
      File f = new File(FileUtil.normalizePath(Config.getContextRealPath() + "/" + fileName));
      if (!f.exists()) {
        map.remove(fileName);
        throw new FileNotFoundException("File not found:" + fileName);
      }
      if (f.lastModified() != je.getLastModified()) {
        je.setLastCheckTime(System.currentTimeMillis());
        return true;
      }
    }
    return false;
  }

  public TemplateExecutor getExecutor(String fileName) throws TemplateCompileException {
    TemplateExecutor je = (TemplateExecutor)map.get(fileName);
    try {
      if ((je == null) || (needReload(je, fileName))) {
        TemplateCompiler jc = new TemplateCompiler(ZhtmlManagerContext.getInstance());
        String fullPath = Config.getContextRealPath() + "/" + fileName;
        fullPath = FileUtil.normalizePath(fullPath);
        if (new File(fullPath).exists()) {
          jc.compile(fullPath);
          je = jc.getExecutor();
        } else {
          String source = getSourceInJar(fileName);
          if (source != null) {
            jc.setFileName(fileName);
            jc.compileSource(source);
            je = jc.getExecutor();
            je.setFromJar(true);
          } else {
            throw new TemplateNotFoundException("Template not found: " + fileName);
          }
        }
        map.put(fileName, je);
      }
    } catch (FileNotFoundException e) {
      throw new TemplateNotFoundException(e.getMessage());
    }
    return je;
  }
}