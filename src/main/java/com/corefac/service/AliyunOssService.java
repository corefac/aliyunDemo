package com.corefac.service;

import com.aliyun.oss.OSSClient;

import java.io.InputStream;

public interface AliyunOssService {
    InputStream getObject(String key);

    InputStream getObject(String bucket, String key);

    InputStream getObject(OSSClient ossClient, String bucket, String key);

	String putObject(String key, InputStream inputStream);

	String putObject(String bucket, String key, InputStream inputStream);

	String putObject(OSSClient ossClient, String bucket, String key, InputStream inputStream);

	String putObject(String key, String fileUrl);

	String putObject(String bucket, String key, String fileUrl);

	String putObject(OSSClient ossClient, String bucket, String key, String fileUrl, String videoId);

	String putImage(String key, InputStream inputStream);

	String putImage(String key, String fileUrl);

	String resizeImage(String fileUrl);

	String putVideo(OSSClient ossClient, String bucket, String key, InputStream inputStream);

	String putVideo(OSSClient ossClient, String bucket, String key, String fileUrl, String videoId);

	String deleteObject(String key);

	String getImgInfo(String url);
}
