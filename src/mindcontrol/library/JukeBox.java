package mindcontrol.library;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class JukeBox
{
	private static HashMap<String, Clip> clips;
	private static int gap;
	private static boolean mute = false;

	public static void init()
	{
		clips = new HashMap<String, Clip>();
		gap = 0;
	}
	
	public static void load(String location, String key)
	{
		// return if the sound is already included in the list.
		if (clips.containsKey(key))
			return;
		
		Clip clip;

		try
		{
			AudioInputStream ais;
			ais = AudioSystem.getAudioInputStream(JukeBox.class.getResourceAsStream(location));

			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, 
					baseFormat.getSampleRate(), 
					16,
					baseFormat.getChannels(), 
					baseFormat.getChannels() * 2, 
					baseFormat.getSampleRate(), 
					false
					);

			AudioInputStream dais = AudioSystem.getAudioInputStream
					(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
			
			clips.put(key, clip);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void play(String key)
	{
		play(key, gap);
	}

	public static void play(String key, int i)
	{
		if(mute)
			return;
		
		Clip c = clips.get(key);
		
		c.stop();
		// Flushes queued data from the line. The flushed data is discarded.
		c.flush();	
		c.setFramePosition(i);		
		c.start();
	}

	public static boolean isPlaying(String key)
	{
		return clips.get(key).isRunning();
	}
	
	public static void stop(String key)
	{
		// Returns if clips is missing 
		if (clips.get(key) == null)
			return;
		if (clips.get(key).isRunning())
			clips.get(key).stop();
	}

	public static void resume(String key)
	{
		// returns if sound is indeed muted.
		if (mute)
			return;
		// returns if sound is already playing. 
		if (clips.get(key).isRunning())
			return;
		
		clips.get(key).start();
	}

	public static void loop(String key)
	{
		loop(key, gap, gap, clips.get(key).getFrameLength() - 1);
	}
	
	public static void loop(String key, int frame)
	{
		loop(key, frame, gap, clips.get(key).getFrameLength() - 1);
	}

	public static void loop(String key, int start, int end)
	{
		loop(key, gap, start, end);
	}

	public static void loop(String key, int frame, int start, int end)
	{
		stop(key);
		// returns if sound is indeed muted.
		if (mute)
			return;
		// Sets start and end of a loop
		clips.get(key).setLoopPoints(start, end);
		// Sets the frame position
		clips.get(key).setFramePosition(frame);
		// Sets how long the loop is looping
		clips.get(key).loop(Clip.LOOP_CONTINUOUSLY);
	}

	public static void setPosition(String key, int frame)
	{
		clips.get(key).setFramePosition(frame);
	}

	public static int getFrames(String key)
	{
		return clips.get(key).getFrameLength();
	}

	public static int getPosition(String key)
	{
		return clips.get(key).getFramePosition();
	}
	
	public static void close(String key)
	{
		stop(key);
		clips.get(key).close();
	}

}
