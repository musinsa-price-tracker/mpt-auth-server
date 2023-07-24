package com.mpt.authservice.SocialLogin.Kakao;

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

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpt.authservice.SocialLogin.SocialOauth;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth {
    @Value("${OAuth2.kakao.url}")
    private String KAKAO_SNS_LOGIN_URL;

    @Value("${OAuth2.kakao.client-id}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${OAuth2.kakao.callback-url}")
    private String KAKAO_SNS_CALLBACK_URL;

    // @Value("${OAuth2.kakao.client-secret}")
    // private String KAKAO_SNS_CLIENT_SECRET;

    // @Value("${OAuth2.kakao.scope}")
    // private String KAKAO_DATA_ACCESS_SCOPE;

    @Value("${OAuth2.kakao.token-url}")
    private String KAKAO_TOKEN_REQUEST_URL;

    @Value("${OAuth2.kakao.userinfo-url}")
    private String KAKAO_USERINFO_REQUEST_URL;

    private final ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL(){
        Map<String,Object> params=new HashMap<>();
        params.put("client_id",KAKAO_SNS_CLIENT_ID);
        params.put("redirect_uri",KAKAO_SNS_CALLBACK_URL);
        params.put("response_type","code");

        String parameterString=params.entrySet().stream()
                .map(x->x.getKey()+"="+x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL=KAKAO_SNS_LOGIN_URL+"?"+parameterString;

        return redirectURL;
    }
    
    public ResponseEntity<String> requestAccessToken(String code) {
        RestTemplate restTemplate=new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.add("code", code);

        ResponseEntity<String> responseEntity=restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL,
                params,String.class);

        if(responseEntity.getStatusCode()== HttpStatus.OK){
            return responseEntity;
        }
        return null;
    }

    public KakaoOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        System.out.println("response.getBody() = " + response.getBody());
        KakaoOauthToken kakaoOauthToken= objectMapper.readValue(response.getBody(),KakaoOauthToken.class);
        return kakaoOauthToken;
    }

    public ResponseEntity<String> requestUserInfo(KakaoOauthToken oauthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",oauthToken.getToken_type() + " " + oauthToken.getAccess_token());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);
        return response;
    }

    public KakaoUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException{
        KakaoUser kakaoUser=objectMapper.readValue(userInfoRes.getBody(),KakaoUser.class);
        return kakaoUser;
    }
}
