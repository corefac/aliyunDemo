package com.corefac.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@ConfigurationProperties("aliyun")
@Component
public class AliyunConfig implements Serializable {
	private static final long serialVersionUID = 1835967735841231125L;

	private String accessKeyId;
	private String accessKeySecret;
	private String vodRegion;
	private String ossEndpoint;
	private String ossBucket;
	private String ossBaseUrl;
	private int taskNum = 5;
	private int partSize = 1024 * 1024;
	private int bigFileSize = 50 * 1024 * 1024;
	private String uploadCallback;
	private String uploadDirPrefix;

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getVodRegion() {
		return vodRegion;
	}

	public void setVodRegion(String vodRegion) {
		this.vodRegion = vodRegion;
	}

	public String getOssEndpoint() {
		return ossEndpoint;
	}

	public void setOssEndpoint(String ossEndpoint) {
		this.ossEndpoint = ossEndpoint;
	}

	public String getOssBucket() {
		return ossBucket;
	}

	public void setOssBucket(String ossBucket) {
		this.ossBucket = ossBucket;
	}

	public String getOssBaseUrl() {
		return ossBaseUrl;
	}

	public void setOssBaseUrl(String ossBaseUrl) {
		this.ossBaseUrl = ossBaseUrl;
	}

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public int getPartSize() {
		return partSize;
	}

	public void setPartSize(int partSize) {
		this.partSize = partSize;
	}

	public int getBigFileSize() {
		return bigFileSize;
	}

	public void setBigFileSize(int bigFileSize) {
		this.bigFileSize = bigFileSize;
	}

	public String getUploadCallback() {
		return uploadCallback;
	}

	public void setUploadCallback(String uploadCallback) {
		this.uploadCallback = uploadCallback;
	}

	public String getUploadDirPrefix() {
		return uploadDirPrefix;
	}

	public void setUploadDirPrefix(String uploadDirPrefix) {
		this.uploadDirPrefix = uploadDirPrefix;
	}
}
