package mindcontrol.gameObjects.characters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import mindcontrol.gameObjects.Elevator;
import mindcontrol.gameObjects.HeavyBox;
import mindcontrol.gameObjects.LevelGate;
import mindcontrol.gameObjects.Shelf;
import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;
import mindcontrol.library.Keys;
import mindcontrol.managers.Animation;
import mindcontrol.managers.Sprite;
import platformer2D.GameObject;
import platformer2D.library.CharacterState;
import platformer2D.library.Debugger;

public class Player extends ControllableCharacter
{
	// the character measured in pixels per second
	private static final int CHARACTERSPEED = 180 * GV.SPEED_MULTIPLIER;
	private static final int CHARACTERJUMP = 500;

	// sprites and animations
	private Sprite idle;
	private Animation walking;
	private Sprite airborne;

	// keys
	private int numOfKeys;

	// beam
	private boolean beaming;
	private Beam beam;

	// control wave
	private boolean attemptingToControl; // true when sending out the wave
	private boolean controllingAnimal; // true when mind controlling an animal
	private ControlWave controlWave;

	// animals
	private Animal closestAnimal;

	public Player(int spawnX, int spawnY, boolean facingRightAtStart)
	{
		super("Robot");

		setSpawn(spawnX, spawnY);
		setFacingRightAtStart(facingRightAtStart);

		init();
		reset();
	}

	private void init()
	{
		// sprites and animations
		try
		{
			idle = new Sprite("/Sprites/Characters/Robot/Robot_Idle.png");
			walking = new Animation("/Sprites/Characters/Robot/Robot_Walking.png", 64, 64);
			walking.setAnimationFPS(24);
			airborne = new Sprite("/Sprites/Characters/Robot/Robot_Airborne.png");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		int twoTileSize = 2 * tileSize;
		setSpriteSize(twoTileSize, twoTileSize);

		// hitbox
		setHbOffsets(0, 6);
		setHitbox(sWidth, sHeight - 6);

		// beam
		beam = new Beam(4, 60, 8);

		// control wave
		controlWave = new ControlWave();

		// speed and jump
		speed = (double) CHARACTERSPEED / GV.FPS;
		jump = (double) CHARACTERJUMP / GV.FPS;

		// closest animal
		closestAnimal = null;

		// SFX
		JukeBox.load("/SFX/Sonar.mp3", "ControlWave");
		JukeBox.load("/SFX/BeamSwitch.mp3", "BeamSwitch");
//		JukeBox.load("/SFX/RobotLanding.mp3", "Landing");
//		JukeBox.load("/SFX/Jump.mp3", "Jump");
	}

	@Override
	public void reset()
	{
		super.reset();

		// beam
		beam.reset();
		beaming = false;

		// control wave
		controlWave.reset();
		attemptingToControl = false;
		controllingAnimal = false;

		// keys
		numOfKeys = 0;
	}

	private void mindControlAttempt()
	{
		// play SFX
		JukeBox.play("ControlWave");

		controlWave.start();
		dx = 0;
		state = CharacterState.IDLE;
		attemptingToControl = true;

		ArrayList<Animal> animals = new ArrayList<Animal>(pm.getAnimals());

		if (animals.size() == 0)
			return;

		// center position of robot
		int centerX = (int) getCenterX();
		int centerY = (int) getCenterY();

		// find closest animal
		int closestAnimalReceiverDistanceSquared = Integer.MAX_VALUE;

		for (Animal animal : animals)
		{
			// calculate distance
			Point p = animal.getReceiverPosition();
			int xDistance = p.x - centerX;
			int yDistance = p.y - centerY;
			int receiverDistanceSquared = xDistance * xDistance + yDistance * yDistance;

			if (receiverDistanceSquared < closestAnimalReceiverDistanceSquared)
			{
				closestAnimalReceiverDistanceSquared = receiverDistanceSquared;
				closestAnimal = animal;
			}
		}

	}

	private void actionAttempt()
	{
		// level gates
		ArrayList<LevelGate> levelGates = pm.getLevelGates();
		for (LevelGate lg : levelGates)
		{
			// if a level gate is close enough for the beam
			if (lg.useBeam(beam.getHitbox()))
				return;
		}
	}

	@Override
	protected void control()
	{
		if (attemptingToControl || beaming || inElevator)
			return;

		// movement
		if (state == CharacterState.AIRBORNE)
		{
			// if pressing both left and right or none at all
			if (Keys.keyState[Keys.LEFT] == Keys.keyState[Keys.RIGHT])
			{
				dx *= 0.9;
			}
			else if (Keys.keyState[Keys.LEFT]) // only pressing left
			{
				if (dx > 0) // slow down momentum if moving right
				{
					dx *= 0.9;
				}
				else
				{
					if (dx > -1)
						dx = -1;

					facingRight = false;
				}
			}
			else // only pressing right
			{
				if (dx < 0) // slow down momentum if moving left
				{
					dx *= 0.9;
				}
				else
				{
					if (dx < 1)
						dx = 1;

					facingRight = true;
				}
			}
		}
		else // on ground
		{
			// mind control attempt
			if (Keys.keyState[Keys.MINDCONTROL] && !Keys.prevKeyState[Keys.MINDCONTROL])
			{
				mindControlAttempt();
				return;
			}

			// beam
			if (Keys.keyState[Keys.ACTION] && !Keys.prevKeyState[Keys.ACTION])
			{
				// play SFX
				JukeBox.play("BeamSwitch");
				
				beam.start();
				dx = 0;
				state = CharacterState.IDLE;
				beaming = true;
				return;
			}

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

			if (Keys.keyState[Keys.UP])
			{
				for (int i = 0; i < elevators.size(); i++)
				{
					if (elevators.get(i).moveUp(this))
					{
						pm.setCurrentElevator(i);
						dx = 0;
						state = CharacterState.IDLE;
						return;
					}
				}
			}
			else if (Keys.keyState[Keys.DOWN])
			{
				for (int i = 0; i < elevators.size(); i++)
				{
					if (elevators.get(i).moveDown(this))
					{
						pm.setCurrentElevator(i);
						dx = 0;
						state = CharacterState.IDLE;
						return;
					}
				}
			}

			if (Keys.keyState[Keys.JUMP])
			{
				dy = -jump;
				state = CharacterState.AIRBORNE;
//				JukeBox.play("Jump");
			}
		}

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
		ArrayList<GameObject> gates = new ArrayList<GameObject>();
		gates.addAll(pm.getGates());

		boolean onGround = false;

		// VERTICAL MOVEMENT (y-axis)
		if (dy < 0) // moving up
		{
			y += dy;
			Rectangle hitboxCopy = getHitbox();

			// check collisions with background
			Rectangle platform = bg.collides(hitboxCopy);
			if (platform != null)
			{
				y = platform.y + platform.height - hbYOffset;
				dy = 0;
			}

			for (GameObject go : gates)
			{
				if (go.collides(hitboxCopy))
				{
					y = go.getHbBottomY() - hbYOffset;
					dy = 0;
					break;
				}
			}

			for (Elevator el : elevators)
			{
				// if the player collides with the roof of the elevator
				if (el.collidesWithRoof(hitboxCopy))
				{
					y = el.getHbTopY() + 1 - hbYOffset;
					dy = 0;
					break;
				}
			}
		}
		else if (dy > 0) // moving down
		{
			// get position of the hitbox before moving to check with jtp:s
			Rectangle hitboxCopy = getHitbox();

			// elevator floor
			for (Elevator el : elevators)
			{
				// if the player lands on the floor of the elevator
				if (el.landsOnFloor(hitboxCopy, (int) dy))
				{
					y = el.getHbBottomY() - sHeight;
					dy = Math.max(0, el.getDy());
					onGround = true;
					break;
				}
			}

			// box roofs
			if (!onGround && !inElevator)
			{
				ArrayList<HeavyBox> boxes = pm.getBoxes();
				for (HeavyBox box : boxes)
				{
					// if the player lands on the roof of the box
					if (box.landsOnRoof(hitboxCopy, (int) dy))
					{
						y = box.getHbTopY() - sHeight;
						dy = Math.max(0, box.getDy());
						onGround = true;
						break;
					}
				}
			}

			// shelf roofs
			if (!onGround)
			{
				ArrayList<Shelf> shelves = pm.getShelves();
				for (Shelf sh : shelves)
				{
					// if the player lands on the roof of the box
					if (sh.landsOnRoof(hitboxCopy, (int) dy))
					{
						y = sh.getHbTopY() - sHeight;
						dy = 0;
						onGround = true;
						break;
					}
				}
			}

			Rectangle movingDownHitboxCopy = null;

			if (!onGround)
			{
				y += dy;

				// check with a rectangle with 1 pixel row below the hitbox to be able to "feel"
				// the ground
				movingDownHitboxCopy = new Rectangle((int) x, (int) y + hbYOffset, getHbWidth(), getHbHeight() + 1);

				// check collisions with background
				Rectangle platform = bg.collides(movingDownHitboxCopy);
				if (platform != null)
				{
					y = platform.y - sHeight;
					dy = 0;
					onGround = true;
				}
			}

			if (!onGround)
			{
				for (GameObject go : gates)
				{
					if (go.collides(movingDownHitboxCopy))
					{
						y = go.getHbTopY() - sHeight;
						dy = 0;
						onGround = true;
						break;
					}
				}
			}

			if (!onGround)
			{
				for (Elevator el : elevators)
				{
					// if the player lands on the roof of the elevator
					if (el.collidesWithRoof(movingDownHitboxCopy))
					{
						y = el.getHbTopY() - sHeight;
						dy = Math.max(0, el.getDy());
						onGround = true;
						break;
					}
				}
			}
		}

		if (!onGround) // if not on ground
			state = CharacterState.AIRBORNE;

		else if (state == CharacterState.AIRBORNE)// if the character just landed
		{
//			JukeBox.play("Landing");
			state = CharacterState.IDLE;
		}

		// HORIZONTAL MOVEMENT (x-axis)
		if (dx < 0) // moving to the left
		{
			x += dx;
			Rectangle hitboxCopy = getHitbox();

			// check collisions with background
			Rectangle platform = bg.collides(hitboxCopy);
			if (platform != null)
			{
				x = platform.x + platform.width;
				dx = 0;

				if (onGround)
					state = CharacterState.PUSHING_AGAINST_WALL;
			}

			for (GameObject go : gates)
			{
				if (go.collides(hitboxCopy))
				{
					x = go.getHbRightX();
					dx = 0;

					if (onGround)
						state = CharacterState.PUSHING_AGAINST_WALL;

					break;
				}
			}

			for (Elevator el : elevators)
			{
				// if the player collides with the roof of the elevator
				if (el.collidesWithRoof(hitboxCopy))
				{
					x = el.getHbRightX();
					dx = 0;

					if (onGround)
						state = CharacterState.PUSHING_AGAINST_WALL;

					break;
				}
			}
		}
		else if (dx > 0) // moving to the right
		{
			x += dx;
			Rectangle hitboxCopy = getHitbox();

			// check collisions with background
			Rectangle platform = bg.collides(hitboxCopy);
			if (platform != null)
			{
				x = platform.x - sWidth;
				dx = 0;

				if (onGround)
					state = CharacterState.PUSHING_AGAINST_WALL;
			}

			for (GameObject go : gates)
			{
				if (go.collides(hitboxCopy))
				{
					x = go.getHbLeftX() - sWidth;
					dx = 0;

					if (onGround)
						state = CharacterState.PUSHING_AGAINST_WALL;

					break;
				}
			}

			for (Elevator el : elevators)
			{
				// if the player collides with the roof of the elevator
				if (el.collidesWithRoof(hitboxCopy))
				{
					x = el.getHbLeftX() - sWidth;
					dx = 0;

					if (onGround)
						state = CharacterState.PUSHING_AGAINST_WALL;

					break;
				}
			}
		}
	}

	@Override
	public void update()
	{
		updateMomentum();

		if (!controllingAnimal)
			control();
		else if (Keys.keyState[Keys.MINDCONTROL] && !Keys.prevKeyState[Keys.MINDCONTROL])
		{
			controllingAnimal = false; // disconnect from animal
			pm.getCamera().setTargetCharacter(this);
		}

		updateSpritePosition();
		updateHbPosition();

		if (attemptingToControl)
		{
			if (closestAnimal == null)
				return;

			if (closestAnimal.mindControl(controlWave.getHitbox()))
			{
				attemptingToControl = false;
				controlWave.reset();

				controllingAnimal = true;
				pm.getCamera().setTargetCharacter(closestAnimal);
			}
		}

	}

	@Override
	public void yPush(double dy)
	{
		y += dy;
	}

	@Override
	public void drawHitbox(Graphics2D g)
	{
		super.drawHitbox(g);
		beam.drawHitbox(g);
	}

	@Override
	public void draw(Graphics2D g)
	{
		switch (state)
		{
		case IDLE:
		case PUSHING_AGAINST_WALL:
			idle.draw((int) x, (int) y, facingRight, g);
			break;
		case WALKING:
			walking.animate((int) x, (int) y, facingRight, g);
			break;
		case AIRBORNE:
			airborne.draw((int) x, (int) y, facingRight, g);
			break;
		default:
			System.out.println("Default reached in Player.draw(Graphics2D g)");
		}

		beam.draw(g);
		controlWave.draw(g);
	}

	public void setBeaming(boolean b)
	{
		beaming = b;
	}

	public void setMindControlling(boolean b)
	{
		attemptingToControl = b;
	}

	/**
	 * Increments keyCount.
	 */
	public void addKey()
	{
		numOfKeys++;
	}

	/**
	 * If the number of keys on the player (keyCount) exceeds maxNumOfKeysToGet if
	 * returns maxNumOfKeysToGet and subtracts that many keys from keyCount.
	 * Otherwise, it returns the total number of keys on the player (keyCount) and
	 * sets keyCount to zero.
	 * 
	 * @return number of keys.
	 */
	public int getKey(int maxNumOfKeysToGet)
	{
		if (numOfKeys >= maxNumOfKeysToGet)
		{
			numOfKeys -= maxNumOfKeysToGet;
			return maxNumOfKeysToGet;
		}
		else
		{
			int temp = numOfKeys;
			numOfKeys = 0;
			return temp;
		}
	}

	public int getNumOfKeys()
	{
		return numOfKeys;
	}

	// Nested classes
	/**
	 * Handles the control wave.
	 * 
	 * @author denhanh
	 *
	 */
	private class ControlWave
	{
		// color
		private Color cwColor = new Color(.3f, 0f, .8f);

		// how many frames the control wave is turned on for when activated
		private int cwInterval = GV.CONTROL_WAVE_INTERVAL_IN_FRAMES;

		private int cwSpeed = GV.CONTROL_WAVE_REACH / cwInterval;

		// current radius of the control wave
		private int cwRadius;

		// how many more frames the control wave will be turned on for
		private int cwTimer;

		public void reset()
		{
			cwRadius = 0;
			cwTimer = 0;
		}

		public void start()
		{
			cwTimer = cwInterval;
		}

		public void draw(Graphics2D g)
		{
			if (cwTimer > 0)
			{
				// increment radius
				cwRadius += cwSpeed;

				// draw control wave
				g.setColor(cwColor);
				int cwDiameter = 2 * cwRadius;
				g.drawOval((int) getCenterX() - cwRadius, (int) (getCenterY() - cwRadius), cwDiameter, cwDiameter);

				// decrement timer
				cwTimer--;
				if (cwTimer == 0)
				{
					cwRadius = 0;
					setMindControlling(false);
				}
			}
		}

		public Ellipse2D.Float getHitbox()
		{
			int diameter = 2 * cwRadius;

			return new Ellipse2D.Float((float) getCenterX() - cwRadius, (float) (getCenterY() - cwRadius), diameter,
					diameter);
		}
	}

	/**
	 * Handles the beam.
	 * 
	 * @author denhanh
	 *
	 */
	private class Beam
	{
		// hitbox
		Rectangle beamHitbox;

		// color
		private Color bColor = new Color(0f, 0.1f, .7f, 0.3f);

		// offsets
		private int bFacingLeftXOffset;
		private int bFacingRightXOffset;
		private int bYOffset;

		// size
		private int bWidth;
		private int bHalfHeight;

		// the x-array for the coordinates describing the beam when facing left.
		private int[] leftBeamXArray;
		// the x-array for the coordinates describing the beam when facing right
		private int[] rightBeamXArray;
		// the y-array for the coordinates describing the beam (facing either side)
		private int[] bYArray;
		// number of points describing the beams
		private int bNumberOfPoints = 3;

		// the beam interval in frames
		private int bInterval = GV.ROBOT_BEAM_INTERVAL_IN_FRAMES;
		// how many more frames the beam will be turned on for.
		private int bTimer;

		public Beam(int facingLeftXOffset, int facingRightXOffset, int yOffset)
		{
			this.bFacingLeftXOffset = facingLeftXOffset;
			this.bFacingRightXOffset = facingRightXOffset;
			this.bYOffset = yOffset;

			init();
		}

		private void init()
		{
			bWidth = bFacingLeftXOffset + tileSize;
			bHalfHeight = tileSize / 4;
			beamHitbox = new Rectangle(bWidth, 2 * bHalfHeight);

			int[] tempLeftBeamXArray = { 0, -bWidth, -bWidth };
			int[] tempRightBeamXArray = { 0, bWidth, bWidth };
			int[] tempYArray = { 0, -bHalfHeight, bHalfHeight };

			leftBeamXArray = tempLeftBeamXArray;
			rightBeamXArray = tempRightBeamXArray;
			bYArray = tempYArray;
		}

		public void reset()
		{
			bTimer = 0;
		}

		public void drawHitbox(Graphics2D g)
		{
			int bX = (int) x;
			if (facingRight)
				bX += bFacingRightXOffset;
			else
				bX -= tileSize;

			// player hitbox y-coordinate
			int bY = (int) y + hbYOffset;

			beamHitbox.setLocation(bX, bY);

			Debugger.drawHitbox(beamHitbox, g);
		}

		public void draw(Graphics2D g)
		{
			if (bTimer > 0)
			{
				// player hitbox x- and y-coordinates
				int bX = (int) x;
				int bY = (int) y + hbYOffset;

				if (facingRight)
				{
					Polygon rightBeam = new Polygon(rightBeamXArray, bYArray, bNumberOfPoints);
					rightBeam.translate(bX + bFacingRightXOffset, bY + bYOffset);

					g.setColor(bColor);
					g.fillPolygon(rightBeam);
				}
				else
				{
					Polygon leftBeam = new Polygon(leftBeamXArray, bYArray, bNumberOfPoints);
					leftBeam.translate(bX + bFacingLeftXOffset, bY + bYOffset);

					g.setColor(bColor);
					g.fillPolygon(leftBeam);
				}

				bTimer--;
				if (bTimer == 0)
				{
					setBeaming(false);
					actionAttempt();
				}
			}
		}

		public void start()
		{
			bTimer = bInterval;
		}

		public Rectangle getHitbox()
		{
			// x
			int x = getHbLeftX();
			if (facingRight)
				x += bFacingRightXOffset;
			else
				x -= tileSize;

			// y
			int y = getHbTopY();

			beamHitbox.setLocation(x, y);

			return beamHitbox;
		}
	}
}
