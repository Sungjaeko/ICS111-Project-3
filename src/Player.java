// Written by Sung Jae Ko
public class Player {
static EZImage spriteSheet;
	
	
	static double x = 0;	// Position of Sprite
	static double y = 0;
	int spriteWidth;		// Width of each sprite
	int spriteHeight;		// Height of each sprite
	int direction = 0;		// Direction character is walking in
	int walkSequence = 0;	// Walk sequence counter
	int cycleSteps;			// Number of steps before cycling to next animation step
	int counter = 0;		// Cycle counter
	double pSpd; // Current player speed
	int timer = 0; // Used to put a delay between actions
	double cursorX = 0; // X and Y positions of the cursor
	double cursorY = 0;
	

	Player(String imgFile, int startX, int startY, int width, int height, int steps) {
		x = startX;					// position of the sprite character on the screen
		y = startY;
		spriteWidth = width;		// Width of the sprite character
		spriteHeight = height;		// Height of the sprite character
		cycleSteps = steps;			// How many pixel movement steps to move before changing the sprite graphic
		spriteSheet = EZ.addImage(imgFile, startX, startY);
		spriteSheet.pullForwardOneLayer();
		setImagePosition();
	}

	public void setImagePosition() {
		
		// Move the entire sprite sheet
		spriteSheet.translateTo(x, y);
		
		// Show only a portion of the sprite sheet.
		// Portion is determined by setFocus which takes 4 parameters:
		// The 1st two numbers is the top left hand corner of the focus region.
		// The 2nd two numbers is the bottom right hand corner of the focus region.
		spriteSheet.setFocus(walkSequence * spriteWidth, direction,
				walkSequence * spriteWidth + spriteWidth, direction + spriteHeight);
	}
	
	
	public void shooting() { // Sets the player sprite into a shooting state switching between the proper sprites
							 // Also makes sure the player sprite is always facing the cursor when shooting
		if (cursorY > y && EZInteraction.isMouseLeftButtonDown() == true) {
			timer++;
			direction = spriteHeight;
			if (timer > 10) {
				walkSequence--;
				if (walkSequence < 3)
					walkSequence = 4;
				timer = 0;
			}
		} else if (cursorY < y && EZInteraction.isMouseLeftButtonDown() == true) {
			timer++;
			direction = spriteHeight * 2;
			if (timer > 10) {
				walkSequence--;
				if (walkSequence < 3)
					walkSequence = 4;
				timer = 0;
			}
		}
	}
	
	
	public void imagePos() { // Used to set the image of the sprite, to also make sure the sprite is facing the right direction
		if (EZInteraction.isMouseLeftButtonDown() == true) {
			cursorX = EZInteraction.getXMouse();
			cursorY = EZInteraction.getYMouse();
			shooting();
		}
		else if ((counter % cycleSteps) == 0) {
			walkSequence++;
			if (walkSequence > 2)
				walkSequence = 0;
		}
	}		
			
	
	public void moveDown(double stepSize) { // Move the sprite down and update the sprite sheet
		y = y + stepSize;
		direction = spriteHeight;
		imagePos();
		counter++;
	}
	
	public void moveLeft(double stepSize) { // Move the sprite left and update the sprite sheet
		x = x - stepSize;
		direction = 0;
		imagePos();
		counter++;
	}

	public void moveRight(double stepSize) { // Move the sprite right and update the sprite sheet
		x = x + stepSize;
		direction = 0;
		imagePos();
		counter++;
	}

	public void moveUp(double stepSize) { // Move the sprite up and update the sprite sheet
		y = y - stepSize;
		direction = spriteHeight*2;
		imagePos();
		counter++;
	}


	
	public void go() { // Keyboard controls for moving the character.
		//Reduce player speed if two movement buttons are held, this is done to combat a bug with player speed doubling if two keys are held down together
		if (EZInteraction.isKeyDown('w')&&EZInteraction.isKeyDown('a')||EZInteraction.isKeyDown('w')&&EZInteraction.isKeyDown('d')
			||EZInteraction.isKeyDown('s')&&EZInteraction.isKeyDown('a')||EZInteraction.isKeyDown('s')&&EZInteraction.isKeyDown('d')) {
			pSpd = pSpd*0.75;
		}
		if (EZInteraction.isKeyDown('w')) {
			moveUp(pSpd);
		} 
		if (EZInteraction.isKeyDown('a')) {
			moveLeft(pSpd);
		}
		if (EZInteraction.isKeyDown('s')) {
			moveDown(pSpd);
		}
		if (EZInteraction.isKeyDown('d')) {
			moveRight(pSpd);
		}
		else {shooting();}
		setImagePosition();
	}
	
	public void setSpeed(double s) { // Used to set the speed of the Player
		pSpd = s;
	}
	
	public void translateTo(double newX, double newY) { //Used to set the position of the Player
		spriteSheet.translateTo(newX, newY);
		x = newX;
		y = newY;
	}
	public static int getX() {return spriteSheet.getXCenter();} // Get the X coordinate of the Player
	public static int getY() {return spriteSheet.getYCenter();} // Get the Y coordinate of the Player
	public void pullToFront() {spriteSheet.pullToFront();} // Used to pull the Player sprite to the front
	public Boolean isTouching(int obx, int oby) { // Used to check if an object is touching the player
		if (spriteSheet.isPointInElement(obx, oby) == true) {
			return true;
		} else
			return false;
	}
}