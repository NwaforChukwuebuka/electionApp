package moniepoint.electionApp.controllers;

import lombok.RequiredArgsConstructor;
import moniepoint.electionApp.dtos.requests.CastVoteRequest;
import moniepoint.electionApp.dtos.responses.ApiResponse;
import moniepoint.electionApp.services.ElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping("/elections")
    public ResponseEntity<ApiResponse> listElections() {
        return ResponseEntity.ok(new ApiResponse(true, electionService.listElections()));
    }

    @GetMapping("/elections/{electionId}/candidates")
    public ResponseEntity<ApiResponse> getCandidates(@PathVariable String electionId) {
        return ResponseEntity.ok(new ApiResponse(true, electionService.getCandidates(electionId)));
    }

    @PatchMapping("/elections/vote/{voterEmail}")
    public ResponseEntity<ApiResponse> castVote(
            @PathVariable String voterEmail,
            @RequestBody CastVoteRequest request) {
        return new ResponseEntity<>(
                new ApiResponse(true, electionService.castVote(voterEmail, request)),
                HttpStatus.CREATED);
    }

    @GetMapping("/elections/{electionId}/results")
    public ResponseEntity<ApiResponse> getResults(@PathVariable String electionId) {
        return ResponseEntity.ok(new ApiResponse(true, electionService.getResults(electionId)));
    }
}
