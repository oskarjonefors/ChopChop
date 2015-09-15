package se.jonefors.chopchop.model;

import java.util.Comparator;

/**
 * Comparator class that sorts Segments by order of their length in descending order.
 *
 * @author Oskar Jönefors
 */

public class SegmentComparator implements Comparator<Segment> {
    @Override
    public int compare(Segment segment, Segment t1) {
                /* Longest first */
        return Integer.compare(t1.getLength(), segment.getLength());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
