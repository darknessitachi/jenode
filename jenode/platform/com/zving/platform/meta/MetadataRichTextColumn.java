package com.zving.platform.meta;

import com.zving.framework.User;
import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMetaColumn;

public class MetadataRichTextColumn
  implements IMetadataColumnControlType
{
  public static final String ID = "RichText";

  public String getID()
  {
    return "RichText";
  }

  public String getName() {
    return "@{Platform.RichText}";
  }

  public String getHtml(ZDMetaColumn mc, String value) {
    String code = "MetaValue_" + mc.getCode();
    if (StringUtil.isEmpty(value)) {
      value = MetaUtil.getValue(mc);
    }
    String styleText = MetaUtil.getClass(mc);

    StringBuilder sb = new StringBuilder();
    sb.append("<textarea id=\"").append(code).append("\" ").append(styleText).append(">" + value + "</textarea>");
    sb.append("<script>");
    sb.append("if(CKEDITOR.instances.").append(code).append("){delete CKEDITOR.instances.").append(code).append(";}");
    sb.append("var editor_").append(code).append(" = CKEDITOR.replace( '").append(code).append("',{");
    sb.append("height : 120,");
    sb.append("width : '500',");
    sb.append("toolbar : 'Article',");
    sb.append("startupFocus : false,");
    sb.append("sharedSpaces: {top : 'EditorToolbar',bottom : 'ElementPath'},");
    sb.append("language: '").append(User.getLanguage()).append("'");
    sb.append("});");
    if (StringUtil.isNotEmpty(value)) {
      sb.append("editor_").append(code).append(".setData($V('#").append(code).append("'));");
    }
    sb.append("editor_").append(code).append(".on(\"blur\", function(){editor_").append(code).append(".updateElement();})");
    sb.append("</script>");
    return sb.toString();
  }
}