package com.zving.framework.ui.control;

import com.zving.framework.Current;
import com.zving.framework.collection.Mapx;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.template.AbstractExecuteContext;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;
import com.zving.framework.template.exception.TemplateRuntimeException;
import com.zving.framework.ui.IPageEnableAction;
import com.zving.framework.ui.tag.ListAction;
import com.zving.framework.utility.HtmlUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.utility.StringFormat;
import com.zving.framework.utility.StringUtil;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PageBarTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private String target;
  private int type;
  private int total;
  private int pageIndex;
  private int pageSize;
  private boolean afloat;
  private boolean autoHide;
  protected IPageEnableAction action;

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName() {
    return "pagebar";
  }

  public int doStartTag() throws TemplateRuntimeException {
    this.action = ((IPageEnableAction)this.pageContext.getAttribute(this.target + "_ZVING_ACTION"));
    if (this.action == null) {
      return 0;
    }
    this.total = this.action.getTotal();
    this.pageIndex = this.action.getPageIndex();
    this.pageSize = this.action.getPageSize();
    Current.put("Page.Total", Integer.valueOf(this.total));
    Current.put("Page.Index", Integer.valueOf(this.pageIndex));
    Current.put("Page.Size", Integer.valueOf(this.pageSize));
    return 2;
  }

  public int doEndTag() throws TemplateRuntimeException {
    try {
      String html = "";
      if ((this.total != 0) || (!this.autoHide)) {
        html = getBody().trim();
        if (ObjectUtil.empty(html)) {
          html = getPageBarHtml(this.target, this.type, this.total, this.pageSize, this.pageIndex);
          if ((this.action instanceof DataListAction)) {
            html = html + "<script>Zving.DataGrid.setParam('" + this.target + "','PageBarType'," + this.type + ");";
            if (this.afloat) {
              html = html + "new Zving.Afloat(Zving.getDom('_PageBar_" + this.target + "').parentNode);";
            }
            html = html + "</script>";
          }
        } else {
          html = HtmlUtil.replacePlaceHolder(html, this.pageContext, true, false);
        }
      }
      this.pageContext.getOut().print(html);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 6;
  }

  public String getPageBarHtml(String target, int type, int total, int pageSize, int pageIndex) {
    return getDefault(target, type, total, pageSize, pageIndex);
  }

  private String getDefault(String target, int type, int total, int pageSize, int pageIndex) {
    String first = LangMapping.get("Framework.DataGrid.FirstPage");
    String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
    String next = LangMapping.get("Framework.DataGrid.NextPage");
    String last = LangMapping.get("Framework.DataGrid.LastPage");
    String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
    String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
    String error = LangMapping.get("Framework.DataGrid.ErrorPage");
    String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
    String pagebar = LangMapping.get("Framework.DataGrid.PageBar");
    String records = LangMapping.get("Framework.DataGrid.Records");

    StringBuilder sb = new StringBuilder();
    int totalPages = new Double(Math.ceil(total * 1.0D / pageSize)).intValue();
    sb.append("<div id='_PageBar_").append(target).append("' class=\"pagebar_wrap\" pagebartype=\"" + type + "\">");
    sb.append("<div style=\"float:right;\" class=\"tfoot-fr\"><div class=\"pagebar\">&nbsp;");
    String queryString = null;
    if ((this.action instanceof ListAction)) {
      queryString = ((ListAction)this.action).getQueryString();
      if (StringUtil.isEmpty(queryString)) {
        queryString = "?PageIndex=";
      } else {
        Mapx map = StringUtil.splitToMapx(queryString, "&", "=");
        map.remove("PageIndex");
        if (map.size() == 0)
          queryString = "?PageIndex=";
        else {
          queryString = ServletUtil.getQueryStringFromMap(map) + "&PageIndex=";
        }
      }
    }
    if (pageIndex > 0) {
      if ((this.action instanceof DataListAction)) {
        sb.append("<span class=\"first\"><a href='javascript:void(0);' onclick=\"Zving.DataList.firstPage('").append(target)
          .append("');\">").append(first).append("</a></span>");
        sb.append("<span class=\"previous\"><a href='javascript:void(0);' onclick=\"Zving.DataList.previousPage('").append(target)
          .append("');\">").append(prev).append("</a></span>");
      } else {
        sb.append("<a href='").append(queryString).append("1'>").append(first).append("</a>&nbsp;|&nbsp;");
        sb.append("<a href='").append(queryString).append(pageIndex).append("'>").append(prev).append("</a>&nbsp;|&nbsp;");
      }
    } else {
      sb.append("<span class=\"first\">").append(first).append("</span>");
      sb.append("<span class=\"previous\">").append(prev).append("</span>");
    }
    if ((totalPages != 0) && (pageIndex + 1 != totalPages)) {
      if ((this.action instanceof DataListAction)) {
        sb.append("<span class=\"next\"><a href='javascript:void(0);' onclick=\"Zving.DataList.nextPage('").append(target)
          .append("');\">").append(next).append("</a></span>");
        sb.append("<span class=\"last\"><a href='javascript:void(0);' onclick=\"Zving.DataList.lastPage('").append(target)
          .append("');\">").append(last).append("</a></span>");
      } else {
        sb.append("<a href='").append(queryString).append(pageIndex + 2).append("'>").append(next).append("</a>&nbsp;|&nbsp;");
        sb.append("<a href='").append(queryString).append(totalPages).append("'>").append(last).append("</a>&nbsp;|&nbsp;");
      }
    } else {
      sb.append("<span class=\"next\">").append(next).append("</span>");
      sb.append("<span class=\"last\">").append(last).append("</span>");
    }

    sb.append("&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(target)
      .append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1).append("\"");
    sb.append(
      "onkeyup=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Zving.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
      .append(target).append("').onclick();\">");
    sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
    sb.append(gotoEnd).append("&nbsp;");
    sb.append("<input type='button' id='_PageBar_JumpBtn_").append(target)
      .append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(target)
      .append("').value,t=Number(Zving.DataList.getParam('").append(target)
      .append("', Constant.PageTotal)),s=Number(Zving.DataList.getParam('").append(target)
      .append("', Constant.Size)),l=Math.floor(t/s)+(t%s===0?0:1);if(!/^\\d+$/.test(v)").append("||v<1||v>l){alert('")
      .append(error).append("');document.getElementById('_PageBar_Index_").append(target).append("').focus();}")
      .append("else{var pageIndex = v>0?v-1:0;");
    if ((this.action instanceof DataListAction))
      sb.append("Zving.DataList.setParam('").append(target).append("','").append("_ZVING_PAGEINDEX")
        .append("',pageIndex);Zving.DataList.loadData('").append(target).append("');");
    else {
      sb.append("window.location='").append(queryString).append("'+(pageIndex+1);");
    }
    sb.append("}\" value='").append(gotoButton).append("'>");

    sb.append("</div></div>");
    sb.append("<div style=\"float:left;\" class=\"tfoot-fl\">");
    sb.append("<span class=\"pageInfo_trigger\" style=\"display:none;\">");
    if (type == 2)
      sb.append("&gl;&gl;");
    else {
      sb.append(new StringFormat(records, new Object[] { "<span class=\"js_total\">" + total + "</span>" }));
    }
    sb.append("</span>");
    sb.append("<span class=\"pageInfo\">");
    sb.append(new StringFormat(pagebar, new Object[] { "<span class=\"js_total\">" + total + "</span>", "<span class=\"inline-block pageSizeGroup\"><ul class=\"sizeList\" style=\"position:absolute;\"><li onclick=\"Zving.DataList.changePageSize('" + 
      target + 
      "',10)\">10</li>" + "<li onclick=\"Zving.DataList.changePageSize('" + target + "',15)\">15</li>" + 
      "<li onclick=\"Zving.DataList.changePageSize('" + target + "',20)\">20</li>" + 
      "<li onclick=\"Zving.DataList.changePageSize('" + target + "',30)\">30</li>" + 
      "<li onclick=\"Zving.DataList.changePageSize('" + target + "',50)\">50</li>" + "</ul>" + "<span class=\"js_pageSize\">" + 
      pageSize + "</span>" + "</span>", "<span class=\"js_pageIndex\">" + (totalPages == 0 ? 0 : pageIndex + 1) + "</span>", 
      "<span class=\"js_totalPages\">" + totalPages + "</span>" }));
    sb.append("</span>");
    sb.append("</div><div style=\"clear:both;height:0;overflow:hidden;\"></div></div>");
    return sb.toString();
  }

  public String getTarget() {
    return this.target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public int getType() {
    return this.type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean isAfloat() {
    return this.afloat;
  }

  public void setAfloat(boolean afloat) {
    this.afloat = afloat;
  }

  public boolean isAutoHide() {
    return this.autoHide;
  }

  public void setAutoHide(boolean autoHide) {
    this.autoHide = autoHide;
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("afloat", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("autoHide", TagAttr.BOOL_OPTIONS));
    list.add(new TagAttr("pageIndex", 8));
    list.add(new TagAttr("pageSize", 8));
    list.add(new TagAttr("total", 8));
    list.add(new TagAttr("type", 8));
    list.add(new TagAttr("target"));
    return list;
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