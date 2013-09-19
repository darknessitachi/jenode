package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.pub.PlatformUtil;
import com.zving.schema.ZDMetaColumn;

public class MetadataCheckboxColumn implements IMetadataColumnControlType {
	public static final String ID = "Checkbox";

	public String getID() {
		return "Checkbox";
	}

	public String getName() {
		return "@{Platform.CheckBox}";
	}

	public String getHtml(ZDMetaColumn mc, String value) {
		String code = "MetaValue_" + mc.getCode();
		if (StringUtil.isEmpty(value)) {
			value = MetaUtil.getValue(mc);
		}
		StringBuffer sb = new StringBuffer();
		String options = mc.getListOptions();

		Mapx<String, Object> map = null;
		if (StringUtil.isNotEmpty(options)) {
			String type = options.substring(0, options.indexOf(":"));
			options = options.substring(options.indexOf(":") + 1);
			if (type.equals("Code")) {
				map = PlatformUtil.getCodeMap(options);
				LangUtil.decode(map);
			}
			String str;
			if (type.equals("Input")) {
				String[] arr = options.split("\\n");
				map = new Mapx();
				for (int i = 0; i < arr.length; i++) {
					str = arr[i].trim();
					if (StringUtil.isNotEmpty(str)) {
						map.put(str, str);
					}
				}
			}
			if (type.equals("Method")) {
				IMethodLocator m = MethodLocatorUtil.find(options);
				PrivCheck.check(m);
				map = (Mapx) m.execute(new Object[0]);
			}
			int i = 0;
			for (String key : map.keySet()) {
				String checked = key.equals(value) ? " checked=\"true\"" : "";
				sb.append("<input type=\"checkbox\" id=\""
						+ code
						+ "_"
						+ i
						+ "\" value=\""
						+ key
						+ "\" name=\""
						+ code
						+ "\" "
						+ checked
						+ (YesOrNo.isYes(mc.getMandatoryFlag()) ? "verify=\"NotNull\""
								: "") + "/>\n");
				sb.append("<label for=\"" + code + "_" + i + "\">"
						+ map.getString(key) + "</label>");
				i++;
			}
		}
		return sb.toString();
	}
}