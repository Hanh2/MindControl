package mindcontrol.view;

import java.awt.Graphics2D;

import mindcontrol.gameObjects.characters.ControllableCharacter;
import mindcontrol.library.GV;
import mindcontrol.managers.HUD;

/*
 * TODO:
 * 
 * zoom?
 */
public class Camera
{
	// reference to the hud
	private HUD hud;
	
	// reference to the character to target
	private ControllableCharacter targetCharacter;

	private int tileSize = GV.TILESIZE;

	// Viewport
	private int viewPortWidth = GV.VIEWPORTWIDTH;
	private int viewPortHeight = GV.VIEWPORTHEIGHT;
	private int viewPortHalfWidth = viewPortWidth / 2;
	private int viewPortHalfHeight = viewPortHeight / 2;

	// Camera position
	private double x;
	private double y;
	private double targetX;
	private double targetY;
	private double lookAheadFactor = 2 * tileSize;

	//
	//@formatter:off
	// bounds
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	//*/
	//@formatter:on

	// Tweening
	private double xTween = 0.03;
	private double yTween = 0.02;

	public Camera(HUD hud)
	{
		this.hud = hud;
	}

	public void setBounds(int mapWidth, int mapHeight)
	{
		//@formatter:off
		//
		if (mapWidth < viewPortWidth)
		{
			x = (mapWidth-viewPortWidth)/2;
			
			minX = maxX = (int) x;
		}
		else
		{
			minX = 0;
			maxX = mapWidth - viewPortWidth;
		}
		
		if (mapHeight < viewPortHeight)
		{
			y = (mapHeight-viewPortHeight)/2;
			
			minY = maxY = (int) y;
		}
		else
		{
			minY = 0;
			maxY = mapHeight - viewPortHeight;
		}
		//*/
		//@formatter:on
	}

	/**
	 * Makes sure that the camera stays within its bounds.
	 */
	private void checkBounds()
	{
		//
		//@formatter:off
		if (targetX < minX)
			targetX = minX;
		else if (targetX > maxX)
			targetX = maxX;

		if (targetY < minY)
			targetY = minY;
		else if (targetY > maxY)
			targetY = maxY;
		//*/
		//@formatter:on
	}

	private void updateTargetPosition()
	{
		targetX = targetCharacter.getCenterX() - viewPortHalfWidth;
		targetY = targetCharacter.getCenterY() - viewPortHalfHeight;

		if (targetCharacter.isFacingRight())
			targetX += lookAheadFactor;
		else
			targetX -= lookAheadFactor;

		checkBounds();
	}

	private void updatePosition()
	{
		double targetDistanceX = targetX - x;
		double targetDistanceY = targetY - y;

		double dx = targetDistanceX * xTween;
		double dy = targetDistanceY * yTween;

		x += dx;
		y += dy;
	}

	public void update()
	{
		updateTargetPosition();
		updatePosition();
	}

	public void translateOrigin(Graphics2D g)
	{
		// Math.floor(x) and Math.floor(y) makes sure that the cameras position is in
		// the same pixel as the top left corner of the screen.
		int gDx = -(int) Math.floor(x);
		int gDy = -(int) Math.floor(y);

		g.translate(gDx, gDy);
	}

	public void setTargetCharacter(ControllableCharacter targetCharacter)
	{
		this.targetCharacter = targetCharacter;
		hud.setTargetCharacter(targetCharacter);
	}
}