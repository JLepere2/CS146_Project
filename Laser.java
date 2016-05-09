import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Laser implements FallingObject {

	int x,y,length,width;
	
	int velocity;
	
	Timer startMoveDownTimer;
	Timer moveDownTimer;
	
	private Box parentBox;
	
	public Laser(int startTime, int velocity, int position, int maxY, Box parent) {
		
		this.x = position*30 + 9;
		this.y = 0;
		this.length = 50;
		this.width = 11;
		
		this.parentBox = parent;
		
		this.velocity = velocity;
		
		ActionListener start = new StartMoveDownTimer();
		this.startMoveDownTimer = new Timer(startTime,start);
		this.startMoveDownTimer.start();
		
		ActionListener moveDown = new MoveDownTimer();
		this.moveDownTimer = new Timer(100, moveDown);
	}
	
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		Rectangle laser = new Rectangle(x,y,width,length);
		
		g2.setColor(Color.green);
		g2.fill(laser);
		
	}

	public void stop() {
		if (startMoveDownTimer.isRunning()) {
			startMoveDownTimer.stop();
		}
	}

	public boolean startTimerIsRunning() {
		return startMoveDownTimer.isRunning();
	}

	public void stopStartTimer() {
		startMoveDownTimer.stop();
	}
	
	class MoveDownTimer implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			y += velocity;
			if (isDead()) {
				System.out.println("Dead");
			}
		}
	}
	
	class StartMoveDownTimer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			moveDownTimer.start();
			startMoveDownTimer.stop();
		}
	}
	
	public boolean isDead() {
		int boxX = parentBox.x;
		int boxY = parentBox.y;
		int boxLength = parentBox.LENGTH;
	
		if (boxX > x && boxX < x + length && boxY < y + length && boxY > y) {
			return true;
		}
		return false;
	}

	public String getName() {
		return "Laser";
	}
	
}
