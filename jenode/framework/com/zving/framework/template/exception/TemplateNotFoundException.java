package com.zving.framework.template.exception;

import com.zving.framework.i18n.LangMapping;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.utility.StringFormat;

public class TemplateNotFoundException extends TemplateException
{
  private static final long serialVersionUID = 1L;

  public TemplateNotFoundException(String message)
  {
    super(message);
  }

  public TemplateNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public TemplateNotFoundException(String message, AbstractTag tag) {
    super((tag == null ? "" : StringFormat.format(LangMapping.get("Staticize.ErrorOnLine"), new Object[] { Integer.valueOf(tag.getStartLineNo()) })) + ":" + message);
  }
}