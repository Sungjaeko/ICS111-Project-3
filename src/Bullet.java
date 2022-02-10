import java.awt.event.KeyEvent;
import java.util.*;
//Written By Joshua Ishimaru
public class Bullet{ 

	double x;		
	double y;					
	EZImage picture;			// variable for our image
	int speed;					// variable for speed of bullet
	double clickX;				// variable for x coordinate of cursor when you click
	double clickY;				// variable for y coordinate of cursor when you click
	double xDistance;			// variable that holds x value difference between two objects
	double yDistance;			// variable that holds y value difference between two objects
	double angleInRadians;		// variable of angle in radians
	double rotationAngle;		// variable that holds number of initial angle before offsetting
	double angle;				// variable final angle after offset
	double range;				// variable for range of bullet
	int timer;					// variable for time interval between each bullet shot
	boolean fired = false;		// boolean whether a bullet has been shot or not

	Bullet(String filename, int posx, int posy, double scale){		// Bullet constructor that takes in the image name, x coordinate, y coordinate
		x = posx;									// Set the class' x to the position of the constructors x
		y = posy;									// Set the class' y to the position of the constructors y 
		picture = EZ.addImage(filename, posx, posy);
		picture.scaleTo(scale);						// Scales the image to a different size
		picture.pushBackOneLayer();					// Pushes back the bullet one layer
	}

	protected void finalize() {		// finalize is a special function designed to destroy your object (destructor)
		EZ.removeEZElement(picture);		// Destroy the picture of the bubble
	}

	boolean rangeCheck() {			// function to check the range of the bullet
		timer++;
		if (timer > range) {
			finalize();
			return false;
		} else return true;
	}

public void playerShoot() {			// function for the shooting mechanic of the player

		if (EZInteraction.isMouseLeftButtonDown() && fired == false) {
			clickX = EZInteraction.getXMouse();
			clickY = EZInteraction.getYMouse();
			xDistance = clickX - Player.getX();
			yDistance = clickY - Player.getY();
			rotationAngle = Math.toDegrees(Math.atan2(yDistance, xDistance));
			angle = rotationAngle;
			angleInRadians = Math.toRadians(angle);
			picture.rotateTo(angle);
			fired = true;
		}
		x += 9 * Math.cos(angleInRadians);
		y += 9 * Math.sin(angleInRadians);
		picture.translateTo(x, y);
	}

	public double setRange(double r) {		// function to set the range of the bullet
		return range = r;
	}

	public void bossShoot(double offset, double vel) {		// function for the mechanic of the boss's shooting

		if (fired == false) {

			xDistance = Player.getX() - Boss.getX();		
			yDistance = Player.getY() - Boss.getY();
			rotationAngle = Math.toDegrees(Math.atan2(yDistance, xDistance));
			angle = rotationAngle + offset;
			angleInRadians = Math.toRadians(angle);
			picture.rotateTo(angle);
			fired = true;
		}
		x += vel * Math.cos(angleInRadians);		// math for allowing bullets to travel diagonally
		y += vel * Math.sin(angleInRadians);		// math for allowing bullets to travel diagonally
		picture.translateTo(x, y);					// move the picture to the new x and y coordinate
	}

public void bossShootRage(double ang, double offset, double vel) {	// function for the last phase of the boss

		if (fired == false) {
			/*xDistance = Player.getX() - Boss.getX();
			yDistance = Player.getY() - Boss.getY();
			rotationAngle = Math.toDegrees(Math.atan2(yDistance, xDistance));*/
			angle = ang + offset;
			angleInRadians = Math.toRadians(angle);
			picture.rotateTo(angle);
			fired = true;
		}
		x += vel * Math.cos(angleInRadians);
		y += vel * Math.sin(angleInRadians);
		picture.translateTo(x, y);
	}

	public double getBulletX() {return x;}		// this function gives the x coordinate of the bullet
	public double getBulletY() {return y;}		// this function returns the y coordinate of the bullet
	public int getBulletPicX() {return picture.getXCenter();}	// this function returns the center x coordinate of the bullet
	public int getBulletPicY() {return picture.getYCenter();}	// this function returns the center y coordinate of the bullet
}