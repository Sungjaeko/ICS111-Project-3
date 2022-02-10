//Written by Reid Ueunten
public class Boss {

	static EZImage boss;
	double x = 0; // X position of the Boss
	double y = 0; // Y position of the Boss
	int spriteWidth; // Width of each sprite
	int spriteHeight; // Height of each sprite
	int walkSequence; // count how long until displaying next sprite
	int timer; // Used to put a delay between actions
	int direction; // Used for the direction the sprite is facing
	double destX, destY; // The current destination of where the Boss is moving to
	boolean NEW = true; // Used to tell if the game has just started/restarted
	boolean GTX = false; // To tell if the destX is greater than the boss's current x position
	boolean GTY = false; // To tell if the destY is greater than the boss's current y position

	boolean rageState = false; // To tell if the Boss is in 'rage' mode or not
	static final int MOVE = 1;
	static final int ATPOINT = 2;
	static final int RAGE = 3;

	int bossState = ATPOINT;
	
	// Constructor
	Boss(String imgFile, int startX, int startY, int width, int height, int dir, double scale) {
		boss = EZ.addImage(imgFile, startX, startY);
		x = startX;
		y = startY;
		spriteWidth = width;
		spriteHeight = height;
		direction = dir;
		boss.scaleTo(scale);
		boss.pushBackOneLayer();
	}
	
	public void setImagePosition() {

		// Move the entire sprite sheet
		boss.translateTo(x, y);

		// Show only a portion of the sprite sheet.
		// Portion is determined by setFocus which takes 4 parameters:
		// The 1st two numbers is the top left hand corner of the focus region.
		// The 2nd two numbers is the bottom right hand corner of the focus region.
		boss.setFocus(walkSequence * spriteWidth, direction, walkSequence * spriteWidth + spriteWidth,
				direction + spriteHeight);
	}
	

	public void move(double pt1X, double pt1Y, double pt2X, double pt2Y, double s) { // function for moving the Boss
		switch (bossState) { // This switch statement is used to switch the Boss between 3 states (MOVE, ATPOINT, RAGE)

		case MOVE: /* Case MOVE tells the boss to move towards the destX and destY
					  checking to see if either GTX or GTY is true or false. Whether those two booleans are true or false is what
					  decides the type of check the Boss will use. Ex. if GTX is true, then the boss is looking for if it passed and is now greater than the destination.
					  Once the boss has reached the destination then it will switch the case to ATPOINT. Unless rageState == true, in which that case it switches to RAGE. 
					*/
			double xDistance = destX - x;
			double yDistance = destY - y;
			double rotationAngle = Math.toDegrees(Math.atan2(yDistance, xDistance));
			double angle = rotationAngle;
			double angleInRadians = Math.toRadians(angle); 
			x += s * Math.cos(angleInRadians); 
			y += s * Math.sin(angleInRadians); 
			timer++;
			setImagePosition();
			if (timer > 20) {
				walkSequence++;
				if (walkSequence > 2) {
					walkSequence = 0;
				}
				timer = 0;
			}
			if (GTX == true && GTY == true) {
				if (x >= destX && y >= destY) {
					if (rageState == true) {
						bossState = RAGE;
					} else {
						bossState = ATPOINT;
					}
				}
			} else if (GTX == true && GTY == false) {
				if (x >= destX && y <= destY) {
					if (rageState == true) {
						bossState = RAGE;
					} else {
						bossState = ATPOINT;
					}
				}
			} else if (GTX == false && GTY == true) {
				if (x <= destX && y >= destY) {
					if (rageState == true) {
						bossState = RAGE;
					} else {
						bossState = ATPOINT;
					}
				}
			} else if (GTX == false && GTY == false) {
				if (x <= destX && y <= destY) {
					if (rageState == true) {
						bossState = RAGE;
					} else {
						bossState = ATPOINT;
					}
				}
			}
			break;
			
		case ATPOINT:// Case ATPOINT is used to switch between the pair of points given as (pt1X, pt1Y) and (pt2X, pt2Y)
					 // and set the opposite one as the destination and then switch back to case MOVE. If rageState == true, then set the destination to the middle of the screen
			if (rageState == true) {
				destX = 1280 / 2;
				destY = 720 / 2;
			} else {
				if (NEW == true) {
					destX = pt1X;
					destY = pt1Y;
				}
				if (destX != pt1X) {
					destX = pt1X;
				} else if (destX != pt2X) {
					destX = pt2X;
				}
				if (destY != pt2Y) {
					destY = pt2Y;
				} else if (destY != pt1Y) {
					destY = pt1Y;
				}
			}
			if (destX >= x) {
				GTX = true;
			} else {
				GTX = false;
			}
			if (destY >= y) {
				GTY = true;
			} else {
				GTY = false;
			}
			NEW = false;
			bossState = MOVE;
			break;

		case RAGE: // Case RAGE is used to tell the Boss that it should stop whatever it's doing and move to the center of the screen by using the ATPOINT and MOVE cases
				   // Once the Boss is at the center of the screen then it should now begin to rapidly change between sprites to give it a 'enraged' appearance
			if (rageState == false) {
				rageState = true;
				bossState = ATPOINT;
			} else {
				timer++;
				setImagePosition();
				if (timer > 10) {
					walkSequence++;
					if (walkSequence > 2) {
						walkSequence = 0;
					}
					timer = 0;
				}
			}
			break;

		}
	}
	
	public void bossPhase(int state) { // Used to change between case states at will
		bossState = state;
	}
	
	public void translateTo(double x, double y) { // Used to move the boss to a point taking an X and Y coordinate
		boss.translateTo(x, y);
		this.x = x;
		this.y = y;
	}

	public static int getX() { // Used to get the X position of the Boss
		return boss.getXCenter();
	}

	public static int getY() { // Used to get the Y position of the Boss
		return boss.getYCenter();
	}

	public void reset() { // Used to reset the booleans to their original state
		rageState = false;
		NEW = true;
	}

	public int getBossState() { // Used to get what state the boss is in, returns a int between 1 and 3
		return bossState;
	}

	public void pullToFront() { // Used to pull the Boss to the front of the screen
		boss.pullToFront();
	}

	public Boolean isTouching(int obx, int oby) { // Used to check if an object is touching the Boss
		if (boss.isPointInElement(obx, oby) == true) {
			return true;
		} else
			return false;
	}
}