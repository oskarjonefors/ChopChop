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

    public void addLength(int length) {
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
        lastSolution = compoundSegments(getIterativeSolution(lengths, cutMeasurements, nbrOfCuts));
        return lastSolution;
    }

    public boolean isReady() {
        return !availableLengths.isEmpty() && !requestedCuts.isEmpty();
    }

    public List<Segment> getLastSolution() {
        return lastSolution;
    }

    public List<Segment> getIterativeSolution(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {

        List<SegmentLink> segLinks = new ArrayList<>();

        for (int length : lengths) {
            int[] maxUse = getMaximumUse(cutMeasurements, nbrOfCuts, length);
            if (getTotalLength(cutMeasurements, maxUse) > 0) {
                final Segment segment = new Segment(length);

                for (int i = 0; i < maxUse.length; i++) {
                    if (maxUse[i] > 0) {
                        segment.addCut(new Cut(cutMeasurements[i], maxUse[i]));
                    }
                }
                int[] remainingCuts = new int[nbrOfCuts.length];
                for (int i = 0; i < nbrOfCuts.length; i++) {
                    remainingCuts[i] = nbrOfCuts[i] - maxUse[i];
                }

                segLinks.add(new SegmentLink(null, segment, remainingCuts));
            }
        }

        int bestWaste = Integer.MAX_VALUE;
        int bestNbrOfLengths = Integer.MAX_VALUE;
        SegmentLink bestRoot = null;

        for (SegmentLink segLink : segLinks) {
            SegmentLink currLink = segLink;

            while (currLink != null && !currLink.searched) {
                if(currLink.spawnedChildren) {
                    boolean allSearched = true;
                    for (SegmentLink next : currLink.children) {
                        if (!next.searched) {
                            currLink = next;
                            allSearched = false;
                            break;
                        }
                    }

                    if (allSearched) {

                        currLink.waste = currLink.segment.getFreeSpace();
                        currLink.nbrOfLengths = 1;
                        if (currLink.children.size() > 0) {
                            SegmentLink topChild = null;
                            int minimumWaste = Integer.MAX_VALUE;
                            int minimumNbrOfLengths = Integer.MAX_VALUE;
                            for (SegmentLink child : currLink.children) {
                                if (child.waste < minimumWaste || (child.waste == minimumWaste && child.nbrOfLengths < minimumNbrOfLengths)) {
                                    minimumWaste = child.waste;
                                    minimumNbrOfLengths = child.nbrOfLengths;
                                    topChild = child;
                                }
                            }

                            final int topChildWaste = topChild == null ? 0 : topChild.waste;
                            final int topChildNbrOfLengths = topChild == null ? 0 : topChild.nbrOfLengths;

                            currLink.waste += topChildWaste;
                            currLink.nbrOfLengths += topChildNbrOfLengths;
                            currLink.next = topChild;
                        }

                        currLink.searched = true;
                        currLink = currLink.parent;
                    }
                } else {
                    currLink.spawnedChildren = true;

                    for (int length : lengths) {
                        int[] maxUse = getMaximumUse(cutMeasurements, currLink.remainingCuts, length);
                        if (getTotalLength(cutMeasurements, maxUse) > 0) {
                            final Segment segment = new Segment(length);

                            for (int i = 0; i < maxUse.length; i++) {
                                if (maxUse[i] > 0) {
                                    segment.addCut(new Cut(cutMeasurements[i], maxUse[i]));
                                }
                            }
                            int[] remainingCuts = new int[nbrOfCuts.length];
                            for (int i = 0; i < nbrOfCuts.length; i++) {
                                remainingCuts[i] = currLink.remainingCuts[i] - maxUse[i];
                            }

                            final SegmentLink newLink = new SegmentLink(currLink, segment, remainingCuts);

                            currLink.children.add(newLink);
                            if (getTotalLength(cutMeasurements, remainingCuts) == 0) {
                                newLink.waste = segment.getFreeSpace();
                                newLink.searched = true;
                                newLink.nbrOfLengths = 1;
                            } else {
                                currLink = newLink;
                            }

                        }
                    }
                }
            }
            if (segLink.waste < bestWaste ||
                    (segLink.waste == bestWaste && segLink.nbrOfLengths < bestNbrOfLengths)) {
                bestRoot = segLink;
                bestWaste = segLink.waste;
                bestNbrOfLengths = segLink.nbrOfLengths;
            }
        }

        List<Segment> rtn = new ArrayList<>();
        while (bestRoot != null) {
            rtn.add(bestRoot.segment);
            bestRoot = bestRoot.next;
        }

        return compoundSegments(rtn);
    }

    private class SegmentLink {

        private final List<SegmentLink> children;
        private final SegmentLink parent;
        private SegmentLink next;
        private final Segment segment;
        private boolean searched;
        private boolean spawnedChildren;
        private final int[] remainingCuts;
        private int waste;
        private int nbrOfLengths;

        private SegmentLink(SegmentLink parent, Segment segment, int[] remainingCuts) {
            this.parent = parent;
            this.segment = segment;
            this.remainingCuts = remainingCuts;
            children = new ArrayList<>();
            searched = false;
            spawnedChildren = false;
            waste = -1;
        }

    }
}
