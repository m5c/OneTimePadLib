/**
 * Custom execption for cryptographic mismatches.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otplib;

/**
 * Custom exception that is used whenever an operation is invoked that attempts to combine encrypted
 * messages with the wrong cryptographic material (the wrong one time pad).
 */
public class OneTimePadMissmatchException extends CryptorException {
  /**
   * Constructor for custom exception.
   *
   * @param cause as descritive text inticating the reason for exception.
   */
  public OneTimePadMissmatchException(String cause) {
    super(cause);
  }
}