package bap.texas.util;

import java.util.HashMap;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;



public class SoundManager {
	
	private  SoundPool mSoundPool; 
	private  HashMap<Integer, Integer> mSoundPoolMap; 
	private  AudioManager  mAudioManager;
	private  Context mContext;
	public static final int button 		= 1;
	public static final int bonus  		= 2;
	public static final int allin  		= 3;
	public static final int check  		= 4;
	public static final int click  		= 5;
	public static final int flip   		= 6;
	public static final int fold   		= 7;
	public static final int deal   		= 8;
	public static final int chipsin    	= 9;
	public static final int cardsFlop  	= 10;
	public static final int cardsTurn  	= 11;
	public static final int win   	   	= 12;
	public static final int winner     	= 13;
	public static final int raisingbet  = 14;
	public static final int playing		= 15;
	public static final int exitinggame = 16;
	public static final int losinghand = 17;

	
	public SoundManager()
	{
		
	}
		
	public void initSounds(Context theContext) { 
		 mContext = theContext;
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); 
	     mSoundPoolMap = new HashMap<Integer, Integer>(); 
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE); 	     
	} 
	
	public void addSound(int Index,int SoundID)
	{
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public void playSound(int index) { 
		
	     int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	     mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f); 
	}
	
	public void playLoopedSound(int index) { 
		
	     int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	     mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f); 
	}
	
}