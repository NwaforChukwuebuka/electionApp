package moniepoint.electionApp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultTallyResponse {
    private String candidateId;
    private String candidateName;
    private String partyCode;
    private long voteCount;
}
