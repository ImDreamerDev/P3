package dk.aau.ds304e18.models;

/**
 * The states that a given project can have.
 */
public enum ProjectState {

    /**
     *
     */
    ONGOING(0),
    /**
     *
     */
    COMPLETED(1),
    /**
     *
     */
    ARCHIVED(2);

    /**
     *
     */
    private final int value;

    /**
     * @param value
     */
    ProjectState(int value) {
        this.value = value;
    }

    /**
     * 
     * @return
     */
    public int getValue() {
        return value;
    }
}
