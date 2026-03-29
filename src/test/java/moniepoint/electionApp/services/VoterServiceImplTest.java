package moniepoint.electionApp.services;

import moniepoint.electionApp.data.models.Voter;
import moniepoint.electionApp.data.repositories.VoterRepository;
import moniepoint.electionApp.dtos.requests.VoterRegistrationRequest;
import moniepoint.electionApp.exceptions.DuplicateVoterException;
import moniepoint.electionApp.exceptions.InvalidLoginDetailsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoterServiceImplTest {

    @Mock
    private VoterRepository voterRepository;

    @InjectMocks
    private VoterServiceImpl voterService;

    @Test
    void registerVoter_registersNewVoterSuccessfully() {
        VoterRegistrationRequest request = VoterRegistrationRequest.builder()
                .firstName("Ada")
                .lastName("Obi")
                .email("ada@example.com")
                .password("pass123")
                .vin("VIN-100")
                .state("Lagos")
                .build();

        when(voterRepository.findByEmail("ada@example.com")).thenReturn(Optional.empty());
        when(voterRepository.findByVin("VIN-100")).thenReturn(Optional.empty());
        when(voterRepository.save(any(Voter.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = voterService.registerVoter(request);

        assertEquals("ada@example.com", response.getEmail());
        assertFalse(response.isLoggedIn());
    }

    @Test
    void registerVoter_throwsWhenEmailExists() {
        VoterRegistrationRequest request = VoterRegistrationRequest.builder()
                .firstName("Ada")
                .lastName("Obi")
                .email("ada@example.com")
                .password("pass123")
                .vin("VIN-100")
                .state("Lagos")
                .build();

        when(voterRepository.findByEmail("ada@example.com"))
                .thenReturn(Optional.of(Voter.builder().email("ada@example.com").build()));

        assertThrows(DuplicateVoterException.class, () -> voterService.registerVoter(request));
    }

    @Test
    void login_logsInWithValidCredentials() {
        Voter saved = Voter.builder()
                .email("ada@example.com")
                .password("pass123")
                .isLoggedIn(false)
                .build();

        when(voterRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(saved));
        when(voterRepository.save(any(Voter.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = voterService.login("ada@example.com", "pass123");

        assertTrue(response.isLoggedIn());
        assertEquals("ada@example.com", response.getEmail());
    }

    @Test
    void login_throwsWhenCredentialsAreInvalid() {
        when(voterRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidLoginDetailsException.class,
                () -> voterService.login("missing@example.com", "pass123"));
    }

    @Test
    void logout_logsOutExistingVoter() {
        Voter saved = Voter.builder()
                .email("ada@example.com")
                .password("pass123")
                .isLoggedIn(true)
                .build();

        when(voterRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(saved));
        when(voterRepository.save(any(Voter.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = voterService.logout("ada@example.com");

        assertFalse(response.isLoggedIn());
        assertEquals("ada@example.com", response.getEmail());
    }
}
