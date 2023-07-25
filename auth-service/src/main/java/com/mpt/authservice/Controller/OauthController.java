package com.mpt.authservice.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mpt.authservice.Dto.UserResponse;
import com.mpt.authservice.Service.OauthService;
import com.mpt.authservice.SocialLogin.SocialLoginType;

@RestController
public class OauthController {
    @Autowired
    OauthService oauthService;

    @GetMapping("/api/login/{socialLoginType}")
    public ResponseEntity<String> socialLogin(@PathVariable(name="socialLoginType") String SocialLoginPath) throws IOException {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
        
        return ResponseEntity.ok().body(oauthService.request(socialLoginType));
    }

    @GetMapping("/api/login/{socialLoginType}/redirection")
    public ResponseEntity<UserResponse> socialLoginRedirect(@PathVariable(name="socialLoginType") String SocialLoginPath, @RequestParam(name = "code") String code, @RequestParam(name = "state") String state) throws IOException {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
        UserResponse userResponse = oauthService.oauthLogin(socialLoginType,code,state);
        return ResponseEntity.ok().body(userResponse);
    }

	// 파라미터로 전달받은 토큰 처리
    @GetMapping("/api/token")
    public ResponseEntity<UserResponse> validCheck(@RequestParam(name = "ACCESS_TOKEN", required = false) String token) throws IOException {
        UserResponse userResponse = oauthService.oauthVerifyToken(token);
        return ResponseEntity.ok().body(userResponse);
    }

    // 헤더로 전달받은 토큰 처리
    @GetMapping("/api/access_token_info")
    public ResponseEntity<UserResponse> getAccessTokenInfo(@RequestHeader HttpHeaders httpHeaders) throws IOException {
        String token = httpHeaders.getFirst("Authorization");
        if(token.toLowerCase().startsWith("Bearer".toLowerCase())) {
            token = token.substring("Bearer".length()).trim();
        }

        UserResponse userResponse = oauthService.oauthVerifyToken(token);
        System.out.println(userResponse);
        return ResponseEntity.ok().body(userResponse);
    }
}
