package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.orm.DAOColumn;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.orm.DAOUtil;
import com.zving.framework.utility.commons.ArrayUtils;
import com.zving.platform.pub.PlatformCache;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaModel;
import java.util.ArrayList;
import java.util.List;

public class MetadataService extends AbstractExtendService<IMetaModelType> {
	public static final String ControlPrefix = "MetaValue_";
	public static Mapx<String, List<String>> fieldMap = new Mapx();

	public static MetadataService getInstance() {
		return (MetadataService) findInstance(MetadataService.class);
	}

	public static void loadFieldMap() {
		 DAOSet<ZDMetaModel> mms = new ZDMetaModel().query();
		for (ZDMetaModel mm : mms)
			if (!fieldMap.containsKey(mm.getTargetTable())) {
				try {
					DAOColumn[] cols = DAOUtil.getColumns(MetaUtil
							.newTargetTableInstance(mm.getTargetTable()));
					List list = new ArrayList();
					for (int i = 2; i < cols.length; i++) {
						list.add(cols[i].getColumnName());
					}
					fieldMap.put(mm.getTargetTable(), list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	public static String arrangeTargetField(long modelID, String targetField,
			String dataType) throws Exception {
		if (fieldMap.size() == 0) {
			loadFieldMap();
		}
		String newTargetField = dataType;
		if ("Datetime".equals(newTargetField)) {
			newTargetField = "Date";
		}

		MetaModel mm = PlatformCache.getMetaModel(modelID);
		List fields = (List) fieldMap.get(mm.getDAO().getTargetTable());

		Mapx mapping = MetaUtil.getMapping(modelID);
		String[] usedField = new String[mapping.size()];
		for (int i = 0; i < mapping.size(); i++) {
			usedField[i] = ((ZDMetaColumn) mapping.valueArray().get(i))
					.getTargetField();
		}

		int i = 1;
		while (true) {
			if (!ArrayUtils.contains(usedField, newTargetField + i)) {
				newTargetField = newTargetField + i;
				break;
			}
			i++;
		}

		if (!fields.contains(newTargetField)) {
			throw new Exception("no such field '" + newTargetField
					+ "' in table " + mm.getDAO().getTargetTable());
		}
		return newTargetField;
	}
}