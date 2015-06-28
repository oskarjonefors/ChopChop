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
    private List<Segment> lastSolution;

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
/*            log.log(Level.FINER, "Can fit length of " + measurement + " a maximum of " + maxqty +
                    " times in the base length of " + baseLength);*/
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

    private List<Segment> compoundSegments(List<Segment> segList) {
        System.out.println("Received seglist of size " + segList.size());
        final List<Segment> compoundedSegList = new ArrayList<>();

        for (Segment seg : segList) {
            boolean existingSegment = false;
            for (Segment com : compoundedSegList) {
                if (seg.equals(com)) {
                    com.setQuantity(com.getQuantity() + 1);
                    existingSegment = true;
                }
            }
            if (!existingSegment) {
                compoundedSegList.add(seg);
            }
        }

        return compoundedSegList;
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

        SegmentLink solution = new SegmentLink(lengths, cutMeasurements, nbrOfCuts);
        lastSolution = compoundSegments(solution.getSegments());
        return lastSolution;
    }

    public boolean isReady() {
        return !availableLengths.isEmpty() && !requestedCuts.isEmpty();
    }

    public List<Segment> getLastSolution() {
        return lastSolution;
    }

    class SegmentLink {

        private SegmentLink next;
        private Segment segment;

        SegmentLink(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {
            this(0, lengths, cutMeasurements, nbrOfCuts);
        }

        private SegmentLink(int length, int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {
            int[] remainingCuts;

            if (length > 0) {
                segment = new Segment(length);
                int[] maxUse = getMaximumUse(cutMeasurements, nbrOfCuts, length);

                for (int i = 0; i < maxUse.length; i++) {
                    if (maxUse[i] > 0) {
                        segment.addCut(new Cut(cutMeasurements[i], maxUse[i]));
                    }
                }
                remainingCuts = new int[nbrOfCuts.length];
                for (int i = 0; i < nbrOfCuts.length; i++) {
                    remainingCuts[i] = nbrOfCuts[i] - maxUse[i];
                }
            } else {
                remainingCuts = nbrOfCuts;
            }

            if (getTotalLength(cutMeasurements, remainingCuts) > 0) {
                int minimumWaste = Integer.MAX_VALUE;
                int minimumNbrOfLengths = minimumWaste;

                SegmentLink bestNext = null;
                for (int len : lengths) {
                    SegmentLink newSegLink = new SegmentLink(len, lengths, cutMeasurements, remainingCuts);
                    final int newWaste = newSegLink.getWaste();
                    int newNbrOfLengths = Integer.MAX_VALUE;
                    if (newWaste < minimumWaste ||
                            (newWaste == minimumWaste && (newNbrOfLengths = newSegLink.getNbrOfLengths()) < minimumNbrOfLengths)) {
                        minimumWaste = newWaste;
                        minimumNbrOfLengths = newNbrOfLengths;
                        bestNext = newSegLink;
                    }
                }
                next = bestNext;
            }
        }

        public int getWaste() {
            int freeSpace = segment == null ? 0 : segment.getFreeSpace();

            if (next == null) {
                return freeSpace;
            } else {
                return freeSpace + next.getWaste();
            }
        }

        public int getNbrOfLengths() {
            int thisSeg = segment == null ? 0 : 1;

            if (next == null) {
                return thisSeg;
            } else {
                return thisSeg + next.getNbrOfLengths();
            }
        }

        public List<Segment> getSegments() {

            if (segment == null) {
                return next.getSegments();
            }

            List<Segment> rtn;

            if (next == null) {
                rtn = new ArrayList<>();
                rtn.add(segment);
                return rtn;
            } else {
                rtn = next.getSegments();
                rtn.add(segment);
                return rtn;
            }
        }

    }
}
