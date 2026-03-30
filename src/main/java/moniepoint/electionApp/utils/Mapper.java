package moniepoint.electionApp.utils;

import moniepoint.electionApp.data.models.Candidate;
import moniepoint.electionApp.data.models.Election;
import moniepoint.electionApp.data.models.Voter;
import moniepoint.electionApp.dtos.requests.VoterRegistrationRequest;
import moniepoint.electionApp.dtos.responses.CandidateResponse;
import moniepoint.electionApp.dtos.responses.ElectionSummaryResponse;
import moniepoint.electionApp.dtos.responses.LoginResponse;
import moniepoint.electionApp.dtos.responses.LogoutResponse;
import moniepoint.electionApp.dtos.responses.VoterRegistrationResponse;

public class Mapper {
    private Mapper() {
    }

    public static Voter map(VoterRegistrationRequest request) {
        return Voter.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .vin(request.getVin())
                .password(request.getPassword())
                .state(request.getState())
                .isLoggedIn(false)
                .build();
    }

    public static VoterRegistrationResponse mapToRegistrationResponse(Voter voter) {
        return VoterRegistrationResponse.builder()
                .email(voter.getEmail())
                .isLoggedIn(voter.isLoggedIn())
                .build();
    }

    public static LoginResponse mapToLoginResponse(Voter voter) {
        return LoginResponse.builder()
                .email(voter.getEmail())
                .isLoggedIn(voter.isLoggedIn())
                .build();
    }

    public static LogoutResponse mapToLogoutResponse(Voter voter) {
        return LogoutResponse.builder()
                .email(voter.getEmail())
                .isLoggedIn(voter.isLoggedIn())
                .build();
    }

    public static ElectionSummaryResponse toElectionSummary(Election election) {
        return ElectionSummaryResponse.builder()
                .id(election.getId())
                .title(election.getTitle())
                .level(election.getLevel())
                .votingOpensAt(election.getVotingOpensAt())
                .votingClosesAt(election.getVotingClosesAt())
                .resultsPublished(election.isResultsPublished())
                .build();
    }

    public static CandidateResponse toCandidateResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .partyCode(candidate.getPartyCode())
                .position(candidate.getPosition())
                .build();
    }
}
