package moniepoint.electionApp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectionResultsResponse {
    private String electionId;
    private long totalVotes;
    private List<ResultTallyResponse> tallies;
}
