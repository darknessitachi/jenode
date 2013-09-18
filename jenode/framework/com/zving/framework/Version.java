package com.zving.framework;

import java.io.PrintStream;

public class Version
{
  public static float VERSION2_0 = 2.0F;
  public static float VERSION2_1 = 2.1F;

  public static float getCurrentVersion() {
    return VERSION2_1;
  }

  public static void main(String[] args) {
    System.out.println(getCurrentVersion());
  }
}