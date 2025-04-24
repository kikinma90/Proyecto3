package com.example.user_service_api.controller;

import com.example.user_service_api.commons.constants.ApiPathConstants;
import com.example.user_service_api.commons.entities.UserModel;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE)
@SecurityRequirement(name = "Bearer Authentication")
public interface UserApi {

    @GetMapping()
    ResponseEntity<UserModel> getUser(
            @RequestAttribute("X-User-Id") Long userId
    );

    @PutMapping()
    ResponseEntity<Void> updateUser(
            @RequestAttribute("X-User-Id") Long userId,
            @RequestBody UserModel gameRequest
    );

    @DeleteMapping()
    ResponseEntity<Void> deleteUser(
            @RequestAttribute("X-User-Id") Long userId
    );
}
