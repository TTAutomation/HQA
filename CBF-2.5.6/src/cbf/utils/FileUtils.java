/******************************************************************************
$Id : FileUtils.java 3/3/2016 6:01:04 PM
Copyright 2016-2017 IGATE GROUP OF COMPANIES. All rights reserved
(Subject to Limited Distribution and Restricted Disclosure Only.)
THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
INFORMATION OF IGATE GROUP OF COMPANIES AND IS INTENDED FOR USE
ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
DISCLOSURE UNDER APPLICABLE LAW.
YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
CONDITIONS OF AN AGREEMENT BETWEEN YOU AND IGATE GROUP OF COMPANIES.
The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
RESTRICTED AS SET FORTH THEREIN.
******************************************************************************/

package cbf.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import cbf.utils.LogUtils;

/**
 * 
 * Utility for performing various file activities. E.g. CreateFile,
 * CreateFolder, etc
 * 
 * 
 */
public class FileUtils {

	/**
	 * Returns list of files in specified folder
	 * 
	 * @param folder
	 *            name of folder containing files
	 * @return list of files
	 */
	public static List<File> fileList(String folder) {
		throw new FrameworkException("Method not implemented : fileList(",
				folder, ")");
	}

	/**
	 * Checks file exist or not at specified path
	 * 
	 * @param sPath
	 *            path of file
	 * @return boolean result
	 */
	public static boolean makePath(String sPath) {
		File file = new File(sPath);
		if (file.exists()) {
			return true;
		}
		if (!makePath(file.getParent())) {
			return false;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.handleError("Exception caught in creating file :"
					+ e.getMessage());
		}
		return file.exists();
	}

	/**
	 * Returns boolean value depending on file exists or not
	 * 
	 * @param sFile
	 *            name of file
	 * @return boolean result
	 */
	public static boolean fileExists(String sFile) {
		return isExist(sFile);
	}

	/**
	 * Returns boolean value depending on folder exists or not
	 * 
	 * @param sFolder
	 *            name of folder
	 * @return boolean result
	 */
	public static boolean folderExists(String sFolder) {
		return isExist(sFolder);
	}

	/**
	 * Returns boolean value depending on existence
	 * 
	 * @param sFile
	 *            name of object needed to check
	 * @return boolean result
	 */
	private static boolean isExist(String sName) {
		return new File(sName).exists();
	}

	/**
	 * Removes folder specified
	 * 
	 * @param sFolderPath
	 *            path of folder to be removed
	 */
	public static void removeFolder(String sFolderPath) {
		File file = new File(sFolderPath);
		file.delete();
	}

	/**
	 * Makes folder with specified path and returns boolean value
	 * 
	 * @param sFolderPath
	 *            path of folder
	 * @return boolean result
	 */
	public static boolean makeFolder(String sFolderPath) {
		if (!new File(sFolderPath).isDirectory())
			return new File(sFolderPath).mkdirs();
		else
			return true;// as directory already exists
	}

	/**
	 * Returns temporary path
	 * 
	 * @return temporary path string
	 */
	public static String getTempPath() {
		throw new FrameworkException("Method not implemented : getTempPath()");
	}

	/**
	 * Creates text file with specified name
	 * 
	 * @param sFile
	 *            name for file
	 */
	public static void createTextFile(String sFile) {
		File file = new File(sFile);
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.handleError("Error while creating new file ",sFile, e);
		}
	}

	private FileUtils() {
	}

	private static LogUtils logger = new LogUtils();
}
