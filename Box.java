import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * This program simulates a box that moves with the arrow keys
 */
public class Box extends JPanel implements ActionListener, KeyListener
{
	private static final int VELOCITY = 10;
	private static final int LENGTH = 10;
	private static final int MAX_X = 590;
	private static final int MIN_X = 0;
	private static final int MIN_Y = 0;
	private static final int MAX_Y = 350;
	private static final int TOTAL_BLOCK_POSITIONS = 20;
	
	public int[] maxYatPos;
	public int[][] maxXatHeight;
	public int[][] minXatHeight;
	
    int x,y;
    int jumpVelocity;
    int fallVelocity;
    
    ArrayList<Block> blocks;
    
    Timer jumpTimer;
    Timer fallTimer;
    private boolean canMove;
    private boolean canJump;
    
    int currentPos;
    int currentMaxX;
    int currentMinX;
    
    Key key;
    
    private int delay;
    
    private ArrayList<Stack<Block>> stackOfBlocksAtPos;
    
    Timer resetBoxTimer;
    
    Timer popBoxesTimer;
    
    int numberOfBoxesToPop;
    
    ArrayList<Pop> pops;
    
    Score score;
    Timer scoreTimer;
    
    /**
     * Creates the box with user inputed x and y top left coordinates
     */
    public Box()
    {   
    	canMove = false;
    	canJump = false;
        x = MIN_X;
        y = MAX_Y;
        currentPos = 0;
        currentMaxX = 590;
        currentMinX = 0;
        addKeyListener(this);
        this.setFocusable(true);
        
        delay = 1000;
        
        jumpVelocity = 9;
        fallVelocity = 0;
        ActionListener jump = new Jump();
		this.jumpTimer = new Timer(100,jump);
		
		ActionListener fall = new Fall();
		this.fallTimer = new Timer(100, fall);
        
        blocks = new ArrayList<Block>();
        generateBlocks();
        resetMovementSpecs();
        
        key = new Key();
        
        ActionListener resetBox = new resetBoxTimer();
        this.resetBoxTimer = new Timer(100,resetBox);
        
        stackOfBlocksAtPos = new ArrayList<Stack<Block>>();
        for (int i = 0; i < 20; i ++) {
        	Stack<Block> stack = new Stack<>();
        	stackOfBlocksAtPos.add(stack);
        }
        
        currentStackPos = 0;
    	currentIndex = 0;
    	numberOfBoxesToPop = 0;
    	
    	ActionListener  popBoxes = new PopBlocks();
    	this.popBoxesTimer = new Timer(100,popBoxes);
    	
    	pops = new ArrayList<>();
    	
    	score = new Score(0,50);
    	
    	ScoreTimerAction scoreAction = new ScoreTimerAction();
    	scoreTimer = new Timer(100, scoreAction);
    	scoreTimer.start();
    }
    
    class ScoreTimerAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			score.incrementScore(10);
		}
    	
    }

    
    /**
     * Creates the box and other graphics
     */
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        Rectangle box = new Rectangle(x, y, LENGTH, LENGTH);
        g2.setColor(Color.RED);
        g2.fill(box);
        repaint();
    }
    
    
    public void paintComponent(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;
		
		for (int i = 0; i < blocks.size(); i ++) {
			Block b = blocks.get(i);
			b.draw(g2);
		}
		
		key.draw(g2);
		
		draw(g2);
		
		for (int i = 0; i < pops.size(); i ++) {
			Pop p = pops.get(i);
			p.draw(g2);
		}
		
		score.draw(g2);
    }
    
    public void resetMovementSpecs() {
    	this.maxYatPos = new int[TOTAL_BLOCK_POSITIONS];
        this.maxXatHeight = new int[13][20];
        this.minXatHeight = new int[13][20];
        for (int i = 0; i < maxYatPos.length; i ++) {
        	maxYatPos[i] = 360;
        	for (int j = 0; j < 13; j ++) {
        		maxXatHeight[j][i] = 600;
        		minXatHeight[j][i] = 0;
        	}
        }
    }

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
	public void up() {
		canJump = false;
		jumpVelocity  = 9;
		jumpTimer.start();
	}
	
	class Jump implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			int yAtCurrentPos = maxYatPos[currentPos];
			if (y < (yAtCurrentPos + jumpVelocity - 10)) {
				y -= jumpVelocity;
				jumpVelocity --;
				if (atKey()) {
					removeBlocks();
				}
			} else {
				y = yAtCurrentPos - 10;
				jumpTimer.stop();
				canJump = true;
			}
			currentMaxX = maxXatHeight[(y)/30][currentPos];
			currentMinX = minXatHeight[(y)/30][currentPos];
		}
	}
	
	public void left() {
		
		if(x != currentMinX) {
			x -= VELOCITY;
        } else {
            x = currentMinX;
        }
		currentPos = x / 30;
		currentMaxX = maxXatHeight[(y)/30][currentPos];
		currentMinX = minXatHeight[(y)/30][currentPos];
		canFall();
		if (atKey()) {
			removeBlocks();
		}
	}
	
	public void right() {
		if(x + VELOCITY < currentMaxX) {
			x += VELOCITY;
        } else {
            x = currentMaxX - 10;
        }
		currentPos = x / 30;
		currentMaxX = maxXatHeight[(y-10)/30][currentPos];
		currentMinX = minXatHeight[(y-10)/30][currentPos];
		canFall();
		if (atKey()) {
			removeBlocks();
		}
	}
	
	public void canFall() {
		if (canJump) {
			int yAtCurrentPos = maxYatPos[currentPos];
			if (y < yAtCurrentPos) {
				canJump = false;
				fallVelocity  = 0;
				fallTimer.start();
			}
		}
	}
	
	class Fall implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			int yAtCurrentPos = maxYatPos[currentPos];
			if (y < (yAtCurrentPos + fallVelocity - 10)) {
				y -= fallVelocity;
				fallVelocity --;
			} else {
				y = yAtCurrentPos - 10;
				fallTimer.stop();
				canJump = true;
			}
			currentMaxX = maxXatHeight[(y)/30][currentPos];
			currentMinX = minXatHeight[(y)/30][currentPos];
			if (atKey()) {
				removeBlocks();
			}
		}
		
	}
	
	public void keyPressed(KeyEvent e) {
		
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_UP) {
        	if (canMove && canJump) {
        		up();
        	}
        } else if (keyCode == KeyEvent.VK_LEFT) {
        	if (canMove) {
        		left();
        	}
        } else if (keyCode == KeyEvent.VK_RIGHT) {
        	if (canMove) {
        		right();
        	}
        }
		
	}


	public void keyReleased(KeyEvent e) {
		
	}


	public void actionPerformed(ActionEvent e) {
		repaint();	
	}
	
	public void generateBlocks() {
		
		int startTime = 0;
		int[] totalPerPos = new int[20];
		while (!isFull(totalPerPos)) {
			Random gen = new Random();
			int pos = gen.nextInt(20);
			int currentStackSize = totalPerPos[pos];
			if (currentStackSize < 13) {
				Block b = new Block(startTime,10,pos, 330 - (currentStackSize * 30), this);
				this.add(b);
				blocks.add(b);
				int randomDelay = gen.nextInt(800);				
				startTime += randomDelay + 200;
				totalPerPos[pos] = currentStackSize + 1;
			}
		}
		
		this.canJump = true;
		this.canMove = true;
	}
    
	
	public boolean isFull(int[] arr) {
		
		for (int i = 0; i < arr.length; i ++) {
			int total = arr[i];
			if (total < 13) {
				return false;
			}
		}
		return true;
	}
	
	public boolean atKey() {
		int keyX = key.getX();
		int keyY = key.getY();
		int keyLength = key.getLength();
		
		if (x > keyX && x < (keyX + keyLength) && y > keyY && y < (keyY + keyLength)) {
			return true;
		}
		if ((x + LENGTH) > keyX && (x + LENGTH) < (keyX + keyLength) && y > keyY && y < (keyY + keyLength)) {
			return true;
		}
		if ((x + LENGTH) > keyX && (x + LENGTH) < (keyX + keyLength) && (y + LENGTH) > keyY && (y + LENGTH) < (keyY + keyLength)) {
			return true;
		}
		if (x > keyX && x < (keyX + keyLength) && (y + LENGTH) > keyY && (y + LENGTH) < (keyY + keyLength)) {
			return true;
		}
		return false;
	}
	
	public void removeBlocks() {
		scoreTimer.stop();
		while (!didRemoveAllButStopped()) {
			for (int i = 0; i < blocks.size(); i ++) {
				Block b = blocks.get(i);
				if (b.startTimerIsRunning()) {
					b.stopStartTimer();
					blocks.remove(b);
					i--;
				} else {
					numberOfBoxesToPop++;
				}
			}
		}
		numberOfBoxesToPop = 0;
		moveBoxToCenter();
	}
	
	public boolean didRemoveAllButStopped() {
		if (blocks.size() == numberOfBoxesToPop) {
			return true;
		}
		return false;
	}
	
	public void moveBoxToCenter() {
		canMove = false;
		fallTimer.stop();
		jumpTimer.stop();
		//resetBoxTimer.start();
		resetBoxTimer.restart();
	}
	
	class resetBoxTimer implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (x != 300) {
				if (x > 290 && x < 310) {
					x = 300;
				} else if (x <= 290) {
					x += 10;
				} else {
					 x -= 10;
				}
			} else if (y != 350) {
				if (y > 340 && y < 360) {
					y = 360;
				} else if (y <= 340) {
					y += 10;
				} else {
					y -= 10;
				}

			} else {
				resetBoxTimer.stop();
				popBlocks();
			}
		}
	}
	
	public void popBlocks() {
		for (int i = 0; i < blocks.size(); i ++) {
			Block b = blocks.get(i);
			int bPos = b.pos;
			if (stackOfBlocksAtPos.get(bPos) == null) {
				Stack<Block> blocksAtPos = new Stack<>();
				stackOfBlocksAtPos.add(blocksAtPos);
			}
			stackOfBlocksAtPos.get(bPos).add(b);
		}
		popBoxesTimer.restart();
	}
	
	int currentStackPos;
	int currentIndex;
	
	class PopBlocks implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			System.out.println("pop");
			Stack<Block> blocksAtPos = stackOfBlocksAtPos.get(currentStackPos);
			if (!blocksAtPos.isEmpty()) {
				Block b = blocksAtPos.pop();
				blocks.remove(b);
				pops.add(new Pop(b.x,b.y));
				currentIndex ++;
				score.incrementScore(50);
			} else {
				currentStackPos ++;
			}
			if (currentStackPos > stackOfBlocksAtPos.size() - 1) {
				popBoxesTimer.stop();
				nextLevel();
				currentStackPos = 0;
				currentIndex = 0;
			}
		}
	}
	
	public void nextLevel() {
		pops.clear();
		resetMovementSpecs();
		generateBlocks();
		canMove = true;
		canJump = true;
		scoreTimer.start();
	}
}