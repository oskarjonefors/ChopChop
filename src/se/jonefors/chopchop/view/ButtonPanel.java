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

package se.jonefors.chopchop.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

/**
 * Panel containing a button for starting the solving process and a button for printing.
 * During the solving process, an indeterminate progress bar is shown instead.
 *
 * @author Oskar Jönefors
 */

public class ButtonPanel extends JPanel implements PropertyChangeListener {
    private final JPanel buttonMode;
    private final JButton calcButton;
    private final JButton printButton;
    private final JButton abortButton;

    private final JPanel workingMode;

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.i18n.Messages");

    public ButtonPanel() {

        buttonMode = new JPanel(new FlowLayout());

        calcButton = new JButton(messages.getString("calcCutSpec"));
        printButton = new JButton(messages.getString("print"));

        calcButton.setActionCommand("CALCULATE");
        printButton.setActionCommand("PRINT");

        buttonMode.add(calcButton);
        buttonMode.add(printButton);

        this.add(buttonMode);

        workingMode = new JPanel(new FlowLayout());
        final JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        abortButton = new JButton(messages.getString("cancel"));
        abortButton.setActionCommand("ABORT");
        progressBar.setIndeterminate(true);
        workingMode.add(progressBar);
        workingMode.add(abortButton);
        progressBar.setSize(this.getSize());
    }

    public void addActionListener(ActionListener listener) {
        calcButton.addActionListener(listener);
        printButton.addActionListener(listener);
        abortButton.addActionListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();

        switch (propertyName) {
            case "SOLVER_STARTED":
                remove(buttonMode);
                add(workingMode);
                revalidate();
                repaint();
                break;
            case "SOLVER_FINISHED":
            case "SOLVER_CANCELLED":
                remove(workingMode);
                add(buttonMode);
                revalidate();
                repaint();
                break;
            default:
                break;
        }
    }
}
