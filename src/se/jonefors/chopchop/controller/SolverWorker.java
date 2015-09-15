/*
 * ChopChop - A very simple 1D cut optimizer with printing capability.
 * Copyright (C) 2015  Oskar Jönefors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.model.CutPlanner;
import se.jonefors.chopchop.model.Segment;
import se.jonefors.chopchop.model.SegmentComparator;
import se.jonefors.chopchop.view.Solution;
import se.jonefors.chopchop.view.CutSpecification;
import se.jonefors.chopchop.view.LengthSpecification;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles the solving process in a separate thread.
 *
 * @author Oskar Jönefors
 */

class SolverWorker extends SwingWorker<Solution, Double> {

    private final List<CutSpecification> cuts;
    private final List<LengthSpecification> lengths;
    private final CutPlanner planner;
    private final String label;

    /**
     * @param cuts      May not be null.
     * @param lengths   May not be null.
     * @param label     May be null.
     */
    public SolverWorker(List<CutSpecification> cuts, List<LengthSpecification> lengths, String label) {
        if (cuts == null) {
            throw new NullPointerException("SolverWorker: cuts was null!");
        } else if (lengths == null) {
            throw new NullPointerException("SolverWorker: lengths was null!");
        }

        this.cuts = cuts;
        this.lengths = lengths;
        this.planner = CutPlanner.getSharedInstance();
        this.label = label;
    }

    @Override
    protected Solution doInBackground() throws Exception {
        planner.clear();
        prepareData();
        firePropertyChange("SOLVER_STARTED", null, null);
        if (planner.isReady()) {
            List<Segment> solutionSegments = planner.getOptimalSolution();
            Collections.sort(solutionSegments, new SegmentComparator());
            return new Solution(solutionSegments, label);
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
