package moniepoint.electionApp.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("elections")
public class Election {
    @Id
    private String id;

    private String title;

    private String level;

    private Instant votingOpensAt;
    private Instant votingClosesAt;

    private boolean resultsPublished;
}
