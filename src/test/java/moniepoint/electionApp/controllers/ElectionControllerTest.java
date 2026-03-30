package moniepoint.electionApp.controllers;

import moniepoint.electionApp.dtos.requests.CastVoteRequest;
import moniepoint.electionApp.dtos.responses.CandidateResponse;
import moniepoint.electionApp.dtos.responses.CastVoteResponse;
import moniepoint.electionApp.dtos.responses.ElectionResultsResponse;
import moniepoint.electionApp.dtos.responses.ElectionSummaryResponse;
import moniepoint.electionApp.dtos.responses.ResultTallyResponse;
import moniepoint.electionApp.exceptions.ElectionNotFoundException;
import moniepoint.electionApp.exceptions.GlobalExceptionHandler;
import moniepoint.electionApp.exceptions.VotingClosedException;
import moniepoint.electionApp.services.ElectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ElectionController.class)
@Import(GlobalExceptionHandler.class)
class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ElectionService electionService;

    @Test
    void listElections_returnsOkWithWrappedList() throws Exception {
        when(electionService.listElections()).thenReturn(List.of(
                ElectionSummaryResponse.builder()
                        .id("e1")
                        .title("Gov 2026")
                        .level("STATE")
                        .votingOpensAt(Instant.parse("2020-01-01T00:00:00Z"))
                        .votingClosesAt(Instant.parse("2030-01-01T00:00:00Z"))
                        .resultsPublished(false)
                        .build()));

        mockMvc.perform(get("/elections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("e1"))
                .andExpect(jsonPath("$.data[0].title").value("Gov 2026"));
    }

    @Test
    void getCandidates_returnsOk() throws Exception {
        when(electionService.getCandidates("e1")).thenReturn(List.of(
                CandidateResponse.builder()
                        .id("c1")
                        .name("Ada")
                        .partyCode("APC")
                        .position("Governor")
                        .build()));

        mockMvc.perform(get("/elections/e1/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].partyCode").value("APC"));
    }

    @Test
    void getCandidates_returnsBadRequestWhenElectionMissing() throws Exception {
        when(electionService.getCandidates("missing"))
                .thenThrow(new ElectionNotFoundException("Election not found"));

        mockMvc.perform(get("/elections/missing/candidates"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void castVote_returnsCreated() throws Exception {
        CastVoteRequest body = CastVoteRequest.builder()
                .electionId("e1")
                .candidateId("c1")
                .build();

        when(electionService.castVote(eq("voter@example.com"), any(CastVoteRequest.class)))
                .thenReturn(CastVoteResponse.builder()
                        .message("Vote recorded")
                        .electionId("e1")
                        .build());

        mockMvc.perform(patch("/elections/vote/voter@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.electionId").value("e1"));
    }

    @Test
    void castVote_returnsBadRequestWhenVotingClosed() throws Exception {
        CastVoteRequest body = CastVoteRequest.builder()
                .electionId("e1")
                .candidateId("c1")
                .build();

        when(electionService.castVote(eq("voter@example.com"), any(CastVoteRequest.class)))
                .thenThrow(new VotingClosedException("Voting is not open for this election"));

        mockMvc.perform(patch("/elections/vote/voter@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getResults_returnsOk() throws Exception {
        when(electionService.getResults("e1")).thenReturn(ElectionResultsResponse.builder()
                .electionId("e1")
                .totalVotes(2)
                .tallies(List.of(
                        ResultTallyResponse.builder()
                                .candidateId("c1")
                                .candidateName("A")
                                .partyCode("APC")
                                .voteCount(2)
                                .build()))
                .build());

        mockMvc.perform(get("/elections/e1/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalVotes").value(2))
                .andExpect(jsonPath("$.data.tallies[0].voteCount").value(2));
    }

    @Test
    void getResults_returnsBadRequestWhenElectionMissing() throws Exception {
        when(electionService.getResults("missing"))
                .thenThrow(new ElectionNotFoundException("Election not found"));

        mockMvc.perform(get("/elections/missing/results"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
