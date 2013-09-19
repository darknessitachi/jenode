package com.zving.platform.ui;

import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.pub.NoUtil;
import com.zving.platform.pub.PlatformCache;
import com.zving.platform.pub.PlatformUtil;
import com.zving.schema.ZDBranch;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Alias("Branch")
public class BranchUI extends UIFacade
{
  @Priv("Platform.Branch")
  public void initDialog()
  {
    String branchInnerCode = $V("BranchInnerCode");
    if (StringUtil.isNotEmpty(branchInnerCode)) {
      ZDBranch branch = new ZDBranch();
      branch.setBranchInnerCode(branchInnerCode);
      branch.fill();
      this.Response.putAll(branch.toMapx());
      String ParentName = new Q("select name from zdbranch where branchInnercode=?", new Object[] { branch.getParentInnerCode() })
        .executeString();
      ParentName = LangUtil.decode(ParentName);
      if (branch.getParentInnerCode().equals(branch.getBranchInnerCode())) {
        $S("ParentInnerCode", "");
      }
      $S("ParentName", ParentName);
      $S("ManagerName", getManagersRealName(branch.getManager()));
    } else {
      $S("Type", "1");
    }
  }

  private static String getManagersRealName(String managers) {
    if (StringUtil.isEmpty(managers)) {
      return "";
    }
    String[] arr = managers.split("\\,");
    StringBuilder sb = new StringBuilder();
    for (String user : arr)
      if (PlatformCache.getUser(user) != null)
      {
        if (sb.length() != 0) {
          sb.append(",");
        }
        sb.append(PlatformUtil.getUserRealName(user));
      }
    return sb.toString();
  }

  @Priv
  public DataTable getBranchTable() {
    DataTable dt = new Q("select BranchInnerCode,Name,TreeLevel,ParentInnerCode from zdbranch where branchinnercode like ? order by orderflag,branchinnercode", new Object[] { 
      User.getBranchInnerCode() + "%" })
      .fetch();
    LangUtil.decode(dt, "Name");
    PlatformUtil.indentDataTable(dt, 1, 2, 1);
    return dt;
  }

  @Priv("Platform.Branch")
  public void dg1DataBind(DataGridAction dga) {
    Q qb = new Q("select * from zdbranch where branchinnercode like ? order by orderflag", new Object[] { 
      User.getBranchInnerCode() + "%" });
    DataTable dt = qb.fetch();
    dt = DataGridAction.sortTreeDataTable(dt, "BranchInnerCode", "ParentInnerCode");
    dt.insertColumn("ManagerName");
    for (DataRow dr : dt) {
      if (ObjectUtil.notEmpty(dr.getString("Manager"))) {
        dr.set("ManagerName", getManagersRealName(dr.getString("Manager")));
      }
    }
    LangUtil.decode(dt, "Name");
    LangUtil.decode(dt, "ManagerName");
    dga.bindData(dt);
  }

  @Priv("Platform.Branch.Add")
  public void add()
  {
    if (isNameOrBranchCodeExists($V("Name"), $V("BranchCode"), null, $V("ParentInnerCode"))) {
      fail(Lang.get("Platform.DuplicateNameOrBranchCode"));
      return;
    }
    String parentInnerCode = $V("ParentInnerCode");
    Transaction trans = new Transaction();
    ZDBranch branch = null;
    if (StringUtil.isEmpty(parentInnerCode)) {
      parentInnerCode = "0";
      branch = new ZDBranch();
      branch.setValue(this.Request);
      branch.setBranchInnerCode(NoUtil.getMaxNo("BranchInnerCode", 4));
      branch.setParentInnerCode(parentInnerCode);
      branch.setTreeLevel(1L);
      branch.setType("0");
      branch.setIsLeaf("Y");

      DataTable dt = new Q("select * from zdbranch order by orderflag", new Object[0]).fetch();
      String orderflag = "";
      if ((dt != null) && (dt.getRowCount() > 0))
        orderflag = dt.getString(dt.getRowCount() - 1, "OrderFlag");
      else {
        orderflag = "0";
      }
      trans.add(new Q("update zdbranch set orderflag=orderflag+1 where orderflag>?", new Object[] { orderflag }));
      branch.setOrderFlag(orderflag + 1);
      branch.setAddTime(new Date());
      branch.setAddUser(User.getUserName());
    } else {
      ZDBranch pBranch = new ZDBranch();
      pBranch.setBranchInnerCode(parentInnerCode);
      pBranch.fill();
      long pTreeLevel = pBranch.getTreeLevel();

      branch = new ZDBranch();
      branch.setValue(this.Request);
      branch.setBranchInnerCode(NoUtil.getMaxNo("BranchInnerCode", pBranch.getBranchInnerCode(), 4));
      branch.setParentInnerCode(pBranch.getBranchInnerCode());
      branch.setTreeLevel(pTreeLevel + 1L);
      branch.setType("0");
      branch.setIsLeaf("Y");
      branch.setAddTime(new Date());
      branch.setAddUser(User.getUserName());

      DataTable dt = new Q("select * from zdbranch where BranchInnerCode like '" + pBranch.getBranchInnerCode() + 
        "%' order by orderflag", new Object[0])
        .fetch();
      long orderflag = Long.parseLong(dt.getString(dt.getRowCount() - 1, "OrderFlag"));
      branch.setOrderFlag(orderflag + 1L);
      trans.add(new Q("update zdbranch set IsLeaf='N' where branchInnerCode=?", new Object[] { pBranch.getBranchInnerCode() }));
      trans.add(new Q("update zdbranch set orderflag=orderflag+1 where orderflag>?", new Object[] { Long.valueOf(orderflag) }));
    }
    branch.setName(LangUtil.getI18nFieldValue("Name"));
    trans.add(branch, 1);

    if (trans.commit()) {
      CacheManager.set("Platform", "Branch", branch.getBranchInnerCode(), branch);
      ExtendManager.invoke("com.zving.Platform.AfterBranchAdd", new Object[] { branch });
      success(Lang.get("Common.AddSuccess"));
    } else {
      fail(Lang.get("Common.AddFailed"));
    }
  }

  @Priv("Platform.Branch.Edit")
  public void save() {
    String branchInnerCode = $V("BranchInnerCode");
    Transaction trans = new Transaction();
    if (StringUtil.isEmpty(branchInnerCode)) {
      fail("BranchInnerCode is empty!");
      return;
    }
    if (branchInnerCode.length() == 4) {
      this.Request.put("ParentInnerCode", branchInnerCode);
    }
    if (isNameOrBranchCodeExists($V("Name"), $V("BranchCode"), branchInnerCode, $V("ParentInnerCode"))) {
      fail(Lang.get("Platform.DuplicateNameOrBranchCode"));
      return;
    }
    ZDBranch branch = new ZDBranch();
    branch.setBranchInnerCode(branchInnerCode);
    if (!branch.fill()) {
      fail(branchInnerCode + " is not found!");
      return;
    }

    branch.setValue(this.Request);
    branch.setModifyUser(User.getUserName());
    branch.setModifyTime(new Date());
    branch.setName(LangUtil.getI18nFieldValue("Name"));

    trans.add(branch, 2);

    if (trans.commit()) {
      CacheManager.set("Platform", "Branch", branch.getBranchInnerCode(), branch);
      ExtendManager.invoke("com.zving.Platform.AfterBranchModify", new Object[] { branch });
      success(Lang.get("Common.ExecuteSuccess"));
    } else {
      fail(Lang.get("Common.ExecuteFailed"));
    }
  }

  @Priv("Platform.Branch.Delete")
  public void del() {
    String IDs = $V("IDs");
    String[] ids = IDs.split(",");
    Transaction trans = new Transaction();
    ZDBranch branch = new ZDBranch();
    for (String innerCode : ids) {
      branch.setBranchInnerCode(innerCode);
      Q q1 = new Q("select count(1) from ZDUser where BranchInnerCode=?", new Object[] { innerCode });
      Q q2 = new Q("select count(1) from ZDRole where BranchInnerCode=?", new Object[] { innerCode });
      if ((q1.executeInt() > 0) || (q2.executeInt() > 0)) {
        fail(Lang.get("Platform.Branch.DeleteExistsUserOrRole"));
        return;
      }
      if (branch.fill()) {
        if ("0".equals(branch.getParentInnerCode())) {
          fail(Lang.get("Branch.DeleteRootMessage"));
          return;
        }
        Q qb = new Q("where BranchInnerCode like ?", new Object[] { branch.getBranchInnerCode() + "%" });
        trans.add(branch.query(qb), 5);
        trans.add(new Q("delete from ZDPrivilege where OwnerType='Branch' and Owner=?", new Object[] { innerCode }));
      }
    }

    if (trans.commit()) {
      ExtendManager.invoke("com.zving.Platform.AfterBranchDelete", new Object[] { ids });
      for (int i = 0; i < ids.length; i++) {
        CacheManager.remove("Platform", "Branch", ids[i]);
      }
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      fail(Lang.get("Common.DeleteFailed"));
    }
  }

  @Priv("Platform.Branch.Edit")
  public void sortBranch() {
    String orderBranch = $V("OrderBranch");
    String nextBranch = $V("NextBranch");
    String ordertype = $V("OrderType");
    if ((StringUtil.isEmpty(orderBranch)) || (StringUtil.isEmpty(nextBranch)) || (StringUtil.isEmpty(ordertype))) {
      return;
    }

    Transaction trans = new Transaction();
    DataTable DT = new Q("select * from zdbranch order by orderflag", new Object[0]).fetch();

    List branchList = new ArrayList();

    DataTable orderDT = new Q("select * from zdbranch where branchinnercode like '" + orderBranch + "%' order by orderflag", new Object[0])
      .fetch();

    DataTable nextDT = new Q("select * from zdbranch where branchinnercode like '" + nextBranch + "%' order by orderflag", new Object[0])
      .fetch();

    if ("before".equalsIgnoreCase(ordertype)) {
      for (int i = 0; i < DT.getRowCount(); i++) {
        if (DT.getString(i, "BranchInnerCode").equals(nextBranch)) {
          int m = 0;
          do { branchList.add(orderDT.getDataRow(m));

            m++; if (orderDT == null) break;  } while (m < orderDT.getRowCount());
        }
        else if (DT.getString(i, "BranchInnerCode").equals(orderBranch))
        {
          i = i - 1 + orderDT.getRowCount();
          continue;
        }
        branchList.add(DT.getDataRow(i));
      }

    }
    else if ("after".equalsIgnoreCase(ordertype)) {
      for (int i = 0; (DT != null) && (i < DT.getRowCount()); i++) {
        if (DT.getString(i, "BranchInnerCode").equals(orderBranch))
        {
          i = i - 1 + orderDT.getRowCount();
        }
        else if (DT.getString(i, "BranchInnerCode").equals(nextBranch))
        {
          for (int m = 0; (nextDT != null) && (m < nextDT.getRowCount()); m++) {
            branchList.add(nextDT.getDataRow(m));
          }

          for (int j = 0; (orderDT != null) && (j < orderDT.getRowCount()); j++) {
            branchList.add(orderDT.getDataRow(j));
          }

          i = i - 1 + nextDT.getRowCount();
        } else {
          branchList.add(DT.getDataRow(i));
        }
      }
    }

    for (int i = 0; (branchList != null) && (i < branchList.size()); i++) {
      DataRow dr = (DataRow)branchList.get(i);
      trans.add(new Q("update zdbranch set orderflag = ? where BranchInnerCode = ?", new Object[] { Integer.valueOf(i), dr.getString("BranchInnerCode") }));
    }
    if (trans.commit())
      success(Lang.get("Common.ExecuteSuccess"));
    else
      fail(Lang.get("Common.ExecuteFailed"));
  }

  private boolean isNameOrBranchCodeExists(String name, String branchCode, String innerCode, String pInnerCode)
  {
    if (StringUtil.isEmpty(pInnerCode)) {
      pInnerCode = "0";
    }
    Mapx mapx = CacheManager.getMapx("Platform", "Branch");
    for (Iterator localIterator = mapx.valueArray().iterator(); localIterator.hasNext(); ) { Object obj = localIterator.next();
      ZDBranch branch = (ZDBranch)obj;
      if (!branch.getBranchInnerCode().equals(innerCode))
      {
        if ((ObjectUtil.notEmpty(branch.getBranchCode())) && (branch.getBranchCode().equals(branchCode))) {
          return true;
        }

        if ((name.equals(LangUtil.get(branch.getName()))) && (branch.getParentInnerCode().equals(pInnerCode)))
          return true;
      }
    }
    return false;
  }
}