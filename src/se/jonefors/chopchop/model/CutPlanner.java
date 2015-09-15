package se.jonefors.chopchop.model;

import java.util.*;

/**
 * Class that saves the problem parameters (cuts and lengths) and verifies them before passing
 * them on to the Solver.
 *
 * @author Oskar Jönefors
 */

public class CutPlanner {

    private final List<Integer> availableLengths;
    private final List<Cut> requestedCuts;
    private static CutPlanner instance;

    private CutPlanner() {
        availableLengths = new ArrayList<>();
        requestedCuts = new ArrayList<>();
    }

    public static CutPlanner getSharedInstance() {
        if (instance == null) {
            instance = new CutPlanner();
        }
        return instance;
    }

    /**
     * Add a full length to the CutPlanner, to be used as a base onto which the solver can distribute
     * the requested cuts as efficiently as possible.
     *
     * @param length  A positive non-zero number.
     */
    public void addLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("addLength: length was " + length +
                    ". May not be 0 or negative!");
        }
        availableLengths.add(length);
    }

    /**
     * Clear all added lengths and requested cuts from the planner.
     */
    public void clear() {
        availableLengths.clear();
        requestedCuts.clear();
    }

    /**
     * Add requested cuts of the given length and quantity. When getOptimalSolution is called,
     * the solver will distribute the given cuts onto the given length as efficiently as possible
     * (minimizing the total free space in the solution).
     *
     * @param length  A positive non-zero number.
     * @param quantity  A positive non-zero number.
     */
    public void addRequestedCut(int length, int quantity) {
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
     * Get a list of segments with cuts distributed so that the total free space of all
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

        return Solver.getOptimalSolution(availableLengths, requestedCuts);
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
     * Cancel the solving process.
     */
    public void cancel() {
        Solver.cancel();
    }

}
