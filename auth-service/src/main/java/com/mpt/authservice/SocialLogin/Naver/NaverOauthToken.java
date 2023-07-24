package com.mpt.authservice.SocialLogin.Naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NaverOauthToken {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private int expires_in;
}
