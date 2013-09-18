package com.zving.framework.extend.action;

import com.zving.framework.Current;
import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.ITemplateManager;
import com.zving.framework.template.ITemplateManagerContext;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class ZhtmlExtendAction
  implements IExtendAction
{
  public Object execute(Object[] args)
    throws ExtendException
  {
    AbstractExecuteContext pageContext = (AbstractExecuteContext)args[0];
    ZhtmlContext context = new ZhtmlContext(Current.getRequest());
    execute(context);
    if (!ObjectUtil.empty(context.getOut())) {
      pageContext.getOut().print(context.getOut());
    }
    if (context.getIncludes().size() > 0) {
      for (String file : context.getIncludes()) {
        try {
          AbstractExecuteContext includeContext = pageContext.getIncludeContext();
          includeContext.getManagerContext().getTemplateManager().execute(file, includeContext);
        } catch (Exception e) {
          e.printStackTrace();
          throw new ExtendException(e.getMessage());
        }
      }
    }
    return null;
  }

  public abstract void execute(ZhtmlContext paramZhtmlContext)
    throws ExtendException;
}