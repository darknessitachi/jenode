package com.zving.framework.annotation.util;

import com.zving.framework.Config;
import com.zving.framework.ConfigLoader;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.asm.objectweb.tree.ClassNode;
import com.zving.framework.core.asm.objectweb.tree.MethodNode;
import com.zving.framework.core.method.UIMethod;
import com.zving.framework.core.scanner.AsmUtil;
import com.zving.framework.core.scanner.BuiltResource;
import com.zving.framework.core.scanner.BuiltResourceScanner;
import com.zving.framework.core.scanner.BuiltResourceVisitorService;
import com.zving.framework.core.scanner.IBuiltResourceVisitor;
import com.zving.framework.xml.XMLElement;
import java.util.List;

public class AnnotationVisitor
  implements IBuiltResourceVisitor
{
  protected static long lastTime = 0L;
  private static Object mutex = new Object();
  private static String UIFACADE;
  private static String ALIAS;
  private static String PRIV;
  private static String UIMETHOD;
  private static final String VALUE = "value";
  private static final String ALONE = "alone";
  private static final String SCHEMA = "com/zving/schema/";
  private static final String FRAMEWORK = "com/zving/framework/";

  public String getID()
  {
    return "com.zving.framework.annotation.AnnotationVisitor";
  }

  public String getName() {
    return "Annotation Visitor";
  }

  public boolean match(BuiltResource br) {
    String fullName = br.getFullName();
    if ((fullName.indexOf("com/zving/framework/") >= 0) || (fullName.indexOf("com/zving/schema/") >= 0)) {
      return false;
    }
    return (fullName.endsWith("UI.class")) || (fullName.endsWith("Method.class")) || (fullName.indexOf("UI$") > 0);
  }

  private static void initNames() {
    UIFACADE = UIFacade.class.getName().replace('.', '/');
    ALIAS = Alias.class.getName().replace('.', '/');
    PRIV = Priv.class.getName().replace('.', '/');
    UIMETHOD = UIMethod.class.getName().replace('.', '/');
  }

  public static void load() {
    if ((lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L)))
      synchronized (mutex) {
        if ((lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L))) {
          initNames();
          BuiltResourceScanner.scanAll(BuiltResourceVisitorService.getInstance().getAll(), null);
          ConfigLoader.reload();

          List<XMLElement> nds = ConfigLoader.getElements("*.mapping.method");
          for (XMLElement data : nds) {
            String id = (String)data.getAttributes().get("id");
            String value = (String)data.getAttributes().get("value");
            AliasMapping.put(id, value);
          }

          nds = ConfigLoader.getElements("*.mapping.sql");
          for (XMLElement data : nds) {
            String id = (String)data.getAttributes().get("id");
            String value = (String)data.getAttributes().get("value");
            SQLMapping.put(id, value);
          }
          lastTime = System.currentTimeMillis();
        }
      }
  }

  public void visitClass(BuiltResource br, ClassNode cn)
  {
    if ((cn.access & 0x400) == 1) {
      return;
    }
    if (cn.superName.equals(UIFACADE)) {
      String classAlias = (String)AsmUtil.getAnnotationValue(cn, ALIAS, "value");
      for (MethodNode mn : cn.methods)
        if ((mn.name != null) && (!mn.name.startsWith("<")))
        {
          if ((mn.access & 0x1) != 0)
          {
            if (AsmUtil.isAnnotationPresent(mn, PRIV))
            {
              boolean flag = AsmUtil.isAnnotationPresent(mn, ALIAS);
              if ((classAlias != null) && (!flag)) {
                AliasMapping.put(classAlias + "." + mn.name, cn.name + "#" + mn.name);
              }
              if (flag) {
                String methodAlias = (String)AsmUtil.getAnnotationValue(mn, ALIAS, "value");
                Boolean alone = (Boolean)AsmUtil.getAnnotationValue(mn, ALIAS, "alone");
                if (((alone == null) || (!alone.booleanValue())) && (classAlias != null)) {
                  methodAlias = classAlias + "." + methodAlias;
                }
                AliasMapping.put(methodAlias, cn.name + "#" + mn.name); } 
            }
          }
        } } else if ((cn.superName.equals(UIMETHOD)) && 
      (AsmUtil.isAnnotationPresent(cn, ALIAS)) && (AsmUtil.isAnnotationPresent(cn, PRIV))) {
      AliasMapping.put(AsmUtil.getAnnotationValue(cn, ALIAS, "value").toString(), cn.name);
    }
  }

  public void visitResource(BuiltResource br)
  {
  }

  public void visitInnerClass(BuiltResource br, ClassNode cn, ClassNode icn)
  {
    if ((icn.access & 0x1) == 0) {
      return;
    }
    if (!icn.name.startsWith(cn.name)) {
      return;
    }
    if (!AsmUtil.isAnnotationPresent(icn, PRIV)) {
      return;
    }
    if (!icn.superName.equals(UIMETHOD)) {
      return;
    }
    boolean flag = AsmUtil.isAnnotationPresent(icn, ALIAS);
    String classAlias = (String)AsmUtil.getAnnotationValue(cn, ALIAS, "value");
    if ((classAlias != null) && (!flag)) {
      String name = icn.name;
      int index = name.indexOf('/');
      if (index > 0) {
        name = name.substring(index + 1);
      }
      AliasMapping.put(classAlias + "." + name, icn.name);
    }
    if (flag) {
      String innerAlias = (String)AsmUtil.getAnnotationValue(icn, ALIAS, "value");
      Boolean innerAlone = (Boolean)AsmUtil.getAnnotationValue(icn, ALIAS, "alone");
      if (((innerAlone == null) || (!innerAlone.booleanValue())) && (classAlias != null)) {
        innerAlias = classAlias + "." + innerAlias;
      }
      AliasMapping.put(innerAlias, icn.name);
    }
  }
}