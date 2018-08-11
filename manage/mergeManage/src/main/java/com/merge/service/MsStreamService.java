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
 * MsStreamService
 */
@Service("msStream")
public class MsStreamService extends UpdateStreamInfoService {

    @Autowired
    public MsStreamService(PlayUrlService playUrlService, StreamService streamService) {
        super(playUrlService, streamService);
    }

	@Override
	Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String resultString = HttpClientUtil.sendHttpGet("http://m-iptv.net:443/IPTV/API-V4.php?mode=active&code=" + accountBean.getCode() + "&mac=&sn=" + accountBean.getSn() + "&raw=yes");
        JSONObject resultJson = JSONObject.parseObject(resultString.split("data=|data =")[1].trim());
        String status = resultJson.getString("status");
        if (status.equals("100")) {
            String userAgent = resultJson.getString("user_agent");
            resultString = HttpClientUtil.sendHttpGet("http://m-iptv.net:443/IPTV/API-V4.php?mode=packages&code=" + accountBean.getCode()+"&mac=&sn=" + accountBean.getSn() + "&raw=yes");
            String categoryString = resultString.split("data=|data =")[1].trim();
            accountBean.setStatus(0);
            Map<String, String> resourceMap = new HashMap<>();
            resourceMap.put("categoryString", categoryString);
            resourceMap.put("userAgent", userAgent);
            return resourceMap;
        } else if (status.equals("102") || status.equals("104")) {
            accountBean.setStatus(2);
        } else if (status.equals("103") || status.equals("115")) {
            accountBean.setStatus(1);
        }
        accountBean.setErrorstr(resultJson.getString("message"));
		return null;
	}

	@Override
	MongoData updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception {
        JSONArray categoryArray = JSONArray.parseArray(resourceJsonMap.get("categoryString"));
        List<Stream> streamList = new LinkedList<>();
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        for (int i = 0; i < categoryArray.size(); i++) {
            JSONArray channelArray = JSONArray.parseArray(categoryArray.getJSONObject(i).getString("channels"));
            String categoryName = categoryArray.getJSONObject(i).getString("category_name");
            for (int j = 0; j < channelArray.size(); j++) {
                JSONObject channel = channelArray.getJSONObject(j);
                if (!channel.getString("stream_url").equals("empty")) {
                    StreamId streamId = new StreamId(channel.getString("stream_id"), channel.getString("category_id"), accountBean.getType());
                    Stream stream = new Stream(channel.getString("stream_id"), channel.getString("stream_name"), channel.getString("stream_icon"), accountBean.getType(), channel.getString("category_id"), categoryName, "none");
                    streamService.findStreamFromMongoAndAddList(streamId, stream, streamList);
                    playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("stream_url"), playUrlList);
                }
            }
        }
        return new MongoData(streamList, playUrlList);
    }
     
}