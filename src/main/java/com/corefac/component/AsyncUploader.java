package com.corefac.component;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.UploadFileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AsyncUploader implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(AsyncUploader.class);
	private OSSClient ossClient;
	private UploadFileRequest uploadFileRequest;
	private String uploadCallback;
	private boolean isSuccess = false;

	public void init(OSSClient ossClient, UploadFileRequest uploadFileRequest, String uploadCallback) {
		this.ossClient = ossClient;
		this.uploadFileRequest = uploadFileRequest;
		this.uploadCallback = uploadCallback;
	}

	@Override
	public void run() {
		while (!isSuccess) {
			isSuccess = uploadFile();
		}
		if(ossClient != null) ossClient.shutdown();
		try {
			Path tmpFile = Paths.get(uploadFileRequest.getUploadFile());
			Files.deleteIfExists(tmpFile);
			if(uploadCallback != null) {
                // 调用接口更新视频信息
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getForObject(uploadCallback, String.class);
            }
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private boolean uploadFile() {
		try {
			ossClient.uploadFile(uploadFileRequest);
			return true;
		}catch (OSSException oe) {
			oe.printStackTrace();
			logger.error("code: "+ oe.getErrorCode() + ";message: " + oe.getMessage());
		} catch (ClientException ce) {
			ce.printStackTrace();
			logger.error("code: "+ ce.getErrorCode() + ";message: " + ce.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return false;
	}
}
