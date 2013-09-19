package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.utility.HtmlUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.pub.PlatformUtil;
import com.zving.schema.ZDMetaColumn;

public class MetadataSelectColumn
  implements IMetadataColumnControlType
{
  public static final String ID = "Select";

  public String getID()
  {
    return "Select";
  }

  public String getName() {
    return "@{Platform.Select}";
  }

  public String getHtml(ZDMetaColumn mc, String value)
  {
    String code = "MetaValue_" + mc.getCode();
    if (StringUtil.isEmpty(value)) {
      value = MetaUtil.getValue(mc);
    }
    String styleText = MetaUtil.getClass(mc);
    String verify = MetaUtil.getVerifys(mc);

    StringBuffer sb = new StringBuffer();
    String options = mc.getListOptions();
    Mapx map = null;
    if (StringUtil.isNotEmpty(options)) {
      String type = options.substring(0, options.indexOf(":"));
      options = options.substring(options.indexOf(":") + 1);
      if (type.equals("Code")) {
        map = PlatformUtil.getCodeMap(options);
        LangUtil.decode(map);
      }
      if (type.equals("Input")) {
        String[] arr = options.split("\\n");
        map = new Mapx();
        for (int i = 0; i < arr.length; i++) {
          String str = arr[i].trim();
          if (StringUtil.isNotEmpty(str)) {
            map.put(str, str);
          }
        }
      }
      if (type.equals("Method")) {
        IMethodLocator m = MethodLocatorUtil.find(options);
        PrivCheck.check(m);
        map = (Mapx)m.execute(new Object[0]);
      }
      if (mc.getControlType().equals("Select")) {
        sb.append("<select id=\"" + code + "\" name=\"" + code + "\" " + styleText + verify + ">\n");
        sb.append(HtmlUtil.mapxToOptions(map, "option", value, true));
        sb.append("</select>\n");
      }
    }
    return sb.toString();
  }
}