package com.corefac.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;

import java.io.InputStream;

public interface AliyunVodService {
	String createVideoCategory(String category);
	CreateUploadVideoResponse createVideoUploadAuth(String fileName, Integer cateId);
	String uploadVideo(String fileName, InputStream inputStream, Integer cateId);
	String uploadVideo(String fileName, String fileUrl, Integer cateId);
	String refreshUploadVideo(String videoId);
	JSONObject getVideoInfo(String videoId);
	JSONArray getVideoPlayInfo(String videoId);
	String getOriginalVideoUrl(String videoId);
	String deleteVideo(String videoId);
}
