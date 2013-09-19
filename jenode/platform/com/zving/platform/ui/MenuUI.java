package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Filter;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.extend.menu.Menu;
import com.zving.framework.extend.menu.MenuManager;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.TreeAction;
import com.zving.framework.ui.control.TreeItem;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringFormat;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.service.MenuPrivService;
import java.io.PrintStream;
import java.util.LinkedList;

@Alias("Menu")
public class MenuUI extends UIFacade {
	@Priv("Platform.Menu.Delete")
	public void del() {
		String ids = $V("IDs");
		String[] arr = ids.split(",");
		for (String id : arr) {
			Menu m = MenuManager.getMenu(id);
			PluginConfig pc = m.getPluginConfig();
			if (pc == null) {
				MenuManager.delMenu(m);
			} else {
				StringFormat sf = new StringFormat(
						Lang.get("Platform.Menu.MenudeleteFailed"),
						new Object[] { m.getID() });
				Errorx.addError(sf.toString());
			}
		}
		if (Errorx.hasError())
			success(Lang.get("Menu.Success") + ","
					+ Lang.get("Platform.Menu.partError")
					+ Errorx.getAllMessage());
		else
			success(Lang.get("Menu.Success"));
	}

	@Priv("Platform.Menu")
	public void bindSelectorTree(TreeAction ta) {
		ta.setRootText("Root");
		DataTable dt = MenuPrivService.getAllMenus();
		dt = dt.filter(new Filter<DataRow>() {
			public boolean filter(DataRow dr) {
				System.out.println();
				if (dr.getString("ParentID").equals("")) {
					return true;
				}
				return false;
			}
		});
		ta.bindData(dt);
		for (TreeItem ti : ta.getItemList())
			if (ti.getData() != null)
				ti.setIcon(ti.getData().getString("Icon"));
	}

	@Priv("Platform.Menu")
	public void dg1DataBind(DataGridAction dga) {
		final String PluginID = $V("PluginID");
		DataTable dt = MenuPrivService.getAllMenus();
		if (ObjectUtil.notEmpty(PluginID)) {
			dt = dt.filter(new Filter<DataRow>() {
				public boolean filter(DataRow dr) {
					if (dr.getString("PluginID").equals(PluginID)) {
						return true;
					}
					return false;
				}
			});
		}
		dt.insertColumn("Status");
		for (DataRow dr : dt) {
			dr.set("Status",
					ExtendManager.getInstance()
							.isMenuEnable(dr.getString("ID")) ? "Y" : "N");
		}
		YesOrNo.decodeYesOrNoIcon(dt, "Status", false);
		dga.bindData(dt);
	}

	@Priv
	public DataTable getFirstLevelMenu() {
		DataTable dt = MenuPrivService.getMainMenus();
		dt.deleteColumn("ParentID");
		dt.deleteColumn("Icon");
		dt.deleteColumn("URL");
		dt.deleteColumn("Order");
		dt.deleteColumn("Description");
		dt.deleteColumn("PluginID");
		dt.deleteColumn("PluginName");
		dt.deleteColumn("HasChild");
		return dt;
	}

	@Priv("Platform.Menu")
	public void init() {
		if (StringUtil.isEmpty($V("ID"))) {
			return;
		}
		Menu m = MenuManager.getMenu($V("ID"));
		$S("ID", m.getID());
		$S("Name", m.getName());
		$S("Description", m.getDescription(User.getLanguage()));
		$S("URL", m.getURL() == null ? "" : m.getURL());
		$S("Icon", m.getIcon());
		$S("ParentID", m.getParentID());

		Menu parent = MenuManager.getMenu(m.getParentID());
		if (parent != null) {
			$S("ParentName",
					MenuManager.getMenu(m.getParentID()).getName(
							User.getLanguage()));
		}
		if (ObjectUtil.notEmpty(m.getPluginConfig()))
			$S("PluginName", m.getPluginConfig().getName(User.getLanguage()));
	}

	@Priv("Platform.Menu.Add")
	public void add() {
		String ID = $V("ID");
		if (StringUtil.isEmpty(ID)) {
			fail("ID不能为空");
			return;
		}
		Menu m = new Menu();
		m.setID(ID);
		m.setIcon("icons/icon001a1.png");
		m.setType("Backend");
		m.setName(LangUtil.getI18nFieldValue("Name"));
		m.setParentID($V("ParentID"));
		m.setURL($V("URL"));
		m.setDescription($V("Description"));

		MenuManager.addMenu(m);

		if (Errorx.hasError())
			fail(Lang.get("Common.ExecuteFailed") + ":"
					+ Errorx.getAllMessage());
		else
			success(Lang.get("Common.ExecuteSuccess"));
	}

	@Priv("Platform.Menu.Edit")
	public void save() {
		if (StringUtil.isEmpty($V("ID"))) {
			return;
		}
		Menu m = MenuManager.getMenu($V("ID"));
		m.setName(LangUtil.getI18nFieldValue("Name"));
		m.setParentID($V("ParentID"));
		m.setURL($V("URL"));
		m.setDescription($V("Description"));
		MenuManager.editMenu(m);
		if (Errorx.hasError())
			fail(Lang.get("Common.ExecuteFailed") + ":"
					+ Errorx.getAllMessage());
		else
			success(Lang.get("Common.ExecuteSuccess"));
	}

	@Priv("Platform.Menu")
	public DataTable getPluginList() {
		DataTable dt = new DataTable();
		dt.insertColumn("ID");
		dt.insertColumn("Name");
		for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {
			dt.insertRow(new Object[] { pc.getID(), pc.getName() });
		}
		return dt;
	}

	@Priv("Platform.Menu.Start||Platform.Menu.Stop")
	public void setStatus() {
		String ids = $V("IDs");
		String status = $V("Status");

		String[] arr = ids.split(",");
		for (String id : arr) {
			if (!ObjectUtil.empty(id)) {
				Menu m = MenuManager.getMenu(id);
				String failed = Lang.get("Common.ExecuteFailed");

				if (m == null) {
					fail(failed + ":Menu not found");
					return;
				}
				if ("true".equals(status)) {
					if (!ExtendManager.getInstance().isMenuEnable(id)) {
						ExtendManager.getInstance().enableMenu(id);
					}
				} else if (ExtendManager.getInstance().isMenuEnable(id)) {
					ExtendManager.getInstance().disableMenu(id);
				}
			}
		}
		success(Lang.get("Common.ExecuteSuccess"));
	}

	@Priv("Platform.Menu.Sort")
	public void sortMenu() {
		int move = this.Request.getInt("Move");

		if (StringUtil.isEmpty($V("ID"))) {
			return;
		}
		Menu m = MenuManager.getMenu($V("ID"));
		final String parentID = m.getParentID() == null ? "" : m.getParentID();
		DataTable dt = MenuPrivService.getAllMenus();
		DataTable dt2 = dt.filter(new Filter<DataRow>() {
			public boolean filter(DataRow obj) {
				return ObjectUtil.equal(parentID, obj.getString("ParentID"));
			}
		});
		int index = -1;
		LinkedList<DataRow> ll = new LinkedList();
		DataRow temp = null;
		for (int i = 0; i < dt2.getRowCount(); i++)
			if (dt2.get(i, "ID").equals(m.getID())) {
				index = i;
				temp = dt2.get(i);
			} else {
				ll.add(dt2.get(i));
			}
		index -= move;
		if ((index > 0) && (index > ll.size())) {
			index = ll.size();
		}
		if (index < 0) {
			index = 0;
		}
		ll.add(index, temp);
		int i = 10;
		for (DataRow dr : ll) {
			Menu menu = MenuManager.getMenu(dr.getString("ID"));
			menu.setOrder(Integer.toString(i));
			MenuManager.editMenu(menu);
			i++;
		}

		if (Current.getTransaction().commit())
			success(Lang.get("Code.SortSuccess"));
		else
			fail(Lang.get("Code.SortFailed"));
	}

	@Priv("Platform.Menu.Reset")
	public void reset() {
		String menuCfg = Config.getContextRealPath()
				+ "WEB-INF/plugins/classes/plugins/menu.config";
		FileUtil.delete(menuCfg);
		success(Lang.get("Common.ExecuteSuccess"));
	}
}