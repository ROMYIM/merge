package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import com.merge.domain.AgreementAccountBean;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.domain.StreamId;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * PlayUrlService
 */
@Service
public class PlayUrlService {

    @Resource
    private MongoTemplate mongoTemplate;

    public void addOrUpdatePlayUrl(Stream stream, AgreementAccountBean accountBean, String playUrl, List<PlayUrlBean> playUrlList) {
        Criteria criteria = createQueryCriteria(accountBean).and("streamid").is(stream.get_id()).and("_class").is(PlayUrlBean.class.getName());
        PlayUrlBean playUrlBean = mongoTemplate.findOne(Query.query(criteria), PlayUrlBean.class, "playUrlBean");
        if (playUrlBean == null) {
            playUrlBean = new PlayUrlBean(stream.get_id(), accountBean, playUrl, 0);
        }
        playUrlList.add(playUrlBean);
    }

    public void addOrUpdatePlayUrl(Stream stream, AgreementAccountBean accountBean, String playUrl, String userAgent, List<PlayUrlBean> playUrlList) {
        Criteria criteria = createQueryCriteria(accountBean).and("streamid").is(stream.get_id()).and("_class").is(PlayUrlBean.class.getName());
        PlayUrlBean playUrlBean = mongoTemplate.findOne(Query.query(criteria), PlayUrlBean.class, "playUrlBean");
        if (playUrlBean == null) {
            playUrlBean = new PlayUrlBean(stream.get_id(), accountBean, playUrl, 0, userAgent);
            playUrlList.add(playUrlBean);
        }
    }

    public void addPlayUrlToList(Stream stream, AgreementAccountBean accountBean, String playUrl, List<PlayUrlBean> playUrlList) {
        PlayUrlBean playUrlBean = new PlayUrlBean(stream.get_id(), accountBean, playUrl, 0);
        playUrlList.add(playUrlBean);
    }

    public void addPlayUrlToList(Stream stream, AgreementAccountBean accountBean, String playUrl, List<PlayUrlBean> playUrlList, String userAgent) {
        PlayUrlBean playUrlBean = new PlayUrlBean(stream.get_id(), accountBean, playUrl, 0, userAgent);
        playUrlList.add(playUrlBean);
    }

    public PlayUrlBean getPlayUrlBean(Stream stream, String streamUrl) {
        Criteria criteria = Criteria.where("streamid").is(stream.get_id()).and("playurl").is(streamUrl);
        return mongoTemplate.findOne(Query.query(criteria), PlayUrlBean.class, "playUrlBean");
    }

    public void addPlayUrlList(List<PlayUrlBean> playUrlList) {
        mongoTemplate.insertAll(playUrlList);
    }

    public List<PlayUrlBean> getPlayUrlList(AgreementAccountBean accountBean) {
        Criteria criteria = createQueryCriteria(accountBean);
        return mongoTemplate.find(Query.query(criteria), PlayUrlBean.class, "playUrlBean");
    }

    public boolean isOneStreamConnectOnePlay(StreamId streamId) {
        Criteria criteria = Criteria.where("streamid").is(streamId);
        List<PlayUrlBean> playUrlList = mongoTemplate.find(Query.query(criteria), PlayUrlBean.class, "playUrlBean");
        if (playUrlList != null && playUrlList.size() > 0) {
            return true;
        }
        return false;
    }

    public void clearOldPlayUrl() {
        long currentMillisecond = System.currentTimeMillis();
        Criteria criteria = Criteria.where("createTimestamp").lt(currentMillisecond - 7776000000L);
        mongoTemplate.remove(Query.query(criteria), PlayUrlBean.class, "playUrlBean");
    }

    public Criteria createQueryCriteria(AgreementAccountBean accountBean) {
        final String type = accountBean.getType();
        Criteria criteria = null;
        switch (type) {
            case "Ms":
                criteria = Criteria.where("code").is(accountBean.getCode())
                .and("sn").is(accountBean.getSn())
                .and("type").is(accountBean.getType());
                break;
            case "orca":
                criteria = Criteria.where("code").is(accountBean.getCode())
                .and("mac").is(accountBean.getMac())
                .and("type").is(accountBean.getType());
                break;
            case "brother":
                criteria = Criteria.where("code").is(accountBean.getCode())
                .and("type").is(accountBean.getType());
                break;
            case "samsat":
                criteria = Criteria.where("code").is(accountBean.getCode())
                .and("sn").is(accountBean.getSn())
                .and("type").is(accountBean.getType());
                break;
            default:
                criteria = Criteria.where("code").is(accountBean.getCode())
                .and("mac").is(accountBean.getMac())
                .and("sn").is(accountBean.getSn())
                .and("type").is(accountBean.getType()); 
        }
        return criteria;
    }
}