package moniepoint.electionApp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectionSummaryResponse {
    private String id;
    private String title;
    private String level;
    private Instant votingOpensAt;
    private Instant votingClosesAt;
    private boolean resultsPublished;
}
