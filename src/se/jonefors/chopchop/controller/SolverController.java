package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.model.CutPlanner;
import se.jonefors.chopchop.model.ListenableSolver;
import se.jonefors.chopchop.model.SolverListener;
import se.jonefors.chopchop.model.SolverWorker;
import se.jonefors.chopchop.model.representations.Segment;
import se.jonefors.chopchop.util.CutSpecification;
import se.jonefors.chopchop.util.LengthSpecification;
import se.jonefors.chopchop.view.CutView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Oskar Jönefors
 */

public class SolverController implements ListenableSolver, ActionListener {

    private final List<SolverListener> listeners;
    private final List<CutSpecification> cuts;
    private final List<LengthSpecification> lengths;
    private final JTextField labelField;
    private SolverWorker worker;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.Messages");

    public SolverController(List<CutSpecification> cuts, List<LengthSpecification> lengths,
                            JTextField labelField) {
        listeners = new ArrayList<>();
        this.cuts = cuts;
        this.lengths = lengths;
        this.labelField = labelField;
    }

    private boolean verifyData() {
        int topLength = 0;

        for (LengthSpecification len : lengths) {
            if (len.isActive()) {
                topLength = Math.max(topLength, len.getLength());
            }
        }

        for (CutSpecification cut : cuts) {
            if (cut.getLength() > topLength) {
                JOptionPane.showMessageDialog(labelField.getParent(),
                        messages.getString("requestedCutTooLongErrorPt1") + " " + cut.getLength() +
                        " " + messages.getString("requestedCutTooLongErrorPt2")+ " " +
                        topLength, messages.getString("requestedCutTooLongErrorHeader"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "CALCULATE":
                if (verifyData()) {
                    if (worker != null && !worker.isDone()) {
                        worker.cancel(true);
                    }
                    worker = new SolverWorker(cuts, lengths, labelField.getText());
                    for (SolverListener listener : listeners) {
                        worker.addListener(listener);
                    }
                    worker.execute();
                }
                break;
            case "PRINT":
                CutPlanner planner = CutPlanner.getSharedInstance();
                List<Segment> sol = planner.getLastSolution();
                if (sol != null) {
                    CutView printCutView = new CutView();
                    printCutView.showSegments(planner.getLastSolution(), labelField.getText());
                    PrinterJob job = PrinterJob.getPrinterJob();
                    job.setPrintable(printCutView);
                    boolean ok = job.printDialog();
                    if (ok) {
                        try {
                            job.print();
                        } catch (PrinterException ex) {
                /* The job did not successfully complete */
                        }
                    }
                }
                break;
            case "ABORT":
                for (SolverListener listener : listeners) {
                    listener.notifyProcessAborted();
                }
                if (worker != null) {
                    worker.cancel(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addListener(SolverListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SolverListener listener) {
        listeners.remove(listener);
    }
}
