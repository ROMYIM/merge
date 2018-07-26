package com.merge.util;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.HttpClientUtil;
import com.merge.domain.AgreementAccountBean;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;
import com.merge.service.AgreementAccountService;
import com.merge.service.StreamService;

public class OrcaIptvController extends BaseLogin {
    
    @Resource
    private MongoTemplate mongoTemplate;
    
    AgreementAccountService aAccountService = ApplicationContextHelper.getBean(AgreementAccountService.class);
    
    StreamService streamService = ApplicationContextHelper.getBean(StreamService.class);
    
    /**
     * 直播节目登录
     */
    @Override
    public String baseLogin(int id, String code, String mac, int streamType, MongoTemplate mongo) {       
        String url = "https://www.orcaapi.com/api/v1.1", flag = "fail", token = "", type = "orca";
        mongoTemplate = mongo;
        AgreementAccountBean account = new AgreementAccountBean();
        try {
            JSONObject json = secondLogin(url + "/startrack/checkMacAddr?", mac);
            if (1 == json.getInteger("result")) {
                token = json.getString("access_token");
                account.setStatus(0);
            }else if(0 == json.getInteger("result") && json.getString("msg").equals("error : macadress not found!")) {
                json = firstLogin(url+"/startrack/checkActiveCode?", code, mac);
                if (1 == json.getInteger("result")) {
                    token = json.getString("access_token");
                }
            }
            if (!token.equals("")) {
                getLiveTvData("https://www.orcaapi.com/api/v1.1/liveTv", token, code, mac);
            
                //获取电影节目(耗时长)
//                result = HttpClientUtil.doHttpsGet(url +"/ss/vod/language/"+language, "utf-8", "");
//                JSONArray categoryArr = JSONArray.parseArray(result);
//                for(int i = 0; i < categoryArr.size(); i++) {
//                    JSONObject category = categoryArr.getJSONObject(i);
//                    getChannelById(category.getString("category_name"), token, streamType, mongo, code, mac);
//                }
            }
            //code no found, macaddress no valid; macaddress error(second)
            if (0 == json.getInteger("result") && !json.getString("msg").equals("error : macadress not found!")) {
                account.setStatus(1);
                account.setErrorstr(json.get("msg").toString());
            }else if(3 == json.getInteger("result") || 4 == json.getInteger("result")) { //expired or blocked
                account.setStatus(2);
                account.setErrorstr(json.get("msg").toString());
            }
            if (account.getStatus() != 0) {
                //当账号无效时，清除playurl
                Criteria c = new Criteria();  
                c.and("code").is(code);
                c.and("mac").is(mac);
                c.and("type").is(type);
                List<PlayUrlBean> sclist = mongoTemplate.find(Query.query(c), PlayUrlBean.class); 
                if (sclist != null && sclist.size() > 0) {
                    for (PlayUrlBean sc : sclist) {
                        String streamid = sc.getStreamid();
                        Query q = Query.query(Criteria.where("streamid").is(streamid));
                        List<PlayUrlBean> sidlist = mongoTemplate.find(q, PlayUrlBean.class); //判断某个节目是否只跟一个账号相关联
                        if (sidlist != null && sidlist.size() == 1) {
                            //删除channel关联，根据streamid去Stream中查找节目信息，传入channelService删除
                            Query query = Query.query(Criteria.where("_id").is(streamid));
                            Stream stream = mongoTemplate.findOne(query, Stream.class);
                            streamService.deleteStreamRelation(stream.getStreamId(), stream.getCategoryId(), type);
                            mongoTemplate.remove(query, Stream.class);  //删除当前code和mac对应的stream详细信息
                        }
                    }
                }
                mongoTemplate.remove(Query.query(c), PlayUrlBean.class);  //删除当前code和mac对应的playurl
            }
            flag = "success";
        }catch (Exception e) {
            account.setStatus(-1);
            account.setErrorstr("request fail");
            e.printStackTrace();
        }
        account.setId(id);
        aAccountService.setStatusAndError(account);
        return flag;
    }
    
    
    /**
     * 
     * @Method: firstLogin
     * @Author: clear
     * @Date: 2018年1月16日 下午3:42:18
     * @Description: orca iptv first login
     */
    public JSONObject firstLogin(String url, String activeCode, String macaddress) {
        // json参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activeCode", activeCode);
        jsonObject.put("macaddress", macaddress);
        String strJson = jsonObject.toString();
        try {
            String result = HttpClientUtil.doHttpsPost(url, strJson, "utf-8","");
            if (!isJson(result)) {
                //请求结果{"result":3,"msg":"Expired","expire date":"2018-04-21-12:04:00"}{"result":0,"msg":"error : account exist!"}
                String[] reArr = result.split("\\}\\{"); 
                result = reArr[0]+"}";
            }
            JSONObject json = JSONObject.parseObject(result);
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @Method: secondLogin
     * @Author: clear
     * @Date: 2018年1月16日 下午3:56:11
     * @Description: orca iptv second login
     */
    public JSONObject secondLogin(String url, String macaddress) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("macaddress", macaddress);
        String strJson = jsonObject.toString();
        try {
            String result = HttpClientUtil.doHttpsPost(url, strJson, "utf-8","");
            if (!isJson(result)) {
                //请求只返回一次结果{"result":0,"msg":"error : macaddress error!"}{"result":0,"msg":"error : macaddress error!"}
                String[] reArr = result.split("\\}\\{"); 
                result = reArr[0]+"}";
            }
            JSONObject json = JSONObject.parseObject(result);
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    //判断是否为json数据
    public boolean isJson(String content){
        try {
            JSONObject.parseObject(content);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
    
    
    /**
     * 
     * @MethodName:getLiveTvData
     * @Description:获取直播节目存入mongo
     * @author Windy
     * @date 2018年1月24日  上午10:45:17
     */
    public void getLiveTvData(String url, String token, String code, String mac) {
        String Authorization = "Bearer " + token, type = "orca";
        try {
            String result = HttpClientUtil.doHttpsPost(url, "", "utf-8", Authorization);
            JSONObject json = JSONObject.parseObject(result);
            JSONArray jsonArray = json.getJSONArray("data");
            List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
            String mongo_id = "";
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonBean = jsonArray.getJSONObject(i); 
                JSONArray channelArray = jsonBean.getJSONArray("channels");
                for(int j=0;j<channelArray.size();j++){
                    JSONObject channel = channelArray.getJSONObject(j);                   
                    Criteria c = new Criteria(); 
                    c.and("streamId").is(channel.getString("channel_id"));
                    c.and("categoryId").is(jsonBean.getString("group_id"));
                    c.and("type").is(type);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class);
                    if(s != null){
                        mongo_id = s.get_id();
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(channel.getString("channel_id"));
                        stream.setStreamName(channel.getString("channel_name"));
                        stream.setStreamIcon(channel.getString("channel_image"));
                        stream.setStreamType("live");
                        stream.setCategoryId(jsonBean.getString("group_id"));
                        stream.setCategoryname(jsonBean.getString("group_name"));
                        stream.setType(type);
                        mongoTemplate.insert(stream); 
                        mongo_id = stream.get_id();
                    }
                    //存放播放链接
                    Criteria cp = new Criteria();  
                    cp.and("streamid").is(mongo_id);
                    cp.and("playurl").is(channel.getString("channel_url"));
                    PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                    if (pu != null) {
                        continue;
                    }else {
                        PlayUrlBean playUrl = new PlayUrlBean();
                        playUrl.setStreamid(mongo_id);
                        playUrl.setPlayurl(channel.getString("channel_url"));
                        playUrl.setCode(code);
                        playUrl.setMac(mac);
                        playUrl.setType(type);
                        playUrl.setFlag(0);
                        list.add(playUrl);
                    }
                    if (list.size() != 0 && list.size() %10000 == 0){
                        mongoTemplate.insertAll(list);
                        list.clear();
                    }
                }
            }
            if (list.size() > 0){
                mongoTemplate.insertAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    

    /**
     * 
     * @MethodName:getChannelById
     * @Description:根据电影分类获取分类下的节目
     * @author Windy
     * @date 2018年4月12日  上午10:45:17
     */
    public void getChannelById(String categoryid, String token, int streamType, MongoTemplate mongoTemplate, String code, String mac){
        String language = "EN", Authorization = "Bearer " + token, type = "orca";
        if (2 == streamType) {
            language = "FR";
        }else if (3 == streamType) {
            language = "AR";
        }
        try {
            String url = "https://www.orcaapi.com/api/v1.1/ss/vod/language/"+language+"/category/"+categoryid;
            String re = HttpClientUtil.doHttpsGet(url, "utf-8", "");
            JSONArray jsonArray = JSONArray.parseArray(re);
            String mongo_id = "";
            List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
            for(int i=0; i<jsonArray.size(); i++){
                JSONObject channel = jsonArray.getJSONObject(i);                    
                Criteria c = new Criteria();  
                c.and("streamId").is(channel.getString("imdb_id"));
                c.and("categoryId").is(categoryid);
                c.and("type").is(type);
                Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                if(s != null){
                    mongo_id = s.get_id();
                }else {
                    Stream stream = new Stream();
                    stream.setStreamId(channel.getString("imdb_id"));
                    stream.setStreamName(channel.getString("name"));
                    stream.setStreamIcon(channel.getString("image"));
                    stream.setStreamType("movie");
                    stream.setCategoryId(categoryid); 
                    stream.setCategoryname(categoryid);
                    stream.setType(type);
                    mongoTemplate.insert(stream);  //返回结果为整个实体对象
                    mongo_id = stream.get_id();
                }
                //获取播放链接
                url = "https://www.orcaapi.com/api/v1.1/ss/vod/movie/"+channel.getString("imdb_id");
                String urlresult = HttpClientUtil.doHttpsGet(url, "utf-8", Authorization);
                JSONObject playdetail = JSONObject.parseObject(urlresult);
                //存放播放链接
                Criteria cp = new Criteria();  
                cp.and("streamid").is(mongo_id);
                cp.and("playurl").is(playdetail.getString("url"));
                PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                if (pu != null) {
                    continue;
                }else {
                    PlayUrlBean playUrl = new PlayUrlBean();
                    playUrl.setStreamid(mongo_id);
                    playUrl.setPlayurl(playdetail.getString("url"));
                    playUrl.setCode(code);
                    playUrl.setMac(mac);
                    playUrl.setType(type);
                    playUrl.setFlag(0);
                    list.add(playUrl);
                }
                if (list.size() != 0 && list.size() %10000 == 0){
                    mongoTemplate.insertAll(list);
                    list.clear();
                }
            }
            if (list.size() > 0){
                mongoTemplate.insertAll(list);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
