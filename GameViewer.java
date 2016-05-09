import java.awt.Color;

import javax.swing.JFrame;

public class GameViewer extends JFrame {
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setTitle("BOX GAME");
		frame.setSize(360, 382);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setBackground(Color.WHITE);
		
		Box box = new Box();
		
		frame.add(box);
		
		frame.setVisible(true);	
	}
	
    
}
