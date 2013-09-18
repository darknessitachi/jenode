package com.zving.framework.utility;

import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAO;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.ui.control.SelectTag;
import com.zving.framework.ui.html.HtmlTD;
import com.zving.framework.ui.html.HtmlTR;
import com.zving.framework.ui.html.HtmlTable;
import com.zving.framework.ui.util.PlaceHolderUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HtmlUtil
{
  public static Mapx<String, Object> codeToMapx(String CodeType)
  {
     Mapx<String, Object> map = CacheManager.getMapx("Code", CodeType);
    Mapx map2 = new Mapx();
    if (map == null) {
      return map2;
    }
    for (String key : map.keyArray()) {
      if (map.get(key).toString().startsWith("{")) {
        DAO dao = (DAO)map.get(key);
        map2.put(key, LangUtil.get(dao.getV("CodeName")+""));
      } else {
        map2.put(key, LangUtil.get(map.get(key).toString()));
      }
    }
    return map2;
  }

  public static String codeToOptions(String CodeType)
  {
    return codeToOptions(CodeType, null);
  }

  public static String codeToOptions(String CodeType, Object checkedValue)
  {
    return mapxToOptions(codeToMapx(CodeType), checkedValue);
  }

  public static String codeToSelectOptions(String CodeType, boolean addBlankOptionFlag)
  {
    return mapxToOptions(codeToMapx(CodeType), "option", null, addBlankOptionFlag);
  }

  public static String codeToSelectOptions(String CodeType, Object checkedValue)
  {
    return mapxToOptions(codeToMapx(CodeType), "option", checkedValue, false);
  }

  public static String codeToOptions(String CodeType, boolean addBlankOptionFlag)
  {
    return mapxToOptions(codeToMapx(CodeType), "span", null, addBlankOptionFlag);
  }

  public static String codeToOptions(String CodeType, Object checkedValue, boolean addBlankOptionFlag)
  {
    return mapxToOptions(codeToMapx(CodeType), "span", checkedValue, addBlankOptionFlag);
  }

  public static String codeToRadios(String name, String CodeType)
  {
    return codeToRadios(name, CodeType, null, false);
  }

  public static String codeToRadios(String name, String CodeType, Object checkedValue)
  {
    return codeToRadios(name, CodeType, checkedValue, false);
  }

  public static String codeToRadios(String name, String CodeType, boolean direction)
  {
    return codeToRadios(name, CodeType, null, direction);
  }

  public static String codeToRadios(String name, String CodeType, Object checkedValue, boolean direction)
  {
    return mapxToRadios(name, codeToMapx(CodeType), checkedValue, direction);
  }

  public static String codeToCheckboxes(String name, String CodeType)
  {
    return mapxToCheckboxes(name, codeToMapx(CodeType));
  }

  public static String codeToCheckboxes(String name, String CodeType, boolean direction)
  {
    return codeToCheckboxes(name, CodeType, new String[0], direction);
  }

  public static String codeToCheckboxes(String name, String CodeType, DataTable checkedDT)
  {
    return codeToCheckboxes(name, CodeType, checkedDT, false);
  }

  public static String codeToCheckboxes(String name, String CodeType, DataTable checkedDT, boolean direction)
  {
    return mapxToCheckboxes(name, codeToMapx(CodeType), checkedDT, null, direction);
  }

  public static String codeToCheckboxes(String name, String CodeType, String[] checkedArray) {
    return codeToCheckboxes(name, CodeType, checkedArray, false);
  }

  public static String codeToCheckboxes(String name, String CodeType, String[] checkedArray, boolean direction)
  {
    return mapxToCheckboxes(name, codeToMapx(CodeType), checkedArray, null, direction);
  }

  public static String mapxToCheckboxesTable(String name, Mapx<?, ?> map, Object[] checkedArray, Object[] disabledValue, int colNum)
  {
    if (colNum < 1) {
      colNum = 1;
    }
    FastStringBuilder sb = new FastStringBuilder();
    sb.append("<table width='100%'>");
    int i = 0;
    for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
      if (i % colNum == 0) {
        sb.append("<tr>");
      }
      Object value = map.get(key);
      sb.append("<td width='").append(100 / colNum).append("%'><input type='checkbox'");
      sb.append(" name='").append(name);
      sb.append("' id='").append(name).append("_").append(i).append("' value='");
      sb.append(value);
      sb.append("'");
      if (disabledValue != null) {
        for (int j = 0; j < disabledValue.length; j++) {
          if (value.equals(disabledValue[j])) {
            sb.append(" disabled='true'");
            break;
          }
        }
      }

      if (checkedArray != null) {
        for (int j = 0; j < checkedArray.length; j++) {
          if (value.equals(checkedArray[j])) {
            sb.append(" checked='true'");
            break;
          }
        }
      }

      sb.append(" >");
      sb.append("<label for='").append(name).append("_").append(i).append("'>");
      sb.append(map.get(value));
      sb.append("</label>&nbsp;</td>");
      if (i % colNum == colNum - 1) {
        sb.append("</tr>");
      }
      i++;
    }
    if (map.size() % colNum != 0) {
      for (i = 0; i < colNum - map.size() % colNum; i++) {
        sb.append("<td>&nbsp;</td>");
      }
      sb.append("</tr>");
    }
    sb.append("</table>");
    return sb.toStringAndClose();
  }

  public static String mapxToCheckboxesTable(String name, Mapx<?, ?> map, DataTable checkedDT, Object[] disabledValue, int colNum) {
    String[] checkedArray = new String[checkedDT.getRowCount()];
    for (int i = 0; i < checkedDT.getRowCount(); i++) {
      checkedArray[i] = checkedDT.getString(i, 0);
    }
    return mapxToCheckboxesTable(name, map, checkedArray, disabledValue, colNum);
  }

  public static String mapxToCheckboxesTable(String name, String CodeType, DataTable checkedDT, Object[] disabledValue, int colNum) {
    return mapxToCheckboxesTable(name, codeToMapx(CodeType), checkedDT, disabledValue, colNum);
  }

  public static String codeToCheckboxesTable(String name, String CodeType, DataTable checkedDT, int colNum) {
    return codeToCheckboxesTable(name, CodeType, checkedDT, null, colNum);
  }

  public static String codeToCheckboxesTable(String name, String CodeType, DataTable checkedDT, Object[] disabledValue, int colNum) {
    return mapxToCheckboxesTable(name, CodeType, checkedDT, disabledValue, colNum);
  }

  public static String mapxToCheckboxesTable(String name, String CodeType, Object[] checkedArray, Object[] disabledValue, int colNum) {
    return mapxToCheckboxesTable(name, codeToMapx(CodeType), checkedArray, disabledValue, colNum);
  }

  public static String mapxToCheckboxesTable(String name, String CodeType, Object[] checkedArray, int colNum) {
    return mapxToCheckboxesTable(name, codeToMapx(CodeType), checkedArray, null, colNum);
  }

  public static String codeToCheckboxesTable(String name, String CodeType, int colNum) {
    return mapxToCheckboxesTable(name, CodeType, null, colNum);
  }

  public static String codeToCheckboxesTable(String name, String CodeType, Object[] checkedArray, int colNum) {
    return mapxToCheckboxesTable(name, CodeType, checkedArray, new String[0], colNum);
  }

  public static String mapxToOptions(Map<?, ?> map) {
    return mapxToOptions(map, null);
  }

  public static String mapxToOptions(Map<?, ?> map, Object checkedValue) {
    return mapxToOptions(map, checkedValue, false);
  }

  public static String mapxToOptions(Map<?, ?> map, boolean addBlankOptionFlag) {
    return mapxToOptions(map, null, addBlankOptionFlag);
  }

  public static String mapxToOptions(Map<?, ?> map, Object checkedValue, boolean addBlankOptionFlag) {
    return mapxToOptions(map, "span", checkedValue, addBlankOptionFlag);
  }

  public static String mapxToOptions(Map<?, ?> map, String type, Object checkedValue, boolean addBlankOptionFlag) {
    FastStringBuilder sb = new FastStringBuilder();
    if (addBlankOptionFlag) {
      sb.append("<").append(type).append(" value=''></").append(type).append(">");
    }
    if (map == null) {
      return sb.toString();
    }
    Object[] keys = map.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      sb.append("<").append(type).append(" value=\"");
      Object v = keys[i];
      sb.append(v);
      if ((v != null) && (v.equals(checkedValue)))
        sb.append("\" selected='true' >");
      else {
        sb.append("\">");
      }
      sb.append(map.get(v));
      sb.append("</").append(type).append(">");
    }
    return sb.toStringAndClose();
  }

  public static String mapxToRadios(String name, Map<?, ?> map) {
    return mapxToRadios(name, map, null, false);
  }

  public static String mapxToRadios(String name, Map<?, ?> map, Object checkedValue) {
    return mapxToRadios(name, map, checkedValue, false);
  }

  public static String mapxToRadios(String name, Map<?, ?> map, Object checkedValue, boolean direction) {
    FastStringBuilder sb = new FastStringBuilder();
    Object[] keys = map.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      Object value = keys[i];
      sb.append("<input type='radio' name='").append(name);
      sb.append("' id='").append(name).append("_").append(i).append("' value='");
      sb.append(value);
      if (value.equals(checkedValue))
        sb.append("' checked='true' >");
      else {
        sb.append("' />");
      }
      sb.append("<label for='").append(name).append("_").append(i).append("'>");
      sb.append(map.get(value));
      sb.append("</label>&nbsp;");
      if (direction) {
        sb.append("<br>");
      }
    }
    return sb.toStringAndClose();
  }

  public static String mapxToCheckboxes(String name, Map<?, ?> map) {
    return mapxToCheckboxes(name, map, (Object[])null, null);
  }

  public static String mapxToCheckboxes(String name, Map<?, ?> map, Object[] checkedArray) {
    return mapxToCheckboxes(name, map, checkedArray, null);
  }

  public static String mapxToCheckboxes(String name, Map<?, ?> map, DataTable checkedDT, Object[] disabledValue)
  {
    return mapxToCheckboxes(name, map, checkedDT, disabledValue, false);
  }

  public static String mapxToCheckboxes(String name, Map<?, ?> map, DataTable checkedDT, Object[] disabledValue, boolean direction) {
    String[] checkedArray = new String[checkedDT.getRowCount()];
    for (int i = 0; i < checkedDT.getRowCount(); i++) {
      checkedArray[i] = checkedDT.getString(i, 0);
    }
    return mapxToCheckboxes(name, map, checkedArray, disabledValue, direction);
  }

  public static String mapxToCheckboxes(String name, Map<?, ?> map, Object[] checkedArray, Object[] disabledValue) {
    return mapxToCheckboxes(name, map, checkedArray, disabledValue, false);
  }

  public static String mapxToCheckboxes(String name, Map<?, ?> map, Object[] checkedArray, Object[] disabledValue, boolean direction) {
    FastStringBuilder sb = new FastStringBuilder();
    int k = 0;
    for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext(); ) { Object value = localIterator.next();
      sb.append("<input type='checkbox'");
      sb.append(" name='").append(name);
      sb.append("' id='").append(name).append("_").append(k).append("' value='");
      sb.append(value);
      sb.append("'");
      if (disabledValue != null) {
        for (int j = 0; j < disabledValue.length; j++) {
          if (value.equals(disabledValue[j])) {
            sb.append(" disabled='true'");
            break;
          }
        }
      }

      if (checkedArray != null) {
        for (int j = 0; j < checkedArray.length; j++) {
          if (value.equals(checkedArray[j])) {
            sb.append(" checked='true'");
            break;
          }
        }
      }

      sb.append(" >");
      sb.append("<label for='").append(name).append("_").append(k).append("'>");
      sb.append(map.get(value));
      sb.append("</label>&nbsp;");
      if (direction) {
        sb.append("<br>");
      }
      k++;
    }
    return sb.toStringAndClose();
  }

  public static String arrayToOptions(String[] array) {
    return arrayToOptions(array, null);
  }

  public static String arrayToOptions(String[] array, Object checkedValue) {
    return arrayToOptions(array, checkedValue, false);
  }

  public static String arrayToOptions(String[] array, boolean addBlankOptionFlag) {
    return arrayToOptions(array, null, addBlankOptionFlag);
  }

  public static String arrayToOptions(String[] array, Object checkedValue, boolean addBlankOptionFlag) {
    FastStringBuilder sb = new FastStringBuilder();
    if (addBlankOptionFlag) {
      sb.append("<span value=''></span>");
    }

    for (int i = 0; i < array.length; i++) {
      sb.append("<span value=\"");
      Object v = array[i];
      String value = (String)v;
      String[] arr = value.split(",");
      String name = value;
      if (arr.length > 1) {
        name = arr[0];
        value = arr[1];
      }
      sb.append(value);
      if ((value != null) && (value.equals((String)checkedValue)))
        sb.append("\" selected='true' >");
      else {
        sb.append("\">");
      }
      sb.append(name);
      sb.append("</span>");
    }
    return sb.toStringAndClose();
  }

  public static String queryToOptions(QueryBuilder qb) {
    return dataTableToOptions(qb.executeDataTable(), null);
  }

  public static String queryToOptions(QueryBuilder qb, Object checkedValue) {
    return dataTableToOptions(qb.executeDataTable(), checkedValue);
  }

  public static String queryToOptions(QueryBuilder qb, boolean addBlankOptionFlag) {
    return dataTableToOptions(qb.executeDataTable(), addBlankOptionFlag);
  }

  public static String queryToOptions(QueryBuilder qb, Object checkedValue, boolean addBlankOptionFlag) {
    return dataTableToOptions(qb.executeDataTable(), checkedValue, addBlankOptionFlag);
  }

  public static String dataTableToOptions(DataTable dt)
  {
    return dataTableToOptions(dt, null);
  }

  public static String dataTableToOptions(DataTable dt, Object checkedValue)
  {
    return dataTableToOptions(dt, checkedValue, false);
  }

  public static String dataTableToOptions(DataTable dt, boolean addBlankOptionFlag) {
    return dataTableToOptions(dt, null, addBlankOptionFlag);
  }

  public static String dataTableToOptions(DataTable dt, Object checkedValue, boolean addBlankOptionFlag) {
    FastStringBuilder sb = new FastStringBuilder();
    if (addBlankOptionFlag) {
      SelectTag.getOptionHtml(sb, "", "", false);
    }
    if (dt == null) {
      return null;
    }
    for (int i = 0; i < dt.getRowCount(); i++) {
      String value = dt.getString(i, 1);
      if (value.equals(checkedValue))
        SelectTag.getOptionHtml(sb, dt.getString(i, 0), value, true);
      else {
        SelectTag.getOptionHtml(sb, dt.getString(i, 0), value, false);
      }
    }
    return sb.toStringAndClose();
  }

  public static String dataTableToRadios(String name, DataTable dt)
  {
    return dataTableToRadios(name, dt, null, false);
  }

  public static String dataTableToRadios(String name, DataTable dt, String checkedValue)
  {
    return dataTableToRadios(name, dt, checkedValue, false);
  }

  public static String dataTableToRadios(String name, DataTable dt, boolean direction)
  {
    return dataTableToRadios(name, dt, null, direction);
  }

  public static String dataTableToRadios(String name, DataTable dt, Object checkedValue, boolean direction)
  {
    FastStringBuilder sb = new FastStringBuilder();
    for (int k = 0; k < dt.getRowCount(); k++) {
      String value = dt.getString(k, 1);
      sb.append("<input type='radio' name='").append(name);
      sb.append("' id='").append(name).append("_").append(k).append("' value='");
      sb.append(value);
      if (value.equals(checkedValue))
        sb.append("' checked >");
      else {
        sb.append("' >");
      }
      sb.append("<label for='").append(name).append("_").append(k).append("'>");
      sb.append(dt.getString(k, 0));
      sb.append("</label>&nbsp;");
      if (direction) {
        sb.append("<br>");
      }
    }
    return sb.toStringAndClose();
  }

  public static String arrayToRadios(String name, String[] array) {
    return arrayToRadios(name, array, null, false);
  }

  public static String arrayToRadios(String name, String[] array, String checkedValue) {
    return arrayToRadios(name, array, checkedValue, false);
  }

  public static String arrayToRadios(String name, String[] array, boolean direction) {
    return arrayToRadios(name, array, null, direction);
  }

  public static String arrayToRadios(String name, String[] array, Object checkedValue, boolean direction) {
    FastStringBuilder sb = new FastStringBuilder();
    for (int k = 0; k < array.length; k++) {
      String value = array[k];
      sb.append("<input type='radio' name='").append(name);
      sb.append("' id='").append(name).append("_").append(k).append("' value='");
      sb.append(value);
      if (value.equals(checkedValue))
        sb.append("' checked >");
      else {
        sb.append("' >");
      }
      sb.append("<label for='").append(name).append("_").append(k).append("'>");
      sb.append(value);
      sb.append("</label>");
      if (direction) {
        sb.append("<br>");
      }
    }
    return sb.toStringAndClose();
  }

  public static String mapxToRadiosTable(String name, Map<?, ?> map, Object checkedValue, int colNum)
  {
    if (colNum < 1) {
      colNum = 1;
    }
    FastStringBuilder sb = new FastStringBuilder();
    sb.append("<table width='100%'>");
    int i = 0;
    for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext(); ) { Object value = localIterator.next();
      if (i % colNum == 0) {
        sb.append("<tr>");
      }
      sb.append("<td width='").append(100 / colNum).append("%'><input type='radio' name='").append(name);
      sb.append("' id='").append(name).append("_").append(i).append("' value='");
      sb.append(value);
      if (value.equals(checkedValue))
        sb.append("' checked >");
      else {
        sb.append("' >");
      }
      sb.append("<label for='").append(name).append("_").append(i).append("'>");
      sb.append(map.get(value));
      sb.append("</label></td>");
      if (i % colNum == colNum - 1) {
        sb.append("</tr>");
      }
      i++;
    }
    if (map.size() % colNum != 0) {
      for (i = 0; i < colNum - map.size() % colNum; i++) {
        sb.append("<td>&nbsp;</td>");
      }
      sb.append("</tr>");
    }
    sb.append("</table>");
    return sb.toStringAndClose();
  }

  public static String codeToRadiosTable(String name, String CodeType, Object checkedValue, int colNum) {
    return mapxToRadiosTable(name, codeToMapx(CodeType), checkedValue, colNum);
  }

  public static String codeToRadiosTable(String name, String CodeType, int colNum) {
    return mapxToRadiosTable(name, codeToMapx(CodeType), null, colNum);
  }

  public static String codeToRadiosTable(String name, String CodeType) {
    return mapxToRadiosTable(name, codeToMapx(CodeType), null, 0);
  }

  public static String dataTableToCheckboxes(String name, DataTable dt)
  {
    return dataTableToCheckboxes(name, dt, false);
  }

  public static String dataTableToCheckboxes(String name, DataTable dt, boolean direction)
  {
    return dataTableToCheckboxes(name, dt, new String[0], direction);
  }

  public static String dataTableToCheckboxes(String name, DataTable dt, DataTable checkedDT)
  {
    return dataTableToCheckboxes(name, dt, checkedDT, false);
  }

  public static String dataTableToCheckboxes(String name, DataTable dt, DataTable checkedDT, boolean direction)
  {
    String[] checkedArray = new String[checkedDT.getRowCount()];
    for (int i = 0; i < checkedDT.getRowCount(); i++) {
      checkedArray[i] = checkedDT.getString(i, 0);
    }
    return dataTableToCheckboxes(name, dt, checkedArray, direction);
  }

  public static String dataTableToCheckboxes(String name, DataTable dt, String[] checkedArray)
  {
    return dataTableToCheckboxes(name, dt, checkedArray, false);
  }

  public static String dataTableToCheckboxes(String name, DataTable dt, String[] checkedArray, boolean direction)
  {
    FastStringBuilder sb = new FastStringBuilder();
    for (int k = 0; k < dt.getRowCount(); k++) {
      String value = dt.getString(k, 1);
      sb.append("<input type='checkbox' name='").append(name);
      sb.append("' id='").append(name).append("_").append(k).append("' value='");
      sb.append(value);
      boolean flag = false;
      if (checkedArray != null) {
        for (int j = 0; j < checkedArray.length; j++) {
          if (value.equals(checkedArray[j])) {
            sb.append("' checked >");
            flag = true;
            break;
          }
        }
      }

      if (!flag) {
        sb.append("' >");
      }
      sb.append("<label for='").append(name).append("_").append(k).append("'>");
      sb.append(dt.getString(k, 0));
      sb.append("</label>&nbsp;");
      if (direction) {
        sb.append("<br>");
      }
    }
    return sb.toStringAndClose();
  }

  public static String arrayToCheckboxes(String name, String[] array) {
    return arrayToCheckboxes(name, array, null, null, false);
  }

  public static String arrayToCheckboxes(String name, String[] array, String[] checkedArray) {
    return arrayToCheckboxes(name, array, checkedArray, null, false);
  }

  public static String arrayToCheckboxes(String name, String[] array, String onclick) {
    return arrayToCheckboxes(name, array, null, onclick, false);
  }

  public static String arrayToCheckboxes(String name, String[] array, String[] checkedArray, String onclick, boolean direction) {
    FastStringBuilder sb = new FastStringBuilder();
    for (int k = 0; k < array.length; k++) {
      String value = array[k];
      sb.append("<label><input type='checkbox'");
      if (StringUtil.isNotEmpty(onclick)) {
        sb.append("onclick='").append(onclick).append("'");
      }
      sb.append(" name='").append(name);
      sb.append("' id='").append(name).append("_").append(k).append("'value='");
      sb.append(value);
      boolean flag = false;
      for (int j = 0; (checkedArray != null) && (j < checkedArray.length); j++) {
        if (value.equals(checkedArray[j])) {
          sb.append("' checked >");
          flag = true;
          break;
        }
      }
      if (!flag) {
        sb.append("' >");
      }
      sb.append("<label for='").append(name).append("_").append(k).append("'>");
      sb.append(value);
      sb.append("</label>");
      if (direction) {
        sb.append("<br>");
      }
    }
    return sb.toStringAndClose();
  }

  public static String replacePlaceHolder(String html, Map<?, ?> map, boolean blankFlag)
  {
    return replacePlaceHolder(html, map, blankFlag, false);
  }

  public static String replacePlaceHolder(String html, Map<?, ?> map, boolean blankFlag, boolean spaceFlag)
  {
    try
    {
      return PlaceHolderUtil.replacePlaceHolder(html, map, blankFlag, spaceFlag);
    } catch (ExpressionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String replacePlaceHolder(String html, AbstractExecuteContext context, boolean blankFlag, boolean spaceFlag)
  {
    try
    {
      return PlaceHolderUtil.replacePlaceHolder(html, context, blankFlag, spaceFlag);
    } catch (ExpressionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String replaceWithDataTable(DataTable dt, String html)
  {
    return replaceWithDataTable(dt, html, false, true);
  }

  public static String replaceWithDataTable(DataTable dt, String html, boolean spaceFlag) {
    return replaceWithDataTable(dt, html, false, spaceFlag);
  }

  public static String replaceWithDataTable(DataTable dt, String html, boolean blankFlag, boolean spaceFlag)
  {
    try
    {
      return PlaceHolderUtil.replaceWithDataTable(html, dt, blankFlag, spaceFlag);
    } catch (ExpressionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String replaceWithDataRow(DataRow dr, String html)
  {
    return replaceWithDataRow(dr, html, true);
  }

  public static String replaceWithDataRow(DataRow dr, String html, boolean spaceFlag) {
    return replaceWithDataRow(dr, html, false, spaceFlag);
  }

  public static String replaceWithDataRow(DataRow dr, String html, boolean blankFlag, boolean spaceFlag)
  {
    return replacePlaceHolder(html, dr.toMapx(), blankFlag, spaceFlag);
  }

  public static HtmlTable dataTableToHtmlTable(DataTable dt, String cellHtml, int columnCount) {
    HtmlTable table = new HtmlTable();
    HtmlTR tr = null;
    for (int i = 0; i < dt.getRowCount(); i++) {
      if (i % columnCount == 0) {
        tr = new HtmlTR();
        table.addTR(tr);
      }
      HtmlTD td = new HtmlTD();
      td.InnerHTML = replaceWithDataRow(dt.getDataRow(i), cellHtml);
      tr.addChild(td);
    }
    for (int i = 0; i < dt.getRowCount() % columnCount; i++) {
      HtmlTD td = new HtmlTD();
      td.InnerHTML = "&nbsp;";
      tr.addChild(td);
    }
    return table;
  }
}