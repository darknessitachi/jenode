package com.zving.framework.i18n;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.config.DefaultLanguage;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.PropertiesUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.ByteArrayInputStream;
import java.io.File;

public class LangLoader {
	public static LangMapping load() {
		String path = Config.getWEBINFPath();
		if (new File(path).exists()) {
			LangMapping mapping = loadMapping(path);
			String lang = DefaultLanguage.getValue();
			if (ObjectUtil.notEmpty(lang)) {
				mapping.DefaultLanguage = lang;
			}
			return mapping;
		}
		return new LangMapping();
	}

	public static LangMapping loadMapping(String path) {
		return loadMapping(path, PluginManager.getInstance());
	}

	public static LangMapping loadMapping(String path, PluginManager pm) {
		Mapx mapping = new Mapx();
		LangMapping lm = new LangMapping();
		lm.mapping = mapping;

		File langFile = new File(path + "/plugins/classes/lang/lang.i18n");
		if (langFile.exists()) {
			Mapx map = PropertiesUtil.read(langFile);
			lm.languageMap = map;
		}
		for (PluginConfig pc : pm.getAllPluginConfig()) {
			loadFromJar(new File(path + "/plugins/lib/" + pc.getID()
					+ ".plugin.jar"), lm);
			loadFromClasses(
					new File(path + "/plugins/classes/lang/" + pc.getID() + "/"),
					lm);
		}
		return lm;
	}

	private static void loadFromJar(File f, LangMapping lm) {
		if (!f.exists()) {
			return;
		}
		if (f.getName().endsWith(".plugin.jar"))
			try {
				 Mapx<String, Long> files = ZipUtil.getFileListInZip(f.getAbsolutePath());
				for (String fileName : files.keyArray()) {
					if (fileName.endsWith(".i18n")) {
						byte[] bs = ZipUtil.readFileInZip(f.getAbsolutePath(),
								fileName);
						int start = fileName.indexOf("/") >= 0 ? fileName
								.lastIndexOf("/") + 1 : 0;
						int end = fileName.lastIndexOf(".");
						String lang = fileName.substring(start, end);
						 Mapx<String, String> map = PropertiesUtil
								.read(new ByteArrayInputStream(bs));
						if (fileName.endsWith("/lang.i18n"))
							lm.languageMap = map;
						else
							for (String key : map.keySet())
								lm.put(lang, key, (String) map.get(key));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private static void loadFromClasses(File f, LangMapping lm) {
		if (!f.exists())
			return;
		try {
			for (File f2 : f.listFiles())
				if ((f2.isFile())
						&& (f2.getName().toLowerCase().endsWith(".i18n"))) {
					 Mapx<String, String> map = PropertiesUtil.read(f2);
					for (String key : map.keySet()) {
						String lang = f2.getName().substring(0,
								f2.getName().lastIndexOf("."));
						lm.put(lang, key, (String) map.get(key));
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}