package se.jonefors.chopchop;

import se.jonefors.chopchop.model.CutPlanner;
import se.jonefors.chopchop.view.MainWindow;

import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(CutPlanner.class.getName());

    public static void main(String[] args) {

        new MainWindow();
    }
}
