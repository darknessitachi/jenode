package com.zving.platform.meta;

import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMetaColumn;

public class MetadataTextAreaColumn
  implements IMetadataColumnControlType
{
  public static final String ID = "TextArea";

  public String getID()
  {
    return "TextArea";
  }

  public String getName() {
    return "@{Platform.Textarea}";
  }

  public String getHtml(ZDMetaColumn mc, String value) {
    String code = "MetaValue_" + mc.getCode();
    if (StringUtil.isEmpty(value)) {
      value = MetaUtil.getValue(mc);
    }
    String styleText = MetaUtil.getClass(mc);
    String verify = MetaUtil.getVerifys(mc);
    return "<textarea id=\"" + code + "\" name=\"" + code + "\" " + styleText + verify + ">" + value + "</textarea>\n<br />";
  }
}