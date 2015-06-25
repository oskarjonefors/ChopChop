package se.jonefors.chopchop.view;

import se.jonefors.chopchop.CutPlanner;

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

        final JTextField nameField = new JTextField(NAME_DEFAULT);
        java.util.List<CutSpecification> cutSpecificationList = new ArrayList<>();
        CutTable ct = new CutTable(cutSpecificationList, nameField);

        final java.util.List<LengthSpecification> lengthSpecificationList = ConfigurationManager.getSavedLengths();
        LengthTable lt = new LengthTable(lengthSpecificationList);

        CutPlanner cp = new CutPlanner();
        CutView cv = new CutView();
        JScrollPane cvs = new JScrollPane();
        cvs.setViewportView(cv);

        this.setLayout(new BorderLayout(0,0));
        this.add(ct, BorderLayout.LINE_START);
        this.add(cvs, BorderLayout.CENTER);
        this.add(lt, BorderLayout.LINE_END);

        final JPanel buttonPanel = new JPanel(new GridLayout(1, 2));


        final JButton calcButton = new JButton("Beräkna kapschema");
        calcButton.addActionListener(new CalculateButtonListener(cutSpecificationList,
                lengthSpecificationList, cp, cv, nameField));

        final JButton printButton = new JButton("Skriv ut");
        this.add(printButton, BorderLayout.SOUTH);
        printButton.addActionListener(new PrintButtonListener(cp, nameField));

        buttonPanel.add(calcButton);
        buttonPanel.add(printButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

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
