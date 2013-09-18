package com.zving.framework.i18n;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LangButtonTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String target;
  private String value;

  public int doStartTag()
  {
    try
    {
      if (LangUtil.getSupportedLanguages().size() > 0) {
        StringBuilder sb = new StringBuilder();
        if (ObjectUtil.empty(this.value)) {
          this.value = "";
        }
        sb.append("<input type=\"hidden\" value=\"").append(StringUtil.escape(this.value)).append("\" id=\"" + this.target + "_I18N\">");
        sb.append("<img src=\"")
          .append(Config.getContextPath())
          .append("icons/extra/i18n.gif\" align=\"absmiddle\" style=\"cursor:pointer\" onclick=\"Zving.Lang.onLangButtonClick('")
          .append(this.target).append("')\"/>");
        sb.append("<script>Zving.Page.onReady(function(){Zving.Node.setValue(\"" + this.target + "\",\"" + LangUtil.decode(this.value) + 
          "\");});</script>");
        this.pageContext.getOut().write(sb.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public String getTarget() {
    return this.target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "langbutton";
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("target", true));
    list.add(new TagAttr("value"));
    return list;
  }

  public String getName()
  {
    return "@{Framework.LangButtonTag.Name}";
  }

  public String getDescription()
  {
    return "@{Framework.LangButtonTag.Desc}";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}