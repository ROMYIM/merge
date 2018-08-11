package com.merge.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.MongoData;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.domain.StreamId;
import com.merge.util.HttpClientUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SamsatStreamService
 */
@Service("samsatStream")
public class SamsatStreamService extends UpdateStreamInfoService {
    
    @Autowired
    public SamsatStreamService(StreamService streamService, PlayUrlService playUrlService) {
        super(playUrlService, streamService);
    }

	@Override
	Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String resultString = HttpClientUtil.sendHttpGet("http://act.smi-iptv.com/stbact/actnew.php?code=" + accountBean.getCode() + "&stbid=" + accountBean.getSn());
        String[] split = resultString.split("\\|");
        String url = split[0].replaceAll(" |link:", "");
        if (url != null && !url.equals("")) {
            resultString = HttpClientUtil.sendHttpGet(url);
            JSONObject resultJson = JSONObject.parseObject(resultString);
            JSONObject userInfoJson = resultJson.getJSONObject("user_info");
            JSONObject serverInfoJson = resultJson.getJSONObject("server_info");
            String urlPrefix = "http://" + serverInfoJson.getString("url") + ":" + serverInfoJson.getString("port") + "/";
            String urlMiddler = "/" + userInfoJson.getString("username") + "/" + userInfoJson.getString("password") + "/";
            String channels = resultJson.getString("available_channels");
            Map<String, String> resourceMap = new HashMap<>();
            resourceMap.put("urlPrefix", urlPrefix);
            resourceMap.put("urlMiddler", urlMiddler);
            resourceMap.put("channels", channels);
            accountBean.setStatus(0);
            return resourceMap;
        }
        accountBean.setStatus(2);
        accountBean.setErrorstr(split[1].replaceAll("msg:", ""));
		return null;
	}

	@Override
	MongoData updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception {
        LinkedHashMap<String, String> channelMap = JSONObject.parseObject(resourceJsonMap.get("channels"), new TypeReference<LinkedHashMap<String, String>>() {});
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        List<Stream> streamList = new LinkedList<>();
        for (Map.Entry<String, String> entry : channelMap.entrySet()) {
            JSONObject channel = JSONObject.parseObject(entry.getValue());
            final String streamType = channel.getString("stream_type");
            String extensionName = null;
            StreamId streamId = new StreamId(channel.getString("stream_id"), channel.getString("category_id"), accountBean.getType());
            Stream stream = new Stream(channel.getString("stream_id"), 
            channel.getString("name"), channel.getString("stream_icon"), 
            accountBean.getType(), channel.getString("category_id"), 
            channel.getString("category_name"), streamType);
            streamService.findStreamFromMongoAndAddList(streamId, stream, streamList);
            switch (streamType) {
                case "live":
                    extensionName = ".ts";
                    break;
                case "movie":
                    extensionName = "." + channel.getString("container_extension");
                    break;
                default:
                    extensionName = "";
            }
            String playUrl = resourceJsonMap.get("urlPrefix") + streamType + resourceJsonMap.get("urlMiddler") + stream.getStreamId() + extensionName;
            playUrlService.addPlayUrlToList(stream, accountBean, playUrl, playUrlList);
        }
        return new MongoData(streamList, playUrlList);
	}

    
}