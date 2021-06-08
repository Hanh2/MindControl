package mindcontrol.managers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import mindcontrol.gameObjects.*;
import mindcontrol.gameObjects.characters.*;
import mindcontrol.gameState.PuzzleState;
import mindcontrol.library.GV;
import mindcontrol.view.Camera;
import platformer2D.Background;
import platformer2D.GameObject;
import platformer2D.MovableGameObject;

public class PuzzleManager
{
	// reference to the puzzle state
	PuzzleState puzzleState;

	// view port and map size
	private int mapWidth;
	private int mapHeight;
	private int viewPortWidth = GV.VIEWPORTWIDTH;
	private int viewPortHeight = GV.VIEWPORTHEIGHT;

	// handles fade in and fade out of the curtain
	private float curtainAlpha;
	private float dA;

	// Camera
	private Camera camera;

	// HUD
	private HUD hud;

	// background
	Background bg;

	// the player (the robot) is always placed at position 0.
	private ArrayList<ControllableCharacter> characters;
	private ArrayList<HeavyBox> boxes;
	private ArrayList<Hologram> holograms;
	private ArrayList<Elevator> elevators;
	private ArrayList<PathGate> pathGates;
	private ArrayList<LevelGate> levelGates;
	private ArrayList<Shelf> shelves;
	private CheckPoint checkPoint;

	private int currentElevator; // -1 when no elevator is moving
	private boolean debugging = GV.DEBUGGING;

	public PuzzleManager(PuzzleState puzzleState)
	{
		this.puzzleState = puzzleState;

		init();
	}

	public void init()
	{
		GameObject.setTileSize(GV.TILESIZE);
		MovableGameObject.setGravity(GV.GRAVITY);
		MovableGameObject.setMaxFallSpeed(GV.MAX_FALL_SPEED);

		characters = new ArrayList<ControllableCharacter>();
		boxes = new ArrayList<HeavyBox>();
		holograms = new ArrayList<Hologram>();
		elevators = new ArrayList<Elevator>();
		pathGates = new ArrayList<PathGate>();
		levelGates = new ArrayList<LevelGate>();
		shelves = new ArrayList<Shelf>();

		hud = new HUD();
		camera = new Camera(hud);

		currentElevator = -1;

		curtainAlpha = 1f;
		dA = -0.02f;
	}

	public void restart()
	{
		for (ControllableCharacter character : characters)
			character.reset();

		for (Hologram h : holograms)
			h.reset();

		for (HeavyBox box : boxes)
			box.reset();

		for (PathGate pg : pathGates)
			pg.reset();

		for (LevelGate lg : levelGates)
			lg.reset();

		for (Elevator elevator : elevators)
			elevator.reset();

		setCurrentElevator(-1);

		camera.setTargetCharacter(getPlayer());

		curtainAlpha = 1f;
		dA = -0.02f;
	}

	public void setMapSize(int width, int height)
	{
		this.mapWidth = width;
		this.mapHeight = height;
	}

	public void setConnections()
	{
		ControllableCharacter.setPm(this);
		MovableGameObject.setBackground(bg);

		for (HeavyBox box : boxes)
		{
			box.setConnections(this);
		}

		for (Hologram hologram : holograms)
		{
			hologram.setPlayer(getPlayer());
		}

		for (Elevator elevator : elevators)
		{
			elevator.setConnections(this);
		}

		for (PathGate pg : pathGates)
		{
			pg.setConnections(this);
		}

		for (LevelGate lg : levelGates)
		{
			lg.setPlayer((Player) characters.get(0));
		}

		checkPoint.setPlayer(getPlayer());

		camera.setTargetCharacter(characters.get(0));
		camera.setBounds(mapWidth, mapHeight);
		
		hud.setPlayer(getPlayer());
	}

	//////////////////////////////////////////////
	// Methods for retrieving objects (getters) //
	//////////////////////////////////////////////
	public Player getPlayer()
	{
		Player p = null;

		try
		{
			p = (Player) characters.get(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return p;
	}

	public ArrayList<Animal> getAnimals()
	{
		ArrayList<Animal> animals = new ArrayList<Animal>();

		for (int i = 1; i < characters.size(); i++)
		{
			animals.add((Animal) characters.get(i));
		}

		return animals;
	}
	
	public ArrayList<HeavyBox> getBoxes()
	{
		return new ArrayList<HeavyBox>(boxes);
	}

	public ArrayList<LevelGate> getLevelGates()
	{
		return levelGates;
	}

	public ArrayList<Hologram> getHolograms()
	{
		return new ArrayList<Hologram>(holograms);
	}

	public ArrayList<Elevator> getElevators()
	{
		return new ArrayList<Elevator>(elevators);
	}

	public ArrayList<GameObject> getGates()
	{
		ArrayList<GameObject> gates = new ArrayList<GameObject>(pathGates);
		gates.addAll(levelGates);

		return gates;
	}

	public ArrayList<Shelf> getShelves()
	{
		return new ArrayList<Shelf>(shelves);
	}

	public Camera getCamera()
	{
		return camera;
	}
	
	public HUD getHUD()
	{
		return hud;
	}
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
	// Methods for retrieving objects (getters) //
	//////////////////////////////////////////////

	/////////////////////////////////////////////////////////
	// Methods for setting up the map (setters and adders) //
	/////////////////////////////////////////////////////////
	public void setBackground(Background bg)
	{
		this.bg = bg;
	}

	public void setPlayer(Player player)
	{
		if (!characters.isEmpty() && characters.get(0).getCharacterType() == "Robot")
			characters.set(0, player);
		else
			characters.add(0, player);
	}

	public void addAnimal(Animal animal)
	{
		characters.add(animal);
	}

	public void addHologram(Hologram hologram)
	{
		holograms.add(hologram);
	}

	public void addBox(HeavyBox box)
	{
		boxes.add(box);
	}

	public void addElevator(Elevator elevator)
	{
		elevators.add(elevator);
	}

	public void addPathGate(PathGate pathGate)
	{
		pathGates.add(pathGate);
	}

	public void addLevelGate(LevelGate levelGate)
	{
		levelGates.add(levelGate);
	}

	public void addShelf(Shelf shelf)
	{
		shelves.add(shelf);
	}

	public void setCheckPoint(CheckPoint checkPoint)
	{
		this.checkPoint = checkPoint;
	}
	/// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^///
	// Methods for setting up the map //
	////////////////////////////////////

	//////////////////////////////////////
	// Methods for manipulating control //
	//////////////////////////////////////
	public void setCurrentElevator(int currentElevator)
	{
		this.currentElevator = currentElevator;

		if (currentElevator < 0)
		{
			for (ControllableCharacter ch : characters)
			{
				ch.setInElevator(false);
			}
		}
		else
		{
			for (ControllableCharacter ch : characters)
			{
				ch.setInElevator(true);
			}
		}
	}

	private void alphaUpdate()
	{
		// change alpha
		curtainAlpha += dA;

		// fade out
		if (dA < 0)
		{
			if (curtainAlpha < 0)
			{
				curtainAlpha = 0;
				dA = 0;
			}
		}
		else // zero alpha and fade in
		{
			if (curtainAlpha > 1)
			{
				puzzleState.nextLevel();
			}
		}
	}

	public void update()
	{
		// only update when curtainAlpha is 0
		if (curtainAlpha != 0f)
		{
			alphaUpdate();
		}
		else
		{
			if (checkPoint.atCheckPoint())
			{
				dA = 0.02f;
				curtainAlpha += dA;
			}

			if (currentElevator >= 0)
			{
				elevators.get(currentElevator).update();
			}

			for (ControllableCharacter character : characters)
				character.update();

			for (MovableGameObject box : boxes)
			{
				box.update();
			}

			for (Hologram h : holograms)
			{
				h.update();
			}

			for (PathGate pg : pathGates)
			{
				pg.update();
			}

			for (LevelGate lg : levelGates)
			{
				lg.update();
			}

			// update camera
			camera.update();
		}
	}

	private void drawHitboxes(Graphics2D g)
	{
		// draw player hitbox
		characters.get(0).drawHitbox(g);

		// draw animal hitboxes
		for (int i = 1; i < characters.size(); i++)
		{
			characters.get(i).drawHitbox(g);
			((Animal) characters.get(i)).drawReceiverHitbox(g);
		}

		// draw elevator hitboxes
		for (Elevator el : elevators)
		{
			el.drawHitbox(g);
		}

		// draw bg hitboxes
		bg.drawHitboxes(g);

		// draw hologram hitboxes
		for (Hologram h : holograms)
		{
			h.drawHitbox(g);
		}

		// draw heavy box hitboxes
		for (HeavyBox box : boxes)
		{
			box.drawHitbox(g);
		}

		// draw gate hitboxes
		for (GameObject gate : pathGates)
		{
			gate.drawHitbox(g);
		}

		// draw level gate hitboxes
		for (LevelGate lg : levelGates)
		{
			lg.drawHitbox(g);
		}

		// draw shelf hitboxes
		for (Shelf sh : shelves)
		{
			sh.drawHitbox(g);
		}

		checkPoint.drawHitbox(g);
	}

	public void draw(Graphics2D g)
	{
		// clear screen
		g.clearRect(0, 0, viewPortWidth, viewPortHeight);
		Graphics2D gCopy = (Graphics2D) g.create();

		try
		{
			// translates the origin of gCopy so that it matches the cameras position
			camera.translateOrigin(gCopy);

			// draw background
			bg.draw(gCopy);

			// draw boxes
			for (HeavyBox box : boxes)
			{
				box.draw(gCopy);
			}

			// draw animals
			for (int i = 1; i < characters.size(); i++)
			{
				characters.get(i).draw(gCopy);
			}

			// draw player
			characters.get(0).draw(gCopy);

			// draw gates
			for (GameObject gate : pathGates)
			{
				gate.draw(gCopy);
			}

			for (LevelGate gate : levelGates)
			{
				gate.draw(gCopy);
			}

			// draw passable stationary objects
			for (Hologram h : holograms)
			{
				h.draw(gCopy);
			}

			// draw elevators
			for (Elevator el : elevators)
			{
				el.draw(gCopy);
			}

			checkPoint.draw(gCopy);

			// draw hitboxes
			if (debugging)
			{
				drawHitboxes(gCopy);
				// ((Player) characters.get(0)).printNumOfKeys(gCopy);
			}

			// g and not gCopy: the HUD and the curtain moves along with the camera
			// draw HUD
			hud.draw(g);

			// draw curtain
			if (curtainAlpha != 0)
			{
				g.setColor(new Color(0f, 0f, 0f, curtainAlpha));
				g.fillRect(0, 0, viewPortWidth, viewPortHeight);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			gCopy.dispose();
		}
	}
}
