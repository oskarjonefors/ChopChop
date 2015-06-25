package se.jonefors.chopchop.view;

import se.jonefors.chopchop.Segment;

import javax.swing.*;
import java.awt.*;

/**
 * @author Oskar Jönefors
 */

public class SegmentView extends JPanel {

    private Segment segment;

    public SegmentView(Segment segment) {
        this.segment = segment;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);



    }

}
