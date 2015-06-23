package se.jonefors.chopchop.view;

import se.jonefors.chopchop.Cut;
import se.jonefors.chopchop.Segment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class CutView extends JPanel {

    private final static Color FONT_COLOR = Color.BLACK;
    private final static Color BASE_SEGMENT_COLOR = Color.BLUE;
    private final static Color CUT_COLOR = Color.LIGHT_GRAY;
    private final static Color MEASUREMENT_FONT_COLOR = Color.BLACK;
    private final static int MEASUREMENT_FONT_SIZE = 11;

    private final static int SECTION_HEIGHT = 30;
    private final static int MARGIN = 5;

    private final Font headerFont;
    private final Font quantityFont;
    private final Font measurementFont;
    private List<Segment> segments;



    public CutView() {

        this.setBackground(Color.WHITE);
        headerFont = new Font("Dialog", Font.BOLD, 20);
        quantityFont = new Font("Quantity", Font.BOLD, 15);
        measurementFont = new Font("Measurement", Font.BOLD, MEASUREMENT_FONT_SIZE);

    }

    private void drawSegments(Graphics g) {
        int currY = SECTION_HEIGHT;
        final double maximumSegmentWidth = this.getWidth() - MARGIN * 2;
        final double scale = maximumSegmentWidth / segments.get(0).getLength();
        
        for (Segment s : segments) {
            currY += SECTION_HEIGHT;
            int freeSpace = s.getFreeSpace();
            String fs = freeSpace > 0 ? ", spill " + s.getFreeSpace() : "";

            g.setFont(quantityFont);
            g.setColor(FONT_COLOR);
            g.drawString(s.getQuantity() + " x " + s.getLength() + fs + ":", MARGIN, currY);
            g.setColor(BASE_SEGMENT_COLOR);
            g.fillRect(MARGIN, currY + MARGIN, (int)(scale * s.getLength()), SECTION_HEIGHT);


            int currX = MARGIN + 1;
            for (Cut c : s.getCuts()) {
                for (int i = 0; i < c.getQuantity(); i++) {
                    int segW = (int)(scale * c.getLength() - 2);
                    g.setColor(CUT_COLOR);
                    g.fillRect(currX, currY + MARGIN + 2,
                            segW, SECTION_HEIGHT - 4);

                    g.setFont(measurementFont);
                    g.setColor(MEASUREMENT_FONT_COLOR);
                    final String measurementString = Integer.toString(c.getLength());
                    if (g.getFontMetrics().stringWidth(measurementString) > scale * c.getLength()) {
                        int height = g.getFontMetrics().getHeight();
                        int verticalY = currY + SECTION_HEIGHT - height;
                        for (char ch : measurementString.toCharArray()) {
                            g.drawString(String.valueOf(ch), currX + 2, verticalY);
                            verticalY += height;
                        }
                    } else {
                        g.drawString(measurementString, currX + 2, currY + SECTION_HEIGHT - 4);
                    }
                    currX += segW + 2;
                }


            }

            currY += SECTION_HEIGHT;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setFont(headerFont);
        graphics.drawString("ChopChop", 0, 30);

        if (segments != null) {
            drawSegments(graphics);
        }
    }

    public void showSegments(List<Segment> segments) {
        this.segments = segments;
    }

}
