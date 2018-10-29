package dk.aau.ds304e18.models;

public enum ProjectState {

    /**
     * The states that a given project can have.
     */
    ONGOING(0), COMPLETED(1), ARCHIVED(2);
    private final int value;

    ProjectState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
