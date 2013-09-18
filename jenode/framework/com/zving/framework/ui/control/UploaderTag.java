package com.zving.framework.ui.control;

import com.zving.framework.Config;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.zhtml.HttpExecuteContext;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UploaderTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String id;
  private String name;
  private String barColor;
  private int width;
  private int height;
  private String allowType;
  private int fileCount;
  private int fileMaxSize;
  private String fileName;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "uploader";
  }

  public int doAfterBody() throws TemplateRuntimeException {
    String content = getBody();
    try {
      getPreviousOut().print(getHtml(content));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public int doEndTag() throws TemplateRuntimeException {
    if (StringUtil.isEmpty(getBody())) {
      this.pageContext.getOut().print(getHtml(""));
    }
    return 6;
  }

  public String getHtml(String content)
  {
    String FlashVars = "";
    String srcSWF = Config.getContextPath() + "framework/components/ZUploader2.swf";
    if (StringUtil.isEmpty(this.id)) {
      this.id = "_ZVING_NOID_";
    }
    if (StringUtil.isEmpty(this.name)) {
      this.name = this.id;
    }
    if (StringUtil.isNotEmpty(this.allowType)) {
      FlashVars = FlashVars + "fileType=" + this.allowType;
    }
    if (StringUtil.isNotEmpty(this.fileName)) {
      FlashVars = FlashVars + "&fileName=" + StringUtil.htmlEncode(this.fileName);
    }
    if (StringUtil.isNotEmpty(this.barColor)) {
      FlashVars = FlashVars + "&barColor=" + this.barColor;
    }
    if (this.fileCount != 0) {
      FlashVars = FlashVars + "&fileCount=" + this.fileCount;
    }
    if (this.fileMaxSize != 0) {
      FlashVars = FlashVars + "&fileMaxSize=" + this.fileMaxSize;
    }
    if (this.width == 0) {
      this.width = 250;
    }
    if (this.height == 0) {
      this.height = 25;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<object id='" + this.id + "' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' width='" + this.width + "' height='" + this.height + 
      "' style='vertical-align:middle;'>\n");
    sb.append("<param name='movie' value='" + srcSWF + "'>\n");
    sb.append("<param name='quality' value='high'>\n");
    sb.append("<param name='wmode' value='transparent'>\n");
    sb.append("<param name='FlashVars' value='" + FlashVars + "'>\n");
    sb.append("<embed name='" + this.name + "' src='" + srcSWF + "' type='application/x-shockwave-flash' FlashVars='" + FlashVars + 
      "' quality='high' wmode='transparent' width='" + this.width + "' height='" + this.height + "'></embed>\n");
    sb.append("</object>\n");

    sb.append("<script type='text/javascript'>");
    String sessionID = "";
    if ((this.pageContext instanceof HttpExecuteContext)) {
      sessionID = ((HttpExecuteContext)this.pageContext).getRequest().getSession().getId();
    }
    sb.append("var _ZUploaderSessionID='" + sessionID + "';Zving.Uploader.checkVersion();</script>\n");
    return sb.toString();
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBarColor() {
    return this.barColor;
  }

  public void setBarColor(String barColor) {
    this.barColor = barColor;
  }

  public String getAllowType() {
    return this.allowType;
  }

  public void setAllowType(String allowType) {
    this.allowType = allowType;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("id", true));
    list.add(new TagAttr("allowType"));
    list.add(new TagAttr("barcolor"));
    list.add(new TagAttr("fileCount", 8));
    list.add(new TagAttr("height", 8));
    list.add(new TagAttr("width", 8));
    list.add(new TagAttr("fileMaxSize", 8));
    list.add(new TagAttr("fileName"));
    list.add(new TagAttr("name"));
    return list;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getFileCount() {
    return this.fileCount;
  }

  public void setFileCount(int fileCount) {
    this.fileCount = fileCount;
  }

  public int getFileMaxSize() {
    return this.fileMaxSize;
  }

  public void setFileMaxSize(int fileMaxSize) {
    this.fileMaxSize = fileMaxSize;
  }

  public String getName()
  {
    return "<" + getPrefix() + ":" + getTagName() + ">";
  }

  public String getDescription()
  {
    return "";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}