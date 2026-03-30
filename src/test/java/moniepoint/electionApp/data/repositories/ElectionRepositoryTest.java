package moniepoint.electionApp.data.repositories;

import moniepoint.electionApp.data.models.Election;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ElectionRepositoryTest {

    @Autowired
    private ElectionRepository electionRepository;

    @BeforeEach
    void setUp() {
        electionRepository.deleteAll();
    }

    @Test
    void findById_returnsSavedElection() {
        Instant opens = Instant.parse("2026-04-01T08:00:00Z");
        Instant closes = Instant.parse("2026-04-01T18:00:00Z");
        Election election = Election.builder()
                .title("Governorship 2026")
                .level("STATE")
                .votingOpensAt(opens)
                .votingClosesAt(closes)
                .resultsPublished(false)
                .build();
        Election saved = electionRepository.save(election);

        var found = electionRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Governorship 2026", found.get().getTitle());
        assertEquals("STATE", found.get().getLevel());
        assertEquals(opens, found.get().getVotingOpensAt());
        assertEquals(closes, found.get().getVotingClosesAt());
        assertFalse(found.get().isResultsPublished());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        var found = electionRepository.findById("nonexistent-id");
        assertTrue(found.isEmpty());
    }
}
