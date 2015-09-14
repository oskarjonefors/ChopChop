package se.jonefors.chopchop.util;

/**
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
