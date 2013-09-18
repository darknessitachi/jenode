package com.zving.framework.core.asm;

import com.zving.framework.core.asm.objectweb.AnnotationVisitor;
import com.zving.framework.core.asm.objectweb.ClassReader;
import com.zving.framework.core.asm.objectweb.ClassVisitor;
import com.zving.framework.core.asm.objectweb.FieldVisitor;
import com.zving.framework.core.asm.objectweb.MethodVisitor;
import java.io.IOException;
import java.util.List;

public class ClassAccess extends AbstractAccess {
	public ClassAccess(String className) {
		try {
			ClassReader reader = new ClassReader(className);
			reader.accept(new AccessClassVisitor(), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ClassAccess(byte[] bs) {
		ClassReader reader = new ClassReader(bs);
		reader.accept(new AccessClassVisitor(), 0);
	}

	public List<MethodAccess> getMethodAccesses() {
		return null;
	}

	public List<FieldAccess> getFieldAccesses() {
		return null;
	}

	public class AccessClassVisitor extends ClassVisitor {
		public AccessClassVisitor() {
			super(0);
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			return new AccessAnnotationVisitor(ClassAccess.this);
		}

		public FieldVisitor visitField(int access, String name, String desc,
				String signature, Object value) {
			return null;
		}

		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			return null;
		}

		public void visitInnerClass(String name, String outerName,
				String innerName, int access) {
		}
	}
}