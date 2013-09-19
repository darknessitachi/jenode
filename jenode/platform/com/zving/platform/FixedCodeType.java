package com.zving.platform;

import com.zving.framework.extend.IExtendItem;
import java.util.ArrayList;
import java.util.List;

public class FixedCodeType
  implements IExtendItem
{
  boolean allowAddItem = true;
  boolean multiLevel = false;
  String codeType;
  String codeName;
  List<FixedCodeItem> fixedItems = new ArrayList();

  public FixedCodeType(String codeType, String codeName, boolean allowAddItem, boolean multiLevel) {
    this.codeType = codeType;
    this.codeName = codeName;
    this.allowAddItem = allowAddItem;
    this.multiLevel = multiLevel;
  }

  public boolean contains(String codeValue) {
    for (FixedCodeItem item : this.fixedItems) {
      if (item.getValue().equals(codeValue)) {
        return true;
      }
    }
    return false;
  }

  public String getID() {
    return getCodeType();
  }

  public String getCodeType() {
    return this.codeType;
  }

  public String getName() {
    return getCodeName();
  }

  public List<FixedCodeItem> getFixedItems() {
    return this.fixedItems;
  }

  public void addFixedItem(String itemValue, String itemName, String icon) {
    this.fixedItems.add(new FixedCodeItem(itemValue, itemName, icon));
  }

  public boolean allowAddItem() {
    return this.allowAddItem;
  }

  public boolean isMultiLevel() {
    return this.multiLevel;
  }

  public void setCodeName(String codeName)
  {
    this.codeName = codeName;
  }

  public String getCodeName() {
    return this.codeName;
  }
  public static class FixedCodeItem {
    private String value;
    private String name;
    private String icon;

    public FixedCodeItem(String value, String name, String icon) { this.value = value;
      this.name = name;
      this.icon = icon; }

    public String getIcon()
    {
      return this.icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return this.value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}