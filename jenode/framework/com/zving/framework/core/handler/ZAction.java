package com.zving.framework.core.handler;

import com.zving.framework.CookieData;
import com.zving.framework.core.Dispatcher;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ZAction
{
  private StringBuilder sb;
  private boolean binaryMode;
  private CookieData cookies;
  private HttpServletRequest request;
  private HttpServletResponse response;

  public CookieData getCookies()
  {
    return this.cookies;
  }

  public void setCookies(CookieData cookies) {
    this.cookies = cookies;
  }

  public void forward(String forwardURL) {
    Dispatcher.forward(forwardURL);
  }

  public void redirect(String redirectURL) {
    Dispatcher.redirect(redirectURL);
  }

  public boolean isBinaryMode() {
    return this.binaryMode;
  }

  public void setBinaryMode(boolean flag) {
    this.binaryMode = flag;
  }

  public void writeHTML(String html) {
    if (!this.binaryMode) {
      if (this.sb == null) {
        this.sb = new StringBuilder();
      }
      this.sb.append(html);
    } else {
      throw new RuntimeException("Can't invoke writeHTML in binary mode!");
    }
  }

  public String getHTML() {
    return this.sb == null ? "" : this.sb.toString();
  }

  public void writeByte(byte[] arr) {
    if (this.binaryMode)
      try {
        this.response.getOutputStream().write(arr);
        this.response.getOutputStream().flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    else
      throw new RuntimeException("Can't invoke writeByte in html mode!");
  }

  public void setRequest(HttpServletRequest request)
  {
    this.request = request;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

  public HttpServletRequest getRequest() {
    return this.request;
  }

  public HttpServletResponse getResponse() {
    return this.response;
  }

  public void setSkipPage(boolean skip) {
    this.request.setAttribute("ZACTION_SKIPPAGE", String.valueOf(skip));
  }

  public void flushCookies() {
    this.cookies.write(this.request, this.response);
  }
}