package net.remesch.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FileUtil {
	/**
	 * Rotate a (log)file - rename it to <filename>.<MMdd>[.<count>] where <count> is only used
	 * if the target filename already exists
	 * 
	 * @param fileName
	 */
	public static void rotateFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			int x = fileName.lastIndexOf('.');
			String newFileName = "", newExtension = "";
			if (x == -1) {
				newFileName = fileName + "_" + DateUtil.now("MMdd");
			} else {
				newFileName = fileName.substring(0, x) + "_" + DateUtil.now("MMdd");
				newExtension = fileName.substring(x);
			}
			File newFile = new File(newFileName + newExtension);
			Integer i = 1;
			if (newFile.exists()) {
				NumberFormat numberFormat = new DecimalFormat("000");
				while (newFile.exists()) {
					String newFileNameCount = newFileName + "_" + numberFormat.format(i) + newExtension;
					newFile = new File(newFileNameCount);
					i++;
				}
			}
			file.renameTo(newFile);
		}
	}
}
