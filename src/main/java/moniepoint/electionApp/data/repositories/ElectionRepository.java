package moniepoint.electionApp.data.repositories;

import moniepoint.electionApp.data.models.Election;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ElectionRepository extends MongoRepository<Election, String> {
}
