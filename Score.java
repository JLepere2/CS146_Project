import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class Score extends JComponent {
	int x,y,score;
	
	public Score(int x, int y) {
		this.x = x;
		this.y = y;
		this.score = 0;
	}
	
	public void incrementScore(int scoreIncrement) {
		this.score += scoreIncrement;
	}
	
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial Bold", Font.BOLD, 12));
		g2.drawString("Score: " + score, x, y);
		
	}
}
