package mindcontrol.gameObjects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;
import mindcontrol.managers.PuzzleManager;
import mindcontrol.managers.Sprite;
import platformer2D.GameObject;
import platformer2D.MovableGameObject;

public class PathGate extends GameObject
{
	// detector constants
	private static final int LASERWIDTH = 4;
	private static final Color LASERCOLOR = new Color(1f, 0f, 0f, 0.5f);

	// sprites
	private Sprite detector;
	private BufferedImage gate;

	// detectors
	private ArrayList<Detector> detectors;
	// game objects that can intersect the laser
	private ArrayList<MovableGameObject> detectableGameObjects;
	// game objects that can stop the gate from closing
	private ArrayList<MovableGameObject> movGameObjects;

	// how far the path gate can stretch and contract
	private int minLength, maxLength;

	// speed
	private int dL, prevDL;
	private static final int speed = 300 / GV.FPS;

	// LaserIntersected

	public PathGate(int x, int y, int maxLength)
	{
		this.x = x;
		this.y = y;

		this.maxLength = maxLength;

		init();
	}

	private void init()
	{
		BufferedImage gatePart = null;

		try
		{
			detector = new Sprite("/Sprites/Map_Objects/Path_Gate/Detector.png");
			gatePart = ImageIO.read(getClass().getResourceAsStream("/Sprites/Map_Objects/Path_Gate/Path_Gate.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// initialize array lists
		detectors = new ArrayList<Detector>();
		detectableGameObjects = new ArrayList<MovableGameObject>();
		movGameObjects = new ArrayList<MovableGameObject>();

		minLength = maxLength / GV.PATH_GATE_EXPANSION_RATIO;

		// create gate image
		gate = new BufferedImage(tileSize, maxLength, BufferedImage.TYPE_INT_ARGB);

		// create graphics
		Graphics2D g = (Graphics2D) gate.getGraphics();

		// clear background
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, tileSize, maxLength);
		g.setComposite(AlphaComposite.Src);

		// draw gate parts onto gate
		for (int tempY = 0; tempY < maxLength; tempY += tileSize)
		{
			g.drawImage(gatePart, 0, tempY, null);
		}

		// dispose graphics
		g.dispose();

		setHitbox((int) x, (int) y, tileSize, minLength);

		// SFX
		JukeBox.load("/SFX/MetalSqueak.mp3", "PathGate");
	}

	public void reset()
	{
		resizeHb(tileSize, minLength);
	}

	public void addDetector(int detX, int detY, int laserLength)
	{
		detectors.add(new Detector(detX, detY, laserLength));
	}

	public void setConnections(PuzzleManager pm)
	{
		detectableGameObjects.add(pm.getPlayer());
		detectableGameObjects.addAll(pm.getAnimals());
		detectableGameObjects.addAll(pm.getBoxes());

		movGameObjects.addAll(detectableGameObjects);
		// movGameObjects.addAll(pm.getElevators());
	}

	public void update()
	{
		// save current length
		int gateLength = getHbHeight();
		boolean anyLaserIntersected = false;

		// check if any laser is intersected and set dL accordingly
		for (Detector d : detectors)
		{
			if (d.laserIsIntersected())
			{
				anyLaserIntersected = true;
				break;
			}
		}

		// if any laser is intersected
		if (anyLaserIntersected)
		{
			if (gateLength > minLength)
			{
				dL = -speed;

				// change gateLength and update hitbox size
				gateLength += dL;				
				if (gateLength < minLength)
					gateLength = minLength;				
				resizeHb(tileSize, gateLength);
			}
			else // gateLength == minLength
				dL = 0;
		}
		else // if no laser is intersected
		{
			// get hitbox copy
			Rectangle hbCopy = getHitbox();
			Rectangle nextHb = new Rectangle(hbCopy.x, hbCopy.y, hbCopy.width, hbCopy.height + speed);

			if (gateLength < maxLength)
			{
				// dL = speed if no object is in the way; otherwise: dL = 0
				dL = speed;
				for (MovableGameObject mgo : movGameObjects)
				{
					if (mgo.collides(nextHb))
					{
						dL = 0;
						break;
					}
				}

				// change gateLength and update hitbox size
				gateLength += dL;				
				if (gateLength > maxLength)
					gateLength = maxLength;				
				resizeHb(tileSize, gateLength);
			}
			else // gateLength == maxLength
				dL = 0;
		}

		// play/stop sound
		if (dL != 0 && prevDL == 0) // just started moving
		{
			if (!JukeBox.isPlaying("PathGate"))
				JukeBox.play("PathGate");
		}
		else if (dL == 0 && prevDL != 0) // just stopped
		{
			if (JukeBox.isPlaying("PathGate"))
				JukeBox.stop("PathGate");
		}

		// update prevDL
		prevDL = dL;
	}

	@Override
	public void draw(Graphics2D g)
	{
		// draw gate
		g.drawImage(gate, (int) x, (int) y, tileSize, getHbHeight(), null);

		// draw detectors and lasers
		g.setColor(LASERCOLOR);
		for (Detector d : detectors)
		{
			d.draw(g);
		}
	}

	// nested class

	/**
	 * Handles detectors and laser hitboxes.
	 * 
	 * @author denhanh
	 *
	 */
	private class Detector
	{
		// x- and y-coordinates for the detector
		private int detX, detY;

		// laser hitbox
		private Rectangle lHitbox;

		private Detector(int detX, int detY, int laserLength)
		{
			this.detX = detX;
			this.detY = detY;
			lHitbox = new Rectangle(detX + (tileSize - LASERWIDTH) / 2, detY, LASERWIDTH, laserLength);
		}

		private void draw(Graphics2D g)
		{
			g.fill(lHitbox);
			detector.draw(detX, detY, g);
		}

		/**
		 * 
		 * @return true if a laser hitbox connected to this gate is intersected,
		 *         otherwise returns false
		 */
		private boolean laserIsIntersected()
		{
			for (MovableGameObject d : detectableGameObjects)
			{
				if (d.collides(lHitbox))
					return true;
			}

			return false;
		}
	}
}
