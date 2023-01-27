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
public class PadGeneratorException extends Throwable {

  /**
   * Constructor for custom exception.
   *
   * @param cause as descritive text inticating the reason for exception.
   */
  public PadGeneratorException(String cause) {
    super(cause);
  }
}
