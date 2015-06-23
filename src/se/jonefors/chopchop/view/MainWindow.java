package se.jonefors.chopchop.view;

import se.jonefors.chopchop.CutPlanner;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Oskar Jönefors
 */

public class MainWindow extends JFrame {

    public MainWindow() {

        java.util.List<CutSpecification> cutSpecificationList = new ArrayList<>();
        CutTable ct = new CutTable(cutSpecificationList);

        java.util.List<LengthSpecification> lengthSpecificationList = new ArrayList<>();
        LengthTable lt = new LengthTable(lengthSpecificationList);

        CutView cv = new CutView();

        this.setLayout(new BorderLayout(0,0));
        this.add(ct, BorderLayout.LINE_START);
        this.add(cv, BorderLayout.CENTER);
        this.add(lt, BorderLayout.LINE_END);

        final JButton calcButton = new JButton("Beräkna kapschema");
        calcButton.addActionListener(new CalculateButtonListener(cutSpecificationList, lengthSpecificationList, new CutPlanner(), cv));
        this.add(calcButton, BorderLayout.SOUTH);

        this.getContentPane().getComponent(0).setPreferredSize(new Dimension(150, 400));
        this.getContentPane().getComponent(1).setPreferredSize(new Dimension(500, 400));
        this.getContentPane().getComponent(2).setPreferredSize(new Dimension(150, 400));

        //this.setResizable(false);


        this.setTitle("ChopChop");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(MainWindow::new);
    }

}
