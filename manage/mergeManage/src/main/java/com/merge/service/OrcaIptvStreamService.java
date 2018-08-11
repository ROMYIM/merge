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
 * OrcaIptvStreamService
 */
@Service("orcaStream")
public class OrcaIptvStreamService extends UpdateStreamInfoService {

    @Autowired
    public OrcaIptvStreamService(StreamService streamService, PlayUrlService playUrlService) {
        super(playUrlService, streamService);
    }

	@Override
	Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String token = null, url = "https://www.orcaapi.com/api/v1.1";
        JSONObject resultJson = secondLogin(url + "/startrack/checkMacAddr?", accountBean.getMac());
        Integer resultCode = resultJson.getInteger("result");
        String resultMessage = resultJson.getString("msg");
        if (1 == resultCode) {
            token = resultJson.getString("access_token");
            accountBean.setStatus(0);
        } else if (0 == resultCode && resultMessage.equals("error : macadress not found!")) {
            resultJson = firstLogin(url+"/startrack/checkActiveCode?", accountBean.getCode(), accountBean.getMac());
            if (1 == resultJson.getInteger("result")) {
                token = resultJson.getString("access_token");
            }
        }
        if (token != null && token.length() > 0) {
            Map<String, String> resourceMap = new HashMap<>();
            String reusltString = HttpClientUtil.doHttpsPost("https://www.orcaapi.com/api/v1.1/liveTv", "", "utf-8", "Bearer " + token);
            resourceMap.put("category", reusltString);
            accountBean.setStatus(0);
            return resourceMap;
        }
        if (resultCode == 0 && !resultMessage.equals("error : macadress not found!")) {
            accountBean.setStatus(1);
        } else if (resultCode == 3 || resultCode == 4) {
            accountBean.setStatus(2);
        }
        accountBean.setErrorstr(resultMessage);
		return null;
	}

	@Override
	MongoData updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception {
        String categoryString = resourceJsonMap.get("category");
        JSONArray categoryArray = JSONObject.parseObject(categoryString).getJSONArray("data");
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        List<Stream> streamList = new LinkedList<>();
		for (int i = 0; i < categoryArray.size(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            JSONArray channelArray = category.getJSONArray("channels");
            for (int j = 0; j < channelArray.size(); j++) {
                JSONObject channel = channelArray.getJSONObject(j);
                StreamId streamId = new StreamId(channel.getString("channel_id"), category.getString("group_id"), accountBean.getType());
                Stream stream = new Stream(channel.getString("channel_id"), 
                    channel.getString("channel_name"), 
                    channel.getString("channel_image"), 
                    accountBean.getType(), 
                    channel.getString("group_id"), 
                    channel.getString("group_name"), "live");
                streamService.findStreamFromMongoAndAddList(streamId, stream, streamList);
                playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("channel_url"), playUrlList);
            }
        }
        return new MongoData(streamList, playUrlList);
	}

    private JSONObject firstLogin(String url, String activeCode, String macaddress) {
        // json参数
        JSONObject paramJson = new JSONObject();
        paramJson.put("activeCode", activeCode);
        paramJson.put("macaddress", macaddress);
        String postString = paramJson.toString();
        try {
            String result = HttpClientUtil.doHttpsPost(url, postString, "utf-8","");
            if (!isJson(result)) {
                //请求结果{"result":3,"msg":"Expired","expire date":"2018-04-21-12:04:00"}{"result":0,"msg":"error : account exist!"}
                String[] reArr = result.split("\\}\\{"); 
                result = reArr[0]+"}";
            }
            JSONObject resultJson = JSONObject.parseObject(result);
            return resultJson;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject secondLogin(String url, String macaddress) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("macaddress", macaddress);
        String strJson = jsonObject.toString();
        try {
            String result = HttpClientUtil.doHttpsPost(url, strJson, "utf-8","");
            if (!isJson(result)) {
                //请求只返回一次结果{"result":0,"msg":"error : macaddress error!"}{"result":0,"msg":"error : macaddress error!"}
                String[] reArr = result.split("\\}\\{"); 
                result = reArr[0]+"}";
            }
            JSONObject json = JSONObject.parseObject(result);
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}