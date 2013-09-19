package com.zving.framework.core.handler;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.SessionListener;
import com.zving.framework.User;
import com.zving.framework.User.UserData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.IURLHandler;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.DataCollection;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.VerifyCheck;
import com.zving.framework.security.exception.PrivException;
import com.zving.framework.ui.control.UploadAction;
import com.zving.framework.ui.control.UploadUI;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.preloader.facade.HttpSessionListenerFacade;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadHandler
  implements IURLHandler
{
  public static final String ID = "com.zving.framework.core.UploadHandler";
  private static Object mutex = new Object();

  private static Mapx<String, TaskFiles> uploadFileMap = new Mapx(5000);

  public boolean match(String url) {
    return url.startsWith("/ZUploader.zhtml");
  }

  public String getID() {
    return "com.zving.framework.core.UploadHandler";
  }

  public String getName() {
    return "Upload Invoke Processor";
  }

  public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("text/html; charset=" + Config.getGlobalCharset());
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter out = response.getWriter();
    FileItemFactory fileFactory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(fileFactory);
    upload.setHeaderEncoding("UTF-8");
    upload.setSizeMax(2097152000L);
    try {
      List items = upload.parseRequest(request);
      Mapx fields = new Mapx();
      ArrayList files = new ArrayList();
      Iterator iter = items.iterator();
      while (iter.hasNext()) {
        FileItem item = (FileItem)iter.next();
        if (item.isFormField()) {
          fields.put(item.getFieldName(), item.getString("UTF-8"));
        } else {
          String OldFileName = item.getName();
          long size = item.getSize();
          if (((OldFileName != null) && (!OldFileName.equals(""))) || (size != 0L))
          {
            LogUtil.info("-----UploadFileName:-----" + OldFileName);
            files.add(item);
          }
        }
      }
      String taskID = (String)fields.get("_ZUploder_TaskID");
      String totalStr = (String)fields.get("_ZUploader_Total");

      UploadUI.setTask(taskID, LangMapping.get("Framework.Upload.Status"));

      String ids = (String)fields.get("_SessionID");
      if (ObjectUtil.empty(ids)) {
        return true;
      }
      HttpSession session = request.getSession();
      String[] arr = ids.split("\\,");
      HttpSession sessionOld = null;
      for (String sessionID : arr) {
        if (session.getId().equals(sessionID)) {
          break;
        }
        sessionOld = HttpSessionListenerFacade.getSession(sessionID);
        if (sessionOld != null) {
          break;
        }
      }
      if (sessionOld != null)
      {
        Enumeration en = sessionOld.getAttributeNames();
        while (en.hasMoreElements()) {
          String n = (String)en.nextElement();
          session.setAttribute(n, sessionOld.getAttribute(n));
        }
        User.UserData u = SessionListener.getUserDataFromSession(session);
        if (u != null) {
          User.setCurrent(u);
        }
      }

      int total = 1;
      if (ObjectUtil.notEmpty(totalStr)) {
        total = Integer.parseInt(totalStr);
      }
      TaskFiles uploadedFiles = null;
      synchronized (mutex) {
        checkTimeout();
        uploadedFiles = (TaskFiles)uploadFileMap.get(taskID);
        if (uploadedFiles == null) {
          uploadedFiles = new TaskFiles();
          uploadFileMap.put(taskID, uploadedFiles);
        }
        uploadedFiles.LastTime = System.currentTimeMillis();
      }
      uploadedFiles.Files.add((FileItem)files.get(0));
      if (total == uploadedFiles.Files.size()) {
        String method = (String)fields.get("_Method");
        IMethodLocator m = MethodLocatorUtil.find(method);
        Current.getRequest().putAll(fields);
        try {
          PrivCheck.check(m);
        } catch (PrivException e) {
          uploadFileMap.remove(taskID);
          for (FileItem file : uploadedFiles.Files) {
            file.delete();
          }
          throw e;
        }

        if (!VerifyCheck.check(m)) {
          String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
          LogUtil.warn(message);

          uploadFileMap.remove(taskID);
          for (FileItem file : uploadedFiles.Files) {
            file.delete();
          }
          return true;
        }

        UploadAction ua = new UploadAction();
        ua.setItems(uploadedFiles.Files);

        m.execute(new Object[] { ua });

        for (FileItem file : uploadedFiles.Files) {
          file.delete();
        }
        synchronized (mutex) {
          uploadFileMap.remove(taskID);
        }
        UploadUI.setTask(taskID, "Finished");
        response.getWriter().write(Current.getResponse().toXML());
      } else {
        response.getWriter().write(new DataCollection().toXML());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    out.flush();
    out.close();
    return true;
  }

  private static void checkTimeout() {
     ArrayList<String> arr = uploadFileMap.keyArray();
    long yesterday = System.currentTimeMillis() - 86400000L;
    for (String id : arr) {
      TaskFiles tf = (TaskFiles)uploadFileMap.get(id);
      if (tf != null)
      {
        if (tf.LastTime < yesterday)
          uploadFileMap.remove(id);
      }
    }
  }

  public void init()
  {
  }

  public void destroy()
  {
  }

  public int getOrder()
  {
    return 9998;
  }

  private static class TaskFiles
  {
    public long LastTime;
    public ArrayList<FileItem> Files = new ArrayList();
  }
}