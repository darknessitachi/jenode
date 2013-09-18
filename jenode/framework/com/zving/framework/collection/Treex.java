package com.zving.framework.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.zving.framework.collection.Treex.TreeNode;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.utility.StringUtil;

public class Treex<T> implements Iterable<TreeNode<T>>, Serializable {
	private static final long serialVersionUID = 1L;
	private TreeNode<T> root = new TreeNode();

	public TreeNode<T> getRoot() {
		return this.root;
	}

	public TreeNode<T> getNode(T data) {
		TreeIterator ti = iterator();
		while (ti.hasNext()) {
			TreeNode tn = ti.next();
			if (tn.getData().equals(data)) {
				return tn;
			}
		}
		return null;
	}

	public TreeIterator<T> iterator() {
		return new TreeIterator(this.root);
	}

	public static <T> TreeIterator<T> iterator(TreeNode<T> node) {
		return new TreeIterator(node);
	}

	public String toString() {
		return toString(Formatter.DefaultFormatter);
	}

	public String toString(Formatter f) {
		StringBuilder sb = new StringBuilder();
		TreeIterator ti = iterator();
		while (ti.hasNext()) {
			TreeNode tn = ti.nextNode();
			TreeNode p = tn.getParent();
			String str = "";
			while ((p != null) && (!p.isRoot())) {
				if (p.isLast())
					str = "  " + str;
				else {
					str = "│ " + str;
				}
				p = p.getParent();
			}
			sb.append(str);
			if (!tn.isRoot()) {
				if (tn.isLast())
					sb.append("└─");
				else {
					sb.append("├─");
				}
			}
			sb.append(f.format(tn.getData()));
			sb.append("\n");
		}
		return sb.toString();
	}

	public ArrayList<TreeNode<T>> toArray() {
		TreeIterator ti = new TreeIterator(this.root);
		ArrayList arr = new ArrayList();
		while (ti.hasNext()) {
			arr.add(ti.next());
		}
		return arr;
	}

	public static Treex<DataRow> dataTableToTree(DataTable dt) {
		return dataTableToTree(dt, "ID", "ParentID");
	}

	public static Treex<DataRow> dataTableToTree(DataTable dt,
			String identifierColumnName, String parentIdentifierColumnName) {
		Treex tree = new Treex();
		Mapx map = dt.toMapx(identifierColumnName, parentIdentifierColumnName);
		Mapx map2 = dt.toMapx(parentIdentifierColumnName, identifierColumnName);
		for (int i = 0; i < dt.getRowCount(); i++) {
			String ID = dt.getString(i, identifierColumnName);
			String parentID = map.getString(ID);
			if ((StringUtil.isEmpty(parentID)) || (!map.containsKey(parentID))
					|| (parentID.equals(ID))) {
				DataRow dr = dt.getDataRow(i);
				TreeNode tn = tree.root.addChild(dr);
				dealNode(dt, tn, map2, identifierColumnName,
						parentIdentifierColumnName);
			}
		}
		return tree;
	}

	private static void dealNode(DataTable dt, TreeNode<DataRow> tn,
			Mapx<?, ?> map, String identifierColumnName,
			String parentIdentifierColumnName) {
		DataRow dr = (DataRow) tn.getData();
		String ID = dr.getString(identifierColumnName);
		for (int i = 0; i < dt.getRowCount(); i++) {
			String ChildID = dt.getString(i, identifierColumnName);
			String parentID = dt.getString(i, parentIdentifierColumnName);
			if ((parentID != null) && (parentID.equals(ID))
					&& (!ChildID.equals(ID))) {
				TreeNode childNode = tn.addChild(dt.getDataRow(i));
				if (map.get(ChildID) != null)
					dealNode(dt, childNode, map, identifierColumnName,
							parentIdentifierColumnName);
			}
		}
	}

	public static class TreeIterator<T> implements Iterator<Treex.TreeNode<T>>,
			Iterable<Treex.TreeNode<T>> {
		private Treex.TreeNode<T> last;
		private Treex.TreeNode<T> next;
		private Treex.TreeNode<T> start;

		TreeIterator(Treex.TreeNode<T> node) {
			this.start = (this.next = node);
		}

		public boolean hasNext() {
			if (this.next == null) {
				return false;
			}
			if ((this.next == this.start)
					&& (this.start.getChildren().size() == 0)) {
				return false;
			}
			if ((this.next != this.start)
					&& (this.next.getLevel() == this.start.getLevel())) {
				return false;
			}
			return true;
		}

		public Treex.TreeNode<T> next() {
			if (this.next == null) {
				throw new NoSuchElementException();
			}
			this.last = this.next;
			if (this.next.hasChild()) {
				this.next = ((Treex.TreeNode) this.next.getChildren().get(0));
			} else {
				while (this.next.getNextSibling() == null) {
//					if (Treex.TreeNode.access$0(this.next).isRoot()) {
//						this.next = null;
//						return this.last;
//					}
//					this.next = Treex.TreeNode.access$0(this.next);
				}

				this.next = this.next.getNextSibling();
			}
			return this.last;
		}

		public Treex.TreeNode<T> nextNode() {
			return next();
		}

		public Treex.TreeNode<T> currentNode() {
			return this.next;
		}

		public void remove() {
			if (this.last == null) {
				throw new IllegalStateException();
			}
//			Treex.TreeNode.access$0(this.last).getChildren().remove(this.last);
			this.last = null;
		}

		public Iterator<Treex.TreeNode<T>> iterator() {
			return this;
		}
	}

	public static class TreeNode<T> implements Serializable {
		private static final long serialVersionUID = 1L;
		private int level;
		private T data;
		private Treex.TreeNodeList<T> children = new Treex.TreeNodeList();
		private TreeNode<T> parent;
		private int pos;

		public TreeNode<T> addChild(T data) {
			TreeNode tn = new TreeNode();
			this.level += 1;
			tn.data = data;
			tn.parent = this;
			tn.pos = this.children.size();
			this.children.add(tn);
			return tn;
		}

		public void removeChild(T data) {
			for (int i = 0; i < this.children.size(); i++) {
				TreeNode child = (TreeNode) this.children.get(i);
				if (child.getData() == null) {
					if (data == null) {
						this.children.remove(i);
						break;
					}

				} else if (child.getData().equals(data)) {
					this.children.remove(i);
					break;
				}
			}
		}

		public TreeNode<T> getChild(T data) {
			for (int i = 0; i < this.children.size(); i++) {
				TreeNode child = (TreeNode) this.children.get(i);
				if (child.getData() == null) {
					if (data == null) {
						return child;
					}

				} else if (child.getData().equals(data)) {
					return child;
				}
			}
			return null;
		}

		public TreeNode<T> getPreviousSibling() {
			if (this.pos == 0) {
				return null;
			}
			return (TreeNode) this.parent.getChildren().get(this.pos - 1);
		}

		public TreeNode<T> getNextSibling() {
			if ((this.parent == null)
					|| (this.pos == this.parent.getChildren().size() - 1)) {
				return null;
			}
			return (TreeNode) this.parent.getChildren().get(this.pos + 1);
		}

		public int getLevel() {
			return this.level;
		}

		public boolean isRoot() {
			return this.parent == null;
		}

		public boolean isLast() {
			if ((this.parent != null)
					&& (this.pos != this.parent.getChildren().size() - 1)) {
				return false;
			}
			return true;
		}

		public boolean hasChild() {
			return this.children.size() != 0;
		}

		public TreeNode<T> getParent() {
			return this.parent;
		}

		public int getPosition() {
			return this.pos;
		}

		public Treex.TreeNodeList<T> getChildren() {
			return this.children;
		}

		public T getData() {
			return this.data;
		}

		public void setData(T data) {
			this.data = data;
		}
	}

	public static class TreeNodeList<T> extends ArrayList<Treex.TreeNode<T>> {
		private static final long serialVersionUID = 1L;

		public Treex.TreeNode<T> remove(Treex.TreeNode<T> node) {
			int pos = node.getPosition();
			for (int i = pos + 1; i < size(); i++) {
				Treex.TreeNode tn = (Treex.TreeNode) get(i);
				tn.pos -= 1;
			}
			super.remove(node);
			return node;
		}

		public Treex.TreeNode<T> last() {
			int size = size();
			if (size == 0) {
				return null;
			}
			return (Treex.TreeNode) get(size - 1);
		}
	}
}