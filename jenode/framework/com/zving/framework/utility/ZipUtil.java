package com.zving.framework.utility;

import com.zving.framework.collection.Mapx;
import com.zving.preloader.zip.ZipEntry;
import com.zving.preloader.zip.ZipFile;
import com.zving.preloader.zip.ZipOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ZipUtil {
	public static byte[] zip(byte[] bs) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Deflater def = new Deflater();
		DeflaterOutputStream dos = new DeflaterOutputStream(bos, def);
		try {
			dos.write(bs);
			dos.finish();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] r = bos.toByteArray();
		return r;
	}

	public static byte[] gzip(byte[] bs) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			GZIPOutputStream dos = new GZIPOutputStream(bos);
			dos.write(bs);
			dos.finish();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] r = bos.toByteArray();
		return r;
	}

	public static void zip(String srcFile, String destFile) throws Exception {
		OutputStream os = new FileOutputStream(destFile);
		zip(new File(srcFile), os);
		os.flush();
		os.close();
	}

	public static void zip(File srcFile, OutputStream destStream)
			throws Exception {
		List fileList = getSubFiles(srcFile, true);
		ZipOutputStream zos = new ZipOutputStream(destStream);
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		int readLen = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);

			ze = new ZipEntry(getAbsFileName(srcFile, f));
			ze.setSize(f.length());
			ze.setTime(f.lastModified());
			LogUtil.info("Compressing:" + f.getPath());

			zos.putNextEntry(ze);
			if (f.isFile()) {
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				while ((readLen = is.read(buf, 0, 1024)) != -1) {
					zos.write(buf, 0, readLen);
				}
				is.close();
				zos.flush();
			}
		}
		zos.close();
	}

	public static void zipBatch(String base, String[] srcFiles, String destFile)
			throws Exception {
		OutputStream os = new FileOutputStream(destFile);
		zipBatch(base, srcFiles, os);
		os.flush();
		os.close();
	}

	public static void zipBatch(String base, String[] srcFiles,
			OutputStream destStream) throws Exception {
		File[] files = new File[srcFiles.length];
		for (int i = 0; i < srcFiles.length; i++) {
			files[i] = new File(srcFiles[i]);
		}
		zipBatch(base, files, destStream);
	}

	public static void zipBatch(String base, File[] srcFiles,
			OutputStream destStream) throws Exception {
		zipBatch(base, srcFiles, destStream, true);
	}

	public static void zipBatch(String base, File[] srcFiles,
			OutputStream destStream, boolean filterSVN) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(destStream);
		base = FileUtil.normalizePath(base);
		if (!base.endsWith("/")) {
			base = base + "/";
		}
		for (int k = 0; k < srcFiles.length; k++)
			if (srcFiles[k].exists()) {
				List fileList = getSubFiles(srcFiles[k], filterSVN);
				ZipEntry ze = null;
				byte[] buf = new byte[1024];
				int readLen = 0;
				for (int i = 0; i < fileList.size(); i++) {
					File f = (File) fileList.get(i);

					if ((!filterSVN)
							|| ((!f.getName().equals(".svn")) && (!f.getName()
									.equals(".temp")))) {
						String name = f.getAbsolutePath();
						name = FileUtil.normalizePath(name);
						if (!name.startsWith(base)) {
							return;
						}
						name = name.substring(base.length());
						ze = new ZipEntry(name);
						ze.setSize(f.length());
						ze.setTime(f.lastModified());
						LogUtil.info("Compressing:" + f.getPath());

						zos.putNextEntry(ze);
						if (f.isFile()) {
							InputStream is = new BufferedInputStream(
									new FileInputStream(f));
							while ((readLen = is.read(buf, 0, 1024)) != -1) {
								zos.write(buf, 0, readLen);
							}
							is.close();
						}
					}
				}
			}
		zos.close();
	}

	public static void zipBatch(String[] srcFiles, String destFile)
			throws Exception {
		OutputStream os = new FileOutputStream(destFile);
		zipBatch(srcFiles, os);
		os.flush();
		os.close();
	}

	public static void zipBatch(String[] srcFiles, OutputStream destStream)
			throws Exception {
		File[] files = new File[srcFiles.length];
		for (int i = 0; i < srcFiles.length; i++) {
			files[i] = new File(srcFiles[i]);
		}
		zipBatch(files, destStream);
	}

	public static void zipBatch(File[] srcFiles, OutputStream destStream)
			throws Exception {
		zipBatch(srcFiles, destStream, true);
	}

	public static void zipBatch(File[] srcFiles, OutputStream destStream,
			boolean filterSVN) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(destStream);
		for (int k = 0; k < srcFiles.length; k++)
			if (srcFiles[k].exists()) {
				List fileList = getSubFiles(srcFiles[k], filterSVN);
				ZipEntry ze = null;
				byte[] buf = new byte[1024];
				int readLen = 0;
				for (int i = 0; i < fileList.size(); i++) {
					File f = (File) fileList.get(i);

					ze = new ZipEntry(getAbsFileName(srcFiles[k], f));
					ze.setSize(f.length());
					ze.setTime(f.lastModified());
					LogUtil.info("Compressing:" + f.getPath());

					zos.putNextEntry(ze);
					if (f.isFile()) {
						InputStream is = new BufferedInputStream(
								new FileInputStream(f));
						while ((readLen = is.read(buf, 0, 1024)) != -1) {
							zos.write(buf, 0, readLen);
						}
						is.close();
					}
				}
			}
		zos.close();
	}

	public static void zipStream(InputStream is, OutputStream os,
			String fileName) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(os);
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		int readLen = 0;
		ze = new ZipEntry(fileName);
		ze.setTime(System.currentTimeMillis());
		LogUtil.info("Compressing stream:" + fileName);

		zos.putNextEntry(ze);
		long total = 0L;
		while ((readLen = is.read(buf, 0, 1024)) != -1) {
			zos.write(buf, 0, readLen);
			total += readLen;
		}
		ze.setSize(total);
		zos.flush();
		zos.close();
	}

	public static byte[] unzip(byte[] bs) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		bos = new ByteArrayOutputStream();
		Inflater inf = new Inflater();
		InflaterInputStream dis = new InflaterInputStream(bis, inf);
		byte[] buf = new byte[1024];
		try {
			int c;
			while ((c = dis.read(buf)) != -1) {
				bos.write(buf, 0, c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] r = bos.toByteArray();
		return r;
	}

	public static byte[] ungzip(byte[] bs) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			GZIPInputStream gis = new GZIPInputStream(bis);
			int c;
			while ((c = gis.read(buf)) != -1) {
				bos.write(buf, 0, c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] r = bos.toByteArray();
		return r;
	}

	public static void unzip(String srcFileName, String destPath)
			throws Exception {
		ZipFile zipFile = new ZipFile(srcFileName);
		Enumeration e = zipFile.getEntries();
		ZipEntry zipEntry = null;
		new File(destPath).mkdirs();
		while (e.hasMoreElements()) {
			zipEntry = (ZipEntry) e.nextElement();
			LogUtil.info("Uncompressing:" + zipEntry.getName());
			if (zipEntry.isDirectory()) {
				new File(destPath + File.separator + zipEntry.getName())
						.mkdirs();
			} else {
				File f = new File(destPath + File.separator
						+ zipEntry.getName());
				f.getParentFile().mkdirs();

				InputStream in = zipFile.getInputStream(zipEntry);
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(f));

				byte[] buf = new byte[1024];
				int c;
				while ((c = in.read(buf)) != -1) {
					out.write(buf, 0, c);
				}
				out.close();
				in.close();
			}
		}
		zipFile.close();
	}

	public static Mapx<String, Long> getFileListInZip(String zipFileName)
			throws Exception {
		ZipFile zipFile = new ZipFile(zipFileName);
		Enumeration e = zipFile.getEntries();
		Mapx map = new Mapx();
		ZipEntry zipEntry = null;
		while (e.hasMoreElements()) {
			zipEntry = (ZipEntry) e.nextElement();
			if (!zipEntry.isDirectory()) {
				map.put(zipEntry.getName(), Long.valueOf(zipEntry.getSize()));
			}
		}
		zipFile.close();
		return map;
	}

	public static byte[] readFileInZip(String zipFileName, String fileName) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFileName);
			Enumeration<ZipEntry> e = zipFile.getEntries();

			while (e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				if ((zipEntry.isDirectory())
						|| (!zipEntry.getName().equals(fileName)))
					continue;
				InputStream in = zipFile.getInputStream(zipEntry);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int c;
				while ((c = in.read(buf)) != -1) {
					out.write(buf, 0, c);
				}
				out.close();
				in.close();
				return out.toByteArray();
			}

		} catch (Exception ex) {
			ex.printStackTrace();

			if (zipFile != null)
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void unzip(String srcFileName, String destPath, boolean isPath)
			throws Exception {
		if (isPath) {
			unzip(srcFileName, destPath);
		}
		ZipFile zipFile = new ZipFile(srcFileName);
		Enumeration e = zipFile.getEntries();
		ZipEntry zipEntry = null;
		new File(destPath).mkdirs();
		while (e.hasMoreElements()) {
			zipEntry = (ZipEntry) e.nextElement();
			LogUtil.info("Uncompressing:" + zipEntry.getName());
			if (!zipEntry.isDirectory()) {
				String fileName = zipEntry.getName();
				if (fileName.lastIndexOf("/") != -1) {
					fileName = fileName.substring(fileName.lastIndexOf("/"));
				}
				File f = new File(destPath + "/" + fileName);
				InputStream in = zipFile.getInputStream(zipEntry);
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(f));

				byte[] buf = new byte[1024];
				int c;
				while ((c = in.read(buf)) != -1) {
					out.write(buf, 0, c);
				}
				out.flush();
				out.close();
				in.close();
			}
		}
		zipFile.close();
	}

	static List<File> getSubFiles(File baseDir, boolean filterSVN) {
		List arr = new ArrayList();
		arr.add(baseDir);
		if (baseDir.isDirectory()) {
			File[] tmp = baseDir.listFiles();
			for (int i = 0; i < tmp.length; i++)
				if ((!tmp[i].getName().equals(".svn"))
						&& (!tmp[i].getName().equals(".temp"))) {
					arr.addAll(getSubFiles(tmp[i], filterSVN));
				}
		}
		return arr;
	}

	static String getAbsFileName(File baseDir, File realFileName) {
		File real = realFileName;
		File base = baseDir;
		String ret = real.getName();
		if (real.isDirectory()) {
			ret = ret + "/";
		}

		while (real != base) {
			real = real.getParentFile();
			if (real == null) {
				break;
			}
			if (real.equals(base)) {
				ret = real.getName() + "/" + ret;
				break;
			}
			ret = real.getName() + "/" + ret;
		}

		return ret;
	}

	public static void main(String[] args) {
		byte[] bs = FileUtil.readByte("H:/shop.html");
		try {
			for (int i = 0; i < 10000; i++) {
				bs = zip(bs);
				bs = unzip(bs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}