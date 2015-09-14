package se.jonefors.chopchop.model;

import se.jonefors.chopchop.model.representations.Segment;

import java.util.List;

/**
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
