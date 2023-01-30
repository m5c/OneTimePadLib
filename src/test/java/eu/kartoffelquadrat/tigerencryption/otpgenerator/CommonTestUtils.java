/**
 * Helper class with code used in several tests.
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

public class CommonTestUtils {

  /**
   * Manually creates a sample pad with 2 chunks, each holding 4 byte.
   *
   * @return OneTimePad ready for testing.
   */
  protected OneTimePad createSamplePad() {
    byte[][] padContent = new byte[2][];
    padContent[0] = "FOO!".getBytes();
    padContent[1] = "BAR!".getBytes();
    String[] parties = new String[] {"Bob", "Alice"};
    return new OneTimePad("2023-01-01--12-02-28", parties, padContent);
  }
}
