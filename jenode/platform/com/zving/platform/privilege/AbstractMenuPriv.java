package com.zving.platform.privilege;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.IExtendItem;
import com.zving.framework.extend.menu.Menu;
import com.zving.framework.extend.menu.MenuManager;

public class AbstractMenuPriv
  implements IExtendItem
{
  private String MenuID;
  private Mapx<String, String> PrivItems = new Mapx();
  private String Memo;

  public AbstractMenuPriv(String MenuID, String Memo)
  {
    this.Memo = Memo;
    this.MenuID = MenuID;
  }

  public String getID() {
    return this.MenuID;
  }

  public void addItem(String itemID, String name) {
    this.PrivItems.put(itemID, name);
  }

  public Mapx<String, String> getPrivItems() {
    return this.PrivItems;
  }

  public String getName() {
    return MenuManager.getMenu(this.MenuID).getName();
  }

  public String getMemo() {
    return this.Memo;
  }
}