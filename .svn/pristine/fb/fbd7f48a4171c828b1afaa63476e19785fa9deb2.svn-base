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

public class BrothersTvController extends BaseLogin {

    @Resource
    private MongoTemplate mongoTemplate;
    
    AgreementAccountService aAccountService = ApplicationContextHelper.getBean(AgreementAccountService.class);

    StreamService streamService = ApplicationContextHelper.getBean(StreamService.class);
        
    @Override
    public String baseLogin(int id, String code, String sn, MongoTemplate mongo) {
        String result = "", flag = "fail", type = "brother";
        String url = "http://115.tvbrothers.info/api/api_codes.php?code="+code+"&method=info";
        mongoTemplate = mongo;
        AgreementAccountBean account = new AgreementAccountBean();
        try {
            result = HttpClientUtil.sendHttpGet(url);
            JSONObject jsonobject = JSONObject.parseObject(result);
            if (100 == Integer.parseInt(jsonobject.getString("status"))) {
                url = "http://115.tvbrothers.info/api/api_codes.php?code="+code+"&method=CategoryList";
                result = HttpClientUtil.sendHttpGet(url);
                JSONObject jsonObj = JSONObject.parseObject(result);
                JSONArray jsonArray = jsonObj.getJSONObject("packages").getJSONArray("package");
                List<PlayUrlBean> list = new LinkedList<PlayUrlBean>();
                for(int i = 0; i < jsonArray.size(); i++) {
                    JSONObject category = jsonArray.getJSONObject(i);
                    list = getChannelById(category.getString("id"), category.getString("name"), code, mongo, list);
                }
                if (list.size() > 0) {
                    mongo.insertAll(list);
                }
                account.setStatus(0);
            }else {
                account.setStatus(1);
                account.setErrorstr(jsonobject.get("message").toString());
                //当账号无效时，清除playurl
                Criteria c = new Criteria();  
                c.and("code").is(code);
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
                            mongoTemplate.remove(query, Stream.class);  //删除当前code和sn对应的stream详细信息
                        }
                    }
                }
                mongoTemplate.remove(Query.query(c), PlayUrlBean.class);  //删除当前code和sn对应的playurl
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
     * 通过分类id获取对应节目
     * @param code 
     * @param list 
     * @return 
     */
    //@Override
    public List<PlayUrlBean> getChannelById(String categoryid, String categoryname, String code, MongoTemplate mongoTemplate, List<PlayUrlBean> list) {
        try {
            String type = "brother";
            String url = "http://115.tvbrothers.info/api/api_codes.php?code="+code+"&method=channelsList&category_id="+categoryid;
            String re = HttpClientUtil.sendHttpGet(url);
            boolean isjson = isJson(re); //categoryid 34返回不是json数据
            if (isjson) {
                JSONObject json = JSONObject.parseObject(re);
                JSONArray jsonArray = json.getJSONArray("tv_channel");
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject channel = jsonArray.getJSONObject(i);
                    //存入mongo
                    Criteria c = new Criteria();  
                    c.and("streamId").is(channel.getString("id"));
                    c.and("categoryId").is(categoryid);
                    c.and("type").is(type);
                    Stream s = mongoTemplate.findOne(Query.query(c), Stream.class); 
                    String mongo_id = "";
                    if(s != null){
                        mongo_id = s.get_id();
                    }else {
                        Stream stream = new Stream();
                        stream.setStreamId(channel.getString("id"));
                        stream.setStreamName(channel.getString("caption"));
                        stream.setStreamIcon(channel.getString("icon_url"));
                        stream.setStreamType("none"); //该协议节目类型无说明，故用none
                        stream.setCategoryId(categoryid);
                        stream.setCategoryname(categoryname);
                        stream.setType(type);
                        mongoTemplate.insert(stream); 
                        mongo_id = stream.get_id();
                    }
                    //存放播放链接
                    Criteria cp = new Criteria();  
                    cp.and("streamid").is(mongo_id);
                    cp.and("playurl").is(channel.getString("streaming_url"));
                    PlayUrlBean pu = mongoTemplate.findOne(Query.query(cp), PlayUrlBean.class);
                    if (pu != null) {
                        continue;
                    }else {
                        PlayUrlBean playUrl = new PlayUrlBean();
                        playUrl.setStreamid(mongo_id);
                        playUrl.setPlayurl(channel.getString("streaming_url"));
                        playUrl.setCode(code);
                        playUrl.setSn("");
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    public boolean isJson(String content){
        try {
            JSONObject.parseObject(content);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
}
