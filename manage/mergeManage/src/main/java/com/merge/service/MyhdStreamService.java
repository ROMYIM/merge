package com.merge.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.URLAvailability;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.MongoData;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.domain.StreamId;
import com.merge.util.HttpClientUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MyhdStream
 */
@Service("myhdStream")
public class MyhdStreamService extends UpdateStreamInfoService {

    @Autowired
    public MyhdStreamService(StreamService streamService, PlayUrlService playUrlService) {
        super(playUrlService, streamService);
    }

	@Override
	Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String defaultUrl = "http://osamaiptv.space";
        if (!URLAvailability.isConnect(defaultUrl)) {
            defaultUrl = "http://naharrsky.online";
        }
        String resultString = HttpClientUtil.sendHttpGet(defaultUrl + "/iptvkn9585/API-V4MyHD.php?mode=active&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
        String status = JSONObject.parseObject(resultString).getString("status");
        if (status.equals("100")) {
            resultString = HttpClientUtil.sendHttpGet(defaultUrl + "/iptvkn9585/API-V4MyHD.php?mode=packages&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
            String categoryArray = null;
            Map<String, String> resourceMap = new HashMap<>();
            if (resultString.indexOf("FROM CACHE =") != -1) {
                categoryArray = resultString.replace("FROM CACHE =", "").trim();
            } else {
                categoryArray = resultString;
            }
            resourceMap.put("live-category", categoryArray);
            resultString = HttpClientUtil.sendHttpGet(defaultUrl + "/iptvkn9585/API-V4MyHD.php?mode=movies_cat&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
            JSONArray movieJson = JSONObject.parseArray(resultString);
            if (movieJson.size() > 0) {
                StringBuilder subIdBuilder = new StringBuilder();
                StringBuilder subNameBuilder = new StringBuilder();
                for (int i = 0; i < movieJson.size(); i++) {
                    JSONObject cat = movieJson.getJSONObject(i);
                    JSONArray subcatArray = cat.getJSONArray("sub_cats");
                    if (subcatArray.size() > 0) {
                        for (int j = 0; j < subcatArray.size(); j++) {
                            JSONObject subcat = subcatArray.getJSONObject(j);
                            subIdBuilder.append(subcat.getString("sub_id")).append('/'); 
                            subNameBuilder.append(subcat.getString("sub_name")).append('/');
                        }    
                    }
                }
                resourceMap.put("sub-id", subIdBuilder.toString());
                resourceMap.put("sub-name", subNameBuilder.toString());
                resourceMap.put("url", defaultUrl);
            }
            accountBean.setStatus(0);
            return resourceMap;
        } else if (status.equals("103")) {
            accountBean.setStatus(1);
        } else if (status.equals("102") || status.equals("104")) {
            accountBean.setStatus(2);
        }
        accountBean.setErrorstr(JSONObject.parseObject(resultString).getString("message"));
		return null;
	}

	@Override
	MongoData updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception {
        JSONArray categoryArray = JSONArray.parseArray(resourceJsonMap.get("live-category"));
        List<Stream> streamList = new LinkedList<>();
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        for (int i = 0; i < categoryArray.size(); i++) {
            String categoryName = categoryArray.getJSONObject(i).getString("category_name");
            JSONArray channelArray = JSONArray.parseArray(categoryArray.getJSONObject(i).getString("channels"));
            for (int j = 0; j < channelArray.size(); j++) {
                JSONObject channel = channelArray.getJSONObject(j);
                if (!channel.getString("stream_url").equals("empty")) {
                    Stream stream = new Stream(channel.getString("stream_id"), 
                    channel.getString("stream_name"), channel.getString("stream_icon"), 
                    accountBean.getType(), channel.getString("category_id"), categoryName, "live");           
                    streamService.findStreamFromMongoAndAddList(stream.get_id(), stream, streamList);
                    playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("stream_url"), playUrlList);
                }
            }
        }
        MongoData mongoData = new MongoData(streamList, playUrlList);

        String[] subIds = resourceJsonMap.get("sub-id").split("/");
        String[] subNames = resourceJsonMap.get("sub-name").split("/");
        String defaultUrl = resourceJsonMap.get("url");
        for (int i = 0; i < subIds.length; i++) {
            String subId = subIds[i];
            String subName = subNames[i];
            String url = defaultUrl + "/iptvkn9585/API-V4MyHD.php?mode=movies_list&code="+accountBean.getCode()+"&mac="+accountBean.getMac()+"&sn="+accountBean.getSn()+"&catid="+subId+"&raw=yes";
            String resultString = HttpClientUtil.sendHttpGet(url);
            if (resultString != null) {
                mongoData.addMongoData(updateMovieChannel(resultString, subName, accountBean, subId));
            } 
        }
        return mongoData;
    }

    private MongoData updateMovieChannel(String movieChannels, String categoryName, AgreementAccountBean accountBean, String categoryId) throws Exception {
        JSONArray channelArray = JSONArray.parseArray(movieChannels);
        List<Stream> streamList = new LinkedList<>();
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        if (channelArray.size() > 0) {
            for (int i = 0; i < channelArray.size(); i++) {
                Stream stream;
                JSONObject channel = channelArray.getJSONObject(i);
                if (!channel.getString("stream_url").equals("empty")) {
                    if (categoryId.equals("0")) {
                        stream = new Stream(channel.getString("stream_id"), channel.getString("stream_name"), channel.getString("stream_icon"), accountBean.getType(), channel.getString("category_id"), categoryName, "live");    
                    } else {
                        stream = new Stream(channel.getString("id"), channel.getString("title"), channel.getString("icon"), accountBean.getType(), categoryId, categoryName, "movie");
                    }
                    streamService.findStreamFromMongoAndAddList(stream.get_id(), stream, streamList);
                    playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("stream_url"), playUrlList);
                }
            }
        }
        return new MongoData(streamList, playUrlList);
    }
    
}