package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.model.CutPlanner;
import se.jonefors.chopchop.model.ListenableSolver;
import se.jonefors.chopchop.model.SolverListener;
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

    @Override
    public void addListener(SolverListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SolverListener listener) {
        listeners.remove(listener);
    }

}
