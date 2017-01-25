

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/* Method: init() */
	/** Sets up the Breakout program. */
	public void init() {
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		addMouseListeners();
		
		for (int i = NBRICK_ROWS; i > 0; i--) {
			
			for(int j = NBRICKS_PER_ROW; j > 0; j--) {
				
				GRect brick = new GRect(0, 0, BRICK_WIDTH, BRICK_HEIGHT);
				/*x coordinate is the centered x-coordinate if we treat 
				 * each layer as 1 huge rectangle (hence, i*BRICK_WIDTH)
				 * but we add pixels according to the position of the brick
				 * relative to the layer (j-1)*BRICK_WIDTH
				 */
				/*y coordinate is the distance of each layer from the bottom
				 * hence (BRICKS_IN_BASE-i)*BRICK_HEIGHT, subtracted from
				 * the height of the screen
				 * an extra BRICK_HEIGHT was subtracted to get the bottom 
				 * layer to show up
				 */
				brick.setLocation((APPLICATION_WIDTH-NBRICKS_PER_ROW*BRICK_WIDTH-(NBRICKS_PER_ROW - 1)*BRICK_SEP)/2 + (j-1)*(BRICK_WIDTH + BRICK_SEP), (BRICK_Y_OFFSET + (i-1)*BRICK_HEIGHT+(i-1)*BRICK_SEP));
				brick.setFilled(true);
				if (i > 8) 
					brick.setColor(Color.CYAN);
				else if (i > 6)
					brick.setColor(Color.GREEN);
				else if(i > 4)
					brick.setColor(Color.YELLOW);
				else if (i > 2)
					brick.setColor(Color.ORANGE);
				else
					brick.setColor(Color.RED);
				add(brick);
				
			}
			
		}
		
		paddle = new GRect((APPLICATION_WIDTH - PADDLE_WIDTH)/2, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		
		ball = new GOval (APPLICATION_WIDTH/2 - BALL_RADIUS, APPLICATION_HEIGHT/2 - BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		while (turns > 0 && counter > 0) {
			ball.move(vx, vy);
			if((ball.getX() + BALL_RADIUS*2) > APPLICATION_WIDTH || ball.getX() < 0)
				vx = -vx;
			if(ball.getY() < 0)
				vy = -vy;
			if((ball.getY() + BALL_RADIUS*2) > APPLICATION_HEIGHT) {
				turns--;
				ball.setLocation(APPLICATION_WIDTH/2 - BALL_RADIUS, APPLICATION_HEIGHT/2 - BALL_RADIUS);
				if (turns != 0) {
					GLabel wait = new GLabel ("Click to serve ball.");
					add(wait, 160, 400);
					waitForClick();
					remove(wait);
					vx = rgen.nextDouble(1.0, 3.0);
					if (rgen.nextBoolean(0.5)) vx = -vx;
					vy = 3;
					paddle_counter = 0;
				}
			}
				
			checkForCollision();
			pause(20);
		}
		removeAll();
		
		GLabel end = new GLabel ("You scored " + (100 - counter) + "!");
		add(end, 100, 100);

	}
	
	public void checkForCollision() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy=-vy;
			bounceClip.play();
			paddle_counter++;
			if (paddle_counter == 7)
				vy *= 2;
			if ((ball.getX() > paddle.getX() && (ball.getX() + 2*BALL_RADIUS) < (paddle.getX() + (0.5)*PADDLE_WIDTH))) {
				vx -= 3;
			}
				
			if ((ball.getX() + 2*BALL_RADIUS) < (paddle.getX() + PADDLE_WIDTH) && ball.getX() > (paddle.getX() + (0.5)*PADDLE_WIDTH)) {
				vx += 3;
			}
				
				
		}
		else if (collider != null) {
			vy=-vy;
			bounceClip.play();
			remove(collider);
			counter--;
		}
			
	}
	
	public GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null)
			return getElementAt(ball.getX(), ball.getY());
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null)
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		if (getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null)
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null)
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		return null;
		
	}
	
	public void mousePressed(MouseEvent e) {
		last = new GPoint (e.getPoint());
		gobj = getElementAt (last);
	}
	
	public void mouseDragged (MouseEvent e) {
		if (gobj != null) {
			gobj.move(e.getX() - last.getX(), 0);
			if (gobj.getX() < 0)
				gobj.setLocation(0, gobj.getY());
			if ((gobj.getX()+PADDLE_WIDTH) > APPLICATION_WIDTH)
				gobj.setLocation(APPLICATION_WIDTH - PADDLE_WIDTH, gobj.getY());
			last = new GPoint (e.getPoint());
		}
	}
	
	private GObject gobj;
	private GPoint last;
	private double vx, vy = 3;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int turns = NTURNS;
	private GOval ball;
    private GRect paddle;
    private int counter = 100;
    private int paddle_counter = 0;
    AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");


}
