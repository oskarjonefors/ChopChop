package se.jonefors.chopchop.model;

import se.jonefors.chopchop.model.representations.Segment;
import se.jonefors.chopchop.util.CutSpecification;
import se.jonefors.chopchop.util.LengthSpecification;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Oskar Jönefors
 */

public class SolverWorker extends SwingWorker<Solution, Double> {

    private final List<CutSpecification> cuts;
    private final List<LengthSpecification> lengths;
    private final CutPlanner planner;
    private final String label;

    public SolverWorker(List<CutSpecification> cuts, List<LengthSpecification> lengths, String label) {
        this.cuts = cuts;
        this.lengths = lengths;
        this.planner = CutPlanner.getSharedInstance();
        this.label = label;
    }

    @Override
    protected Solution doInBackground() throws Exception {
        planner.clear();
        prepareData();
        firePropertyChange("SOLVING_STARTED", null, null);
        if (planner.isReady()) {
            return new Solution(planner.getOptimalSolution(), label);
        }
        return new Solution(null, null);
    }

    @Override
    protected void done() {
        try {
            firePropertyChange("SOLVER_FINISHED", null, get());
        } catch (InterruptedException | CancellationException e) {
            CutPlanner.getSharedInstance().cancel();
            firePropertyChange("SOLVER_CANCELLED", null, null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void prepareData() {
        List<Integer> submittedLengths = new ArrayList<>();

        int topLength = 0;

        for (LengthSpecification len : lengths) {
            if (len.isActive() && len.getLength() > 0 && !submittedLengths.contains(len.getLength())) {
                submittedLengths.add(len.getLength());
                planner.addLength(len.getLength());

                topLength = Math.max(topLength, len.getLength());
            }
        }

        Map<Integer, Integer> summarizedCuts = new HashMap<>();

        for (CutSpecification cut : cuts) {
            if (summarizedCuts.containsKey(cut.getLength())) {

                final int prevQty = summarizedCuts.get(cut.getLength());
                summarizedCuts.remove(cut.getLength());
                summarizedCuts.put(cut.getLength(), prevQty + cut.getQuantity());

            } else if (cut.getLength() > 0 && cut.getQuantity() > 0) {
                summarizedCuts.put(cut.getLength(), cut.getQuantity());
            }
        }

        for (Integer length : summarizedCuts.keySet()) {
            planner.addRequestedCut(length, summarizedCuts.get(length));
        }
    }
}
