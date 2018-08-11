package com.merge.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.MongoData;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;

/**
 * UpdateStreamInfoService
 */
public abstract class UpdateStreamInfoService {

    protected PlayUrlService playUrlService;
    protected StreamService streamService;

    abstract Map<String, String> getRemoteResource(AgreementAccountBean accountBean) throws Exception;

    abstract MongoData updateChannel(Map<String, String> resourceJsonMap, AgreementAccountBean accountBean) throws Exception;

    public void updateRealtion(AgreementAccountBean accountBean) throws Exception {
        List<PlayUrlBean> playUrlList = playUrlService.getPlayUrlList(accountBean);
        if (playUrlList != null && playUrlList.size() > 0) {
            for (PlayUrlBean playUrlBean : playUrlList) {
                if (playUrlService.isOneStreamConnectOnePlay(playUrlBean.getStreamid())) {
                    Stream stream = streamService.getStreamFormMongo(playUrlBean.getStreamid());
                    streamService.deleteStreamRelation(stream.getStreamId(), stream.getCategoryId(), stream.getType());
                }
            }
        }
    }

    public UpdateStreamInfoService(PlayUrlService playUrlService, StreamService streamService) {
        this.playUrlService = playUrlService;
        this.streamService = streamService;
    }

    protected boolean isJson(String content) {
        try {
            JSONObject.parseObject(content);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
}