package com.zving.framework.template;

import com.zving.framework.collection.Mapx;
import com.zving.framework.collection.Treex;
import com.zving.framework.collection.Treex.TreeNode;
import com.zving.framework.collection.Treex.TreeNodeList;
import com.zving.framework.template.command.ExpressionCommand;
import com.zving.framework.template.command.PrintCommand;
import com.zving.framework.template.command.TagInvokeCommand;
import com.zving.framework.template.exception.TemplateCompileException;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TemplateCompiler
{
  protected String fileName;
  protected long lastModified;
  protected TemplateExecutor executor;
  protected ArrayList<ITemplateCommand> commandList = new ArrayList();
  protected TemplateParser parser;
  protected ITemplateManagerContext managerContext;

  public TemplateCompiler(ITemplateManagerContext managerContext)
  {
    this.managerContext = managerContext;
    this.parser = new TemplateParser(managerContext);
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void compile(String fileName)
    throws FileNotFoundException, TemplateCompileException
  {
    fileName = FileUtil.normalizePath(fileName);
    if (!new File(fileName).exists()) {
      throw new FileNotFoundException("File not found:" + fileName);
    }
    this.lastModified = new File(fileName).lastModified();
    this.fileName = fileName;
    compileSource(FileUtil.readText(fileName));
  }

  public void compileSource(String source)
    throws TemplateCompileException
  {
    long start = System.currentTimeMillis();
    this.parser.setContent(source);
    this.parser.setFileName(this.fileName);
    this.parser.parse();
    if (Errorx.hasError()) {
      throw new TemplateCompileException(Errorx.getAllMessage());
    }
    if (this.lastModified == 0L) {
      this.lastModified = System.currentTimeMillis();
    }

    this.executor = new TemplateExecutor(this.managerContext);
    this.executor.fileName = this.fileName;
    this.executor.lastModified = this.lastModified;
    this.executor.sessionFlag = this.parser.isSessionFlag();
    this.executor.contentType = this.parser.getContentType();

    Treex tree = this.parser.getTree();
    compile(tree);
    if (Errorx.hasError()) {
      throw new TemplateCompileException(Errorx.getAllMessage());
    }
    this.executor.init(this.commandList);
    LogUtil.info("Compile " + this.fileName + " cost " + (System.currentTimeMillis() - start) + " ms.");
  }

  public void compile(Treex<TemplateFragment> tree) {
    if (Errorx.hasError()) {
      return;
    }

    Treex.TreeNodeList list = tree.getRoot().getChildren();
    for (int i = 0; i < list.size(); i++)
      compileNode((Treex.TreeNode)list.get(i), this.commandList, this.executor.tree.getRoot());
  }

  protected void compileNode(Treex.TreeNode<TemplateFragment> node, ArrayList<ITemplateCommand> parentList, Treex.TreeNode<AbstractTag> parentTagNode)
  {
    TemplateFragment tf = (TemplateFragment)node.getData();
    if (tf.Type == 1) {
      parentList.add(new PrintCommand(tf.FragmentText));
    } else if (tf.Type == 3) {
      parentList.add(new ExpressionCommand(tf.FragmentText));
    } else if (tf.Type == 4) {
      if (tf.FragmentText.trim().startsWith("@")) {
        return;
      }

      parentList.add(new PrintCommand("<%" + StringUtil.javaEncode(tf.FragmentText) + "%>"));
    } else if (tf.Type == 2) {
      try {
        compileTag(node, parentList, parentTagNode);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected void compileTag(Treex.TreeNode<TemplateFragment> node, ArrayList<ITemplateCommand> parentList, Treex.TreeNode<AbstractTag> parentTagNode) throws Exception
  {
    TemplateFragment tf = (TemplateFragment)node.getData();
    AbstractTag tag = this.managerContext.getTag(tf.TagPrefix, tf.TagName);
    tag.setStartLineNo(tf.StartLineNo);
    tag.setStartCharIndex(tf.StartCharIndex);
    for (String k : tf.Attributes.keyArray()) {
      tag.setAttribute(k, (String)tf.Attributes.get(k));
    }
    Treex.TreeNode tagNode = parentTagNode.addChild(tag);
    ArrayList list = new ArrayList();
    boolean hasBody = StringUtil.isNotEmpty(tf.FragmentText);
    if (hasBody) {
      Treex.TreeNodeList nodeList = node.getChildren();
      for (int i = 0; i < nodeList.size(); i++) {
        compileNode((Treex.TreeNode)nodeList.get(i), list, tagNode);
      }
    }
    TagInvokeCommand invoke = new TagInvokeCommand(tag, list, node.getLevel(), hasBody);
    parentList.add(invoke);
  }

  public TemplateExecutor getExecutor() {
    return this.executor;
  }
}