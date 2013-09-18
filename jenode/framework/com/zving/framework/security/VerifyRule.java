package com.zving.framework.security;

import com.zving.framework.utility.DateUtil;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyRule
{
  public static final String F_String = "String";
  public static final String F_Any = "Any";
  public static final String F_Number = "Number";
  public static final String F_Date = "Date";
  public static final String F_Time = "Time";
  public static final String F_DateTime = "DateTime";
  public static final String F_Year = "Year";
  public static final String F_Month = "Month";
  public static final String F_Day = "Day";
  public static final String F_Int = "Int";
  public static final String F_DigitChar = "DigitChar";
  public static final String F_AsciiChar = "AsciiChar";
  public static final String F_LetterChar = "LetterChar";
  public static final String F_UpperChar = "UpperChar";
  public static final String F_LowerChar = "LowerChar";
  public static final String F_NotNull = "NotNull";
  public static final String F_Email = "Email";
  public static final String F_Code = "Code";
  public static final String F_HalfChar = "HalfChar";
  public static final String F_FullChar = "FullChar";
  public static final String O_Add = "&&";
  public static final String O_Or = "||";
  public static final String O_Not = "!";
  public static final String A_Format = "Format";
  public static final String A_RegFormat = "RegFormat";
  public static final String A_Max = "Max";
  public static final String A_Min = "Min";
  public static final String A_Len = "Length";
  private static final String regEmail = "^[_\\-a-z0-9A-Z]*?[\\._\\-a-z0-9]*?[a-z0-9]+@[a-z0-9]+[a-z0-9\\-]*?[a-z0-9]+\\.[\\.a-z0-9]*$";
  private static Pattern patternEmail = null;
  private String Rule;
  private String[] Features;
  private ArrayList<String> Messages;

  public VerifyRule()
  {
  }

  public VerifyRule(String rule)
  {
    this.Rule = rule;
  }

  public boolean verify(String value)
  {
    this.Messages = new ArrayList();
    this.Features = this.Rule.split("\\&\\&");
    boolean sqlFlag = true;
    boolean verifyFlag = true;
    try {
      for (int i = 0; i < this.Features.length; i++) {
        String op = "=";
        if (this.Features[i].indexOf('>') > 0)
          op = ">";
        else if (this.Features[i].indexOf('<') > 0) {
          op = "<";
        }
        String[] f = this.Features[i].split("\\" + op);
        String fName = f[0];
        String fValue = null;
        if (f.length > 1) {
          fValue = f[1];
        }
        if (fName.equals("Any")) {
          sqlFlag = false;
        } else if (fName.equals("NotNull")) {
          if ((value == null) || (value.equals(""))) {
            this.Messages.add("Can't be empty");
            return false;
          }
        } else if (fName.equals("Code"))
        {
          if ((value == null) || (!value.equals("")));
        }
        else if (fName.equals("Date")) {
          if ((value != null) && (!value.equals("")))
          {
            if (!DateUtil.isDate(value)) {
              this.Messages.add("Invalid date");
              verifyFlag = false;
            }
          } } else if (fName.equals("Time")) {
          if ((value != null) && (!value.equals("")))
          {
            if (!DateUtil.isTime(value)) {
              this.Messages.add("Invalid time");
              verifyFlag = false;
            }
          } } else if (fName.equals("DateTime")) {
          if ((value != null) && (!value.equals("")))
          {
            String[] arr = value.split(" ");
            if ((arr.length == 1) && (!DateUtil.isDate(arr[0]))) {
              this.Messages.add("Invalid date");
              verifyFlag = false;
            } else if (arr.length == 2) {
              if ((!DateUtil.isDate(arr[0])) || (!DateUtil.isTime(arr[1]))) {
                this.Messages.add("Invalid datetime");
                verifyFlag = false;
              }
            } else {
              this.Messages.add("Invalid datetime");
              verifyFlag = false;
            }
          } } else if (fName.equals("Number")) {
          if ((value != null) && (!value.equals("")))
          {
            try
            {
              Double.parseDouble(value);
            } catch (Exception e) {
              this.Messages.add("Invalid number");
              verifyFlag = false;
            }
          } } else if (fName.equals("Int")) {
          if ((value != null) && (!value.equals("")))
          {
            try
            {
              Integer.parseInt(value);
            } catch (Exception e) {
              this.Messages.add("Invalid integer");
              verifyFlag = false;
            }
          } } else if (fName.equals("String")) {
          if ((value != null) && (!value.equals("")))
          {
            if ((value.indexOf('\'') >= 0) || (value.indexOf('"') >= 0)) {
              this.Messages.add("Illegal string");
              verifyFlag = false;
            }
          } } else if (fName.equals("Email")) {
          if ((value != null) && (!value.equals("")))
          {
            if (patternEmail == null) {
              patternEmail = Pattern.compile("^[_\\-a-z0-9A-Z]*?[\\._\\-a-z0-9]*?[a-z0-9]+@[a-z0-9]+[a-z0-9\\-]*?[a-z0-9]+\\.[\\.a-z0-9]*$");
            }
            Matcher m = patternEmail.matcher(value);
            if (!m.find()) {
              this.Messages.add("Invalid email address");
              verifyFlag = false;
            }
          } } else if ((fName.equals("Length")) && 
          (value != null) && (!value.equals("")))
        {
          if ((fValue == null) || (fValue.equals("")))
            throw new RuntimeException("Length must not be empty");
          try
          {
            int len = Integer.parseInt(fValue);
            if ((op.equals("=")) && (value.length() != len)) {
              this.Messages.add("Length must be " + len);
              verifyFlag = false;
            } else if ((op.equals(">")) && (value.length() <= len)) {
              this.Messages.add("Length must greater than" + len);
              verifyFlag = false;
            } else if ((op.equals("<")) && (value.length() >= len)) {
              this.Messages.add("Length must less than" + len);
              verifyFlag = false;
            }
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Length must be integer");
          }
        }
      }

      if ((sqlFlag) && 
        (value != null) && 
        ((value.indexOf(" and ") > 0) || (value.indexOf(" or ") > 0)) && (
        (value.indexOf('!') > 0) || (value.indexOf(" like ") > 0) || (value.indexOf('=') > 0) || 
        (value.indexOf('>') > 0) || (value.indexOf('<') > 0))) {
        this.Messages.add("Illegal string ,maybe is SQL Inject");
        verifyFlag = false;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException("Invalid verify rule:" + this.Rule);
    }
    if ((sqlFlag) && 
      (!checkSQL(value))) {
      verifyFlag = false;
    }

    return verifyFlag;
  }

  public String getMessages(String fieldName)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.Messages.size(); i++) {
      sb.append(fieldName);
      sb.append(":");
      sb.append((String)this.Messages.get(i));
      sb.append("\n");
    }
    return sb.toString();
  }

  private boolean checkSQL(String value)
  {
    return true;
  }

  protected String getRule()
  {
    return this.Rule;
  }

  protected void setRule(String rule)
  {
    this.Rule = rule;
  }

  public static void main(String[] args) {
    VerifyRule rule = new VerifyRule();
    rule.setRule("Email");
    System.out.println(rule.verify("wyuch_.-2@m165-a.com"));
    System.out.println(rule.getMessages("电子邮相"));
  }
}