package dk.aau.ds304e18.models;

/**
 * The states that a given project can have.
 */
public enum ProjectState {

    /**
     * The State that describes that the project is still being worked on.
     */
    ONGOING(0),

    /**
     * The State that describes that the project is done (completed).
     */
    COMPLETED(1),

    /**
     * The State that describes that the project has been archived.
     */
    ARCHIVED(2);

    /**
     * The attribute Value, which is used to determine which state the project is in.
     */
    private final int value;

    /**The constructor of the enum
     * @param value - The enum value of the specific state that is chosen.
     */
    ProjectState(int value) {
        this.value = value;
    }

    /**
     * The getter for the value.
     * @return value - The value of the current state.
     */
    public int getValue() {
        return value;
    }
}
