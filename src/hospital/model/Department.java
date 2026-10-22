package hospital.model;

/**
 * Enumeration representing medical departments in the hospital.
 */
public enum Department {
    CARDIOLOGY("Cardiology", "Heart & Cardiovascular Care"),
    ORTHOPEDICS("Orthopedics", "Bones, Joints & Musculoskeletal"),
    PEDIATRICS("Pediatrics", "Infant, Child & Adolescent Healthcare"),
    GENERAL_MEDICINE("General Medicine", "Primary & Preventive Healthcare"),
    NEUROLOGY("Neurology", "Brain, Spinal & Nerve Disorders");

    private final String displayName;
    private final String description;

    Department(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
