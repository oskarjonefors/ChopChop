package se.jonefors.chopchop.view;

/**
 * A class representing an available length to be used as a basis for the solving algorithm,
 * as well as it's status. If inactive, it will not be passed along to the solving algorithm
 * as a possible candidate in the solution.
 *
 * @author Oskar Jönefors
 */

public class LengthSpecification {
    private boolean active;
    private int length;

    public LengthSpecification(int length) {
        active = true;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public boolean isActive() {
        return active;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setStatus(boolean active) {
        this.active = active;
    }
}
