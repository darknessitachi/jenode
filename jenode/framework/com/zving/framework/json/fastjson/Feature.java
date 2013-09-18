package com.zving.framework.json.fastjson;

public enum Feature
{
  AutoCloseSource, 

  AllowComment, 

  AllowUnQuotedFieldNames, 

  AllowSingleQuotes, 

  InternFieldNames, 

  AllowISO8601DateFormat, 

  AllowArbitraryCommas, 

  UseBigDecimal, 

  IgnoreNotMatch, 

  SortFeidFastMatch, 

  DisableASM;

  private final int mask;

  private Feature() { this.mask = (1 << ordinal()); }


  public final int getMask()
  {
    return this.mask;
  }

  public static boolean isEnabled(int features, Feature feature) {
    return (features & feature.getMask()) != 0;
  }

  public static int config(int features, Feature feature, boolean state) {
    if (state)
      features |= feature.getMask();
    else {
      features &= (feature.getMask() ^ 0xFFFFFFFF);
    }

    return features;
  }
}