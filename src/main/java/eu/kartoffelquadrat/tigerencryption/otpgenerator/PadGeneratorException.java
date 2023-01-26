package eu.kartoffelquadrat.tigerencryption.otpgenerator;

/**
 * Custom Exception class that is thrown whenever something unresolvable occurrs throughout the pad
 * generation process.
 * @author Maximilian Schiedermeier
 */
public class PadGeneratorException
    extends RuntimeException {
  public PadGeneratorException(String cause) {
    super(cause);
  }
}
