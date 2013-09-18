package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearHtmlTag extends AbstractFunction
{
  public static String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
  public static String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
  public static String regEx_html = "<[^>]+>";

  public String getFunctionName() {
    return "clearHtmlTag";
  }

  public static void main(String[] args) {
    System.out.println(clearHtml("<p style=\"margin: 3\"><strong><span style=\"dispaly:none;\">&nbsp;</span>测试"));
  }

  private static String clearHtml(String htmlStr) {
    Pattern p_script = Pattern.compile(regEx_script, 2);
    Matcher m_script = p_script.matcher(htmlStr);
    htmlStr = m_script.replaceAll("");

    Pattern p_style = Pattern.compile(regEx_style, 2);
    Matcher m_style = p_style.matcher(htmlStr);
    htmlStr = m_style.replaceAll("");

    Pattern p_html = Pattern.compile(regEx_html, 2);
    Matcher m_html = p_html.matcher(htmlStr);
    htmlStr = m_html.replaceAll("");

    htmlStr = htmlStr.replaceAll("[\\s]{2,}", " ");
    htmlStr = htmlStr.trim();
    htmlStr = htmlStr.replaceAll("&nbsp;", " ")
      .replaceAll("&quot;", "\"")
      .replaceAll("&lt;", "<")
      .replaceAll("&gt;", ">")
      .replaceAll("&amp;", "&");

    return htmlStr;
  }

  public Object execute(IVariableResolver resolver, Object[] args) {
    String htmlStr = (String)args[0];
    if (htmlStr == null)
      return "";
    return clearHtml(htmlStr);
  }

  public String getFunctionPrefix() {
    return "";
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_String;
  }
}