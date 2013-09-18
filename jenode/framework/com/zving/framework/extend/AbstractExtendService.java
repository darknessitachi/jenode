package com.zving.framework.extend;

import com.zving.framework.collection.Mapx;
import java.util.ArrayList;
import java.util.List;

public class AbstractExtendService<T extends IExtendItem>
  implements IExtendService<T>
{
  protected Mapx<String, T> itemMap = new Mapx();
  protected List<T> itemList = new ArrayList();

  protected static <S extends IExtendService<?>> S findInstance(Class<S> clazz)
  {
    IExtendService service = ExtendManager.getInstance().findExtendServiceByClass(clazz.getName()).getInstance();
    return service;
  }

  public void register(IExtendItem item)
  {
    this.itemMap.put(item.getID(), item);
    prepareItemList();
  }

  public T get(String id) {
    return (IExtendItem)this.itemMap.get(id);
  }

  public T remove(String id) {
    IExtendItem ret = (IExtendItem)this.itemMap.remove(id);
    prepareItemList();
    return ret;
  }

  protected void prepareItemList() {
    this.itemList = this.itemMap.valueArray();
  }

  public List<T> getAll()
  {
    return this.itemList;
  }

  public int size() {
    return this.itemList.size();
  }

  public void destory() {
    this.itemMap.clear();
    this.itemList.clear();
    this.itemMap = null;
    this.itemList = null;
  }
}