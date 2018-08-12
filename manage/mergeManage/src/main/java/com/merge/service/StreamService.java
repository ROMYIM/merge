package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.StreamMapper;
import com.merge.domain.Stream;
import com.merge.domain.StreamBean;
import com.merge.domain.StreamId;

@Service
public class StreamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamService.class);

    @Resource
    private StreamMapper streamMapper;

    @Resource
    private MongoTemplate mongoTemplate;
    
    public int getCountStream(int channelid, String keyword) {
        return streamMapper.getCountStream(channelid, keyword);
    }

    public List<StreamBean> getStreamList(Page<StreamBean> page) {
        return streamMapper.getStreamList(page);
    }

    public void addStream(List<StreamBean> list) {
        streamMapper.addStream(list);
    }

    public void delStream(String sids) {
        streamMapper.delStream(sids);
    }

    public boolean isStreamRelationExist(StreamBean stream) {
        StreamBean streamBean = streamMapper.getStream(stream);
        if (streamBean != null) {
            streamMapper.updateRelationStatus(1, streamBean.getId());
            return true;
        } else {
            return false;
        }
        
    }

    public void delStreamByChannelids(String ids) {
        streamMapper.delStreamByChannelids(ids);
    }

    public List<StreamBean> getStreamByProperties() {
        return streamMapper.selectStreamGroupByIdTypeCategory();
    }

    public void addStreamIntoMongo(Stream stream) {
        mongoTemplate.insert(stream, "stream");
    }

    public Stream getStreamFromMongo(String streamId, String categoryId, String type) {
        StreamId id = new StreamId(streamId, categoryId, type);
        return getStreamFormMongo(id);
    }

    public Stream getStreamFormMongo(StreamId streamId) {
        Query query = Query.query(Criteria.where("_id").is(streamId));
        Stream stream = mongoTemplate.findOne(query, Stream.class, "stream");
        return stream;
    }

    public Stream getStreamFromMongo(Stream stream) {
        return getStreamFormMongo(stream.get_id());
    }

    public void findStreamFromMongoAndAddList(StreamId streamId, Stream stream, List<Stream> streamList) {
        if (getStreamFormMongo(streamId) == null) {
            streamList.add(stream);
        }
    }

    public void addStreamListIntoMongo(List<Stream> streamList) {
        if (streamList != null && streamList.size() > 0) {
            for (Stream stream : streamList) {
                mongoTemplate.insert(stream, "stream");
            }
        }
    }

    public int updateStreamRelation(String streamid, String categoryid, String type) {
        //更新stream与channel的关联
        Stream stream = getStreamFromMongo(streamid, categoryid, type);
        int updateCount = 0;
        StreamBean streamBean;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("streamId:").append(streamid).append("-categoryId:").append(categoryid).append("-type:").append(type);
        if (stream == null) {
            LOGGER.info("删除关系:" + stringBuilder.toString());
            streamBean = new StreamBean(streamid, categoryid, type, 0);
        } else {
            LOGGER.info("恢复关系:" + stringBuilder.toString());
            streamBean = new StreamBean(streamid, categoryid, type, 1);
        }
        updateCount = streamMapper.updateStreamRelation(streamBean); 
        return updateCount;
    }

    public int deleteStreamRelation(String streamid, String categoryid, String type) {
        Criteria criteria = Criteria.where("streamiId").is(streamid).and("categoryId").is(categoryid).and("type").is(type);
        mongoTemplate.remove(Query.query(criteria), Stream.class, "stream");
        StreamBean streamBean = new StreamBean(streamid, categoryid, type, 0);
        return streamMapper.updateStreamRelation(streamBean);
    }

    public void clearOldStreams() {
        long currentMillisecond = System.currentTimeMillis();
        Criteria criteria = Criteria.where("createTimestamp").lt(currentMillisecond - 7776000000L);
        mongoTemplate.remove(Query.query(criteria), Stream.class, "stream");
    }

}
