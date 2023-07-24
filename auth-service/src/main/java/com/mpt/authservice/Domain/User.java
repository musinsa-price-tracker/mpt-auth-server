package com.mpt.authservice.Domain;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    private String userID;
    private String email;
    private String platform;

    @Builder
    public User(String userID, String email, String platform ) {
        this.userID = userID;
        this.email = email;
        this.platform = platform;
    }

    public Map<String,Object> getHashMap(){
        Map<String,Object> claims = new HashMap<>();

        claims.put("userID",userID);
        claims.put("email",email);
        claims.put("platform", platform);

        return claims;
    }
}
