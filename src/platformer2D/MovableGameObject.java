package platformer2D;

import java.awt.Rectangle;

/**
 * The super class for all movable game objects.
 * 
 * @author denhanh
 *
 */
public abstract class MovableGameObject extends GameObject
{
	/**
	 * Reference to the background.
	 */
	protected static Background bg;
	
	// static variables
	protected static double gravity;
	protected static double maxFallSpeed;

	/**
	 * Hitbox offsets.
	 */
	protected int hbXOffset, hbYOffset;
	
	/**
	 * The width and height of the sprite measured in pixels.
	 */
	protected int sWidth, sHeight;

	/**
	 * (spawnX, spawnY) is the position where the object is spawned at the start of
	 * a level.
	 */
	private int spawnX, spawnY;

	/**
	 * The change of the x and y coordinates of the game object in one frame.
	 */
	protected double dx, dy;

	/**
	 * The absolute value dx will be set to when moving a character.
	 */
	protected double speed;

	/**
	 * Sets the object position to its spawn location and sets its momentum to (0,
	 * 0).
	 */
	public void reset()
	{
		x = spawnX;
		y = spawnY;
		dx = 0;
		dy = 0;

		setHbLocation(spawnX + hbXOffset, spawnY + hbYOffset);
	}

	/**
	 * Updates the position of the sprite.
	 */
	protected abstract void updateSpritePosition();

	/**
	 * Updates the values of dx and dy.
	 */
	protected abstract void updateMomentum();

	/**
	 * Updates the hitbox position according to the sprite position.
	 */
	protected void updateHbPosition()
	{
		setHbLocation((int) (x + hbXOffset), (int) (y + hbYOffset));
	}

	/**
	 * Updates momentum, position and hitbox position.
	 */
	public void update()
	{
		updateMomentum();
		updateSpritePosition();
		updateHbPosition();
	}

	/**
	 * Sets the background.
	 * @param bg the new background
	 */
	public static void setBackground(Background bg)
	{
		MovableGameObject.bg = bg;
	}
	
	/**
	 * Sets the gravity
	 * 
	 * @param gravity
	 *            describes how much the movable game objects are affected by
	 *            gravity
	 */
	public static void setGravity(double gravity)
	{
		MovableGameObject.gravity = gravity;
	}

	/**
	 * Sets the maximum fall speed.
	 * 
	 * @param maxFallSpeed
	 *            the maximum value of dy.
	 */
	public static void setMaxFallSpeed(double maxFallSpeed)
	{
		MovableGameObject.maxFallSpeed = maxFallSpeed;
	}

	/**
	 * Sets the offsets for the hitbox relative to the sprite.
	 * 
	 * @param hbXOffset
	 *            the x offset of the hitbox
	 * @param hbYOffset
	 *            the y offset of the hitbox
	 */
	protected void setHbOffsets(int hbXOffset, int hbYOffset)
	{
		this.hbXOffset = hbXOffset;
		this.hbYOffset = hbYOffset;
	}

	/**
	 * Creates and sets the hitbox to a Rectangle whose width and height are
	 * specified by the arguments of the same name.
	 * 
	 * The position is set to (x + hbXOffset, y + hbYOffset)
	 * 
	 * @param width
	 *            the width of the hitbox
	 * @param height
	 *            the height of the hitbox
	 */
	protected void setHitbox(int width, int height)
	{
		super.setHitbox((int) x + hbXOffset, (int) y + hbYOffset, width, height);
	}

	/**
	 * 
	 * @return A copy of the hitbox with the location updated according to the
	 *         sprite location, i.e. at ((int) x + hbXOffset, (int) y + hbYOffset)
	 */
	@Override
	public Rectangle getHitbox()
	{
		return new Rectangle((int) x + hbXOffset, (int) y + hbYOffset, getHbWidth(), getHbHeight());
	}

	/**
	 * Sets the sprite size.
	 * 
	 * @param sWidth
	 *            the width of the sprite measured in pixels
	 * @param sHeight
	 *            the height of the sprite measured in pixels
	 */
	public void setSpriteSize(int sWidth, int sHeight)
	{
		this.sWidth = sWidth;
		this.sHeight = sHeight;
	}
	
	/**
	 * Sets the location of the spawn
	 * 
	 * @param spawnX
	 *            the x coordinate of the spawn position
	 * @param spawnY
	 *            the y coordinate of the spawn position
	 */
	protected void setSpawn(int spawnX, int spawnY)
	{
		this.spawnX = spawnX;
		this.spawnY = spawnY;
	}

	/**
	 * 
	 * @return dy
	 */
	public double getDy()
	{
		return dy;
	}

	/**
	 * Pushes the object in the y direction. If the object cannot be pushed farther
	 * in the direction of dy the object will stay at the same place.
	 * 
	 * @param dy
	 *            the distance the object will be pushed upwards
	 */
	public abstract void yPush(double dy);
}
