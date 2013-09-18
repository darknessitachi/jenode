package com.zving.framework;

import java.util.regex.Pattern;

public class Constant
{
  public static final Pattern PatternField = Pattern.compile("\\$\\{([\\w\\.]+?)\\}");

  public static final Pattern PatternSpeicalField = Pattern.compile("\\$\\{[@#](\\w+?)\\}");
  public static final String UserAttrName = "_ZVING_USER";
  public static final String DefaultAuthKey = "_ZVING_AUTHKEY";
  public static final String SessionIDCookieName = "JSESSIONID";
  public static final String ResponseScriptAttr = "_ZVING_SCRIPT";
  public static final String ResponseMessageAttrName = "_ZVING_MESSAGE";
  public static final String ResponseStatusAttrName = "_ZVING_STATUS";
  public static final String DataGridSQL = "_ZVING_DATAGRID_SQL";
  public static final String DataGridPageIndex = "_ZVING_PAGEINDEX";
  public static final String DataGridPageTotal = "_ZVING_PAGETOTAL";
  public static final String DataGridSortString = "_ZVING_SORTSTRING";
  public static final String DataGridInsertRow = "_ZVING_INSERTROW";
  public static final String DataGridMultiSelect = "_ZVING_MULTISELECT";
  public static final String DataGridAutoFill = "_ZVING_AUTOFILL";
  public static final String DataGridScroll = "_ZVING_SCROLL";
  public static final String DataTable = "_ZVING_DATATABLE";
  public static final String ID = "_ZVING_ID";
  public static final String Method = "_ZVING_METHOD";
  public static final String Page = "_ZVING_PAGE";
  public static final String Size = "_ZVING_SIZE";
  public static final String SortEnd = "_ZVING_SORTEND";
  public static final String DragHandle = "_ZVING_DRAGHANDLE";
  public static final String DragClass = "_ZVING_DRAGCLASS";
  public static final String TagBody = "_ZVING_TAGBODY";
  public static final String TreeLevel = "_ZVING_TREE_LEVEL";
  public static final String TreeLazy = "_ZVING_TREE_LAZY";
  public static final String TreeExpand = "_ZVING_TREE_EXPAND";
  public static final String TreeParentColumn = "_ZVING_TREE_PARENTCOLUMN";
  public static final String TreeStyle = "_ZVING_TREE_STYLE";
  public static final String Data = "_ZVING_DATA";
  public static final String URL = "_ZVING_URL";
  public static final String DataFormat = "_ZVING_DATA_FORMAT";
  public static final String Null = "_ZVING_NULL";
  public static final String PAGE_BREAK = "<!--_ZVING_PAGE_BREAK_-->";
  public static final String ActionInPageContext = "_ZVING_ACTION";
  public static final String LanguageCookieName = "_ZVING_LANGUAGE";
  public static final String NoSession = "_ZVING_NOSESSION";
}