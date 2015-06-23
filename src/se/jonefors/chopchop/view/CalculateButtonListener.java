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
        prepareLengths();
        prepareCuts();

        if (planner.isReady()) {
            List<Segment> sol = planner.getOptimalSolution();
            for (Segment s : sol) {
                System.out.println(s);
            }
            cv.showSegments(sol, nameField.getText());
            cv.repaint();

        }
    }

    private void prepareLengths() {
        List<Integer> submittedLengths = new ArrayList<>();

        for (LengthSpecification len : lengths) {
            if (len.active && len.length > 0 && !submittedLengths.contains(len.length)) {
                submittedLengths.add(len.length);
                planner.addLength(len.length);
            }
        }
    }

    private void prepareCuts() {
        Map<Integer, Integer> summarizedCuts = new HashMap<>();

        for (CutSpecification cut : cuts) {
            if (summarizedCuts.containsKey(cut.length)) {
                summarizedCuts.replace(cut.length, summarizedCuts.get(cut.length) + cut.quantity);
            } else if (cut.length > 0 && cut.quantity > 0) {
                summarizedCuts.put(cut.length, cut.quantity);
            }
        }

        for (Integer length : summarizedCuts.keySet()) {
            planner.addRequestedCut(length, summarizedCuts.get(length));
        }
    }
}
