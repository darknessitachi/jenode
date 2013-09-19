package com.zving.platform.pub;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

public class Mail
{
  public static final String SUCCESS = "success";
  public static final String FAILED_SEND = "failed_send";
  public static final String FAILED_WRONG = "failed_wrong";
  public static final String FAILED_WRONG_HOST = "failed_wrong_host";
  public static final String FAILED_WRONG_NOATTACH = "failed_wrong_noattach";
  public static final String FAILED_EMPTY_TOUSER = "failed_empty_user";
  public static final String FAILED_EMPTY_CONTENT = "failed_empty_content";
  public static final String FAILED_EMPTY_URL = "failed_empty_url";
  public static final Mapx<String, String> RESULT_MAP = new Mapx();

  static {
    RESULT_MAP.put("success", "发送成功");
    RESULT_MAP.put("failed_send", "发送失败");
    RESULT_MAP.put("failed_wrong", "传值错误");
    RESULT_MAP.put("failed_wrong_host", "邮件服务器主机地址有误");
    RESULT_MAP.put("failed_wrong_noattach", "附件不存在");
    RESULT_MAP.put("failed_empty_user", "邮件接收者为空");
    RESULT_MAP.put("failed_empty_content", "邮件内容为空");
    RESULT_MAP.put("failed_empty_url", "URL为空");
  }

  public static String sendSimpleEmail(Mapx<String, String> map) {
    if (map == null) {
      return "failed_wrong";
    }
    String host = map.getString("mail.host");
    String port = map.getString("mail.port");
    String userName = map.getString("mail.username");
    String password = map.getString("mail.password");
    if (StringUtil.isEmpty(map.getString("ToUser")))
      return "failed_empty_user";
    if (StringUtil.isEmpty(map.getString("Content"))) {
      return "failed_empty_content";
    }

    String realName = map.getString("RealName");
    if (StringUtil.isEmpty(realName)) {
      realName = map.getString("ToUser");
    }

    String subject = map.getString("Subject");
    if (StringUtil.isEmpty(subject)) {
      subject = "来自" + realName + "的系统邮件";
    }

    SimpleEmail email = new SimpleEmail();
    try {
      email.setAuthentication(userName, password);
      email.setHostName(host);
      email.setSmtpPort(Integer.parseInt(port));
      email.addTo(map.getString("ToUser"), realName);
      email.setFrom(userName);
      email.setSubject(subject);
      email.setContent(map.getString("Content"), "text/html;charset=" + Config.getGlobalCharset());
      email.send();
    } catch (EmailException e) {
      e.printStackTrace();
      return "failed_send";
    }
    return "success";
  }

  public static String sendHtmlMail(Mapx<String, String> map) {
    if (map == null) {
      return "failed_wrong";
    }
    String host = map.getString("mail.host");
    String port = map.getString("mail.port");
    String userName = map.getString("mail.username");
    String password = map.getString("mail.password");
    if (StringUtil.isEmpty(map.getString("ToUser")))
      return "failed_empty_user";
    if (StringUtil.isEmpty(map.getString("URL"))) {
      return "failed_empty_url";
    }

    String realName = map.getString("RealName");
    if (StringUtil.isEmpty(realName)) {
      realName = map.getString("ToUser");
    }

    String subject = map.getString("Subject");
    if (StringUtil.isEmpty(subject)) {
      subject = "来自" + realName + "的系统邮件";
    }

    String htmlContent = FileUtil.readURLText(map.getString("URL"));
    HtmlEmail email = new HtmlEmail();
    try {
      email.setAuthentication(userName, password);
      email.addTo(map.getString("ToUser"), realName);
      email.setHostName(host);
      email.setSmtpPort(Integer.parseInt(port));
      email.setFrom(userName);
      email.setSubject(subject);
      email.setHtmlMsg(htmlContent);
      email.send();
    } catch (EmailException e) {
      return "failed_send";
    }
    return "success";
  }

  public String sendMailWithAttach(Mapx<String, String> map) {
    if (map == null) {
      return "failed_wrong";
    }
    Properties props = new Properties();
    String host = map.getString("mail.host");
    String port = map.getString("mail.port");
    String userName = map.getString("mail.username");
    String password = map.getString("mail.password");

    if (StringUtil.isEmpty(map.getString("ToUser"))) {
      return "failed_empty_user";
    }

    String realName = map.getString("RealName");
    if (StringUtil.isEmpty(realName)) {
      realName = map.getString("ToUser");
    }

    String subject = map.getString("Subject");
    if (StringUtil.isEmpty(subject)) {
      subject = "来自" + realName + "的系统邮件";
    }

    String content = map.getString("Content");
    if (StringUtil.isEmpty(content)) {
      content = "您好！";
    }

    String attachPath = map.getString("AttachPath");
    String attachName = map.getString("AttachName");
    String toUser = map.getString("ToUser");

    props = System.getProperties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);
    props.put("mail.smtp.auth", "true");

    MailAutherticator myEmailAuther = new MailAutherticator(userName, password);

    Session mailSession = Session.getInstance(props, myEmailAuther);
    try
    {
      Transport transport = mailSession.getTransport("smtp");

      MimeMessage mimeMsg = new MimeMessage(mailSession);

      if (StringUtil.isNotEmpty(userName)) {
        InternetAddress sentFrom = new InternetAddress(userName);
        mimeMsg.setFrom(sentFrom);
      }

      InternetAddress[] sendTo = new InternetAddress[1];
      sendTo[0] = new InternetAddress(toUser);
      mimeMsg.setRecipients(MimeMessage.RecipientType.TO, sendTo);
      mimeMsg.setSubject(subject, Config.getGlobalCharset());

      MimeBodyPart messageBodyPart1 = new MimeBodyPart();
      messageBodyPart1.setContent(content, "text/html;charset=" + Config.getGlobalCharset());

      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart1);

      String[] attachs = StringUtil.splitEx(attachPath, ",");
      String[] attachNames = StringUtil.splitEx(attachName, ",");
      boolean wrongFlag = false;
      for (int i = 0; i < attachs.length; i++) {
        if (!new File(attachs[i]).exists()) {
          wrongFlag = true;
        }
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        String filename = attachs[i];
        String displayname = attachNames[i];

        FileDataSource fds = new FileDataSource(filename);

        messageBodyPart.setDataHandler(new DataHandler(fds));

        messageBodyPart.setFileName(MimeUtility.encodeText(displayname));
        multipart.addBodyPart(messageBodyPart);
      }

      if (wrongFlag) {
        return "failed_wrong_noattach";
      }

      mimeMsg.setContent(multipart);

      mimeMsg.setSentDate(new Date());
      mimeMsg.saveChanges();

      Transport.send(mimeMsg);
      transport.close();
    } catch (NoSuchProviderException e) {
      return "failed_wrong_host";
    } catch (AddressException e) {
      return "failed_wrong_host";
    } catch (MessagingException e) {
      return "failed_send";
    } catch (UnsupportedEncodingException e) {
      return "failed_send";
    }
    MimeMessage mimeMsg;
    return "success";
  }

  public static class MailAutherticator extends Authenticator {
    private String m_username = null;
    private String m_userpass = null;

    public void setUsername(String username) {
      this.m_username = username;
    }

    public void setUserpass(String userpass) {
      this.m_userpass = userpass;
    }

    public MailAutherticator(String username, String userpass)
    {
      setUsername(username);
      setUserpass(userpass);
    }

    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.m_username, this.m_userpass);
    }
  }
}