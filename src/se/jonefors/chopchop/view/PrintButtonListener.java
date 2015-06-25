package se.jonefors.chopchop.view;

import se.jonefors.chopchop.CutPlanner;
import se.jonefors.chopchop.Segment;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class PrintButtonListener implements ActionListener {

    private CutPlanner planner;
    private JTextField label;

    public PrintButtonListener(CutPlanner planner, JTextField label) {
        this.planner = planner;
        this.label = label;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        List<Segment> sol = planner.getLastSolution();
        if (sol != null) {
            CutViewPrinter printer = new CutViewPrinter(planner.getLastSolution(), label.getText());
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(printer);
            boolean ok = job.printDialog();
            if (ok) {
                try {
                    job.print();
                } catch (PrinterException ex) {
        /* The job did not successfully complete */
                }
            }
        }
    }
}
