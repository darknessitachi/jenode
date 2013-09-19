package com.zving.framework.extend.plugin;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.ExtendActionConfig;
import com.zving.framework.extend.ExtendItemConfig;
import com.zving.framework.extend.ExtendPointConfig;
import com.zving.framework.extend.ExtendServiceConfig;
import com.zving.framework.extend.OptionalItemConfig;
import com.zving.framework.extend.ReplaceableItemConfig;
import com.zving.framework.extend.menu.Menu;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLDocument;
import com.zving.framework.xml.XMLElement;
import com.zving.framework.xml.XMLParser;
import java.util.ArrayList;
import java.util.List;

public class PluginConfig {
	private Mapx<String, ExtendPointConfig> extendPoints = new Mapx();
	private Mapx<String, ExtendServiceConfig> extendServices = new Mapx();
	private Mapx<String, String> requiredExtendPoints = new Mapx();
	private Mapx<String, String> requiredExtendServices = new Mapx();
	private Mapx<String, String> requiredPlugins = new Mapx();
	private Mapx<String, ExtendActionConfig> extendActions = new Mapx();
	private Mapx<String, ExtendItemConfig> extendItems = new Mapx();
	private Mapx<String, ReplaceableItemConfig> replaceableItems = new Mapx();
	private Mapx<String, OptionalItemConfig> optionalItems = new Mapx();
	private Mapx<String, Menu> menus = new Mapx();
	private String ID;
	private String Name;
	private String ClassName;
	private String author;
	private String provider;
	private String version;
	private String description;
	private String updateSite;
	private boolean UIAsJar;
	private boolean resourceAsJar;
	private ArrayList<String> pluginFiles = new ArrayList();
	private boolean enabling;
	private boolean running;

	public boolean isResourceAsJar() {
		return this.resourceAsJar;
	}

	public void setResourceAsJar(boolean resourceAsJar) {
		this.resourceAsJar = resourceAsJar;
	}

	public Mapx<String, ExtendPointConfig> getExtendPoints() {
		return this.extendPoints;
	}

	public Mapx<String, ExtendServiceConfig> getExtendServices() {
		return this.extendServices;
	}

	public Mapx<String, String> getRequiredExtendPoints() {
		return this.requiredExtendPoints;
	}

	public Mapx<String, String> getRequiredPlugins() {
		return this.requiredPlugins;
	}

	public Mapx<String, ExtendActionConfig> getExtendActions() {
		return this.extendActions;
	}

	public Mapx<String, ExtendItemConfig> getExtendItems() {
		return this.extendItems;
	}

	public Mapx<String, OptionalItemConfig> getOptionalItems() {
		return this.optionalItems;
	}

	public Mapx<String, ReplaceableItemConfig> getReplaceableItems() {
		return this.replaceableItems;
	}

	public Mapx<String, Menu> getMenus() {
		return this.menus;
	}

	public String getID() {
		return this.ID;
	}

	public String getClassName() {
		return this.ClassName;
	}

	public String getAuthor() {
		return this.author;
	}

	public String getVersion() {
		return this.version;
	}

	public String getDescription() {
		return this.description;
	}

	public String getDescription(String language) {
		if (this.description == null) {
			return null;
		}
		return LangUtil.get(this.description, language);
	}

	public String getName() {
		return this.Name;
	}

	public String getName(String language) {
		if (this.Name == null) {
			return null;
		}
		return LangUtil.get(this.Name, language);
	}

	public ArrayList<String> getPluginFiles() {
		return this.pluginFiles;
	}

	public boolean isEnabling() {
		return this.enabling;
	}

	public void setEnabling(boolean enabling) {
		this.enabling = enabling;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getProvider() {
		return this.provider;
	}

	public void setID(String iD) {
		this.ID = iD;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public void setClassName(String className) {
		this.ClassName = className;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void parse(String xml) throws PluginException {
		XMLParser parser = new XMLParser(xml);
		parser.parse();

		this.enabling = true;

		XMLElement root = parser.getDocument().getRoot();
		this.ID = root.elementText("id");
		this.Name = root.elementText("name");
		this.ClassName = root.elementText("class");
		this.description = root.elementText("description");
		this.version = root.elementText("version");
		this.author = root.elementText("author");
		this.provider = root.elementText("provider");
		this.updateSite = root.elementText("updateSite");
		this.UIAsJar = Boolean.parseBoolean(root.elementText("UIAsJar"));
		this.resourceAsJar = Boolean.parseBoolean(root
				.elementText("resourceAsJar"));

		if (ObjectUtil.empty(this.updateSite)) {
			this.updateSite = "http://www.zving.com/update/";
		}

		if (ObjectUtil.empty(this.ID)) {
			throw new PluginException("id is empty!");
		}
		if (ObjectUtil.empty(this.Name)) {
			throw new PluginException("name is empty!");
		}
		if (ObjectUtil.empty(this.version)) {
			throw new PluginException("version is empty!");
		}

		List<XMLElement> nds = root.elements("files.*");
		for (XMLElement nd : nds) {
			if (nd.getQName().equalsIgnoreCase("directory")) {
				this.pluginFiles.add("[D]" + nd.getText());
			}
			if (nd.getQName().equalsIgnoreCase("file")) {
				this.pluginFiles.add(nd.getText());
			}

		}

		nds = root.elements("required.plugin");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				this.requiredPlugins.put(nd.getText(), (String) nd
						.getAttributes().get("version"));
			}

		}

		nds = root.elements("extendPoint");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendPointConfig ep = new ExtendPointConfig();
				ep.init(this, nd);
				this.extendPoints.put(ep.getID(), ep);
			}

		}

		nds = root.elements("extendService");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendServiceConfig ep = new ExtendServiceConfig();
				ep.init(this, nd);
				this.extendServices.put(ep.getID(), ep);
			}

		}

		nds = root.elements("extendItem");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendItemConfig ei = new ExtendItemConfig();
				ei.init(this, nd);
				this.extendItems.put(ei.getID(), ei);
				this.requiredExtendServices.put(ei.getExtendServiceID(), "Y");
			}

		}

		nds = root.elements("replaceableItem");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ReplaceableItemConfig ei = new ReplaceableItemConfig();
				ei.init(this, nd);
				this.replaceableItems.put(ei.getID(), ei);
			}

		}

		nds = root.elements("optionalItem");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				OptionalItemConfig ei = new OptionalItemConfig();
				ei.init(this, nd);
				this.optionalItems.put(ei.getID(), ei);
			}

		}

		nds = root.elements("menu");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				Menu menu = new Menu();
				menu.parse(this, nd);
				this.menus.put(menu.getID(), menu);
			}

		}

		nds = root.elements("extendAction");
		if (ObjectUtil.notEmpty(nds))
			for (XMLElement nd : nds) {
				ExtendActionConfig eac = new ExtendActionConfig();
				eac.init(this, nd);
				this.extendActions.put(eac.getID(), eac);
				this.requiredExtendPoints.put(eac.getExtendPointID(), "Y");
			}
	}

	public String getUpdateSite() {
		return this.updateSite;
	}

	public void setUpdateSite(String updateSite) {
		this.updateSite = updateSite;
	}

	public boolean isUIAsJar() {
		return this.UIAsJar;
	}

	public void setUIAsJar(boolean UIAsJar) {
		this.UIAsJar = UIAsJar;
	}
}