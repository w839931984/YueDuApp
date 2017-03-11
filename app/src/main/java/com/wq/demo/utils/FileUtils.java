package com.wq.demo.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by WQ on 2016/8/20.
 */
public class FileUtils {

	private static int byteSize = 512;

	public static ArrayList<String> getFiles(ArrayList<String> listFilePath, String Path, String Extension, long fileMinSize, boolean IsIterative) //搜索目录，扩展名，是否进入子文件夹
	{
		File[] files = new File(Path).listFiles();

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension) && f.length() >= fileMinSize) { //判断扩展名
					listFilePath.add(f.getPath());
				}
				if (!IsIterative) {
					break;
				}
			} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) { //忽略点文件（隐藏文件/文件夹）
				getFiles(listFilePath, f.getPath(), Extension, fileMinSize, IsIterative);
			}
		}

		return listFilePath;
	}

	public static String getCharset(File file) throws IOException {

		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
		int p = (bin.read() << 8) + bin.read();

		String code = null;

		switch (p) {
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}
		return code;
	}

	public static String getStringFromFile(File file, long index, boolean isReadNext) {
		FileInputStream fis = null;
		try {
			String code = FileUtils.getCharset(file);
			fis = new FileInputStream(file);
			int num = byteSize;
			if (isReadNext) {
				fis.skip(index);
				if (file.length() - index < byteSize) {
					num = (int) (file.length() - index);
				}
			} else {
				long i = index - byteSize;
				if (i < 0) {
					i = 0;
					num = (int) (index);
				}
				fis.skip(i);
			}

			byte[] bytes = new byte[num];
			if (fis.read(bytes) != -1) {
				if (code == "GBK") {
					int count = 0;
					for (byte b : bytes) {
						if (b < 0) {
							count++;
						}
					}
					if (count % 2 == 0) {
						return new String(bytes, code);
					} else {
						if (isReadNext) {
							bytes = Arrays.copyOf(bytes, bytes.length - 1);
							return new String(bytes, code);
						} else {
							bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
							return new String(bytes, code);
						}
					}
				} else {
					int i;
					int len = bytes.length;
					if (isReadNext) {
						i = 1;
						if (bytes[len - i] > 0) {
							return new String(bytes, code);
						}
						while (bytes[len - i] < 0) {
							if (bytes[len - i] << 25 < 0) {
								break;
							}
							i++;
						}
						bytes = Arrays.copyOf(bytes, len - i);
						return new String(bytes, code);
					} else {
						i = 0;
						if (bytes[i] > 0) {
							return new String(bytes, code);
						}
						while (bytes[i] < 0) {
							if (bytes[i] << 25 < 0){
								break;
							}
							i++;
						}
						bytes = Arrays.copyOfRange(bytes, i, bytes.length);
						return new String(bytes, code);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 文件大小换算
	 */
	public static String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}
}
