package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.RadioTag;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.FixedConfigItem;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.pub.PlatformUtil;
import com.zving.platform.service.ConfigService;
import com.zving.schema.ZDConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Alias("Config")
public class ConfigUI extends UIFacade
{
  @Priv("Platform.Config")
  public void init()
  {
     List<FixedConfigItem> configs = ConfigService.getInstance().getAll();
     ArrayList<PluginConfig> pcs = PluginManager.getInstance().getAllPluginConfig();

    Mapx pnm = new Mapx();
    for (PluginConfig pc : pcs) {
      pnm.put(pc.getID(), StringUtil.isEmpty(LangMapping.get(pc.getName())) ? pc.getName() : LangMapping.get(pc.getName()));
    }

    DataTable dt = new DataTable();
    dt.insertColumns(new String[] { "code", "name", "pluginID", "value", "pluginName", "controlType", "id" });
    for (FixedConfigItem fc : configs) {
      dt.insertRow(new Object[] { fc.getCode(), 
        StringUtil.isEmpty(LangMapping.get(fc.getName())) ? fc.getName() : LangMapping.get(fc.getName()), 
        fc.getPluginID(), 
        Config.getValue(fc.getID()), 
        pnm.getString(fc.getPluginID()), 
        fc.getControlType(), 
        fc.getID() });
    }

    dt.sort("pluginID", "asc");

    StringBuilder sb = new StringBuilder();
    String lastplugin = "";
    for (DataRow dr : dt) {
      if (!dr.getString("pluginID").equals(lastplugin)) {
        lastplugin = dr.getString("pluginID");
        sb.append("</table>");
        sb.append("\n");
        sb.append("<div class=\"z-legend\"><b>" + dr.getString("pluginName") + "</b></div>");
        sb.append("\n");
        sb.append("<table width=\"600\" border=\"1\" cellpadding=\"4\" cellspacing=\"0\" bordercolor=\"#eeeeee\" class=\"formTable\">");
        sb.append("\n");
      }
      sb.append("<tr>\n<td width=\"300\">" + dr.getString("name") + "：</td><td>");

      String ctr = dr.getString("controlType");
      if ("Text".equals(ctr)) {
        sb.append("<input name=\"" + dr.getString("code") + "\" type=\"text\" style=\"width:280px\" value=\"" + dr.getString("value") + "\"  ></td>\n");
      } else if ("Radio".equals(ctr)) {
        RadioTag radioTag = new RadioTag();
        FixedConfigItem fci = (FixedConfigItem)ConfigService.getInstance().get(dr.getString("id"));
        String option = "";
         Mapx<String, String> opm = fci.getOptions();
        for (String key : opm.keyArray()) {
          option = option + "," + opm.getString(key) + ":" + key;
        }
        if (option.length() > 0) {
          option = option.substring(1);
        }
        radioTag.setOptions(option);
        radioTag.setType("Radio");
        radioTag.setCode("");
        radioTag.setName(dr.getString("code"));
        radioTag.setValue(dr.getString("value"));
        sb.append(radioTag.getHtml() + "\n");
      } else if ("TextArea".equals(ctr)) {
        sb.append("<textarea name=\"" + dr.getString("code") + "\" type=\"text\" style=\"width:280px\"   >" + dr.getString("value") + "</textarea>\n");
      } else {
        sb.append("<input name=\"" + dr.getString("code") + "\" type=\"text\" style=\"width:280px\" value=\"" + dr.getString("value") + "\"  ></td>\n");
      }

      sb.append("</tr>\n");
    }
    String body = sb.toString();
    if (body.length() > 0) {
      body = body.substring(9) + body.substring(0, 9);
    }
    $S("body", body);
  }

  @Priv("Platform.Config")
  public void saveAll()
  {
    Transaction tran = Current.getTransaction();
     List<FixedConfigItem> configs = ConfigService.getInstance().getAll();

    for (FixedConfigItem fci : configs) {
      String value = this.Request.getString(fci.getID());
      if (value != null) {
        ZDConfig dao = new ZDConfig();
        dao.setCode(fci.getID());
        if (dao.fill()) {
          if (ObjectUtil.empty(value)) {
            tran.delete(dao);
          }
          else {
            dao.setValue(value);
            dao.setName(fci.getName());
            dao.setModifyTime(new Date());
            dao.setModifyUser(User.getUserName());
            tran.update(dao);
          }
        } else if (!ObjectUtil.empty(value))
        {
          dao.setValue(value);
          dao.setAddTime(new Date());
          dao.setAddUser(User.getUserName());
          dao.setName(fci.getName());
          tran.insert(dao);
        }
      }
    }
    if (tran.commit()) {
      for (FixedConfigItem fci : configs) {
        Config.getMapx().remove(fci.getID());
      }
      PlatformUtil.refresh();
      success(Lang.get("Common.ExecuteSuccess"));
    } else {
      fail(Lang.get("Common.ExecuteFailed"));
    }
  }

  @Priv("Platform.Config")
  public void bindGrid(DataGridAction dga) {
    String SearchType = (String)dga.getParams().get("SearchType");
    Q qb = new Q(
      "select code,name,value from zdconfig where code not like ? ", new Object[0]);

    qb.add(new Object[] { "System.%" });
    if (StringUtil.isNotEmpty(SearchType)) {
      qb.append(" and (code like ? or name like ?)", new Object[0]);
      qb.add(new Object[] { "%" + SearchType + "%" });
      qb.add(new Object[] { "%" + SearchType + "%" });
    }
    qb.append("order by code", new Object[0]);
    dga.setTotal(qb);
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    dt.insertColumn("Fixed");
    for (DataRow dr : dt) {
      FixedConfigItem fct = (FixedConfigItem)ConfigService.getInstance().get(
        dr.getString("Code"));
      if (fct != null)
        dr.set("Fixed", "Y");
      else {
        dr.set("Fixed", "N");
      }
    }
    YesOrNo.decodeYesOrNoIcon(dt, "Fixed");
    dga.bindData(dt);
  }

  @Priv("Platform.Config.Add||Platform.Config.Edit")
  public void add() {
    String OldCode = $V("OldCode");
    ZDConfig config = new ZDConfig();
    if ($V("Code").startsWith("System.")) {
      fail(Lang.get("Config.DenySystemPrefix"));
      return;
    }
    Transaction tran = new Transaction();
    FixedConfigItem fci = (FixedConfigItem)ConfigService.getInstance().get($V("Code"));

    if (ObjectUtil.empty(OldCode)) {
      config.setValue(this.Request);
      config.setName(fci.getMemo());
      config.setAddTime(new Date());
      config.setAddUser(User.getUserName());
      tran.insert(config);
    } else {
      config.setCode(OldCode);
      config.fill();
      config.setValue(this.Request);
      config.setName(fci.getMemo());
      tran.update(config);
    }
    if (tran.commit()) {
      PlatformUtil.refresh();
      success(Lang.get("Common.ExecuteSuccess"));
    } else {
      fail(Lang.get("Common.ExecuteFailed"));
    }
  }

  @Priv("Platform.Config.Delete")
  public void del() {
    String ids = $V("IDs");
    ids = ids.replaceAll(",", "','");
    Transaction trans = new Transaction();
    ZDConfig zdconfig = new ZDConfig();
    DAOSet set = zdconfig.query(new Q("where code in ('" + ids + 
      "')", new Object[0]));
    trans.deleteAndBackup(set);
    if (trans.commit()) {
      PlatformUtil.refresh();
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      fail(Lang.get("Common.DeleteFailed") + ":" + 
        trans.getExceptionMessage());
    }
  }

  @Priv("Platform.Config")
  public void initDialog() {
    String code = $V("Code");
    if (ObjectUtil.notEmpty(code)) {
      ZDConfig schema = new ZDConfig();
      schema.setCode(code);
      schema.fill();
      this.Response.putAll(schema.toMapx());
      if (ConfigService.getInstance().get(code) != null) {
        $S("ControlType", ((FixedConfigItem)ConfigService.getInstance().get(code))
          .getControlType());
        $S("Fixed", "true");
      }
    }
  }

  @Priv
  public DataTable getOptions() {
    String code = $V("Code");
    if ((ObjectUtil.notEmpty(code)) && 
      (ConfigService.getInstance().get(code) != null)) {
      return ((FixedConfigItem)ConfigService.getInstance().get(code)).getOptions()
        .toDataTable();
    }

    return new DataTable();
  }

  @Priv("Platform.Config")
  public DataTable getConfigs4Insert() {
    DataTable dt = new DataTable();
    dt.insertColumns(new String[] { "ID", "Name" });
    ZDConfig dao = new ZDConfig();
     DAOSet<ZDConfig> set = dao.query();
    Mapx map = new Mapx();
    for (ZDConfig zc : set) {
      map.put(zc.getCode(), zc);
    }

    for (FixedConfigItem fci : ConfigService.getInstance().getAll()) {
      if (!map.containsKey(fci.getID()))
        dt.insertRow(new Object[] { fci.getID(), fci.getMemo() });
    }
    return dt;
  }
}