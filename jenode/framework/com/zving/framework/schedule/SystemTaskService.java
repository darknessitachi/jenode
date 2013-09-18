package com.zving.framework.schedule;

import com.zving.framework.ConfigLoader;
import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.extend.IExtendItem;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;
import java.util.List;

public class SystemTaskService extends AbstractExtendService<SystemTask>
{
  public static final String ID = "com.zving.framework.schedule.SystemTaskService";

  public static SystemTaskService getInstance()
  {
    return (SystemTaskService)findInstance(SystemTaskService.class);
  }

  public void register(IExtendItem item) {
    super.register(item);
    loadCronConfig((SystemTask)item);
  }

  public static void loadCronConfig(SystemTask task) {
    List<XMLElement> datas = ConfigLoader.getElements("*.cron.task");
    for (XMLElement data : datas) {
      String id = (String)data.getAttributes().get("id");
      String time = (String)data.getAttributes().get("time");
      String disabled = (String)data.getAttributes().get("disabled");
      if (!ObjectUtil.empty(id))
      {
        if (task.getID().equals(id)) {
          if (!ObjectUtil.empty(time)) {
            task.setCronExpression(time);
          }
          task.setDisabled("true".equals(disabled));
          break;
        }
      }
    }
  }
}