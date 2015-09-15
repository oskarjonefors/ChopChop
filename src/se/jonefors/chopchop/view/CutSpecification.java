package se.jonefors.chopchop.view;

/**
 * A class representing a Cut in the CutTable.
 * The reason that the Cut class from the model package is not used for this purpose is that
 * the length and quantity in the table may be 0 to represent a blank cell, whereas this is not
 * permitted in the model.
 *
 * @author Oskar Jönefors
 */

public class CutSpecification {

    private int length;
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
