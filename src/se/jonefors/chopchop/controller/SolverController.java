/*
 * ChopChop - A very simple 1D cut optimizer with printing capability.
 * Copyright (C) 2015  Oskar Jönefors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.jonefors.chopchop.controller;

import se.jonefors.chopchop.view.CutSpecification;
import se.jonefors.chopchop.view.LengthSpecification;
import se.jonefors.chopchop.view.CutView;
import se.jonefors.chopchop.view.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Class that starts and stops the solving process.
 * Also starts the printing of cut specifications.
 *
 * @author Oskar Jönefors
 */

public class SolverController implements ActionListener {

    private final JTextField labelField;
    private SolverWorker worker;
    private final PropertyChangeSupport propertyChangeSupport;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.i18n.Messages");

    private final MainWindow view;

    /**
     * Create a new SolverController which will fetch the solving parameters from the given
     * MainWindow, and the label of the problem from the given JTextField.
     *
     * @param view          May not be null.
     * @param labelField    May not be null.
     */
    public SolverController(MainWindow view,
                            JTextField labelField) {
        if (view == null) {
            throw new NullPointerException("SolverController(MainWindow, JTextField): " +
                    "MainWindow was null!");
        }
        if (labelField == null) {
            throw new NullPointerException("SolverController(MainWindow, JTextField): " +
                    "JTextField was null!");
        }
        this.view = view;
        this.labelField = labelField;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    private boolean verifyData() {
        int topLength = 0;

        for (LengthSpecification len : view.getLengths()) {
            if (len.isActive()) {
                topLength = Math.max(topLength, len.getLength());
            }
        }

        for (CutSpecification cut : view.getCuts()) {
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
                    /* No need to pass on the placeholder name to the specification */
                    final String label = labelField.getText().equals(messages.getString("label")) ?
                            "" : labelField.getText();
                    worker = new SolverWorker(view.getCuts(), view.getLengths(), label);
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
                    } catch (InterruptedException | ExecutionException e) {
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
