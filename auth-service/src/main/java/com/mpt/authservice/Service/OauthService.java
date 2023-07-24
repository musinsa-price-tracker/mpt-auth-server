package com.mpt.authservice.Service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mpt.authservice.Domain.User;
import com.mpt.authservice.Dto.UserResponse;
import com.mpt.authservice.SocialLogin.LoginTokenManager;
import com.mpt.authservice.SocialLogin.SocialLoginType;
import com.mpt.authservice.SocialLogin.Google.GoogleOauth;
import com.mpt.authservice.SocialLogin.Google.GoogleOauthToken;
import com.mpt.authservice.SocialLogin.Google.GoogleUser;
import com.mpt.authservice.SocialLogin.Kakao.KakaoOauth;
import com.mpt.authservice.SocialLogin.Kakao.KakaoOauthToken;
import com.mpt.authservice.SocialLogin.Kakao.KakaoUser;
import com.mpt.authservice.SocialLogin.Naver.NaverOauth;
import com.mpt.authservice.SocialLogin.Naver.NaverOauthToken;
import com.mpt.authservice.SocialLogin.Naver.NaverUser;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthService{    
    @Autowired
    private final GoogleOauth googleOauth;
    @Autowired
    private final KakaoOauth kakaoOauth;
    @Autowired
    private final NaverOauth naverOauth;    
	@Autowired
	private final LoginTokenManager loginTokenManager;
    
    public String request(SocialLoginType socialLoginType) throws IOException {
        String redirectURL;
        switch (socialLoginType){
            case GOOGLE : redirectURL = googleOauth.getOauthRedirectURL();   break;
            case KAKAO  : redirectURL = kakaoOauth.getOauthRedirectURL();    break;
            case NAVER  : redirectURL = naverOauth.getOauthRedirectURL();    break;
            default:{
                throw new IllegalArgumentException("Unknown Social Login Type");
            }
        }

        return redirectURL;
    }

    public UserResponse oauthLogin(SocialLoginType socialLoginType, String code, String state) {
        UserResponse userResponse;
        
        try {
        switch (socialLoginType){
            case GOOGLE:{
                ResponseEntity<String> accessTokenResponse= googleOauth.requestAccessToken(code);
                GoogleOauthToken oauthToken = googleOauth.getAccessToken(accessTokenResponse);
                ResponseEntity<String> userInfoResponse=googleOauth.requestUserInfo(oauthToken);
                GoogleUser googleUser= googleOauth.getUserInfo(userInfoResponse);                
                userResponse = UserResponse.builder()
                        .status("Login Success")
                        .token(loginTokenManager.genToken(User.builder()
                                                            .userID(googleUser.id)
                                                            .email(googleUser.email)
                                                            .platform("GOOGLE")
                                                            .build()))
                        .email(googleUser.email)
                        .platform("GOOGLE")
                        .build();
            }
            break;
            case KAKAO:{
                ResponseEntity<String> accessTokenResponse= kakaoOauth.requestAccessToken(code);
                KakaoOauthToken oauthToken = kakaoOauth.getAccessToken(accessTokenResponse);
                ResponseEntity<String> userInfoResponse=kakaoOauth.requestUserInfo(oauthToken);
                KakaoUser kakaoUser = kakaoOauth.getUserInfo(userInfoResponse);
                userResponse = UserResponse.builder()
                        .status("Login Success")
                        .token(loginTokenManager.genToken(User.builder()
                                                            .userID(String.valueOf(kakaoUser.id))
                                                            .email(kakaoUser.kakao_account.email)
                                                            .platform("KAKAO")
                                                            .build()))
                        .email(kakaoUser.kakao_account.email)
                        .platform("KAKAO")
                        .build();
            }
            break;
            case NAVER:{
                ResponseEntity<String> accessTokenResponse= naverOauth.requestAccessToken(code,state);
                NaverOauthToken oauthToken = naverOauth.getAccessToken(accessTokenResponse);
                ResponseEntity<String> userInfoResponse=naverOauth.requestUserInfo(oauthToken);
                NaverUser naverUser = naverOauth.getUserInfo(userInfoResponse);
                userResponse = UserResponse.builder()
                        .status("Login Success")
                        .token(loginTokenManager.genToken(User.builder()
                                                            .userID(naverUser.response.id)
                                                            .email(naverUser.response.email)
                                                            .platform("NAVER")
                                                            .build()))
                        .email(naverUser.response.email)
                        .platform("NAVER")
                        .build();
            }
            break;
            default:{
                userResponse = UserResponse.builder()
                        .status("Login Failed :Unknown Social Login Type")
                        .build();
            }
        }            
        } catch (Exception e) {
            userResponse = UserResponse.builder()
                                        .status("Login Failed")
                                        .build();
        }
        return userResponse;
    }
    public UserResponse oauthVerifyToken(String token) throws IOException {
        UserResponse userResponse;
        if(token == null){
            userResponse = UserResponse.builder()
                                        .status("Verify Fail :Empty Token")
                                        .build();
        } else if(loginTokenManager.isValid(token)){
            Claims claims = loginTokenManager.getClaims(token);

            userResponse = UserResponse.builder()
                                        .status("Verify Success :Token Refreshed")
                                        .token(loginTokenManager.refreshToken(token))
                                        .email(claims.get("email",String.class))
                                        .platform(claims.get("platform",String.class))
                                        .build();
        } else {            
            userResponse = UserResponse.builder()
                                        .status("Verify Fail :Invaild Token")
                                        .build();
        }
        return userResponse;
    }
}
