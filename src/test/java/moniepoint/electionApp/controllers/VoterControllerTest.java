package moniepoint.electionApp.controllers;

import tools.jackson.databind.json.JsonMapper;
import moniepoint.electionApp.dtos.requests.VoterRegistrationRequest;
import moniepoint.electionApp.dtos.responses.LoginResponse;
import moniepoint.electionApp.dtos.responses.LogoutResponse;
import moniepoint.electionApp.dtos.responses.VoterRegistrationResponse;
import moniepoint.electionApp.exceptions.DuplicateVoterException;
import moniepoint.electionApp.exceptions.GlobalExceptionHandler;
import moniepoint.electionApp.exceptions.InvalidLoginDetailsException;
import moniepoint.electionApp.services.VoterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VoterController.class)
@Import(GlobalExceptionHandler.class)
class VoterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private VoterService voterService;

    @Test
    void register_returnsCreatedWithWrappedResponse() throws Exception {
        VoterRegistrationRequest body = VoterRegistrationRequest.builder()
                .firstName("Ada")
                .lastName("Obi")
                .email("ada@example.com")
                .password("pass123")
                .vin("VIN-100")
                .state("Lagos")
                .build();

        when(voterService.registerVoter(any(VoterRegistrationRequest.class)))
                .thenReturn(VoterRegistrationResponse.builder()
                        .email("ada@example.com")
                        .isLoggedIn(false)
                        .build());

        mockMvc.perform(post("/voter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("ada@example.com"));
    }

    @Test
    void register_returnsBadRequestWhenDuplicateEmail() throws Exception {
        VoterRegistrationRequest body = VoterRegistrationRequest.builder()
                .firstName("Ada")
                .lastName("Obi")
                .email("dup@example.com")
                .password("pass123")
                .vin("VIN-200")
                .state("Lagos")
                .build();

        when(voterService.registerVoter(any(VoterRegistrationRequest.class)))
                .thenThrow(new DuplicateVoterException("Voter with email dup@example.com already exists"));

        mockMvc.perform(post("/voter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value("Voter with email dup@example.com already exists"));
    }

    @Test
    void login_returnsCreatedWhenCredentialsValid() throws Exception {
        when(voterService.login(eq("ada@example.com"), eq("pass123")))
                .thenReturn(LoginResponse.builder()
                        .email("ada@example.com")
                        .isLoggedIn(true)
                        .build());

        mockMvc.perform(patch("/voter/ada@example.com/pass123"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("ada@example.com"));
    }

    @Test
    void login_returnsBadRequestWhenInvalid() throws Exception {
        when(voterService.login(eq("missing@example.com"), eq("x")))
                .thenThrow(new InvalidLoginDetailsException("Invalid login details"));

        mockMvc.perform(patch("/voter/missing@example.com/x"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value("Invalid login details"));
    }

    @Test
    void logout_returnsCreatedWhenVoterExists() throws Exception {
        when(voterService.logout("ada@example.com"))
                .thenReturn(LogoutResponse.builder()
                        .email("ada@example.com")
                        .isLoggedIn(false)
                        .build());

        mockMvc.perform(patch("/voter/ada@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("ada@example.com"));
    }

    @Test
    void logout_returnsBadRequestWhenVoterMissing() throws Exception {
        when(voterService.logout("ghost@example.com"))
                .thenThrow(new InvalidLoginDetailsException("Voter with email ghost@example.com does not exist"));

        mockMvc.perform(patch("/voter/ghost@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
