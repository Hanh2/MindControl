package mindcontrol.gameObjects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.imageio.ImageIO;

import mindcontrol.gameObjects.characters.Player;
import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;
import mindcontrol.managers.Sprite;
import platformer2D.MovableGameObject;
import platformer2D.library.Debugger;

public class LevelGate extends MovableGameObject
{
	// reference to the player
	Player p1;

	// speed
	private static final int LEVELGATESPEED = 120;

	// sprites
	private Sprite pulley, wires, gate;

	// pulley
	private int pulleyXPos, pulleyYPos;

	// scanner
	private LGScanner scanner;

	// opened gate position (one tile below pulley position)
	private int openedGateYPos;

	// state
	private boolean unlocked;
	private int numOfLocks;
	private int keysUsed;

	// level gate lights
	private int lightSize;
	private int lightSpacing;
	private int lightXOffset;
	private int lightYOffset;

	public LevelGate(int x, int y, int numOfLocks, boolean facingRight)
	{
		this.x = x;
		this.y = y;
		this.numOfLocks = numOfLocks;

		init(facingRight);
		reset();
	}

	private void init(boolean facingRight)
	{
		// sprites
		String pulleyLocation = "/Sprites/Map_Objects/Pulley.png";
		String wiresLocation = "/Sprites/Map_Objects/Wire_Ropes.png";
		String gateLocation = "/Sprites/Map_Objects/Level_Gate/Level_Gate.png";
		String scannerLocation = "/Sprites/Map_Objects/Level_Gate/Scanner.png";

		pulley = new Sprite(pulleyLocation);
		wires = new Sprite(wiresLocation);
		gate = new Sprite(gateLocation, scannerLocation, tileSize, 70);
		gate.flipSprite();

		int hbWidth = tileSize;
		int hbHeight = 3 * tileSize;

		// level gate lights
		lightSize = tileSize / 2;
		lightSpacing = hbHeight / 4;
		lightXOffset = (tileSize - lightSize) / 2;
		lightYOffset = tileSize + (lightSpacing - lightSize) / 2;

		// scanner
		scanner = new LGScanner(facingRight);

		if (facingRight)
		{
			setHbOffsets(0, tileSize);
			setSpawn((int) x, (int) y - tileSize);
		}
		else
		{
			int scannerWidth = 3;
			lightXOffset += scannerWidth;
			setHbOffsets(scannerWidth, tileSize);
			setSpawn((int) x - scannerWidth, (int) y - tileSize);
		}

		// pulley
		pulleyXPos = (int) x;
		pulleyYPos = (int) y - 5 * tileSize;

		// opened gate position
		openedGateYPos = pulleyYPos + tileSize;

		// hitbox
		setHitbox(hbWidth, hbHeight);

		// speed
		speed = (double) LEVELGATESPEED / GV.FPS;

		// SFX
		JukeBox.load("/SFX/Lift.mp3", "LevelGate");
	}

	@Override
	public void reset()
	{
		super.reset();
		scanner.reset();

		// reset state
		unlocked = false;
		keysUsed = 0;

		JukeBox.stop("LevelGate");
	}

	public void setPlayer(Player p1)
	{
		this.p1 = p1;
	}

	@Override
	protected void updateMomentum()
	{
		if (dy < 0 && y < openedGateYPos)
		{
			y = openedGateYPos;
			dy = 0;
			unlocked = true;
			JukeBox.stop("LevelGate");
		}
	}

	@Override
	protected void updateSpritePosition()
	{
		y += dy;
	}

	@Override
	public void yPush(double dy)
	{
	}

	@Override
	public void drawHitbox(Graphics2D g)
	{
		super.drawHitbox(g);
		scanner.drawHitbox(g);
	}

	@Override
	public void draw(Graphics2D g)
	{
		// pulley
		pulley.draw(pulleyXPos, pulleyYPos, g);

		// wire ropes
		wires.draw(pulleyXPos, openedGateYPos, tileSize, (int) y - openedGateYPos, g);

		// gate
		gate.draw((int) x, (int) y, g);
		for (int i = 0; i < numOfLocks; i++)
		{
			if (i < keysUsed)
				g.setColor(Color.GREEN);
			else
				g.setColor(Color.RED);

			g.fillOval((int) x + lightXOffset, (int) y + i * lightSpacing + lightYOffset, lightSize, lightSize);
		}

		// scanner light
		scanner.drawLight(g);
	}

	public boolean useBeam(Rectangle beamHitbox)
	{
		if (!unlocked && beamHitbox.intersects(scanner.getHitbox()))
		{
			if (numOfLocks == 0) // success
			{
				scanner.startTimer(true);
				unlocked = true;
				dy = -speed;
			}
			else
			{
				int maxNumOfKeysToGet = numOfLocks - keysUsed;
				int keysRetrieved = p1.getKey(maxNumOfKeysToGet);

				if (keysRetrieved > 0) // success
				{
					scanner.startTimer(true);
					keysUsed += keysRetrieved;

					if (keysUsed >= numOfLocks)
					{
						unlocked = true;
						dy = -speed;
					}
				}
				else // fail
					scanner.startTimer(false);
			}

			// play SFX
			if (unlocked)
				JukeBox.play("LevelGate");
			return true;
		}
		else
			return false;
	}

	// nested class
	/**
	 * Handles the scanner light.
	 * 
	 * @author denhanh
	 *
	 */
	public class LGScanner
	{
		// sprite
		private BufferedImage sImg;
		private final float scannerTransparency = 0.8f;
		// filter for changing the color of the scanner
		private RescaleOp successRop, failRop;

		// hitbox
		private Rectangle sHitbox;

		// The absolute value is how many more frames the scanner will light up.
		// If positive: success
		// If negative: fail
		private int sTimer;
		// total number of frames the scaner will light up
		private int sInterval = GV.SCANNER_INTERVAL_IN_FRAMES;
		private int sXOffset;
		private int sYOffset;

		public LGScanner(boolean facingRight)
		{
			init(facingRight);
		}

		private void init(boolean facingRight)
		{
			int tileSize = GV.TILESIZE;

			// Scanner filters
			float[] sScales = { .2f, 1f, 0f, scannerTransparency };
			float[] sOffsets = new float[4];
			successRop = new RescaleOp(sScales, sOffsets, null);
			float[] fScales = { 1f, .2f, 0f, scannerTransparency };
			float[] fOffsets = new float[4];
			failRop = new RescaleOp(fScales, fOffsets, null);

			// sprite
			try
			{
				sImg = ImageIO.read(getClass().getResourceAsStream("/Sprites/Map_Objects/Level_Gate/Scanner.png"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			int scannerWidth = sImg.getWidth();
			int scannerHeight = sImg.getHeight();

			// offsets
			if (facingRight)
			{
				sXOffset = tileSize;
				sYOffset = 38;
			}
			else
			{
				sXOffset = -scannerWidth;
				sYOffset = 38;

				int sWidth = sImg.getWidth();
				int sHeight = sImg.getHeight();

				// copy sImg
				BufferedImage sImgCopy = new BufferedImage(sWidth, sHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = sImgCopy.createGraphics();
				g.drawImage(sImg, 0, 0, null);
				g.dispose();

				// create graphics for sImg
				Graphics2D g2 = sImg.createGraphics();

				// clear sImg
				g2.setComposite(AlphaComposite.Clear);
				g2.fillRect(0, 0, sWidth, sHeight);
				g2.setComposite(AlphaComposite.Src);

				// draw flipped image onto sImg
				g2.drawImage(sImgCopy, sWidth, 0, -sWidth, sHeight, null);

				// dispose g2
				g2.dispose();
			}

			// hitbox
			sHitbox = new Rectangle((int) x + sXOffset, (int) y + sYOffset, scannerWidth, scannerHeight);

			// SFX
			JukeBox.load("/SFX/Red.mp3", "Red");
			JukeBox.load("/SFX/Green.mp3", "Green");
		}

		public void reset()
		{
			sTimer = 0;
		}

		public void drawLight(Graphics2D g)
		{
			if (sTimer > 0)
			{
				int lgX = getHbLeftX();
				int lgY = getHbTopY();

				g.drawImage(sImg, successRop, lgX + sXOffset, lgY + sYOffset);
				sTimer--;
			}
			else if (sTimer < 0)
			{
				int lgX = getHbLeftX();
				int lgY = getHbTopY();

				g.drawImage(sImg, failRop, lgX + sXOffset, lgY + sYOffset);
				sTimer++;
			}
		}

		public void drawHitbox(Graphics2D g)
		{
			Debugger.drawHitbox(sHitbox, g);
		}

		public Rectangle getHitbox()
		{
			return sHitbox;
		}

		public int getWidth()
		{
			return sImg.getWidth();
		}

		// success if numOfKeys >= 0
		// fail if numOfKeys < 0
		public void startTimer(boolean success)
		{
			if (success)
			{
				JukeBox.play("Green");
				sTimer = sInterval;
			}
			else
			{
				JukeBox.play("Red");
				sTimer = -sInterval;
			}
		}
	}
}