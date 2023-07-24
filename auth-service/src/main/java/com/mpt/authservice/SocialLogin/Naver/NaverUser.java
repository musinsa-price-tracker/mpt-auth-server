package com.mpt.authservice.SocialLogin.Naver;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NaverUser {
    public String resultcode;
    public String message;
    public Response response;

    public class Response {
        public String id;
        public String email;
        public String name;    
    }
}
