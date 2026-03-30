package moniepoint.electionApp.services;

import moniepoint.electionApp.data.models.Candidate;
import moniepoint.electionApp.data.models.Election;
import moniepoint.electionApp.data.models.Vote;
import moniepoint.electionApp.data.models.Voter;
import moniepoint.electionApp.data.repositories.CandidateRepository;
import moniepoint.electionApp.data.repositories.ElectionRepository;
import moniepoint.electionApp.data.repositories.VoteRepository;
import moniepoint.electionApp.data.repositories.VoterRepository;
import moniepoint.electionApp.dtos.requests.CastVoteRequest;
import moniepoint.electionApp.exceptions.AlreadyVotedException;
import moniepoint.electionApp.exceptions.ElectionNotFoundException;
import moniepoint.electionApp.exceptions.InvalidCandidateException;
import moniepoint.electionApp.exceptions.InvalidLoginDetailsException;
import moniepoint.electionApp.exceptions.VotingClosedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElectionServiceImplTest {

    private static final Instant WINDOW_OPEN_START = Instant.parse("2020-01-01T00:00:00Z");
    private static final Instant WINDOW_OPEN_END = Instant.parse("2030-01-01T00:00:00Z");

    @Mock
    private ElectionRepository electionRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private VoterRepository voterRepository;

    @InjectMocks
    private ElectionServiceImpl electionService;

    @Test
    void listElections_returnsSummaries() {
        Election election = Election.builder()
                .id("e1")
                .title("Gov 2026")
                .level("STATE")
                .votingOpensAt(WINDOW_OPEN_START)
                .votingClosesAt(WINDOW_OPEN_END)
                .resultsPublished(false)
                .build();
        when(electionRepository.findAll()).thenReturn(List.of(election));

        var list = electionService.listElections();

        assertEquals(1, list.size());
        assertEquals("e1", list.getFirst().getId());
        assertEquals("Gov 2026", list.getFirst().getTitle());
    }

    @Test
    void getCandidates_throwsWhenElectionMissing() {
        when(electionRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ElectionNotFoundException.class, () -> electionService.getCandidates("missing"));
    }

    @Test
    void getCandidates_returnsCandidatesForElection() {
        when(electionRepository.findById("e1")).thenReturn(Optional.of(openElection("e1")));
        when(candidateRepository.findByElectionId("e1")).thenReturn(List.of(
                Candidate.builder().id("c1").electionId("e1").name("A").partyCode("APC").position("Gov").build()));

        var list = electionService.getCandidates("e1");

        assertEquals(1, list.size());
        assertEquals("c1", list.getFirst().getId());
        assertEquals("APC", list.getFirst().getPartyCode());
    }

    @Test
    void castVote_recordsVoteWhenValid() {
        Voter voter = Voter.builder().id("v1").email("ada@x.com").isLoggedIn(true).build();
        Election election = openElection("e1");
        Candidate candidate = Candidate.builder().id("c1").electionId("e1").name("A").partyCode("APC").position("Gov").build();

        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById("e1")).thenReturn(Optional.of(election));
        when(candidateRepository.findById("c1")).thenReturn(Optional.of(candidate));
        when(voteRepository.existsByElectionIdAndVoterId("e1", "v1")).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = electionService.castVote("ada@x.com", CastVoteRequest.builder()
                .electionId("e1")
                .candidateId("c1")
                .build());

        assertEquals("e1", response.getElectionId());
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void castVote_throwsWhenNotLoggedIn() {
        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(
                Voter.builder().id("v1").email("ada@x.com").isLoggedIn(false).build()));

        assertThrows(InvalidLoginDetailsException.class,
                () -> electionService.castVote("ada@x.com", CastVoteRequest.builder()
                        .electionId("e1")
                        .candidateId("c1")
                        .build()));
    }

    @Test
    void castVote_throwsWhenElectionMissing() {
        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(
                Voter.builder().id("v1").isLoggedIn(true).build()));
        when(electionRepository.findById("e1")).thenReturn(Optional.empty());

        assertThrows(ElectionNotFoundException.class,
                () -> electionService.castVote("ada@x.com", CastVoteRequest.builder()
                        .electionId("e1")
                        .candidateId("c1")
                        .build()));
    }

    @Test
    void castVote_throwsWhenVotingNotStarted() {
        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(
                Voter.builder().id("v1").isLoggedIn(true).build()));
        Election election = Election.builder()
                .id("e1")
                .title("Future")
                .level("STATE")
                .votingOpensAt(Instant.parse("2090-01-01T00:00:00Z"))
                .votingClosesAt(Instant.parse("2091-01-01T00:00:00Z"))
                .build();
        when(electionRepository.findById("e1")).thenReturn(Optional.of(election));

        assertThrows(VotingClosedException.class,
                () -> electionService.castVote("ada@x.com", CastVoteRequest.builder()
                        .electionId("e1")
                        .candidateId("c1")
                        .build()));
    }

    @Test
    void castVote_throwsWhenVotingEnded() {
        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(
                Voter.builder().id("v1").isLoggedIn(true).build()));
        Election election = Election.builder()
                .id("e1")
                .title("Past")
                .level("STATE")
                .votingOpensAt(Instant.parse("2000-01-01T00:00:00Z"))
                .votingClosesAt(Instant.parse("2001-01-01T00:00:00Z"))
                .build();
        when(electionRepository.findById("e1")).thenReturn(Optional.of(election));

        assertThrows(VotingClosedException.class,
                () -> electionService.castVote("ada@x.com", CastVoteRequest.builder()
                        .electionId("e1")
                        .candidateId("c1")
                        .build()));
    }

    @Test
    void castVote_throwsWhenAlreadyVoted() {
        Voter voter = Voter.builder().id("v1").email("ada@x.com").isLoggedIn(true).build();
        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById("e1")).thenReturn(Optional.of(openElection("e1")));
        when(candidateRepository.findById("c1")).thenReturn(Optional.of(
                Candidate.builder().id("c1").electionId("e1").name("A").partyCode("APC").position("Gov").build()));
        when(voteRepository.existsByElectionIdAndVoterId("e1", "v1")).thenReturn(true);

        assertThrows(AlreadyVotedException.class,
                () -> electionService.castVote("ada@x.com", CastVoteRequest.builder()
                        .electionId("e1")
                        .candidateId("c1")
                        .build()));
    }

    @Test
    void castVote_throwsWhenCandidateWrongElection() {
        Voter voter = Voter.builder().id("v1").isLoggedIn(true).build();
        when(voterRepository.findByEmail("ada@x.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById("e1")).thenReturn(Optional.of(openElection("e1")));
        when(candidateRepository.findById("c1")).thenReturn(Optional.of(
                Candidate.builder().id("c1").electionId("other").name("A").partyCode("APC").position("Gov").build()));

        assertThrows(InvalidCandidateException.class,
                () -> electionService.castVote("ada@x.com", CastVoteRequest.builder()
                        .electionId("e1")
                        .candidateId("c1")
                        .build()));
    }

    @Test
    void getResults_talliesVotesPerCandidate() {
        when(electionRepository.findById("e1")).thenReturn(Optional.of(openElection("e1")));
        when(voteRepository.findByElectionId("e1")).thenReturn(List.of(
                Vote.builder().electionId("e1").voterId("v1").candidateId("c1").castAt(Instant.now()).build(),
                Vote.builder().electionId("e1").voterId("v2").candidateId("c1").castAt(Instant.now()).build(),
                Vote.builder().electionId("e1").voterId("v3").candidateId("c2").castAt(Instant.now()).build()));
        when(candidateRepository.findByElectionId("e1")).thenReturn(List.of(
                Candidate.builder().id("c1").electionId("e1").name("A").partyCode("APC").position("Gov").build(),
                Candidate.builder().id("c2").electionId("e1").name("B").partyCode("PDP").position("Gov").build()));

        var results = electionService.getResults("e1");

        assertEquals(3, results.getTotalVotes());
        assertEquals(2, results.getTallies().stream().filter(t -> "c1".equals(t.getCandidateId())).findFirst().orElseThrow().getVoteCount());
        assertEquals(1, results.getTallies().stream().filter(t -> "c2".equals(t.getCandidateId())).findFirst().orElseThrow().getVoteCount());
    }

    private static Election openElection(String id) {
        return Election.builder()
                .id(id)
                .title("Open")
                .level("STATE")
                .votingOpensAt(WINDOW_OPEN_START)
                .votingClosesAt(WINDOW_OPEN_END)
                .resultsPublished(false)
                .build();
    }
}
