package se.jonefors.chopchop;

import java.util.Comparator;

/**
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
