package moniepoint.electionApp.services;

import moniepoint.electionApp.dtos.requests.CastVoteRequest;
import moniepoint.electionApp.dtos.responses.CandidateResponse;
import moniepoint.electionApp.dtos.responses.CastVoteResponse;
import moniepoint.electionApp.dtos.responses.ElectionResultsResponse;
import moniepoint.electionApp.dtos.responses.ElectionSummaryResponse;

import java.util.List;

public interface ElectionService {
    List<ElectionSummaryResponse> listElections();

    List<CandidateResponse> getCandidates(String electionId);

    CastVoteResponse castVote(String voterEmail, CastVoteRequest request);

    ElectionResultsResponse getResults(String electionId);
}
