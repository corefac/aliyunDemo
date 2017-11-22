package com.corefac.client;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.corefac.config.AliyunConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunCoreClient {
	@Autowired
	private AliyunConfig aliyunConfig;

	@Bean
	public IAcsClient getIAcsClient() {
		DefaultAcsClient aliyunCoreClient = new DefaultAcsClient(
				DefaultProfile.getProfile(aliyunConfig.getVodRegion(),
				aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret()));
		 return aliyunCoreClient;
	}
}
