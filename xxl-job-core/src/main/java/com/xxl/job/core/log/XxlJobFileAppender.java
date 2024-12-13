package com.xxl.job.core.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * store trigger log in each log-file
 *
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender {

	private static final Logger logger = LoggerFactory.getLogger(XxlJobFileAppender.class);

	/**
	 * log base path
	 * <p>
	 * strut like:
	 * ---/
	 * ---/gluesource/
	 * ---/gluesource/10_1514171108000.js
	 * ---/gluesource/10_1514171108000.js
	 * ---/2017-12-25/
	 * ---/2017-12-25/639.log
	 * ---/2017-12-25/821.log
	 */
	private static String logBasePath = "/data/applogs/xxl-job/jobhandler";
	private static String glueSrcPath = logBasePath.concat("/gluesource");

	public static void initLogPath(String logPath) {
		// init
		if (StringUtils.hasText(logPath)) {
			logBasePath = logPath;
		}
		// mk base dir
		File logPathDir = new File(logBasePath);
		FileUtil.ensureDirExists(logPathDir);
		logBasePath = logPathDir.getPath();

		// mk glue dir
		File glueBaseDir = new File(logPathDir, "gluesource");
		FileUtil.ensureDirExists(glueBaseDir);
		glueSrcPath = glueBaseDir.getPath();
	}

	public static String getLogPath() {
		return logBasePath;
	}

	public static String getGlueSrcPath() {
		return glueSrcPath;
	}

	/**
	 * log filename, like "logPath/yyyy-MM-dd/9999.log"
	 */
	public static String makeLogFileName(Date triggerDate, long logId) {

		// filePath/yyyy-MM-dd
		File logFilePath = new File(getLogPath(), DateUtil.format(triggerDate, "yyyy-MM-dd"));
		FileUtil.ensureDirExists(logFilePath);
		// filePath/yyyy-MM-dd/9999.log
		return logFilePath.getPath() + File.separator + logId + ".log";
	}

	/**
	 * append log
	 */
	public static void appendLog(String logFileName, String appendLog) {

		// log file
		if (!StringUtils.hasText(logFileName)) {
			return;
		}
		File logFile = new File(logFileName);

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return;
			}
		}

		// log
		if (appendLog == null) {
			appendLog = "";
		}
		appendLog += "\r\n";

		// append file content
		OutputStream fos = null;
		try {
			fos = Files.newOutputStream(logFile.toPath(), StandardOpenOption.APPEND);
			fos.write(appendLog.getBytes(StandardCharsets.UTF_8));
			fos.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			FileUtil.close(fos, logger);
		}

	}

	/**
	 * support read log-file
	 *
	 * @return log content
	 */
	public static LogResult readLog(String logFileName, int fromLineNum) {

		// valid log file
		if (!StringUtils.hasText(logFileName)) {
			return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		File logFile = new File(logFileName);

		if (!logFile.exists()) {
			return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// read file
		StringBuilder logContentBuffer = new StringBuilder(256);
		int toLineNum = 0;
		LineNumberReader reader = null;
		try {
			//reader = new LineNumberReader(new FileReader(logFile));
			reader = new LineNumberReader(new InputStreamReader(Files.newInputStream(logFile.toPath()), StandardCharsets.UTF_8));
			String line;

			while ((line = reader.readLine()) != null) {
				toLineNum = reader.getLineNumber();        // [from, to], start as 1
				if (toLineNum >= fromLineNum) {
					logContentBuffer.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			FileUtil.close(reader, logger);
		}

		// result
		return new LogResult(fromLineNum, toLineNum, logContentBuffer.toString(), false);

		/*
        // it will return the number of characters actually skipped
        reader.skip(Long.MAX_VALUE);
        int maxLineNum = reader.getLineNumber();
        maxLineNum++;	// 最大行号
        */
	}

	/**
	 * read log data
	 *
	 * @return log line content
	 */
	public static String readLines(File logFile) {
		try (BufferedReader reader = Files.newBufferedReader(logFile.toPath(), StandardCharsets.UTF_8)) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
			return sb.toString();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}