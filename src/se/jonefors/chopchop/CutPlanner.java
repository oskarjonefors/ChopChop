package se.jonefors.chopchop;

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

    public CutPlanner() {
        availableLengths = new ArrayList<>();
        requestedCuts = new ArrayList<>();
        log.log(Level.FINE, "Initialized CutPlanner");
    }

    private int[] getMaximumUse(int[] cuts, int[] nbrOfCuts, int baseLength) {

        int[] maxNbrOfCuts = new int[nbrOfCuts.length];

        for (int cut = 0; cut < nbrOfCuts.length; cut++) {
            final int measurement = cuts[cut];
            final int maxqty = baseLength / measurement;
            maxNbrOfCuts[cut] = maxqty < nbrOfCuts[cut] ? maxqty : nbrOfCuts[cut];
            log.log(Level.FINER, "Can fit length of " + measurement + " a maximum of " + maxqty +
                    " times in the base length of " + baseLength);
        }

        int[] optimalSolution = new int[maxNbrOfCuts.length];
        int[] attempt = new int[maxNbrOfCuts.length];
        int currentIndex = 0;
        int minimumWaste = baseLength;
        boolean triedAllSolutions = false;

        while (!triedAllSolutions) {

            if (currentIndex < attempt.length && attempt[currentIndex] < maxNbrOfCuts[currentIndex]) {
                    /* Add one more of the current cut if possible */
                attempt[currentIndex]++;
                currentIndex = 0;
            } else if (currentIndex < attempt.length) {
                int initialIndex = currentIndex;
                boolean foundNextIndex = false;
                while (currentIndex < attempt.length && !foundNextIndex) {
                    if (attempt[currentIndex] == maxNbrOfCuts[currentIndex]) {
                        currentIndex++;
                    } else {
                        foundNextIndex = true;
                    }
                }
                if (foundNextIndex) {
                    attempt[initialIndex] = 0;
                    attempt[currentIndex]++;
                    currentIndex = 0;
                }
            } else {
                triedAllSolutions = true;
            }

            int currentWaste = baseLength -
                    getTotalLength(cuts, attempt);

            if (currentWaste >= 0 && currentWaste < minimumWaste) {
                minimumWaste = currentWaste;
                optimalSolution = Arrays.copyOf(attempt, attempt.length);
                log.log(Level.FINER, "Found new solution with a waste of " + minimumWaste);
            }

        }

        return optimalSolution;
    }

    private int getTotalLength(int[] measurements, int[] nbrOfCuts) {
        int length = 0;
        for (int cut = 0; cut < nbrOfCuts.length; cut++) {
            length += measurements[cut] * nbrOfCuts[cut];
        }
        return length;
    }

    public boolean addLength(int length) {
        log.log(Level.FINE, "Added base segment of length " + length);
        if (length <= 0) {
            throw new IllegalArgumentException("addLength: length was " + length +
                    ". May not be 0 or negative!");
        }
        return availableLengths.add(length);
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

    public List<Segment> getOptimalSolution() {
        return getOptimalSolution(false);
    }

    public List<Segment> getOptimalSolution(boolean prioritizeLongerSegments) {
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

        boolean allCutsAllocated = false;

        int[] cuts = new int[requestedCuts.size()];
        int[] nbrOfCuts = new int[requestedCuts.size()];

        for (int i = 0; i < requestedCuts.size(); i++) {
            cuts[i] = requestedCuts.get(i).getLength();
            nbrOfCuts[i] = requestedCuts.get(i).getQuantity();
        }

        List<Segment> segs = new ArrayList<>();

        while (!allCutsAllocated) {
            int minimumWaste = -1;
            int optimalLength = -1;
            int[] maxUse = new int[cuts.length];
            for (Integer length : availableLengths) {
                int[] currUse = getMaximumUse(cuts, nbrOfCuts, length);
                int waste = length - getTotalLength(cuts, currUse);


                boolean priority;
                if (prioritizeLongerSegments) {
                    priority = waste == minimumWaste && length > optimalLength;
                } else {
                    priority = waste == minimumWaste && length < optimalLength;
                }

                if (waste < minimumWaste || priority || minimumWaste < 0) {
                    minimumWaste = waste;
                    optimalLength = length;
                    maxUse = currUse;
                }
            }

            Segment s = new Segment(optimalLength);

            for (int i = 0; i < maxUse.length; i++) {
                if (maxUse[i] > 0) {
                    s.addCut(new Cut(cuts[i], maxUse[i]));
                }
            }

            boolean existingSegment = false;

            for (Segment seg : segs) {
                if (s.equals(seg)) {
                    seg.setQuantity(seg.getQuantity() + 1);
                    existingSegment = true;
                }
            }

            if (!existingSegment) {
                segs.add(s);
            }

            boolean cutsLeft = false;
            for (int i = 0; i < nbrOfCuts.length; i++) {
                nbrOfCuts[i] -= maxUse[i];
                if (nbrOfCuts[i] > 0) {
                    cutsLeft = true;
                }
            }

            if (!cutsLeft) {
                allCutsAllocated = true;
            }
        }

        segs.sort(new Comparator<Segment>() {
            @Override
            public int compare(Segment segment, Segment t1) {
                /* Longest first */
                return Integer.compare(t1.getLength(), segment.getLength());
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }
        });

        return segs;
    }

    public boolean isReady() {
        return !availableLengths.isEmpty() && !requestedCuts.isEmpty();
    }

}
