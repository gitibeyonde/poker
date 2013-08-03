package com.onlinepoker.exceptions;
/**
 * This exception throwed when player has not enough money on his account
 * to sit to table.
 */
public class UnsufficentFundException extends Exception {

  public UnsufficentFundException(String message) {
    super(message);
  }

}
