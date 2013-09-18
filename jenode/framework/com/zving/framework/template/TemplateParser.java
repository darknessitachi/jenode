package com.zving.framework.template;

import com.zving.framework.Config;
import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.collection.Treex;
import com.zving.framework.collection.Treex.TreeNode;
import com.zving.framework.template.exception.TemplateCompileException;
import com.zving.framework.ui.html.HtmlElement;
import com.zving.framework.ui.resource.UIResourceFile;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringFormat;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParser {
	protected String fileName;
	protected String content;
	protected Treex<TemplateFragment> tree;
	protected Treex.TreeNode<TemplateFragment> currentParent;
	protected boolean sessionFlag = true;
	protected String contentType = null;
	protected ITemplateManagerContext managerContext;
	private static final String ScriptStart = "<%";
	private static final String ScriptEnd = "%>";
	public static final Pattern PInclude = Pattern.compile(
			"\\<\\%\\s*@\\s*include\\s+file\\=\\\"(.*?)\\\".*?\\%\\>", 34);

	public TemplateParser(ITemplateManagerContext context) {
		this.managerContext = context;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void parseDirective(String str) {
		str = str.trim();
		if (!str.startsWith("@")) {
			return;
		}
		str = str.substring(1).trim();
		if (str.indexOf(" ") < 0) {
			return;
		}
		String command = str.substring(0, str.indexOf(" "));
		str = str.substring(str.indexOf(" "));
		Mapx attrs = HtmlElement.parseAttr(str);
		if (command.equalsIgnoreCase("page")) {
			this.sessionFlag = (!"false".equals(attrs.getString("session")));
			this.contentType = attrs.getString("contenttype");
		}
	}

	private void include() {
		Matcher m = PInclude.matcher(this.content);
		int lastIndex = 0;
		StringBuilder sb = new StringBuilder();
		while (m.find(lastIndex)) {
			String current = this.fileName.lastIndexOf("/") >= 0 ? this.fileName
					.substring(0, this.fileName.lastIndexOf("/")) : "";
			String file = m.group(1);
			if (file.startsWith("/")) {
				file = Config.getContextRealPath() + file;
			} else {
				while (true)
					if (file.startsWith("./")) {
						file = file.substring(2);
					} else {
						if (!file.startsWith("../"))
							break;
						current = current.lastIndexOf("/") >= 0 ? current
								.substring(0, current.lastIndexOf("/")) : "";
						file = file.substring(3);
					}

				file = current + "/" + file;
			}
			file = FileUtil.normalizePath(file);
			String txt = null;
			if (!file.startsWith(Config.getContextRealPath())) {
				file = Config.getContextRealPath() + file;
			}
			if (new File(file).exists()) {
				txt = FileUtil.readText(file);
			} else {
				if (file.startsWith(Config.getContextRealPath())) {
					file = file.substring(Config.getContextRealPath().length());
				}
				if (file.startsWith("/")) {
					file = file.substring(1);
				}
				UIResourceFile rf = new UIResourceFile(file);
				txt = rf.readText();
			}
			sb.append(this.content.substring(lastIndex, m.start()));
			if (txt != null)
				sb.append(txt);
			else {
				sb.append("#Include file not found:" + file);
			}
			lastIndex = m.end();
		}
		sb.append(this.content.substring(lastIndex));
		this.content = sb.toString();
		m = PInclude.matcher(this.content);
		if (m.find())
			include();
	}

	public void parse() throws TemplateCompileException {
		if (this.tree != null) {
			return;
		}
		if (this.content == null) {
			throw new TemplateCompileException(
					"Template content cann't be empty!");
		}
		include();
		this.content = this.content.trim();
		char[] cs = this.content.toCharArray();
		int currentLineNo = 1;

		int exprStartIndex = -1;
		boolean exprStringFlag = false;

		int htmlStartIndex = 0;
		int htmlStartLineNo = 0;

		int scriptStartIndex = -1;
		int scriptStartLineNo = -1;

		this.tree = new Treex();
		this.currentParent = this.tree.getRoot();
		String lowerTemplate = this.content.toLowerCase();
		char c;
		for (int i = 0; i < cs.length; i++) {
			c = cs[i];
			if (c == '\n') {
				currentLineNo++;
			}
			if (scriptStartIndex >= 0) {
				if ((c == '>')
						&& (this.content.indexOf("%>", i - "%>".length() + 1) == i
								- "%>".length() + 1)) {
					TemplateFragment tf = new TemplateFragment();
					tf.Type = 4;
					tf.FragmentText = this.content.substring(scriptStartIndex
							+ "<%".length(), i - "%>".length() + 1);
					tf.StartLineNo = scriptStartLineNo;
					this.currentParent.addChild(tf);
					htmlStartIndex = i + 1;
					htmlStartLineNo = currentLineNo;
					scriptStartIndex = -1;
					parseDirective(tf.FragmentText);
				}

			} else if ((c == '$') && (i < cs.length - 1)
					&& (cs[(i + 1)] == '{')) {
				if ((exprStartIndex >= 0) && (!exprStringFlag)) {
					Errorx.addMessage("Error on line " + currentLineNo
							+ ":expression not end!");
					htmlStartIndex = exprStartIndex;
					htmlStartLineNo = currentLineNo;
				}
				if (!exprStringFlag) {
					if (htmlStartIndex != i) {
						TemplateFragment tf = new TemplateFragment();
						tf.Type = 1;
						tf.FragmentText = this.content.substring(
								htmlStartIndex, i);
						tf.StartLineNo = htmlStartLineNo;
						this.currentParent.addChild(tf);
						htmlStartIndex = -1;
					}
					exprStartIndex = i;
				}
			} else {
				if (((c == '\'') || (c == '"')) && (exprStartIndex >= 0)
						&& (i != 0) && (cs[(i - 1)] != '\\')) {
					exprStringFlag = !exprStringFlag;
				}

				if ((c == '}') || (c == '\n')) {
					if ((exprStartIndex >= 0) && (!exprStringFlag)) {
						if ((c == '}') && (exprStartIndex + 2 < i)) {
							exprStringFlag = false;
							TemplateFragment tf = new TemplateFragment();
							tf.Type = 3;
							tf.FragmentText = this.content.substring(
									exprStartIndex, i + 1);
							tf.StartLineNo = currentLineNo;
							tf.StartCharIndex = exprStartIndex;
							tf.EndCharIndex = (i + 1);
							this.currentParent.addChild(tf);
							htmlStartIndex = i + 1;
							htmlStartLineNo = currentLineNo;
						} else {
							Errorx.addMessage("Error on line " + currentLineNo
									+ ":expression not end!");
							htmlStartIndex = exprStartIndex;
							htmlStartLineNo = currentLineNo;
						}
						exprStartIndex = -1;
					}

				} else if ((c == '<') && (i < cs.length - 1)) {
					if ((cs[(i + 1)] == '!')
							&& (lowerTemplate.indexOf("<!--", i) == i)
							&& (lowerTemplate.indexOf("<%", i) != i)) {
						int end = lowerTemplate.indexOf("-->", i);
						if (end >= 0) {
							for (int k = i; k < end; k++) {
								if (cs[k] == '\n') {
									currentLineNo++;
								}
							}
							i = end + 2;
						}
					} else {
						boolean tagStartFlag = true;
						int index = this.content.indexOf(':', i);
						int index2 = 0;
						if (index <= 0) {
							tagStartFlag = false;
						} else {
							for (int j = this.content.charAt(i + 1) == '/' ? i + 2
									: i + 1; j < index; j++) {
								if (!Character
										.isJavaIdentifierPart(this.content
												.charAt(j))) {
									tagStartFlag = false;
									break;
								}
							}
							if (tagStartFlag) {
								for (int j = index + 1; j < this.content.length(); j++) {
									char c2 = this.content.charAt(j);
									if ((Character.isWhitespace(c2))
											|| (c2 == '>')) {
										index2 = j - 1;
										break;
									}
									if (!Character.isJavaIdentifierPart(c2)) {
										tagStartFlag = false;
										break;
									}
								}
							}
						}
						if (tagStartFlag) {
							String prefix = lowerTemplate.substring(i + 1,
									index);
							if (prefix.startsWith("/")) {
								prefix = prefix.substring(1);
							}
							String tagName = lowerTemplate.substring(index + 1,
									index2 + 1);

							AbstractTag tag = this.managerContext.getTag(
									prefix, tagName);
							if ((tag != null) && (cs[(i + 1)] != '/')) {
								int tagEnd = getTagEnd(cs, i + 1);
								if (tagEnd < 0) {
									throw new TemplateCompileException(
											"Error on line " + currentLineNo
													+ ",tag no end:"
													+ this.fileName);
								}
								if ((htmlStartIndex != -1)
										&& (htmlStartIndex != i)) {
									TemplateFragment tf = new TemplateFragment();
									tf.Type = 1;
									tf.FragmentText = this.content.substring(
											htmlStartIndex, i);
									tf.StartCharIndex = htmlStartIndex;
									tf.StartLineNo = htmlStartLineNo;
									this.currentParent.addChild(tf);
									htmlStartIndex = -1;
								}
								String tagText = this.content.substring(i + 1,
										tagEnd).trim();
								TemplateFragment tf = new TemplateFragment();
								if (tagText.endsWith("/")) {
									tf.StartLineNo = currentLineNo;
									tf.Type = 2;
									tf.StartCharIndex = i;
									tf.EndCharIndex = tagEnd;
									tf.FragmentText = null;
									this.currentParent.addChild(tf);
								} else {
									tf.StartLineNo = currentLineNo;
									tf.Type = 2;
									tf.StartCharIndex = i;
									Treex.TreeNode tn = this.currentParent
											.addChild(tf);
									this.currentParent = tn;
								}
								parseTagAttributes(tf, tagText);
								for (int k = i; k < tagEnd; k++) {
									if (cs[k] == '\n') {
										currentLineNo++;
									}
								}
								i = tagEnd;
								htmlStartIndex = tagEnd + 1;
								htmlStartLineNo = currentLineNo;
								continue;
							}
							if ((tag != null) && (cs[(i + 1)] == '/')) {
								String tagEnd = this.content.substring(i,
										this.content.indexOf(">", i) + 1);
								TemplateFragment tf = (TemplateFragment) this.currentParent
										.getData();
								if (tf == null) {
									throw new TemplateCompileException(
											"Error on line " + currentLineNo
													+ ":" + tagEnd
													+ " no start!");
								}
								if ((htmlStartIndex != -1)
										&& (htmlStartIndex != i)) {
									TemplateFragment tf2 = new TemplateFragment();
									tf2.Type = 1;
									tf2.FragmentText = this.content.substring(
											htmlStartIndex, i);
									tf2.StartLineNo = htmlStartLineNo;
									tf2.StartCharIndex = htmlStartIndex;
									this.currentParent.addChild(tf2);
									htmlStartIndex = -1;
								}
								tf.FragmentText = this.content
										.substring(
												getTagEnd(cs,
														tf.StartCharIndex + 1) + 1,
												i);
								int end = this.content.indexOf('>', i);
								for (int k = i; k < end; k++) {
									if (cs[k] == '\n') {
										currentLineNo++;
									}
								}
								tf.EndCharIndex = (i = end);
								this.currentParent = this.currentParent
										.getParent();
								htmlStartIndex = i + 1;
								htmlStartLineNo = currentLineNo;
								continue;
							}
						}
						if ((this.content.indexOf("<%", i) == i)
								&& (htmlStartIndex != -1)) {
							if (htmlStartIndex != i) {
								TemplateFragment tf = new TemplateFragment();
								tf.Type = 1;
								tf.FragmentText = this.content.substring(
										htmlStartIndex, i);
								tf.StartLineNo = htmlStartLineNo;
								tf.StartCharIndex = htmlStartIndex;
								this.currentParent.addChild(tf);
								htmlStartIndex = -1;
								scriptStartIndex = i;
								scriptStartLineNo = currentLineNo;
							} else {
								scriptStartIndex = i;
								scriptStartLineNo = currentLineNo;
							}
						}
					}
				}
			}
		}
		if ((htmlStartIndex != -1) && (htmlStartIndex != cs.length - 1)) {
			TemplateFragment tf = new TemplateFragment();
			tf.Type = 1;
			tf.FragmentText = this.content.substring(htmlStartIndex);
			tf.StartLineNo = htmlStartLineNo;
			tf.StartCharIndex = htmlStartIndex;
			this.currentParent.addChild(tf);
			htmlStartIndex = -1;
		}

		for (Treex.TreeNode tn : this.tree.iterator()) {
			TemplateFragment tf = (TemplateFragment) tn.getData();
			if ((tf != null) && (tf.Type == 2) && (tf.EndCharIndex <= 0))
				throw new TemplateCompileException("Error on line "
						+ tf.StartLineNo + ": <" + tf.TagPrefix + ":"
						+ tf.TagName + "> no end");
		}
	}

	public int getTagEnd(char[] cs, int start) {
		char lastStringChar = '\000';
		for (int i = start; i < cs.length; i++) {
			char c = cs[i];
			if ((c == '"') || (c == '\'')) {
				if ((i > 0) && (cs[(i - 1)] == '\\')) {
					continue;
				}
				if (lastStringChar == c)
					lastStringChar = '\000';
				else if (lastStringChar == 0) {
					lastStringChar = c;
				}
			}
			if ((c == '>') && (lastStringChar == 0)) {
				return i;
			}
			if ((c == '<') && (lastStringChar == 0)) {
				return -1;
			}
		}
		return -1;
	}

	public void parseTagAttributes(TemplateFragment tf, String tagHTML)
			throws TemplateCompileException {
		String prefix = tagHTML.substring(0, tagHTML.indexOf(":")).trim()
				.toLowerCase();
		int nameEnd = -1;
		for (int i = prefix.length(); i < tagHTML.length(); i++) {
			if (Character.isWhitespace(tagHTML.charAt(i))) {
				nameEnd = i;
				break;
			}
		}
		String tagName = null;
		CaseIgnoreMapx map = new CaseIgnoreMapx();
		if (nameEnd > 0) {
			tagName = tagHTML.substring(tagHTML.indexOf(":") + 1, nameEnd)
					.trim().toLowerCase();
			tagHTML = tagHTML.substring(nameEnd + 1).trim();
			AbstractTag tag = this.managerContext.getTag(prefix, tagName);
			if (tag == null) {
				String message = StringFormat.format(
						"Error on line ?:<?:?> not registered.", new Object[] {
								Integer.valueOf(tf.StartLineNo), prefix,
								tagName });
				throw new TemplateCompileException(message);
			}
			if (tagHTML.endsWith("/")) {
				tagHTML = tagHTML.substring(0, tagHTML.length() - 1).trim();
			}
			tagHTML = tagHTML.replaceAll("\\s+", " ");
			char lastStringChar = '\000';
			int nameStartIndex = 0;
			int valueStartIndex = -1;
			String key = null;
			char[] cs = tagHTML.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				char c = cs[i];
				if ((c == '=') && (lastStringChar == 0)) {
					key = tagHTML.substring(nameStartIndex, i);
					if (!tag.hasAttribute(key)) {
						String message = StringFormat.format(
								"Error on line ?:<?:?> no attribute ?.",
								new Object[] { Integer.valueOf(tf.StartLineNo),
										prefix, tagName, key });
						throw new TemplateCompileException(message);
					}
					nameStartIndex = 0;
				}
				if ((c == ' ') && (lastStringChar == 0)) {
					nameStartIndex = i + 1;
				}
				if (((c == '"') || (c == '\''))
						&& ((i <= 0) || (cs[(i - 1)] != '\\'))) {
					if (lastStringChar == c) {
						lastStringChar = '\000';
						map.put(key, tagHTML.substring(valueStartIndex, i));
					} else if (lastStringChar != '"') {
						lastStringChar = c;
						valueStartIndex = i + 1;
					}
				}
			}
		} else {
			tagName = tagHTML.substring(tagHTML.indexOf(":") + 1).trim();
		}
		tf.TagPrefix = prefix;
		tf.TagName = tagName;
		tf.Attributes = map;
	}

	public Treex<TemplateFragment> getTree() {
		return this.tree;
	}

	public static void main(String[] args) {
	}

	public boolean isSessionFlag() {
		return this.sessionFlag;
	}

	public String getContentType() {
		return this.contentType;
	}
}