package marlin;

import marlin.graphicsLib.Window;
import marlin.sandbox.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
//        Window.PANEL = new Squares();
//        Window.PANEL = new PaintInk();
//        Window.PANEL = new PaintInk();
//        Window.PANEL = new ShapeTrainer();
        Window.PANEL = new Music2();
        Window.launch();
    }
}
