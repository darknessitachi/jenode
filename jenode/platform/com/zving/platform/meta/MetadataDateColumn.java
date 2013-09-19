package com.zving.platform.meta;

import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMetaColumn;

public class MetadataDateColumn
  implements IMetadataColumnControlType
{
  public static final String ID = "Date";

  public String getID()
  {
    return "Date";
  }

  public String getName() {
    return "@{Platform.DataSellection}";
  }

  public String getHtml(ZDMetaColumn mc, String value) {
    String code = "MetaValue_" + mc.getCode();
    if (StringUtil.isEmpty(value)) {
      value = MetaUtil.getValue(mc);
    }
    String styleText = MetaUtil.getClass(mc);
    String verify = MetaUtil.getVerifys(mc);
    return "<input type=\"text\" ztype=\"Date\" id=\"" + code + "\" value=\"" + value + "\" name=\"" + code + "\" " + styleText + 
      verify + " />\n";
  }
}