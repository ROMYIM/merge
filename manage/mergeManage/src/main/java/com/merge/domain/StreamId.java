package com.merge.domain;

import java.io.Serializable;

/**
 * StreamId
 */
public class StreamId implements Serializable {

    private static final long serialVersionUID = 1L;
	private String streamId;
    private String categoryId;
    private String type;

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the categoryId
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @param streamId the streamId to set
     */
    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    /**
     * @return the streamId
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    public StreamId() {}

    public StreamId(String streamId, String categoryId, String type) {
        this.setCategoryId(categoryId);
        this.setStreamId(streamId);
        this.setType(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StreamId) {
            StreamId id = (StreamId) obj;
            if (id.getCategoryId().equals(this.categoryId) && 
                id.getStreamId().equals(this.streamId) && 
                id.getType().equals(this.type)) {
                return true;
            }
        }
        return false;
    }
}