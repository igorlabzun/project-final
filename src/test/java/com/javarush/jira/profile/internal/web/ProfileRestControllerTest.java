package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Set;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.login.internal.web.UserTestData.ADMIN_MAIL;
import static com.javarush.jira.login.internal.web.UserTestData.USER_MAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String PROFILE_REST_URL = ProfileRestController.REST_URL;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getSuccess() throws Exception {
        perform(get(PROFILE_REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(USER_MAIL));
    }

    @Test
    void getUnauthorized() throws Exception {
        perform(get(PROFILE_REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateSuccess() throws Exception {
        ProfileTo updatedProfile = new ProfileTo(null,
                Set.of("news", "alerts"), // Корректные mailNotifications
                Set.of(new ContactTo("email", "new-email@example.com"))
        );

        perform(put(PROFILE_REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedProfile)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUnauthorized() throws Exception {
        ProfileTo updatedProfile = new ProfileTo(null,
                Set.of("news", "alerts"), // Корректные mailNotifications
                Set.of(new ContactTo("email", "new-email@example.com"))
        );

        perform(put(PROFILE_REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedProfile)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateInvalid() throws Exception {
        ProfileTo invalidProfile = new ProfileTo(null,
                Set.of(""),
                Set.of(new ContactTo(null, ""))
        );

        perform(put(PROFILE_REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidProfile)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.details").exists());
    }
}