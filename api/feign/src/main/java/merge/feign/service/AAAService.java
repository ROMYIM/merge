package merge.feign.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("AAA")
public interface AAAService {

	@RequestMapping(value = "login", method = RequestMethod.GET)
	String login(@RequestParam(value = "userid") String userid,
			@RequestParam(value = "Authenticator") String Authenticator,
			@RequestParam(value = "clientIP", required = false) String clientIP);

	@RequestMapping(value = "updateUserToken", method = RequestMethod.GET)
	String updateUserToken(@RequestParam(value = "oldusertoken") String oldusertoken);
}
