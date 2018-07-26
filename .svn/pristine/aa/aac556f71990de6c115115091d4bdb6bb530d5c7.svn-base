package com.merge.config;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.merge.util.BaseLogin;
import com.merge.util.BrothersTvController;
import com.merge.util.FerarriController;
import com.merge.util.MsController;
import com.merge.util.MyhdController;
import com.merge.util.OrcaIptvController;
import com.merge.util.PlatinumController;
import com.merge.util.SamsatController;
import com.merge.util.ShamController;

public class MyThread extends Thread{
    
    public volatile boolean exit = false; 
    public volatile String res = "";

    private int id;
    private String code;
    private String sn;
    private String mac;
    private String type;
    private MongoTemplate mongoTemplate;

    public MyThread(int id, String code, String mac, String sn, String type, MongoTemplate mongoTemplate) {
        this.id = id;
        this.code = code;
        this.mac = mac;
        this.sn = sn;
        this.type = type;
        this.mongoTemplate = mongoTemplate;
    }
    
    public void run() {
        try {
            long startTime=System.currentTimeMillis();   //获取开始时间
            System.out.println(code+"-----"+mac+"-----"+sn+"-----"+type);
            while (!exit); 
            //=========== iptv request start ===============
            if (type.equals("samsat")) {
                BaseLogin login = new SamsatController();
                res = login.baseLogin(id, code, sn, mongoTemplate);
                System.out.println("samsat: "+res);
            }else if (type.equals("Ms")) {
                BaseLogin login = new MsController();
                res = login.baseLogin(id, code, sn, mongoTemplate);
                System.out.println("ms: "+res);
            }else if (type.equals("brother")) { 
                BaseLogin login = new BrothersTvController();
                res = login.baseLogin(id, code, sn, mongoTemplate);
                System.out.println("brother: "+res);
            }else if (type.equals("orca")) {
                BaseLogin login = new OrcaIptvController();
                //请求直播和电影
                int streamType = 1; //1:En; 2:Fr; 3: Ar，暂时只保存英语版本
                res = login.baseLogin(id, code, mac, streamType, mongoTemplate);
                System.out.println("orca: "+res);
            }else if (type.equals("platinum")) {
                BaseLogin login = new PlatinumController();
                res = login.baseLogin(id, code, sn, mac, mongoTemplate);
                System.out.println("platinum: "+res);
            }else if (type.equals("sham")) {
                BaseLogin login = new ShamController();
                res = login.baseLogin(id, code, sn, mac, mongoTemplate);
                System.out.println("sham: "+res);
            }else if (type.equals("myhd")) {
                BaseLogin login = new MyhdController();
                res = login.baseLogin(id, code, sn, mac, mongoTemplate);
                System.out.println("myhd: "+res);
            }else if (type.equals("ferarri")) {
                BaseLogin login = new FerarriController();
                res = login.baseLogin(id, code, sn, mac, mongoTemplate);
                System.out.println("ferarri: "+res);
            }
            //=========== iptv request end ===============
            long endTime=System.currentTimeMillis(); //获取结束时间
            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
