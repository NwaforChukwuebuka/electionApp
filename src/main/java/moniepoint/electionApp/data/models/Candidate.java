package moniepoint.electionApp.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("candidates")
public class Candidate {
    @Id
    private String id;

    @Indexed
    private String electionId;

    private String name;
    private String partyCode;
    private String position;
}
