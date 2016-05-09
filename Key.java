import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

public class Key extends JComponent {

	int x,y,length;
	Color keyColor;
	
	public Key() {
		this.x = 100;
		this.y = 200;
		this.length = 15;
		keyColor = Color.yellow;
	}
	
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		Rectangle key = new Rectangle(x,y,length,length);
		
		g2.setColor(keyColor);
		g2.fill(key);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getLength() {
		return length;
	}
}
