package com.corefac.component;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class StorageManager {
	public static final int BUFFER_SIZE = 8192;
	private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);

	public String saveBinaryFile(byte[] data) {
		File tmpFile = getTmpFile();

		if (!valid(tmpFile)) {
			return null;
		}

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(tmpFile));
			bos.write(data);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return tmpFile.getAbsolutePath();
	}

	public String saveFileByInputStream(InputStream is, long maxSize) {
		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);

			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();
			if (tmpFile.length() > maxSize) {
				tmpFile.delete();
				logger.debug("MAX_SIZE");
				return null;
			}
			return tmpFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.error("IO_ERROR");
		return null;
	}

	public String saveFileByInputStream(InputStream is) {
		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);
			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();
			return tmpFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.error("IO_ERROR");
		return null;
	}

	private File getTmpFile() {
		File tmpDir = FileUtils.getTempDirectory();
		String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
		return new File(tmpDir, tmpFileName);
	}

	private boolean valid(File file) {
		File parentPath = file.getParentFile();

		if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
			logger.error("FAILED_CREATE_FILE");
			return false;
		}
		if (!parentPath.canWrite()) {
			logger.error("PERMISSION_DENIED");
			return false;
		}
		return true;
	}
}
