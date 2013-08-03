package com.onlinepoker.exceptions;
/**
 * This exception throwed when some error occuried dure attempt to register a
 * new user account.
 */
public class AccountRegistrationException extends Exception {
  /**
   * Unknown error.
   */
  public static final int UNKNOWN_ERROR   = 0;
  /**
   * Attempt to register account with the nickname already existed in the system.
   */
  public static final int NICKNAME_EXISTS = 1;

  private int errorCode = UNKNOWN_ERROR;

  public AccountRegistrationException() {
    super();
    this.errorCode = UNKNOWN_ERROR;
  }

  public AccountRegistrationException(int errorCode) {
    super();
    this.errorCode = errorCode;
  }

  /**
   * Gets the error code.
   */
  public int getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    switch (errorCode) {
      case NICKNAME_EXISTS:
        return "Attempt to register account with the nickname already existed in the system.";
    }
    return "Unknown error dure account registration";
  }

}
