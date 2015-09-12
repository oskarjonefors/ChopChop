package se.jonefors.chopchop.view;

import se.jonefors.chopchop.util.ConfigurationManager;
import se.jonefors.chopchop.util.CutSpecification;
import se.jonefors.chopchop.util.LengthSpecification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

/**
 * @author Oskar Jönefors
 */

public class MainWindow extends JFrame {

    private static final String APP_NAME = "Kapet";
    private static final String NAME_DEFAULT = "Märke";

    public MainWindow() {

        final JTextField labelField = new JTextField(NAME_DEFAULT);
        java.util.List<CutSpecification> cutSpecificationList = new ArrayList<>();
        CutTable ct = new CutTable(cutSpecificationList, labelField);

        final java.util.List<LengthSpecification> lengthSpecificationList = ConfigurationManager.getSavedLengths();
        LengthTable lt = new LengthTable(lengthSpecificationList);

        CutView cv = new CutView();
        JScrollPane cvs = new JScrollPane();
        cvs.setViewportView(cv);

        this.setLayout(new BorderLayout(0,0));
        this.add(ct, BorderLayout.LINE_START);
        this.add(cvs, BorderLayout.CENTER);
        this.add(lt, BorderLayout.LINE_END);

        final SolverController controller = new SolverController(cutSpecificationList, lengthSpecificationList,
                labelField);
        final ButtonPanel buttonPanel = new ButtonPanel();
        this.add(buttonPanel, BorderLayout.SOUTH);

        controller.addListener(buttonPanel);
        controller.addListener(cv);

        buttonPanel.addActionListener(controller);

        this.getContentPane().getComponent(0).setPreferredSize(new Dimension(150, 400));
        this.getContentPane().getComponent(1).setPreferredSize(new Dimension(500, 400));
        this.getContentPane().getComponent(2).setPreferredSize(new Dimension(150, 400));

        this.setTitle(APP_NAME);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                ConfigurationManager.writeConfig(lengthSpecificationList);
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
                new MainWindow();
            }
        });
    }

}
