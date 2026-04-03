package com.example.demo.testsupport.fixtures;

import com.example.demo.models.useraccount.Role;
import com.example.demo.models.useraccount.UserAccount;
import com.example.demo.testsupport.factories.UserAccountFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class AuthFixtures {

    @Autowired
    private UserAccountFactory userAccountFactory;

    public record TestUserSubjects(
        UserAccount admin,
        UserAccount receptionist
    ) {}

    public TestUserSubjects defaultUserSubjects;

    /**
     * Seeds the standard local users used by auth endpoint tests.
     */
    public void seedUserSubjects() {
        UserAccount admin = userAccountFactory.saveUserAccount(
            "Admin User",
            "admin",
            null,
            null,
            "admin-subject",
            Role.ROLE_ADMIN,
            "admin@example.com",
            "admin-password"
        );

        UserAccount receptionist = userAccountFactory.saveUserAccount(
            "Receptionist User",
            "receptionist",
            null,
            null,
            "receptionist-subject",
            Role.ROLE_RECEPTIONIST,
            "receptionist@example.com",
            "receptionist-password"
        );

        defaultUserSubjects = new TestUserSubjects(admin, receptionist);
    }

}
