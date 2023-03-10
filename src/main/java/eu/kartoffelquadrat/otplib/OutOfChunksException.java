/**
 * Custom exception to indicate the library ran out of key material.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otplib;

/**
 * This exception is thrown if the library was requested to encrypt a message but, there are not
 * enough remaining chunks in the provided one time pad.
 */
public class OutOfChunksException extends CryptorException {
  /**
   * Constructor for custom exception.
   *
   * @param cause as descritive text inticating the reason for exception.
   */
  public OutOfChunksException(String cause) {
    super(cause);
  }
}
