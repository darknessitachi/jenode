package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.Verify;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.VerifyCheck;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;

public class DataListUI extends UIFacade
{
  @Verify(ignoreAll=true)
  @Priv(login=false)
  public void doWork()
  {
    try
    {
      DataListAction dla = new DataListAction();

      dla.setTagBody(StringUtil.htmlDecode($V("_ZVING_TAGBODY")));
      dla.setPage("true".equalsIgnoreCase($V("_ZVING_PAGE")));
      String method = $V("_ZVING_METHOD");
      dla.setMethod(method);
      dla.setID($V("_ZVING_ID"));
      dla.setSortEnd($V("_ZVING_SORTEND"));
      dla.setDragClass($V("_ZVING_DRAGCLASS"));
      dla.setParams(this.Request);
      dla.setPageSize(Integer.parseInt($V("_ZVING_SIZE")));
      if (dla.getPageSize() > 10000) {
        dla.setPageSize(10000);
      }
      if (dla.isPage()) {
        dla.setPageIndex(0);
        if ((this.Request.get("_ZVING_PAGEINDEX") != null) && (!this.Request.get("_ZVING_PAGEINDEX").equals(""))) {
          dla.setPageIndex(Integer.parseInt(this.Request.get("_ZVING_PAGEINDEX").toString()));
        }
        if (dla.getPageIndex() < 0) {
          dla.setPageIndex(0);
        }
        if ((this.Request.get("_ZVING_PAGETOTAL") != null) && (!this.Request.get("_ZVING_PAGETOTAL").equals(""))) {
          dla.setTotal(Integer.parseInt(this.Request.get("_ZVING_PAGETOTAL").toString()));
          if (dla.getPageIndex() > Math.ceil(dla.getTotal() * 1.0D / dla.getPageSize())) {
            dla.setPageIndex(new Double(Math.floor(dla.getTotal() * 1.0D / dla.getPageSize())).intValue());
          }
        }
      }

      IMethodLocator m = MethodLocatorUtil.find(method);
      PrivCheck.check(m);

      if (!VerifyCheck.check(m)) {
        String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
        LogUtil.warn(message);
        Current.getResponse().setFailedMessage(message);
        return;
      }
      m.execute(new Object[] { dla });
      $S("HTML", dla.getHtml());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}