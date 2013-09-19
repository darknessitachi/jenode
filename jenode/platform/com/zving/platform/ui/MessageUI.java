package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.User.UserData;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.commons.ArrayUtils;
import com.zving.platform.pub.MessageCache;
import com.zving.preloader.facade.HttpSessionListenerFacade;
import com.zving.schema.ZDMessage;
import javax.servlet.http.HttpSession;

@Alias("Message")
public class MessageUI extends UIFacade
{
  @Priv
  public void init()
  {
  }

  @Priv
  public void initDetailDialog()
  {
    String id = this.Request.getString("ID");
    String Type = this.Request.getString("Type");
    if (StringUtil.isEmpty(id)) {
      return;
    }
    ZDMessage m = new ZDMessage();
    m.setID(Long.parseLong(id));
    if (m.fill()) {
      this.Request.putAll(m.toMapx());
      if ("history".equals(Type)) {
        $S("UserType", "收");
        $S("FromUser", "");
      } else {
        $S("UserType", "发");
        $S("ToUser", "");

        if (m.getReadFlag() == 0L) {
          new Q("update ZDMessage set ReadFlag=1 where ID=?", new Object[] { Long.valueOf(Long.parseLong(id)) }).executeNoQuery();
          Q qb = new Q("select count(1) from ZDMessage where ReadFlag=0 and ToUser=?", new Object[] { User.getUserName() });
          CacheManager.set("Message", "Count", User.getUserName(), Integer.valueOf(qb.executeInt()));
        }
      }
    }
  }

  @Priv
  public void dg1DataBind(DataGridAction dga) {
    Q qb = new Q("select * from ZDMessage where touser=? and DelByToUser<>1", new Object[] { User.getUserName() });
    qb.append(dga.getSortString(), new Object[0]);
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    dt.insertColumn("ReadFlagIcon");
    dt.insertColumn("ReadFlagStr");
    dt.insertColumn("Color");
    for (DataRow dr : dt) {
      long flag = dr.getLong("ReadFlag");
      if (flag != 1L) {
        dr.set("ReadFlagIcon", "<img src='../../icons/icon037a7.png'>");
        dr.set("ReadFlagStr", Lang.get("Platform.Readed"));
        dr.set("Color", "red");
      } else {
        dr.set("ReadFlagIcon", "<img src='../../icons/icon037a17.png'>");
        dr.set("ReadFlagStr", Lang.get("Platform.Unread"));
        dr.set("Color", "");
      }
      if (StringUtil.isNotEmpty(dr.getString("RedirectURL"))) {
        dr.set("RedirectURL", "<a href=\"" + dr.getString("RedirectURL") + 
          "\" target=\"_blank\"><img src=\"../../icons/icon403a10.png\" width=\"20\" height=\"20\" /></a>");
      }
    }
    dga.setTotal(qb);
    dga.bindData(dt);
  }

  @Priv
  public void historyDataBind(DataGridAction dga)
  {
    Q qb = new Q("select * from ZDMessage where fromuser=? and DelByFromUser<>1 ", new Object[] { User.getUserName() });
    qb.append(dga.getSortString(), new Object[0]);
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    dt.insertColumn("ReadFlagIcon");
    dt.insertColumn("ReadFlagStr");
    dt.insertColumn("Color");
    for (DataRow dr : dt) {
      long flag = dr.getLong("ReadFlag");
      if (flag != 1L) {
        dr.set("ReadFlagIcon", "<img src='../../icons/icon037a7.png'>");
        dr.set("ReadFlagStr", Lang.get("Platform.Readed"));
        dr.set("Color", "red");
      } else {
        dr.set("ReadFlagIcon", "<img src='../../icons/icon037a17.png'>");
        dr.set("ReadFlagStr", Lang.get("Platform.Unread"));
        dr.set("Color", "");
      }
      if (StringUtil.isNotEmpty(dr.getString("RedirectURL"))) {
        dr.set("RedirectURL", "<a href=\"" + dr.getString("RedirectURL") + 
          "\" target=\"_blank\"><img src=\"../../icons/icon403a10.png\" width=\"20\" height=\"20\" /></a>");
      }
    }
    dga.setTotal(qb);
    dga.bindData(dt);
  }

  @Priv(login=false)
  public void getNewMessage()
  {
    if (!Config.isInstalled) {
      $S("_ZVING_SCRIPT", "window.location=\"" + Config.getContextPath() + "install.zhtml\";");
      return;
    }
    HttpSession session = HttpSessionListenerFacade.getSession($V("SessionID"));
    if (session == null) {
      $S("LogoutFlag", "Y");
      return;
    }
    try {
      User.setCurrent((User.UserData)session.getAttribute("_ZVING_USER"));
    } catch (Throwable t) {
      LogUtil.warn("Message.getNewMessage():" + t.getMessage());
      return;
    }
    $S("Count", Integer.valueOf(MessageCache.getNoReadCount()));
    String message = MessageCache.getFirstPopMessage();
    if (StringUtil.isEmpty(message)) {
      $S("PopFlag", "N");
    } else {
      $S("Message", message);
      $S("PopFlag", "Y");
    }
    ExtendManager.invoke("com.zving.platform.AfterGetNewMessage", new Object[] { this });
  }

  @Priv
  public void updateReadFlag()
  {
    long id = Long.parseLong($V("_Param0"));
    Q qb = new Q("update ZDMessage set ReadFlag=1 where ID=?", new Object[] { Long.valueOf(id) });
    qb.executeNoQuery();
    int count = ((Integer)CacheManager.get("Message", "Count", User.getUserName())).intValue();
    CacheManager.set("Message", "Count", User.getUserName(), Integer.valueOf(count - 1));

    DAOSet set = new ZDMessage().query(new Q("where ID=?", new Object[] { Long.valueOf(id) }));
    MessageCache.removeIDs(set);
  }

  @Priv
  public void add() {
    String[] userList = (String[])null;
    if (StringUtil.isNotEmpty($V("ToUser"))) {
      userList = $V("ToUser").split(",");
    }
    if (StringUtil.isNotEmpty($V("ToRole"))) {
      String[] roleList = $V("ToRole").split(",");
      if (roleList.length > 0) {
        String roleStr = "";
        for (int j = 0; j < roleList.length; j++) {
          if (StringUtil.isNotEmpty(roleList[j])) {
            if (j == 0)
              roleStr = roleStr + "'" + roleList[j] + "'";
            else {
              roleStr = roleStr + ",'" + roleList[j] + "'";
            }
          }
        }
        if (StringUtil.isNotEmpty(roleStr)) {
          DataTable dt = new Q("select UserName from zduserRole where rolecode in (" + roleStr + ")", new Object[0]).fetch();
          for (int k = 0; k < dt.getRowCount(); k++) {
            String userName = dt.getString(k, "UserName");
            if ((!User.getUserName().equals(userName)) && (!ArrayUtils.contains(userList, userName))) {
              userList = (String[])ArrayUtils.add(userList, userName);
            }
          }
        }
      }
    }
    if (MessageCache.addMessage($V("Subject"), $V("Content"), userList, User.getUserName()))
      success(Lang.get("Common.AddSuccess"));
    else
      fail(Lang.get("Common.AddFailed"));
  }

  @Priv
  public void reply()
  {
    String toUser = $V("ToUser");
    if (MessageCache.addMessage($V("Subject"), $V("Content"), toUser))
      success(Lang.get("Platform.ReplySuccess"));
    else
      fail(Lang.get("Common.ExecuteFailed"));
  }

  @Priv
  public Mapx<String, Object> replyInit()
  {
    String ID = $V("ID");
    DataTable dt = new Q("select * from ZDMessage where ID=?", new Object[] { ID }).fetch();
    this.Request.put("ToUser", dt.getString(0, "FromUser"));
    this.Request.put("Subject", Lang.get("Platform.Reply") + ":" + StringUtil.clearHtmlTag(dt.getString(0, "Subject")));
    return this.Request;
  }

  @Priv
  public void del() {
    String ids = $V("IDs");
    String userType = $V("UserType");
    Transaction trans = new Transaction();
    if (StringUtil.isEmpty(ids)) {
      fail(Lang.get("Common.InvalidID"));
    }
    if ((StringUtil.isEmpty(userType)) || ((!userType.equals("FromUser")) && (!userType.equals("ToUser")))) {
      fail(Lang.get("Common.ExecuteFailed"));
      return;
    }
    DAOSet set = new ZDMessage().query(new Q("where ID in (" + ids + ")", new Object[0]));
    if (set.size() > 0) {
      for (int i = 0; i < set.size(); i++) {
        if (userType.equals("FromUser")) {
          ((ZDMessage)set.get(i)).setDelByFromUser(1L);
        }
        if (userType.equals("ToUser")) {
          ((ZDMessage)set.get(i)).setDelByToUser(1L);
          ((ZDMessage)set.get(i)).setReadFlag(1L);
        }
      }
      trans.update(set);
      if (trans.commit()) {
        success(Lang.get("Common.DeleteSuccess"));
      } else {
        fail(Lang.get("Common.DeleteFailed"));
        return;
      }
    }
    set = new ZDMessage().query(new Q("where ID in (" + ids + ") and ((DelByFromUser=1 and DelByToUser=1) or FromUser='SYSTEM')", new Object[0]));
    if (set.size() > 0) {
      trans.deleteAndBackup(set);
      if (trans.commit()) {
        MessageCache.removeIDs(set);
        Q qb = new Q("select count(1) from ZDMessage where ReadFlag=0 and ToUser=?", new Object[] { User.getUserName() });
        CacheManager.set("Message", "Count", User.getUserName(), Integer.valueOf(qb.executeInt()));
      }
    }
  }

  @Priv
  public void setReadFlag() {
    String ids = $V("IDs");
    DAOSet set = new ZDMessage().query(new Q("where ReadFlag=0 and id in (" + ids + ")", new Object[0]));
    Q qb = new Q("update ZDMessage set ReadFlag=1 where id in (" + ids + ")", new Object[0]);
    qb.executeNoQuery();
    success(Lang.get("Platform.MarkSuccess"));
    MessageCache.removeIDs(set);
    qb = new Q("select count(1) from ZDMessage where ReadFlag=0 and ToUser=?", new Object[] { User.getUserName() });
    CacheManager.set("Message", "Count", User.getUserName(), Integer.valueOf(qb.executeInt()));
  }
}