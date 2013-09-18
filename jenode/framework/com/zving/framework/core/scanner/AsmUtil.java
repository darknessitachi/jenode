package com.zving.framework.core.scanner;

import com.zving.framework.core.asm.objectweb.tree.AnnotationNode;
import com.zving.framework.core.asm.objectweb.tree.ClassNode;
import com.zving.framework.core.asm.objectweb.tree.FieldNode;
import com.zving.framework.core.asm.objectweb.tree.MethodNode;
import java.util.List;

public class AsmUtil
{
  public static boolean isAnnotationPresent(MethodNode cn, String annotation)
  {
    if (cn.visibleAnnotations == null) {
      return false;
    }
    annotation = "L" + annotation + ";";
    for (AnnotationNode an : cn.visibleAnnotations) {
      if (annotation.equals(an.desc)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isAnnotationPresent(FieldNode cn, String annotation) {
    if (cn.visibleAnnotations == null) {
      return false;
    }
    annotation = "L" + annotation + ";";
    for (AnnotationNode an : cn.visibleAnnotations) {
      if (annotation.equals(an.desc)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isAnnotationPresent(ClassNode cn, String annotation) {
    if (cn.visibleAnnotations == null) {
      return false;
    }
    annotation = "L" + annotation + ";";
    for (AnnotationNode an : cn.visibleAnnotations) {
      if (annotation.equals(an.desc)) {
        return true;
      }
    }
    return false;
  }

  public static Object getAnnotationValue(ClassNode cn, String annotation, String prop) {
    if (cn.visibleAnnotations == null) {
      return null;
    }
    annotation = "L" + annotation + ";";
    for (AnnotationNode an : cn.visibleAnnotations) {
      if (annotation.equals(an.desc)) {
        for (int i = 0; i < an.values.size(); i += 2) {
          if (an.values.get(i).equals(prop)) {
            return an.values.get(i + 1);
          }
        }
        break;
      }
    }
    return null;
  }

  public static Object getAnnotationValue(FieldNode cn, String annotation, String prop) {
    if (cn.visibleAnnotations == null) {
      return null;
    }
    annotation = "L" + annotation + ";";
    for (AnnotationNode an : cn.visibleAnnotations) {
      if (annotation.equals(an.desc)) {
        for (int i = 0; i < an.values.size(); i += 2) {
          if (an.values.get(i).equals(prop)) {
            return an.values.get(i + 1);
          }
        }
        break;
      }
    }
    return null;
  }

  public static Object getAnnotationValue(MethodNode cn, String annotation, String prop) {
    if (cn.visibleAnnotations == null) {
      return null;
    }
    annotation = "L" + annotation + ";";
    for (AnnotationNode an : cn.visibleAnnotations) {
      if (annotation.equals(an.desc)) {
        for (int i = 0; i < an.values.size(); i += 2) {
          if (an.values.get(i).equals(prop)) {
            return an.values.get(i + 1);
          }
        }
        break;
      }
    }
    return null;
  }
}