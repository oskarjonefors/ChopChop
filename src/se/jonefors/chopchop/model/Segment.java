package se.jonefors.chopchop.model;

import java.util.*;

/**
 * Class that represents a number of full length segments and any cuts assigned to them.
 *
 * @author Oskar Jönefors
 */
public class Segment {

    private final int length;
    private final Map<Integer,Cut> cuts;
    private int quantity;

    /**
     * Create a new segment of the given length.
     * @param length    An int > 0
     */
    public Segment(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Segment: length must be > 0, was " + length);
        }
        this.length = length;
        this.cuts = new HashMap<>();
        this.quantity = 1;
    }

    public int getLength() {
        return length;
    }

    /**
     * Adds the given cut to the segment.
     *
     * @param cut  The cut length times its quantity must be less than or equal to the amount of
     *             free space the Segment has.
     */
    public void addCut(Cut cut) {

        int usedLength = 0;

        for (Cut c : cuts.values()) {
            usedLength += c.getLength() * c.getQuantity();
        }

        int addedLength = cut.getLength() * cut.getQuantity();

        if (usedLength + addedLength > length) {
            throw new IllegalArgumentException("addCut: Tried to add " + cut.getQuantity() + "cuts of length " +
                    cut.getLength() + " to segment of length " + length + " where " + usedLength + " is already used.");
        }

        if (cuts.containsKey(cut.getLength())) {
            final Cut oldCut = cuts.get(cut.getLength());
            oldCut.setQuantity(oldCut.getQuantity() + cut.getQuantity());
        } else {
            cuts.put(cut.getLength(), cut);
        }
    }

    public List<Cut> getCuts() {
        final List<Cut> cutsCpy = new ArrayList<>();
        for (Cut c : cuts.values()) {
            cutsCpy.add(c);
        }
        Collections.sort(cutsCpy, new CutComparator());
        return cutsCpy;
    }

    public int getFreeSpace() {
        int usedSpace = 0;
        for (Cut s : cuts.values()) {
            usedSpace += s.getLength() * s.getQuantity();
        }
        return length - usedSpace;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity  An int > 0
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("setQuantity: quantity was " + quantity +
                    ", may not be 0 or negative!");
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(quantity + " segment(s) of length " + length + " with free space " +
                getFreeSpace() + " and the following cuts:");
        for (Cut c : cuts.values()) {
            sb.append("\n");
            sb.append(c.toString());
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;

        if (length != segment.length) return false;

        List<Cut> segmentCuts = segment.getCuts();
        if (segmentCuts.size() != cuts.size()) return false;

        boolean equals = true;
        for (Cut c : segmentCuts) {
            final int cLen = c.getLength();
            final int cQty = c.getQuantity();

            if (!cuts.containsKey(cLen)) {
                equals = false;
            } else if (cuts.get(cLen).getQuantity() != cQty) {
                equals = false;
            }
        }

        return equals;
    }

    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + cuts.hashCode();
        return result;
    }
}
