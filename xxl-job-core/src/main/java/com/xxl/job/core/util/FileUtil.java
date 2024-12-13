package com.xxl.job.core.util;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

/**
 * file tool
 *
 * @author xuxueli 2017-12-29 17:56:48
 */
public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static void ensureDirExists(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void close(@Nullable AutoCloseable res, @Nullable Logger logger) {
		if (res != null) {
			try {
				res.close();
			} catch (Exception e) {
				if (logger != null) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * delete recursively
	 */
	public static boolean deleteRecursively(File root) {
		if (root != null && root.exists()) {
			if (root.isDirectory()) {
				File[] children = root.listFiles();
				if (children != null) {
					for (File child : children) {
						deleteRecursively(child);
					}
				}
			}
			return root.delete();
		}
		return false;
	}

	public static void deleteFile(String fileName) {
		// file
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void writeFileContent(File file, byte[] data) {

		// file
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		// append file content
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			close(fos, logger);
		}
	}

	public static byte[] readFileContent(File file) {
		long filelength = file.length();
		byte[] filecontent = new byte[(int) filelength];

		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			in.read(filecontent);
			return filecontent;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			close(in, logger);
		}
	}


    /*public static void appendFileLine(String fileName, String content) {

        // file
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return;
            }
        }

        // content
        if (content == null) {
            content = "";
        }
        content += "\r\n";

        // append file content
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            fos.write(content.getBytes("utf-8"));
            fos.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

    }

    public static List<String> loadFileLines(String fileName){

        List<String> result = new ArrayList<>();

        // valid log file
        File file = new File(fileName);
        if (!file.exists()) {
            return result;
        }

        // read file
        StringBuffer logContentBuffer = new StringBuffer();
        int toLineNum = 0;
        LineNumberReader reader = null;
        try {
            //reader = new LineNumberReader(new FileReader(logFile));
            reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line = null;
            while ((line = reader.readLine())!=null) {
                if (line!=null && line.trim().length()>0) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return result;
    }*/

}