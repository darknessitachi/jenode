package com.zving.framework.i18n;

import com.zving.framework.User;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LangTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String Default;
  private String language;
  private String oldLanguage;

  public int doStartTag()
    throws TemplateRuntimeException
  {
    try
    {
      String str = LangMapping.get(this.id);
      if (this.id != null) {
        if (ObjectUtil.empty(str)) {
          str = this.Default;
        }
        if (str == null) {
          str = "@{" + this.id + "}";
        }
        this.pageContext.getOut().write(str);
      }
      if (this.language != null) {
        this.oldLanguage = User.getLanguage();
        User.setLanguage(this.language);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 2;
  }

  public int doAfterBody() throws TemplateRuntimeException {
    if (this.oldLanguage != null) {
      User.setLanguage(this.oldLanguage);
    }
    if (this.language != null) {
      String content = getBody().trim();
      getPreviousOut().write(content);
    }
    return 6;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String var) {
    this.id = var;
  }

  public String getDefault() {
    return this.Default;
  }

  public void setDefault(String default1) {
    this.Default = default1;
  }

  public String getLanguage() {
    return this.language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "lang";
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", 1, "@{Framework.LangTag.ID}"));
    list.add(new TagAttr("language", 1, "@{Framework.LangTag.Language}"));
    list.add(new TagAttr("default", 1, "@{Framework.LangTag.Default}"));
    return list;
  }

  public String getName()
  {
    return "@{Framework.LangTag.Name}";
  }

  public String getDescription()
  {
    return "@{Framework.LangTag.Desc}";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}