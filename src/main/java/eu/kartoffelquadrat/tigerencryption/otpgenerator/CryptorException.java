/**
 * Custom Exception class that is thrown whenever something unresolvable occurrs throughout the pad
 * generation process.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

/**
 * Custom Runtime Exception definition.
 */
public class CryptorException extends Throwable {

  /**
   * Constructor for custom exception.
   *
   * @param cause as descritive text inticating the reason for exception.
   */
  public CryptorException(String cause) {
    super(cause);
  }
}
