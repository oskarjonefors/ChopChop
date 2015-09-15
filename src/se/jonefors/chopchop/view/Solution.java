package se.jonefors.chopchop.view;

import se.jonefors.chopchop.model.Segment;

import java.util.List;

/**
 * A class representing a solution: A list of segments and a label for the problem.
 *
 * @author Oskar Jönefors
 */

public class Solution {

    private final List<Segment> segments;
    private final String label;

    public Solution(List<Segment> segments, String label) {
        this.segments = segments;
        this.label = label;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public String getLabel() {
        return label;
    }

}
