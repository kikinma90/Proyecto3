package com.example.user_service_api.commons.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogin {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
