package moniepoint.electionApp.services;

import moniepoint.electionApp.dtos.requests.VoterRegistrationRequest;
import moniepoint.electionApp.dtos.responses.LoginResponse;
import moniepoint.electionApp.dtos.responses.LogoutResponse;
import moniepoint.electionApp.dtos.responses.VoterRegistrationResponse;

public interface VoterService {
    VoterRegistrationResponse registerVoter(VoterRegistrationRequest request);

    LoginResponse login(String email, String password);

    LogoutResponse logout(String email);
}
