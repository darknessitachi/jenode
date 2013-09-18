package com.zving.framework.data.command;

public abstract interface IDBCommand
{
  public abstract String getPrefix();

  public abstract String[] getDefaultSQLArray(String paramString);

  public abstract void parse(String paramString);

  public abstract String toJSON();
}