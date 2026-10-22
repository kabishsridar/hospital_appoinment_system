package hospital.exception;

/**
 * Thrown when a doctor, patient, slot, or appointment is not found.
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
