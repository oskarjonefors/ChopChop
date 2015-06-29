package se.jonefors.chopchop.model;

import se.jonefors.chopchop.model.representations.Cut;
import se.jonefors.chopchop.model.representations.Segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Oskar Jönefors
 */

class Solver {
    private static final Logger log = Logger.getLogger(Solver.class.getName());

    static int[] getMaximumUse(int[] cuts, int[] nbrOfCuts, int baseLength) {

        int[] maxNbrOfCuts = new int[nbrOfCuts.length];

        for (int cut = 0; cut < nbrOfCuts.length; cut++) {

            if (nbrOfCuts[cut] < 0) {
                throw new IllegalArgumentException("getMaximumUse: cut quantity at index " + cut +
                        " was negative!");
            }

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

    static int getTotalLength(int[] measurements, int[] nbrOfCuts) {
        int length = 0;
        for (int cut = 0; cut < nbrOfCuts.length; cut++) {
            length += measurements[cut] * nbrOfCuts[cut];
        }
        return length;
    }

    static List<Segment> compoundSegments(List<SegDef> segDefList, int[] cutMeasurements) {
        final List<Segment> segList = new ArrayList<>();

        for (SegDef def : segDefList) {
            final Segment segment = new Segment(def.length);
            for (int i = 0; i < def.usage.length; i++) {
                if (def.usage[i] > 0) {
                    segment.addCut(new Cut(cutMeasurements[i], def.usage[i]));
                }
            }
            segList.add(segment);
        }

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

    static List<SegDef> getSuitableLengths(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {
        List<SegDef> rtn = new ArrayList<>();

        int minimumWaste = Integer.MAX_VALUE;
        SegDef minimumWasteDef = new SegDef();
        minimumWasteDef.length = 0;
        int maxUsage = 0;
        SegDef maximumUsageDef = new SegDef();
        maximumUsageDef.length = Integer.MAX_VALUE;
        for (int length : lengths) {
            int[] maxUse = getMaximumUse(cutMeasurements, nbrOfCuts, length);
            int usedLength = getTotalLength(cutMeasurements, maxUse);

            int waste = length - usedLength;
            if (waste < minimumWaste || (
                    waste == minimumWaste && length > minimumWasteDef.length)) {
                minimumWaste = waste;
                minimumWasteDef = new SegDef();
                minimumWasteDef.length = length;
                minimumWasteDef.usage = maxUse;
            }
            if (usedLength > maxUsage ||
                    (usedLength == maxUsage && length < maximumUsageDef.length)) {
                maxUsage = usedLength;
                maximumUsageDef = new SegDef();
                maximumUsageDef.length = length;
                maximumUsageDef.usage = maxUse;
            }
        }
        rtn.add(minimumWasteDef);
        if (maximumUsageDef.usage != minimumWasteDef.usage) {
            rtn.add(maximumUsageDef);
        }
        return rtn;
    }

    static int getFreeSpace(SegDef def, int[] cutMeasurements) {
        int remainingSpace = def.length;
        for (int cut = 0; cut < cutMeasurements.length; cut++) {
            remainingSpace -= def.usage[cut] * cutMeasurements[cut];
        }
        return remainingSpace;
    }

    static List<Segment> getOptimalSolution(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {
        return compoundSegments(getIterativeSolution(
                lengths, cutMeasurements, nbrOfCuts), cutMeasurements);
    }

    static List<SegDef> getIterativeSolution(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {

        List<SegDefLink> segLinks = new ArrayList<>();

        for (SegDef def : getSuitableLengths(lengths, cutMeasurements, nbrOfCuts)) {
                int[] remainingCuts = new int[nbrOfCuts.length];
                for (int i = 0; i < nbrOfCuts.length; i++) {
                    remainingCuts[i] = nbrOfCuts[i] - def.usage[i];
                }

                segLinks.add(new SegDefLink(null, def, remainingCuts));
        }

        int bestWaste = Integer.MAX_VALUE;
        int bestNbrOfLengths = Integer.MAX_VALUE;
        SegDefLink bestRoot = null;

        for (SegDefLink segLink : segLinks) {
            SegDefLink currLink = segLink;

            while (currLink != null && !currLink.searched) {
                if(currLink.spawnedChildren) {
                    boolean allSearched = true;
                    for (SegDefLink next : currLink.children) {
                        if (!next.searched) {
                            currLink = next;
                            allSearched = false;
                            break;
                        }
                    }

                    if (allSearched) {

                        currLink.waste = getFreeSpace(currLink.segdef, cutMeasurements);
                        currLink.nbrOfLengths = 1;
                        if (currLink.children.size() > 0) {
                            SegDefLink topChild = null;
                            int minimumWaste = Integer.MAX_VALUE;
                            int minimumNbrOfLengths = Integer.MAX_VALUE;
                            for (SegDefLink child : currLink.children) {
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
                    for (SegDef def : getSuitableLengths(lengths, cutMeasurements, currLink.remainingCuts)) {
                        if (getTotalLength(cutMeasurements, def.usage) > 0) {
                            int[] remainingCuts = new int[nbrOfCuts.length];
                            for (int i = 0; i < nbrOfCuts.length; i++) {
                                remainingCuts[i] = currLink.remainingCuts[i] - def.usage[i];
                            }
                            final SegDefLink newLink = new SegDefLink(currLink, def, remainingCuts);
                            currLink.children.add(newLink);
                            if (getTotalLength(cutMeasurements, remainingCuts) == 0) {
                                newLink.waste = getFreeSpace(def, cutMeasurements);
                                newLink.searched = true;
                                newLink.nbrOfLengths = 1;
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

        List<SegDef> rtn = new ArrayList<>();
        while (bestRoot != null) {
            rtn.add(bestRoot.segdef);
            bestRoot = bestRoot.next;
        }

        return rtn;
    }

    static class SegDefLink {
        private final List<SegDefLink> children;
        private final SegDefLink parent;
        private SegDefLink next;
        private final SegDef segdef;
        private boolean searched;
        private boolean spawnedChildren;
        private final int[] remainingCuts;
        private int waste;
        private int nbrOfLengths;

        SegDefLink(SegDefLink parent, SegDef segdef, int[] remainingCuts) {
            this.parent = parent;
            this.segdef = segdef;
            this.remainingCuts = remainingCuts;
            children = new ArrayList<>();
            searched = false;
            spawnedChildren = false;
            waste = -1;
        }

    }

    static class SegDef {
        private int length;
        private int[] usage;
    }
}
