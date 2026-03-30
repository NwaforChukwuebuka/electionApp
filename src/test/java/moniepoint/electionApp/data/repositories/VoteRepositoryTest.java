package moniepoint.electionApp.data.repositories;

import moniepoint.electionApp.data.models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class VoteRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @BeforeEach
    void setUp() {
        voteRepository.deleteAll();
    }

    @Test
    void existsByElectionIdAndVoterId_trueAfterSave() {
        voteRepository.save(Vote.builder()
                .electionId("el-1")
                .voterId("voter-1")
                .candidateId("cand-1")
                .castAt(Instant.now())
                .build());

        assertTrue(voteRepository.existsByElectionIdAndVoterId("el-1", "voter-1"));
    }

    @Test
    void existsByElectionIdAndVoterId_falseWhenNoVote() {
        assertFalse(voteRepository.existsByElectionIdAndVoterId("el-1", "voter-1"));
    }
}
