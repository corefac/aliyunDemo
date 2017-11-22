package com.corefac.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.UploadFileRequest;
import com.corefac.client.AliyunOssClient;
import com.corefac.component.AsyncUploader;
import com.corefac.component.PutObjectProgressListener;
import com.corefac.config.AliyunConfig;
import com.corefac.service.AliyunOssService;
import com.corefac.util.UploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("aliyunOssService")
public class AliyunOssServiceImpl implements AliyunOssService {
	private static final Logger logger = LoggerFactory.getLogger(AliyunOssServiceImpl.class);
	private static final int IMAGE_RESOLUTION_LIMIT = 2048;
	private static final String IMAGE_WIDTH_RESET_FORMAT = "?x-oss-process=image/resize,w_2048";
    private static final String IMAGE_HEIGHT_RESET_FORMAT = "?x-oss-process=image/resize,h_2048";

	@Autowired
	private AliyunConfig aliyunConfig;
	@Autowired
	private RestTemplate restTemplate;

    @Override
    public InputStream getObject(String key) {
        return getObject(AliyunOssClient.getOSSClient(aliyunConfig), aliyunConfig.getOssBucket(), key);
    }

    @Override
    public InputStream getObject(String bucket, String key) {
        return getObject(AliyunOssClient.getOSSClient(aliyunConfig), bucket, key);
    }

	@Override
    public InputStream getObject(OSSClient ossClient, String bucket, String key) {
        OSSObject ossObject = ossClient.getObject(bucket, key);
        return ossObject.getObjectContent();
    }

	@Override
	public String putObject(String key, InputStream inputStream) {
		return putObject(aliyunConfig.getOssBucket(), key, inputStream);
	}

	@Override
	public String putObject(String bucket, String key, InputStream inputStream) {
		return putObject(AliyunOssClient.getOSSClient(aliyunConfig), bucket, key, inputStream);
	}

	@Override
	public String putObject(OSSClient ossClient, String bucket, String key, InputStream inputStream) {
		try {
			ossClient.putObject(bucket, key, inputStream);
			return aliyunConfig.getOssBaseUrl() + "/" + key;
		}catch (OSSException oe) {
			oe.printStackTrace();
			logger.error("code: "+ oe.getErrorCode() + ";message: " + oe.getMessage());
		} catch (ClientException ce) {
			ce.printStackTrace();
			logger.error("code: "+ ce.getErrorCode() + ";message: " + ce.getMessage());
		} finally {
			// 关闭client
			if(ossClient != null) ossClient.shutdown();
		}
		return null;
	}

	@Override
	public String putObject(String key, String fileUrl) {
		return putObject(aliyunConfig.getOssBucket(), key, fileUrl);
	}

	@Override
	public String putObject(String bucket, String key, String fileUrl) {
		return putObject(AliyunOssClient.getOSSClient(aliyunConfig), bucket, key, fileUrl, null);
	}

	@Override
	public String putObject(OSSClient ossClient, String bucket, String key, String fileUrl, String videoId) {
		// 设置断点续传请求
		UploadFileRequest uploadFileRequest = new UploadFileRequest(bucket, key);
		// 指定上传的本地文件
		uploadFileRequest.setUploadFile(fileUrl);
		// 指定上传并发线程数
		uploadFileRequest.setTaskNum(aliyunConfig.getTaskNum());
		// 指定上传的分片大小
		uploadFileRequest.setPartSize(aliyunConfig.getPartSize() * 1024);
        String uploadCallback = null;
		if(videoId != null) {
            // 生成指定的uploadCallback
            uploadCallback = aliyunConfig.getUploadCallback() + "?type=video&videoId=" + videoId;
		}
		// 开启断点续传
		uploadFileRequest.setEnableCheckpoint(true);
		// 增加进度条
		uploadFileRequest.withProgressListener(new PutObjectProgressListener());
		// 断点续传上传
		AsyncUploader asyncUploader = new AsyncUploader();
		asyncUploader.init(ossClient, uploadFileRequest, uploadCallback);
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.submit(asyncUploader);

		return aliyunConfig.getOssBaseUrl() + "/" + key;
	}

	@Override
	public String deleteObject(String key) {
		OSSClient ossClient = null;
		try {
			ossClient = AliyunOssClient.getOSSClient(aliyunConfig);
			ossClient.deleteObject(aliyunConfig.getOssBucket(), key);
			return aliyunConfig.getOssBaseUrl() + "/" + key;
		}catch (OSSException oe) {
			oe.printStackTrace();
			logger.error("code: "+ oe.getErrorCode() + ";message: " + oe.getMessage());
		} catch (ClientException ce) {
			ce.printStackTrace();
			logger.error("code: "+ ce.getErrorCode() + ";message: " + ce.getMessage());
		} finally {
			// 关闭client
			if(ossClient != null) ossClient.shutdown();
		}
		return null;
	}

	public  String getImgInfo(String url) {
		url = url + "?x-oss-process=image/info";
		return restTemplate.getForObject(url, String.class);
	}

	@Override
	public String putImage(String key, InputStream inputStream) {
		key = aliyunConfig.getUploadDirPrefix() + UploadUtil.getFileKey(key);
		return putObject(key, inputStream);
	}

	@Override
	public String putImage(String key, String fileUrl) {
		key = aliyunConfig.getUploadDirPrefix() + UploadUtil.getFileKey(key);
		return putObject(key, fileUrl);
	}

	@Override
	public String resizeImage(String fileUrl) {
        JSONObject imageInfo = JSON.parseObject(getImgInfo(fileUrl));
        if(imageInfo != null && imageInfo.getJSONObject("ImageWidth") != null && imageInfo.getJSONObject("ImageHeight") != null) {
            int width = imageInfo.getJSONObject("ImageWidth").getIntValue("value");
            int height = imageInfo.getJSONObject("ImageHeight").getIntValue("value");
            if(width > IMAGE_RESOLUTION_LIMIT || height > IMAGE_RESOLUTION_LIMIT) {
                if(width >= height) {
                    fileUrl = compressImage(fileUrl, IMAGE_WIDTH_RESET_FORMAT);
                } else {
                    fileUrl = compressImage(fileUrl, IMAGE_HEIGHT_RESET_FORMAT);
                }
            }
        }
        return fileUrl;
    }

    private String compressImage(String fileUrl, String format) {
        ResponseEntity<Resource> responseEntity = restTemplate.exchange( fileUrl + format, HttpMethod.GET, null, Resource.class );
        InputStream responseInputStream;
        String result = null;
        try {
            responseInputStream = responseEntity.getBody().getInputStream();
            result = putObject(UploadUtil.getFileKey(fileUrl), responseInputStream);
            deleteObject(aliyunConfig.getUploadDirPrefix() + UploadUtil.getOSSKey(fileUrl));
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

	@Override
	public String putVideo(OSSClient ossClient, String bucket, String key, InputStream inputStream) {
		return putObject(ossClient, bucket, key, inputStream);
	}

	@Override
	public String putVideo(OSSClient ossClient, String bucket, String key, String fileUrl, String videoId) {
		return putObject(ossClient, bucket, key, fileUrl, videoId);
	}
}
