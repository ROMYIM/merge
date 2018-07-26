package merge.channel.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import merge.channel.dao.ChannelDao;
import merge.channel.domain.ChannelBean;
import merge.channel.domain.ImageBean;
import merge.channel.domain.StreamBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class ChannelSerivce {
	@Resource
	private ChannelDao channelDao;
	
	@Resource
	private RestTemplate restTemplate;
	
//	@Value("${CHANNEL_DEFAULT_IMG}")
	String CHANNEL_DEFAULT_IMG = "http://38.75.137.228/images/default.jpg";

	public List<ChannelBean> getChannelByCid(int cid, int start, int total) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("cid", cid);
		map.put("start", start);
		map.put("total", total);
		return channelDao.getChannelByCid(map);
	}
	
	public String getImageByChannelId(int channelID) {
		String result = "";
		ImageBean ib = channelDao.getImageByChannelId(channelID);
		if(null !=ib)
			result = ib.getPicpath();
		else
			result = CHANNEL_DEFAULT_IMG;
		return result;
	}
	
	public String getCategoryName(int cid) {
		return restTemplate.getForEntity("http://CATEGORY/getCategoryName?cid={1}", String.class, cid).getBody();
	}
	
	public String getEPGIP() {
		return restTemplate.getForEntity("http://zuul/getZuulIp", String.class).getBody();
	}
	
	public String getPlayUrl(String streamid, String categoryid, String type, String userid) {
		return restTemplate.getForEntity("http://play/getPlayUrl?streamid={1}&categoryid={2}&type={3}&userid={4}", String.class, streamid, categoryid, type, userid).getBody();
	}

	public JSONArray getPlayUrlList(int channelID) {
		JSONArray jaPlayUrls = new JSONArray();
		List<StreamBean> sbs = channelDao.getPlayUrlList(channelID);
		String epgPath = getEPGIP();
		for(StreamBean sb:sbs) {
			jaPlayUrls.add(String.format("%s/channel/play?sourceid=%d", epgPath, sb.getId()));
		}
		return jaPlayUrls;
	}

	public String getPlayUrl(int streamId, String userid) {
		String result = "";
		StreamBean sb = channelDao.getStreamByStreamId(streamId);
		System.out.println("-------------streamId----------" + streamId);
		if(null != sb) {
			System.out.println(String.format("-------steamid:%s--categoryid:%s--type:%s----------", sb.getStreamid(), sb.getCategoryid(), sb.getType()));
			result = getPlayUrl(sb.getStreamid(), sb.getCategoryid(), sb.getType(), userid);
			System.out.println("------------result: " + result);
		}
		return result;
	}

}
