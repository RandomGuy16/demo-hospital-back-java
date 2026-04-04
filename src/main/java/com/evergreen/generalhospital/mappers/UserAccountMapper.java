package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.UserAccountResponse;
import com.evergreen.generalhospital.models.useraccount.UserAccount;

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
