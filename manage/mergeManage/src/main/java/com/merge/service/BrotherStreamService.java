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
 * BrotherStreamService
 */
@Service("brotherStream")
public class BrotherStreamService extends UpdateStreamInfoService {


    @Autowired
    public BrotherStreamService(PlayUrlService playUrlService, StreamService streamService) {
        super(playUrlService, streamService);
    }

	@Override
	public Map<String, String> getRemoteResource(AgreementAccountBean accountBean) {
        String code = accountBean.getCode();
        String url = "http://115.tvbrothers.info/api/api_codes.php?code=" + code + "&method=info";
        String result = HttpClientUtil.sendHttpGet(url);
        JSONObject resultJson = JSONObject.parseObject(result);
        String status = resultJson.getString("status");
        Map<String, String> resultStringMap = new HashMap<>();
        if (status.equals("100")) {
            url = "http://115.tvbrothers.info/api/api_codes.php?code=" + code + "&method=CategoryList";
            result = HttpClientUtil.sendHttpGet(url);
            accountBean.setStatus(0);
            resultStringMap.put("packages", result);
            return resultStringMap;
        }
        accountBean.setStatus(1);
        accountBean.setErrorstr(resultJson.getString("message"));
		return resultStringMap;
	}

	@Override
	public MongoData updateChannel(Map<String, String> resourceStringMap, AgreementAccountBean accountBean) {
		JSONObject resourceJson = JSONObject.parseObject(resourceStringMap.get("packages"));
        JSONArray jsonArray = resourceJson.getJSONObject("packages").getJSONArray("package");
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        List<Stream> streamList = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject categoryJson = jsonArray.getJSONObject(i);
            String categoryName = categoryJson.getString("name");
            String categoryId = categoryJson.getString("id");                
            String url = "http://115.tvbrothers.info/api/api_codes.php?code=" + accountBean.getCode() + "&method=channelsList&category_id=" + categoryId;
            String result = HttpClientUtil.sendHttpGet(url);
            if (!isJson(result)) {
                continue;
            }
            JSONArray channelJsonArray = JSONObject.parseObject(result).getJSONArray("tv_channel");
            for (int j = 0; j < channelJsonArray.size(); j++) {
                JSONObject channelJson = channelJsonArray.getJSONObject(j);
                String streamId = channelJson.getString("id");
                StreamId id = new StreamId(streamId, categoryId, accountBean.getType());
                Stream stream = new Stream(streamId, channelJson.getString("caption"), channelJson.getString("icon_url"), accountBean.getType(), categoryId, categoryName, "none");
                streamService.findStreamFromMongoAndAddList(id, stream, streamList);
                playUrlService.addPlayUrlToList(stream, accountBean, channelJson.getString("streaming_url"), playUrlList);
            }
        }
        return new MongoData(streamList, playUrlList);
    }

}