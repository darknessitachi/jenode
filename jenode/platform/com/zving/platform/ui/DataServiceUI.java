package com.zving.platform.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.core.handler.ZAction;
import com.zving.platform.IDataProvider;
import com.zving.platform.config.DataServicePassword;
import com.zving.platform.service.DataProviderService;

public class DataServiceUI extends UIFacade
{
  @Priv(login=false)
  @Alias("api/dataservice/getdata")
  public void getData(ZAction za)
  {
    String password = $V("password");
    if (!DataServicePassword.getValue().equals(password)) {
      za.writeHTML("error=Password is wrong!");
      return;
    }
    String dataTypeID = $V("datatype");
    IDataProvider dataType = (IDataProvider)DataProviderService.getInstance().get(dataTypeID.toLowerCase());
    if (dataType == null) {
      za.writeHTML("error=DataType is not found!");
      return;
    }
    za.writeHTML(dataType.getData(this.Request));
  }
}