package com.spring.community.controller;

import com.spring.community.dto.AccessTokenDTO;
import com.spring.community.dto.GithubUser;
import com.spring.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectURI;


    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state
                           ){

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectURI);
        String accessToken= githubProvider.getAccessToken(accessTokenDTO);
        String token = accessToken.split("&")[0].split("=")[1];
        GithubUser githubUser = githubProvider.getUser(token);
        System.out.println(githubUser.getName());
        System.out.println(githubUser.getBio());
        System.out.println(githubUser.getId());

        return "index";
    }
}
