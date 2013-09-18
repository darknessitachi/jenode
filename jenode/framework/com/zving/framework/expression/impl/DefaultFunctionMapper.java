package com.zving.framework.expression.impl;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.expression.core.IFunction;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.function.CharWidth;
import com.zving.framework.expression.function.ClearHtmlTag;
import com.zving.framework.expression.function.Contains;
import com.zving.framework.expression.function.ContainsIgnoreCase;
import com.zving.framework.expression.function.EndsWith;
import com.zving.framework.expression.function.EscapeXml;
import com.zving.framework.expression.function.Format;
import com.zving.framework.expression.function.IndexOf;
import com.zving.framework.expression.function.Join;
import com.zving.framework.expression.function.Length;
import com.zving.framework.expression.function.Replace;
import com.zving.framework.expression.function.Split;
import com.zving.framework.expression.function.StartsWith;
import com.zving.framework.expression.function.Substring;
import com.zving.framework.expression.function.SubstringAfter;
import com.zving.framework.expression.function.SubstringBefore;
import com.zving.framework.expression.function.ToLowerCase;
import com.zving.framework.expression.function.ToUpperCase;
import com.zving.framework.expression.function.Trim;

public class DefaultFunctionMapper
  implements IFunctionMapper
{
  private static DefaultFunctionMapper instance = new DefaultFunctionMapper();
  private CaseIgnoreMapx<String, CaseIgnoreMapx<String, IFunction>> all = new CaseIgnoreMapx();

  public static DefaultFunctionMapper getInstance() {
    return instance;
  }

  public DefaultFunctionMapper() {
    registerFunction(new CharWidth());
    registerFunction(new ClearHtmlTag());
    registerFunction(new Contains());
    registerFunction(new ContainsIgnoreCase());
    registerFunction(new EndsWith());
    registerFunction(new StartsWith());
    registerFunction(new EscapeXml());
    registerFunction(new Format());
    registerFunction(new IndexOf());
    registerFunction(new Join());
    registerFunction(new Length());
    registerFunction(new Replace());
    registerFunction(new Split());
    registerFunction(new Substring());
    registerFunction(new SubstringAfter());
    registerFunction(new SubstringBefore());
    registerFunction(new ToLowerCase());
    registerFunction(new ToUpperCase());
    registerFunction(new Trim());
  }

  public IFunction resolveFunction(String prefix, String name) {
    if (prefix == null) {
      prefix = "";
    }
    CaseIgnoreMapx map = (CaseIgnoreMapx)this.all.get(prefix);
    if (map == null) {
      return null;
    }
    return (IFunction)map.get(name);
  }

  public void registerFunction(IFunction f) {
    String prefix = f.getFunctionPrefix();
    if (prefix == null) {
      prefix = "";
    }
    CaseIgnoreMapx map = (CaseIgnoreMapx)this.all.get(prefix);
    if (map == null) {
      map = new CaseIgnoreMapx();
      this.all.put(prefix, map);
    }
    map.put(f.getFunctionName(), f);
  }
}