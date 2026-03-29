package moniepoint.electionApp.controllers;

import lombok.RequiredArgsConstructor;
import moniepoint.electionApp.dtos.requests.VoterRegistrationRequest;
import moniepoint.electionApp.dtos.responses.ApiResponse;
import moniepoint.electionApp.services.VoterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VoterController {

    private final VoterService voterService;

    @PostMapping("/voter")
    public ResponseEntity<ApiResponse> register(@RequestBody VoterRegistrationRequest request) {
        return new ResponseEntity<>(
                new ApiResponse(true, voterService.registerVoter(request)),
                HttpStatus.CREATED);
    }

    @PatchMapping("/voter/{email}/{password}")
    public ResponseEntity<ApiResponse> login(@PathVariable String email, @PathVariable String password) {
        return new ResponseEntity<>(
                new ApiResponse(true, voterService.login(email, password)),
                HttpStatus.CREATED);
    }

    @PatchMapping("/voter/{email}")
    public ResponseEntity<ApiResponse> logout(@PathVariable String email) {
        return new ResponseEntity<>(
                new ApiResponse(true, voterService.logout(email)),
                HttpStatus.CREATED);
    }
}
