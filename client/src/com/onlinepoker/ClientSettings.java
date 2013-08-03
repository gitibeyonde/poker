package com.onlinepoker;

import java.text.MessageFormat;

import com.golconda.db.PlayerPreferences;


/**
 * This class holds info about client preferences (settings) related to
 * appiarence of application, auto play settings and so on.
 * The such info stored in persistent storage on server side.
 * @hibernate.class
 *    table="clientSettings"
 */
public class ClientSettings {

  private int userId;

  private boolean bigSymbols = false;
  private boolean autoPostBlind = false;
  private boolean waitForBigBlind = true;
  private boolean showBestCards = true;
  private boolean muckLosingCards = true;
  private boolean randomDelay = false;

  //@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
  private boolean sound = true;

  //@@@@@@@@@@@@@@@@@@   LANGUAGE   @@@@@@@@@@@@@@@@@@@@@@@@@@
  private String language = "";
  
//@@@@@@@@@@@@@@@@@@   4 color cards   @@@@@@@@@@@@@@@@@@@@@@@@@@
  private boolean fourColorCards = false;



/**
   * Empty constructor.
   */

  public ClientSettings() {

  }

  public ClientSettings(int pref) {
	  if(pref == -1)// this is default case when client system having no preference file
	  {
		  autoPostBlind = true;
		  waitForBigBlind = true;
		  sound = true;
		  fourColorCards = false;
	  }
	  else
	  {
		//bigSymbols = (PlayerPreferences.BIGSYMBOLS & pref) > 0;
	    autoPostBlind = (PlayerPreferences.AUTOPOSTBLIND & pref) > 0;
	    waitForBigBlind = (PlayerPreferences.WAITFORBIGBLIND & pref) > 0;
	    //showBestCards = (PlayerPreferences.SHOWBESTCARDS & pref) > 0;
	    //muckLosingCards = (PlayerPreferences.MUCKLOSINGCARDS & pref) > 0;
	    //randomDelay = (PlayerPreferences.RANDOMDELAY & pref) > 0;
	    sound = (PlayerPreferences.SOUND & pref) > 0;
	    fourColorCards = (PlayerPreferences.u4 & pref) > 0;
	  }
  }

  /**
   * Copy constructor.
   */
  public void copy(ClientSettings settings) {
	//bigSymbols = settings.bigSymbols;
    autoPostBlind = settings.autoPostBlind;
    waitForBigBlind = settings.waitForBigBlind;
    //showBestCards = settings.showBestCards;
    //muckLosingCards = settings.muckLosingCards;
    //randomDelay = settings.randomDelay;
    sound = settings.sound;
    fourColorCards = settings.fourColorCards;
  }

  public int intVal() {
    int p = 0;
//    if (bigSymbols) {
//      p |= PlayerPreferences.BIGSYMBOLS;
//    }
    if (autoPostBlind) {
      p |= PlayerPreferences.AUTOPOSTBLIND;
    }
    if (waitForBigBlind) {
      p |= PlayerPreferences.WAITFORBIGBLIND;
    }
//    if (showBestCards) {
//      p |= PlayerPreferences.SHOWBESTCARDS;
//    }
//    if (muckLosingCards) {
//      p |= PlayerPreferences.MUCKLOSINGCARDS;
//    }
//    if (randomDelay) {
//      p |= PlayerPreferences.RANDOMDELAY;
//    }
    if (sound) {
      p |= PlayerPreferences.SOUND;
    }
    if (fourColorCards) {
      p |= PlayerPreferences.u4;
    }

    return p;
  }

  /**
   * Gets the user id to whom this settings belongs.
   * @hibernate.id
   *    column="userId"
   *    generator-class="assigned"
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Sets the user id to whom this settings belongs.
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  
  public String getLanguage() {
	return language;
  }

  public void setLanguage(String language) {
	this.language = language;
  }
  
  public boolean isFourColorCards() {
	return fourColorCards;
  }

  public void setFourColorCards(boolean fourColorCards) {
	this.fourColorCards = fourColorCards;
  }
  /**
   *  Automatically posts a Blind when it's your turn to post it.
   * @hibernate.property
   */
  public boolean isAutoPostBlind() {
    return autoPostBlind;
  }

  public void setAutoPostBlind(boolean autoPostBlind) {
    this.autoPostBlind = autoPostBlind;
  }

  /**
   * When this option is selected you will see a deck with larger symbols on it.
   * @hibernate.property
   */
  public boolean isBigSymbols() {
    return bigSymbols;
  }

  public void setBigSymbols(boolean bigSymbols) {
    this.bigSymbols = bigSymbols;
  }

  /**
   * Automatically mucks your hand if it's worse than other already shown
   * hands at the showdown.
   * @hibernate.property
   */
  public boolean isMuckLosingCards() {
    return muckLosingCards;
  }

  public void setMuckLosingCards(boolean muckLosingCards) {
    this.muckLosingCards = muckLosingCards;
  }

  /**
   * There will be a short, random delay when you are acting using the
   * in-turn choices.
   * @hibernate.property
   */
  public boolean isRandomDelay() {
    return randomDelay;
  }

  public void setRandomDelay(boolean randomDelay) {
    this.randomDelay = randomDelay;
  }

  /**
   * Automatically shows your hand if it's better than other already shown
   * hands at the showdown.
   * @hibernate.property
   */
  public boolean isShowBestCards() {
    return showBestCards;
  }

  public void setShowBestCards(boolean showBestCards) {
    this.showBestCards = showBestCards;
  }

  /**
   * You will not be dealt in until it's your turn to post the Big Blind,
   * when you have just joined at table.
   * @hibernate.property
   */
  public boolean isWaitForBigBlind() {
    return waitForBigBlind;
  }

  public void setWaitForBigBlind(boolean waitForBigBlind) {
    this.waitForBigBlind = waitForBigBlind;
  }

//@@@@@@@@@@@@@@@@@@@@Sound
  public boolean isSound() {
    return sound;
  }

  public void setSound(boolean sound) {
    this.sound = sound;
  }

  public String toString() {
    return MessageFormat.format(
        "autoblind: {0}, waitforblind: {1}, sound: {2}, colorcards: {3}",
        new Object[] {
        "" + autoPostBlind, "" + waitForBigBlind,
        "" + sound, "" + fourColorCards 
    });
  }

}
