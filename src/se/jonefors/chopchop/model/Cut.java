package se.jonefors.chopchop.model;

/**
 * @author Oskar Jönefors
 */

public class Cut {
    private final int length;
    private int quantity;

    /**
     * @param length    An int > 0.
     * @param quantity       An int > 0.
     */
    public Cut(int length, int quantity) {
        if (length <= 0) {
            throw new IllegalArgumentException("Cut: length must be > 0, was " + length);
        } else if (quantity <= 0) {
            throw new IllegalArgumentException("Cut: quantity must be > 0, was " + quantity);
        }
        this.length = length;
        this.quantity = quantity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("setLength: length must be > 0, was " + length);
        }
        this.length = length;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("setQuantity: quantity must be > 0, was " + quantity);
        }

        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Cut of length " + length + ", repeated " + quantity + " times.";
    }
}
