package com.merge.service;

import java.util.ArrayList;
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
 * FerarriStreamService
 */
@Service("ferarriStream")
public class FerarriStreamService extends UpdateStreamInfoService {

    @Autowired
    public FerarriStreamService(StreamService streamService, PlayUrlService playUrlService) {
        super(playUrlService, streamService);
    }

	@Override
	public Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception {
        String url = "https://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=active&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes";
        String[] array = HttpClientUtil.sendHttpGet(url).split("Decoded data = ");
        JSONObject resultJson = JSONObject.parseObject(array[1].trim());
        String status = resultJson.getString("status");
        if (status != null && (status.equals("0") || status.equals("1"))) {
            accountBean.setStatus(0);
            Map<String, String> resultStringMap = new HashMap<>();
            String[] packageStrings = HttpClientUtil.sendHttpGet("http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=packages&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes").split("Decoded data = ");
            String[] channelStrings = HttpClientUtil.sendHttpGet("http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=channels&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&raw=yes").split("Decoded data = ");
            resultStringMap.put("live-category", packageStrings[1].trim());
            resultStringMap.put("live-channel", channelStrings[1].trim());
            String[] categoryStrings = HttpClientUtil.sendHttpGet("http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=movies_cat&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&parent=all&raw=yes").split("Decoded data = ");
            JSONArray categoryArr = JSONArray.parseArray(categoryStrings[1].trim());
            List<String> parent = new ArrayList<>();
            if (categoryArr.size() > 0) {
                for (int i = 0; i < categoryArr.size(); i++) {
                    if (parent.contains(categoryArr.getJSONObject(i).getString("parent_id"))) {
                        continue;
                    }else {
                        parent.add(categoryArr.getJSONObject(i).getString("parent_id"));
                    }
                }
                //由于分类下若有子分类，节目会为空，故先剔除有子分类的顶级分类
                if (parent.size() > 0) {
                    for (int m = 0; m < categoryArr.size(); m++) {
                        for (int n = 0; n < parent.size(); n++) {
                            if (categoryArr.getJSONObject(m).getString("id").equals(parent.get(n))) {
                                categoryArr.remove(m);
                            }
                        }
                    }
                }
            }
            channelStrings = HttpClientUtil.sendHttpGet("http://tigert8.online/iptvkn9585/API-V3Ferrari.php?mode=movies_list&code=" + accountBean.getCode() + "&mac=" + accountBean.getMac() + "&sn=" + accountBean.getSn() + "&catid=all&raw=yes").split("Decoded data = ");
            resultStringMap.put("movie-category", categoryArr.toJSONString());
            resultStringMap.put("movie-channel", channelStrings[1].trim());
            return resultStringMap;
        } else if (status.equals("3")) {
            accountBean.setStatus(1);
        } else if (status.equals("2") || status.equals("4")) {
            accountBean.setStatus(2);
        }
        accountBean.setErrorstr(resultJson.getString("message"));
		return null;
	}

	@Override
	public MongoData updateChannel(Map<String, String> resourceStringMap, AgreementAccountBean accountBean) throws Exception {
        JSONArray categoryArray = JSONArray.parseArray(resourceStringMap.get("live-category"));
        JSONArray channelArray = JSONArray.parseArray(resourceStringMap.get("live-channel"));
        MongoData liveData = addOrUpdatePlayUrl(categoryArray, channelArray, accountBean, "live");
        categoryArray = JSONArray.parseArray(resourceStringMap.get("movie-category"));
        channelArray = JSONArray.parseArray(resourceStringMap.get("movie-channel"));
        MongoData movieData = addOrUpdatePlayUrl(categoryArray, channelArray, accountBean, "movie");
        liveData.addMongoData(movieData);
        return liveData;
	}
    
    private MongoData addOrUpdatePlayUrl(JSONArray categoryArray, JSONArray channelArray, AgreementAccountBean accountBean, String streamType) {
        List<Stream> streamList = new LinkedList<>();
        List<PlayUrlBean> playUrlList = new LinkedList<>();
        String categoryname = "";
        if (channelArray.size() > 0) {
            for (int i = 0; i < channelArray.size(); i++) {
                JSONObject channel = channelArray.getJSONObject(i);    
                for (int j = 0; j < categoryArray.size(); j++) {
                    JSONObject category = categoryArray.getJSONObject(j);
                    if (channel.getString("category_id").equals(category.getString("id"))) {
                        categoryname = category.getString("category_name");
                    }
                }
                Stream stream = new Stream(channel.getString("id"), 
                    channel.getString("stream_display_name"), 
                    channel.getString("stream_icon"), accountBean.getType(), 
                    channel.getString("category_id"), categoryname, streamType);
                    streamList.add(stream);
                StreamId streamId = new StreamId(channel.getString("id"), channel.getString("category_id"), accountBean.getType());
                streamService.findStreamFromMongoAndAddList(streamId, stream, streamList);
                playUrlService.addPlayUrlToList(stream, accountBean, channel.getString("stream_url"), playUrlList);
            }
        }
        return new MongoData(streamList, playUrlList);
    }
    
}