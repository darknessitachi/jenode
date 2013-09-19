package com.zving.platform.meta;

import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMetaColumn;

public class MetadataDateTimeColumn
  implements IMetadataColumnControlType
{
  public static final String ID = "DateTime";

  public String getID()
  {
    return "DateTime";
  }

  public String getName() {
    return "@{Platform.DateTimeSelection}";
  }

  public String getHtml(ZDMetaColumn mc, String value) {
    String code = "MetaValue_" + mc.getCode();
    if (StringUtil.isEmpty(value)) {
      value = MetaUtil.getValue(mc);
    }
    String styleText = MetaUtil.getClass(mc);
    String verify = MetaUtil.getVerifys(mc);
    return "<input type=\"text\" ztype=\"DateTime\" id=\"" + code + "\" value=\"" + value + "\" name=\"" + code + "\" " + 
      styleText + verify + " />\n";
  }
}