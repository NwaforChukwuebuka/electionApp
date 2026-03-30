package moniepoint.electionApp.data.repositories;

import moniepoint.electionApp.data.models.Candidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class CandidateRepositoryTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();
    }

    @Test
    void findByElectionId_returnsAllCandidatesForThatElection() {
        String electionA = "el-1";
        String electionB = "el-2";

        candidateRepository.save(Candidate.builder()
                .electionId(electionA)
                .name("Chidi Okonkwo")
                .partyCode("APC")
                .position("Governor")
                .build());
        candidateRepository.save(Candidate.builder()
                .electionId(electionA)
                .name("Amaka Nwosu")
                .partyCode("PDP")
                .position("Governor")
                .build());
        candidateRepository.save(Candidate.builder()
                .electionId(electionB)
                .name("Other Race")
                .partyCode("LP")
                .position("Senator")
                .build());

        List<Candidate> forA = candidateRepository.findByElectionId(electionA);

        assertEquals(2, forA.size());
        assertTrue(forA.stream().allMatch(c -> electionA.equals(c.getElectionId())));
    }

    @Test
    void findByElectionId_returnsEmptyWhenNoCandidates() {
        List<Candidate> found = candidateRepository.findByElectionId("unknown-election");
        assertTrue(found.isEmpty());
    }
}
