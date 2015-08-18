package se.jonefors.chopchop.model;

/**
 * @author Oskar Jönefors
 */
public interface ListenableSolver {

    void addListener(SolverListener listener);

    void removeListener(SolverListener listener);

}
