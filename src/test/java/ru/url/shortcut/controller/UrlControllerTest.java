package ru.url.shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.url.shortcut.dto.AuthResponse;
import ru.url.shortcut.dto.RegistrationResponse;
import ru.url.shortcut.service.SiteService;
import ru.url.shortcut.service.UrlService;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UrlControllerTest {

    private static final String SITE = "job4j.ru";
    private static final String URL = "https://job4j.ru/profile/exercise/106/task-view/532";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SiteService siteService;

    @Autowired
    private UrlService urlService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    void clear() {
        urlService.deleteAll();
        siteService.deleteAll();
    }

    @Test
    void whenRegisterThenReturnCredentials() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "site":"%s"
                                }
                                """.formatted(SITE)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").isNotEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    void whenConvertWithoutTokenThenUnauthorized() throws Exception {
        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url":"%s"
                                }
                                """.formatted(URL)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAuthorizedUserConvertUrlThenStatisticReturned() throws Exception {
        String token = token(register());

        mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "url":"%s"
                            }
                            """.formatted(URL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty());

        mockMvc.perform(get("/statistic/" + SITE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].url").value(URL));
    }

    @Test
    void whenRedirectThenFoundAndStatisticIncremented() throws Exception {
        String token = token(register());
        String response = mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "url":"%s"
                            }
                            """.formatted(URL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String shortCode = objectMapper.readTree(response)
                .get("code")
                .asText();

        mockMvc.perform(get("/redirect/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, URL));

        mockMvc.perform(get("/statistic/" + SITE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].url").value(URL))
                .andExpect(jsonPath("$[0].total").value(1));
    }

    private RegistrationResponse register() throws Exception {
        String response = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "site":"%s"
                                }
                                """.formatted(SITE)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, RegistrationResponse.class);
    }

    private String token(RegistrationResponse registration) throws Exception {
        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "login":"%s",
                                    "password":"%s"
                                }
                                """.formatted(
                                registration.login(),
                                registration.password()
                        )))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, AuthResponse.class)
                .token();
    }
}