package merge.category.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import merge.category.domain.CategoryBean;
import merge.category.service.CategorySerivce;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
public class CategoryController {
	
	@Resource
	private CategorySerivce categorySerivce;
	
	@Resource
	private HttpServletRequest request;
	
	@Resource
	private RestTemplate restTemplate;

	@RequestMapping(value = "index")
	public String index(@RequestParam(value = "userid") String userid) {
		JSONObject jResult = new JSONObject();
		int cid = Integer.parseInt(getCategoryIdByUserid(userid));
		jResult.put("category", setCategory(cid));
		return jResult.toString();
	}
	
	
	private JSONArray setCategory(int cid) {
		List<CategoryBean> cbs = categorySerivce.getCategoryByParentId(cid);
		JSONArray jaCategorys = new JSONArray();
		for (CategoryBean cb : cbs) {
			JSONObject jCategory = new JSONObject();
			jCategory.put("id", cb.getId());
			jCategory.put("name", cb.getName());
			jCategory.put("language", cb.getLanguage());
			jCategory.put("adult", 0);
			jCategory.put("programsum", cb.getSnum());
			jCategory.put("type", cb.getType());
			JSONArray jaSubCategorys = setCategory(cb.getId());
			int count = jaSubCategorys.size();
			jCategory.put("subCategoryCount", count);
			if(0 != count) {
				jCategory.put("subCategory", jaSubCategorys);
			} else {
				jCategory.put("programListUrl", getEPGIP() + String.format("/channel/channelList?cid=%d", cb.getId()));
			}
			jaCategorys.add(jCategory);
		}
		return jaCategorys;
	}
	
	private String getEPGIP() {
		return restTemplate.getForEntity("http://zuul/getZuulIp", String.class).getBody();
	}
	
	private String getCategoryIdByUserid(String userid) {
		return restTemplate.getForEntity("http://AAA/getCategoryIdByUserid?userid={1}", String.class, userid).getBody();
	}
	
	@RequestMapping(value = "getCategoryName")
	public String getCategoryName(@RequestParam(value = "cid") int cid) {
		String result = "";
		CategoryBean cb = categorySerivce.getCategoryById(cid);
		if(null !=cb)
			result = cb.getName();
		return result;
	}
}
