package se.jonefors.chopchop;

import se.jonefors.chopchop.view.MainWindow;

import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(CutPlanner.class.getName());

    public static void main(String[] args) {

        new MainWindow();
    }
}
