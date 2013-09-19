package com.zving.platform.ui;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.Verify;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Filter;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.FixedCodeType;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.pub.OrderUtil;
import com.zving.platform.service.CodeService;
import com.zving.schema.ZDCode;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Alias("Code")
public class CodeUI extends UIFacade
{
  @Priv("Platform.Code")
  public void bindCodeTypeGrid(DataGridAction dga)
  {
    final String SearchCodeType = dga.getParam("SearchCodeType");
    Q qb = new Q("select * from ZDCode where ParentCode ='System'", new Object[0]);
    qb.append(" order by CodeType,ParentCode", new Object[0]);

    DataTable dt = qb.fetch();
    LangUtil.decode(dt, "CodeName");
    if (StringUtil.isNotEmpty(SearchCodeType)) {
      dt = dt.filter(new Filter() {
        public boolean filter(DataRow dr) {
          return (dr.getString("CodeType").indexOf(SearchCodeType) >= 0) || (dr.getString("CodeName").indexOf(SearchCodeType) >= 0);
        }
      });
    }
    dt.insertColumn("Fixed");
    dt.insertColumn("ID");
    for (DataRow dr : dt) {
      FixedCodeType fct = (FixedCodeType)CodeService.getInstance().get(dr.getString("CodeType"));
      if (fct != null)
        dr.set("Fixed", "Y");
      else {
        dr.set("Fixed", "N");
      }
      dr.set("ID", dr.getString("CodeType") + dr.getString("ParentCode") + dr.getString("CodeValue"));
    }
    YesOrNo.decodeYesOrNoIcon(dt, "Fixed");
    dga.setTotal(dt.getRowCount());

    dga.bindData(dt.getPagedDataTable(dga.getPageSize(), dga.getPageIndex()));
  }

  @Priv("Platform.Code")
  public void bindCodeListGrid(DataGridAction dga) {
    Q qb = new Q("select * from ZDCode where ParentCode=?", new Object[] { dga.getParam("CodeType") });
    qb.append(" order by CodeOrder,CodeType,ParentCode", new Object[0]);

    DataTable dt = qb.fetch();
    LangUtil.decode(dt, "CodeName");
    dt.insertColumn("Fixed");
    for (DataRow dr : dt) {
      FixedCodeType fct = (FixedCodeType)CodeService.getInstance().get(dr.getString("CodeType"));
      if (fct != null)
        dr.set("Fixed", fct.contains(dr.getString("CodeValue")) ? "Y" : "N");
      else {
        dr.set("Fixed", "N");
      }
    }
    YesOrNo.decodeYesOrNoIcon(dt, "Fixed");
    dga.setTotal(dt.getRowCount());
    dga.bindData(dt);
  }

  @Priv("Platform.Code")
  public void initDialog() throws UnsupportedEncodingException {
    String CodeType = $V("CodeType");
    String ParentCode = $V("ParentCode");
    String CodeValue = $V("CodeValue");
    if ((ObjectUtil.empty(CodeValue)) || (ObjectUtil.empty(CodeType))) {
      $S("CodeName", "");
      return;
    }
    ZDCode code = new ZDCode();
    code.setCodeType(CodeType);
    code.setParentCode(ParentCode);
    code.setCodeValue(CodeValue);
    code.fill();
    this.Response.putAll(code.toMapx());
    FixedCodeType fct = (FixedCodeType)CodeService.getInstance().get(CodeType);
    if (fct != null)
      $S("Fixed", Boolean.valueOf(true));
  }

  @Priv("Platform.Code")
  public void initList()
  {
    String codeType = $V("CodeType");
    ZDCode code = new ZDCode();
    code.setCodeType(codeType);
    code.setParentCode("System");
    code.setCodeValue("System");
    code.fill();
    this.Response.putAll(code.toMapx());
    FixedCodeType fct = (FixedCodeType)CodeService.getInstance().get(codeType);
    if (fct != null)
      $S("AllowAddItem", Boolean.valueOf(fct.allowAddItem()));
    else
      $S("AllowAddItem", Boolean.valueOf(true));
  }

  @Priv("Platform.Code.Add||Platform.Code.Edit")
  @Verify
  public void addOrSave() {
    ZDCode code = new ZDCode();
    code.setValue(this.Request);
    boolean updateFlag = "Edit".equals($V("Action"));
    if (updateFlag) {
      code.setCodeValue($V("OldCodeValue"));
    }
    if ((code.fill()) && 
      (!updateFlag)) {
      if (!"System".equals($V("ParentCode")))
        fail(code.getCodeValue() + " " + Lang.get("Common.Exists"));
      else {
        fail(Lang.get("Code.CodeType") + " " + code.getCodeType() + Lang.get("Common.Exists"));
      }
      return;
    }

    code.setValue(this.Request);
    code.setCodeName(LangUtil.getI18nFieldValue("CodeName"));
    Transaction tran = new Transaction();
    if (!updateFlag) {
      code.setCodeOrder(System.currentTimeMillis());
      code.setAddTime(new Date());
      code.setAddUser(User.getUserName());
      tran.insert(code);
    } else {
      code.setModifyTime(new Date());
      code.setModifyUser(User.getUserName());
      tran.update(code);
    }
    if (tran.commit()) {
      if (!"System".equals(code.getParentCode())) {
        CacheManager.removeType("Code", code.getCodeType());
      }
      success(Lang.get("Common.ExecuteSuccess"));
    } else {
      fail(Lang.get("Common.ExecuteFailed"));
    }
  }

  @Priv("Platform.Code.Delete")
  public void del()
  {
    DataTable dt = (DataTable)this.Request.get("DT");
    DAOSet set = new DAOSet();
    for (int i = 0; i < dt.getRowCount(); i++) {
      ZDCode code = new ZDCode();
      code.setValue(dt.getDataRow(i));
      code.fill();
      set.add(code);
      if ("System".equals(code.getParentCode())) {
        ZDCode childCode = new ZDCode();
        childCode.setParentCode(code.getCodeType());
        set.add(childCode.query());
      }
    }
    if (set.deleteAndBackup()) {
      for (int i = 0; i < set.size(); i++) {
        CacheManager.remove("Code", ((ZDCode)set.get(i)).getCodeType(), ((ZDCode)set.get(i)).getCodeValue());
        if ("System".equals(((ZDCode)set.get(i)).getParentCode())) {
          CacheManager.removeType("Code", ((ZDCode)set.get(i)).getCodeType());
        }
      }
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      fail(Lang.get("Common.DeleteFailed"));
    }
  }

  @Priv("Platform.Code.Edit")
  public void sortColumn() {
    long targetOrderFlag = Long.valueOf($V("TargetOrderFlag")).longValue();
    if ((StringUtil.isNull($V("CodeType"))) || (StringUtil.isNull($V("CodeValue"))) || (StringUtil.isNull($V("ParentCode"))) || 
      (targetOrderFlag <= 0L)) {
      fail(Lang.get("Common.ExecuteFailed"));
      return;
    }
    ZDCode schema = new ZDCode();
    schema.setCodeType($V("CodeType"));
    schema.setParentCode($V("ParentCode"));
    schema.setCodeValue($V("CodeValue"));
    if (!schema.fill()) {
      fail(Lang.get("Common.ExecuteFailed"));
      return;
    }
    OrderUtil.updateOrder(new ZDCode().table(), "CodeOrder", targetOrderFlag, " and ParentCode='" + $V("ParentCode") + "'", schema, 
      Current.getTransaction());

    if (Current.getTransaction().commit())
      success(Lang.get("Common.ExecuteSuccess"));
    else
      fail(Lang.get("Common.ExecuteFailed"));
  }
}