package com.merge.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.merge.config.MongoPageable;
import com.merge.domain.PlaySearchBean;
import com.merge.domain.PlayUrlBean;
import com.merge.domain.Stream;

@Controller
public class AgreementSearchController {
    
    @Resource
    private MongoTemplate mongoTemplate;

    @RequestMapping("/playsearch")
    public String agreementSearch() {
        return "agreement/playsearch";
    }
    
    @RequestMapping("playSearch")
    @ResponseBody
    public String playSearch(@RequestParam("offset")int offset, @RequestParam("limit")int limit, 
            @RequestParam("sort")String sorts, @RequestParam("order")String order,
            @RequestParam("keyword")String keyword) {
        MongoPageable pageable = new MongoPageable();
        Query query = new Query();
        Criteria criteria = new Criteria();
        PageImpl<PlaySearchBean> pagelist = null;
        List<PlaySearchBean> newlist = new ArrayList<PlaySearchBean>();
        try {  
            criteria.and("flag").is(1); 
            if (!keyword.equals("")) {
                criteria.and("userid").regex(".*?"+keyword+".*","i");  //节目名模糊查询，不区分大小写     
            }
            List<Order> orders = new ArrayList<Order>();
            if (order.equals("asc")) {
                orders.add(new Order(Direction.ASC, sorts));
            } else if (order.equals("desc")) {
                orders.add(new Order(Direction.DESC, sorts));
            }
            Sort sort = new Sort(orders);
            if (null != sort) {  
                query.with(sort); 
            } 
            query.addCriteria(criteria);           
            pageable.setPagenumber(offset);       
            pageable.setPagesize(limit);       
            pageable.setSort(sort);
            Long count = mongoTemplate.count(query, PlayUrlBean.class);   //总条数
            List<PlayUrlBean> list = mongoTemplate.find(query.with(pageable), PlayUrlBean.class); //查询结果
            for(PlayUrlBean play : list) {
                PlaySearchBean playSearch = new PlaySearchBean();
                Criteria sc = new Criteria();  
                sc.and("_id").is(play.getStreamid());
                Stream stream = mongoTemplate.findOne(Query.query(sc), Stream.class);
                if (stream != null) {
                    playSearch.setStream(stream.getStreamName());
                    playSearch.setCategory(stream.getCategoryname());
                    playSearch.setPlayurl(play.getPlayurl());
                    playSearch.setType(play.getType());
                    playSearch.setUserid(play.getUserid());
                    newlist.add(playSearch);
                }
            }
            pagelist = new PageImpl<PlaySearchBean>(newlist, pageable, count);  //将集合与分页结果封装
        } catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.toJSONString(pagelist).toString();
    }
}
