package com.zving.platform.meta.bl;

import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.pub.NoUtil;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaColumnGroup;
import com.zving.schema.ZDMetaModel;
import com.zving.schema.ZDMetaValue;
import com.zving.schema.ZDModelTemplate;
import java.util.Date;

public class MetaModelBL {
	public static final String MetaModelID = "MetaModelID";
	public static final String MetaColumnID = "MetaColumnID";
	public static final String MetaColumnGroupID = "MetaColumnGroupID";

	public static boolean isNameExists(String modelName, String modelCode,
			String modelID) {
		Q qb = new Q(
				"select count(1) from ZDMetaModel where (Name=? or Code=?)",
				new Object[] { modelName, modelCode });
		if (StringUtil.isNotEmpty(modelID)) {
			qb.append(" and ID<>?",
					new Object[] { Long.valueOf(Long.parseLong(modelID)) });
		}
		return qb.executeInt() > 0;
	}

	public static void insert(Mapx<String, Object> params, Transaction trans) {
		ZDMetaModel mm = new ZDMetaModel();
		mm.setValue(params);
		mm.setID(NoUtil.getMaxID("MetaModelID"));
		trans.add(mm, 1);
		mm.setAddTime(new Date());
		mm.setAddUser(User.getUserName());

		if (ObjectUtil.notEmpty(params.get("CopyID"))) {
			Q qb = new Q("where ModelID=?", new Object[] { Long.valueOf(params
					.getLong("CopyID")) });
			 DAOSet<ZDMetaColumn> columnSet = new ZDMetaColumn().query(qb);
			for (ZDMetaColumn c : columnSet) {
				c.setID(NoUtil.getMaxID("MetaColumnID"));
				c.setModelID(mm.getID());
				c.setAddTime(new Date());
				c.setAddUser(User.getUserName());
				c.setModifyTime(c.getAddTime());
				c.setModifyUser(c.getAddUser());
			}
			 DAOSet<ZDMetaColumnGroup> groupSet = new ZDMetaColumnGroup().query(qb);
			for (ZDMetaColumnGroup g : groupSet) {
				g.setID(NoUtil.getMaxID("MetaColumnGroupID"));
				g.setModelID(mm.getID());
				g.setAddTime(new Date());
				g.setAddUser(User.getUserName());
				g.setModifyTime(g.getAddTime());
				g.setModifyUser(g.getAddUser());
			}
			trans.insert(columnSet);
			trans.insert(groupSet);
		}
	}

	public static void save(Mapx<String, Object> params, Transaction trans) {
		ZDMetaModel mm = new ZDMetaModel();
		mm.setID(params.getLong("ID"));
		mm.fill();
		String oldModeCode = mm.getCode();
		trans.add((ZDMetaModel) mm.clone(), 4);

		mm.setValue(params);
		trans.add(mm, 2);
		mm.setModifyTime(new Date());
		mm.setModifyUser(User.getUserName());

		Object[] objs = ExtendManager.invoke(
				"com.zving.platform.BeforeMetaModelSave", new Object[] { mm,
						oldModeCode });
		if (ObjectUtil.notEmpty(objs)) {
			StringBuilder sb = new StringBuilder();
			for (Object obj : objs) {
				sb.append(ObjectUtil.empty(obj) ? "" : Lang.get(obj.toString()));
			}
			if (StringUtil.isNotNull(sb.toString().trim())) {
				Errorx.addError(sb.toString());
				return;
			}
		}
	}

	public static DAOSet<ZDMetaModel> delete(String ids, Transaction trans) {
		ids = StringUtil.replaceEx(ids, ",", "','");
		DAOSet set = new ZDMetaModel().query(new Q("where ID in ('" + ids
				+ "')", new Object[0]));
		trans.deleteAndBackup(set);

		DAOSet values = new ZDMetaValue().query(new Q("where ModelID in ('"
				+ ids + "')", new Object[0]));
		trans.deleteAndBackup(values);

		DAOSet columns = new ZDMetaColumn().query(new Q("where ModelID in ('"
				+ ids + "')", new Object[0]));
		trans.deleteAndBackup(columns);

		DAOSet columnGroups = new ZDMetaColumnGroup().query(new Q(
				"where ModelID in ('" + ids + "')", new Object[0]));
		trans.deleteAndBackup(columnGroups);

		DAOSet templates = new ZDModelTemplate().query(new Q(
				"where ModelID in ('" + ids + "')", new Object[0]));
		trans.deleteAndBackup(templates);

		Object[] objs = ExtendManager.invoke(
				"com.zving.platform.BeforeMetaModelDelete",
				new Object[] { set });
		if (ObjectUtil.notEmpty(objs)) {
			StringBuilder sb = new StringBuilder();
			for (Object obj : objs) {
				sb.append(ObjectUtil.empty(obj) ? "" : obj.toString());
			}
			if (sb.length() > 0) {
				Errorx.addError(sb.toString());
				return null;
			}
		}
		return set;
	}
}