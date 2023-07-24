package com.mpt.authservice.SocialLogin.Naver;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpt.authservice.SocialLogin.SocialOauth;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaverOauth implements SocialOauth {
    @Value("${OAuth2.naver.url}")
    private String NAVER_SNS_LOGIN_URL;

    @Value("${OAuth2.naver.client-id}")
    private String NAVER_SNS_CLIENT_ID;

    @Value("${OAuth2.naver.callback-url}")
    private String NAVER_SNS_CALLBACK_URL;
    
    @Value("${OAuth2.naver.client-secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    
    @Value("${OAuth2.naver.token-url}")
    private String NAVER_TOKEN_REQUEST_URL;
    
    @Value("${OAuth2.naver.userinfo-url}")
    private String NAVER_USERINFO_REQUEST_URL;

    private final ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL(){
        Map<String,Object> params=new HashMap<>();
        params.put("response_type","code");
        params.put("client_id",NAVER_SNS_CLIENT_ID);
        params.put("state","mpt-sociallogin-test");
        params.put("redirect_uri",NAVER_SNS_CALLBACK_URL);

        String parameterString=params.entrySet().stream()
                .map(x->x.getKey()+"="+x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL=NAVER_SNS_LOGIN_URL+"?"+parameterString;

        return redirectURL;
    }

    public ResponseEntity<String> requestAccessToken(String code, String state) {
        RestTemplate restTemplate=new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_SNS_CLIENT_ID);
        params.add("client_secret", NAVER_SNS_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", state);

        ResponseEntity<String> responseEntity=restTemplate.postForEntity(NAVER_TOKEN_REQUEST_URL,
                params,String.class);

        if(responseEntity.getStatusCode()== HttpStatus.OK){
            return responseEntity;
        }
        return null;
    }

    public NaverOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        System.out.println("response.getBody() = " + response.getBody());
        NaverOauthToken naverOauthToken= objectMapper.readValue(response.getBody(),NaverOauthToken.class);
        return naverOauthToken;
    }

    public ResponseEntity<String> requestUserInfo(NaverOauthToken oauthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",oauthToken.getToken_type() + " " + oauthToken.getAccess_token());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(NAVER_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);
        return response;
    }

    public NaverUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException{
        NaverUser naverUser=objectMapper.readValue(userInfoRes.getBody(),NaverUser.class);
        return naverUser;
    }
}
