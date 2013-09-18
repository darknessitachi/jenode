package com.zving.framework.security;

import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import java.util.ArrayList;

public class VerifyRuleList
{
  private ArrayList<String[]> array = new ArrayList();
  private String Message;
  private RequestData Request;
  private ResponseData Response;

  public void add(String fieldID, String fieldName, String rule)
  {
    this.array.add(new String[] { fieldID, fieldName, rule });
  }

  public boolean doVerify()
  {
    VerifyRule rule = new VerifyRule();
    boolean flag = true;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.array.size(); i++) {
      String[] f = (String[])this.array.get(i);
      rule.setRule(f[2]);
      if (!rule.verify(this.Request.getString(f[0]))) {
        sb.append(rule.getMessages(f[1]));
        flag = false;
      }
    }
    if (!flag) {
      this.Message = sb.toString();
      this.Response.setFailedMessage(sb.toString());
    }
    return flag;
  }

  public String getMessage() {
    return this.Message;
  }

  public RequestData getRequest() {
    return this.Request;
  }

  public void setRequest(RequestData request) {
    this.Request = request;
  }

  public ResponseData getResponse() {
    return this.Response;
  }

  public void setResponse(ResponseData response) {
    this.Response = response;
  }
}