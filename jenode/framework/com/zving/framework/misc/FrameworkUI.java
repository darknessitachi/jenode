package com.zving.framework.misc;

import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataCollection;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.utility.StringUtil;

public class FrameworkUI extends UIFacade {
	@Priv(login = false)
	public void sendPageCost() {
		String url = $V("URL");
		url = url.substring(url.indexOf("/", 8));
		int cost = Integer.parseInt($V("Cost"));
		int readyCost = Integer.parseInt($V("ReadyCost"));
		if (cost > 100)
			LogUtil.info("ClientCost\tReady=" + $V("ReadyCost") + "ms"
					+ (readyCost > 1000 ? "!" : "") + "\tLoad=" + $V("Cost")
					+ "ms" + (cost > 3000 ? "!" : "") + "\t" + url);
	}

	public static DataCollection callRemoteMethod(String url, String method,
			RequestData request) {
		if (!url.endsWith("/")) {
			url = StringUtil.concat(new Object[] { url, "/" });
		}
		if (!url.startsWith("http://")) {
			url = StringUtil.concat(new Object[] { "http://", url });
		}
		Mapx params = new Mapx();
		params.put("_ZVING_METHOD", method);
		params.put("_ZVING_DATA", request.toJSON());
		params.put("_ZVING_URL", url);
		params.put("_ZVING_DATA_FORMAT", "json");
		url = url + "ajax/invoke";
		String result = ServletUtil.postURLContent(url, params, "UTF-8");
		if (result != null) {
			DataCollection dc = new DataCollection();
			dc.parseJSON(result);
			return dc;
		}
		LogUtil.warn("Framework.callRemoteMethod() error,URL=" + url);
		return null;
	}
}