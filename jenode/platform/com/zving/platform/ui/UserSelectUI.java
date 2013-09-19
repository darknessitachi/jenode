package com.zving.platform.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.i18n.Lang;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.TreeAction;
import com.zving.framework.ui.control.TreeItem;
import com.zving.framework.utility.StringUtil;
import java.util.List;

@Alias("UserSelect")
public class UserSelectUI extends UIFacade
{
  @Priv
  public void init()
  {
  }

  @Priv
  public void selectedUserDataBind(DataGridAction dga)
  {
    String selectedUser = dga.getParam("SelectedUsers");
    if (StringUtil.isNotEmpty(selectedUser)) {
      selectedUser = StringUtil.replaceEx(selectedUser, ",", "','");
      DataTable dt = new Q(
        "select ZDUser.*,(select Name from ZDBranch where BranchInnerCode=ZDUser.BranchInnerCode) as BranchName from ZDUser where UserName in ('" + 
        selectedUser + "')", new Object[0])
        .fetch();
      dga.setTotal(dt.getRowCount());
      dga.bindData(dt);
    } else {
      dga.bindData(new DataTable());
      return;
    }
  }

  @Priv
  public void allUserDataBind(DataGridAction dga) {
    Q qb = new Q(
      "select ZDUser.*,(select Name from ZDBranch where BranchInnerCode=ZDUser.BranchInnerCode) as BranchName from ZDUser where 1=1", new Object[0]);

    String selectedUser = $V("SelectedUsers");
    String searchContent = $V("SearchContent");
    String branchInnerCode = User.getBranchInnerCode();
    if (StringUtil.isNotEmpty(selectedUser)) {
      selectedUser = StringUtil.replaceEx(selectedUser, ",", "','");
      qb.append(" and UserName not in ('" + selectedUser + "')", new Object[0]);
    }
    if ((StringUtil.isNotEmpty($V("BranchInnerCode"))) && ($V("BranchInnerCode").startsWith(branchInnerCode))) {
      qb.append(" and BranchInnerCode=?", new Object[] { $V("BranchInnerCode") });
    }
    if (StringUtil.isNotEmpty(searchContent)) {
      qb.append(" and (UserName like ? or RealName like ?) ", new Object[] { "%" + searchContent + "%", "%" + searchContent + "%" });
    }
    dga.bindData(qb);
  }

  @Priv("Platform.User")
  public void bindBranchTree(TreeAction ta) {
    DataTable dt = null;
    String[] branches = new String[0];
    if (StringUtil.isNotEmpty(ta.getParam("Branches"))) {
      branches = getSelectedBranchList(ta.getParam("Branches"), 2);
    }
    String parentTreeLevel = ta.getParams().getString("ParentLevel");
    String parentInnerCode = ta.getParams().getString("ParentInnerCode");
    if ((StringUtil.isEmpty(parentInnerCode)) && (!User.isBranchAdministrator())) {
      parentInnerCode = User.getBranchInnerCode();
    }
    Q qb = null;
    if (ta.isLazyLoad()) {
      qb = new Q("select * from ZDBranch where TreeLevel>? and BranchinnerCode like ? order by OrderFlag,BranchInnerCode", new Object[0]);
      qb.add(new Object[] { Long.valueOf(Long.parseLong(parentTreeLevel)) });
      qb.add(new Object[] { parentInnerCode + "%" });
      dt = qb.fetch();
    } else {
      qb = new Q("select * from ZDBranch where TreeLevel-1 <=? order by OrderFlag,BranchInnerCode", new Object[0]);
      qb.add(new Object[] { Integer.valueOf(ta.getLevel()) });
      dt = qb.fetch();
      prepareSelectedBranchData(dt, branches, ta.getLevel());
    }
    ta.setIdentifierColumnName("BranchInnerCode");
    ta.setParentIdentifierColumnName("ParentInnerCode");
    ta.setRootText(Lang.get("Platform.BranchList"));
    ta.bindData(dt);
    addSelectedBranches(ta, branches);
  }

  public static String[] getSelectedBranchList(String Branches, int level) {
    Mapx<String, String> map = new Mapx();
    if (StringUtil.isNotEmpty(Branches)) {
      String[] arr = Branches.split("\\,");
      for (int i = 0; i < arr.length; i++) {
        if (StringUtil.isNotEmpty(arr[i])) {
          String innerCode = arr[i];
          if ((StringUtil.isNotEmpty(innerCode)) && 
            (innerCode.length() > level * 4)) {
            innerCode = innerCode.substring(0, level * 4);
            map.put(innerCode, "1");
          }
        }
      }
    }

    String[] codes = new String[map.size()];
    int i = 0;
    for (String k : map.keySet()) {
      codes[(i++)] = k;
    }
    return codes;
  }

  public static void prepareSelectedBranchData(DataTable dt, String[] codes, int level) {
    for (int i = 0; i < codes.length; i++) {
      String innerCode = codes[i];
      Q qb = new Q(
        "select * from ZDBranch where TreeLevel>? and BranchinnerCode like ? order by OrderFlag,BranchInnerCode", new Object[0]);

      qb.add(new Object[] { Integer.valueOf(level + 1) });
      qb.add(new Object[] { innerCode + "%" });
      DataTable dt2 = qb.fetch();
      dt.union(dt2);
    }
  }

  public static void addSelectedBranches(TreeAction ta, String[] codes) {
    List list = ta.getItemList();
    for (int i = 0; i < list.size(); i++) {
      TreeItem item = (TreeItem)list.get(i);
      if (!item.isRoot())
      {
        for (int j = 0; j < codes.length; j++)
          if (item.getData().getString("BranchInnerCode").equals(codes[j].toString())) {
            item.setLazy(false);
            item.setExpanded(true);
            try {
              int level = ta.getLevel();
              ta.setLevel(1000);
              ta.addChild(item);
              ta.setLevel(level);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
      }
    }
  }
}