package com.zving.platform.pub;

import com.zving.framework.collection.Mapx;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.schedule.AbstractTaskManager;
import com.zving.schema.ZDSchedule;

public abstract class ConfigEanbleTaskManager extends AbstractTaskManager
{
  protected static DAOSet<ZDSchedule> set;
  private static Object mutex = new Object();
  protected static long LastTime;

  public Mapx<String, String> getUsableTasks()
  {
    synchronized (mutex) {
      if ((set == null) || (System.currentTimeMillis() - LastTime > 60000L)) {
        set = new ZDSchedule().query();
        LastTime = System.currentTimeMillis();
      }
    }
    Mapx map = new Mapx();
    for (int i = 0; i < set.size(); i++) {
      ZDSchedule s = (ZDSchedule)set.get(i);
      if ((s.getTypeCode().equals(getID())) && ("Y".equals(s.getIsUsing()))) {
        map.put(s.getSourceID(), "");
      }
    }
    return map;
  }

  public String getTaskCronExpression(String id) {
    synchronized (mutex) {
      if ((set == null) || (System.currentTimeMillis() - LastTime > 60000L)) {
        set = new ZDSchedule().query();
        LastTime = System.currentTimeMillis();
      }
    }
    for (int i = 0; i < set.size(); i++) {
      ZDSchedule s = (ZDSchedule)set.get(i);
      if ((s.getTypeCode().equals(getID())) && (id.equals(s.getSourceID()))) {
        return s.getCronExpression();
      }
    }
    return null;
  }
}