 import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
//Written by Reid Ueunten, Joshua Ishimaru, Sung Jae Ko
public class Main {

	//Stores sound files into EZSound
	static EZSound bossFightMusic;
	static EZSound bossHit;
	static EZSound playerHit;
	static EZSound playerBullet;
	static EZSound win;
	//Stores text into EZText
	static EZText gameText;
	static EZText restart;
	//Size of the screen
	static int screenSizeX = 1280; 	// x-axis = range of 0-1280
	static int screenSizeY = 720;	// y-axis = range of 0-720
	//This will be the character's and bosses life count and life bar
	static int bossLife; 
	static int charLife;
	static EZRectangle bossLifeBar;
	static EZRectangle charLifeBar;
	//Boss and Player object
	static Boss Boss;
	static Player Player;
	//Arrays that store the images of floor and poison in EZImage
	static EZImage floor[] = new EZImage[858]; //858 tiles on the screen
	static EZImage poison[] = new EZImage[108]; //108 tiles on the screen
	//Stores chest into EZImage
	static EZImage chest;
	//Determines if game is over
	static boolean gameOver = false;

	public static void setup() { 
		
		EZ.initialize(screenSizeX, screenSizeY); //Setup EZ graphics system.
		EZ.setBackgroundColor(new Color(211, 211, 211)); //Set background color to gray
		Boss = new Boss("Boss_sprite.png", screenSizeX / 2, screenSizeY / 2, 18, 18, 0, 8.0); //Creates new Boss object known as Boss
		Player = new Player("char_sprites_2.0.png", screenSizeX / 2, (screenSizeY / 2) - 200, 49, 50, 8); //Creates new Player object known as test
		chest = EZ.addImage("chest.png", 2000, 2000); //Adds chest image to x = 2000, y = 2000
		//Text
		gameText = EZ.addText(2000, 2000, "GAME OVER", Color.RED, 100); //Add text to x = 2000, y = 2000, message = "GAME OVER", color set to red, font size of 100 
		restart = EZ.addText(2000, 2000, "PRESS K TO TRY AGAIN", Color.BLACK, 100); //Add text to x = 2000, y = 2000, message of "PRESS K TO TRY AGAIN", color set to black, font size of 100
		gameText.pullToFront(); //pulls gameText message to the front of the screen
		restart.pullToFront(); //pulls restart message to the front of the screen
		//Sound files
		bossFightMusic = EZ.addSound("Bossfight_BRM.wav"); //Stores Bossfight_BRM.wav to bossFightMusic
		bossHit = EZ.addSound("boss_hit.wav"); //Stores boss_hit.wav to bossHit
		playerHit = EZ.addSound("player_hit.wav"); //Stores player_hit.wav to playerHit
		playerBullet = EZ.addSound("player_bullet.wav"); //Stores player_bullet.wav to playerBullet
		win = EZ.addSound("win_sound.wav"); //Stores win_sound.wav to win
		//Life bar
		EZRectangle lifeCharOutline = EZ.addRectangle(screenSizeX / 2, 700, 352, 27, Color.white, false); //Creates the outline of the life bar for Character
		chest.scaleTo(5); //the size of the image for chest is scaled to 5
		charLife = 350; //charLife(character's life) is set to 350
		charLifeBar = EZ.addRectangle(screenSizeX / 2, 700, charLife, 25, Color.green, true); //Creates the life bar for Character
		bossLife = 2000; //bossLife (bosses life) is set to 2000
		EZRectangle lifeBossOutline = EZ.addRectangle(screenSizeX / 2, 20, (bossLife / 2) + 2, 27, Color.white, false); //Creates the outline of the life bar for Boss
		bossLifeBar = EZ.addRectangle(screenSizeX / 2, 20, bossLife / 2, 25, Color.red, true); //Creates the life bar for Boss
		//Pulls the character(test) and Boss object to the front of the screen
		Player.pullToFront();
		Boss.pullToFront();

	}

	static void boundaries() {
		int playerPosY = Player.getY();
		int playerPosX = Player.getX();
		double playerWidth = 49 / 2; // double playerWidth keeps track of the sides of the player
		double playerHeight = 50 / 2; // double playerHeight keeps track of the top and bottom of the player
		// keeps the player inbounds on the right and left sides of the screen
		if (playerPosX + playerWidth >= screenSizeX) {
			Player.translateTo(screenSizeX - playerWidth, playerPosY);
		} else if (playerPosX - playerWidth <= 0) {
			Player.translateTo(0 + playerWidth, playerPosY);
		}
		// keeps the player inbounds on the top and bottom of the screen
		if (playerPosY + playerHeight >= screenSizeY) {
			Player.translateTo(playerPosX, screenSizeY - playerHeight);
		} else if (playerPosY - playerHeight <= 0) {
			Player.translateTo(playerPosX, 0 + playerHeight);
		}
		// the next 4 if statements check for the corners of the screen
		if (playerPosY - playerHeight <= 0 && playerPosX + playerWidth >= screenSizeX) {
			Player.translateTo(screenSizeX - playerWidth, 0 + playerHeight);
		}
		if (playerPosY - playerHeight <= 0 && playerPosX - playerWidth <= 0) {
			Player.translateTo(0 + playerWidth, 0 + playerHeight);
		}
		if (playerPosY + playerHeight >= screenSizeY && playerPosX - playerWidth <= 0) {
			Player.translateTo(0 + playerWidth, screenSizeY - playerHeight);
		}
		if (playerPosY + playerHeight >= screenSizeY && playerPosX + playerWidth >= screenSizeX) {
			Player.translateTo(screenSizeX - playerWidth, screenSizeY - playerHeight);
		}

	}

	@SuppressWarnings("finally")
	public static void main(String[] args) throws IOException { //throws IOException is used to pass off responsibility for error checking

		setup();

		try (FileReader fr = new FileReader("level.txt")) { //Make a file reader object fr
			Scanner fileScanner = new Scanner(fr); //Make a file scanner object fileScanner
			
			int width = fileScanner.nextInt(); //Reads next integer
			int height = fileScanner.nextInt(); //Reads next integer
			String inputText = fileScanner.nextLine(); //Reads next string

			int t = 0;
			int p = 0;
			for (int row = 0; row < height; row++) {
				inputText = fileScanner.nextLine(); // Read a whole line of text

				for (int column = 0; column < width; column++) {
					char ch = inputText.charAt(column); // Retrieve the character at position "column" of the inputText.
					if (ch == 'F') { //if ch == character F then places the "Floor.png"
						floor[t] = EZ.addImage("Floor.png", column * 32, row * 32); 
						floor[t].scaleTo(7);  //floor image is scaled to 7
						floor[t].pushToBack(); //Pushes the image to the back of the screen
						t++; 
					}
					if (ch == 'P') { //if ch == character P then places the "Poison.png" 
						poison[p] = EZ.addImage("Poison.png", column * 32, row * 32);
						poison[p].scaleTo(7); //poison image is scaled to 7
						poison[p].pushToBack(); //Pushes the image to the back of the screen
						p++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			//File not found or no lines to read
		} finally { //Execute rest of the code
			//Creating array list for player bullet and boss bullets
			ArrayList<Bullet> bulletList = new ArrayList<Bullet>(); 
			ArrayList<Bullet> bulletList2 = new ArrayList<Bullet>();
			ArrayList<Bullet> bulletList3 = new ArrayList<Bullet>();
			//Creating variables for the fire rates of player and boss
			int firerate = 0;
			int firerateB = 0;
			int firerateB2 = 0;
			//Creating points for the boss to move in during different phase states
			int pt1x = 200;
			int pt1y = 200;
			int pt2x = 1100;
			int pt2y = 640;
			//Creating variables of 0 degrees as j and 180 degrees as k
			//Used in bulletList3
			int j = 0; // stores the angle for the forward facing projectiles in bulletList 3
			int k = 180; // stores the angle for the backwards facing projectiles in bulletList 3
			double speed = 2; // stores the speed of the player
			boolean rage = false; // Tells if the Boss has entered rage phase
			bossFightMusic.play(); 
			
			
			while (gameOver == false) {
				
				Player.go(); // Makes the player move
				boundaries(); // function that makes sure the player stays on the screen
				
				// if the player holds or clicks the left mouse button start shooting
				if (EZInteraction.isMouseLeftButtonDown() || EZInteraction.wasMouseLeftButtonPressed()) {
					firerate++;
					if (firerate == 10 && EZInteraction.isMouseLeftButtonDown()) { //while left mouse is held shoot a bullet everytime firerate == 10
						bulletList.add(new Bullet("Bullet1.png", Player.getX(), Player.getY(), 1));
						playerBullet.play();
						firerate = 0;
					} else if (EZInteraction.wasMouseLeftButtonPressed()) { // or shoot if left mouse is just clicked
						bulletList.add(new Bullet("Bullet1.png", Player.getX(), Player.getY(), 1));
						playerBullet.play();
					}
				}

				if (EZInteraction.wasMouseLeftButtonReleased()) { // reset firerate when left mouse button is released
					firerate = 0;
				}
				
				
				// Boss Phase 1, while the boss is between 2000 and 1300 health
				if (bossLife <= 2000 && bossLife > 1300 && charLife > 0) { 
					/* During this phase the boss will walk across the screen in a diagonal pattern
					 * his attack pattern will be a three projectile shotgun the direction of the player											
					 */
					Boss.move(pt1x, pt1y, pt2x, pt2y, speed);
					firerateB++;
					if (firerateB == 30) {
						bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
						bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
						bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
						firerateB = 0;
					}
				  // Boss Phase 2, while the boss is between 1300 and 600 health
				} else if (bossLife <= 1300 && bossLife > 600 && charLife > 0) {
					/* While the boss is in this state he will increase his movement speed
					 * and move to the top of the screen, he will keep the same projectile shot pattern from Phase 1
					 */
					pt1x = 200; // New points for the boss to move between
					pt1y = 150;
					pt2x = 1000;
					pt2y = 150;
					speed = 3.2; // Set boss speed to 3.2
					Boss.move(pt1x, pt1y, pt2x, pt2y, speed);
					firerateB++;
					if (firerateB == 30) {
						bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
						bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
						bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
						firerateB = 0;
					}
				  // Boss Phase 3 aka Rage Phase, while the boss is between 600 and 0 health	
				} else if (bossLife <= 600 && charLife > 0) {
					/* First the boss will stop shooting and move to the center of the screen
					 * once at the center of the screen the boss will then begin to shoot out a hail of yellow star projectiles in a rotating spiral pattern around the boss
					 * while also maintaining his normal shot pattern form the previous phases, the boss will continue this behavior until bossLife == 0;
					 */
					
					if (rage == false) { // Move the boss to the center of the screen first
						Boss.bossPhase(3);
						rage = true;
					}
					Boss.move(pt1x, pt1y, pt2x, pt2y, speed);
					if (Boss.getBossState() == 3) { // Once boss is at center begin shooting
						firerateB++;
						firerateB2++;
						if (firerateB2 == 40) { // Yellow Star Projectiles, 3 shot infront of the boss, 3 fromt the back
							bulletList3.add(new Bullet("Boss_Projectile2.png", Boss.getX(), Boss.getY(), 5));
							bulletList3.add(new Bullet("Boss_Projectile2.png", Boss.getX(), Boss.getY(), 5));
							bulletList3.add(new Bullet("Boss_Projectile2.png", Boss.getX(), Boss.getY(), 5));
							bulletList3.add(new Bullet("Boss_Projectile2.png", Boss.getX(), Boss.getY(), 5));
							bulletList3.add(new Bullet("Boss_Projectile2.png", Boss.getX(), Boss.getY(), 5));
							bulletList3.add(new Bullet("Boss_Projectile2.png", Boss.getX(), Boss.getY(), 5));
							firerateB2 = 0;
							j += 20; //Increase the angle of both sets of the 3 projectiles
							k += 20;
						}
						if (firerateB == 30) { // Normal purple shot pattern
							bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
							bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
							bulletList2.add(new Bullet("Boss_Projectile1.png", Boss.getX(), Boss.getY(), 5));
							firerateB = 0;
						}
						if (j == 180 && k == 360) { // Once the yellow projectiles have gone 180 degrees reset
							k = 180;
							j = 0;
						}
					}
				}
				
				
				for (int i = 0; i < 108; i++) { // if the player steps on a poison tile reduce player speed
					if (poison[i].isPointInElement(Player.getX(), Player.getY())) {
						Player.setSpeed(2.0);
					}
				}
				for (int i = 0; i < 858; i++) {// if the player steps on a normal floor tile player speed is normal
					if (floor[i].isPointInElement(Player.getX(), Player.getY())) {
						Player.setSpeed(5.0);
					}
				}

				if (charLife <= 0) { // This conditional is used to check if the player has died
					// If the player is dead display the game over screen
					gameText.translateTo(screenSizeX / 2, screenSizeY / 2);
					restart.translateTo(screenSizeX / 2, screenSizeY / 2 + 200);
					if (EZInteraction.isKeyDown('k')) { // If the player presses K restart the game, this resets all the possibly changed variables to their default state
						charLife = 350;
						bossLife = 2000;
						gameText.translateTo(2000, 2000);
						restart.translateTo(2000, 2000);
						Player.translateTo(screenSizeX / 2, screenSizeY / 2);
						Boss.translateTo(screenSizeX / 2, screenSizeY / 2 + 200);
						bossLifeBar.translateTo(screenSizeX / 2, 20);
						charLifeBar.translateTo(screenSizeX / 2, 700);
						bossLifeBar.setWidth(bossLife / 2);
						charLifeBar.setWidth(charLife);
						pt1x = 200;
						pt1y = 200;
						pt2x = 1100;
						pt2y = 640;
						speed = 2;
						rage = false;
						Boss.bossPhase(2);
						Boss.reset();
					}
				}

				if (bossLife == 0) { // If the boss is killed move it off the screen and move the chest on the screen
					Boss.translateTo(2000, 2000);
					chest.translateTo(screenSizeX / 2, screenSizeY / 2);
					if (chest.isPointInElement(Player.getX(), Player.getY())) { // if the player touches the chest end the game, and display victory text and sound
						EZ.addText(screenSizeX / 2, screenSizeY / 2, "YOU WIN", Color.BLUE, 100);
						win.play();
						gameOver = true;
					}
				}
				
				//Player's bullet for bulletList
				//Loop through each bullet in the array list
				for (int i = 0; i < bulletList.size(); i++) {
					bulletList.get(i).playerShoot(); 
					bulletList.get(i).setRange(20); //Range of players bullet = 20
					Bullet eachBullet; //Object eachBullet
					eachBullet = bulletList.get(i);
					if (Boss.isTouching(bulletList.get(i).getBulletPicX(), bulletList.get(i).getBulletPicY()) == true) { //If boss is in the x and y for players bullet
						bossLife -= 25; //subtracts 5 from bossLife
						bossLifeBar.setWidth(bossLife / 2); //reduces the life bar from both sides
						bossLifeBar.translateBy(-2.5 / 2, 0); //Keeps bosses life bar in place while reducing from only the right side
						bossHit.play(); //plays the bossHit from sound file
						bulletList.get(i).finalize(); //Removes EZImage of the bullet
						bulletList.remove(eachBullet);  //Removes the object eachBullet
					}
					if (eachBullet.rangeCheck() == false) { //uses rangeCheck() from bullet class to check if bullet is out of range
						bulletList.remove(eachBullet); //Removes eachBullet
					}
				}
				//First Bullet for bulletList2
				//Bosses regular bullet pattern
				//Loop through each bullet in the array list
				for (int i = 0; i < bulletList2.size(); i += 3) { 
					bulletList2.get(i).bossShoot(30, 5); //Shotgun pattern is at 30 degrees and velocity of 5
					bulletList2.get(i).setRange(150); //Range of 150
					Bullet eachBullet;
					eachBullet = bulletList2.get(i);
					if (Player.isTouching(bulletList2.get(i).getBulletPicX(),
							bulletList2.get(i).getBulletPicY()) == true) { //If player is in the x and y for boss bullet
						charLife -= 10; //subtracts 10 from the charLife
						charLifeBar.setWidth(charLife); //reduces the life bar from both sides
						charLifeBar.translateBy(-5, 0); //Keeps characters life bar in place while reducing only from the right side
						playerHit.play(); //plays playerHit from sound file
						bulletList2.get(i).finalize(); //Removes EZImage or picture using finalize()
						bulletList2.remove(eachBullet); //Removes the object eachBullet
					}
					if (eachBullet.rangeCheck() == false) { //uses rangeCheck() from bullet class to check if bullet is out of range
						bulletList2.remove(eachBullet); //Removes eachBullet
					}
				}
				//Second Bullet for bulletList2
				for (int i = 1; i < bulletList2.size(); i += 3) {
					bulletList2.get(i).bossShoot(0, 5); //Shotgun pattern is at 0 degrees and velocity of 5
					bulletList2.get(i).setRange(150);
					Bullet eachBullet; 
					eachBullet = bulletList2.get(i);
					if (Player.isTouching(bulletList2.get(i).getBulletPicX(),
							bulletList2.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList2.get(i).finalize();
						bulletList2.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList2.remove(eachBullet);
					}	
				}
				//Third bullet for bulletlist2 
				for (int i = 2; i < bulletList2.size(); i += 3) {
					bulletList2.get(i).bossShoot(-30, 5); //Shotgun pattern is at -30 degrees and velocity of 5
					bulletList2.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList2.get(i);
					if (Player.isTouching(bulletList2.get(i).getBulletPicX(),
							bulletList2.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList2.get(i).finalize();
						bulletList2.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList2.remove(eachBullet);
					}
				}
				//First Bullet for bulletList3
				//Bosses rage phase bullet pattern
				//Loop through each bullet in the array list
				for (int i = 0; i < bulletList3.size(); i += 6) {
					bulletList3.get(i).bossShootRage(j, 40, 3); //Shooting pattern is at j + 40 degrees and velocity of 3
					bulletList3.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList3.get(i);
					if (Player.isTouching(bulletList3.get(i).getBulletPicX(),
							bulletList3.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList3.get(i).finalize();
						bulletList3.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList3.remove(eachBullet);
					}
				}
				//Second Bullet for bulletList3
				for (int i = 1; i < bulletList3.size(); i += 6) {
					bulletList3.get(i).bossShootRage(j, 0, 3); //Shotgun pattern is at j + 0 degrees and velocity of 3
					bulletList3.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList3.get(i);
					if (Player.isTouching(bulletList3.get(i).getBulletPicX(),
							bulletList3.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList3.get(i).finalize();
						bulletList3.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList3.remove(eachBullet);
					}
				}
				//Third Bullet for bulletList3
				for (int i = 2; i < bulletList3.size(); i += 6) {
					bulletList3.get(i).bossShootRage(j, -40, 3); //Shotgun pattern is at j + -40 degrees and velocity of 3
					bulletList3.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList3.get(i);
					if (Player.isTouching(bulletList3.get(i).getBulletPicX(),
							bulletList3.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList3.get(i).finalize();
						bulletList3.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList3.remove(eachBullet);
					}
				}
				//Fourth Bullet for bulletList3
				for (int i = 3; i < bulletList3.size(); i += 6) {
					bulletList3.get(i).bossShootRage(k, 40, 3); //Shotgun pattern is at k + 40 degrees and velocity of 3
					bulletList3.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList3.get(i);
					if (Player.isTouching(bulletList3.get(i).getBulletPicX(),
							bulletList3.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList3.get(i).finalize();
						bulletList3.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList3.remove(eachBullet);
					}
				}
				//Fifth Bullet for bulletList3
				for (int i = 4; i < bulletList3.size(); i += 6) {
					bulletList3.get(i).bossShootRage(k, 0, 3); //Shotgun pattern is at k + 0 degrees and velocity of 3
					bulletList3.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList3.get(i);
					if (Player.isTouching(bulletList3.get(i).getBulletPicX(),
							bulletList3.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList3.get(i).finalize();
						bulletList3.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList3.remove(eachBullet);
					}
				}
				//Second Sixth for bulletList3
				for (int i = 5; i < bulletList3.size(); i += 6) {
					bulletList3.get(i).bossShootRage(k, -40, 3); //Shotgun pattern is at k + -40 degrees and velocity of 3
					bulletList3.get(i).setRange(150);
					Bullet eachBullet;
					eachBullet = bulletList3.get(i);
					if (Player.isTouching(bulletList3.get(i).getBulletPicX(),
							bulletList3.get(i).getBulletPicY()) == true) {
						charLife -= 10;
						charLifeBar.setWidth(charLife);
						charLifeBar.translateBy(-5, 0);
						playerHit.play();
						bulletList3.get(i).finalize();
						bulletList3.remove(eachBullet);
					}
					if (eachBullet.rangeCheck() == false) {
						bulletList3.remove(eachBullet);
					}
				}
				
				EZ.refreshScreen();

			}

		}
	}
}