package com.zving.framework.ui.zhtml;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.extend.action.ExtendTag;
import com.zving.framework.i18n.LangButtonTag;
import com.zving.framework.i18n.LangTag;
import com.zving.framework.security.PrivTag;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.ui.control.ButtonTag;
import com.zving.framework.ui.control.CheckboxTag;
import com.zving.framework.ui.control.ChildTabTag;
import com.zving.framework.ui.control.DataGridTag;
import com.zving.framework.ui.control.DataListTag;
import com.zving.framework.ui.control.MenuTag;
import com.zving.framework.ui.control.PageBarTag;
import com.zving.framework.ui.control.PanelHeaderTag;
import com.zving.framework.ui.control.RadioTag;
import com.zving.framework.ui.control.ScrollPanelTag;
import com.zving.framework.ui.control.SelectTag;
import com.zving.framework.ui.control.SliderTag;
import com.zving.framework.ui.control.TabTag;
import com.zving.framework.ui.control.ToolBarTag;
import com.zving.framework.ui.control.TreeTag;
import com.zving.framework.ui.control.UploaderTag;
import com.zving.framework.ui.tag.ActionTag;
import com.zving.framework.ui.tag.ChooseTag;
import com.zving.framework.ui.tag.ElseTag;
import com.zving.framework.ui.tag.EvalTag;
import com.zving.framework.ui.tag.ForTag;
import com.zving.framework.ui.tag.IfTag;
import com.zving.framework.ui.tag.InitTag;
import com.zving.framework.ui.tag.ListTag;
import com.zving.framework.ui.tag.ParamTag;
import com.zving.framework.ui.tag.VarTag;
import com.zving.framework.ui.tag.WhenTag;

public class ZhtmlTagService extends AbstractExtendService<AbstractTag>
{
  private static ZhtmlTagService instance;

  public static ZhtmlTagService getInstance()
  {
    if (instance == null) {
      synchronized (ZhtmlTagService.class) {
        if (instance == null) {
          ZhtmlTagService tmp = (ZhtmlTagService)findInstance(ZhtmlTagService.class);
          tmp.register(new DataGridTag());
          tmp.register(new DataListTag());
          tmp.register(new InitTag());
          tmp.register(new PageBarTag());
          tmp.register(new ButtonTag());
          tmp.register(new PanelHeaderTag());
          tmp.register(new ChildTabTag());
          tmp.register(new TabTag());
          tmp.register(new TreeTag());
          tmp.register(new MenuTag());
          tmp.register(new SelectTag());
          tmp.register(new UploaderTag());
          tmp.register(new ListTag());
          tmp.register(new RadioTag());
          tmp.register(new CheckboxTag());
          tmp.register(new ParamTag());
          tmp.register(new IfTag());
          tmp.register(new ElseTag());
          tmp.register(new ChooseTag());
          tmp.register(new WhenTag());
          tmp.register(new ToolBarTag());
          tmp.register(new ScrollPanelTag());
          tmp.register(new ActionTag());
          tmp.register(new ExtendTag());
          tmp.register(new PrivTag());
          tmp.register(new EvalTag());
          tmp.register(new ForTag());
          tmp.register(new VarTag());
          tmp.register(new LangTag());
          tmp.register(new LangButtonTag());
          tmp.register(new SliderTag());
          instance = tmp;
        }
      }
    }
    return instance;
  }
}