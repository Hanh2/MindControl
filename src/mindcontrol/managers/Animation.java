package mindcontrol.managers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.imageio.ImageIO;

import mindcontrol.library.GV;

public class Animation
{
	/**
	 * enum of nodes
	 * 
	 * @author denhanh
	 *
	 */
	public enum Mode
	{
		LOOP, ALTERNATE
	}

	private BufferedImage[] frames;
	private int lastFrame;

	// size
	private int width;
	private int height;

	// animation speed
	private int updatesPerFrame;

	// keeps track of the current frame
	private int currentFrame;
	private int counter;
	// direction = 1:
	// currentFrame will increment each time counter == updatesPerFrame
	// direction = -1:
	// currentFrame will decrement each time counter == updatesPerFrame
	private int direction;

	// modes
	private Mode mode;

	public Animation(String spriteSheetLocation, int spriteWidth, int spriteHeight)
	{
		this.width = spriteWidth;
		this.height = spriteHeight;

		init(spriteSheetLocation);
	}

	private void init(String spriteSheetLocation)
	{
		// get sprite sheet
		BufferedImage spriteSheet = null;
		try
		{
			spriteSheet = ImageIO.read(getClass().getResourceAsStream(spriteSheetLocation));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// initiate sprites
		int numOfCols = spriteSheet.getWidth() / width;
		int numOfRows = spriteSheet.getHeight() / height;
		int numOfSprites = numOfCols * numOfRows;
		frames = new BufferedImage[numOfSprites];
		lastFrame = numOfSprites - 1;

		// get sprites from sprite sheet
		for (int row = 0; row < numOfRows; row++)
		{
			for (int col = 0; col < numOfCols; col++)
			{
				frames[row * numOfCols + col] = spriteSheet.getSubimage(col * width, row * height, width, height);
			}
		}

		// initiate counter
		counter = 0;
		direction = 1;

		// start at a random frame
		java.util.Random rand = new java.util.Random();
		currentFrame = rand.nextInt(numOfSprites);

		// default mode is to loop
		mode = Mode.LOOP;
	}

	public void resetCounter()
	{
		counter = 0;
	}

	public void drawCurrentFrame(int x, int y, boolean facingRight, Graphics2D g)
	{
		if (facingRight)
			g.drawImage(frames[counter / updatesPerFrame], x, y, null);
		else
			g.drawImage(frames[counter / updatesPerFrame], x + width, y, -width, height, null);
	}

	public void drawCurrentFrame(int x, int y, Graphics2D g)
	{
		g.drawImage(frames[counter / updatesPerFrame], x, y, null);
	}

	public void animate(int x, int y, boolean facingRight, Graphics2D g)
	{
		if (facingRight)
			g.drawImage(frames[currentFrame], x, y, null);
		else
			g.drawImage(frames[currentFrame], x + width, y, -width, height, null);

		switch (mode)
		{
		case LOOP:
			counter = (counter + 1) % updatesPerFrame;
			if (counter == 0)
			{
				if (currentFrame == lastFrame)
					currentFrame = 0;
				else
					currentFrame++;
			}
			break;
		case ALTERNATE:
			counter = (counter + 1) % updatesPerFrame;
			if (counter == 0)
			{
				// change direction if currentFrame is at the end
				if (currentFrame == 0)
					direction = 1;
				else if (currentFrame == lastFrame)
					direction = -1;

				currentFrame += direction;
			}
			break;
		default:
			System.out.println("Default reached in Animation.animate()");
		}
	}

	public void animate(int x, int y, Graphics2D g)
	{
		g.drawImage(frames[currentFrame], x, y, null);

		switch (mode)
		{
		case LOOP:
			counter = (counter + 1) % updatesPerFrame;
			if (counter == 0)
			{
				if (currentFrame == lastFrame)
					currentFrame = 0;
				else
					currentFrame++;
			}
			break;
		case ALTERNATE:
			counter = (counter + 1) % updatesPerFrame;
			if (counter == 0)
			{
				// change direction if currentFrame is at the end
				if (currentFrame == 0)
					direction = 1;
				else if (currentFrame == lastFrame)
					direction = -1;

				currentFrame += direction;
			}
			break;
		default:
			System.out.println("Default reached in Animation.animate()");
		}
	}

	public void animate(int x, int y, RescaleOp itemRop, Graphics2D g)
	{
		g.drawImage(frames[currentFrame], itemRop, x, y);

		switch (mode)
		{
		case LOOP:
			counter = (counter + 1) % updatesPerFrame;
			if (counter == 0)
			{
				if (currentFrame == lastFrame)
					currentFrame = 0;
				else
					currentFrame++;
			}
			break;
		case ALTERNATE:
			counter = (counter + 1) % updatesPerFrame;
			if (counter == 0)
			{
				// change direction if currentFrame is at the end
				if (currentFrame == 0)
					direction = 1;
				else if (currentFrame == lastFrame)
					direction = -1;

				currentFrame += direction;
			}
			break;
		default:
			System.out.println("Default reached in Animation.animate()");
		}
	}

	/**
	 * Adds the image at the specified location to all frames of this animation.
	 * 
	 * @param ImageLocation
	 *            location of the image to add
	 * @param x
	 *            the X coordinate of the position to add the image
	 * @param y
	 *            the Y coordinate of the position to add the image
	 */
	public void addToAllFrames(String imageLocation, int x, int y)
	{
		// get image
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(getClass().getResourceAsStream(imageLocation));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// add imgae to all frames
		for (BufferedImage frame : frames)
		{
			// create graphics
			Graphics2D g = frame.createGraphics();

			// add image
			g.drawImage(img, x, y, null);

			// dispose graphics
			g.dispose();
		}
	}

	public void setUpdatesPerFrame(int updatesPerFrame)
	{
		this.updatesPerFrame = updatesPerFrame;
	}

	public void setAnimationFPS(int fps)
	{
		this.updatesPerFrame = GV.FPS / fps;
	}

	public void setMode(Mode mode)
	{
		this.mode = mode;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getCurrentFrameIndex()
	{
		return currentFrame;
	}
}
