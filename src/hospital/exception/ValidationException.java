package hospital.exception;

/**
 * Thrown when patient or booking input validation fails.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
