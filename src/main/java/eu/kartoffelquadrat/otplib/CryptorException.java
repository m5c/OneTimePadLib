/**
 * Custom Exception class that is thrown whenever something unresolvable occurrs throughout the pad
 * generation process.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otplib;

/**
 * Custom Exception definition for generic failure situations throught encryption or decryption.
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
