import java.awt.Graphics;

public interface FallingObject {
	//Timer startMoveDownTimer;
	//Timer moveDownTimer;
	
	//int pos;
	
	public void draw(Graphics g);
	public void stop();
	public boolean startTimerIsRunning();
	public void stopStartTimer(); 
	
	public String getName();
}
