/**
 * Helper class with code used in several tests.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

public class CommonTestUtils {

  protected OneTimePad createRealisticPad() throws PadGeneratorException {
    String[] parties = new String[] {"alice@luna", "bob@mars", "alice@phobos", "bob@titan"};
    return OneTimePadGenerator.generatePad(parties);
  }

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
    padContent[10] = "nope".getBytes();
    padContent[11] = "yaya".getBytes();
    String[] parties = new String[] {"Bob", "Alice"};
    return new OneTimePad("2023-01-01--12-02-28", parties, padContent);
  }

  protected byte[] getSampleMessageBytes() {
    // Create test message
    return ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor "
        + "incididunt ut labore et dolore magna aliqua !!!").getBytes();
  }

  protected OneTimePad createIdentityPad() {

    String time = "Now :)";
    String[] parties = new String[] {"Alice", "Bob"};
    byte[][] identityChunks = new byte[32][];
    for (int i = 0; i < identityChunks.length; i++) {
      identityChunks[i] = new byte[] {0, 0, 0, 0, 0, 0, 0, 0};
    }
    return new OneTimePad(time, parties, identityChunks);
  }
}
