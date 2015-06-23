package se.jonefors.chopchop.view;

/**
 * @author Oskar Jönefors
 */

public class LengthSpecification {
    boolean active;
    int length;

    public LengthSpecification(int length) {
        active = true;
        this.length = length;
    }
}
