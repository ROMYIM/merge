package com.merge.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.MongoData;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.domain.StreamId;
import com.merge.util.HttpClientUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ShamStreamService
 */
@Service("shamStream")
public class ShamStreamService extends UpdateStreamInfoService {

    @Autowired
    public ShamStreamService(StreamService streamService, PlayUrlService playUrlService) {
        super(playUrlService, streamService);
    }

	@Override
	Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String resultString = HttpClientUtil.sendHttpGet("http://shamtv1.mod92.com/iptv/V5/API-V5.php?mode=active&code="+accountBean.getCode()+"&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
        JSONObject resultJson = JSONObject.parseObject(resultString);
        String status = resultJson.getString("status");
        if (status.equals("100")) {
            Map<String, String> resourceMap = new HashMap<>();
            resourceMap.put("userAgent", resultJson.getString("user_agent"));
            resultString = HttpClientUtil.sendHttpGet("http://shamtv2.mod92.com/iptv/V5/API-V5.php?mode=packages&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
            JSONObject packageJson;
            if (resultString.indexOf("FROM CACHE =") != -1) {
                packageJson = JSONObject.parseObject(resultString.replace("FROM CACHE =", "").trim());
            } else {
                packageJson = JSONObject.parseObject(resultString);
            }
            resourceMap.put("live-category", packageJson.getString("packages"));
            resultString = HttpClientUtil.sendHttpGet("http://shamtv2.mod92.com/iptv/V5/API-V5.php?mode=movies_cat&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
            resourceMap.put("movie-category", resultString);
            accountBean.setStatus(0);
            return resourceMap;
        } else if (status.equals("103")) {
            accountBean.setStatus(1);
        } else if (status.equals("102") || status.equals("104")) {
            accountBean.setStatus(2);
        }
		return null;
	}

	@Override
	MongoData updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception {
        JSONArray categoryArray = JSONArray.parseArray(resourceJsonMap.get("live-category"));
        List<Stream> streamList = new LinkedList<>();
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        for (int i = 0; i < categoryArray.size(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            String categoryName = category.getString("pkg_name");
            JSONArray channelArray = JSONArray.parseArray(category.getString("pkg_channels"));
            for (int j = 0; j < channelArray.size(); j++) {
                JSONObject channel = channelArray.getJSONObject(j);
                StreamId streamId = new StreamId(channel.getString("stream_id"), channel.getString("stream_pkg_id"), accountBean.getType());
                Stream stream = new Stream(channel.getString("stream_id"), channel.getString("stream_name"), channel.getString("stream_icon"), accountBean.getType(), channel.getString("stream_pkg_id"), categoryName, "live");
                streamService.findStreamFromMongoAndAddList(streamId, stream, streamList);
                playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("stream_url"), playUrlList, resourceJsonMap.get("userAgent"));
            }
        }

        categoryArray = JSONArray.parseArray(resourceJsonMap.get("movie-category"));
        for (int i = 0; i < categoryArray.size(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            String categoryName = category.getString("categroy_name");
            String channelString = HttpClientUtil.sendHttpGet("http://shamtv2.mod92.com/iptv/V5/API-V5.php?mode=movies_list&code="+accountBean.getCode()+"&mac="+accountBean.getMac()+"&sn="+accountBean.getSn()+"&catid="+category.getString("id")+"&raw=yes");
            if (!channelString.equals("null")) {
                JSONArray channelArray = JSONArray.parseArray(channelString);
                for (int j = 0; j < channelArray.size(); j++) {
                    JSONObject channel = channelArray.getJSONObject(j);
                    StreamId streamId = new StreamId(channel.getString("id"), channel.getString("stream_display_name"), accountBean.getType());
                    Stream stream = new Stream(channel.getString("id"), 
                    channel.getString("stream_display_name"), 
                    channel.getString("stream_icon"), 
                    accountBean.getType(), 
                    channel.getString("category_id"), 
                    categoryName, "movie");
                    streamService.findStreamFromMongoAndAddList(streamId, stream, streamList);
                    playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("stream_url"), playUrlList, resourceJsonMap.get("userAgent"));
                }
            }
        }

        return new MongoData(streamList, playUrlList);
	}

    
}