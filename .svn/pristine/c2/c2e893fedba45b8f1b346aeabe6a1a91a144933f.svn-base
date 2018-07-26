package merge.channel.dao;

import java.util.List;
import java.util.Map;

import merge.channel.domain.ChannelBean;
import merge.channel.domain.ImageBean;
import merge.channel.domain.StreamBean;

public interface ChannelDao {
	List<ChannelBean> getChannelByCid(Map<String, Integer> map);
	
	ImageBean getImageByChannelId(int channelID);

	List<StreamBean> getPlayUrlList(int channelID);

	StreamBean getStreamByStreamId(int streamId);
}
