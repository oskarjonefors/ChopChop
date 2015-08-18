package se.jonefors.chopchop.view;

import se.jonefors.chopchop.model.SolverListener;
import se.jonefors.chopchop.model.representations.Segment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oskar Jönefors
 */

public class ButtonPanel extends JPanel implements SolverListener {
    private JPanel buttonMode;
    private JButton calcButton;
    private JButton printButton;
    private JButton abortButton;

    private JPanel workingMode;
    private List<ActionListener> listeners;

    public ButtonPanel() {

        listeners = new ArrayList<>();

        buttonMode = new JPanel(new FlowLayout());

        calcButton = new JButton("Beräkna kapschema");
        printButton = new JButton("Skriv ut");

        calcButton.setActionCommand("CALCULATE");
        printButton.setActionCommand("PRINT");

        buttonMode.add(calcButton);
        buttonMode.add(printButton);

        this.add(buttonMode);

        workingMode = new JPanel(new FlowLayout());
        final JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        abortButton = new JButton("Avbryt");
        abortButton.setActionCommand("ABORT");
        progressBar.setIndeterminate(true);
        workingMode.add(progressBar);
        workingMode.add(abortButton);
        progressBar.setSize(this.getSize());
    }

    @Override
    public void notifyProcessStarted() {
        remove(buttonMode);
        add(workingMode);
        revalidate();
        repaint();
    }

    @Override
    public void notifyProcessAborted() {
        remove(workingMode);
        add(buttonMode);
        revalidate();
        repaint();
    }

    @Override
    public void notifySolution(List<Segment> solution, String label) {
        remove(workingMode);
        add(buttonMode);
        revalidate();
        repaint();
    }

    public void addActionListener(ActionListener listener) {
        calcButton.addActionListener(listener);
        printButton.addActionListener(listener);
        abortButton.addActionListener(listener);
    }

    public void removeActionListener(ActionListener listener) {
        calcButton.removeActionListener(listener);
        printButton.removeActionListener(listener);
        abortButton.removeActionListener(listener);
    }

}
