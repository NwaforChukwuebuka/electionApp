package moniepoint.electionApp.services;

import moniepoint.electionApp.data.models.Voter;
import moniepoint.electionApp.data.repositories.VoterRepository;
import moniepoint.electionApp.dtos.requests.VoterRegistrationRequest;
import moniepoint.electionApp.dtos.responses.LoginResponse;
import moniepoint.electionApp.dtos.responses.LogoutResponse;
import moniepoint.electionApp.dtos.responses.VoterRegistrationResponse;
import moniepoint.electionApp.exceptions.DuplicateVoterException;
import moniepoint.electionApp.exceptions.InvalidLoginDetailsException;
import moniepoint.electionApp.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoterServiceImpl implements VoterService {

    private final VoterRepository voterRepository;

    @Override
    public VoterRegistrationResponse registerVoter(VoterRegistrationRequest request) {
        if (voterRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateVoterException("Voter with email " + request.getEmail() + " already exists");
        }
        if (voterRepository.findByVin(request.getVin()).isPresent()) {
            throw new DuplicateVoterException("Voter with vin " + request.getVin() + " already exists");
        }

        Voter voter = Mapper.map(request);
        Voter savedVoter = voterRepository.save(voter);
        return Mapper.mapToRegistrationResponse(savedVoter);
    }

    @Override
    public LoginResponse login(String email, String password) {
        Voter voter = voterRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidLoginDetailsException("Invalid login details"));

        if (!voter.getPassword().equals(password)) {
            throw new InvalidLoginDetailsException("Invalid login details");
        }

        voter.setLoggedIn(true);
        Voter savedVoter = voterRepository.save(voter);
        return Mapper.mapToLoginResponse(savedVoter);
    }

    @Override
    public LogoutResponse logout(String email) {
        Voter voter = voterRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidLoginDetailsException("Voter with email " + email + " does not exist"));

        voter.setLoggedIn(false);
        Voter savedVoter = voterRepository.save(voter);
        return Mapper.mapToLogoutResponse(savedVoter);
    }
}
