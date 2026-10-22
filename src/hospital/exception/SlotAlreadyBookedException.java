package hospital.exception;

/**
 * Thrown when attempting to book a slot that has already been taken.
 */
public class SlotAlreadyBookedException extends Exception {
    public SlotAlreadyBookedException(String message) {
        super(message);
    }
}
