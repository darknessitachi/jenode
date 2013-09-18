package com.zving.framework.core.asm.objectweb.tree;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.zving.framework.core.asm.objectweb.AnnotationVisitor;
import com.zving.framework.core.asm.objectweb.Attribute;
import com.zving.framework.core.asm.objectweb.ClassVisitor;
import com.zving.framework.core.asm.objectweb.Handle;
import com.zving.framework.core.asm.objectweb.Label;
import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodNode extends MethodVisitor {
	public int access;
	public String name;
	public String desc;
	public String signature;
	public List<String> exceptions;
	public List<AnnotationNode> visibleAnnotations;
	public List<AnnotationNode> invisibleAnnotations;
	public List<Attribute> attrs;
	public Object annotationDefault;
	public List<AnnotationNode>[] visibleParameterAnnotations;
	public List<AnnotationNode>[] invisibleParameterAnnotations;
	public InsnList instructions;
	public List<TryCatchBlockNode> tryCatchBlocks;
	public int maxStack;
	public int maxLocals;
	public List<LocalVariableNode> localVariables;
	private boolean visited;

	public MethodNode() {
		this(262144);
	}

	public MethodNode(int api) {
		super(api);
		this.instructions = new InsnList();
	}

	public MethodNode(int access, String name, String desc, String signature,
			String[] exceptions) {
		this(262144, access, name, desc, signature, exceptions);
	}

	public MethodNode(int api, int access, String name, String desc,
			String signature, String[] exceptions) {
		super(api);
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = new ArrayList(exceptions == null ? 0
				: exceptions.length);
		boolean isAbstract = (access & 0x400) != 0;
		if (!isAbstract) {
			this.localVariables = new ArrayList(5);
		}
		this.tryCatchBlocks = new ArrayList();
		if (exceptions != null) {
			this.exceptions.addAll(Arrays.asList(exceptions));
		}
		this.instructions = new InsnList();
	}

	public AnnotationVisitor visitAnnotationDefault() {
		return new AnnotationNode(new ArrayList(0) {
			private static final long serialVersionUID = 1L;

			public boolean add(Object o) {
				MethodNode.this.annotationDefault = o;
				return super.add(o);
			}
		});
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationNode an = new AnnotationNode(desc);
		if (visible) {
			if (this.visibleAnnotations == null) {
				this.visibleAnnotations = new ArrayList(1);
			}
			this.visibleAnnotations.add(an);
		} else {
			if (this.invisibleAnnotations == null) {
				this.invisibleAnnotations = new ArrayList(1);
			}
			this.invisibleAnnotations.add(an);
		}
		return an;
	}

	public AnnotationVisitor visitParameterAnnotation(int parameter,
			String desc, boolean visible) {
		AnnotationNode an = new AnnotationNode(desc);
		if (visible) {
			if (this.visibleParameterAnnotations == null) {
				int params = Type.getArgumentTypes(this.desc).length;
				this.visibleParameterAnnotations = new List[params];
			}
			if (this.visibleParameterAnnotations[parameter] == null) {
				this.visibleParameterAnnotations[parameter] = new ArrayList(1);
			}
			this.visibleParameterAnnotations[parameter].add(an);
		} else {
			if (this.invisibleParameterAnnotations == null) {
				int params = Type.getArgumentTypes(this.desc).length;
				this.invisibleParameterAnnotations = new List[params];
			}
			if (this.invisibleParameterAnnotations[parameter] == null) {
				this.invisibleParameterAnnotations[parameter] = new ArrayList(1);
			}
			this.invisibleParameterAnnotations[parameter].add(an);
		}
		return an;
	}

	public void visitAttribute(Attribute attr) {
		if (this.attrs == null) {
			this.attrs = new ArrayList(1);
		}
		this.attrs.add(attr);
	}

	public void visitCode() {
	}

	public void visitFrame(int type, int nLocal, Object[] local, int nStack,
			Object[] stack) {
		this.instructions.add(new FrameNode(type, nLocal, local == null ? null
				: getLabelNodes(local), nStack, stack == null ? null
				: getLabelNodes(stack)));
	}

	public void visitInsn(int opcode) {
		this.instructions.add(new InsnNode(opcode));
	}

	public void visitIntInsn(int opcode, int operand) {
		this.instructions.add(new IntInsnNode(opcode, operand));
	}

	public void visitVarInsn(int opcode, int var) {
		this.instructions.add(new VarInsnNode(opcode, var));
	}

	public void visitTypeInsn(int opcode, String type) {
		this.instructions.add(new TypeInsnNode(opcode, type));
	}

	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {
		this.instructions.add(new FieldInsnNode(opcode, owner, name, desc));
	}

	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		this.instructions.add(new MethodInsnNode(opcode, owner, name, desc));
	}

	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
			Object[] bsmArgs) {
		this.instructions.add(new InvokeDynamicInsnNode(name, desc, bsm,
				bsmArgs));
	}

	public void visitJumpInsn(int opcode, Label label) {
		this.instructions.add(new JumpInsnNode(opcode, getLabelNode(label)));
	}

	public void visitLabel(Label label) {
		this.instructions.add(getLabelNode(label));
	}

	public void visitLdcInsn(Object cst) {
		this.instructions.add(new LdcInsnNode(cst));
	}

	public void visitIincInsn(int var, int increment) {
		this.instructions.add(new IincInsnNode(var, increment));
	}

	public void visitTableSwitchInsn(int min, int max, Label dflt,
			Label[] labels) {
		this.instructions.add(new TableSwitchInsnNode(min, max,
				getLabelNode(dflt), getLabelNodes(labels)));
	}

	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		this.instructions.add(new LookupSwitchInsnNode(getLabelNode(dflt),
				keys, getLabelNodes(labels)));
	}

	public void visitMultiANewArrayInsn(String desc, int dims) {
		this.instructions.add(new MultiANewArrayInsnNode(desc, dims));
	}

	public void visitTryCatchBlock(Label start, Label end, Label handler,
			String type) {
		this.tryCatchBlocks.add(new TryCatchBlockNode(getLabelNode(start),
				getLabelNode(end), getLabelNode(handler), type));
	}

	public void visitLocalVariable(String name, String desc, String signature,
			Label start, Label end, int index) {
		this.localVariables.add(new LocalVariableNode(name, desc, signature,
				getLabelNode(start), getLabelNode(end), index));
	}

	public void visitLineNumber(int line, Label start) {
		this.instructions.add(new LineNumberNode(line, getLabelNode(start)));
	}

	public void visitMaxs(int maxStack, int maxLocals) {
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
	}

	public void visitEnd() {
	}

	protected LabelNode getLabelNode(Label l) {
		if (!(l.info instanceof LabelNode)) {
			l.info = new LabelNode();
		}
		return (LabelNode) l.info;
	}

	private LabelNode[] getLabelNodes(Label[] l) {
		LabelNode[] nodes = new LabelNode[l.length];
		for (int i = 0; i < l.length; i++) {
			nodes[i] = getLabelNode(l[i]);
		}
		return nodes;
	}

	private Object[] getLabelNodes(Object[] objs) {
		Object[] nodes = new Object[objs.length];
		for (int i = 0; i < objs.length; i++) {
			Object o = objs[i];
			if ((o instanceof Label)) {
				o = getLabelNode((Label) o);
			}
			nodes[i] = o;
		}
		return nodes;
	}

	public void check(int api) {
	}

	public void accept(ClassVisitor cv) {
		String[] exceptions = new String[this.exceptions.size()];
		this.exceptions.toArray(exceptions);
		MethodVisitor mv = cv.visitMethod(this.access, this.name, this.desc,
				this.signature, exceptions);
		if (mv != null)
			accept(mv);
	}

	public void accept(MethodVisitor mv) {
		if (this.annotationDefault != null) {
			AnnotationVisitor av = mv.visitAnnotationDefault();
			AnnotationNode.accept(av, null, this.annotationDefault);
			if (av != null) {
				av.visitEnd();
			}
		}
		int n = this.visibleAnnotations == null ? 0 : this.visibleAnnotations
				.size();
		for (int i = 0; i < n; i++) {
			AnnotationNode an = (AnnotationNode) this.visibleAnnotations.get(i);
			an.accept(mv.visitAnnotation(an.desc, true));
		}
		n = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations
				.size();
		for (int i = 0; i < n; i++) {
			AnnotationNode an = (AnnotationNode) this.invisibleAnnotations
					.get(i);
			an.accept(mv.visitAnnotation(an.desc, false));
		}
		n = this.visibleParameterAnnotations == null ? 0
				: this.visibleParameterAnnotations.length;
		for (int i = 0; i < n; i++) {
			List l = this.visibleParameterAnnotations[i];
			if (l != null) {
				for (int j = 0; j < l.size(); j++) {
					AnnotationNode an = (AnnotationNode) l.get(j);
					an.accept(mv.visitParameterAnnotation(i, an.desc, true));
				}
			}
		}
		n = this.invisibleParameterAnnotations == null ? 0
				: this.invisibleParameterAnnotations.length;
		for (int i = 0; i < n; i++) {
			List l = this.invisibleParameterAnnotations[i];
			if (l != null) {
				for (int j = 0; j < l.size(); j++) {
					AnnotationNode an = (AnnotationNode) l.get(j);
					an.accept(mv.visitParameterAnnotation(i, an.desc, false));
				}
			}
		}
		if (this.visited) {
			this.instructions.resetLabels();
		}
		n = this.attrs == null ? 0 : this.attrs.size();
		for (int i = 0; i < n; i++) {
			mv.visitAttribute((Attribute) this.attrs.get(i));
		}

		if (this.instructions.size() > 0) {
			mv.visitCode();

			n = this.tryCatchBlocks == null ? 0 : this.tryCatchBlocks.size();
			for (int i = 0; i < n; i++) {
				((TryCatchBlockNode) this.tryCatchBlocks.get(i)).accept(mv);
			}

			this.instructions.accept(mv);

			n = this.localVariables == null ? 0 : this.localVariables.size();
			for (int i = 0; i < n; i++) {
				((LocalVariableNode) this.localVariables.get(i)).accept(mv);
			}

			mv.visitMaxs(this.maxStack, this.maxLocals);
			this.visited = true;
		}
		mv.visitEnd();
	}
}