package merge.channel.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import merge.channel.domain.ChannelBean;
import merge.channel.service.ChannelSerivce;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
public class ChannelController {
	
	@Resource
	private ChannelSerivce channelSerivce;
	
	@Resource
	private HttpServletRequest request;
	
	@RequestMapping(value = "channelList")
	public String channelList(@RequestParam(value = "cid") int cid,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "total", required = false) Integer total) {
		JSONObject jResult = new JSONObject();
		if(null == start) {
			start = 0;
		}
		if(null == total) {
			total = 20;
		}
		List<ChannelBean> cbs = channelSerivce.getChannelByCid(cid, start, total);
		JSONArray jaChannnels = new JSONArray();
		jResult.put("total", cbs.size());
		for(ChannelBean cb : cbs) {
			JSONObject jChannel = new JSONObject();
			jChannel.put("id", cb.getId());
			jChannel.put("name", cb.getName());
			jChannel.put("language", cb.getLanguage());
			jChannel.put("channelType", cb.getType());
			jChannel.put("callsign", cb.getCallsign());
			jChannel.put("image", channelSerivce.getImageByChannelId(cb.getId()));
			jChannel.put("categoryid", cid);
			jChannel.put("category", channelSerivce.getCategoryName(cid));
			jChannel.put("description", cb.getDescription());
			jChannel.put("playUrlList", channelSerivce.getPlayUrlList(cb.getId()));
			jaChannnels.add(jChannel);
		}
		jResult.put("channel", jaChannnels);
		return jResult.toString();
	}
	
	@RequestMapping(value = "play")
	public String play(@RequestParam(value = "sourceid") int sourceid,
			@RequestParam(value = "userid") String userid) {
		return channelSerivce.getPlayUrl(sourceid, userid);
	}
	
}
