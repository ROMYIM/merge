package com.merge.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merge.config.MongoPageable;
import com.merge.config.Page;
import com.merge.domain.Stream;
import com.merge.domain.StreamBean;
import com.merge.service.StreamService;

@Controller
public class StreamController {
    
    @Resource
    private StreamService streamService;
        
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 
     * @MethodName:getStream
     * @Description:获取节目列表
     * @author Windy
     * @date 2018年3月7日  上午10:54:00
     */
    @RequestMapping("/getStream")
    @ResponseBody
    public String getStream(@RequestParam("offset")int offset, @RequestParam("limit")int limit,
            @RequestParam("channelid")int channelid, @RequestParam("keyword")String keyword){
        Page<StreamBean> page = new Page<StreamBean>();
        page.setCid(channelid);
        page.setStartNum(offset);
        page.setPageNum(limit);
        page.setQuery(keyword);
        try {
            page.setTotal(streamService.getCountStream(channelid,keyword));
            List<StreamBean> rows = streamService.getStreamList(page);
            page.setRows(rows);
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(page).toString();
    }
    
    /**
     * 
     * @MethodName:streamManage
     * @Description:添加节目
     * @author Windy
     * @date 2018年3月7日  上午10:53:37
     */
    @RequestMapping("streamManage")
    @ResponseBody
    public String streamManage(@RequestParam("channelid")int channelid, @RequestParam("info")String info){
        String result = "fail";
        try{
            List<StreamBean> list = new ArrayList<StreamBean>();
            JSONArray jsonarr = JSONArray.parseArray(info); 
            for(int i=0;i<jsonarr.size();i++){
                JSONObject json = jsonarr.getJSONObject(i);
                StreamBean s = new StreamBean();
                s.setStreamid(json.getString("sid"));
                s.setStreamname(json.getString("sname"));
                s.setStreamicon(json.getString("sicon")); //要不要存 
                s.setType(json.getString("type"));
                s.setCategoryid(json.getString("cid"));
                s.setChannelid(channelid);
                if(!streamService.isStreamRelationExist(s)){
                    list.add(s);
                }
            }
            if (list.size() >0){
                streamService.addStream(list);
            }
            result = "success";
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 
     * @MethodName:delStream
     * @Description:删除节目
     * @author Windy
     * @date 2018年3月7日  上午10:53:49
     */
    @RequestMapping("delStream")
    @ResponseBody
    public String delStream(@RequestParam("sids")String sids, @RequestParam("channelid")int channelid){
        String result = "fail";
        try{
            streamService.delStream(sids);
            result = "success";
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 
     * @MethodName:getIStreamList
     * @Description:获取可添加的iptv节目列表(发现不同分类中有相同节目)
     * @author Windy
     * @date 2018年3月14日  上午11:08:27
     */
    @RequestMapping("getIStreamList")
    @ResponseBody
    public String getIStreamList(@RequestParam("limit")int limit,@RequestParam("offset")int offset,
            @RequestParam("keyword")String keyword, @RequestParam("type")String type,
            @RequestParam("streamtype")String streamtype){
        MongoPageable pageable = new MongoPageable();
        Query query = new Query();
        Criteria criteria = new Criteria();
        PageImpl<Stream> pagelist = null;
        try {
            if (!type.equals("-1")) {
                criteria.and("type").is(type);
            }
            if (!streamtype.equals("all")) {
                criteria.and("streamType").is(streamtype); 
            }
            criteria.and("streamName").regex(".*?"+keyword+".*","i");  //节目名模糊查询，不区分大小写     
            List<Order> orders = new ArrayList<Order>();
            orders.add(new Order(Direction.DESC, "streamId"));
            Sort sort = new Sort(orders);
            if (null != sort) {  
                query.with(sort); 
            } 
            query.addCriteria(criteria);           
            pageable.setPagenumber(offset);       
            pageable.setPagesize(limit);       
            pageable.setSort(sort);
            Long count = mongoTemplate.count(query, Stream.class);   //总条数
            List<Stream> list = mongoTemplate.find(query.with(pageable), Stream.class); //查询结果
            pagelist = new PageImpl<Stream>(list, pageable, count);  //将集合与分页结果封装
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(pagelist).toString();
    }
}
