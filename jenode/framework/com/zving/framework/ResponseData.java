package com.zving.framework;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataCollection;

public class ResponseData extends DataCollection
{
  private static final long serialVersionUID = 1L;
  public static final int SUCCESS = 1;
  public static final int FAILED = 0;
  private Mapx<String, String> Headers = new CaseIgnoreMapx();

  public int Status = 1;

  public String Message = "";

  public String getMessage()
  {
    return this.Message;
  }

  public void setFailedMessage(String message)
  {
    setStatusAndMessage(0, message);
  }

  @Deprecated
  public void setError(String message) {
    setFailedMessage(message);
  }

  @Deprecated
  public void setMessage(String message)
  {
    setSuccessMessage(message);
  }

  public void setSuccessMessage(String message)
  {
    setStatusAndMessage(1, message);
  }

  public int getStatus()
  {
    return this.Status;
  }

  public void setStatus(int status)
  {
    this.Status = status;
    put("_ZVING_STATUS", Integer.valueOf(this.Status));
  }

  public void setStatusAndMessage(int status, String message)
  {
    this.Status = status;
    put("_ZVING_STATUS", Integer.valueOf(this.Status));
    this.Message = message;
    put("_ZVING_MESSAGE", this.Message);
  }

  @Deprecated
  public void setLogInfo(int status, String message) {
    setStatusAndMessage(status, message);
  }

  public String toXML()
  {
    put("_ZVING_STATUS", Integer.valueOf(this.Status));
    return super.toXML();
  }

  public String toJSON()
  {
    put("_ZVING_STATUS", Integer.valueOf(this.Status));
    return super.toJSON();
  }

  public Mapx<String, String> getHeaders() {
    return this.Headers;
  }

  public void setHeaders(Mapx<String, String> headers) {
    this.Headers = headers;
  }
}