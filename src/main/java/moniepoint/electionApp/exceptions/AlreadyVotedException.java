package moniepoint.electionApp.exceptions;

public class AlreadyVotedException extends ElectionAppException {
    public AlreadyVotedException(String message) {
        super(message);
    }
}
