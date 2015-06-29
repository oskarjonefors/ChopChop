package se.jonefors.chopchop.model.representations;

import java.util.Comparator;

/**
 * @author Oskar Jönefors
 */

public class CutComparator implements Comparator<Cut> {
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
