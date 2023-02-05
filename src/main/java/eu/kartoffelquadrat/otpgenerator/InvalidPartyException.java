package eu.kartoffelquadrat.otpgenerator;

public class InvalidPartyException extends CryptorException {
  /**
   * Constructor for custom exception.
   *
   * @param cause as descritive text inticating the reason for exception.
   */
  public InvalidPartyException(String cause) {
    super(cause);
  }
}