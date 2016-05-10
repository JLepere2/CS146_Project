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
		this.x = 600;
		this.y = position * 30 + 20;
		this.length = 10;
		this.width = 50;
		
		this.parentBox = parent;
		
		this.velocity = velocity;
		
		ActionListener start = new StartMoveDownTimer();
		this.startMoveDownTimer = new Timer(startTime,start);
		this.startMoveDownTimer.start();
		
		ActionListener moveDown = new MoveDownTimer();
		this.moveDownTimer = new Timer(100, moveDown);
	}
	
	class MoveDownTimer implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			x -= velocity;
			if (isDead()) {
				System.out.println("Dead");
				parentBox.dead();
			}
		}
	}
	
	class StartMoveDownTimer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			moveDownTimer.start();
			startMoveDownTimer.stop();
		}
	}
	
	public void draw(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		Rectangle icicle = new Rectangle(x,y,width,length);
		
		g2.setColor(Color.green);
		g2.fill(icicle);
		
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

	public String getName() {
		return "Icicle";
	}

	public boolean isDead() {
		int boxX = parentBox.x;
		int boxY = parentBox.y;
		int boxLength = parentBox.LENGTH;
	
		if ((boxY == y) && boxX < x + width && boxX > x) {
			return true;
		}
		return false;
	}
	
	public void stopAndRemove() {
		startMoveDownTimer.stop();
		moveDownTimer.stop();
	}
	
}
