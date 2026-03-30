package moniepoint.electionApp.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("votes")
public class Vote {
    @Id
    private String id;

    @Indexed
    private String electionId;

    @Indexed
    private String voterId;

    private String candidateId;
    private Instant castAt;
}
