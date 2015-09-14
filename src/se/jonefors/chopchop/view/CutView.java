package se.jonefors.chopchop.view;

import se.jonefors.chopchop.model.Cut;
import se.jonefors.chopchop.model.Segment;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class CutView extends JPanel implements Printable, PropertyChangeListener {

    private final static Color FONT_COLOR = Color.BLACK;
    private final static Color BASE_SEGMENT_COLOR = Color.BLUE;
    private final static Color CUT_COLOR = Color.WHITE;
    private final static Color MEASUREMENT_FONT_COLOR = Color.BLACK;
    private final static int MEASUREMENT_FONT_SIZE = 12;
    private final static int SUMMARY_HEADER_FONT_SIZE = 18;

    private final static int SECTION_HEIGHT = 30;
    private final static int MARGIN = 15;
    private final static int SUMMARY_HORIZONTAL_SPACE = 10;
    private final static int ROW_SPACING = 20;
    private final static int MAX_NBR_OF_PAGES = 20;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.i18n.Messages");

    private final Font headerFont = new Font("Dialog", Font.BOLD, 20);
    private final Font quantityFont = new Font("Quantity", Font.BOLD, 15);
    private final Font measurementFont = new Font("Measurement", Font.BOLD, MEASUREMENT_FONT_SIZE);
    private final Font summaryHeaderFont = new Font("Summary", Font.BOLD, SUMMARY_HEADER_FONT_SIZE);

    private Solution solution;
    private int totHeight;
    private int[] pagePadding;
    private int pageNbr;

    public CutView() {
        this.setBackground(Color.WHITE);
        pageNbr = -1;
        pagePadding = new int[MAX_NBR_OF_PAGES];
    }

    private void drawSegments(Graphics g) {

        final List<Segment> segments = solution.getSegments();
        final String label = solution.getLabel();

        g.setColor(FONT_COLOR);
        g.setFont(headerFont);
        int currY = g.getFontMetrics().getHeight() + MARGIN;
        int pageMargin = MARGIN;
        if (pageNbr >= 0) {
            int currentPadding = 0;
            for (int i = 0; i < pageNbr; i++) {
                currentPadding += pagePadding[i];
            }
            currY -= pageNbr * getHeight() - currentPadding;
            pageMargin = 2 * MARGIN;
        }

        if (currY >= 0) {
            g.drawString(messages.getString("cutSpecification") + ": " + label, pageMargin, currY);
        }

        currY += ROW_SPACING;
        final double maximumSegmentWidth = this.getWidth() - (pageMargin + MARGIN);
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
            g.drawString(messages.getString("usedFullLengths"), pageMargin, currY);
        }
        int summaryX = pageMargin + SUMMARY_HORIZONTAL_SPACE +
                g.getFontMetrics().stringWidth(messages.getString("usedFullLengths"));

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
            String fs = freeSpace > 0 ? ", " + messages.getString("waste") + " " +
                    s.getFreeSpace() + " " + messages.getString("perLength") + ", " +
                    (s.getFreeSpace() * s.getQuantity()) + " " + messages.getString("inTotal") : "";

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
                pagePadding[pageNbr] = getHeight() + g.getFontMetrics().getHeight() - currY;
                currY += pagePadding[pageNbr];
                break;
            }
            if (pageNbr < 0 || isWithinPageBounds(currY) && isWithinPageBounds(recStartY)) {
                g.setColor(FONT_COLOR);
                g.drawString(s.getQuantity() + " x " + s.getLength() + fs + ":", pageMargin, currY);
                g.setColor(BASE_SEGMENT_COLOR);
                g.fillRect(pageMargin, recStartY, (int)(scale * s.getLength()), secHeight);
            }

            int currX = pageMargin + 1;


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
                    int centerXPos = currX + (segW / 2);
                    int height = g.getFontMetrics().getHeight() - 2;
                    g.setColor(MEASUREMENT_FONT_COLOR);
                    if (measurementStringWidth + 4 > scale * c.getLength()) {
                        centerXPos -=  (g.getFontMetrics().stringWidth("0") / 2);
                        int verticalY = recStartY + height;
                        for (char ch : measurementString.toCharArray()) {
                            if (pageNbr < 0 || isWithinPageBounds(verticalY)) {
                                g.drawString(String.valueOf(ch), centerXPos, verticalY);
                            }
                            verticalY += height;
                        }
                    } else {
                        centerXPos -=  measurementStringWidth / 2;
                        if (pageNbr < 0 || isWithinPageBounds(recStartY + 2)) {
                            g.drawString(measurementString, centerXPos, recStartY + (secHeight - 4 + height) / 2);
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
                totHeight += pagePadding[p];
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

        if (solution != null) {
            drawSegments(graphics);
        }
    }

    public void showSegments(Solution solution) {
        this.solution = solution;
        showSegments(-1);
    }

    private void showSegments(int pageNbr) {
        this.pageNbr = pageNbr;
        if (pageNbr >= pagePadding.length) {
            pagePadding = Arrays.copyOf(pagePadding, pageNbr * 2);
        }
        repaint();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int i) throws PrinterException {
        showSegments(i);
        setSize(new Dimension((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight()));

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        printAll(graphics);

    /* tell the caller that this page is part of the printed document */
        return i * getHeight() < totHeight ? PAGE_EXISTS : NO_SUCH_PAGE;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();

        switch (propertyName) {
            case "SOLVER_STARTED":
            case "SOLVER_CANCELLED":
                repaint();
                break;
            case "SOLVER_FINISHED":
                Object oSolution = propertyChangeEvent.getNewValue();
                if (oSolution.getClass() == Solution.class) {
                    showSegments((Solution) oSolution);
                }
            default:
                break;
        }
    }
}
