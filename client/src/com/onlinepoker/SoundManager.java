package com.onlinepoker;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SoundManager {

  static Logger _cat = Logger.getLogger(SoundManager.class.getName());

  private static int loopNo;
  private static AudioClip[] audioClips;
  public static int CARDS_DEALING  = 0;
  public static int CARDS_FOLDING  = 1;
  public static int CHECK          = 2;
  public static int CHIPS_BETTING  = 3;
  public static int CHIPS_DRAGGING = 4;
  public static int CLICK_SOUND    = 5;
  public static int FLOP_DEALING   = 6;
  public static int PIG            = 7;
  public static int SHUFFLE_DECK   = 8;
  public static int WAKE_UP        = 9;
  public static int YOUR_TURN      = 10;
  public static int PIG_PIG_PIG    = 11;
  public static int WIN    = 12;
  public static int WIN2    = 13;

  public SoundManager() {
  }

  static {
  	audioClips = new AudioClip[20];
  	Class clas = SoundManager.class;
  	try {
		audioClips[0] = Applet.newAudioClip(clas.getResource("sound/cards_dealing.wav"));
		audioClips[1] = Applet.newAudioClip(clas.getResource("sound/cards_folding.wav"));
		audioClips[2] = Applet.newAudioClip(clas.getResource("sound/check.wav"));
		audioClips[3] = Applet.newAudioClip(clas.getResource("sound/chips_betting.wav"));
		audioClips[4] = Applet.newAudioClip(clas.getResource("sound/chips_dragging.wav"));
		audioClips[5] = Applet.newAudioClip(clas.getResource("sound/click_sound.wav"));
		audioClips[6] = Applet.newAudioClip(clas.getResource("sound/flop_dealing.wav"));
		audioClips[7] = Applet.newAudioClip(clas.getResource("sound/pig.wav"));
		audioClips[8] = Applet.newAudioClip(clas.getResource("sound/shuffle_deck.wav"));
		audioClips[9] = Applet.newAudioClip(clas.getResource("sound/wake_up.wav"));
		audioClips[10] = Applet.newAudioClip(clas.getResource("sound/your_turn.wav"));
		audioClips[11] = Applet.newAudioClip(clas.getResource("sound/pig_rep.wav"));
		audioClips[12] = Applet.newAudioClip(clas.getResource("sound/win.wav"));
		audioClips[13] = Applet.newAudioClip(clas.getResource("sound/win2.wav"));
  } catch (Exception e) {
  		_cat.log(Level.WARNING, "Unable to load", e);
	}
  }


  public static void playEffect(int num) {
  	try {
	  loopTest();
  	  if (audioClips[num] != null)
      	audioClips[num].play();
	} catch (Exception e) {
		_cat.log(Level.WARNING, "Unable to load",e);
	}
  }

  public static void playEffectRepeatable(int num) {
	try {
	  loopTest();
	  if (audioClips[num] != null)
		audioClips[num].loop();
		loopNo = num;
	} catch (Exception e) {
		_cat.log(Level.WARNING, "Unable to load",e);
	}
  }

  protected static void loopTest() {
	try {
	  if (loopNo != -1) {
		audioClips[loopNo].stop();
		loopNo = -1;
	  }
	} catch (Exception e) {
		_cat.log(Level.WARNING, "Unable to load",e);
	}
  }
}
