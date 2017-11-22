package com.corefac.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corefac.util.Base64Util;

import java.io.Serializable;

public class VodUploadAuth implements Serializable {
	private static final long serialVersionUID = -226241346665177273L;

	// 阿里云视频点播状态及说明
	public static final String STATUS_NORMAL = "Normal";
	public static final String STATUS_UPLOADING = "Uploading";
	public static final String STATUS_UPLOADFAIL = "UploadFail";
	public static final String STATUS_UPLOADSUCC = "UploadSucc";
	public static final String STATUS_TRANSCODING = "Transcoding";
	public static final String STATUS_TRANSCODEFAIL = "TranscodeFail";
	public static final String STATUS_CHECKING = "Checking";
	public static final String STATUS_BLOCKED = "Blocked";
    public static final String MESSAGE_CLIENT_ERROR = "访问阿里云出错";
    public static final String MESSAGE_ERROR = "视频错误";
    public static final String MESSAGE_UPLOADING = "上传阿里云中";
    public static final String MESSAGE_UPLOADFAIL = "上传阿里云失败";
    public static final String MESSAGE_UPLOADSUCC = "上传阿里云完成";
    public static final String MESSAGE_TRANSCODING = "阿里云转码中";
    public static final String MESSAGE_TRANSCODEFAIL = "阿里云转码失败";
    public static final String MESSAGE_CHECKING = "阿里云审核中";
    public static final String MESSAGE_BLOCKED = "被阿里云屏蔽";

	public static final String FILE_NAME = "FileName";
	public static final String BUCKET = "Bucket";
	public static final String ENDPOINT = "Endpoint";
	public static final String ACCESS_KEY_ID = "AccessKeyId";
	public static final String ACCESS_KEY_SECRET = "AccessKeySecret";
	public static final String SECURITY_TOKEN = "SecurityToken";

	private String fileName;
	private String bucket;
	private String endpoint;
	private String accessKeyId;
	private String accessKeySecret;
	private String securityToken;

	public VodUploadAuth(String uploadAddress, String uploadAuth) {
		JSONObject vodAddress = JSON.parseObject(Base64Util.decode(uploadAddress));
		JSONObject vodAuth = JSON.parseObject(Base64Util.decode(uploadAuth));

		this.fileName = vodAddress.getString(FILE_NAME);
		this.bucket = vodAddress.getString(BUCKET);
		this.endpoint = vodAddress.getString(ENDPOINT);
		this.accessKeyId = vodAuth.getString(ACCESS_KEY_ID);
		this.accessKeySecret = vodAuth.getString(ACCESS_KEY_SECRET);
		this.securityToken = vodAuth.getString(SECURITY_TOKEN);
	}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

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

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
}
