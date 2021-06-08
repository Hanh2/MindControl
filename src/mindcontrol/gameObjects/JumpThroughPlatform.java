package mindcontrol.gameObjects;

import java.awt.Rectangle;

public abstract class JumpThroughPlatform
{
	private JumpThroughPlatform(){}

	/**
	 * Returns true if the moving game object with hitbox hb lands on the platform
	 * from above.
	 * 
	 * @param x
	 *            the x coordinate of the jump through platform
	 * @param y
	 *            the y coordinate of the jump through platform
	 * @param width
	 *            the width of the jump through platform
	 * @param hb
	 *            hitbox of the moving game object
	 * @param dy
	 *            the speed of the moving game object
	 * @return
	 */
	public static boolean landsOn(int x, int y, int width, Rectangle hb, int dy)
	{
		// return false if moving up
		if (dy < 0)
			return false;

		// update hitbox location
		Rectangle hitbox = new Rectangle(x, y, width, 1);

		// create the rectangle below hb
		Rectangle boxBelowHb = new Rectangle(hb.x, hb.y + hb.height, hb.width, dy + 1);

		return hitbox.intersects(boxBelowHb);
	}
}
