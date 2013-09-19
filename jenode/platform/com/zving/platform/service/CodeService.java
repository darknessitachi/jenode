package com.zving.platform.service;

import com.zving.framework.data.Transaction;
import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.FixedCodeType;
import com.zving.platform.FixedCodeType.FixedCodeItem;
import com.zving.platform.pub.OrderUtil;
import com.zving.schema.ZDCode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CodeService extends AbstractExtendService<FixedCodeType>
{
  public static CodeService getInstance()
  {
    return (CodeService)findInstance(CodeService.class);
  }

  public static void init()
  {
    Transaction trans = new Transaction();
    ArrayList dbSet = new ArrayList();
    for (ZDCode code : new ZDCode().query())
      dbSet.add("@CodeType=" + code.getCodeType() + "@ParentCode=" + code.getParentCode() + "@CodeValue=" + code.getCodeValue());
    Iterator localIterator2;
    for (??? = getInstance().getAll().iterator(); ???.hasNext(); 
      localIterator2.hasNext())
    {
      FixedCodeType fct = (FixedCodeType)???.next();
      ZDCode code = new ZDCode();
      code.setCodeType(fct.getCodeType());
      code.setParentCode("System");
      code.setCodeValue("System");

      if (!dbSet.contains("@CodeType=" + fct.getCodeType() + "@ParentCode=System@CodeValue=System"))
      {
        code.setCodeName(fct.getCodeName());
        code.setCodeOrder(OrderUtil.getDefaultOrder());
        code.setAddTime(new Date());
        code.setAddUser("System");
        trans.add(code, 1);
      }

      List items = fct.getFixedItems();
      localIterator2 = items.iterator(); continue; FixedCodeType.FixedCodeItem item = (FixedCodeType.FixedCodeItem)localIterator2.next();

      if (!dbSet.contains("@CodeType=" + code.getCodeType() + "@ParentCode=" + code.getCodeType() + "@CodeValue=" + 
        item.getValue())) {
        if (!StringUtil.isEmpty(item.getValue()))
        {
          ZDCode codeChild = new ZDCode();
          codeChild.setCodeType(code.getCodeType());
          codeChild.setParentCode(code.getCodeType());
          codeChild.setCodeValue(item.getValue());
          codeChild.setCodeName(item.getName());
          codeChild.setCodeOrder(OrderUtil.getDefaultOrder());
          codeChild.setAddTime(new Date());
          codeChild.setAddUser("System");
          trans.add(codeChild, 6);
        }
      }
    }
    if (!trans.commit())
      LogUtil.error("Code 初始化失败！");
  }
}