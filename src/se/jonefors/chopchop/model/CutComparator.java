package se.jonefors.chopchop.model;

import java.util.Comparator;

/**
 * Comparator that sorts Cuts by their lengths in descending order.
 *
 * @author Oskar Jönefors
 */

class CutComparator implements Comparator<Cut> {
    @Override
    public int compare(Cut cut, Cut t1) {
                /* Longest first */
        return Integer.compare(t1.getLength(), cut.getLength());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
