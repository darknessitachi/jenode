package com.zving.framework.template.exception;

import com.zving.framework.i18n.LangMapping;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.utility.StringFormat;

public class TemplateRuntimeException extends TemplateException
{
  private static final long serialVersionUID = 1L;

  public TemplateRuntimeException(String message)
  {
    super(message);
  }

  public TemplateRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public TemplateRuntimeException(Throwable cause) {
    super(cause);
  }

  public TemplateRuntimeException(String message, AbstractTag tag) {
    super((tag == null ? "" : StringFormat.format(LangMapping.get("Staticize.ErrorOnLine"), new Object[] { Integer.valueOf(tag.getStartLineNo()) })) + ":" + message);
  }
}