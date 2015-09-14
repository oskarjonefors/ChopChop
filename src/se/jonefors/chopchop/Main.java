package se.jonefors.chopchop;

import se.jonefors.chopchop.controller.SolverController;
import se.jonefors.chopchop.controller.ConfigurationManager;
import se.jonefors.chopchop.view.LengthSpecification;
import se.jonefors.chopchop.view.ButtonPanel;
import se.jonefors.chopchop.view.CutView;
import se.jonefors.chopchop.view.MainWindow;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.ResourceBundle;

class Main {

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.i18n.Messages");

    private Main() {

        final ButtonPanel buttonPanel = new ButtonPanel();
        final CutView cutView = new CutView();
        final JTextField labelField = new JTextField(messages.getString("label"));

        final List<LengthSpecification> lengthSpecifications =
                ConfigurationManager.getSavedLengths();

        final MainWindow mainWindow = new MainWindow(buttonPanel, cutView, labelField,
                lengthSpecifications);

        final SolverController controller = new SolverController(mainWindow, labelField);
        controller.addPropertyChangeListener(buttonPanel);
        controller.addPropertyChangeListener(cutView);
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
