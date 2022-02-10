
public class Camera {

	//Elements
	private float x, y;
	
	//Constructor
	public Camera (float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	//Methods
	/*
	public void followChar(SpriteChar me) {
		x = me.getX();
		y = me.getY();
	}
	*/
	
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
}
