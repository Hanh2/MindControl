package mindcontrol.gameObjects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import mindcontrol.gameObjects.characters.ControllableCharacter;
import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;
import mindcontrol.managers.PuzzleManager;
import mindcontrol.managers.Sprite;
import platformer2D.MovableGameObject;

// TODO
// something is wrong with the elevators when going back to menu and then starting again
public class Elevator extends MovableGameObject
{
	// speed
	private static final int ELEVATORSPEED = 120;

	// reference to the puzzle manager
	private PuzzleManager pm;

	// sprites
	private Sprite pulley, wires, cage;

	// pulley
	private int pulleyXPos;
	private int pulleyYPos;

	// one tile below pulleyYPos;
	private int wiresYPos;

	// Stores the stop points in y-coordinates. (Only need y since the elevators
	// won't be moving sideways.)
	private int[] stopPoints;
	private int respawnStop;
	private int currentStop;

	// holds all gameObjects that are inside the elevator
	private ArrayList<MovableGameObject> mgoInsideElevator;
	// holds all gameObjects that can ride the elevator
	private ArrayList<MovableGameObject> allElevatorMgo;

	public Elevator(int pulleyX, int pulleyY, int[] stopPoints, int respawnStop)
	{
		setSpawn(pulleyX - tileSize, stopPoints[respawnStop]);

		// pulley
		pulleyXPos = pulleyX;
		pulleyYPos = pulleyY;

		// stop points
		this.stopPoints = stopPoints;
		this.respawnStop = respawnStop;

		init();
		reset();
	}

	private void init()
	{
		// sprites
		pulley = new Sprite("/Sprites/Map_Objects/Pulley.png");
		wires = new Sprite("/Sprites/Map_Objects/Wire_Ropes.png");
		cage = new Sprite("/Sprites/Map_Objects/Lift_Cage.png");

		// wires position
		wiresYPos = pulleyYPos + tileSize;

		// hitbox
		setHbOffsets(0, tileSize);
		int threeTileSize = 3 * tileSize;
		setHitbox(threeTileSize, threeTileSize);

		// speed
		speed = (double) ELEVATORSPEED / GV.FPS;

		// initialize array list
		mgoInsideElevator = new ArrayList<MovableGameObject>();
		allElevatorMgo = new ArrayList<MovableGameObject>();

		// SFX
		JukeBox.load("/SFX/Lift.mp3", "Elevator");
	}

	@Override
	public void reset()
	{
		super.reset();

		currentStop = respawnStop;
		mgoInsideElevator.clear();

		JukeBox.stop("Elevator");
	}

	public void setConnections(PuzzleManager pm)
	{
		this.pm = pm;

		allElevatorMgo.add(pm.getPlayer());
		allElevatorMgo.addAll(pm.getAnimals());
	}

	@Override
	protected void updateMomentum()
	{
		// moving up
		if (dy < 0 && y < stopPoints[currentStop])
		{
			mgoInsideElevator.clear();

			dy = 0;
			y = stopPoints[currentStop];
			pm.setCurrentElevator(-1);

			// stop SFX
			JukeBox.stop("Elevator");
		}

		// moving down
		if (dy > 0 && y > stopPoints[currentStop])
		{
			dy = 0;
			y = stopPoints[currentStop];
			pm.setCurrentElevator(-1);

			// stop SFX
			JukeBox.stop("Elevator");
		}
	}

	@Override
	protected void updateSpritePosition()
	{
		// if moving up
		if (dy < 0)
		{
			for (MovableGameObject mgo : mgoInsideElevator)
			{
				mgo.yPush(dy);
			}
		}

		y += dy;
	}

	@Override
	public void yPush(double dy)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g)
	{
		// pulley
		pulley.draw(pulleyXPos, pulleyYPos, g);

		// wires
		wires.draw(pulleyXPos, wiresYPos, tileSize, (int) (y - wiresYPos), g);

		// cage
		cage.draw((int) x, (int) y, g);
	}

	/**
	 * 
	 * @param ch
	 *            the the character trying to move up
	 * @return true if it is possible to move up (i.e. if the player is entirely
	 *         inside the elevator cage and the elevator is not at the top stop),
	 *         otherwise return false.
	 */
	public boolean moveUp(ControllableCharacter ch)
	{
		// get hitbox copy
		Rectangle hbCopy = getHitbox();

		// if the player is entirely inside the elevator cage standing on the
		// floor and the elevator is not at the top stop.
		if (currentStop > 0 && ch.inside(hbCopy) && ch.getHbBottomY() == this.getHbBottomY())
		{
			// play SFX
			JukeBox.play("Elevator");

			for (MovableGameObject mgo : allElevatorMgo)
			{
				if (mgo.inside(hbCopy))
					mgoInsideElevator.add(mgo);
			}

			currentStop--;
			dy = -speed;
			return true;
		}
		else
			return false;
	}

	/**
	 * 
	 * @param playerHitbox
	 * @return true if it is possible to move down (i.e. if the player is entirely
	 *         inside the elevator cage and the elevator is not at the bottom stop),
	 *         otherwise return false.
	 */
	public boolean moveDown(ControllableCharacter ch)
	{
		// get hitbox copy
		Rectangle hbCopy = getHitbox();

		// if the player is entirely inside the elevator cage standing on the
		// floor and the elevator is not at the bottom stop.
		if (currentStop < stopPoints.length - 1 && ch.inside(hbCopy) && ch.getHbBottomY() == this.getHbBottomY())
		{
			// play SFX
			JukeBox.play("Elevator");

			currentStop++;
			dy = speed;
			return true;
		}
		else
			return false;

	}

	public boolean collidesWithRoof(Rectangle hb)
	{
		Rectangle roof = new Rectangle((int) x, (int) y + hbYOffset, getHbWidth(), 1);

		return hb.intersects(roof);
	}

	public boolean landsOnFloor(Rectangle hb, int dy)
	{
		return JumpThroughPlatform.landsOn(getHbLeftX(), getHbBottomY(), getHbWidth(), hb, dy);
	}

}
