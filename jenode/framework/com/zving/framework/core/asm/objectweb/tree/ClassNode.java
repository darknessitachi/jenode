package com.zving.framework.core.asm.objectweb.tree;

import com.zving.framework.core.asm.objectweb.AnnotationVisitor;
import com.zving.framework.core.asm.objectweb.Attribute;
import com.zving.framework.core.asm.objectweb.ClassVisitor;
import com.zving.framework.core.asm.objectweb.FieldVisitor;
import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassNode extends ClassVisitor {
	public int version;
	public int access;
	public String name;
	public String signature;
	public String superName;
	public List<String> interfaces;
	public String sourceFile;
	public String sourceDebug;
	public String outerClass;
	public String outerMethod;
	public String outerMethodDesc;
	public List<AnnotationNode> visibleAnnotations;
	public List<AnnotationNode> invisibleAnnotations;
	public List<Attribute> attrs;
	public List<InnerClassNode> innerClasses;
	public List<FieldNode> fields;
	public List<MethodNode> methods;

	public ClassNode() {
		this(262144);
	}

	public ClassNode(int api) {
		super(api);
		this.interfaces = new ArrayList();
		this.innerClasses = new ArrayList();
		this.fields = new ArrayList();
		this.methods = new ArrayList();
	}

	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		this.version = version;
		this.access = access;
		this.name = name;
		this.signature = signature;
		this.superName = superName;
		if (interfaces != null)
			this.interfaces.addAll(Arrays.asList(interfaces));
	}

	public void visitSource(String file, String debug) {
		this.sourceFile = file;
		this.sourceDebug = debug;
	}

	public void visitOuterClass(String owner, String name, String desc) {
		this.outerClass = owner;
		this.outerMethod = name;
		this.outerMethodDesc = desc;
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

	public void visitAttribute(Attribute attr) {
		if (this.attrs == null) {
			this.attrs = new ArrayList(1);
		}
		this.attrs.add(attr);
	}

	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
		InnerClassNode icn = new InnerClassNode(name, outerName, innerName,
				access);
		this.innerClasses.add(icn);
	}

	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		FieldNode fn = new FieldNode(access, name, desc, signature, value);
		this.fields.add(fn);
		return fn;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodNode mn = new MethodNode(access, name, desc, signature,
				exceptions);
		this.methods.add(mn);
		return mn;
	}

	public void visitEnd() {
	}

	public void check(int api) {
	}

	public void accept(ClassVisitor cv) {
		String[] interfaces = new String[this.interfaces.size()];
		this.interfaces.toArray(interfaces);
		cv.visit(this.version, this.access, this.name, this.signature,
				this.superName, interfaces);

		if ((this.sourceFile != null) || (this.sourceDebug != null)) {
			cv.visitSource(this.sourceFile, this.sourceDebug);
		}

		if (this.outerClass != null) {
			cv.visitOuterClass(this.outerClass, this.outerMethod,
					this.outerMethodDesc);
		}

		int n = this.visibleAnnotations == null ? 0 : this.visibleAnnotations
				.size();
		for (int i = 0; i < n; i++) {
			AnnotationNode an = (AnnotationNode) this.visibleAnnotations.get(i);
			an.accept(cv.visitAnnotation(an.desc, true));
		}
		n = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations
				.size();
		for (int i = 0; i < n; i++) {
			AnnotationNode an = (AnnotationNode) this.invisibleAnnotations
					.get(i);
			an.accept(cv.visitAnnotation(an.desc, false));
		}
		n = this.attrs == null ? 0 : this.attrs.size();
		for (int i = 0; i < n; i++) {
			cv.visitAttribute((Attribute) this.attrs.get(i));
		}

		for (int i = 0; i < this.innerClasses.size(); i++) {
			((InnerClassNode) this.innerClasses.get(i)).accept(cv);
		}

		for (int i = 0; i < this.fields.size(); i++) {
			((FieldNode) this.fields.get(i)).accept(cv);
		}

		for (int i = 0; i < this.methods.size(); i++) {
			((MethodNode) this.methods.get(i)).accept(cv);
		}

		cv.visitEnd();
	}
}