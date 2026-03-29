package moniepoint.electionApp.exceptions;

import moniepoint.electionApp.dtos.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ElectionAppException.class)
    public ResponseEntity<ApiResponse> handleElectionAppException(ElectionAppException ex) {
        return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
    }
}
