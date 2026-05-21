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
import ru.url.shortcut.repository.SiteDataRepository;
import ru.url.shortcut.repository.UserDataRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SiteControllerTest {
    private static final String SITE = "job4j.ru";
    private static final String URL = "https://job4j.ru/profile/exercise/106/task-view/532";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SiteDataRepository siteDataRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @AfterEach
    void clear() {
        siteDataRepository.deleteAll();
        userDataRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void whenRegisterThenReturnCredentials() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"site\":\"" + SITE + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registration").value(true))
                .andExpect(jsonPath("$.login").isNotEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    void whenRegisterExistingSiteThenRegistrationFalse() throws Exception {
        register();
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"site\":\"" + SITE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(false));
    }

    @Test
    void whenConvertWithoutTokenThenUnauthorized() throws Exception {
        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + URL + "\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenConvertWithTokenThenReturnCodeAndStatistic() throws Exception {
        String token = token(register());
        mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + URL + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty());
        mockMvc.perform(get("/statistic")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenRedirectThenFoundAndStatisticIncremented() throws Exception {
        String token = token(register());
        String code = mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + URL + "\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String shortCode = objectMapper.readTree(code).get("code").asText();

        mockMvc.perform(get("/redirect/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, URL));

        mockMvc.perform(get("/statistic")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].url").value(URL))
                .andExpect(jsonPath("$[0].total").value(1));
    }

    private RegistrationResponse register() throws Exception {
        String response = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"site\":\"" + SITE + "\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, RegistrationResponse.class);
    }

    private String token(RegistrationResponse registration) throws Exception {
        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"%s","password":"%s"}
                                """.formatted(registration.login(), registration.password())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, AuthResponse.class).token();
    }
}
