package com.zving.platform.update;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.PropertiesUtil;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.xml.XMLDocument;
import com.zving.framework.xml.XMLElement;
import com.zving.framework.xml.XMLParser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateServer
{
  private String url;
  private List<PluginUpdateRecord> pluginUpdateRecords = new ArrayList();
  private List<Product> products = new ArrayList();

  public UpdateServer(String url) {
    this.url = url;
  }

  public void loadContent() throws Exception {
    String xml = ServletUtil.getURLContent(this.url + "/update.xml", "UTF-8");
    XMLParser loader = new XMLParser(xml);
    List list = loader.getDocument().elements("update.plugin");

    File statusFile = new File(Config.getPluginPath() + "update/update.status");
    Mapx map = statusFile.exists() ? PropertiesUtil.read(statusFile) : new Mapx();
    String desc;
    for (XMLElement nd : list) {
      String id = nd.elementText("id").trim();
      String time = nd.elementText("time").trim();
      String size = nd.elementText("size").trim();
      desc = nd.elementText("desc") == null ? null : nd.elementText("desc").trim();
      PluginUpdateRecord pur = new PluginUpdateRecord();
      pur.ID = id;
      pur.LastUpdateTime = Long.parseLong(time);
      pur.FileSize = Long.parseLong(size);
      pur.UpdateDescription = desc;
      if (map.getLong(id) < pur.LastUpdateTime) {
        pur.NeedUpdate = true;
      }
      this.pluginUpdateRecords.add(pur);
    }
    list = loader.getDocument().elements("update.product");
    for (XMLElement nd : list) {
      String id = ((String)nd.getAttributes().get("id")).trim();
      Product p = new Product();
      p.ID = id;
      for (XMLElement child : nd.elements("plugin")) {
        p.PluginList.add(child.getText().trim());
      }
      this.products.add(p);
    }
  }

  public String getUrl()
  {
    return this.url;
  }

  public List<PluginUpdateRecord> getPluginUpdateRecords() {
    return this.pluginUpdateRecords;
  }

  public List<Product> getProducts() {
    return this.products;
  }

  public static class PluginUpdateRecord
  {
    public String ID;
    public long LastUpdateTime;
    public long FileSize;
    public String UpdateDescription;
    public boolean NeedUpdate;
  }

  public static class Product
  {
    public String ID;
    public List<String> PluginList = new ArrayList();
  }
}