package com.zving.platform.meta.ui;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.Verify;
import com.zving.framework.core.handler.ZAction;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.ui.tag.ListAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.meta.bl.MetaModelTemplateBL;

@Alias("ModelTemplate")
public class ModelTemplateUI extends UIFacade
{
  @Priv("Platform.Metadata")
  public void initTemplate()
  {
    long modelID = this.Request.getLong("ID");
    if (modelID == 0L);
  }

  @Priv("Platform.Metadata")
  @Alias(value="model/template/preview", alone=true)
  public void preview(ZAction za)
  {
    String modelID = $V("ID");
    String tmplTypeID = $V("MMTemplateTypeID");
    if ((ObjectUtil.empty(modelID)) || (ObjectUtil.empty(tmplTypeID))) {
      return;
    }
    String html = MetaModelTemplateBL.getPreviewHtml(modelID, tmplTypeID);
    za.writeHTML(html);
  }
  @Priv("Platform.Metadata.Add||Platform.Metadata.SimilarCreate")
  @Verify(ignoreAll=true)
  public void save() {
    String modelID = $V("ID");
    if (ObjectUtil.empty(modelID)) {
      fail(Lang.get("Common.InvalidID"));
      return;
    }
    MetaModelTemplateBL.save(this.Request, Current.getTransaction());
    if (Current.getTransaction().commit())
      success(Lang.get("Common.ExecuteSuccess"));
    else
      fail(Lang.get("Common.ExecuteFailed"));
  }

  @Priv
  public void tmplTypeBind(ListAction la)
  {
    if (ObjectUtil.empty($V("ID"))) {
      return;
    }
    la.bindData(MetaModelTemplateBL.getTemplateTypes($V("ID")));
  }
}