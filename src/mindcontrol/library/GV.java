package mindcontrol.library;

/**
 * Contains all the global values (short: GV).
 * 
 * @author Jerry
 *
 */
public final class GV
{

	private GV()
	{
	}

	public static String GAME_NAME = "MIND CONTROL";// Temporary?

	public static int VIEWPORTWIDTH = 800;
	public static int VIEWPORTHEIGHT = 500;
	public static int TILESIZE = 32; // the tiles are 32x32 pixels.
	public static int FPS = 60;

	// BEAM
	public static double ROBOT_BEAM_INTERVAL_IN_SECONDS = 0.7;
	public static int ROBOT_BEAM_INTERVAL_IN_FRAMES = (int) (ROBOT_BEAM_INTERVAL_IN_SECONDS * FPS);

	// CONNECTION WAVE
	// how far the connection wave reaches (radius of the circle in pixels)
	public static int CONTROL_WAVE_REACH = (int) (4 * TILESIZE);
	public static double CONTROL_WAVE_INTERVAL_IN_SECONDS = 0.7;
	public static int CONTROL_WAVE_INTERVAL_IN_FRAMES = (int) (CONTROL_WAVE_INTERVAL_IN_SECONDS * FPS);

	// RECEIVER
	public static int RECEIVER_RADIUS = 2;
	public static double RECEIVER_INVERVAL_IN_SECONDS = 0.7;
	public static int RECEIVER_INTERVAL_IN_FRAMES = (int) (RECEIVER_INVERVAL_IN_SECONDS * FPS);

	// PATH GATE
	// how many times longer the expanded gate is compared to its minimum length;
	public static int PATH_GATE_EXPANSION_RATIO = 4;

	// LEVEL GATE
	public static double SCANNER_INTERVAL_IN_SECONDS = 0.7;
	public static int SCANNER_INTERVAL_IN_FRAMES = (int) (SCANNER_INTERVAL_IN_SECONDS * FPS);

	// draw hitboxes if true;
	public static boolean DEBUGGING = false;
	public static int SPEED_MULTIPLIER = 1;
	
	// MAX_FALL_SPEED should never be faster than 1 tile size per frame
	// TILESIZE * 20 / FPS is a good speed for playing
	// TILESIZE * 5 / FPS if a good speed for debugging
	public static double MAX_FALL_SPEED = (TILESIZE * (double) 50 / FPS);
	public static double GRAVITY = .8;

	public static int NUMBEROFLEVELS = 6;
	public static int STARTLEVEL = 1;
}
