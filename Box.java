import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * This program simulates a box that moves with the arrow keys
 */
public class Box extends JPanel implements ActionListener, KeyListener
{
	private static final int BOX_VELOCITY = 10;
	public static final int LENGTH = 10;
	private static final int MAX_X = 590;
	private static final int MIN_X = 0;
	private static final int MIN_Y = 0;
	private static final int MAX_Y = 350;
	private static final int TOTAL_BLOCK_POSITIONS = 20;
	
	public int[] maxYatPos;
	public int[][] maxXatHeight;
	public int[][] minXatHeight;
	
    int x,y;
    int blockVelocity;
    int jumpVelocity;
    int fallVelocity;
    
    ArrayList<FallingObject> blocks;
    
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
    
    int level;
    
    Timer levelTimer;
	ArrayList<Level> levelHolder;
	
	Random gen;
	
	String status;
    
    /**
     * Creates the box with user inputed x and y top left coordinates
     */
    public Box()
    {   
    	
    	status = "Alive";
    	
    	blockVelocity = 10;
    	
    	gen = new Random();
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
        
        blocks = new ArrayList<FallingObject>();
        //generateBlocks();
        //resetMovementSpecs();
        
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
    	
    	int level = 1;
    	
    	LevelAction levelAction = new LevelAction();
    	levelTimer = new Timer(2000, levelAction);
    	levelHolder = new ArrayList<>();
    	levelHolder.add(new Level(level));
    	levelTimer.start();
    	
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
		
    	for (int i = 0; i < levelHolder.size(); i ++) {
    		levelHolder.get(i).draw(g2);
    	}
    	
		for (int i = 0; i < blocks.size(); i ++) {
			//Block b = (Block) blocks.get(i);
			//b.draw(g2);
			blocks.get(i).draw(g2);
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
        this.maxXatHeight = new int[12][20];
        this.minXatHeight = new int[12][20];
        for (int i = 0; i < maxYatPos.length; i ++) {
        	maxYatPos[i] = 360;
        	for (int j = 0; j < 12; j ++) {
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
			x -= BOX_VELOCITY;
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
		if(x + BOX_VELOCITY < currentMaxX) {
			x += BOX_VELOCITY;
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
			int pos = gen.nextInt(20);
			int currentStackSize = totalPerPos[pos];
			if (currentStackSize < 12) {
				Block b = new Block(startTime,blockVelocity,pos, 330 - (currentStackSize * 30), this);
				this.add(b);
				blocks.add(b);
				int randomDelay = gen.nextInt(800);				
				startTime += randomDelay + 200;
				totalPerPos[pos] = currentStackSize + 1;
				int badNumber = gen.nextInt(100);
				if (badNumber < 20) {
					int newPos = gen.nextInt(20);
					blocks.add(new Laser(startTime,blockVelocity*2,newPos,0,this));
					randomDelay = gen.nextInt(800);				
					startTime += randomDelay + 200;
					
					newPos = gen.nextInt(12);
					blocks.add(new Icicle(startTime,blockVelocity*2,newPos,0,this));
					randomDelay = gen.nextInt(800);				
					startTime += randomDelay + 200;
				}
			}
		}
		
		this.canJump = true;
		this.canMove = true;
	}
	
	public boolean isFull(int[] arr) {
		
		for (int i = 0; i < arr.length; i ++) {
			int total = arr[i];
			if (total < 12) {
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
				FallingObject b = blocks.get(i);
				if (b.startTimerIsRunning() || b.getName() != "Block") {
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
				if (status != "Dead") {
					popBlocks();
				}
			}
		}
	}
	
	public void popBlocks() {
		for (int i = 0; i < blocks.size(); i ++) {
			Block b = (Block) blocks.get(i);
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
				System.out.println(level);
				if (level == 0) {
					level = 2;
				} else {
					 level ++;
				}
				System.out.println(level);
				levelHolder.add(new Level(level));
				levelTimer.start();
				currentStackPos = 0;
				currentIndex = 0;
			}
		}
	}
	
	public void nextLevel() {
		blockVelocity += 5;
		pops.clear();
		resetMovementSpecs();
		generateBlocks();
		canMove = true;
		canJump = true;
		scoreTimer.start();
		
		int newY = gen.nextInt(100) + 50;
		int newX = gen.nextInt(500) + 50;
		key.x = newX;
		key.y = newY;
		
	}
	
	class LevelAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			levelTimer.stop();
			levelHolder.clear();
			nextLevel();
		}
		
	}
	
	public void dead() {
		status = "Dead";
		moveBoxToCenter();
		blocks.clear();
		levelTimer.stop();
		levelHolder.clear();
		scoreTimer.stop();
	}
}