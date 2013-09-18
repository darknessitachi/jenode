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
import com.zving.framework.ui.html.HtmlDt;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;

public class TreeUI extends UIFacade
{
  @Priv(login=false)
  @Verify(ignoreAll=true)
  public void doWork()
  {
    try
    {
      TreeAction ta = new TreeAction();

      ta.setTagBody(StringUtil.htmlDecode($V("_ZVING_TAGBODY")));
      String method = $V("_ZVING_METHOD");
      ta.setMethod(method);

      if ("true".equals($V("_ZVING_TREE_LAZY"))) {
        if (!"false".equals($V("_ZVING_TREE_EXPAND"))) {
          ta.setExpand(true);
        }
        ta.setLazy(true);
      }

      if (($V("ParentLevel") != null) && (!"".equals($V("ParentLevel")))) {
        ta.setParentLevel(Integer.parseInt($V("ParentLevel")));
        ta.setLazyLoad(true);
      }

      ta.setID($V("_ZVING_ID"));
      ta.setParams(this.Request);

      String levelStr = $V("_ZVING_TREE_LEVEL");
      String style = $V("_ZVING_TREE_STYLE");
      if (ObjectUtil.empty(levelStr)) {
        levelStr = "0";
      }

      int level = Integer.parseInt(levelStr);
      if (level <= 0) {
        level = 999;
      }
      ta.setLevel(level);
      ta.setStyle(style);

      ta.setPageSize(10000);
      if (($V("_ZVING_SIZE") != null) && (!$V("_ZVING_SIZE").equals(""))) {
        ta.setPageSize(Integer.parseInt($V("_ZVING_SIZE").toString()));
        if (ta.getPageSize() > 10000) {
          ta.setPageSize(10000);
        }
      }
      ta.setPageIndex(0);
      if ((this.Request.get("_ZVING_PAGEINDEX") != null) && (!this.Request.get("_ZVING_PAGEINDEX").equals(""))) {
        ta.setPageIndex(Integer.parseInt(this.Request.get("_ZVING_PAGEINDEX").toString()));
      }
      if (ta.getPageIndex() < 0) {
        ta.setPageIndex(0);
      }
      if (($V("_ZVING_PAGETOTAL") != null) && (!$V("_ZVING_PAGETOTAL").equals(""))) {
        ta.setTotal(Integer.parseInt(this.Request.get("_ZVING_PAGETOTAL").toString()));
        if (ta.getPageIndex() > Math.ceil(ta.getTotal() * 1.0D / ta.getPageSize())) {
          ta.setPageIndex(new Double(Math.floor(ta.getTotal() * 1.0D / ta.getPageSize())).intValue());
        }
      }

      HtmlDt p = new HtmlDt();
      p.parseHtml(ta.getTagBody());
      ta.setTemplate(p);

      IMethodLocator m = MethodLocatorUtil.find(method);
      PrivCheck.check(m);

      if (!VerifyCheck.check(m)) {
        String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
        LogUtil.warn(message);
        Current.getResponse().setFailedMessage(message);
        return;
      }
      m.execute(new Object[] { ta });

      $S("HTML", ta.getHtml());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}