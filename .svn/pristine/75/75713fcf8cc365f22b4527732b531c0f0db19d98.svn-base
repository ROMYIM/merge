package merge.aaa.service;

import java.text.SimpleDateFormat;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	@Resource
	private AAAService aaaService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 30 * 1000)
    public void cleanUsertoken() {
    	long nowTime = System.currentTimeMillis();
    	long oleTime = 0;
    	Set<String> usertokens = aaaService.getRangeUsertoken(oleTime, nowTime);
    	for (String usertoken : usertokens) {
    		String userid = aaaService.getUseridCached(usertoken);
    		if(null != userid) {
	    		aaaService.userLogout(userid);
    		}
		}
    	aaaService.delUsertoken(usertokens);
    	aaaService.delUsertokenTimeout(oleTime, nowTime);
    }
}
