package com.merge.controller;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.merge.config.SessionListener;
import com.merge.domain.UserBean;
import com.merge.service.UserService;
import com.merge.util.AesUserPass;
import com.merge.util.Const;
import com.merge.util.SpringUtil;
import com.merge.util.Tools;

@Controller
public class IndexController {
    
    @Autowired
    private LocaleResolver LocaleResolver;
    
    @Resource
    private UserService userService;
    
    @Resource
    private HttpServletRequest request;
    
    @Resource
    private HttpSession session;
    
    @Resource
    private Tools tools;
    
    @Resource 
    private SpringUtil springUtil;

    @RequestMapping("/")  
    public String login() {
        return "signin";
        //return "index";
    }
    @RequestMapping("/signin")
    public String signin() {
        return "signin";
    }
    
    @RequestMapping("/index")  
    public String goIndex() {
        return "index";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute(Const.SESSION_USER);
        session.removeAttribute(Const.SESSION_SECURITY_CODE);
        return "signin";
    }
    
    @RequestMapping("/login")
    @ResponseBody
    public ModelAndView login(HttpSession session, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("name")String username, @RequestParam("password")String password,
            @RequestParam("code")String code, @RequestParam("language")String language) {
        String sessionCode = (String) session.getAttribute(Const.SESSION_SECURITY_CODE);
        ModelAndView mv = new ModelAndView();
        String errInfo = "";
        UserBean userbean = userService.getUserByName(username);
        if (tools.notEmpty(sessionCode) && sessionCode.equalsIgnoreCase(code)) {
            String pwd = new AesUserPass().encrypt(password);
            UserBean user = userService.getUser(username, pwd);
            if (user != null) {
                session.setAttribute(Const.SESSION_USER, user);
                session.setAttribute("username", username);
                session.removeAttribute(Const.SESSION_SECURITY_CODE);
                request.setAttribute("username", username);
                if (language.equals("zh_CN")) {
                    LocaleResolver.setLocale(request, response, new Locale("zh", "CN"));
                } else if (language.equals("en_US")) {
                    LocaleResolver.setLocale(request, response, new Locale("en", "US"));
                }
            } else {
                errInfo = springUtil.getTextValue("username.or.password.error");
            }
        } else {
            errInfo = springUtil.getTextValue("validation.code.error");
            
        }
        if (tools.isEmpty(errInfo)) {
            if( userbean.getStatus() == 1){
                errInfo = springUtil.getTextValue("invalid.user");
                mv.addObject("errInfo", errInfo);
                mv.setViewName("signin");
                return mv;
            }
            mv.setViewName("redirect:index");
        } else {
            mv.addObject("errInfo", errInfo);
            mv.addObject("username", username);
            mv.addObject("password", password);
            mv.setViewName("signin");
        }
        return mv;
    }
    

    public void sessionHandlerByCacheMap(HttpSession session){  
        String userid=session.getAttribute("userid").toString();  
        if(SessionListener.sessionContext.getSessionMap().get(userid)!=null){  
            HttpSession userSession=(HttpSession)SessionListener.sessionContext.getSessionMap().get(userid);  
            //注销在线用户  
            userSession.invalidate();             
            SessionListener.sessionContext.getSessionMap().remove(userid);  
            //清除在线用户后，更新map,替换map sessionid  
            SessionListener.sessionContext.getSessionMap().remove(session.getId());   
            SessionListener.sessionContext.getSessionMap().put(userid,session);   
        }else {  
            // 根据当前sessionid 取session对象。 更新map key=用户名 value=session对象 删除map  
            SessionListener.sessionContext.getSessionMap().get(session.getId());  
            SessionListener.sessionContext.getSessionMap().put(userid,SessionListener.sessionContext.getSessionMap().get(session.getId()));  
            SessionListener.sessionContext.getSessionMap().remove(session.getId());  
        }  
    }  

}
