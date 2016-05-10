import java.awt.Graphics;

public interface PowerUp {

	public void draw(Graphics g);
	public boolean atPowerUp();
	public void activate();
	public boolean canDraw();
	public void hide();
}
