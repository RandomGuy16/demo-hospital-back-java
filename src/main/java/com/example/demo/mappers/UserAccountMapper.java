package com.example.demo.mappers;

import com.example.demo.dto.UserAccountResponse;
import com.example.demo.models.useraccount.UserAccount;

public class UserAccountMapper {
    
    public static UserAccountResponse userAccountToUserAccountResponse(UserAccount user) {
        return new UserAccountResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getProvider(),
                user.getProviderSubject(),
                user.getRole(),
                user.getEmail()
        );
    }
}
