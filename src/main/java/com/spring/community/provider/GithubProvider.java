package com.spring.community.provider;

import com.alibaba.fastjson.JSON;
import com.spring.community.dto.AccessTokenDTO;
import com.spring.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

// component will put the class(instance ) into spring container
// use autowired to call it
@Component
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO),mediaType );
        Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

    }

    public GithubUser getUser(String token) {

        OkHttpClient client = new OkHttpClient();
        final String url = "https://api.github.com/user";

        Request request = new Request.Builder()
                .url(url)
                //This adds the token to the header.
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String str =  response.body().string();
            return JSON.parseObject(str, GithubUser.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
