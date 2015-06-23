package se.jonefors.chopchop;

import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(CutPlanner.class.getName());

    public static void main(String[] args) {
        CutPlanner p = new CutPlanner();

        p.addLength(6000);
        p.addLength(12000);

        p.addRequestedCut(1500, 3);
        p.addRequestedCut(1800, 5);
        p.addRequestedCut(2000, 2);

        List<Segment> sol = p.getOptimalSolution();

        System.out.println("Got solution");
        for (Segment s : sol) {
            System.out.println(s);
        }
    }
}
