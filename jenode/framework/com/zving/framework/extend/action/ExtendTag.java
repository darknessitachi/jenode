package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendManager;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import java.util.ArrayList;
import java.util.List;

public class ExtendTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;

  public void setId(String id)
  {
    this.id = id;
  }

  public int doStartTag() {
    ExtendManager.invoke(this.id, new Object[] { this.pageContext });
    return 0;
  }

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "extend";
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    return list;
  }

  public String getDescription()
  {
    return "@{Framework.ExtendTag.Desc}";
  }

  public String getName()
  {
    return "@{Framework.ExtendTag.Name}";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}