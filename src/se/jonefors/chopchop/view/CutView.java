package se.jonefors.chopchop.view;

import se.jonefors.chopchop.Cut;
import se.jonefors.chopchop.Segment;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oskar Jönefors
 */

public class CutView extends JPanel {

    private final static Color FONT_COLOR = Color.BLACK;
    private final static Color BASE_SEGMENT_COLOR = Color.BLUE;
    private final static Color CUT_COLOR = Color.LIGHT_GRAY;
    private final static Color MEASUREMENT_FONT_COLOR = Color.BLACK;
    private final static int MEASUREMENT_FONT_SIZE = 12;
    private final static int SUMMARY_HEADER_FONT_SIZE = 18;

    private final static int SECTION_HEIGHT = 30;
    private final static int MARGIN = 5;
    private final static int ROW_SPACING = 20;

    private final Font headerFont = new Font("Dialog", Font.BOLD, 20);
    private final Font quantityFont = new Font("Quantity", Font.BOLD, 15);
    private final Font measurementFont = new Font("Measurement", Font.BOLD, MEASUREMENT_FONT_SIZE);
    private final Font summaryHeaderFont = new Font("Summary", Font.BOLD, SUMMARY_HEADER_FONT_SIZE);
    private List<Segment> segments;

    private String name;



    public CutView() {
        this.setBackground(Color.WHITE);
        name = "";
        this.setLayout(new GridLayout());
    }

    private void drawSegments(Graphics g, String label) {
        g.setColor(FONT_COLOR);
        g.setFont(headerFont);
        g.drawString("Kapspecifikation: " + label, 0, 30);

        int currY = SECTION_HEIGHT * 3;
        final double maximumSegmentWidth = this.getWidth() - MARGIN * 2;
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

        g.setFont(summaryHeaderFont);
        g.drawString("Materialåtgång", MARGIN, currY);
        int summaryX = MARGIN + SECTION_HEIGHT + g.getFontMetrics().stringWidth("Materialåtgång");

        g.setFont(quantityFont);
        for (Integer len : segmentSummary.keySet()) {
            g.drawString(segmentSummary.get(len) + " x " + len, summaryX, currY);
            currY += g.getFontMetrics().getHeight();
        }

        for (Segment s : segments) {
            currY += SECTION_HEIGHT;
            int freeSpace = s.getFreeSpace();
            String fs = freeSpace > 0 ? ", spill " + s.getFreeSpace() : "";

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

            g.setFont(quantityFont);
            g.setColor(FONT_COLOR);
            g.drawString(s.getQuantity() + " x " + s.getLength() + fs + ":", MARGIN, currY);
            g.setColor(BASE_SEGMENT_COLOR);
            g.fillRect(MARGIN, currY + MARGIN, (int)(scale * s.getLength()), secHeight);


            int currX = MARGIN + 1;

            g.setFont(measurementFont);
            for (Cut c : s.getCuts()) {
                for (int i = 0; i < c.getQuantity(); i++) {
                    int segW = (int)(scale * c.getLength() - 2);
                    g.setColor(CUT_COLOR);
                    g.fillRect(currX, currY + MARGIN + 2,
                            segW, secHeight - 4);
                    g.setColor(MEASUREMENT_FONT_COLOR);
                    final String measurementString = Integer.toString(c.getLength());
                    final int measurementStringWidth = g.getFontMetrics().stringWidth(measurementString);
                    int centerXpos = currX + (segW / 2);
                    if (measurementStringWidth + 4 > scale * c.getLength()) {
                        centerXpos -=  (g.getFontMetrics().stringWidth("0") / 2);
                        int height = g.getFontMetrics().getHeight() - 2;
                        int verticalY = currY + SECTION_HEIGHT + 2 - height;
                        for (char ch : measurementString.toCharArray()) {
                            g.drawString(String.valueOf(ch), centerXpos, verticalY);
                            verticalY += height;
                        }
                    } else {
                        centerXpos -=  measurementStringWidth / 2;
                        g.drawString(measurementString, centerXpos, currY + SECTION_HEIGHT - 4);
                    }
                    currX += segW + 2;
                }


            }

            currY += secHeight + ROW_SPACING;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (segments != null) {
            drawSegments(graphics, name);
        }
    }

    public void showSegments(List<Segment> segments, String name) {
        this.segments = segments;
        this.name = name;
    }
}
