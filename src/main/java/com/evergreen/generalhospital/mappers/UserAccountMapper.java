package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.useraccount.UserAccountResponse;
import com.evergreen.generalhospital.dto.useraccount.UserAccountSummaryResponse;
import com.evergreen.generalhospital.models.useraccount.UserAccount;

public class UserAccountMapper {
    
    public static UserAccountResponse userAccountToUserAccountResponse(UserAccount user) {
        return new UserAccountResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getProvider(),
                user.getProviderSubject(),
                user.getRoles().stream().toList(),
                user.getEmail()
        );
    }

    public static UserAccountSummaryResponse userAccountToUserAccountSummaryResponse(UserAccount user) {
        return new UserAccountSummaryResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
