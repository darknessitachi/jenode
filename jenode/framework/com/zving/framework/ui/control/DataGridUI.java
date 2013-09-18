package com.zving.framework.ui.control;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.Verify;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.handler.ZAction;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.json.JSONObject;
import com.zving.framework.json.convert.DataTableConvertor;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.VerifyCheck;
import com.zving.framework.ui.html.HtmlTD;
import com.zving.framework.ui.html.HtmlTR;
import com.zving.framework.ui.html.HtmlTable;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.utility.StringUtil;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataGridUI extends UIFacade
{
  public static final int MaxPageSize = 10000;

  @Verify(ignoreAll=true)
  @Priv(login=false)
  public void doWork()
  {
    DataGridAction dga = new DataGridAction();

    dga.setTagBody(StringUtil.htmlDecode($V("_ZVING_TAGBODY")));
    String method = $V("_ZVING_METHOD");
    dga.setMethod(method);

    if (($V("ParentLevel") != null) && (!"".equals($V("ParentLevel")))) {
      dga.setParentLevel(Integer.parseInt($V("ParentLevel")));
      dga.setParentID($V(dga.ParentIdentifierColumnName));
      dga.setTreeLazyLoad(true);
    }

    dga.setID($V("_ZVING_ID"));
    dga.setPageFlag("true".equalsIgnoreCase($V("_ZVING_PAGE")));
    dga.setMultiSelect(!"false".equalsIgnoreCase($V("_ZVING_MULTISELECT")));
    dga.setAutoFill(!"false".equalsIgnoreCase($V("_ZVING_AUTOFILL")));
    dga.setScroll("true".equalsIgnoreCase($V("_ZVING_SCROLL")));
    dga.setLazy("true".equalsIgnoreCase($V("_ZVING_LAZY")));
    if (StringUtil.isNotEmpty($V("_ZVING_CACHESIZE"))) {
      dga.setCacheSize(Integer.parseInt($V("_ZVING_CACHESIZE")));
    }
    dga.setParams(Current.getRequest());
    dga.Response = Current.getResponse();

    if (dga.isPageFlag()) {
      dga.setPageIndex(0);
      if ((this.Request.get("_ZVING_PAGEINDEX") != null) && (!this.Request.get("_ZVING_PAGEINDEX").equals(""))) {
        dga.setPageIndex(Integer.parseInt(this.Request.getString("_ZVING_PAGEINDEX")));
      }
      if (dga.getPageIndex() < 0) {
        dga.setPageIndex(0);
      }
      if (dga.getPageIndex() != 0) {
        dga.setTotal(Integer.parseInt(this.Request.getString("_ZVING_PAGETOTAL")));
      }
      dga.setPageSize(Integer.parseInt($V("_ZVING_SIZE")));
      if (dga.getPageSize() > 10000) {
        dga.setPageSize(10000);
      }
    }

    HtmlTable table = new HtmlTable();
    table.parseHtml(dga.getTagBody());
    dga.setTemplate(table);
    dga.parse();

    String strInsertRowIndex = this.Request.getString("_ZVING_INSERTROW");
    if (StringUtil.isNotEmpty(strInsertRowIndex)) {
      Object dtv = this.Request.get("_ZVING_DATATABLE");
      DataTable dt;
      DataTable dt;
      if (dtv.getClass().getSimpleName() == "DataTable") {
        dt = (DataTable)dtv;
      } else {
        JSONObject dtJSONObj = (JSONObject)dtv;
        dt = DataTableConvertor.reverse(dtJSONObj);
      }
      this.Request.remove("_ZVING_DATATABLE");
      this.Request.remove("_ZVING_INSERTROW");
      dga.bindData(dt);

      HtmlTR tr = dga.getTable().getTR(1);
      $S("TRAttr", tr.getAttributes());
      for (int i = 0; i < tr.Children.size(); i++) {
        $S("TDAttr" + i, tr.getTD(i).getAttributes());
        $S("TDHtml" + i, tr.getTD(i).getInnerHTML());
      }
    } else {
      IMethodLocator m = MethodLocatorUtil.find(method);
      PrivCheck.check(m);

      if (!VerifyCheck.check(m)) {
        String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
        LogUtil.warn(message);
        Current.getResponse().setFailedMessage(message);
        return;
      }
      m.execute(new Object[] { dga });
      $S("HTML", dga.getBodyHtml());
    }
  }

  @Priv(login=false)
  public void toExcel(ZAction za) throws Exception {
    HttpServletRequest request = za.getRequest();
    HttpServletResponse response = za.getResponse();
    request.setCharacterEncoding(Config.getGlobalCharset());
    response.reset();
    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment; filename=Excel_" + DateUtil.getCurrentDateTime("yyyyMMddhhmmss") + ".xls");
    try
    {
      String xls = "_Excel_";
      Mapx params = ServletUtil.getParameterMap(request);
      String ID = params.getString(xls + "_ZVING_ID");
      String tagBody = params.getString(xls + "_ZVING_TAGBODY");
      String pageIndex = params.getString(xls + "_ZVING_PAGEINDEX");
      String pageSize = params.getString(xls + "_ZVING_SIZE");
      String rowTotal = params.getString(xls + "_ZVING_PAGETOTAL");
      String method = params.getString(xls + "_ZVING_METHOD");
      String pageFlag = params.getString(xls + "_ZVING_PAGE");
      String excelPageFlag = params.getString(xls + "_ZVING_ToExcelPageFlag");
      String strWidths = params.getString(xls + "_ZVING_Widths");
      String strIndexes = params.getString(xls + "_ZVING_Indexes");
      String strRows = params.getString(xls + "_ZVING_Rows");

      if ((tagBody != null) && (!tagBody.equals(""))) {
        tagBody = StringUtil.htmlDecode(tagBody);
      }
      DataGridAction dga = new DataGridAction();
      HtmlTable table = new HtmlTable();
      dga.setMethod(method);
      dga.setID(ID);
      dga.setTagBody(tagBody);
      OutputStream os = response.getOutputStream();
      try {
        Class clazz = Class.forName("com.zving.framework.data.DataTableUtil");
        Method htmlTableToExcel = clazz.getMethod("prepareHtmlTableToExcel", new Class[] { OutputStream.class, 
          DataGridAction.class, HtmlTable.class, String.class, String.class, String.class, String.class, String.class, 
          String.class, String.class, String.class, String.class, String.class });
        htmlTableToExcel.invoke(null, new Object[] { os, dga, table, rowTotal, excelPageFlag, pageIndex, pageSize, pageFlag, 
          method, xls, strIndexes, strRows, strWidths });
      } catch (Exception e) {
        e.printStackTrace();
      }

      os.flush();
      os.close();

      os = null;
      response.flushBuffer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sqlBind(DataGridAction dga) {
    dga.bindData(new QueryBuilder($V("_ZVING_DATAGRID_SQL"), new Object[0]));
  }
}