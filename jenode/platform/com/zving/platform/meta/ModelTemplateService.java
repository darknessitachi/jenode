package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaColumnGroup;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelTemplateService
{
  public static final Pattern FieldGroupCodePattern = Pattern.compile("<model:fieldgroup .*?code=\"(.*?)\">", 34);

  public static final Pattern FieldCodePattern = Pattern.compile("<model:field .*?code=\"(.*?)\">", 34);
  public static final String DefaultTemplate = "<model:fieldgroup><fieldset class=\"extend\"><legend ><b>@{FieldGroup.Name}</b></legend><table id=\"table@{FieldGroup.Code}\" width=\"500\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" bordercolor=\"#eeeeee\"><model:field><tr><td width=\"160\">@{Field.Name}：</td><td>@{Field.ControlHtml}</td></tr></model:field></table></fieldset></model:fieldgroup>";

  public static String parseModelTemplate(long modelID, String template)
  {
    template = parseModelFieldGroupTag(template, modelID);
    template = parseModelFieldTag(template, modelID);
    return template;
  }

  private static String parseModelFieldGroupTag(String tmpl, long modelID)
  {
    StringBuilder sb = new StringBuilder();
    while (tmpl.indexOf("<model:fieldgroup") > -1) {
      int beginIndex = tmpl.indexOf("<model:fieldgroup");
      int endIndex = tmpl.indexOf("</model:fieldgroup>", beginIndex);
      sb.append(tmpl.substring(0, beginIndex));

      String tagStr = tmpl.substring(beginIndex, endIndex + 19);

      Matcher matcher = FieldGroupCodePattern.matcher(tagStr);
      String code = matcher.find() ? matcher.group(1) : "";
      Q qb = new Q("where ModelID=?", new Object[] { Long.valueOf(modelID) });
      if (StringUtil.isNotEmpty(code)) {
        qb.append(" and Code=?", new Object[] { code });
      }
      DAOSet groups = new ZDMetaColumnGroup().query(qb);

      String body = tagStr.substring(tagStr.indexOf(">") + 1, tagStr.indexOf("</model:fieldgroup>"));
      for (ZDMetaColumnGroup group : groups) {
        String newBody = body;
        Mapx groupMap = group.toMapx();
        for (String key : groupMap.keyArray()) {
          newBody = StringUtil.replaceEx(newBody, "@{FieldGroup." + key + "}", groupMap.getString(key));
        }
        newBody = parseModelFieldTag(newBody, modelID, group.getID());
        sb.append(newBody);
      }

      tmpl = tmpl.substring(endIndex + 19);
    }
    sb.append(tmpl);
    return sb.toString();
  }

  private static String parseModelFieldTag(String tmpl, long modelID, long groupID)
  {
    Q qb = new Q("where ModelID=? and GroupID=?", new Object[] { Long.valueOf(modelID), Long.valueOf(groupID) });
    return parseModelFieldTag(tmpl, qb);
  }

  private static String parseModelFieldTag(String tmpl, long modelID)
  {
    Q qb = new Q("where ModelID=?", new Object[] { Long.valueOf(modelID) });
    return parseModelFieldTag(tmpl, qb);
  }

  private static String parseModelFieldTag(String tmpl, Q qb)
  {
    StringBuilder sb = new StringBuilder();
    while (tmpl.indexOf("<model:field") > -1) {
      int beginIndex = tmpl.indexOf("<model:field");
      int endIndex = tmpl.indexOf("</model:field>", beginIndex);
      sb.append(tmpl.substring(0, beginIndex));

      String tagStr = tmpl.substring(beginIndex, endIndex + 14);

      Matcher matcher = FieldCodePattern.matcher(tagStr);
      String code = matcher.find() ? matcher.group(1) : "";
      Q qbTemp = new Q();
      qbTemp.setSQL(qb.getSQL());
      qbTemp.setParams((ArrayList)qb.getParams().clone());
      if (StringUtil.isNotEmpty(code)) {
        qbTemp.append(" and Code=?", new Object[] { code });
      }
      DAOSet cols = new ZDMetaColumn().query(qbTemp);
      qbTemp.append(" orderby orderflag asc ", new Object[0]);

      String body = tagStr.substring(tagStr.indexOf(">") + 1, tagStr.indexOf("</model:field>"));
      for (ZDMetaColumn col : cols) {
        String newBody = body;
        Mapx colMap = col.toMapx();
        colMap.put("ControlHtml", MetaUtil.getControlHTML(col));
        for (String key : colMap.keyArray()) {
          newBody = StringUtil.replaceEx(newBody, "@{Field." + key + "}", colMap.getString(key));
        }
        sb.append(newBody);
      }

      tmpl = tmpl.substring(endIndex + 14);
    }
    sb.append(tmpl);
    return sb.toString();
  }

  public static DataTable listToDataTable(List<IMetaModelTemplateType> list) {
    DataTable dt = new DataTable();
    dt.insertColumns(new String[] { "MMTemplateTypeID", "Name" });
    for (IMetaModelTemplateType mmtt : list) {
      dt.insertRow(new Object[] { mmtt.getID(), mmtt.getName() });
    }
    return dt;
  }

  public static DataTable getModelHtml(String modelCode) {
    long modelID = new Q("select ID from ZDMetaModel where Code=?", new Object[] { modelCode }).executeLong();
    return getModelHtml(modelID);
  }

  public static DataTable getModelHtml(long modelID) {
    DAOSet columns = new ZDMetaColumn().query(new Q("where ModelID=?", new Object[] { Long.valueOf(modelID) }));
    DataTable dt = columns.toDataTable();
    dt.insertColumn("ControlHtml");
    for (int i = 0; i < columns.size(); i++) {
      dt.set(i, "ControlHtml", MetaUtil.getControlHTML((ZDMetaColumn)columns.get(i)));
    }
    return dt;
  }

  public static void main(String[] args) {
    String template = "<table>\n<model:fieldgroup code=\"catalog_group2\">\n<div class=\"z-legend\"><b>@{FieldGroup.Name}</b></div>\n<model:field code=\"b\">\n<tr>\n<td>@{Field.Name}</td>\n</td>@{Field.ControlHtml}</td>\n</tr>\n</model:field>\n</model:fieldgroup>\n</table>";

    System.out.println(parseModelFieldGroupTag(template, 8L));
  }
}