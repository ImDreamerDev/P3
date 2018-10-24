package dk.aau.ds304e18.models;

public enum ProjectState {

    /**
     * The states that a given project can have.
     */
    ONGOING(1), COMPLETED(2), ARCHIVED(3);
    private final int value;

    ProjectState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
