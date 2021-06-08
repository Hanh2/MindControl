package mindcontrol.gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.RescaleOp;

import mindcontrol.gameObjects.characters.Player;
import mindcontrol.managers.Animation;
import mindcontrol.managers.Sprite;
import platformer2D.GameObject;

public abstract class Hologram extends GameObject
{
	// reference to the player
	Player p1;

	// sprites and animations
	private Sprite base;
	private Animation item;

	// light
	private String type;
	private Color hologramColor = new Color(.05f, .7f, .05f, .4f);
	private Polygon hologramLight;

	// Base
	private int baseXPos, baseYPos;

	// item
	private final float itemTransparency = 0.5f;
	// filter for making the item transparent
	RescaleOp itemRop;
	private int itemXPos, itemYPos;

	private boolean active;

	protected Hologram(int baseXPos, int baseBottomYPos, String type)
	{
		this.baseXPos = baseXPos;
		this.baseYPos = baseBottomYPos; // will be adjusted in init()
		this.type = type;

		init();
		reset();
	}

	private void init()
	{
		// sprites
		String baseLocation = "/Sprites/Map_Objects/Holograms/Hologram_Base.png";
		String itemLocation = "/Sprites/Map_Objects/Holograms/Hologram_" + type + ".png";
		base = new Sprite(baseLocation);
		item = new Animation(itemLocation, 30, 20);
		item.setAnimationFPS(4);
		
		item.setMode(Animation.Mode.ALTERNATE);

		int baseWidth = base.getWidth();
		int baseHeight = base.getHeight();
		int itemWidth = item.getWidth();
		int itemHeight = item.getHeight();

		int centerX = baseXPos + baseWidth / 2;

		// hitbox: 1x1 tiles
		setHitbox(baseXPos, baseYPos - tileSize, tileSize, tileSize);

		// adjust baseYPos
		baseYPos -= baseHeight;

		// item position
		itemXPos = centerX - itemWidth / 2;
		itemYPos = baseYPos - tileSize - 5 * itemHeight / 8;

		// light polygon
		int topY = baseYPos - 5 * tileSize / 3;
		int bottomY = baseYPos + 3;
		int threeQuartersTileSize = 3 * tileSize / 4;
		int[] xArray = { centerX - threeQuartersTileSize, centerX + threeQuartersTileSize, baseXPos + 23,
				baseXPos + 8 };
		int[] yArray = { topY, topY, bottomY, bottomY };
		hologramLight = new Polygon(xArray, yArray, 4);

		float[] scales = { 1f, 1f, 1f, itemTransparency };
		float[] offsets = new float[4];
		itemRop = new RescaleOp(scales, offsets, null);
	}

	public void reset()
	{
		active = true;
	}

	public void setPlayer(Player p1)
	{
		this.p1 = p1;
	}

	protected abstract void action();

	public void update()
	{
		if (active && p1.contains(getHitbox()))
		{
			active = false;
			action();
		}
	}

	@Override
	public void draw(Graphics2D g)
	{
		// draw base
		base.draw(baseXPos, baseYPos, g);

		// draw item and hologram light if active
		if (active)
		{
			item.animate(itemXPos, itemYPos, itemRop, g);

			g.setColor(hologramColor);
			g.fill(hologramLight);
		}
	}

}