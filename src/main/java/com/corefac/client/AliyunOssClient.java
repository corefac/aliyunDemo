package com.corefac.client;

import com.aliyun.oss.OSSClient;
import com.corefac.config.AliyunConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliyunOssClient {
	private static Logger logger = LoggerFactory.getLogger(AliyunOssClient.class);
	public static OSSClient getOSSClient(AliyunConfig aliyunConfig) {
		return new OSSClient(aliyunConfig.getOssEndpoint(),
				aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
	}

	public static OSSClient getSTSOSSClient(String endpoint, String accessKeyId, String accessKeySecret, String securityToken) {
		return new OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken);
	}
}
