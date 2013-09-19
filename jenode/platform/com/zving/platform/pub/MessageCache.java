package com.zving.platform.pub;

import com.zving.framework.User;
import com.zving.framework.cache.CacheDataProvider;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Executor;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.StringFormat;
import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMessage;
import java.util.ArrayList;
import java.util.Date;

public class MessageCache extends CacheDataProvider
{
  public static final String ProviderID = "Message";

  public static boolean addMessage(String subject, String content, String toUser)
  {
    Transaction tran = new Transaction();
    addMessage(tran, subject, content, new String[] { toUser }, User.getUserName(), "", true);
    return tran.commit();
  }

  public static boolean addMessage(Transaction tran, String subject, String content, String toUser) {
    return addMessage(tran, subject, content, new String[] { toUser }, User.getUserName(), "", true);
  }

  public static boolean addMessage(Transaction tran, String subject, String content, String toUser, String formUser, String RedirectURL) {
    return addMessage(tran, subject, content, new String[] { toUser }, formUser, RedirectURL, true);
  }

  public static boolean addMessage(String subject, String content, String[] userList, String fromUser) {
    Transaction tran = new Transaction();
    addMessage(tran, subject, content, userList, fromUser, "", true);
    return tran.commit();
  }

  public static boolean addMessage(String subject, String content, String[] userList, String fromUser, String RedirectURL)
  {
    Transaction tran = new Transaction();
    addMessage(tran, subject, content, userList, fromUser, RedirectURL, true);
    return tran.commit();
  }

  public static boolean addMessage(Transaction tran, String subject, String content, String[] userList, String fromUser, String RedirectURL, final boolean popFlag)
  {
    ArrayList list = new ArrayList();
    for (int i = 0; i < userList.length; i++) {
      if ((!userList[i].equals(fromUser)) && (StringUtil.isNotEmpty(userList[i]))) {
        ZDMessage message = new ZDMessage();
        message.setID(NoUtil.getMaxID("MessageID"));
        message.setSubject(subject);
        message.setBox("outbox");
        message.setContent(content);
        message.setFromUser(fromUser);
        message.setToUser(userList[i]);
        message.setReadFlag(0L);
        message.setDelByFromUser(0L);
        message.setDelByToUser(0L);
        message.setPopFlag(popFlag ? 0 : 1);
        message.setAddTime(new Date());
        message.setRedirectURL(RedirectURL);
        PopMessage pm = new PopMessage(message.getID(), getHtmlMessage(message.getID(), message.getSubject(), content), 
          System.currentTimeMillis(), userList[i]);
        list.add(pm);
        tran.add(message, 1);
      }
    }
    tran.addExecutor(new Executor(new Object[] { list })
    {
      public boolean execute() {
        ArrayList list = (ArrayList)this.params[0];
        for (int i = 0; i < list.size(); i++) {
          MessageCache.PopMessage pm = (MessageCache.PopMessage)list.get(i);
          MessageCache.PopMessageList pml = (MessageCache.PopMessageList)CacheManager.get("Message", "LastMessage", pm.ToUser);
          synchronized (pml) {
            if (popFlag) {
              MessageCache.PopMessageList.access$0(pml).add(pm);
            }
          }
          String count = String.valueOf(CacheManager.get("Message", "Count", pm.ToUser));
          CacheManager.set("Message", "Count", pm.ToUser, Integer.valueOf(Integer.parseInt(count) + 1));
        }
        return true;
      }
    });
    return true;
  }

  public static void removeIDs(DAOSet<ZDMessage> set) {
    PopMessageList pml = (PopMessageList)CacheManager.get("Message", "LastMessage", User.getUserName());
    int NoReadCount = 0;
    synchronized (pml) {
      for (int j = 0; j < set.size(); j++) {
        for (int i = 0; i < pml.list.size(); i++) {
          PopMessage pm = (PopMessage)pml.list.get(i);
          if (((ZDMessage)set.get(j)).getID() == pm.ID) {
            pml.list.remove(pm);
            break;
          }
        }
        if (((ZDMessage)set.get(j)).getReadFlag() == 0L) {
          NoReadCount++;
        }
      }
    }

    String count = String.valueOf(CacheManager.get("Message", "Count", User.getUserName()));
    CacheManager.set("Message", "Count", User.getUserName(), Integer.valueOf(Integer.parseInt(count) - NoReadCount));
  }

  public static int getNoReadCount() {
    String count = CacheManager.get("Message", "Count", User.getUserName());
    if (StringUtil.isNull(count)) {
      return 0;
    }
    int c = Integer.parseInt(count);
    if (c < 0) {
      CacheManager.set("Message", "Count", User.getUserName(), "0");
      return 0;
    }
    return c;
  }

  public static String getFirstPopMessage()
  {
    return getFirstPopMessage(User.getUserName());
  }

  public static String getFirstPopMessage(String userName)
  {
    PopMessageList pml = (PopMessageList)CacheManager.get("Message", "LastMessage", userName);
    if (pml == null) {
      return "";
    }
    return pml.getLastMessage();
  }

  public String getID() {
    return "Message";
  }

  public String getName() {
    return "@{Platform.MessageCache}";
  }

  public void onKeyNotFound(String type, String key) {
    if (type.equals("Count")) {
      Q qb = new Q("select count(1) from ZDMessage where ReadFlag=0 and ToUser=?", new Object[] { key });
      int count = qb.executeInt();
      CacheManager.set("Message", type, key, Integer.valueOf(count));
    }
    if (type.equals("LastMessage")) {
      PopMessageList pml = new PopMessageList(key.toString());
      CacheManager.set("Message", type, key, pml);
    }
  }

  public void onTypeNotFound(String type)
  {
    CacheManager.setMapx("Message", type, new Mapx());
  }

  public static String getHtmlMessage(long id, String subject, String content)
  {
    StringFormat sf = new StringFormat(Lang.get("Platform.Message.GetAMessage") + "：<hr>?<hr>?<br><br>?");
    sf.add(subject);
    sf.add(content);
    sf.add("<p align='center' width='100%'><input type='button' class='inputButton' value='" + 
      Lang.get("Platform.MessagePopButton") + "'" + " onclick=\"Server.getOneValue('Message.updateReadFlag'," + id + 
      ",function(){MsgPop.closeSelf();});\"></p>");
    return sf.toString();
  }

  static class PopMessage
  {
    public long ID;
    public String Message;
    public long LastTime;
    public boolean PopedFlag = false;
    public String ToUser;
    public Mapx<String, String> SessionIDMap = new Mapx();

    public PopMessage(long id, String message, long lastTime, String toUser) {
      this.ID = id;
      this.Message = message;
      this.LastTime = lastTime;
      this.ToUser = toUser;
    }
  }

  static class PopMessageList
  {
    private ArrayList<MessageCache.PopMessage> list = new ArrayList();
    public static final int Interval = 10000;

    public PopMessageList(String userName)
    {
      long current = System.currentTimeMillis();
      Q qb = new Q("select * from ZDMessage where ReadFlag=0 and PopFlag=0 and ToUser=? order by AddTime asc", new Object[] { 
        userName });
      DataTable dt = qb.fetch();
      for (int i = 0; i < dt.getRowCount(); i++) {
        String html = MessageCache.getHtmlMessage(dt.getLong(i, "ID"), dt.getString(i, "Subject"), dt.getString(i, "Content"));
        this.list.add(new MessageCache.PopMessage(dt.getLong(i, "ID"), html, current, userName));
      }
    }

    public synchronized String getLastMessage() {
      if (this.list.size() == 0) {
        return null;
      }
      for (int i = this.list.size() - 1; i >= 0; i--) {
        MessageCache.PopMessage pm = (MessageCache.PopMessage)this.list.get(i);
        if (System.currentTimeMillis() - pm.LastTime > 1800000L) {
          this.list.remove(pm);
        }
        if (!pm.SessionIDMap.containsKey(User.getSessionID()))
        {
          pm.SessionIDMap.put(User.getSessionID(), "1");
          if (!pm.PopedFlag) {
            Q qb = new Q("update ZDMessage set PopFlag=1 where ID=?", new Object[] { Long.valueOf(pm.ID) });
            qb.executeNoQuery();
            pm.PopedFlag = true;
          }
          return pm.Message;
        }
      }
      return null;
    }
  }
}