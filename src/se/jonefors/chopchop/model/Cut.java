package se.jonefors.chopchop.model;

/**
 * @author Oskar Jönefors
 */

public class Cut {
    final int length;
    int qty;

    Cut(int length, int qty) {
        this.length = length;
        this.qty = qty;
    }

    public int getLength() {
        return length;
    }

    public int getQuantity() {
        return qty;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("setQuantity: quantity was " + quantity +
                    ", may not be 0 or negative!");
        }

        qty = quantity;
    }

    @Override
    public String toString() {
        return "Cut of length " + length + ", repeated " + qty + " times.";
    }
}
