package se.jonefors.chopchop.view;

import se.jonefors.chopchop.Segment;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * @author Oskar Jönefors
 */

public class CutViewPrinter implements Printable {

    private java.util.List<Segment> segments;
    private String label;

    public CutViewPrinter(java.util.List<Segment> segments, String label) {
        this.segments = segments;
        this.label = label;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int i) throws PrinterException {
        CutView printCutView = new CutView();
        printCutView.showSegments(segments, label);
        printCutView.setSize(new Dimension((int)pageFormat.getImageableWidth(),(int)pageFormat.getImageableHeight()));
            /*
     * User (0,0) is typically outside the imageable area, so we must translate
     * by the X and Y values in the PageFormat to avoid clipping
     */
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

    /* Now print the window and its visible contents */
        printCutView.printAll(graphics);

    /* tell the caller that this page is part of the printed document */
        return i < 1 ? PAGE_EXISTS : NO_SUCH_PAGE;
    }
}
