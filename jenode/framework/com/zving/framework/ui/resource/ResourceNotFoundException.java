package com.zving.framework.ui.resource;

public class ResourceNotFoundException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ResourceNotFoundException(Exception e)
  {
    super(e);
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }
}