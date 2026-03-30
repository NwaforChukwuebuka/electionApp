package moniepoint.electionApp.services;

import lombok.RequiredArgsConstructor;
import moniepoint.electionApp.data.models.Candidate;
import moniepoint.electionApp.data.models.Election;
import moniepoint.electionApp.data.models.Vote;
import moniepoint.electionApp.data.models.Voter;
import moniepoint.electionApp.data.repositories.CandidateRepository;
import moniepoint.electionApp.data.repositories.ElectionRepository;
import moniepoint.electionApp.data.repositories.VoteRepository;
import moniepoint.electionApp.data.repositories.VoterRepository;
import moniepoint.electionApp.dtos.requests.CastVoteRequest;
import moniepoint.electionApp.dtos.responses.CandidateResponse;
import moniepoint.electionApp.dtos.responses.CastVoteResponse;
import moniepoint.electionApp.dtos.responses.ElectionResultsResponse;
import moniepoint.electionApp.dtos.responses.ElectionSummaryResponse;
import moniepoint.electionApp.dtos.responses.ResultTallyResponse;
import moniepoint.electionApp.exceptions.AlreadyVotedException;
import moniepoint.electionApp.exceptions.ElectionNotFoundException;
import moniepoint.electionApp.exceptions.InvalidCandidateException;
import moniepoint.electionApp.exceptions.InvalidLoginDetailsException;
import moniepoint.electionApp.exceptions.VotingClosedException;
import moniepoint.electionApp.utils.Mapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;

    @Override
    public List<ElectionSummaryResponse> listElections() {
        return electionRepository.findAll().stream()
                .map(Mapper::toElectionSummary)
                .toList();
    }

    @Override
    public List<CandidateResponse> getCandidates(String electionId) {
        requireElection(electionId);
        return candidateRepository.findByElectionId(electionId).stream()
                .map(Mapper::toCandidateResponse)
                .toList();
    }

    @Override
    public CastVoteResponse castVote(String voterEmail, CastVoteRequest request) {
        Voter voter = voterRepository.findByEmail(voterEmail)
                .orElseThrow(() -> new InvalidLoginDetailsException("Voter not found"));
        if (!voter.isLoggedIn()) {
            throw new InvalidLoginDetailsException("You must log in to vote");
        }

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ElectionNotFoundException("Election not found"));
        assertVotingOpen(election);

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new InvalidCandidateException("Invalid candidate"));
        if (!election.getId().equals(candidate.getElectionId())) {
            throw new InvalidCandidateException("Candidate does not belong to this election");
        }

        if (voteRepository.existsByElectionIdAndVoterId(election.getId(), voter.getId())) {
            throw new AlreadyVotedException("You have already voted in this election");
        }

        voteRepository.save(Vote.builder()
                .electionId(election.getId())
                .voterId(voter.getId())
                .candidateId(candidate.getId())
                .castAt(Instant.now())
                .build());

        return CastVoteResponse.builder()
                .message("Vote recorded")
                .electionId(election.getId())
                .build();
    }

    @Override
    public ElectionResultsResponse getResults(String electionId) {
        requireElection(electionId);

        List<Vote> votes = voteRepository.findByElectionId(electionId);
        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);
        Map<String, Long> counts = votes.stream()
                .collect(Collectors.groupingBy(Vote::getCandidateId, Collectors.counting()));

        List<ResultTallyResponse> tallies = candidates.stream()
                .map(c -> ResultTallyResponse.builder()
                        .candidateId(c.getId())
                        .candidateName(c.getName())
                        .partyCode(c.getPartyCode())
                        .voteCount(counts.getOrDefault(c.getId(), 0L))
                        .build())
                .toList();

        return ElectionResultsResponse.builder()
                .electionId(electionId)
                .totalVotes(votes.size())
                .tallies(tallies)
                .build();
    }

    private Election requireElection(String electionId) {
        return electionRepository.findById(electionId)
                .orElseThrow(() -> new ElectionNotFoundException("Election not found"));
    }

    private static void assertVotingOpen(Election election) {
        Instant now = Instant.now();
        if (now.isBefore(election.getVotingOpensAt()) || now.isAfter(election.getVotingClosesAt())) {
            throw new VotingClosedException("Voting is not open for this election");
        }
    }
}
