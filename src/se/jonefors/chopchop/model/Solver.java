package se.jonefors.chopchop.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that handles the solving algorithm.
 * Does not verify any parameters.
 *
 * @author Oskar Jönefors
 */

@SuppressWarnings("ConstantConditions")
class Solver {
    private static boolean cancel = false;

    /**
     * Get a list of segments with the requested cuts distributed over the given lengths so that the
     * total free space of all segments in the solution is minimized.
     *
     * @param availableLengths  A list of positive non-zero integers.
     * @param requestedCuts  A list of requested cuts.
     * @return  A solution represented by a list of Segments with the requested cuts distributed
     *          over them.
     */
    public static List<Segment> getOptimalSolution(List<Integer> availableLengths, List<Cut> requestedCuts) {
        Collections.sort(availableLengths);
        Collections.reverse(availableLengths);

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

        cancel = false;
        return compoundSegments(getIterativeSolution(
                lengths, cutMeasurements, nbrOfCuts), cutMeasurements);
    }

    /**
     * Cancel any ongoing solving process.
     */
    public static void cancel() {
        cancel = true;
    }

    private static int[] getMaximumUse(int[] cuts, int[] nbrOfCuts, int baseLength) {

        int[] maxNbrOfCuts = new int[nbrOfCuts.length];

        for (int cut = 0; cut < nbrOfCuts.length; cut++) {
            final int measurement = cuts[cut];
            final int maxQty = baseLength / measurement;
            maxNbrOfCuts[cut] = maxQty < nbrOfCuts[cut] ? maxQty : nbrOfCuts[cut];
        }

        int[] optimalSolution = new int[maxNbrOfCuts.length];
        int[] attempt = new int[maxNbrOfCuts.length];
        int currentIndex = 0;
        int minimumWaste = baseLength;
        boolean triedAllSolutions = false;

        while (!triedAllSolutions && !cancel) {

            if (currentIndex < attempt.length && attempt[currentIndex] < maxNbrOfCuts[currentIndex]) {
                    /* Add one more of the current cut if possible */
                attempt[currentIndex]++;
                currentIndex = 0;
            } else if (currentIndex < attempt.length) {
                attempt[currentIndex] = 0;
                currentIndex++;
            } else {
                triedAllSolutions = true;
            }

            int currentWaste = baseLength -
                    getTotalLength(cuts, attempt);

            if (currentWaste >= 0 && currentWaste < minimumWaste) {
                minimumWaste = currentWaste;
                optimalSolution = Arrays.copyOf(attempt, attempt.length);
            }

        }

        if (cancel) {
            return null;
        }

        return optimalSolution;
    }

    private static int getTotalLength(int[] measurements, int[] nbrOfCuts) {
        int length = 0;
        for (int cut = 0; cut < nbrOfCuts.length; cut++) {
            length += measurements[cut] * nbrOfCuts[cut];
        }
        return length;
    }

    private static List<Segment> compoundSegments(List<SegDef> segDefList, int[] cutMeasurements) {
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

    private static List<SegDef> getSuitableLengths(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {
        List<SegDef> rtn = new ArrayList<>();

        int minimumWaste = Integer.MAX_VALUE;
        SegDef minimumWasteDef = new SegDef();
        minimumWasteDef.length = 0;
        int maxUsage = 0;
        SegDef maximumUsageDef = new SegDef();
        maximumUsageDef.length = Integer.MAX_VALUE;
        for (int length : lengths) {
            if (cancel) {
                break;
            }
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

    private static int getFreeSpace(SegDef def, int[] cutMeasurements) {
        int remainingSpace = def.length;
        for (int cut = 0; cut < cutMeasurements.length; cut++) {
            remainingSpace -= def.usage[cut] * cutMeasurements[cut];
        }
        return remainingSpace;
    }

    private static List<SegDef> getIterativeSolution(int[] lengths, int[] cutMeasurements, int[] nbrOfCuts) {

        List<SegDefLink> segLinks = new ArrayList<>();

        for (SegDef def : getSuitableLengths(lengths, cutMeasurements, nbrOfCuts)) {
            if (cancel) {
                break;
            }
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
            if (cancel) {
                break;
            }

            SegDefLink currLink = segLink;

            while (currLink != null && !currLink.searched && !cancel) {
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
                } else if (!cancel) {
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
        while (bestRoot != null && !cancel) {
            rtn.add(bestRoot.segdef);
            bestRoot = bestRoot.next;
        }

        return cancel ? null : rtn;
    }

    private static class SegDefLink {
        private final List<SegDefLink> children = new ArrayList<>();
        private final SegDefLink parent;
        private SegDefLink next;
        private final SegDef segdef;
        private boolean searched = false;
        private boolean spawnedChildren = false;
        private final int[] remainingCuts;
        private int waste = -1;
        private int nbrOfLengths;

        SegDefLink(SegDefLink parent, SegDef segdef, int[] remainingCuts) {
            this.parent = parent;
            this.segdef = segdef;
            this.remainingCuts = remainingCuts;
        }
    }

    private static class SegDef {
        private int length;
        private int[] usage;
    }
}
