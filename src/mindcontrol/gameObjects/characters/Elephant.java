package mindcontrol.gameObjects.characters;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import mindcontrol.gameObjects.HeavyBox;
import mindcontrol.library.GV;
import mindcontrol.library.Keys;
import mindcontrol.managers.Animation;
import mindcontrol.managers.Sprite;
import platformer2D.GameObject;
import platformer2D.library.CharacterState;

public class Elephant extends Animal
{
	// speed
	private static final int ELEPHANTSPEED = 60 * GV.SPEED_MULTIPLIER;

	// sprites and animations
	private Sprite idle, idleAction;
	private Animation walking, pushing;

	public Elephant(int x, int y, boolean facingRightAtStart)
	{
		super("Elephant");

		setSpawn(x, y);
		setFacingRightAtStart(facingRightAtStart);

		init();
		reset();
	}

	private void init()
	{
		// sprite size
		int sWidth = 6 * tileSize;
		int sHeight = 4 * tileSize;
		setSpriteSize(sWidth, sHeight);

		// receiver
		int rFacingLeftXOffset = 29;
		int rFacingRightXOffset = sWidth - 30;
		int rYOffset = 7;
		int rActionFacingRightXOffset = sWidth - 19;
		int rActionFacingLeftXOffset = 18;
		int rActionYOffset = 15;
		receiver = new Receiver(rFacingLeftXOffset, rFacingRightXOffset, rYOffset, rActionFacingLeftXOffset,
				rActionFacingRightXOffset, rActionYOffset);
		int receiverRadius = GV.RECEIVER_RADIUS;

		// hitbox
		int hbXOffset = 2;
		int hbYOffset = 5;
		setHbOffsets(hbXOffset, 5);
		setHitbox(sWidth - 2 * hbXOffset, sHeight - hbYOffset);

		// sprites and animations
		try
		{
			// idle sprite
			String elephantIdleLocation = "/Sprites/Characters/Elephant/Elephant_Idle.png";
			String receiverLocation = "/Sprites/Characters/Receiver.png";
			idle = new Sprite(elephantIdleLocation, receiverLocation, rFacingRightXOffset - receiverRadius,
					rYOffset - receiverRadius);
			String elephantIdleActionLocation = "/Sprites/Characters/Elephant/Elephant_Idle_Action.png";

			// action sprite
			idleAction = new Sprite(elephantIdleActionLocation, receiverLocation,
					rActionFacingRightXOffset - receiverRadius, rActionYOffset - receiverRadius);

			// walking animation
			String elephantWalkingLocation = "/Sprites/Characters/Elephant/Elephant_Walking.png";
			walking = new Animation(elephantWalkingLocation, sWidth, sHeight);
			walking.setUpdatesPerFrame(8);
			walking.addToAllFrames(receiverLocation, rFacingRightXOffset - receiverRadius, rYOffset - receiverRadius);

			// pushing animation
			String elephantPushingLocation = "/Sprites/Characters/Elephant/Elephant_Pushing.png";
			pushing = new Animation(elephantPushingLocation, sWidth, sHeight);
			pushing.setUpdatesPerFrame(8);
			pushing.addToAllFrames(receiverLocation, rActionFacingRightXOffset - receiverRadius,
					rActionYOffset - receiverRadius);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// speed
		speed = (double) ELEPHANTSPEED / GV.FPS;
	}

	@Override
	protected void control()
	{
		if (!controlled)
			return;

		// disconnect
		if (Keys.keyState[Keys.MINDCONTROL] && !Keys.prevKeyState[Keys.MINDCONTROL])
		{
			controlled = false;
			receiver.disconnect();
			dx = 0;
			state = CharacterState.IDLE;
			actionActivated = false;
			return;
		}

		if (Keys.keyState[Keys.ACTION])
			actionActivated = true;
		else
			actionActivated = false;

		// if pressing both left and right or none at all
		if (Keys.keyState[Keys.LEFT] == Keys.keyState[Keys.RIGHT])
		{
			dx = 0;
			state = CharacterState.IDLE;
		}
		else if (Keys.keyState[Keys.LEFT]) // only pressing left
		{
			facingRight = false;
			dx = -speed;
			state = CharacterState.WALKING;
		}
		else // only pressing right
		{
			facingRight = true;
			dx = speed;
			state = CharacterState.WALKING;
		}

	}

	@Override
	protected void updateMomentum()
	{

	}

	@Override
	protected void updateSpritePosition()
	{
		// get objects to check collision with
		ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
		gameObjects.addAll(pm.getGates());

		// save hitbox
		Rectangle prevHitbox = getHitbox();

		// move sprite
		x += dx;

		// HORIZONTAL MOVEMENT (x-axis)
		if (dx < 0) // moving to the left
		{
			// get hitbox copy
			Rectangle hbCopy = getHitbox();

			// check collisions with background
			Rectangle platform = bg.collides(hbCopy);
			if (platform != null)
			{
				x = platform.x + platform.width - hbXOffset;
				dx = 0;

				state = CharacterState.PUSHING_AGAINST_WALL;
				actionActivated = true;

				return;
			}

			for (GameObject go : gameObjects)
			{
				if (go.collides(hbCopy))
				{
					x = go.getHbRightX() - hbXOffset;
					dx = 0;

					state = CharacterState.PUSHING_AGAINST_WALL;
					actionActivated = true;

					return;
				}
			}

			// if the action button is pressed and no objects have been collided
			if (actionActivated)
			{		
				// create list of all boxes
				ArrayList<HeavyBox> boxes = new ArrayList<HeavyBox>();
				boxes.addAll(pm.getBoxes());

				for (HeavyBox box : boxes)
				{
					if (box.collides(hbCopy) && !box.collides(prevHitbox))
					{				
						int deltaX = hbCopy.x - box.getHbRightX();
						int pushBack = box.push(deltaX);
						x = hbCopy.x + pushBack - hbXOffset;
					}
					// Box
				}
			}
		}
		else if (dx > 0) // moving to the right
		{
			// get hitbox copy
			Rectangle hbCopy = getHitbox();

			// check collisions with background
			Rectangle platform = bg.collides(hbCopy);
			if (platform != null)
			{
				x = platform.x - getHbWidth() - hbXOffset;
				dx = 0;

				state = CharacterState.PUSHING_AGAINST_WALL;
				actionActivated = true;

				return;
			}
			

			for (GameObject go : gameObjects)
			{
				if (go.collides(hbCopy))
				{
					x = go.getHbLeftX() - sWidth + hbXOffset;
					dx = 0;

					state = CharacterState.PUSHING_AGAINST_WALL;
					actionActivated = true;

					return;
				}
			}

			// if the action button is pressed and no other items has been collided
			if (actionActivated)
			{
				// create list of all boxes
				ArrayList<HeavyBox> boxes = new ArrayList<HeavyBox>();
				boxes.addAll(pm.getBoxes());

				for (HeavyBox box : boxes)
				{
					if (box.collides(hbCopy) && !box.collides(prevHitbox))
					{														
						int deltaX = hbCopy.x + hbCopy.width - box.getHbLeftX();
						int pushBack = box.push(deltaX);
						x = hbCopy.x + pushBack - hbXOffset;
					}
				}				
			}
		}
	}

	@Override
	public void yPush(double dy)
	{
		y += dy;
	}

	@Override
	public void draw(Graphics2D g)
	{
		switch (state)
		{
		case IDLE:
			if (actionActivated)
				idleAction.draw((int) x, (int) y, facingRight, g);
			else
				idle.draw((int) x, (int) y, facingRight, g);
			break;
		case WALKING:
			if (actionActivated)
				pushing.animate((int) x, (int) y, facingRight, g);
			else
				walking.animate((int) x, (int) y, facingRight, g);
			break;
		case PUSHING_AGAINST_WALL:
			idleAction.draw((int) x, (int) y, facingRight, g);
			break;
		default:
			System.out.println("Default reached in Elephant.draw(Graphics2D g)");
		}

		// receiver light
		receiver.draw(g);
	}

}
