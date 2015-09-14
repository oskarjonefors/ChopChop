package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.model.PropertyChangeHandler;
import se.jonefors.chopchop.model.SolverWorker;
import se.jonefors.chopchop.util.CutSpecification;
import se.jonefors.chopchop.util.LengthSpecification;
import se.jonefors.chopchop.view.CutView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * @author Oskar Jönefors
 */

public class SolverController implements PropertyChangeHandler, ActionListener {

    private final List<CutSpecification> cuts;
    private final List<LengthSpecification> lengths;
    private final JTextField labelField;
    private SolverWorker worker;
    private PropertyChangeSupport propertyChangeSupport;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.Messages");

    public SolverController(List<CutSpecification> cuts, List<LengthSpecification> lengths,
                            JTextField labelField) {
        this.cuts = cuts;
        this.lengths = lengths;
        this.labelField = labelField;
        propertyChangeSupport = new PropertyChangeSupport(this);
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
                    for (PropertyChangeListener listener : propertyChangeSupport.getPropertyChangeListeners()) {
                        worker.addPropertyChangeListener(listener);
                    }
                    worker.execute();
                }
                break;
            case "PRINT":
                if (worker != null && worker.isDone()) {
                    CutView printCutView = new CutView();
                    try {
                        printCutView.showSegments(worker.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
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
                if (worker != null) {
                    worker.cancel(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
