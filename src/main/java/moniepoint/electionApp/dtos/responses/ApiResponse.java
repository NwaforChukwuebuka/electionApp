package moniepoint.electionApp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Uniform API envelope: success flag plus payload or error message in {@code data}.
 */
@Data
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private Object data;
}
