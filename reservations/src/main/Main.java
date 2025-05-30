package main;
import gui.HotelGUI;

public class Main {
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new HotelGUI().setVisible(true));
    }

}

