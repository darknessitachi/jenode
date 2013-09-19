package com.zving.platform.pub;

import com.zving.framework.User;
import com.zving.framework.core.IURLHandler;
import com.zving.framework.utility.NumberUtil;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import nl.captcha.backgrounds.TransparentBackgroundProducer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.renderer.ColoredEdgesWordRenderer;

public class AuthCodeURLHandler
  implements IURLHandler
{
  public static final String ID = "com.zving.platform.pub.AuthCodeURLProcessor";
  private static int _width = 100;
  private static int _height = 28;

  private static final List<Color> COLORS = new ArrayList();
  private static final List<Font> FONTS = new ArrayList();

  static {
    COLORS.add(new Color(40, 74, 83));
    COLORS.add(new Color(95, 129, 52));

    FONTS.add(new Font("Geneva", 2, 28));
    FONTS.add(new Font("Courier", 2, 28));
    FONTS.add(new Font("Serif", 2, 28));
    FONTS.add(new Font("Arial", 2, 28));
  }

  public String getID() {
    return "com.zving.platform.pub.AuthCodeURLProcessor";
  }

  public String getName() {
    return "AuthCode Generator";
  }

  public boolean match(String url) {
    int i = url.indexOf("?");
    if (i > 0) {
      url = url.substring(0, i);
    }
    return url.equals("/authCode.zhtml");
  }

  public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    Color c = (Color)COLORS.get(NumberUtil.getRandomInt(COLORS.size()));
    List cs = new ArrayList();
    cs.add(c);
    ColoredEdgesWordRenderer wordRenderer = new ColoredEdgesWordRenderer(cs, FONTS);
    Captcha captcha = new Captcha.Builder(_width, _height).addText(wordRenderer).gimp().addNoise(new CurvedLineNoiseProducer(c, 1.0F))
      .addBackground(new TransparentBackgroundProducer()).build();
    CaptchaServletUtil.writeImage(response, captcha.getImage());
    User.setValue("_ZVING_AUTHKEY", captcha.getAnswer());
    return true;
  }

  public void init() {
  }

  public int getOrder() {
    return 9997;
  }

  public void destroy()
  {
  }
}