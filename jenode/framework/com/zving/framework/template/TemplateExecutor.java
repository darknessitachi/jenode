package com.zving.framework.template;

import com.zving.framework.collection.Treex;
import com.zving.framework.template.exception.TemplateException;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.utility.ObjectUtil;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

public class TemplateExecutor
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected ITemplateCommand[] commands;
  protected String fileName;
  protected String contentType;
  protected boolean sessionFlag;
  protected long lastModified;
  protected long lastCheckTime;
  protected boolean fromJar;
  protected ITemplateManagerContext managerContext;
  protected Treex<AbstractTag> tree = new Treex();

  public TemplateExecutor(ITemplateManagerContext executeContext) {
    this.managerContext = executeContext;
    this.lastCheckTime = System.currentTimeMillis();
  }

  public void init(List<ITemplateCommand> commandList) {
    this.commands = new ITemplateCommand[commandList.size()];
    this.commands = ((ITemplateCommand[])commandList.toArray(this.commands));
  }

  public void execute(AbstractExecuteContext context) throws TemplateRuntimeException {
    for (ITemplateCommand command : this.commands)
      try {
        if (command.execute(context) != 5);
      }
      catch (TemplateRuntimeException t)
      {
        context.getRootWriter().print(t.getMessage());
      } catch (NullPointerException t) {
        throw new TemplateException(t.getClass().getName() + "\n" + ObjectUtil.getStack(t), t);
      }
  }

  public String getFileName()
  {
    return this.fileName;
  }

  public long getLastModified() {
    return this.lastModified;
  }

  public boolean isFromJar() {
    return this.fromJar;
  }

  public void setFromJar(boolean fromJar) {
    this.fromJar = fromJar;
  }

  public boolean isSessionFlag() {
    return this.sessionFlag;
  }

  public ITemplateManagerContext getExecuteContext() {
    return this.managerContext;
  }

  public long getLastCheckTime() {
    return this.lastCheckTime;
  }

  public void setLastCheckTime(long lastCheckTime) {
    this.lastCheckTime = lastCheckTime;
  }

  public Treex<AbstractTag> getTagTree() {
    return this.tree;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }
}