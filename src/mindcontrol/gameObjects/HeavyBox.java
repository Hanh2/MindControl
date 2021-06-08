package mindcontrol.gameObjects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import mindcontrol.managers.PuzzleManager;
import mindcontrol.managers.Sprite;
import platformer2D.GameObject;
import platformer2D.MovableGameObject;

public class HeavyBox extends MovableGameObject
{
	// sprite
	private Sprite box;

	// list of all game objects that the box cannot pass
	private ArrayList<HeavyBox> boxes;
	private ArrayList<GameObject> gates;

	public HeavyBox(int x, int y)
	{
		setSpawn(x, y);

		init();
		reset();
	}

	private void init()
	{
		// sprite
		box = new Sprite("/Sprites/Map_Objects/Heavy_Box.png");
		int twoTileSize = 2 * tileSize;
		setSpriteSize(twoTileSize, twoTileSize);

		// hitbox
		setHitbox(twoTileSize, twoTileSize);

		// initialize array list
		boxes = new ArrayList<HeavyBox>();
		gates = new ArrayList<GameObject>();

	}

	public void setConnections(PuzzleManager pm)
	{
		// add boxes
		boxes.addAll(pm.getBoxes());

		// remove this object from the list
		for (int i = 0; i < boxes.size(); i++)
		{
			if (boxes.get(i) == this)
			{
				boxes.remove(i);
				break;
			}
		}

		// add other movable game objects

		// add stationary game objects
		gates.addAll(pm.getGates());

	}

	@Override
	protected void updateMomentum()
	{
		dy += gravity;

		if (dy > maxFallSpeed)
			dy = maxFallSpeed;
	}

	@Override
	protected void updateSpritePosition()
	{
		if (dy > 0)
		{
			// save hitbox
			Rectangle hbCopy = getHitbox();

			for (HeavyBox box : boxes)
			{
				if (box.landsOnRoof(hbCopy, (int) dy))
				{
					y = box.getHbTopY() - sHeight;
					dy = Math.max(0, box.getDy());
					return;
				}
			}

			// move box
			y += dy;

			// check with a rectangle with 1 pixel row below the hitbox to be able to "feel"
			// the ground
			Rectangle movingDownHitboxCopy = new Rectangle(getHbLeftX(), getHbTopY(), getHbWidth(), getHbHeight() + 1);

			// check collisions with background
			Rectangle platform = bg.collides(movingDownHitboxCopy);
			if (platform != null)
			{
				y = platform.y - sHeight;
				dy = 0;
				return;
			}

			for (GameObject sgo : gates)
			{
				if (sgo.collides(movingDownHitboxCopy))
				{
					y = sgo.getHbTopY() - sHeight;
					dy = 0;
					return;
				}
			}
		}
	}

	@Override
	public void yPush(double dy)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g)
	{
		box.draw((int) x, (int) y, g);
	}

	public boolean landsOnRoof(Rectangle hb, int dy)
	{
		return JumpThroughPlatform.landsOn((int) x, (int) y, getHbWidth(), hb, dy);
	}

	/**
	 * Tries to push this box the indicated distance (dx) to the right along the X
	 * coordinate axis.
	 * 
	 * @param dx
	 *            the distance the box will be moved in the x-direction if nothing
	 *            is in the way
	 * @return the distance traveled
	 */
	public int push(int dx)
	{
		return push(dx, false);
	}

	/**
	 * Tries to push this box the indicated distance (dx) to the right along the X
	 * coordinate axis.
	 * 
	 * @param dx
	 *            the distance the box will be moved in the x-direction if nothing
	 *            is in the way
	 * @param pushByBox
	 *            whether the box was pushed by another box or not
	 * @return the distance traveled
	 */
	private int push(int dx, boolean pushByBox)
	{
		// save hitbox
		Rectangle prevHitbox = getHitbox();

		// move box
		x += dx;

		// The distance to push the other object back if this object cannot be
		// translated the indicated distance.
		int pushback = 0;
		int targetX = (int) x;

		// get hitbox copy
		Rectangle hbCopy = getHitbox();

		for (HeavyBox box : boxes)
		{
			if (box.collides(hbCopy) && !box.collides(prevHitbox))
			{
				// the box wont push other boxes if it was pushed by another box. This limits
				// the elephant to pushing 2 boxes at the same time (unless they are stacked on
				// top of each other).
				if (pushByBox)
				{
					if (dx > 0)
					{
						x = box.getHbLeftX() - sWidth;
					}
					else
					{
						x = box.getHbRightX();
					}
				}
				else
				{
					if (dx > 0)
					{
						int deltaX = hbCopy.x + hbCopy.width - box.getHbLeftX();
						int pushBack = box.push(deltaX, true);
						x = hbCopy.x + pushBack;
					}
					else
					{
						int deltaX = hbCopy.x - box.getHbRightX();
						int pushBack = box.push(deltaX, true);
						x = hbCopy.x + pushBack;
					}
				}

				pushback = (int) x - targetX;
				break;
			}
		}

		// check collisions with background
		if (pushback == 0)
		{
			Rectangle platform = bg.collides(hbCopy);
			if (platform != null)
			{
				if (dx > 0)
					x = platform.x - sWidth;
				else
					x = platform.x + platform.width;

				pushback = (int) x - targetX;
			}
		}

		if (pushback == 0)
		{
			for (GameObject sgo : gates)
			{
				if (sgo.collides(hbCopy))
				{
					if (dx > 0)
						x = sgo.getHbLeftX() - sWidth;
					else
						x = sgo.getHbRightX();

					pushback = (int) x - targetX;
					break;
				}
			}
		}

		for (HeavyBox box : boxes)
		{
			if (box.onTopOf(hbCopy))
			{
				box.push(dx+pushback);
			}
		}

		return pushback;
	}
}
