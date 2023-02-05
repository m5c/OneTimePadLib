/**
 * Custom exception to indicate a provided party was rejected.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;


/**
 * Custom Exception indicating a privded party was not recognized.
 */
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