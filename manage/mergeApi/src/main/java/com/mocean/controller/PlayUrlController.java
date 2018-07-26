package com.mocean.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mocean.domain.PlayUrlBean;
import com.mocean.domain.Stream;

@Controller
public class PlayUrlController {
    
    @Resource
    private MongoTemplate mongoTemplate;
    
    @Resource
    private RestTemplate restTemplate;
    
    /**
     * 
     * @MethodName:getPlayUrl
     * @Description:获取节目的播放链接
     * @author Windy
     * @date 2018年5月10日  下午5:28:33
     */
    @RequestMapping(value = "getPlayUrl", method = RequestMethod.GET)
    @ResponseBody
    public String getPlayUrl(@RequestParam("streamid")String streamid,
            @RequestParam("categoryid")String categoryid,
            @RequestParam("type")String type, @RequestParam("userid")String userid) {
        System.out.println("streamid: "+streamid+"--categoryid: "+categoryid+"--type: "+type+"--userid: "+userid);
        JSONObject result = new JSONObject();
        String playurl = "", user_agent = "", cancelUrl = "";
        int status = 0;  //-1：失败；0：成功，有值； 1：节目不存在； 2：播放链接已被分配完；
        try {
            Criteria c = new Criteria();  
            c.and("streamId").is(streamid);
            c.and("categoryId").is(categoryid);
            c.and("type").is(type);
            Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
            //System.out.println("s: "+JSONObject.toJSONString(s));
            if (s != null) {
                String mongo_id = s.get_id(); //获取在mongo中的id
                Criteria cp = new Criteria();  
                cp.and("streamid").is(mongo_id);
                cp.and("flag").is(0); //若为1，表示已被使用
                PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                if (pu != null) {
                    playurl = pu.getPlayurl();
                    if(pu.getUserAgent()!=null &&!pu.getUserAgent().equals("")) {  //Ms播放需要user-agent
                        user_agent = pu.getUserAgent();
                        result.put("user_agent", user_agent);
                    }
                    result.put("playurl", playurl);
                    cancelUrl = getEPGIP()+"/play/cancelPlayUrl?playurl="+playurl;
                    result.put("cancelUrl", cancelUrl);
                    status = 0;
                    //更新flag，标记为已用
                    Query query = new Query();
                    query.addCriteria(Criteria.where("playurl").is(playurl));
                    Update update = Update.update("flag", 1).set("userid", userid);
                    mongoTemplate.updateMulti(query, update, PlayUrlBean.class);
                }else {
                    status = 2;  //播放链接已分配完
                }
            }else {
                status = 1; //节目不存在
            }
        }catch (Exception e){
            status = -1;
            e.printStackTrace();
        }
        result.put("status", status);
        return result.toString();
    }
    
    /**
     * 
     * @MethodName:cancelPlayUrl
     * @Description:取消播放链接的绑定
     * @author Windy
     * @date 2018年5月10日  下午5:28:49
     */
    @RequestMapping(value="cancelPlayUrl", method=RequestMethod.GET)
    @ResponseBody
    public String cancelPlayUrl(@RequestParam("playurl")String playurl) {
        JSONObject result = new JSONObject();
        int status = 0;  //-1：失败；0：修改成功； 1：播放链接不存在；
        try {
            Criteria cp = new Criteria();  
            cp.and("playurl").is(playurl);
            PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
            if (pu != null) {
                //更新flag，标记为未用
                Query query = new Query();
                query.addCriteria(Criteria.where("playurl").is(playurl));
                Update update = new Update();
                update.set("flag", 0);
                mongoTemplate.updateMulti(query, update, PlayUrlBean.class);
                
                status = 0;  
            }else {
                status = 1;
            }
        }catch (Exception e){
            status = -1;
            e.printStackTrace();
        }
        result.put("status", status);
        return result.toString();
    }
    
    /**
     * 
     * @MethodName:cancelByUserid
     * @Description:取消绑定所有播放链接
     * @author Windy
     * @date 2018年5月18日  下午3:55:01
     */
    @RequestMapping(value = "cancelByUserid", method = RequestMethod.GET)
    @ResponseBody
    public String cancelByUserid(@RequestParam("userid")String userid) {
        JSONObject result = new JSONObject();
        int status = 0;  //-1：失败；0：释放成功； 1：该用户未绑定播放链接；
        try {
            Criteria cp = new Criteria();  
            cp.and("userid").is(userid);
            List<PlayUrlBean> list = mongoTemplate.find(Query.query(cp), PlayUrlBean.class);
            if (list != null && list.size() > 0) {
                //更新flag，标记为未用
                Query query = new Query();
                query.addCriteria(Criteria.where("userid").is(userid));
                Update update = Update.update("flag", 0).set("userid", "");
                mongoTemplate.updateMulti(query, update, PlayUrlBean.class);
                
                status = 0;  
            }else {
                status = 1;
            }
        }catch (Exception e){
            status = -1;
            e.printStackTrace();
        }
        result.put("status", status);
        return result.toString();
    }
    
    /**
     * 
     * @MethodName:getEPGIP
     * @Description:获取zuul地址
     * @author Windy
     * @date 2018年5月18日  下午3:55:55
     */
    public String getEPGIP() {
        return restTemplate.getForEntity("http://zuul/getZuulIp", String.class).getBody();
    }
}
