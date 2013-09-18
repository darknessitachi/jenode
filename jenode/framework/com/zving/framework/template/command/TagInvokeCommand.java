package com.zving.framework.template.command;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.ITemplateCommand;
import com.zving.framework.template.exception.TemplateRuntimeException;
import java.util.List;

public class TagInvokeCommand
  implements ITemplateCommand
{
  private AbstractTag tag;
  private ITemplateCommand[] commands;
  private boolean hasBody;
  private int depth;

  public TagInvokeCommand(AbstractTag tag, List<ITemplateCommand> commandList, int depth, boolean hasBody)
  {
    this.tag = tag;
    this.commands = new ITemplateCommand[commandList.size()];
    this.commands = ((ITemplateCommand[])commandList.toArray(this.commands));
    this.hasBody = hasBody;
    this.depth = depth;
  }

  public int execute(AbstractExecuteContext context) throws TemplateRuntimeException {
    AbstractTag tagCopy = (AbstractTag)this.tag.clone();
    AbstractTag parent = context.getCurrentTag();
    tagCopy.setPageContext(context);
    tagCopy.setParent(parent);
    context.setCurrentTag(tagCopy);
    try {
      tagCopy.init();
    } catch (ExpressionException e) {
      throw new TemplateRuntimeException(e.getMessage());
    }
    int startFlag = tagCopy.doStartTag();
    if (startFlag == 5) {
      return startFlag;
    }
    if ((startFlag != 0) && (this.hasBody)) {
      if (startFlag != 1) {
        tagCopy.createBodyBuffer(this.depth);
      }
      do {
        for (ITemplateCommand command : this.commands) {
          if (command.execute(context) == 5)
            break;
        }
      }
      while (tagCopy.doAfterBody() == 2);
    }

    int endFlag = tagCopy.doEndTag();
    context.setCurrentTag(parent);
    if (endFlag == 5) {
      return 5;
    }
    return 6;
  }
}