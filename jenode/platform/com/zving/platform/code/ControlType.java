package com.zving.platform.code;

import com.zving.platform.FixedCodeType;

public class ControlType extends FixedCodeType
{
  public static final String Text = "Text";
  public static final String Selector = "Select";
  public static final String TextArea = "TextArea";
  public static final String Radio = "Radio";
  public static final String Checkbox = "Checkbox";
  public static final String DateSelector = "Date";
  public static final String DateTimeSelector = "DateTime";
  public static final String TimeSelector = "Time";

  public ControlType()
  {
    super("ControlType", "@{Metadata.ControlType}", true, false);
    addFixedItem("Checkbox", "@{Platform.CheckBox}", null);
    addFixedItem("Date", "@{Platform.DataSellection}", null);
    addFixedItem("DateTime", "@{Platform.DateTimeSelection}", null);
    addFixedItem("Radio", "@{Platform.Radio}", null);
    addFixedItem("Select", "@{Platform.Select}", null);
    addFixedItem("Text", "@{Platform.Input}", null);
    addFixedItem("TextArea", "@{Platform.Textarea}", null);
  }

  public boolean isText(String type) {
    return "Text".equals(type);
  }

  public boolean isSelector(String type) {
    return "Select".equals(type);
  }

  public boolean isTextArea(String type) {
    return "TextArea".equals(type);
  }

  public boolean isRadio(String type) {
    return "Radio".equals(type);
  }

  public boolean isCheckbox(String type) {
    return "Checkbox".equals(type);
  }

  public boolean isDateSelector(String type) {
    return "Date".equals(type);
  }

  public boolean isDateTimeSelector(String type) {
    return "DateTime".equals(type);
  }
}