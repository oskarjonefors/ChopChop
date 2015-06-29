package se.jonefors.chopchop.model;

import se.jonefors.chopchop.model.representations.Cut;
import se.jonefors.chopchop.model.representations.Segment;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Oskar Jönefors
 */

public class CutPlanner {

    private static final Logger log = Logger.getLogger(CutPlanner.class.getName());

    private final List<Integer> availableLengths;
    private final List<Cut> requestedCuts;
    private List<Segment> lastSolution;

    public CutPlanner() {
        availableLengths = new ArrayList<>();
        requestedCuts = new ArrayList<>();
        log.log(Level.FINE, "Initialized CutPlanner");
    }

    public void addLength(int length) {
        log.log(Level.FINE, "Added base segment of length " + length);
        if (length <= 0) {
            throw new IllegalArgumentException("addLength: length was " + length +
                    ". May not be 0 or negative!");
        }
        availableLengths.add(length);
    }

    public void clear() {
        availableLengths.clear();
        requestedCuts.clear();
        log.log(Level.FINE, "Cleared CutPlanner");
    }

    public void addRequestedCut(int length, int quantity) {
        log.log(Level.FINE, "Adding requested cut of length " + length +
                " and quantity " + quantity);
        if (quantity <= 0) {
            throw new IllegalArgumentException("addRequestedCut: quantity was " + quantity +
                    ", cannot be 0 or negative");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("addRequestedCut: length was " + length +
                    ", cannot be 0 or negative");
        }
        requestedCuts.add(new Cut(length, quantity));
    }

    /**
     * Get a list of segments with cuts distributed in such a fashion that the free space on all
     * segments in the solution is minimized.
     * @return A list of Segments.
     */
    public List<Segment> getOptimalSolution() {
        if (availableLengths.isEmpty()) {
            throw new IllegalArgumentException("getOptimalSolution: No optimal solution can be " +
                    "found since no base lengths have been added!");
        }
        if (requestedCuts.isEmpty()) {
            throw new IllegalArgumentException("getOptimalSolution: No optimal solution can be " +
                    "found since no requested cuts have been added!");
        }
        int maxLength = 0;
        for (Integer len : availableLengths) {
            maxLength = len > maxLength ? len : maxLength;
        }
        for (Cut c : requestedCuts) {
            if (c.getLength() > maxLength) {
                throw new IllegalArgumentException("getOptimalSolution: Requested cut of length " +
                        c.getLength() + " but the maximum available length is " + maxLength);
            }
        }

        Collections.sort(availableLengths);
        Collections.reverse(availableLengths);

        int[] lengths = new int[availableLengths.size()];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = availableLengths.get(i);
        }

        int[] cutMeasurements = new int[requestedCuts.size()];
        int[] nbrOfCuts = new int [requestedCuts.size()];

        for (int c = 0; c < cutMeasurements.length; c++) {
            cutMeasurements[c] = requestedCuts.get(c).getLength();
            nbrOfCuts[c] = requestedCuts.get(c).getQuantity();
        }
        lastSolution = Solver.getOptimalSolution(lengths, cutMeasurements, nbrOfCuts);
        return lastSolution;
    }

    /**
     * Check if the CutPlanner has received cut measurements and available lengths.
     *
     * @return True if both cuts and lengths have been added,false otherwise.
     */
    public boolean isReady() {
        return !availableLengths.isEmpty() && !requestedCuts.isEmpty();
    }

    /**
     * Get the last solution calculated, or null if no solution has been calculated yet.
     * @return A list of Segments, or null.
     */
    public List<Segment> getLastSolution() {
        return lastSolution;
    }

}
