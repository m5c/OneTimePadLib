/**
 * Helper class with code used in several tests.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

public class CommonTestUtils {

  protected String[] getSampleSeriesOfMessages() {

    return new String[] {
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi id diam euismod, ",
        "pretium nisl et, mollis orci. Aliquam vitae elit et felis bibendum facilisis condimentum vitae lorem. Praesent viverra imperdiet convallis. Sed quis malesuada ligula, ut accumsan nulla. Vivamus vestibulum, arcu nec eleifend egestas, elit metus",
        " semper nisl,", "eu", " molestie", " q", "u", "a ", "m ", " magna vitae justo.",
        "Fusce lacinia nulla enim, eu finibus augue congue eu. Donec sollicitudin consequat nunc, in congue tellus egestas sed."};
  }

  protected String[] getDefaultParties() {
    return new String[] {"alice@luna", "bob@mars", "alice@phobos", "bob@titan"};
  }

  protected OneTimePad createRealisticPad() throws PadGeneratorException {
    return OneTimePadGenerator.generatePad(getDefaultParties());
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
