package platformer2D;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import platformer2D.library.Debugger;

/**
 * The super class of all game objects.
 * 
 * @author denhanh
 *
 */
public abstract class GameObject
{
	/**
	 * The size of a tile measured in pixels.
	 */
	protected static int tileSize;

	/**
	 * The top left position of the sprite of the game object measured in pixels.
	 */
	protected double x, y;

	/**
	 * The hitbox of the game object.
	 */
	private Rectangle hitbox;

	/**
	 * Draw the contours of the hitbox. (Used for debugging.)
	 * 
	 * @param g
	 *            the graphics object of the image to be drawn on
	 */
	public void drawHitbox(Graphics2D g)
	{
		Debugger.drawHitbox(hitbox, g);
	}

	/**
	 * Draw the game object.
	 * 
	 * @param g
	 *            the graphics object of the image to be drawn on
	 */
	public abstract void draw(Graphics2D g);

	/**
	 * @return the y coordinate of the top of the hitbox measured in pixels
	 */
	public int getHbTopY()
	{
		return hitbox.y;
	}

	/**
	 * @return the y coordinate of the bottom of the hitbox measured in pixels
	 */
	public int getHbBottomY()
	{
		return hitbox.y + hitbox.height;
	}

	/**
	 * @return the x coordinate of the left of the hitbox measured in pixels
	 */
	public int getHbLeftX()
	{
		return hitbox.x;
	}

	/**
	 * @return the x coordinate of the right of the hitbox measured in pixels
	 */
	public int getHbRightX()
	{
		return hitbox.x + hitbox.width;
	}

	/**
	 * Creates and sets the hitbox to a Rectangle whose upper-left corner is
	 * specified as (x,y) and whose width and height are specified by the arguments
	 * of the same name.
	 * 
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 * @param width
	 *            the width of the hitbox
	 * @param height
	 *            the height of the hitbox
	 */
	protected void setHitbox(int x, int y, int width, int height)
	{
		hitbox = new Rectangle(x, y, width, height);
	}

	/**
	 * Sets the size of the hitbox to the specified width and height.
	 * 
	 * @param width
	 *            the new width for the hitbox
	 * @param height
	 *            the new height for the hitbox
	 */
	protected void resizeHb(int width, int height)
	{
		hitbox.setSize(width, height);
	}

	/**
	 * 
	 * @return A copy of the hitbox.
	 */
	public Rectangle getHitbox()
	{
		return new Rectangle(hitbox);
	}

	/**
	 * 
	 * @return the hitbox width
	 */
	public int getHbWidth()
	{
		return hitbox.width;
	}

	/**
	 * 
	 * @return the hitbox height
	 */
	public int getHbHeight()
	{
		return hitbox.height;
	}

	/**
	 * Moves the hitbox to the specified location.
	 * 
	 * @param x
	 *            the X coordinate of the new location
	 * @param y
	 *            the Y coordinate of the new location
	 */
	protected void setHbLocation(int x, int y)
	{
		hitbox.setLocation(x, y);
	}

	/**
	 * Determines whether or not the specified hitbox collides with the hitbox of
	 * this object. Two hitboxes collides if their intersection is nonempty.
	 * 
	 * @param hb
	 *            the specified hitbox
	 * @return true if hb collides with the hitbox of this object; false otherwise
	 */
	public boolean collides(Rectangle hb)
	{
		return hb.intersects(hitbox);
	}

	/**
	 * Checks whether or not the hitbox of this object entirely contains the
	 * specified hitbox.
	 * 
	 * @param hb
	 *            the specified hitbox
	 * @return true if hb is contained entirely inside the hitbox of this object;
	 *         false otherwise
	 */
	public boolean contains(Rectangle hb)
	{
		return hitbox.contains(hb);
	}

	/**
	 * Checks whether or not the hitbox of this object is entirely contained by the
	 * specified hitbox.
	 * 
	 * @param hb
	 *            the specified hitbox
	 * @return true if this hitbox is entirely inside hb (the hitbox of the other
	 *         game object); false otherwise
	 */
	public boolean inside(Rectangle hb)
	{
		return hb.contains(hitbox);
	}

	/**
	 * Checks whether or not the hitbox of this object is on top of the specified hitbox.
	 * 
	 * @param hb the specified hitbox
	 * @return true if this hitbox is on top of hb; false otherwise
	 */
	public boolean onTopOf(Rectangle hb)
	{
		// check if this hitbox would have landed on hb if it were to move one pixel down
		Rectangle movingDownHitbox = new Rectangle(hitbox.x, hitbox.y, hitbox.width, hitbox.height + 1);
		
		return movingDownHitbox.intersects(hb) && !collides(hb);
	}
	
	/**
	 * Sets the size of the tiles
	 * 
	 * @param tileSize
	 *            the size of a tile measured in pixels
	 */
	public static void setTileSize(int tileSize)
	{
		GameObject.tileSize = tileSize;
	}
}
