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
    byte[][] padContent = new byte[12][];
    padContent[0] = "FOO!".getBytes();
    padContent[1] = "BAR!".getBytes();
    padContent[2] = "YES!".getBytes();
    padContent[3] = "NOPE".getBytes();
    padContent[4] = "WHAT".getBytes();
    padContent[5] = "YAY!".getBytes();
    padContent[6] = "NNNN".getBytes();
    padContent[7] = "ARGH".getBytes();
    padContent[8] = "EVER".getBytes();
    padContent[9] = "FRWD".getBytes();
    padContent[10] = "0378".getBytes();
    padContent[11] = "!0-3".getBytes();
    String[] parties = new String[] {"Bob", "Alice"};
    return new OneTimePad("2023-01-01--12-02-28", parties, padContent);
  }
}
