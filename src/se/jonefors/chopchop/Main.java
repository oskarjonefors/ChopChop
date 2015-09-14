package se.jonefors.chopchop;

import se.jonefors.chopchop.controller.SolverController;
import se.jonefors.chopchop.util.ConfigurationManager;
import se.jonefors.chopchop.util.CutSpecification;
import se.jonefors.chopchop.util.LengthSpecification;
import se.jonefors.chopchop.view.ButtonPanel;
import se.jonefors.chopchop.view.CutView;
import se.jonefors.chopchop.view.MainWindow;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Main {

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.Messages");

    public Main() {

        final ButtonPanel buttonPanel = new ButtonPanel();
        final CutView cutView = new CutView();
        final JTextField labelField = new JTextField(messages.getString("label"));

        final List<CutSpecification> cutSpecifications = new ArrayList<>();
        final List<LengthSpecification> lengthSpecifications =
                ConfigurationManager.getSavedLengths();

        final MainWindow mainWindow = new MainWindow(buttonPanel, cutView, labelField,
                cutSpecifications, lengthSpecifications);

        final SolverController controller = new SolverController(cutSpecifications, lengthSpecifications,
                labelField);
        controller.addListener(buttonPanel);
        controller.addListener(cutView);
        buttonPanel.addActionListener(controller);

        mainWindow.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                ConfigurationManager.writeConfig(lengthSpecifications);
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}
