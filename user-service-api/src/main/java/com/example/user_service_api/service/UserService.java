package com.example.user_service_api.service;

import com.example.user_service_api.commons.entities.UserModel;

public interface UserService {

    UserModel getUser(Long userId);

    void updateUser(Long userId, UserModel gameRequest);

    void deleteUser(Long userId);
}
