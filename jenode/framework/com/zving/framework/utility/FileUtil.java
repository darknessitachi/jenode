package com.zving.framework.utility;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.commons.ArrayUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

public class FileUtil {
	public static String normalizePath(String path) {
		path = path.replace('\\', '/');
		path = StringUtil.replaceEx(path, "../", "/");
		path = StringUtil.replaceEx(path, "./", "/");
		if (path.endsWith("..")) {
			path = path.substring(0, path.length() - 2);
		}
		path = path.replaceAll("/+", "/");
		return path;
	}

	public static File normalizeFile(File f) {
		String path = f.getAbsolutePath();
		path = normalizePath(path);
		return new File(path);
	}

	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index < 0) {
			return null;
		}
		String ext = fileName.substring(index + 1);
		return ext.toLowerCase();
	}

	public static boolean writeText(String fileName, String content) {
		return writeText(fileName, content, Config.getGlobalCharset());
	}

	public static boolean writeText(String fileName, String content,
			String encoding) {
		return writeText(fileName, content, encoding, false);
	}

	public static boolean writeText(String fileName, String content,
			String encoding, boolean bomFlag) {
		fileName = normalizePath(fileName);
		try {
			byte[] bs = content.getBytes(encoding);
			if ((encoding.equalsIgnoreCase("UTF-8")) && (bomFlag)) {
				bs = ArrayUtils.addAll(StringUtil.BOM, bs);
			}
			writeByte(fileName, bs);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static byte[] readByte(String fileName) {
		fileName = normalizePath(fileName);
		try {
			FileInputStream fis = new FileInputStream(fileName);
			byte[] r = new byte[fis.available()];
			fis.read(r);
			fis.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readByte(File f) {
		f = normalizeFile(f);
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] r = readByte(fis);
			fis.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readByte(InputStream is) {
		byte[] buffer = new byte[8192];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead = -1;
		try {
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			throw new RuntimeException("File.readByte() failed");
		}
		return os.toByteArray();
	}

	public static boolean writeByte(String fileName, byte[] b) {
		fileName = normalizePath(fileName);
		try {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(fileName));
			fos.write(b);
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean writeByte(File f, byte[] b) {
		f = normalizeFile(f);
		try {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(f));
			fos.write(b);
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String readText(File f) {
		return readText(f, Config.getGlobalCharset());
	}

	public static String readText(File f, String encoding) {
		f = normalizeFile(f);
		try {
			InputStream is = new FileInputStream(f);
			String str = readText(is, encoding);
			is.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readText(InputStream is, String encoding) {
		try {
			byte[] bs = readByte(is);
			if ((encoding.equalsIgnoreCase("utf-8"))
					&& (StringUtil.hexEncode(ArrayUtils.subarray(bs, 0, 3))
							.equals("efbbbf"))) {
				bs = ArrayUtils.subarray(bs, 3, bs.length);
			}

			return new String(bs, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readText(String fileName) {
		return readText(fileName, Config.getGlobalCharset());
	}

	public static String readText(String fileName, String encoding) {
		fileName = normalizePath(fileName);
		try {
			InputStream is = new FileInputStream(fileName);
			String str = readText(is, encoding);
			is.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readURLText(String urlPath) {
		return readURLText(urlPath, Config.getGlobalCharset());
	}

	public static String readURLText(String urlPath, String encoding) {
		try {
			URL url = new URL(urlPath);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), encoding));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean delete(String path) {
		File file = new File(path);
		return delete(file);
	}

	public static boolean delete(File f) {
		f = normalizeFile(f);
		if (!f.exists()) {
			LogUtil.warn("File or directory not found" + f);
			return false;
		}
		if (f.isFile()) {
			return f.delete();
		}
		return deleteDir(f);
	}

	private static boolean deleteDir(File dir) {
		dir = normalizeFile(dir);
		try {
			return (deleteFromDir(dir)) && (dir.delete());
		} catch (Exception e) {
			LogUtil.warn("Delete directory failed");
		}
		return false;
	}

	public static boolean mkdir(String path) {
		path = normalizePath(path);
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return true;
	}

	public static boolean exists(String path) {
		path = normalizePath(path);
		File dir = new File(path);
		return dir.exists();
	}

	public static boolean deleteEx(String fileName) {
		fileName = normalizePath(fileName);
		int index1 = fileName.lastIndexOf("\\");
		int index2 = fileName.lastIndexOf("/");
		index1 = index1 > index2 ? index1 : index2;
		String path = fileName.substring(0, index1);
		String name = fileName.substring(index1 + 1);
		File f = new File(path);
		if ((f.exists()) && (f.isDirectory())) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (Pattern.matches(name, files[i].getName())) {
					files[i].delete();
				}
			}
			return true;
		}
		return false;
	}

	public static boolean deleteFromDir(String dirPath) {
		dirPath = normalizePath(dirPath);
		File file = new File(dirPath);
		return deleteFromDir(file);
	}

	public static boolean deleteFromDir(File dir) {
		dir = normalizeFile(dir);
		if (!dir.exists()) {
			LogUtil.warn("Directory not found：" + dir);
			return false;
		}
		if (!dir.isDirectory()) {
			LogUtil.warn(dir + " is not directory");
			return false;
		}
		File[] tempList = dir.listFiles();
		for (int i = 0; i < tempList.length; i++) {
			if (!delete(tempList[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean copy(String oldPath, String newPath, FileFilter filter) {
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		File oldFile = new File(oldPath);
		File[] oldFiles = oldFile.listFiles(filter);
		boolean flag = true;
		if (oldFiles != null) {
			for (int i = 0; i < oldFiles.length; i++) {
				if (!copy(oldFiles[i], newPath + "/" + oldFiles[i].getName())) {
					flag = false;
				}
			}
		}
		return flag;
	}

	public static boolean copy(String oldPath, String newPath) {
		File oldFile = new File(oldPath);
		return copy(oldFile, newPath);
	}

	public static boolean copy(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		if (!oldFile.exists()) {
			LogUtil.warn("File not found:" + oldFile);
			return false;
		}
		if (oldFile.isFile()) {
			return copyFile(oldFile, newPath);
		}
		return copyDir(oldFile, newPath);
	}

	private static boolean copyFile(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);

		if (!oldFile.exists()) {
			LogUtil.warn("File not found:" + oldFile);
			return false;
		}
		if (!oldFile.isFile()) {
			LogUtil.warn(oldFile + " is not file");
			return false;
		}
		if (oldFile.getName().equalsIgnoreCase("Thumbs.db")) {
			LogUtil.warn(oldFile + " is ignored");
			return true;
		}
		mkdir(newPath.substring(0, newPath.lastIndexOf("/")));
		try {
			int byteread = 0;
			InputStream inStream = new FileInputStream(oldFile);
			File newFile = new File(newPath);

			if (newFile.isDirectory()) {
				newFile = new File(newPath, oldFile.getName());
			}
			if (newFile.getAbsoluteFile().equals(oldFile.getAbsoluteFile())) {
				return true;
			}
			FileOutputStream fs = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			fs.close();
			newFile.setLastModified(oldFile.lastModified());
			inStream.close();
		} catch (Exception e) {
			LogUtil.warn("Copy file " + oldFile.getPath() + " failed,cause:"
					+ e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean copyDir(File oldDir, String newPath) {
		oldDir = normalizeFile(oldDir);
		newPath = normalizePath(newPath);
		if (!oldDir.exists()) {
			LogUtil.info("File not found:" + oldDir);
			return false;
		}
		if (!oldDir.isDirectory()) {
			LogUtil.info(oldDir + " is not directory");
			return false;
		}
		try {
			new File(newPath).mkdirs();
			File[] files = oldDir.listFiles();
			File temp = null;
			for (int i = 0; i < files.length; i++) {
				temp = files[i];
				if (temp.isFile()) {
					if (!copyFile(temp, newPath + "/" + temp.getName()))
						return false;
				} else if ((temp.isDirectory())
						&& (!copyDir(temp, newPath + "/" + temp.getName()))) {
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			LogUtil.info("Copy directory failed,cause:" + e.getMessage());
		}
		return false;
	}

	public static boolean move(String oldPath, String newPath) {
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		return (copy(oldPath, newPath)) && (delete(oldPath));
	}

	public static boolean move(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		return (copy(oldFile, newPath)) && (delete(oldFile));
	}

	public static void serialize(Serializable obj, String fileName) {
		fileName = normalizePath(fileName);
		try {
			FileOutputStream f = new FileOutputStream(fileName);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(obj);
			s.flush();
			s.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] serialize(Serializable obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream s = new ObjectOutputStream(b);
			s.writeObject(obj);
			s.flush();
			s.close();
			return b.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object unserialize(String fileName) {
		fileName = normalizePath(fileName);
		try {
			FileInputStream in = new FileInputStream(fileName);
			ObjectInputStream s = new ObjectInputStream(in);
			Object o = s.readObject();
			s.close();
			return o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object unserialize(byte[] bs) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			ObjectInputStream s = new ObjectInputStream(in);
			Object o = s.readObject();
			s.close();
			return o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] mapToBytes(Mapx<?, ?> map) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			for (Iterator localIterator = map.keySet().iterator(); localIterator
					.hasNext();) {
				Object k = localIterator.next();
				if (k != null) {
					Object v = map.get(k);
					if (v == null)
						bos.write(new byte[1]);
					else if ((v instanceof String))
						bos.write(new byte[] { 1 });
					else if ((v instanceof Long))
						bos.write(new byte[] { 2 });
					else if ((v instanceof Integer))
						bos.write(new byte[] { 3 });
					else if ((v instanceof Boolean))
						bos.write(new byte[] { 4 });
					else if ((v instanceof Date))
						bos.write(new byte[] { 5 });
					else if ((v instanceof Mapx))
						bos.write(new byte[] { 6 });
					else if ((v instanceof Serializable))
						bos.write(new byte[] { 7 });
					else {
						throw new RuntimeException("Unknown datatype:"
								+ v.getClass().getName());
					}
					byte[] bs = k.toString().getBytes();
					bos.write(NumberUtil.toBytes(bs.length));
					bos.write(bs);
					if (v != null) {
						if ((v instanceof String)) {
							bs = v.toString().getBytes();
							bos.write(NumberUtil.toBytes(bs.length));
							bos.write(bs);
						} else if ((v instanceof Long)) {
							bos.write(NumberUtil.toBytes(((Long) v).longValue()));
						} else if ((v instanceof Integer)) {
							bos.write(NumberUtil.toBytes(((Integer) v)
									.intValue()));
						} else if ((v instanceof Boolean)) {
							bos.write(((Boolean) v).booleanValue() ? 1 : 0);
						} else if ((v instanceof Date)) {
							bos.write(NumberUtil.toBytes(((Date) v).getTime()));
						} else if ((v instanceof Mapx)) {
							byte[] arr = mapToBytes((Mapx) v);
							bos.write(NumberUtil.toBytes(arr.length));
							bos.write(arr);
						} else if ((v instanceof Serializable)) {
							byte[] arr = serialize((Serializable) v);
							bos.write(NumberUtil.toBytes(arr.length));
							bos.write(arr);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	public static Mapx<Object, Object> bytesToMap(byte[] arr) {
		ByteArrayInputStream bis = new ByteArrayInputStream(arr);
		int b = -1;
		Mapx map = new Mapx();
		byte[] kbs = new byte[4];
		byte[] vbs = (byte[]) null;
		try {
			while ((b = bis.read()) != -1) {
				bis.read(kbs);
				int len = NumberUtil.toInt(kbs);
				vbs = new byte[len];
				bis.read(vbs);
				String k = new String(vbs);
				Object v = null;
				if (b == 1) {
					bis.read(kbs);
					len = NumberUtil.toInt(kbs);
					vbs = new byte[len];
					bis.read(vbs);
					v = new String(vbs);
				} else if (b == 2) {
					vbs = new byte[8];
					bis.read(vbs);
					v = new Long(NumberUtil.toLong(vbs));
				} else if (b == 3) {
					vbs = new byte[4];
					bis.read(vbs);
					v = new Integer(NumberUtil.toInt(vbs));
				} else if (b == 4) {
					int i = bis.read();
					v = new Boolean(i == 1);
				} else if (b == 5) {
					vbs = new byte[8];
					bis.read(vbs);
					v = new Date(NumberUtil.toLong(vbs));
				} else if (b == 6) {
					bis.read(kbs);
					len = NumberUtil.toInt(kbs);
					vbs = new byte[len];
					bis.read(vbs);
					v = bytesToMap(vbs);
				} else if (b == 7) {
					bis.read(kbs);
					len = NumberUtil.toInt(kbs);
					vbs = new byte[len];
					bis.read(vbs);
					v = unserialize(vbs);
				}
				map.put(k, v);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static void main(String[] args) {
		File f = new File(
				"F:/Workspace_Product\\ZCMS\\UI\\Framework\\Controls/../../..");
		System.out.println(f.list().length);
		System.out.println(f.getAbsolutePath());
	}
}