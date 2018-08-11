package com.merge.service;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.Stream;
import com.merge.util.HttpClientUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PlatinumStreamService
 */
@Service("platinumStream")
public class PlatinumStreamService extends UpdateStreamInfoService {

    @Autowired
    public PlatinumStreamService(StreamService streamService, PlayUrlService playUrlService) {
        super(playUrlService, streamService);
    }

	@Override
	Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String resultString = HttpClientUtil.sendHttpGet("http://majood.net/iptv/API-V4.php?mode=active&code="+accountBean.getCode()+"&mac="+accountBean.getMac()+"&sn="+accountBean.getSn()+"&raw=yes");
        JSONObject resultJson = JSONObject.parseObject(resultString);
        String status = resultJson.getString("status");
        if (status.equals("100")) {
            resultString = HttpClientUtil.sendHttpGet("http://majood.net/iptv/API-V4.php?mode=packages&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
            String categoryString;
            String userAgent = resultJson.getString("user_agent");
            Map<String, String> resourceMap = new HashMap<>();
            if (resultString.indexOf("FROM CACHE =") != -1) {
               categoryString = resultString.replace("FROM CACHE =", "").trim(); 
            } else {
                categoryString = resultString;
            }
            resourceMap.put("live-category", categoryString);
            resourceMap.put("userAgent", userAgent);
            resultString = HttpClientUtil.sendHttpGet("http://majood.net/iptv/API-V4.php?mode=movies_cat&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes");
            JSONArray movieJson = JSONObject.parseArray(resultString);
            if (movieJson.size() > 0) {
                for (int i = 0; i < movieJson.size(); i++) {
                    JSONObject cat = movieJson.getJSONObject(i);
                    JSONArray subcatArr = cat.getJSONArray("sub_cats");
                    if (subcatArr.size() > 0) {
                        for (int j = 0; j < subcatArr.size(); j++) {
                            JSONObject subcat = subcatArr.getJSONObject(j);
                            String url = "http://majood.net/iptv/API-V4.php?mode=movies_list&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&catid=" + subcat.getString("sub_id") + "&raw=yes";
                            resultString = HttpClientUtil.sendHttpGet(url);
                            if (resultString != null) {
                                updateMovieChannel(resultString, subcat.getString("sub_name"), userAgent, accountBean, subcat.getString("sub_id"));
                            }  
                        }
                    }
                }
            }
            accountBean.setStatus(0);
            return resourceMap;
        } else if (status.equals("103")) {
            accountBean.setStatus(1);
        } else if (status.equals("102") || status.equals("104")) {
            accountBean.setStatus(2);
        }
        accountBean.setErrorstr(resultJson.getString("message"));
		return null;
	}

	@Override
	void updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception {
        JSONArray categoryArray = JSONArray.parseArray(resourceJsonMap.get("live-category"));
        for (int i = 0; i < categoryArray.size(); i++) {
            String categoryName = categoryArray.getJSONObject(i).getString("category_name");
            JSONArray channelArray = JSONArray.parseArray(categoryArray.getJSONObject(i).getString("channels"));
            for (int j = 0; j < channelArray.size(); j++) {
                Stream stream;
                JSONObject channel = channelArray.getJSONObject(j);
                if (!channel.getString("stream_url").equals("empty")) {
                    stream = new Stream(channel.getString("stream_id"), channel.getString("stream_name"), channel.getString("stream_icon"), accountBean.getType(), channel.getString("category_id"), categoryName, "live");           
                    Stream streamTemp = streamService.getStreamFromMongo(stream);
                    if (streamTemp == null) {
                        streamService.addStreamIntoMongo(stream);
                    } else {
                        stream.set_id(streamTemp.get_id());
                    }
                    playUrlService.addOrUpdatePlayUrl(stream, accountBean, channel.getString("stream_url"), resourceJsonMap.get("userAgent"));
                }
            }
        }
	}

    private void updateMovieChannel(String channelStrings, String categoryName, String userAgent, AgreementAccountBean accountBean, String categoryId) {
        JSONArray channelArray = JSONArray.parseArray(channelStrings);
        for (int i = 0; i < channelArray.size(); i++) {
            JSONObject channel = channelArray.getJSONObject(i);
            if (!channel.getString("stream_url").equals("empty")) {
                Stream stream;
                if (categoryId.equals("0")) {
                    stream = new Stream(channel.getString("stream_id"), channel.getString("stream_name"), channel.getString("stream_icon"), accountBean.getType(), channel.getString("category_id"), categoryName, "live"); 
                } else {
                    stream = new Stream(channel.getString("id"), channel.getString("title"), channel.getString("icon"), accountBean.getType(), categoryId, categoryName, "movie");
                } 
                Stream streamTemp = streamService.getStreamFromMongo(stream);
                if (streamTemp == null) {
                    streamService.addStreamIntoMongo(stream);
                } else {
                    stream.set_id(streamTemp.get_id());
                }
                playUrlService.addOrUpdatePlayUrl(stream, accountBean, channel.getString("stream_url"), userAgent);
            }
        }
    }
    
}