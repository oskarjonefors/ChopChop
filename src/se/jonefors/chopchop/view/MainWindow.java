package se.jonefors.chopchop.view;

import se.jonefors.chopchop.util.CutSpecification;
import se.jonefors.chopchop.util.LengthSpecification;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Oskar Jönefors
 */

public class MainWindow extends JFrame {

    private static final ResourceBundle messages =
            ResourceBundle.getBundle("se.jonefors.chopchop.Messages");

    public MainWindow(ButtonPanel buttonPanel, CutView cutView,
                      JTextField labelField,
                      List<CutSpecification> cutSpecifications,
                      List<LengthSpecification> lengthSpecifications) {

        CutTable cutTable = new CutTable(cutSpecifications, labelField);

        LengthTable lengthTable = new LengthTable(lengthSpecifications);

        JScrollPane cutViewScrollPane = new JScrollPane();
        cutViewScrollPane.setViewportView(cutView);

        this.setLayout(new BorderLayout(0,0));
        this.add(cutTable, BorderLayout.LINE_START);
        this.add(cutViewScrollPane, BorderLayout.CENTER);
        this.add(lengthTable, BorderLayout.LINE_END);

        this.add(buttonPanel, BorderLayout.SOUTH);

        this.getContentPane().getComponent(0).setPreferredSize(new Dimension(150, 400));
        this.getContentPane().getComponent(1).setPreferredSize(new Dimension(500, 400));
        this.getContentPane().getComponent(2).setPreferredSize(new Dimension(150, 400));

        this.setTitle(messages.getString("appName"));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

}
