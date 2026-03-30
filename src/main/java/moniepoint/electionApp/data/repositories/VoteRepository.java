package moniepoint.electionApp.data.repositories;

import moniepoint.electionApp.data.models.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoteRepository extends MongoRepository<Vote, String> {
    boolean existsByElectionIdAndVoterId(String electionId, String voterId);
}
