package com.onlinepoker.models;

/**
 * This class is a simple value object mapped on DBMS table record and
 * contained information about distribution of tournament prize pool.
 * @author Kom
 * @hibernate.class
 *    table="tournamentPrizeDistr"
 */

public class TournamentPrizeDistribution implements java.io.Serializable {

  private int distrId;
  private String name;
  private int place1 = 50;
  private int place2 = 30;
  private int place3 = 20;
  private int place4 = 0;
  private int place5 = 0;
  private int place6 = 0;
  private int place7 = 0;
  private int place8 = 0;
  private int place9 = 0;
  private int place10 = 0;

  
	public static final String[] HEADERS = {
				"Name",
				"Place I",
				"Place II",
				"Place III",
				"Place IV",
				"Place V",
				"Place VI",
				"Place VII",
				"Place VIII",
				"Place IX",
				"Place X" };
  /**
   * Empty constructor.
   */
  public TournamentPrizeDistribution() {}
  /**
   * Gets a tournament prize distribution id.
   * @hibernate.id
   *    unsaved-value="0"
   *    generator-class="sequence"
   * @hibernate.generator-param
   *    name="sequence"
   *    value="distrId"
   */
  public int getDistrId() {
    return distrId;
  }
  /**
   * Sets a tournament prize distribution id.
   */
  public void setDistrId(int distrId) {
    this.distrId = distrId;
  }
  /**
   * Gets a tournament prize distribution name.
   * @hibernate.property
   */
  public String getName() {
    return name;
  }
  /**
   * Sets a tournament prize distribution name.
   */
  public void setName(String newValue) {
    this.name = newValue;
  }
  /**
   * Gets a percent from prize pool for the first place.
   * @hibernate.property
   */
  public int getPlace1() {
    return place1;
  }
  /**
   * Sets a percent from prize pool for first place.
   */
  public void setPlace1(int place1) {
    this.place1 = place1;
  }
  /**
   * Gets a percent from prize pool for the second place.
   * @hibernate.property
   */
  public int getPlace2() {
    return place2;
  }
  /**
   * Sets a percent from prize pool for the second place.
   */
  public void setPlace2(int place2) {
    this.place2 = place2;
  }
  /**
   * Gets a percent from prize pool for the third place.
   * @hibernate.property
   */
  public int getPlace3() {
    return place3;
  }
  /**
   * Sets a percent from prize pool for the third place.
   */
  public void setPlace3(int place3) {
    this.place3 = place3;
  }

  /**
   * Gets a percent from prize pool for the fourth place.
   * @hibernate.property
   */
  public int getPlace4() {
    return place4;
  }
  /**
   * Sets a percent from prize pool for the fourth place.
   */
  public void setPlace4(int place4) {
    this.place4 = place4;
  }
  /**
   * Gets a percent from prize pool for the fifth place.
   * @hibernate.property
   */
  public int getPlace5() {
    return place5;
  }
  /**
   * Sets a percent from prize pool for the fifth place.
   */
  public void setPlace5(int place5) {
    this.place5 = place5;
  }
  /**
   * Gets a percent from prize pool for the sixth place.
   * @hibernate.property
   */
  public int getPlace6() {
    return place6;
  }
  /**
   * Sets a percent from prize pool for the sixth place.
   */
  public void setPlace6(int place6) {
    this.place6 = place6;
  }
  /**
   * Gets a percent from prize pool for the seventh place.
   * @hibernate.property
   */
  public int getPlace7() {
    return place7;
  }
  /**
   * Sets a percent from prize pool for the seventh place.
   */
  public void setPlace7(int place7) {
    this.place7 = place7;
  }
  /**
   * Gets a percent from prize pool for the eightth place.
   * @hibernate.property
   */
  public int getPlace8() {
    return place8;
  }
  /**
   * Sets a percent from prize pool for the eightth place.
   */
  public void setPlace8(int place8) {
    this.place8 = place8;
  }
  /**
   * Gets a percent from prize pool for the ninth place.
   * @hibernate.property
   */
  public int getPlace9() {
    return place9;
  }
  /**
   * Sets a percent from prize pool for the ninth place.
   */
  public void setPlace9(int place9) {
    this.place9 = place9;
  }
  /**
   * Gets a percent from prize pool for the tenth place.
   * @hibernate.property
   */
  public int getPlace10() {
    return place10;
  }
  /**
   * Sets a percent from prize pool for the tenth place.
   */
  public void setPlace10(int place10) {
    this.place10 = place10;
  }
  /**
   * Gets the percent from prize pool for the specified place.
   */
  public int getPercentForPlace(int place) {
    switch (place) {
      case 1:
        return place1;
      case 2:
        return place2;
      case 3:
        return place3;
      case 4:
        return place4;
      case 5:
        return place5;
      case 6:
        return place6;
      case 7:
        return place7;
      case 8:
        return place8;
      case 9:
        return place9;
      case 10:
        return place10;
    }
    return 0;
  }

	public String toString() {
		return getName();
	}

}