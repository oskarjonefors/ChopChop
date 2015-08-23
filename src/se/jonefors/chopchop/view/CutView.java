package se.jonefors.chopchop.view;

import se.jonefors.chopchop.model.SolverListener;
import se.jonefors.chopchop.model.representations.Cut;
import se.jonefors.chopchop.model.representations.Segment;
import se.jonefors.chopchop.model.representations.SegmentComparator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class CutView extends JPanel implements SolverListener {

    private final static Color FONT_COLOR = Color.BLACK;
    private final static Color BASE_SEGMENT_COLOR = Color.BLUE;
    private final static Color CUT_COLOR = Color.WHITE;
    private final static Color MEASUREMENT_FONT_COLOR = Color.BLACK;
    private final static int MEASUREMENT_FONT_SIZE = 12;
    private final static int SUMMARY_HEADER_FONT_SIZE = 18;

    private final static int SECTION_HEIGHT = 30;
    private final static int MARGIN = 15;
    private final static int ROW_SPACING = 20;
    private final static int MAX_NBR_OF_PAGES = 20;

    private final Font headerFont = new Font("Dialog", Font.BOLD, 20);
    private final Font quantityFont = new Font("Quantity", Font.BOLD, 15);
    private final Font measurementFont = new Font("Measurement", Font.BOLD, MEASUREMENT_FONT_SIZE);
    private final Font summaryHeaderFont = new Font("Summary", Font.BOLD, SUMMARY_HEADER_FONT_SIZE);
    private List<Segment> segments;

    private String name;
    private int totHeight;
    private int[] pagePaddings;
    private int pageNbr;



    public CutView() {
        this.setBackground(Color.WHITE);
        pageNbr = -1;
        pagePaddings = new int[MAX_NBR_OF_PAGES];
    }

    private void drawSegments(Graphics g, String label) {
        g.setColor(FONT_COLOR);
        g.setFont(headerFont);
        int currY = g.getFontMetrics().getHeight() + MARGIN;
        if (pageNbr >= 0) {
            int currentPadding = 0;
            for (int i = 0; i < pageNbr; i++) {
                currentPadding += pagePaddings[i];
            }
            currY -= pageNbr * getHeight() - currentPadding;
        }

        if (currY >= 0) {
            g.drawString("Kapspecifikation: " + label, MARGIN, currY);
        }
        currY += ROW_SPACING;
        final double maximumSegmentWidth = this.getWidth() - MARGIN * 4;
        final double scale = maximumSegmentWidth / segments.get(0).getLength();

        /* Header */
        Map<Integer,Integer> segmentSummary = new HashMap<>();
        for (Segment s : segments) {
            final int segLen = s.getLength();
            final int segQty = s.getQuantity();

            if (segmentSummary.containsKey(segLen)) {
                final int prevQty = segmentSummary.get(segLen);
                segmentSummary.remove(segLen);
                segmentSummary.put(segLen, prevQty + segQty);
            } else {
                segmentSummary.put(segLen, segQty);
            }
        }

        currY += ROW_SPACING;
        g.setFont(summaryHeaderFont);

        if (currY >= 0) {
            g.drawString("Materialåtgång", MARGIN, currY);
        }
        int summaryX = MARGIN + SECTION_HEIGHT + g.getFontMetrics().stringWidth("Materialåtgång");

        g.setFont(quantityFont);

        List<Integer> segLengths = new ArrayList<>();
        segLengths.addAll(segmentSummary.keySet());
        Collections.sort(segLengths);
        Collections.reverse(segLengths);

        for (Integer len : segLengths) {
            if (pageNbr < 0 || isWithinPageBounds(currY)) {
                g.drawString(segmentSummary.get(len) + " x " + len, summaryX, currY);
            }
            currY += g.getFontMetrics().getHeight();
        }

        for (Segment s : segments) {
            currY += SECTION_HEIGHT;

            int freeSpace = s.getFreeSpace();
            String fs = freeSpace > 0 ? ", spill " + s.getFreeSpace() + " per längd, " +
                    (s.getFreeSpace() * s.getQuantity()) + " totalt" : "";

            g.setFont(measurementFont);
            int maxHeight = 0;
            for (Cut c : s.getCuts()) {
                final String measurementString = Integer.toString(c.getLength());
                final FontMetrics m = g.getFontMetrics();
                if (m.stringWidth(measurementString) + 4 > scale * c.getLength()) {
                    final int height = m.getHeight() * measurementString.length();
                    maxHeight = Math.max(maxHeight, height);
                }
            }

            int secHeight = Math.max(SECTION_HEIGHT, maxHeight);
            int recStartY = currY + ROW_SPACING / 2;

            g.setFont(quantityFont);

            if (pageNbr >= 0 && currY + secHeight + g.getFontMetrics().getHeight() > getHeight()) {
                pagePaddings[pageNbr] = getHeight() + g.getFontMetrics().getHeight() - currY;
                currY += pagePaddings[pageNbr];
                break;
            }
            if (pageNbr < 0 || isWithinPageBounds(currY) && isWithinPageBounds(recStartY)) {
                g.setColor(FONT_COLOR);
                g.drawString(s.getQuantity() + " x " + s.getLength() + fs + ":", MARGIN, currY);
                g.setColor(BASE_SEGMENT_COLOR);
                g.fillRect(MARGIN, recStartY, (int)(scale * s.getLength()), secHeight);
            }

            int currX = MARGIN + 1;


            g.setFont(measurementFont);
            for (Cut c : s.getCuts()) {
                for (int i = 0; i < c.getQuantity(); i++) {
                    int segW = (int)(scale * c.getLength() - 2);

                    if (pageNbr < 0 || isWithinPageBounds(recStartY + 2)) {
                        g.setColor(CUT_COLOR);
                        g.fillRect(currX, recStartY + 2,
                                segW, secHeight - 4);
                    }

                    final String measurementString = Integer.toString(c.getLength());
                    final int measurementStringWidth = g.getFontMetrics().stringWidth(measurementString);
                    int centerXpos = currX + (segW / 2);
                    int height = g.getFontMetrics().getHeight() - 2;
                    g.setColor(MEASUREMENT_FONT_COLOR);
                    if (measurementStringWidth + 4 > scale * c.getLength()) {
                        centerXpos -=  (g.getFontMetrics().stringWidth("0") / 2);
                        int verticalY = recStartY + height;
                        for (char ch : measurementString.toCharArray()) {
                            if (pageNbr < 0 || isWithinPageBounds(verticalY)) {
                                g.drawString(String.valueOf(ch), centerXpos, verticalY);
                            }
                            verticalY += height;
                        }
                    } else {
                        centerXpos -=  measurementStringWidth / 2;
                        if (pageNbr < 0 || isWithinPageBounds(recStartY + 2)) {
                            g.drawString(measurementString, centerXpos, recStartY + (secHeight - 4 + height) / 2);
                        }
                    }
                    currX += segW + 2;
                }


            }

            currY += secHeight + ROW_SPACING;
        }

        totHeight = currY;
        if (pageNbr >= 0) {
            totHeight += getHeight() * pageNbr;
            for (int p = 0; p <= pageNbr; p++) {
                totHeight += pagePaddings[p];
            }
        } else {
            this.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(), totHeight));
            this.revalidate();
        }
    }

    private boolean isWithinPageBounds(int y) {
        return y >= 0 && y <= getHeight();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (segments != null) {
            drawSegments(graphics, name);
        }
    }

    public void showSegments(List<Segment> segments, String name) {
        showSegments(segments, name, -1);
    }

    public void showSegments(List<Segment> segments, String name, int pageNbr) {
        Collections.sort(segments, new SegmentComparator());
        this.segments = segments;
        this.name = name;
        this.pageNbr = pageNbr;
        if (pageNbr >= pagePaddings.length) {
            pagePaddings = Arrays.copyOf(pagePaddings, pageNbr * 2);
        }
        repaint();
    }

    @Override
    public void notifyProcessStarted() {
        segments = null;
        repaint();
    }

    @Override
    public void notifyProcessAborted() {
        segments = null;
        repaint();
    }

    @Override
    public void notifySolution(List<Segment> solution, String label) {
        showSegments(solution, label);
    }

    public boolean isPageNbrWithinBounds(int i) {
        return i * getHeight() < totHeight;
    }
}
