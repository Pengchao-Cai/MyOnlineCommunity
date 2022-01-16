package com.spring.community;

import com.spring.community.dao.User;
import com.spring.community.dto.AuthCode;
import com.spring.community.dto.GithubUser;
import com.spring.community.mapper.UserMapper;
import com.spring.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    GithubProvider githubProvider;

    // 去配置文件中读取这个key，取出他的值，赋给clientId
    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String getCode(@RequestParam(name="code") String code,
                          @RequestParam(name="state") String state,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        AuthCode authCode = new AuthCode();
        authCode.setCode(code);
        authCode.setClient_id(clientId);
        authCode.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(authCode);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null) {
            User user = new User();
            user.setAccountId(githubUser.getId().toString());
            user.setName(githubUser.getName());
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token", token));
        }
        /*
        session 的知识点：
        cookie： 服务器回传给客户端（浏览器）的数据（键值对），由客户端保存。
        session: 验证用户是谁，同时维持一定的状态
        cookie包括两种
        session cookie（存于浏览器内存中，浏览器关闭即消失。或者服务器重启时，虽然本地浏览器中还存有cookie，
        比如说jsessionid,但是服务器上的此jsessionid对应的信息丢失，所以会话失败，无法验证用户身份）
        久化的cookie （存于本地文件中，同时服务器对应的验证信息存于数据库中，保证了即使浏览器关闭或者重启服务器，
        都还可以验证用户身份）
         */
        return "redirect:/";
    }

    @GetMapping("/")
    public String hello(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return "";
        for (Cookie c : cookies) {
            if (c.getName().equals("token")) {
                User user = userMapper.findByToken(c.getValue());
                // 如果request 中包含了名为token的cookie就显示对应的用户名
                // 否则就显示登录按钮
                // 此处session 没啥用，就是为了控制主页是否显示login 按钮
                request.getSession().setAttribute("user", user);
                break;
            }
        }
        /* 几个问题：
        为什么不把用户信息存到cookie里？ 因为前端可以看到，然后修改
        如果用户多的话数据库校验会不会很慢？ 会的。可以改用redis等
        */
        return "index";
    }
}
