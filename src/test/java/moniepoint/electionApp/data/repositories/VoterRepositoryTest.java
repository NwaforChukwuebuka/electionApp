package moniepoint.electionApp.data.repositories;

import moniepoint.electionApp.data.models.Voter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class VoterRepositoryTest {

    @Autowired
    private VoterRepository voterRepository;

    @BeforeEach
    void setUp() {
        voterRepository.deleteAll();
    }

    @Test
    void findByEmail_returnsSavedVoter() {
        Voter voter = Voter.builder()
                .firstName("Ada")
                .lastName("Obi")
                .email("ada@example.com")
                .vin("VIN-001")
                .password("pass123")
                .state("Lagos")
                .isLoggedIn(false)
                .build();
        voterRepository.save(voter);

        var found = voterRepository.findByEmail("ada@example.com");

        assertTrue(found.isPresent());
        assertEquals("VIN-001", found.get().getVin());
    }

    @Test
    void findByVin_returnsSavedVoter() {
        Voter voter = Voter.builder()
                .firstName("Bola")
                .lastName("Daniels")
                .email("bola@example.com")
                .vin("VIN-002")
                .password("pass123")
                .state("Abuja")
                .isLoggedIn(false)
                .build();
        voterRepository.save(voter);

        var found = voterRepository.findByVin("VIN-002");

        assertTrue(found.isPresent());
        assertEquals("bola@example.com", found.get().getEmail());
    }

    @Test
    void findByEmail_returnsEmptyWhenNotFound() {
        var found = voterRepository.findByEmail("missing@example.com");
        assertTrue(found.isEmpty());
    }
}
