package com.zving.framework.json.fastjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class ParserConfig
{
  private final Set<Class<?>> primitiveClasses = new HashSet();

  private static ParserConfig global = new ParserConfig();

  protected final SymbolTable symbolTable = new SymbolTable();

  public static ParserConfig getGlobalInstance()
  {
    return global;
  }

  public ParserConfig()
  {
    this.primitiveClasses.add(Boolean.TYPE);
    this.primitiveClasses.add(Boolean.class);

    this.primitiveClasses.add(Character.TYPE);
    this.primitiveClasses.add(Character.class);

    this.primitiveClasses.add(Byte.TYPE);
    this.primitiveClasses.add(Byte.class);

    this.primitiveClasses.add(Short.TYPE);
    this.primitiveClasses.add(Short.class);

    this.primitiveClasses.add(Integer.TYPE);
    this.primitiveClasses.add(Integer.class);

    this.primitiveClasses.add(Long.TYPE);
    this.primitiveClasses.add(Long.class);

    this.primitiveClasses.add(Float.TYPE);
    this.primitiveClasses.add(Float.class);

    this.primitiveClasses.add(Double.TYPE);
    this.primitiveClasses.add(Double.class);

    this.primitiveClasses.add(BigInteger.class);
    this.primitiveClasses.add(BigDecimal.class);

    this.primitiveClasses.add(String.class);
    this.primitiveClasses.add(java.util.Date.class);
    this.primitiveClasses.add(java.sql.Date.class);
    this.primitiveClasses.add(Time.class);
    this.primitiveClasses.add(Timestamp.class);
  }

  public SymbolTable getSymbolTable() {
    return this.symbolTable;
  }

  public boolean isPrimitive(Class<?> clazz) {
    return this.primitiveClasses.contains(clazz);
  }
}