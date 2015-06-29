package se.jonefors.chopchop.view;

import se.jonefors.chopchop.CutPlanner;
import se.jonefors.chopchop.Segment;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oskar Jönefors
 */

public class CalculateButtonListener implements ActionListener {

    private final List<CutSpecification> cuts;
    private final List<LengthSpecification> lengths;
    private final CutPlanner planner;
    private final CutView cv;
    private final JTextField nameField;

    public CalculateButtonListener(List<CutSpecification> cuts, List<LengthSpecification> lengths,
                                   CutPlanner planner, CutView cv, JTextField nameField) {
        this.cuts = cuts;
        this.lengths = lengths;
        this.planner = planner;
        this.cv = cv;
        this.nameField = nameField;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        planner.clear();
        if (prepareData() && planner.isReady()) {
            List<Segment> sol = planner.getOptimalSolution();
            cv.showSegments(sol, nameField.getText());
            cv.repaint();
        }
    }

    private boolean prepareData() {
        List<Integer> submittedLengths = new ArrayList<>();

        int topLength = 0;

        for (LengthSpecification len : lengths) {
            if (len.active && len.length > 0 && !submittedLengths.contains(len.length)) {
                submittedLengths.add(len.length);
                planner.addLength(len.length);

                topLength = Math.max(topLength, len.length);
            }
        }

        Map<Integer, Integer> summarizedCuts = new HashMap<>();

        for (CutSpecification cut : cuts) {
            if (cut.length > topLength) {
                JOptionPane.showMessageDialog(cv, "Kapet på " + cut.length +
                        " är längre än den maximalt aktiverade längden " +
                        topLength, "Varning", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (summarizedCuts.containsKey(cut.length)) {

                final int prevQty = summarizedCuts.get(cut.length);
                summarizedCuts.remove(cut.length);
                summarizedCuts.put(cut.length, prevQty + cut.quantity);

            } else if (cut.length > 0 && cut.quantity > 0) {
                summarizedCuts.put(cut.length, cut.quantity);
            }
        }

        for (Integer length : summarizedCuts.keySet()) {
            planner.addRequestedCut(length, summarizedCuts.get(length));
        }
        return true;
    }
}
