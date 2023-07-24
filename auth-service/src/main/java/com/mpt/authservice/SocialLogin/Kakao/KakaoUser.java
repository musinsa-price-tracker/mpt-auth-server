package com.mpt.authservice.SocialLogin.Kakao;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class KakaoUser {
    public long id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;

    public class Properties{
        public String nickname;
    }

    public class KakaoAccount{
        public boolean profile_nickname_needs_agreement;
        public Profile profile;
        public boolean has_email;
        public boolean email_needs_agreement;
        public boolean is_email_valid;
        public boolean is_email_verified;
        public String email;
        public class Profile {
            public String nickname;
        }
    }
}
