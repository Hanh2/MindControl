package mindcontrol.gameObjects.characters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import mindcontrol.library.GV;
import mindcontrol.library.JukeBox;
import platformer2D.library.Debugger;

public abstract class Animal extends ControllableCharacter
{
	// receiver
	Receiver receiver;

	// state
	protected boolean controlled;

	// action
	protected boolean actionActivated;

	protected Animal(String characterType)
	{
		super(characterType);
		// SFX
		JukeBox.load("/SFX/" + characterType + ".mp3", characterType);
	}

	@Override
	public void reset()
	{
		super.reset();
		receiver.reset();
		
		controlled = false;
	}
	
	public void drawReceiverHitbox(Graphics2D g)
	{
		receiver.drawHitbox(g);
	}

	public boolean mindControl(Ellipse2D.Float controlWaveHb)
	{
		if (controlWaveHb.contains(receiver.getCenterPosition()))
		{
			controlled = true;
			receiver.connect();
			// play SFX
			playAnimalSound();
			return true;
		}
		else
			return false;
	}

	public Point getReceiverPosition()
	{
		return receiver.getCenterPosition();
	}
	
	private void playAnimalSound()
	{
		String animalType = super.getCharacterType();
		
		if(controlled)
		{
			if (animalType == "Elephant")
			{
				JukeBox.play("Elephant");
			}			
		}
	}


	// nested class
	/**
	 * Handles the receiver.
	 * 
	 * @author denhanh
	 *
	 */
	protected class Receiver
	{
		// offsets
		private int rFacingLeftXOffset, rActionFacingLeftXOffset;
		private int rFacingRightXOffset, rActionFacingRightXOffset;
		private int rYOffset, rActionYOffset;

		// radius
		private int rRadius = GV.RECEIVER_RADIUS;
		private int rDiameter = 2 * rRadius + 1;

		// number of frames the receiver is lit
		private int rInterval = GV.RECEIVER_INTERVAL_IN_FRAMES;

		// the absolute value is how many more frames the receiver will be lit.
		// if positive: connect
		// if negative: disconnect
		private int rTimer;

		// colors
		private Color connectColor = new Color(0f, 1f, 0f, 0.4f);
		private Color disconnectColor = new Color(1f, 0f, 0f, 0.4f);

		protected Receiver(int facingLeftXOffset, int facingRightXOffset, int yOffset, int actionFacingLeftXOffset,
				int actionFacingRightXOffset, int actionYOffset)
		{
			this.rFacingLeftXOffset = facingLeftXOffset;
			this.rFacingRightXOffset = facingRightXOffset;
			this.rYOffset = yOffset;
			this.rActionFacingLeftXOffset = actionFacingLeftXOffset;
			this.rActionFacingRightXOffset = actionFacingRightXOffset;
			this.rActionYOffset = actionYOffset;
		}

		public void reset()
		{
			rTimer = 0;
		}

		public void draw(Graphics2D g)
		{
			if (rTimer > 0)
			{
				Point p = getCenterPosition();

				g.setColor(connectColor);
				g.fillOval(p.x - rRadius, p.y - rRadius, rDiameter, rDiameter);

				rTimer--;
			}
			else if (rTimer < 0)
			{
				Point p = getCenterPosition();

				g.setColor(disconnectColor);
				g.fillOval(p.x - rRadius, p.y - rRadius, rDiameter, rDiameter);

				rTimer++;
			}
		}

		public void connect()
		{
			rTimer = rInterval;
		}

		public void disconnect()
		{
			rTimer = -rInterval;
		}

		public Point getCenterPosition()
		{
			int rX = (int) x;
			int rY = (int) y;

			if (actionActivated)
			{
				if (facingRight)
					rX += rActionFacingRightXOffset;
				else
					rX += rActionFacingLeftXOffset;

				rY += rActionYOffset;
			}
			else
			{
				if (facingRight)
					rX += rFacingRightXOffset;
				else
					rX += rFacingLeftXOffset;

				rY += rYOffset;
			}

			return new Point(rX, rY);
		}

		public void drawHitbox(Graphics2D g)
		{
			Point p = getCenterPosition();
			Debugger.drawHitbox(new Rectangle(p.x, p.y, 0, 0), g);
		}
	}
}
