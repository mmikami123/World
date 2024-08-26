package core;

import edu.princeton.cs.algs4.StdDraw;

public class Main {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void main(String[] args) {
        Main mainInstance = new Main();
        mainInstance.run();
    }

    /** Initializes StdDraw settings and displays main menu */
    private void run() {
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        MenuAndMovement menu = new MenuAndMovement(WIDTH, HEIGHT);
        menu.createMenu();
    }
}
