package se.jonefors.chopchop.model;

import se.jonefors.chopchop.model.representations.Segment;

import java.util.List;

/**
 * @author Oskar Jönefors
 */
public interface SolverListener {

    void notifyProcessStarted();
    void notifyProcessAborted();
    void notifySolution(List<Segment> solution, String label);

}
