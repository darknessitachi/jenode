package com.zving.framework.core.asm.objectweb;

import java.io.IOException;
import java.io.InputStream;

public class ClassReader {
	static final boolean SIGNATURES = true;
	static final boolean ANNOTATIONS = true;
	static final boolean FRAMES = true;
	static final boolean WRITER = true;
	static final boolean RESIZE = true;
	public static final int SKIP_CODE = 1;
	public static final int SKIP_DEBUG = 2;
	public static final int SKIP_FRAMES = 4;
	public static final int EXPAND_FRAMES = 8;
	public final byte[] b;
	private final int[] items;
	private final String[] strings;
	private final int maxStringLength;
	public final int header;

	public ClassReader(byte[] b) {
		this(b, 0, b.length);
	}

	public ClassReader(byte[] b, int off, int len) {
		this.b = b;

		if (readShort(off + 6) > 51) {
			throw new IllegalArgumentException();
		}

		this.items = new int[readUnsignedShort(off + 8)];
		int n = this.items.length;
		this.strings = new String[n];
		int max = 0;
		int index = off + 10;
		for (int i = 1; i < n; i++) {
			this.items[i] = (index + 1);
			int size;
			switch (b[index]) {
			case 3:
			case 4:
			case 9:
			case 10:
			case 11:
			case 12:
			case 18:
				size = 5;
				break;
			case 5:
			case 6:
				 size = 9;
				i++;
				break;
			case 1:
				 size = 3 + readUnsignedShort(index + 1);
				if (size > max) {
					max = size;
				}
				break;
			case 15:
				size = 4;
				break;
			case 2:
			case 7:
			case 8:
			case 13:
			case 14:
			case 16:
			case 17:
			default:
				size = 3;
			}

			index = index + size;
		}
		this.maxStringLength = max;

		this.header = index;
	}

	public int getAccess() {
		return readUnsignedShort(this.header);
	}

	public String getClassName() {
		return readClass(this.header + 2, new char[this.maxStringLength]);
	}

	public String getSuperName() {
		return readClass(this.header + 4, new char[this.maxStringLength]);
	}

	public String[] getInterfaces() {
		int index = this.header + 6;
		int n = readUnsignedShort(index);
		String[] interfaces = new String[n];
		if (n > 0) {
			char[] buf = new char[this.maxStringLength];
			for (int i = 0; i < n; i++) {
				index += 2;
				interfaces[i] = readClass(index, buf);
			}
		}
		return interfaces;
	}

	void copyPool(ClassWriter classWriter)
  {
    char[] buf = new char[this.maxStringLength];
    int ll = this.items.length;
    Item[] items2 = new Item[ll];
    for (int i = 1; i < ll; i++) {
      int index = this.items[i];
      int tag = this.b[(index - 1)];
      Item item = new Item(i);

      switch (tag) {
      case 9:
      case 10:
      case 11:
        int nameType = this.items[readUnsignedShort(index + 2)];
        item.set(tag, readClass(index, buf), readUTF8(nameType, buf), 
          readUTF8(nameType + 2, buf));
        break;
      case 3:
        item.set(readInt(index));
        break;
      case 4:
        item.set(Float.intBitsToFloat(readInt(index)));
        break;
      case 12:
        item.set(tag, readUTF8(index, buf), readUTF8(index + 2, buf), 
          null);
        break;
      case 5:
        item.set(readLong(index));
        i++;
        break;
      case 6:
        item.set(Double.longBitsToDouble(readLong(index)));
        i++;
        break;
      case 1:
        String s = this.strings[i];
        if (s == null) {
          index = this.items[i];
//          s = this.strings[i] =  = readUTF(index + 2, 
//            readUnsignedShort(index), buf);
        }
        item.set(tag, s, null, null);
        break;
      case 15:
        int fieldOrMethodRef = this.items[readUnsignedShort(index + 1)];
//        int nameType = this.items[readUnsignedShort(fieldOrMethodRef + 2)];
//        item.set(20 + readByte(index), 
//          readClass(fieldOrMethodRef, buf), 
//          readUTF8(nameType, buf), readUTF8(nameType + 2, buf));
        break;
      case 18:
        if (classWriter.bootstrapMethods == null) {
          copyBootstrapMethods(classWriter, items2, buf);
        }
//        int nameType = this.items[readUnsignedShort(index + 2)];
//        item.set(readUTF8(nameType, buf), readUTF8(nameType + 2, buf), 
//          readUnsignedShort(index));
        break;
      case 2:
      case 7:
      case 8:
      case 13:
      case 14:
      case 16:
      case 17:
      default:
        item.set(tag, readUTF8(index, buf), null, null);
      }

      int index2 = item.hashCode % 
        items2.length;
      item.next = items2[index2];
      items2[index2] = item;
    }

    int off = this.items[1] - 1;
    classWriter.pool.putByteArray(this.b, off, this.header - off);
    classWriter.items = items2;
    classWriter.threshold = ((int)(0.75D * ll));
    classWriter.index = ll;
  }

	private void copyBootstrapMethods(ClassWriter classWriter, Item[] items,
			char[] c) {
		int u = getAttributes();
		boolean found = false;
		for (int i = readUnsignedShort(u); i > 0; i--) {
			String attrName = readUTF8(u + 2, c);
			if ("BootstrapMethods".equals(attrName)) {
				found = true;
				break;
			}
			u += 6 + readInt(u + 4);
		}
		if (!found) {
			return;
		}

		int boostrapMethodCount = readUnsignedShort(u + 8);
		int j = 0;
		for (int v = u + 10; j < boostrapMethodCount; j++) {
			int position = v - u - 10;
			int hashCode = readConst(readUnsignedShort(v), c).hashCode();
			for (int k = readUnsignedShort(v + 2); k > 0; k--) {
				hashCode ^= readConst(readUnsignedShort(v + 4), c).hashCode();
				v += 2;
			}
			v += 4;
			Item item = new Item(j);
			item.set(position, hashCode & 0x7FFFFFFF);
			int index = item.hashCode % items.length;
			item.next = items[index];
			items[index] = item;
		}
		int attrSize = readInt(u + 4);
		ByteVector bootstrapMethods = new ByteVector(attrSize + 62);
		bootstrapMethods.putByteArray(this.b, u + 10, attrSize - 2);
		classWriter.bootstrapMethodsCount = boostrapMethodCount;
		classWriter.bootstrapMethods = bootstrapMethods;
	}

	public ClassReader(InputStream is) throws IOException {
		this(readClass(is, false));
	}

	public ClassReader(String name) throws IOException {
		this(readClass(
				ClassLoader.getSystemResourceAsStream(name.replace('.', '/')
						+ ".class"), true));
	}

	private static byte[] readClass(InputStream is, boolean close)
			throws IOException {
		if (is == null)
			throw new IOException("Class not found");
		try {
			byte[] b = new byte[is.available()];
			int len = 0;
			while (true) {
				int n = is.read(b, len, b.length - len);
				byte[] arrayOfByte1;
				if (n == -1) {
					if (len < b.length) {
						byte[] c = new byte[len];
						System.arraycopy(b, 0, c, 0, len);
						b = c;
					}
					return b;
				}
				len += n;
				if (len == b.length) {
					int last = is.read();
					if (last < 0)
						return b;
					byte[] c = new byte[b.length + 1000];
					System.arraycopy(b, 0, c, 0, len);
					c[(len++)] = ((byte) last);
//					byte[] b = c;
				}
			}
		} finally {
			if (close)
				is.close();
		}
	}

	public void accept(ClassVisitor classVisitor, int flags) {
		accept(classVisitor, new Attribute[0], flags);
	}

	public void accept(ClassVisitor classVisitor, Attribute[] attrs, int flags) {
		int u = this.header;
		char[] c = new char[this.maxStringLength];

		Context context = new Context();
		context.attrs = attrs;
		context.flags = flags;
		context.buffer = c;

		int access = readUnsignedShort(u);
		String name = readClass(u + 2, c);
		String superClass = readClass(u + 4, c);
		String[] interfaces = new String[readUnsignedShort(u + 6)];
		u += 8;
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = readClass(u, c);
			u += 2;
		}

		String signature = null;
		String sourceFile = null;
		String sourceDebug = null;
		String enclosingOwner = null;
		String enclosingName = null;
		String enclosingDesc = null;
		int anns = 0;
		int ianns = 0;
		int innerClasses = 0;
		Attribute attributes = null;

		u = getAttributes();
		for (int i = readUnsignedShort(u); i > 0; i--) {
			String attrName = readUTF8(u + 2, c);

			if ("SourceFile".equals(attrName)) {
				sourceFile = readUTF8(u + 8, c);
			} else if ("InnerClasses".equals(attrName)) {
				innerClasses = u + 8;
			} else if ("EnclosingMethod".equals(attrName)) {
				enclosingOwner = readClass(u + 8, c);
				int item = readUnsignedShort(u + 10);
				if (item != 0) {
					enclosingName = readUTF8(this.items[item], c);
					enclosingDesc = readUTF8(this.items[item] + 2, c);
				}
			} else if ("Signature".equals(attrName)) {
				signature = readUTF8(u + 8, c);
			} else if ("RuntimeVisibleAnnotations".equals(attrName)) {
				anns = u + 8;
			} else if ("Deprecated".equals(attrName)) {
				access |= 131072;
			} else if ("Synthetic".equals(attrName)) {
				access |= 266240;
			} else if ("SourceDebugExtension".equals(attrName)) {
				int len = readInt(u + 4);
				sourceDebug = readUTF(u + 8, len, new char[len]);
			} else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
				ianns = u + 8;
			} else if ("BootstrapMethods".equals(attrName)) {
				int[] bootstrapMethods = new int[readUnsignedShort(u + 8)];
				int j = 0;
				for (int v = u + 10; j < bootstrapMethods.length; j++) {
					bootstrapMethods[j] = v;
					v += (2 + readUnsignedShort(v + 2) << 1);
				}
				context.bootstrapMethods = bootstrapMethods;
			} else {
				Attribute attr = readAttribute(attrs, attrName, u + 8,
						readInt(u + 4), c, -1, null);
				if (attr != null) {
					attr.next = attributes;
					attributes = attr;
				}
			}
			u += 6 + readInt(u + 4);
		}

		classVisitor.visit(readInt(this.items[1] - 7), access, name, signature,
				superClass, interfaces);

		if (((flags & 0x2) == 0)
				&& ((sourceFile != null) || (sourceDebug != null))) {
			classVisitor.visitSource(sourceFile, sourceDebug);
		}

		if (enclosingOwner != null) {
			classVisitor.visitOuterClass(enclosingOwner, enclosingName,
					enclosingDesc);
		}

		if (anns != 0) {
			int i = readUnsignedShort(anns);
			for (int v = anns + 2; i > 0; i--) {
				v = readAnnotationValues(v + 2, c, true,
						classVisitor.visitAnnotation(readUTF8(v, c), true));
			}
		}
		if (ianns != 0) {
			int i = readUnsignedShort(ianns);
			for (int v = ianns + 2; i > 0; i--) {
				v = readAnnotationValues(v + 2, c, true,
						classVisitor.visitAnnotation(readUTF8(v, c), false));
			}

		}

		while (attributes != null) {
			Attribute attr = attributes.next;
			attributes.next = null;
			classVisitor.visitAttribute(attributes);
			attributes = attr;
		}

		if (innerClasses != 0) {
			int v = innerClasses + 2;
			for (int i = readUnsignedShort(innerClasses); i > 0; i--) {
				classVisitor.visitInnerClass(readClass(v, c),
						readClass(v + 2, c), readUTF8(v + 4, c),
						readUnsignedShort(v + 6));
				v += 8;
			}

		}

		u = this.header + 10 + 2 * interfaces.length;
		for (int i = readUnsignedShort(u - 2); i > 0; i--) {
			u = readField(classVisitor, context, u);
		}
		u += 2;
		for (int i = readUnsignedShort(u - 2); i > 0; i--) {
			u = readMethod(classVisitor, context, u);
		}

		classVisitor.visitEnd();
	}

	private int readField(ClassVisitor classVisitor, Context context, int u) {
		char[] c = context.buffer;
		int access = readUnsignedShort(u);
		String name = readUTF8(u + 2, c);
		String desc = readUTF8(u + 4, c);
		u += 6;

		String signature = null;
		int anns = 0;
		int ianns = 0;
		Object value = null;
		Attribute attributes = null;

		for (int i = readUnsignedShort(u); i > 0; i--) {
			String attrName = readUTF8(u + 2, c);

			if ("ConstantValue".equals(attrName)) {
				int item = readUnsignedShort(u + 8);
				value = item == 0 ? null : readConst(item, c);
			} else if ("Signature".equals(attrName)) {
				signature = readUTF8(u + 8, c);
			} else if ("Deprecated".equals(attrName)) {
				access |= 131072;
			} else if ("Synthetic".equals(attrName)) {
				access |= 266240;
			} else if ("RuntimeVisibleAnnotations".equals(attrName)) {
				anns = u + 8;
			} else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
				ianns = u + 8;
			} else {
				Attribute attr = readAttribute(context.attrs, attrName, u + 8,
						readInt(u + 4), c, -1, null);
				if (attr != null) {
					attr.next = attributes;
					attributes = attr;
				}
			}
			u += 6 + readInt(u + 4);
		}
		u += 2;

		FieldVisitor fv = classVisitor.visitField(access, name, desc,
				signature, value);
		if (fv == null) {
			return u;
		}

		if (anns != 0) {
			int i = readUnsignedShort(anns);
			for (int v = anns + 2; i > 0; i--) {
				v = readAnnotationValues(v + 2, c, true,
						fv.visitAnnotation(readUTF8(v, c), true));
			}
		}
		if (ianns != 0) {
			int i = readUnsignedShort(ianns);
			for (int v = ianns + 2; i > 0; i--) {
				v = readAnnotationValues(v + 2, c, true,
						fv.visitAnnotation(readUTF8(v, c), false));
			}

		}

		while (attributes != null) {
			Attribute attr = attributes.next;
			attributes.next = null;
			fv.visitAttribute(attributes);
			attributes = attr;
		}

		fv.visitEnd();

		return u;
	}

	private int readMethod(ClassVisitor classVisitor, Context context, int u) {
		char[] c = context.buffer;
		int access = readUnsignedShort(u);
		String name = readUTF8(u + 2, c);
		String desc = readUTF8(u + 4, c);
		u += 6;

		int code = 0;
		int exception = 0;
		String[] exceptions = (String[]) null;
		String signature = null;
		int anns = 0;
		int ianns = 0;
		int dann = 0;
		int mpanns = 0;
		int impanns = 0;
		int firstAttribute = u;
		Attribute attributes = null;

		for (int i = readUnsignedShort(u); i > 0; i--) {
			String attrName = readUTF8(u + 2, c);

			if ("Code".equals(attrName)) {
				if ((context.flags & 0x1) == 0)
					code = u + 8;
			} else if ("Exceptions".equals(attrName)) {
				exceptions = new String[readUnsignedShort(u + 8)];
				exception = u + 10;
				for (int j = 0; j < exceptions.length; j++) {
					exceptions[j] = readClass(exception, c);
					exception += 2;
				}
			} else if ("Signature".equals(attrName)) {
				signature = readUTF8(u + 8, c);
			} else if ("Deprecated".equals(attrName)) {
				access |= 131072;
			} else if ("RuntimeVisibleAnnotations".equals(attrName)) {
				anns = u + 8;
			} else if ("AnnotationDefault".equals(attrName)) {
				dann = u + 8;
			} else if ("Synthetic".equals(attrName)) {
				access |= 266240;
			} else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
				ianns = u + 8;
			} else if ("RuntimeVisibleParameterAnnotations".equals(attrName)) {
				mpanns = u + 8;
			} else if ("RuntimeInvisibleParameterAnnotations".equals(attrName)) {
				impanns = u + 8;
			} else {
				Attribute attr = readAttribute(context.attrs, attrName, u + 8,
						readInt(u + 4), c, -1, null);
				if (attr != null) {
					attr.next = attributes;
					attributes = attr;
				}
			}
			u += 6 + readInt(u + 4);
		}
		u += 2;

		MethodVisitor mv = classVisitor.visitMethod(access, name, desc,
				signature, exceptions);
		if (mv == null) {
			return u;
		}

		if ((mv instanceof MethodWriter)) {
			MethodWriter mw = (MethodWriter) mv;
			if ((mw.cw.cr == this) && (signature == mw.signature)) {
				boolean sameExceptions = false;
				if (exceptions == null) {
					sameExceptions = mw.exceptionCount == 0;
				} else if (exceptions.length == mw.exceptionCount) {
					sameExceptions = true;
					for (int j = exceptions.length - 1; j >= 0; j--) {
						exception -= 2;
						if (mw.exceptions[j] != readUnsignedShort(exception)) {
							sameExceptions = false;
							break;
						}
					}
				}
				if (sameExceptions) {
					mw.classReaderOffset = firstAttribute;
					mw.classReaderLength = (u - firstAttribute);
					return u;
				}
			}

		}

		if (dann != 0) {
			AnnotationVisitor dv = mv.visitAnnotationDefault();
			readAnnotationValue(dann, c, null, dv);
			if (dv != null) {
				dv.visitEnd();
			}
		}
		if (anns != 0) {
			int i = readUnsignedShort(anns);
			for (int v = anns + 2; i > 0; i--) {
				v = readAnnotationValues(v + 2, c, true,
						mv.visitAnnotation(readUTF8(v, c), true));
			}
		}
		if (ianns != 0) {
			int i = readUnsignedShort(ianns);
			for (int v = ianns + 2; i > 0; i--) {
				v = readAnnotationValues(v + 2, c, true,
						mv.visitAnnotation(readUTF8(v, c), false));
			}
		}
		if (mpanns != 0) {
			readParameterAnnotations(mpanns, desc, c, true, mv);
		}
		if (impanns != 0) {
			readParameterAnnotations(impanns, desc, c, false, mv);
		}

		while (attributes != null) {
			Attribute attr = attributes.next;
			attributes.next = null;
			mv.visitAttribute(attributes);
			attributes = attr;
		}

		if (code != 0) {
			context.access = access;
			context.name = name;
			context.desc = desc;
			mv.visitCode();
			readCode(mv, context, code);
		}

		mv.visitEnd();

		return u;
	}

	private void readCode(MethodVisitor mv, Context context, int u) {
		byte[] b = this.b;
		char[] c = context.buffer;
		int maxStack = readUnsignedShort(u);
		int maxLocals = readUnsignedShort(u + 2);
		int codeLength = readInt(u + 4);
		u += 8;

		int codeStart = u;
		int codeEnd = u + codeLength;
		Label[] labels = new Label[codeLength + 2];
		readLabel(codeLength + 1, labels);
		while (u < codeEnd) {
			int offset = u - codeStart;
			int opcode = b[u] & 0xFF;
			switch (ClassWriter.TYPE[opcode]) {
			case 0:
			case 4:
				u++;
				break;
			case 9:
				readLabel(offset + readShort(u + 1), labels);
				u += 3;
				break;
			case 10:
				readLabel(offset + readInt(u + 1), labels);
				u += 5;
				break;
			case 17:
				opcode = b[(u + 1)] & 0xFF;
				if (opcode == 132)
					u += 6;
				else {
					u += 4;
				}
				break;
			case 14:
				u = u + 4 - (offset & 0x3);

				readLabel(offset + readInt(u), labels);
				for (int i = readInt(u + 8) - readInt(u + 4) + 1; i > 0; i--) {
					readLabel(offset + readInt(u + 12), labels);
					u += 4;
				}
				u += 12;
				break;
			case 15:
				u = u + 4 - (offset & 0x3);

				readLabel(offset + readInt(u), labels);
				for (int i = readInt(u + 4); i > 0; i--) {
					readLabel(offset + readInt(u + 12), labels);
					u += 8;
				}
				u += 8;
				break;
			case 1:
			case 3:
			case 11:
				u += 2;
				break;
			case 2:
			case 5:
			case 6:
			case 12:
			case 13:
				u += 3;
				break;
			case 7:
			case 8:
				u += 5;
				break;
			case 16:
			default:
				u += 4;
			}

		}

		for (int i = readUnsignedShort(u); i > 0; i--) {
			Label start = readLabel(readUnsignedShort(u + 2), labels);
			Label end = readLabel(readUnsignedShort(u + 4), labels);
			Label handler = readLabel(readUnsignedShort(u + 6), labels);
			String type = readUTF8(this.items[readUnsignedShort(u + 8)], c);
			mv.visitTryCatchBlock(start, end, handler, type);
			u += 8;
		}
		u += 2;

		int varTable = 0;
		int varTypeTable = 0;
		boolean zip = true;
		boolean unzip = (context.flags & 0x8) != 0;
		int stackMap = 0;
		int stackMapSize = 0;
		int frameCount = 0;
		Context frame = null;
		Attribute attributes = null;

		for (int i = readUnsignedShort(u); i > 0; i--) {
			String attrName = readUTF8(u + 2, c);
			if ("LocalVariableTable".equals(attrName)) {
				if ((context.flags & 0x2) == 0) {
					varTable = u + 8;
					int j = readUnsignedShort(u + 8);
					for (int v = u; j > 0; j--) {
						int label = readUnsignedShort(v + 10);
						if (labels[label] == null) {
							readLabel(label, labels).status |= 1;
						}
						label += readUnsignedShort(v + 12);
						if (labels[label] == null) {
							readLabel(label, labels).status |= 1;
						}
						v += 10;
					}
				}
			} else if ("LocalVariableTypeTable".equals(attrName))
				varTypeTable = u + 8;
			else if ("LineNumberTable".equals(attrName)) {
				if ((context.flags & 0x2) == 0) {
					int j = readUnsignedShort(u + 8);
					for (int v = u; j > 0; j--) {
						int label = readUnsignedShort(v + 10);
						if (labels[label] == null) {
							readLabel(label, labels).status |= 1;
						}
						labels[label].line = readUnsignedShort(v + 12);
						v += 4;
					}
				}
			} else if ("StackMapTable".equals(attrName)) {
				if ((context.flags & 0x4) == 0) {
					stackMap = u + 10;
					stackMapSize = readInt(u + 4);
					frameCount = readUnsignedShort(u + 8);
				}

			} else if ("StackMap".equals(attrName)) {
				if ((context.flags & 0x4) == 0) {
					zip = false;
					stackMap = u + 10;
					stackMapSize = readInt(u + 4);
					frameCount = readUnsignedShort(u + 8);
				}

			} else {
				for (int j = 0; j < context.attrs.length; j++) {
					if (context.attrs[j].type.equals(attrName)) {
						Attribute attr = context.attrs[j].read(this, u + 8,
								readInt(u + 4), c, codeStart - 8, labels);
						if (attr != null) {
							attr.next = attributes;
							attributes = attr;
						}
					}
				}
			}
			u += 6 + readInt(u + 4);
		}
		u += 2;

		if (stackMap != 0) {
			frame = context;
			frame.offset = -1;
			frame.mode = 0;
			frame.localCount = 0;
			frame.localDiff = 0;
			frame.stackCount = 0;
			frame.local = new Object[maxLocals];
			frame.stack = new Object[maxStack];
			if (unzip) {
				getImplicitFrame(context);
			}

			for (int i = stackMap; i < stackMap + stackMapSize - 2; i++) {
				if (b[i] == 8) {
					int v = readUnsignedShort(i + 1);
					if ((v >= 0) && (v < codeLength)
							&& ((b[(codeStart + v)] & 0xFF) == 187)) {
						readLabel(v, labels);
					}
				}

			}

		}

		u = codeStart;
		while (u < codeEnd) {
			int offset = u - codeStart;

			Label l = labels[offset];
			if (l != null) {
				mv.visitLabel(l);
				if (((context.flags & 0x2) == 0) && (l.line > 0)) {
					mv.visitLineNumber(l.line, l);
				}

			}

			while ((frame != null)
					&& ((frame.offset == offset) || (frame.offset == -1))) {
				if (frame.offset != -1) {
					if ((!zip) || (unzip))
						mv.visitFrame(-1, frame.localCount, frame.local,
								frame.stackCount, frame.stack);
					else {
						mv.visitFrame(frame.mode, frame.localDiff, frame.local,
								frame.stackCount, frame.stack);
					}
				}
				if (frameCount > 0) {
					stackMap = readFrame(stackMap, zip, unzip, labels, frame);
					frameCount--;
				} else {
					frame = null;
				}

			}

			int opcode = b[u] & 0xFF;
			switch (ClassWriter.TYPE[opcode]) {
			case 0:
				mv.visitInsn(opcode);
				u++;
				break;
			case 4:
				if (opcode > 54) {
					opcode -= 59;
					mv.visitVarInsn(54 + (opcode >> 2), opcode & 0x3);
				} else {
					opcode -= 26;
					mv.visitVarInsn(21 + (opcode >> 2), opcode & 0x3);
				}
				u++;
				break;
			case 9:
				mv.visitJumpInsn(opcode, labels[(offset + readShort(u + 1))]);
				u += 3;
				break;
			case 10:
				mv.visitJumpInsn(opcode - 33, labels[(offset + readInt(u + 1))]);
				u += 5;
				break;
			case 17:
				opcode = b[(u + 1)] & 0xFF;
				if (opcode == 132) {
					mv.visitIincInsn(readUnsignedShort(u + 2), readShort(u + 4));
					u += 6;
				} else {
					mv.visitVarInsn(opcode, readUnsignedShort(u + 2));
					u += 4;
				}
				break;
			case 14:
				u = u + 4 - (offset & 0x3);

				int label = offset + readInt(u);
				int min = readInt(u + 4);
				int max = readInt(u + 8);
				Label[] table = new Label[max - min + 1];
				u += 12;
				for (int i = 0; i < table.length; i++) {
					table[i] = labels[(offset + readInt(u))];
					u += 4;
				}
				mv.visitTableSwitchInsn(min, max, labels[label], table);
				break;
			case 15:
				u = u + 4 - (offset & 0x3);

				label = offset + readInt(u);
				int len = readInt(u + 4);
				int[] keys = new int[len];
				Label[] values = new Label[len];
				u += 8;
				for (int i = 0; i < len; i++) {
					keys[i] = readInt(u);
					values[i] = labels[(offset + readInt(u + 4))];
					u += 8;
				}
				mv.visitLookupSwitchInsn(labels[label], keys, values);
				break;
			case 3:
				mv.visitVarInsn(opcode, b[(u + 1)] & 0xFF);
				u += 2;
				break;
			case 1:
				mv.visitIntInsn(opcode, b[(u + 1)]);
				u += 2;
				break;
			case 2:
				mv.visitIntInsn(opcode, readShort(u + 1));
				u += 3;
				break;
			case 11:
				mv.visitLdcInsn(readConst(b[(u + 1)] & 0xFF, c));
				u += 2;
				break;
			case 12:
				mv.visitLdcInsn(readConst(readUnsignedShort(u + 1), c));
				u += 3;
				break;
			case 6:
			case 7:
				int cpIndex = this.items[readUnsignedShort(u + 1)];
				String iowner = readClass(cpIndex, c);
				cpIndex = this.items[readUnsignedShort(cpIndex + 2)];
				String iname = readUTF8(cpIndex, c);
				String idesc = readUTF8(cpIndex + 2, c);
				if (opcode < 182)
					mv.visitFieldInsn(opcode, iowner, iname, idesc);
				else {
					mv.visitMethodInsn(opcode, iowner, iname, idesc);
				}
				if (opcode == 185)
					u += 5;
				else {
					u += 3;
				}
				break;
			case 8:
				cpIndex = this.items[readUnsignedShort(u + 1)];
				int bsmIndex = context.bootstrapMethods[readUnsignedShort(cpIndex)];
				Handle bsm = (Handle) readConst(readUnsignedShort(bsmIndex), c);
				int bsmArgCount = readUnsignedShort(bsmIndex + 2);
				Object[] bsmArgs = new Object[bsmArgCount];
				bsmIndex += 4;
				for (int i = 0; i < bsmArgCount; i++) {
					bsmArgs[i] = readConst(readUnsignedShort(bsmIndex), c);
					bsmIndex += 2;
				}
				cpIndex = this.items[readUnsignedShort(cpIndex + 2)];
				 iname = readUTF8(cpIndex, c);
				 idesc = readUTF8(cpIndex + 2, c);
				mv.visitInvokeDynamicInsn(iname, idesc, bsm, bsmArgs);
				u += 5;
				break;
			case 5:
				mv.visitTypeInsn(opcode, readClass(u + 1, c));
				u += 3;
				break;
			case 13:
				mv.visitIincInsn(b[(u + 1)] & 0xFF, b[(u + 2)]);
				u += 3;
				break;
			case 16:
			default:
				mv.visitMultiANewArrayInsn(readClass(u + 1, c),
						b[(u + 3)] & 0xFF);
				u += 4;
			}
		}

		if (labels[codeLength] != null) {
			mv.visitLabel(labels[codeLength]);
		}

		if (((context.flags & 0x2) == 0) && (varTable != 0)) {
			int[] typeTable = (int[]) null;
			if (varTypeTable != 0) {
				u = varTypeTable + 2;
				typeTable = new int[readUnsignedShort(varTypeTable) * 3];
				for (int i = typeTable.length; i > 0;) {
					typeTable[(--i)] = (u + 6);
					typeTable[(--i)] = readUnsignedShort(u + 8);
					typeTable[(--i)] = readUnsignedShort(u);
					u += 10;
				}
			}
			u = varTable + 2;
			for (int i = readUnsignedShort(varTable); i > 0; i--) {
				int start = readUnsignedShort(u);
				int length = readUnsignedShort(u + 2);
				int index = readUnsignedShort(u + 8);
				String vsignature = null;
				if (typeTable != null) {
					for (int j = 0; j < typeTable.length; j += 3) {
						if ((typeTable[j] == start)
								&& (typeTable[(j + 1)] == index)) {
							vsignature = readUTF8(typeTable[(j + 2)], c);
							break;
						}
					}
				}
				mv.visitLocalVariable(readUTF8(u + 4, c), readUTF8(u + 6, c),
						vsignature, labels[start], labels[(start + length)],
						index);
				u += 10;
			}

		}

		while (attributes != null) {
			Attribute attr = attributes.next;
			attributes.next = null;
			mv.visitAttribute(attributes);
			attributes = attr;
		}

		mv.visitMaxs(maxStack, maxLocals);
	}

	private void readParameterAnnotations(int v, String desc, char[] buf,
			boolean visible, MethodVisitor mv) {
		int n = this.b[(v++)] & 0xFF;

		int synthetics = Type.getArgumentTypes(desc).length - n;

		for (int i = 0; i < synthetics; i++) {
			AnnotationVisitor av = mv.visitParameterAnnotation(i,
					"Ljava/lang/Synthetic;", false);
			if (av != null) {
				av.visitEnd();
			}
		}
		for (int i=0; i < n + synthetics; i++) {
			int j = readUnsignedShort(v);
			v += 2;
			for (; j > 0; j--) {
				AnnotationVisitor av = mv.visitParameterAnnotation(i,
						readUTF8(v, buf), visible);
				v = readAnnotationValues(v + 2, buf, true, av);
			}
		}
	}

	private int readAnnotationValues(int v, char[] buf, boolean named,
			AnnotationVisitor av) {
		int i = readUnsignedShort(v);
		v += 2;
		if (named) {
			for (; i > 0; i--)
				v = readAnnotationValue(v + 2, buf, readUTF8(v, buf), av);
		} else {
			for (; i > 0; i--) {
				v = readAnnotationValue(v, buf, null, av);
			}
		}
		if (av != null) {
			av.visitEnd();
		}
		return v;
	}

	private int readAnnotationValue(int v, char[] buf, String name,
			AnnotationVisitor av) {
		if (av == null) {
			switch (this.b[v] & 0xFF) {
			case 101:
				return v + 5;
			case 64:
				return readAnnotationValues(v + 3, buf, true, null);
			case 91:
				return readAnnotationValues(v + 1, buf, false, null);
			}
			return v + 3;
		}

		switch (this.b[(v++)] & 0xFF) {
		case 68:
		case 70:
		case 73:
		case 74:
			av.visit(name, readConst(readUnsignedShort(v), buf));
			v += 2;
			break;
		case 66:
			av.visit(name, new Byte(
					(byte) readInt(this.items[readUnsignedShort(v)])));
			v += 2;
			break;
		case 90:
			av.visit(
					name,
					readInt(this.items[readUnsignedShort(v)]) == 0 ? Boolean.FALSE
							: Boolean.TRUE);
			v += 2;
			break;
		case 83:
			av.visit(name, new Short(
					(short) readInt(this.items[readUnsignedShort(v)])));
			v += 2;
			break;
		case 67:
			av.visit(name, new Character(
					(char) readInt(this.items[readUnsignedShort(v)])));
			v += 2;
			break;
		case 115:
			av.visit(name, readUTF8(v, buf));
			v += 2;
			break;
		case 101:
			av.visitEnum(name, readUTF8(v, buf), readUTF8(v + 2, buf));
			v += 4;
			break;
		case 99:
			av.visit(name, Type.getType(readUTF8(v, buf)));
			v += 2;
			break;
		case 64:
			v = readAnnotationValues(v + 2, buf, true,
					av.visitAnnotation(name, readUTF8(v, buf)));
			break;
		case 91:
			int size = readUnsignedShort(v);
			v += 2;
			if (size == 0) {
				return readAnnotationValues(v - 2, buf, false,
						av.visitArray(name));
			}
			switch (this.b[(v++)] & 0xFF) {
			case 66:
				byte[] bv = new byte[size];
				for (int i = 0; i < size; i++) {
					bv[i] = ((byte) readInt(this.items[readUnsignedShort(v)]));
					v += 3;
				}
				av.visit(name, bv);
				v--;
				break;
			case 90:
				boolean[] zv = new boolean[size];
				for (int i = 0; i < size; i++) {
//					zv[i] = (readInt(this.items[readUnsignedShort(v)]) != 0 ? 1
//							: false);
					v += 3;
				}
				av.visit(name, zv);
				v--;
				break;
			case 83:
				short[] sv = new short[size];
				for (int i = 0; i < size; i++) {
					sv[i] = ((short) readInt(this.items[readUnsignedShort(v)]));
					v += 3;
				}
				av.visit(name, sv);
				v--;
				break;
			case 67:
				char[] cv = new char[size];
				for (int i = 0; i < size; i++) {
					cv[i] = ((char) readInt(this.items[readUnsignedShort(v)]));
					v += 3;
				}
				av.visit(name, cv);
				v--;
				break;
			case 73:
				int[] iv = new int[size];
				for (int i = 0; i < size; i++) {
					iv[i] = readInt(this.items[readUnsignedShort(v)]);
					v += 3;
				}
				av.visit(name, iv);
				v--;
				break;
			case 74:
				long[] lv = new long[size];
				for (int i = 0; i < size; i++) {
					lv[i] = readLong(this.items[readUnsignedShort(v)]);
					v += 3;
				}
				av.visit(name, lv);
				v--;
				break;
			case 70:
				float[] fv = new float[size];
				for (int i = 0; i < size; i++) {
					fv[i] = Float
							.intBitsToFloat(readInt(this.items[readUnsignedShort(v)]));
					v += 3;
				}
				av.visit(name, fv);
				v--;
				break;
			case 68:
				double[] dv = new double[size];
				for (int i = 0; i < size; i++) {
					dv[i] = Double
							.longBitsToDouble(readLong(this.items[readUnsignedShort(v)]));
					v += 3;
				}
				av.visit(name, dv);
				v--;
				break;
			default:
				v = readAnnotationValues(v - 3, buf, false, av.visitArray(name));
			}
			break;
		}
		return v;
	}

	private void getImplicitFrame(Context frame) {
		String desc = frame.desc;
		Object[] locals = frame.local;
		int local = 0;
		if ((frame.access & 0x8) == 0) {
			if ("<init>".equals(frame.name))
				locals[(local++)] = Opcodes.UNINITIALIZED_THIS;
			else {
				locals[(local++)] = readClass(this.header + 2, frame.buffer);
			}
		}
		int i = 1;
		while (true) {
			int j = i;
			switch (desc.charAt(i++)) {
			case 'B':
			case 'C':
			case 'I':
			case 'S':
			case 'Z':
				locals[(local++)] = Opcodes.INTEGER;
				break;
			case 'F':
				locals[(local++)] = Opcodes.FLOAT;
				break;
			case 'J':
				locals[(local++)] = Opcodes.LONG;
				break;
			case 'D':
				locals[(local++)] = Opcodes.DOUBLE;
				break;
			case '[':
				while (desc.charAt(i) == '[') {
					i++;
				}
				if (desc.charAt(i) == 'L') {
					i++;
					while (desc.charAt(i) != ';') {
						i++;
					}
				}
				locals[(local++)] = desc.substring(j, ++i);
				break;
			case 'L':
				while (desc.charAt(i) != ';') {
					i++;
				}
				locals[(local++)] = desc.substring(j + 1, i++);
			}

		}

//		frame.localCount = local;
	}

	private int readFrame(int stackMap, boolean zip, boolean unzip,
			Label[] labels, Context frame) {
		char[] c = frame.buffer;
		int tag;
		if (zip) {
			tag = this.b[(stackMap++)] & 0xFF;
		} else {
			tag = 255;
			frame.offset = -1;
		}
		frame.localDiff = 0;
		int delta;
		if (tag < 64) {
			 delta = tag;
			frame.mode = 3;
			frame.stackCount = 0;
		} else if (tag < 128) {
			 delta = tag - 64;
			stackMap = readFrameType(frame.stack, 0, stackMap, c, labels);
			frame.mode = 4;
			frame.stackCount = 1;
		} else {
			delta = readUnsignedShort(stackMap);
			stackMap += 2;
			if (tag == 247) {
				stackMap = readFrameType(frame.stack, 0, stackMap, c, labels);
				frame.mode = 4;
				frame.stackCount = 1;
			} else if ((tag >= 248) && (tag < 251)) {
				frame.mode = 2;
				frame.localDiff = (251 - tag);
				frame.localCount -= frame.localDiff;
				frame.stackCount = 0;
			} else if (tag == 251) {
				frame.mode = 3;
				frame.stackCount = 0;
			} else if (tag < 255) {
				int local = unzip ? frame.localCount : 0;
				for (int i = tag - 251; i > 0; i--) {
					stackMap = readFrameType(frame.local, local++, stackMap, c,
							labels);
				}
				frame.mode = 1;
				frame.localDiff = (tag - 251);
				frame.localCount += frame.localDiff;
				frame.stackCount = 0;
			} else {
				frame.mode = 0;
				int n = readUnsignedShort(stackMap);
				stackMap += 2;
				frame.localDiff = n;
				frame.localCount = n;
				for (int local = 0; n > 0; n--) {
					stackMap = readFrameType(frame.local, local++, stackMap, c,
							labels);
				}
				n = readUnsignedShort(stackMap);
				stackMap += 2;
				frame.stackCount = n;
				for (int stack = 0; n > 0; n--) {
					stackMap = readFrameType(frame.stack, stack++, stackMap, c,
							labels);
				}
			}
		}
		frame.offset += delta + 1;
		readLabel(frame.offset, labels);
		return stackMap;
	}

	private int readFrameType(Object[] frame, int index, int v, char[] buf,
			Label[] labels) {
		int type = this.b[(v++)] & 0xFF;
		switch (type) {
		case 0:
			frame[index] = Opcodes.TOP;
			break;
		case 1:
			frame[index] = Opcodes.INTEGER;
			break;
		case 2:
			frame[index] = Opcodes.FLOAT;
			break;
		case 3:
			frame[index] = Opcodes.DOUBLE;
			break;
		case 4:
			frame[index] = Opcodes.LONG;
			break;
		case 5:
			frame[index] = Opcodes.NULL;
			break;
		case 6:
			frame[index] = Opcodes.UNINITIALIZED_THIS;
			break;
		case 7:
			frame[index] = readClass(v, buf);
			v += 2;
			break;
		default:
			frame[index] = readLabel(readUnsignedShort(v), labels);
			v += 2;
		}
		return v;
	}

	protected Label readLabel(int offset, Label[] labels) {
		if (labels[offset] == null) {
			labels[offset] = new Label();
		}
		return labels[offset];
	}

	private int getAttributes() {
		int u = this.header + 8 + readUnsignedShort(this.header + 6) * 2;

		for (int i = readUnsignedShort(u); i > 0; i--) {
			for (int j = readUnsignedShort(u + 8); j > 0; j--) {
				u += 6 + readInt(u + 12);
			}
			u += 8;
		}
		u += 2;
		for (int i = readUnsignedShort(u); i > 0; i--) {
			for (int j = readUnsignedShort(u + 8); j > 0; j--) {
				u += 6 + readInt(u + 12);
			}
			u += 8;
		}

		return u + 2;
	}

	private Attribute readAttribute(Attribute[] attrs, String type, int off,
			int len, char[] buf, int codeOff, Label[] labels) {
		for (int i = 0; i < attrs.length; i++) {
			if (attrs[i].type.equals(type)) {
				return attrs[i].read(this, off, len, buf, codeOff, labels);
			}
		}
		return new Attribute(type).read(this, off, len, null, -1, null);
	}

	public int getItemCount() {
		return this.items.length;
	}

	public int getItem(int item) {
		return this.items[item];
	}

	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	public int readByte(int index) {
		return this.b[index] & 0xFF;
	}

	public int readUnsignedShort(int index) {
		byte[] b = this.b;
		return (b[index] & 0xFF) << 8 | b[(index + 1)] & 0xFF;
	}

	public short readShort(int index) {
		byte[] b = this.b;
		return (short) ((b[index] & 0xFF) << 8 | b[(index + 1)] & 0xFF);
	}

	public int readInt(int index) {
		byte[] b = this.b;
		return (b[index] & 0xFF) << 24 | (b[(index + 1)] & 0xFF) << 16
				| (b[(index + 2)] & 0xFF) << 8 | b[(index + 3)] & 0xFF;
	}

	public long readLong(int index) {
		long l1 = readInt(index);
		long l0 = readInt(index + 4) & 0xFFFFFFFF;
		return l1 << 32 | l0;
	}

	public String readUTF8(int index, char[] buf)
  {
    int item = readUnsignedShort(index);
    if ((index == 0) || (item == 0)) {
      return null;
    }
    String s = this.strings[item];
    if (s != null) {
      return s;
    }
    index = this.items[item];
    this.strings[item] = readUTF(index + 2, readUnsignedShort(index), buf);
    return  this.strings[item];
  }

	private String readUTF(int index, int utfLen, char[] buf) {
		int endIndex = index + utfLen;
		byte[] b = this.b;
		int strLen = 0;

		int st = 0;
		char cc = '\000';
		while (index < endIndex) {
			int c = b[(index++)];
			switch (st) {
			case 0:
				c &= 255;
				if (c < 128) {
					buf[(strLen++)] = ((char) c);
				} else if ((c < 224) && (c > 191)) {
					cc = (char) (c & 0x1F);
					st = 1;
				} else {
					cc = (char) (c & 0xF);
					st = 2;
				}
				break;
			case 1:
				buf[(strLen++)] = ((char) (cc << '\006' | c & 0x3F));
				st = 0;
				break;
			case 2:
				cc = (char) (cc << '\006' | c & 0x3F);
				st = 1;
			}
		}

		return new String(buf, 0, strLen);
	}

	public String readClass(int index, char[] buf) {
		return readUTF8(this.items[readUnsignedShort(index)], buf);
	}

	public Object readConst(int item, char[] buf) {
		int index = this.items[item];
		switch (this.b[(index - 1)]) {
		case 3:
			return new Integer(readInt(index));
		case 4:
			return new Float(Float.intBitsToFloat(readInt(index)));
		case 5:
			return new Long(readLong(index));
		case 6:
			return new Double(Double.longBitsToDouble(readLong(index)));
		case 7:
			return Type.getObjectType(readUTF8(index, buf));
		case 8:
			return readUTF8(index, buf);
		case 16:
			return Type.getMethodType(readUTF8(index, buf));
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		}
		int tag = readByte(index);
		int[] items = this.items;
		int cpIndex = items[readUnsignedShort(index + 1)];
		String owner = readClass(cpIndex, buf);
		cpIndex = items[readUnsignedShort(cpIndex + 2)];
		String name = readUTF8(cpIndex, buf);
		String desc = readUTF8(cpIndex + 2, buf);
		return new Handle(tag, owner, name, desc);
	}
}