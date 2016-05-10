import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Level {

	int level;
	
	public Level(int level) {
		this.level = level;
	}
	
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial Bold", Font.BOLD, 50));
		g2.drawString("Level " + level, 75, 100);
	}
	
}
