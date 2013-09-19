package com.zving.platform.meta;

import java.util.ArrayList;
import java.util.List;

public class SystemModelType
  implements IMetaModelType
{
  public static final String ID = "System";

  public String getID()
  {
    return "System";
  }

  public String getName() {
    return "@{Platform.SystemMetadata}";
  }

  public boolean isSystemModel() {
    return true;
  }

  public List<IMetaModelTemplateType> getTemplateTypes() {
    ArrayList list = new ArrayList();
    list.add(new SystemModelTemplateType());
    return list;
  }

  public String getDefautlTemplateHtml() {
    return "<model:fieldgroup><fieldset class=\"extend\"><legend ><b>@{FieldGroup.Name}</b></legend><table id=\"table@{FieldGroup.Code}\" width=\"500\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" bordercolor=\"#eeeeee\"><model:field><tr><td width=\"160\">@{Field.Name}：</td><td>@{Field.ControlHtml}</td></tr></model:field></table></fieldset></model:fieldgroup>";
  }
}