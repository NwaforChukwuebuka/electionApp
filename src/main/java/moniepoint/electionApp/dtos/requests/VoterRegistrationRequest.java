package moniepoint.electionApp.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoterRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String vin;
    private String state;
}
