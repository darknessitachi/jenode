package com.zving.framework.core.handler;

import com.zving.framework.Config;
import com.zving.framework.core.Dispatcher;
import com.zving.framework.data.BlockingTransaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.template.exception.TemplateNotFoundException;
import com.zving.framework.ui.zhtml.HttpExecuteContext;
import com.zving.framework.ui.zhtml.ZhtmlManagerContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ZhtmlHandler extends AbstractTextHandler
{
  public static final String ID = "com.zving.framework.core.ZhtmlHandler";

  public String getID()
  {
    return "com.zving.framework.core.ZhtmlHandler";
  }

  public boolean match(String url) {
    int i = url.indexOf("?");
    if (i > 0) {
      url = url.substring(0, i);
    }
    return url.endsWith(".zhtml");
  }

  public String getName() {
    return "Zhtml URL Processor";
  }

  public boolean execute(String url, HttpServletRequest request, HttpServletResponse response) {
    int i = url.indexOf("?");
    if (i > 0) {
      url = url.substring(0, i);
    }
    if ((!Config.isInstalled) && (url.indexOf("install.zhtml") < 0) && (url.indexOf("ajax/invoke") < 0)) {
      Dispatcher.forward("/install.zhtml");
      return true;
    }
    if ((url != null) && (url.indexOf("/ajax/invoke") > 0) && (!url.equals("/ajax/invoke"))) {
      Dispatcher.forward("/ajax/invoke");
      return true;
    }
    HttpExecuteContext context = new HttpExecuteContext(ZhtmlManagerContext.getInstance(), request, response);
    try {
      if (!context.execute(url))
        return false;
    }
    catch (TemplateNotFoundException localTemplateNotFoundException) {
      return false;
    } finally {
      BlockingTransaction.clearTransactionBinding(); } BlockingTransaction.clearTransactionBinding();

    ExtendManager.invoke("com.zving.framework.AfterMainFilter", new Object[] { request, response });
    return true;
  }

  public int getOrder() {
    return 9999;
  }
}