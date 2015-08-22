package se.jonefors.chopchop.view;

import se.jonefors.chopchop.model.CutPlanner;
import se.jonefors.chopchop.model.ListenableSolver;
import se.jonefors.chopchop.model.SolverListener;
import se.jonefors.chopchop.model.representations.Segment;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Oskar Jönefors
 */

class SolverWorker extends SwingWorker<List<Segment>, Double> implements ListenableSolver {

    private final List<CutSpecification> cuts;
    private final List<LengthSpecification> lengths;
    private final CutPlanner planner;
    private final List<SolverListener> listeners;
    private final String label;

    SolverWorker(List<CutSpecification> cuts, List<LengthSpecification> lengths, String label) {
        this.cuts = cuts;
        this.lengths = lengths;
        this.planner = CutPlanner.getSharedInstance();
        listeners = new ArrayList<>();
        this.label = label;
    }

    @Override
    protected List<Segment> doInBackground() throws Exception {
        planner.clear();
        prepareData();
        for (SolverListener listener : listeners) {
            listener.notifyProcessStarted();
        }
        if (planner.isReady()) {
            return planner.getOptimalSolution();
        }
        return new ArrayList<>();
    }

    @Override
    protected void done() {
        try {
            for (SolverListener listener : listeners) {
                listener.notifySolution(get(), label);
            }
        } catch (InterruptedException | CancellationException e) {
            CutPlanner.getSharedInstance().cancel();
            listeners.clear();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void prepareData() {
        List<Integer> submittedLengths = new ArrayList<>();

        int topLength = 0;

        for (LengthSpecification len : lengths) {
            if (len.active && len.length > 0 && !submittedLengths.contains(len.length)) {
                submittedLengths.add(len.length);
                planner.addLength(len.length);

                topLength = Math.max(topLength, len.length);
            }
        }

        Map<Integer, Integer> summarizedCuts = new HashMap<>();

        for (CutSpecification cut : cuts) {
            if (summarizedCuts.containsKey(cut.length)) {

                final int prevQty = summarizedCuts.get(cut.length);
                summarizedCuts.remove(cut.length);
                summarizedCuts.put(cut.length, prevQty + cut.quantity);

            } else if (cut.length > 0 && cut.quantity > 0) {
                summarizedCuts.put(cut.length, cut.quantity);
            }
        }

        for (Integer length : summarizedCuts.keySet()) {
            planner.addRequestedCut(length, summarizedCuts.get(length));
        }
    }

    @Override
    public void addListener(SolverListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SolverListener listener) {
        listeners.remove(listener);
    }

}
