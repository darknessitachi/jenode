package com.zving.framework.security;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.Member;
import com.zving.framework.User;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.Priv.LoginType;
import com.zving.framework.core.FrameworkException;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.extend.action.PrivExtendAction;
import com.zving.framework.security.exception.MemberNotLoginException;
import com.zving.framework.security.exception.NoPrivException;
import com.zving.framework.security.exception.UserNotLoginException;
import com.zving.framework.ui.util.PlaceHolderUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;

public class PrivCheck
{
  public static void check(IMethodLocator m)
  {
    if (m == null) {
      return;
    }

    boolean flag = m.isAnnotationPresent(Priv.class);
    if (flag) {
      Priv priv = (Priv)m.getAnnotation(Priv.class);
      check(priv.login(), priv.loginType(), priv.userType(), priv.value(), Current.getVariableResolver());
    } else {
      throw new NoPrivException("Method hasn't @Priv annotation");
    }
  }

  public static void check(boolean login, Priv.LoginType loginType, String userType, String priv, IVariableResolver context) {
    if (!login) {
      return;
    }
    if (login) {
      if ((Config.isFrontDeploy()) && (User.isLogin())) {
        throw new FrameworkException("User cann't login in Front Deploy mode!");
      }
      if ((loginType == Priv.LoginType.User) && (!User.isLogin())) {
        throw new UserNotLoginException("User isn't logined");
      }
      if ((loginType == Priv.LoginType.Member) && (!Member.isLogin())) {
        throw new MemberNotLoginException("Member isn't logined");
      }

      if (ObjectUtil.notEmpty(userType)) {
        if ((loginType == Priv.LoginType.User) && (ObjectUtil.notEqual(User.getType(), userType))) {
          throw new NoPrivException("User.Type is not applicable:" + userType);
        }

        if ((loginType == Priv.LoginType.Member) && (ObjectUtil.notEqual(Member.getType(), userType))) {
          throw new NoPrivException("Member.Type is not applicable:" + userType);
        }
      }
      if (!check(context, priv))
        throw new NoPrivException("Privilege " + priv + " is not owned by current user.");
    }
  }

  public static boolean check(String priv)
  {
    return check(Current.getVariableResolver(), priv);
  }

  public static void assertPriv(String priv) {
    assertPriv(priv, "Privilege " + priv + " is not owned by current user.");
  }

  public static void assertPriv(String priv, String errorMessage) {
    if (!check(priv))
      throw new NoPrivException(errorMessage);
  }

  public static boolean check(IVariableResolver context, String priv)
  {
    if (ObjectUtil.empty(priv)) {
      return true;
    }
    for (String item : StringUtil.splitEx(priv, "||")) {
      item = item.trim();
      if (!ObjectUtil.empty(item))
      {
        if ((context != null) && (item.indexOf("${") >= 0)) {
          try {
            item = PlaceHolderUtil.replacePlaceHolder(item, context, false, false);
          } catch (ExpressionException e) {
            e.printStackTrace();
            return false;
          }
        }
        Object[] arr = ExtendManager.invoke(PrivExtendAction.ExtendPointID, new Object[] { item });
        boolean allowFlag = false;
        int itemFlag = 0;
        if (arr != null) {
          for (Object obj : arr) {
            Integer flag = (Integer)obj;
            if (flag.intValue() == -1) {
              itemFlag = flag.intValue();
              break;
            }
            if (flag.intValue() == 1) {
              allowFlag = true;
            }
          }
        }
        if ((itemFlag != -1) && (allowFlag))
          return true;
      }
    }
    return false;
  }
}