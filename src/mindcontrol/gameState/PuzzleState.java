package mindcontrol.gameState;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mindcontrol.gameObjects.*;
import mindcontrol.gameObjects.characters.Elephant;
import mindcontrol.gameObjects.characters.Player;
import mindcontrol.library.GV;
import mindcontrol.library.Keys;
import mindcontrol.managers.*;
import platformer2D.Background;

import java.awt.Color;

public class PuzzleState extends GameState
{
	private int viewPortWidth = GV.VIEWPORTWIDTH;
	private int viewPortHeight = GV.VIEWPORTHEIGHT;

	private Color bgColor = new Color(90, 90, 80);

	// Pause menu
	private int currentChoice = 0;
	// private String[] menuOptions = { "Resume", "Restart", "Achievements", "Menu"
	// };
	private String[] menuOptions = { "Resume", "Restart", "Menu" };// enum?
	private Font menuFont;
	private float pauseMenuTransparency = 0.05f;
	private int fontSize;

	// puzzle manager
	private PuzzleManager pm;

	// pause
	private boolean pause;

	// menu items
	private int[] menuItemXPos;
	private int menuItemsYOffset;

	public PuzzleState(GameStateManager gsm)
	{
		this.gsm = gsm;

		init();
	}

	@Override
	public void init()
	{
		pm = new PuzzleManager(this);

		// Loads the current level.
		readMap();

		// sets up connections between the puzzle manager and the map objects that needs
		// information about the map
		pm.setConnections();

		// pause menu
		menuFont = new Font("Arial", Font.PLAIN, viewPortHeight / 10);

		pause = false;

		fontSize = menuFont.getSize();
		int numOfOptions = menuOptions.length;
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

		// menu items
		menuItemXPos = new int[numOfOptions];
		for (int i = 0; i < numOfOptions; i++)
		{
			menuItemXPos[i] = (viewPortWidth - (int) menuFont.getStringBounds(menuOptions[i], frc).getWidth()) / 2;
		}
		menuItemsYOffset = viewPortHeight / 2 - fontSize * numOfOptions / 4;
	}

	/**
	 * Reads the map from file.
	 */
	private void readMap()
	{
		Background bg = null;

		try
		{
			InputStream in = getClass().getResourceAsStream("/Levels/level" + gsm.getCurrentLevel() + ".map");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			int tileSize = GV.TILESIZE;

			// map dimensions
			int numRows = Integer.parseInt(br.readLine());
			int numCols = Integer.parseInt(br.readLine());
			int width = numCols * tileSize;
			int height = numRows * tileSize;

			pm.setMapSize(width, height);

			// sets up the map tile matrix
			char[][] map = new char[numRows][numCols];
			for (int row = 0; row < numRows; row++)
			{
				String line = br.readLine();
				map[row] = line.toCharArray();
			}

			// remove one line (was used for readability)
			br.readLine();

			// sets up the background tile matrix
			char[][] bgTMat = new char[numRows][numCols];
			for (int row = 0; row < numRows; row++)
			{
				String line = br.readLine();
				bgTMat[row] = line.toCharArray();
			}

			// initialize background
			bg = new Background(width, height, bgColor);

			// keeps track of the direction of characters when spawned
			boolean facingRight;

			// For counting tiles.
			int widthInTiles = 1;
			int heightInTiles = 1;

			// creates the game objects
			for (int row = 0; row < numRows; row++)
			{
				for (int col = 0; col < numCols; col++)
				{

					// upper left corner of the current tile.
					int tileXPos = col * tileSize;
					int tileYPos = row * tileSize;

					switch (map[row][col])
					{
					case 'W': // Wall

						// reset counters
						widthInTiles = 1;
						heightInTiles = 1;

						// Counts the number of tiles wide the wall is.
						while (col + 1 < numCols && map[row][col + 1] == '=')
						{
							widthInTiles++;
							col++;
						}
						// Count number of tiles high the wall is.
						while (row + heightInTiles < numRows && map[row + heightInTiles][col] == '|')
						{
							heightInTiles++;
						}

						// add hitbox to background
						int wallWidth = widthInTiles * tileSize;
						int wallHeight = heightInTiles * tileSize;
						bg.addHitbox(tileXPos, tileYPos, wallWidth, wallHeight);

						// create wall image
						BufferedImage wallImg = new BufferedImage(wallWidth, wallHeight, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = wallImg.createGraphics();
						g.setColor(Color.BLACK);
						g.fillRect(0, 0, wallWidth, wallHeight);
						g.dispose();

						// add wall image to background
						bg.addImage(wallImg, tileXPos, tileYPos);
						break;
					case 'p': // platform
						// reset counters
						widthInTiles = 1;
						heightInTiles = 1;

						// Counts the number of tiles wide the wall is.
						while (col + 1 < numCols && map[row][col + 1] == '=')
						{
							widthInTiles++;
							col++;
						}
						// Count number of tiles high the wall is.
						while (row + heightInTiles < numRows && map[row + heightInTiles][col] == '|')
						{
							heightInTiles++;
						}

						// add hitbox to background
						int platformWidth = widthInTiles * tileSize;
						int platformHeight = heightInTiles * tileSize;
						bg.addHitbox(tileXPos, tileYPos, platformWidth, platformHeight);

						// create platform image (temporary)
						BufferedImage platformImg = new BufferedImage(platformWidth, platformHeight,
								BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2 = platformImg.createGraphics();
						g2.setColor(Color.cyan);
						g2.fillRect(0, 0, platformWidth, platformHeight);
						g2.dispose();

						// add wall image to background
						bg.addImage(platformImg, tileXPos, tileYPos);
						break;
					case 'P':
						facingRight = true;
						if (row - 1 >= 0 && map[row - 1][col] == 'l')
							facingRight = false;

						Player player = new Player(tileXPos, tileYPos - tileSize, facingRight);

						// set player
						pm.setPlayer(player);
						break;
					case 'E':
						facingRight = true;
						if (row - 1 >= 0 && map[row - 1][col] == 'l')
							facingRight = false;

						Elephant elephant = new Elephant(tileXPos, tileYPos - 3 * tileSize, facingRight);

						// add elephant
						pm.addAnimal(elephant);
						break;
					case 'G':
						facingRight = true;
						if (row - 1 > 0 && map[row - 1][col] == 'l')
							facingRight = false;

						int numberOfLocks = Character.getNumericValue(map[row + 1][col]);

						LevelGate levelGate = new LevelGate(tileXPos, tileYPos, numberOfLocks, facingRight);

						// add level gate
						pm.addLevelGate(levelGate);
						break;
					case 'k':
						KeyHologram key = new KeyHologram(tileXPos + tileSize / 2, tileYPos + tileSize);

						// add key
						pm.addHologram(key);
						break;
					case 'g': // path gate
						// The number of tiles high the path gate is is given by map[row+2][col].
						heightInTiles = Character.getNumericValue(map[row + 2][col]);

						// create path gate
						PathGate pathGate = new PathGate(tileXPos, tileYPos, heightInTiles * tileSize);

						char detectorSign = map[row + 1][col];
						int laserXPos = 0;
						int laserYPos = 0;
						int laserLengthInTiles;

						// search through the map for the detectorSign to place the
						// detectors.
						for (int r = 0; r < map.length; r++)
						{
							for (int c = 0; c < map[r].length; c++)
							{
								if (map[r][c] == detectorSign)
								{
									// ignore the laserSign if it is right below the path gate sign.
									if (r > 0 && map[r - 1][c] == 'g')
										continue;

									laserXPos = c * tileSize;
									laserYPos = r * tileSize;
									laserLengthInTiles = 1;

									char temp = map[r + laserLengthInTiles][c];

									while (temp != '=' && temp != 'W' && temp != '/')
									{
										laserLengthInTiles++;
										temp = map[r + laserLengthInTiles][c];
									}

									pathGate.addDetector(laserXPos, laserYPos, laserLengthInTiles * tileSize);
								}
							}
						}

						// add path gate
						pm.addPathGate(pathGate);
						break;
					case 'H':
						HeavyBox heavyBox = new HeavyBox(tileXPos, tileYPos - tileSize);

						// add heavy box
						pm.addBox(heavyBox);
						break;

					case 'L':
						heightInTiles = 1;
						// holds the positions where the lift will make stops
						// measured in tiles from the pulley.
						ArrayList<Integer> tileStopPoints = new ArrayList<Integer>();
						// holds the number of the stop where the lift will
						// start. (The stop at the start is 1, the next is 2 and
						// so on.)
						int startingStop = 0;
						while (map[row + heightInTiles][col] != 'X')
						{
							if (map[row + heightInTiles][col] == 'o')
								tileStopPoints.add(heightInTiles);
							if (map[row + heightInTiles][col] == 'O')
							{
								startingStop = tileStopPoints.size();
								tileStopPoints.add(heightInTiles);
							}
							heightInTiles++;
						}

						// calculate stop points
						int[] stopPoints = new int[tileStopPoints.size()];
						for (int i = 0; i < stopPoints.length; i++)
						{
							stopPoints[i] = tileYPos + tileStopPoints.get(i) * tileSize;
						}

						// create elevator
						Elevator elevator = new Elevator(tileXPos, tileYPos, stopPoints, startingStop);

						// add elevator
						pm.addElevator(elevator);
						break;
					case 'S': // Shelf
						int shelfY = tileYPos - 2 * tileSize;
						Shelf shelf = new Shelf(tileXPos, shelfY);

						bg.addImage(shelf.getImage(), tileXPos, shelfY);
						pm.addShelf(shelf);
						break;
					case '#':
						CheckPoint checkPoint = new CheckPoint(tileXPos, tileYPos - 2 * tileSize);

						// set check point
						pm.setCheckPoint(checkPoint);
						break;
					}
				}
			}

			// draw platforms onto background
			try
			{
				// @formatter:off
				// u: up 	v: vertical		d: down
				// l: left	h: horizontal	r: right
				// s: single
				//
				// -------
				// |123|u|
				// |456|v|
				// |789|d|
				// -------
				// |lhr|s|
				// -------
				// @formatter:on
				BufferedImage tileset = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tileset.png"));

				for (int row = 0; row < numRows; row++)
				{
					for (int col = 0; col < numCols; col++)
					{
						// upper left corner of the object.
						int tileXPos = col * tileSize;
						int tileYPos = row * tileSize;

						switch (bgTMat[row][col])
						{
						case 'l':
							BufferedImage leftImage = tileset.getSubimage(0, 3 * tileSize, tileSize, tileSize);
							bg.addImage(leftImage, tileXPos, tileYPos);
							break;
						case 'h':
							BufferedImage horizontalImage = tileset.getSubimage(tileSize, 3 * tileSize, tileSize,
									tileSize);
							bg.addImage(horizontalImage, tileXPos, tileYPos);
							break;
						case 'r':
							BufferedImage rightImage = tileset.getSubimage(2 * tileSize, 3 * tileSize, tileSize,
									tileSize);
							bg.addImage(rightImage, tileXPos, tileYPos);
							break;
						case 'u':
							BufferedImage upImage = tileset.getSubimage(3 * tileSize, 0, tileSize, tileSize);
							bg.addImage(upImage, tileXPos, tileYPos);
							break;
						case 'v':
							BufferedImage verticalImage = tileset.getSubimage(3 * tileSize, tileSize, tileSize,
									tileSize);
							bg.addImage(verticalImage, tileXPos, tileYPos);
							break;
						case 'd':
							BufferedImage downImage = tileset.getSubimage(3 * tileSize, 2 * tileSize, tileSize,
									tileSize);
							bg.addImage(downImage, tileXPos, tileYPos);
							break;
						case 's':
							BufferedImage singleImage = tileset.getSubimage(3 * tileSize, 3 * tileSize, tileSize,
									tileSize);
							bg.addImage(singleImage, tileXPos, tileYPos);
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		/////////

		pm.setBackground(bg);
	}

	private void select()
	{
		switch (menuOptions[currentChoice])
		{
		case "Resume":
			pause = false;
			break;
		case "Restart":
			pause = false;
			pm.restart();
			break;
		case "Achievements":
			break;
		case "Menu":
			this.pm = null;
			gsm.setState(GameStateManager.MAINMENUSTATE);
			break;
		default:
		}
	}

	public void nextLevel()
	{
		gsm.nextLevel();
	}

	private void menuUpdate()
	{
		if (Keys.keyState[Keys.ENTER] && !Keys.prevKeyState[Keys.ENTER])
		{
			select();
		}
		if (Keys.keyState[Keys.UP] && !Keys.prevKeyState[Keys.UP])
		{
			currentChoice--;
			if (currentChoice == -1)
				currentChoice = menuOptions.length - 1;
		}
		if (Keys.keyState[Keys.DOWN] && !Keys.prevKeyState[Keys.DOWN])
		{
			currentChoice++;
			if (currentChoice == menuOptions.length)
				currentChoice = 0;
		}
	}

	@Override
	public void update()
	{
		// Toggles pause if the pause key is pressed
		if (Keys.keyState[Keys.PAUSE] && !Keys.prevKeyState[Keys.PAUSE])
		{
			currentChoice = 0;
			pause = !pause;
		}

		if (!pause)
			pm.update();
		else
		{
			menuUpdate();
		}
	}

	private void drawMenu(Graphics2D g)
	{
		g.setFont(menuFont);

		for (int i = 0; i < menuOptions.length; i++)
		{
			if (i == currentChoice)
			{
				g.setColor(new Color(255, 0, 0));
				g.drawString(menuOptions[i], menuItemXPos[i], menuItemsYOffset + i * fontSize);
			}
			else
			{
				g.setColor(Color.DARK_GRAY);
				g.drawString(menuOptions[i], menuItemXPos[i], menuItemsYOffset + i * fontSize);
			}
		}
	}

	@Override
	public void draw(Graphics2D g)
	{
		if (!pause)
		{
			pm.draw(g);
		}
		else // pause menu
		{
			g.setColor(new Color(.1f, .2f, .1f, pauseMenuTransparency));
			g.fillRect(0, 0, viewPortWidth, viewPortHeight);

			drawMenu(g);
		}
	}

}
