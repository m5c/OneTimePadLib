/**
 * Unit tests for the One Time Pad class.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

public class OneTimePadTest extends CommonTestUtils {

  /**
   * Test verifies that the original pad content is not modified if retreived parties are modified.
   * Pads are supposed to be immutable.
   */
  @Test
  public void testPadPartyIntegrity() {

    OneTimePad pad = createSamplePad();
    String[] parties = pad.getParties();
    parties[0] = "Eve";

    // Get parties again and verify the position 0 is unchanged
    parties = pad.getParties();
    Assert.assertEquals(
        "Pads should be immutatble but a test managed to change to contained parties.", parties[0],
        "Bob");
  }

  /**
   * Tests creation of one time pad and sanity of retrieved getter values for amount of chunks and
   * chunk size.
   */
  @Test
  public void createOneTimePadTest() {
    OneTimePad pad = createSamplePad();
    Assert.assertEquals(
        "Created test pad with two chunks, but the retreived chunk amount is not the same.", 2,
        pad.getChunkAmount());
    Assert.assertEquals(
        "Created test pad chunks size 4, but the retreived chunk size is not the same.", 4,
        pad.getChunkSize());
  }

  /**
   * One time pads are supposed to be immutable. Access to a chunk only returns a copy. This test
   * ensures that tampering with a retrieved pad does not alter the original.
   */
  @Test
  public void ensureIntegrityTest() {

    byte[] untamperedChunk = "BAR!".getBytes();
    OneTimePad pad = createSamplePad();

    // Try to manipulate pad contents (Change "BAR!" to "BAZ!". Then Verify pad content is unchanged.
    pad.getChunkContent(1)[2] = "Z".getBytes()[0];
    boolean identical = Arrays.equals(untamperedChunk, pad.getChunkContent(1));
    Assert.assertTrue("Tampered with pad contents and the original pad has changed.", identical);
  }
}
