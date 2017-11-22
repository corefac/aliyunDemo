package com.corefac.weidu.admin.thirdparty.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.vod.model.v20170321.*;
import com.corefac.client.AliyunOssClient;
import com.corefac.module.VodUploadAuth;
import com.corefac.service.AliyunOssService;
import com.corefac.service.AliyunVodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service("aliyunVodService")
public class AliyunVodServiceImpl implements AliyunVodService {
	private static final Logger logger = LoggerFactory.getLogger(AliyunVodServiceImpl.class);

	@Autowired
	private IAcsClient iacsClient;
	@Autowired
	private AliyunOssService aliyunOssService;

	@Override
	public String createVideoCategory(String type) {
		AddCategoryResponse response;
		AddCategoryRequest request = new AddCategoryRequest();
		try {
			request.setCateName(type);
			response = iacsClient.getAcsResponse(request);
			logger.debug(response.getRequestId());
			logger.debug(response.getCategory().getCateId()+"");
			return response.getCategory().getCateId()+"";
		} catch (ServerException e) {
			logger.error("RefreshUploadVideoRequest Server Exception:");
			e.printStackTrace();
		} catch (ClientException e) {
			logger.error("RefreshUploadVideoRequest Client Exception:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String uploadVideo(String fileName, InputStream inputStream, Integer cateId) {
		CreateUploadVideoResponse response = createVideoUploadAuth(fileName, cateId);
		if(response != null) {
			VodUploadAuth vodUploadAuth = new VodUploadAuth(response.getUploadAddress(), response.getUploadAuth());
			OSSClient ossClient = AliyunOssClient.getSTSOSSClient(vodUploadAuth.getEndpoint(), vodUploadAuth.getAccessKeyId(),
					vodUploadAuth.getAccessKeySecret(), vodUploadAuth.getSecurityToken());
			String url = aliyunOssService.putVideo(ossClient, vodUploadAuth.getBucket(),
					vodUploadAuth.getFileName(), inputStream);
			return response.getVideoId();
		}
		return null;
	}

	@Override
	public String uploadVideo(String fileName, String fileUrl, Integer cateId) {
		CreateUploadVideoResponse response = createVideoUploadAuth(fileName, cateId);
		if(response != null) {
			VodUploadAuth vodUploadAuth = new VodUploadAuth(response.getUploadAddress(), response.getUploadAuth());
			logger.debug(vodUploadAuth.toString());
			OSSClient ossClient = AliyunOssClient.getSTSOSSClient(vodUploadAuth.getEndpoint(), vodUploadAuth.getAccessKeyId(),
					vodUploadAuth.getAccessKeySecret(), vodUploadAuth.getSecurityToken());
			String url = aliyunOssService.putVideo(ossClient, vodUploadAuth.getBucket(),
					vodUploadAuth.getFileName(), fileUrl, response.getVideoId());
			logger.debug(url);
			return response.getVideoId();
		}
		return null;
	}

	@Override
	public String refreshUploadVideo(String videoId) {
		RefreshUploadVideoResponse response;
		RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
		try {
			request.setVideoId(videoId);
			response = iacsClient.getAcsResponse(request);
			logger.debug("RequestId:" + response.getRequestId());
			logger.debug("UploadAuth:" + response.getUploadAuth());
			return response.getRequestId();
		} catch (ServerException e) {
			logger.error("RefreshUploadVideoRequest Server Exception:");
			e.printStackTrace();
		} catch (ClientException e) {
			logger.error("RefreshUploadVideoRequest Client Exception:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject getVideoInfo(String videoId) {
		GetVideoInfoResponse response;
		GetVideoInfoRequest request = new GetVideoInfoRequest();
		try {
			request.setVideoId(videoId);
			response = iacsClient.getAcsResponse(request);
			return (JSONObject) JSON.toJSON(response.getVideo());
		} catch (ServerException e) {
			logger.error("RefreshUploadVideoRequest Server Exception:");
			e.printStackTrace();
		} catch (ClientException e) {
			logger.error("RefreshUploadVideoRequest Client Exception:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONArray getVideoPlayInfo(String videoId) {
		GetPlayInfoResponse response;
		GetPlayInfoRequest request = new GetPlayInfoRequest();
		try {
			request.setVideoId(videoId);
			response = iacsClient.getAcsResponse(request);
			return (JSONArray)JSON.toJSON(response.getPlayInfoList());
		}  catch (ServerException e) {
			logger.error("RefreshUploadVideoRequest Server Exception:");
			e.printStackTrace();
		} catch (ClientException e) {
			logger.error("RefreshUploadVideoRequest Client Exception:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getOriginalVideoUrl(String videoId) {
        GetVideoInfoResponse getVideoInfoResponse;
        GetVideoInfoRequest getVideoInfoRequest = new GetVideoInfoRequest();
		try {
		    getVideoInfoRequest.setVideoId(videoId);
		    getVideoInfoResponse = iacsClient.getAcsResponse(getVideoInfoRequest);
		    String status = getVideoInfoResponse.getVideo().getStatus();
		    if(status != null && (status.equals(VodUploadAuth.STATUS_NORMAL)
				    || status.equals(VodUploadAuth.STATUS_TRANSCODING))) {
                GetPlayInfoResponse getPlayInfoResponse;
                GetPlayInfoRequest getPlayInfoRequest = new GetPlayInfoRequest();
                getPlayInfoRequest.setVideoId(videoId);
                getPlayInfoResponse = iacsClient.getAcsResponse(getPlayInfoRequest);
                if(getPlayInfoResponse != null){
                    List<String> videoList = getPlayInfoResponse.getPlayInfoList().stream()
                            .filter(video -> video.getDefinition().equals("OD"))
                            .map(GetPlayInfoResponse.PlayInfo::getPlayURL)
                            .collect(Collectors.toList());
                    if(videoList.size() > 0) return videoList.get(0);
                }
                else
                    return VodUploadAuth.MESSAGE_CLIENT_ERROR;
            } else {
		        return getStatusMessage(status);
            }
		}  catch (ServerException e) {
			logger.error("RefreshUploadVideoRequest Server Exception:");
			e.printStackTrace();
		} catch (ClientException e) {
			logger.error("RefreshUploadVideoRequest Client Exception:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String deleteVideo(String videoId) {
		return null;
	}

	@Override
	public CreateUploadVideoResponse createVideoUploadAuth(String fileName, Integer cateId) {
		CreateUploadVideoResponse response = null;
		CreateUploadVideoRequest request = new CreateUploadVideoRequest();
		// 必选，视频源文件名称(必须带后缀)
		request.setFileName(fileName);
		//必选，视频标题
		request.setTitle(fileName.substring(0, fileName.indexOf(".")));
		if(cateId != null) request.setCateId(cateId);
		try {
			response = iacsClient.getAcsResponse(request);
		} catch (ServerException e) {
			logger.error("CreateUploadVideoRequest Server Exception:");
			e.printStackTrace();
		} catch (ClientException e) {
			logger.error("CreateUploadVideoRequest Client Exception:");
			e.printStackTrace();
		}
		return response;
	}

	private String getStatusMessage(String status) {
	    switch (status) {
            case VodUploadAuth.STATUS_UPLOADING:
                return VodUploadAuth.MESSAGE_UPLOADING;
            case VodUploadAuth.STATUS_UPLOADSUCC:
                return VodUploadAuth.MESSAGE_UPLOADSUCC;
            case VodUploadAuth.STATUS_UPLOADFAIL:
                return VodUploadAuth.MESSAGE_UPLOADFAIL;
            case VodUploadAuth.STATUS_TRANSCODING:
                return VodUploadAuth.MESSAGE_TRANSCODING;
            case VodUploadAuth.STATUS_TRANSCODEFAIL:
                return VodUploadAuth.MESSAGE_TRANSCODEFAIL;
            case VodUploadAuth.STATUS_CHECKING:
                return VodUploadAuth.MESSAGE_CHECKING;
            case VodUploadAuth.STATUS_BLOCKED:
                return VodUploadAuth.MESSAGE_BLOCKED;
            default:
                return VodUploadAuth.MESSAGE_ERROR;
        }
    }
}
