package se.jonefors.chopchop.view;

import se.jonefors.chopchop.Segment;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oskar Jönefors
 */

public class SummaryView extends JPanel {

    private final static Color FONT_COLOR = Color.BLACK;
    private final static Color BASE_SEGMENT_COLOR = Color.BLUE;
    private final static Color CUT_COLOR = Color.LIGHT_GRAY;
    private final static Color MEASUREMENT_FONT_COLOR = Color.BLACK;
    private final static int MEASUREMENT_FONT_SIZE = 12;
    private final static int SUMMARY_HEADER_FONT_SIZE = 18;

    private final static int SECTION_HEIGHT = 30;
    private final static int MARGIN = 5;

    private final Font headerFont = new Font("Dialog", Font.BOLD, 20);
    private final Font quantityFont = new Font("Quantity", Font.BOLD, 15);
    private final Font measurementFont = new Font("Measurement", Font.BOLD, MEASUREMENT_FONT_SIZE);
    private final Font summaryHeaderFont = new Font("Summary", Font.BOLD, SUMMARY_HEADER_FONT_SIZE);
    private List<Segment> segments;
    private String label;

    public SummaryView(List<Segment> segments, String label) {
        this.segments = segments;
        this.label = label;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (segments != null) {
            drawSummary(graphics, label);
        }
    }

    private void drawSummary(Graphics g, String label) {
        g.setFont(headerFont);
        g.drawString("Kapspecifikation: " + label, 0, 30);

        int currY = SECTION_HEIGHT * 3;

        /* Header */
        Map<Integer,Integer> segmentSummary = new HashMap<>();
        for (Segment s : segments) {
            final int segLen = s.getLength();
            final int segQty = s.getQuantity();

            if (segmentSummary.containsKey(segLen)) {
                segmentSummary.replace(segLen, segmentSummary.get(segLen) + segQty);
            } else {
                segmentSummary.put(segLen, segQty);
            }
        }

        g.setFont(summaryHeaderFont);
        g.drawString("Materialåtgång", MARGIN, currY);
        int summaryX = MARGIN + SECTION_HEIGHT + g.getFontMetrics().stringWidth("Materialåtgång");

        g.setFont(quantityFont);
        for (Integer len : segmentSummary.keySet()) {
            g.drawString(segmentSummary.get(len) + " x " + len, summaryX, currY);
            currY += g.getFontMetrics().getHeight();
        }

    }
}
