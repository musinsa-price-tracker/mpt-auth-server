package com.mpt.authservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserResponse {
    private String status;
    private String token;
    private String email;
    private String platform;
}
