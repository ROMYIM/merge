package com.merge.domain;

import java.util.List;

/**
 * MongoData
 */
public class MongoData {

    private List<PlayUrlBean> playUrlList;
    private List<Stream> streamList;

    public MongoData(List<Stream> streamList, List<PlayUrlBean> playUrlList) {
        this.playUrlList = playUrlList;
        this.streamList = streamList;
    }

    /**
     * @param playUrlList the playUrlList to set
     */
    public void setPlayUrlList(List<PlayUrlBean> playUrlList) {
        this.playUrlList = playUrlList;
    }

    /**
     * @return the playUrlList
     */
    public List<PlayUrlBean> getPlayUrlList() {
        return playUrlList;
    }

    /**
     * @param streamList the streamList to set
     */
    public void setStreamList(List<Stream> streamList) {
        this.streamList = streamList;
    }

    /**
     * @return the streamList
     */
    public List<Stream> getStreamList() {
        return streamList;
    }

    public void addMongoData(MongoData mongoData) {
        List<Stream> streamList = mongoData.getStreamList();
        if (this.streamList != null && streamList != null) {
            this.streamList.addAll(streamList);
        }
        List<PlayUrlBean> playUrlList = mongoData.getPlayUrlList();
        if (this.playUrlList != null && playUrlList != null) {
            this.playUrlList.addAll(playUrlList);
        }
    }
}