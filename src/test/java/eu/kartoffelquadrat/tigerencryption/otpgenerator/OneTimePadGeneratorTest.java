/**
 * Unit tests for the otp generator.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OneTimePadGeneratorTest {

  // Name used for the test pad.
  private static final String TEST_PAD_NAME = "otp-test.json";

  /**
   * Automatic safety deletion of all test cache files, before and aver every test execution, to
   * ensure the tests have no cross dependencies and are not blemished by preceding failed tests and
   * do not clutter.
   */
  @After
  @Before
  public void removeTestFiles() {

    // Tidy up, so this does not affect any other tests or subsequent runs.
    File expectedLocation = new File(System.getProperty("user.dir") + "/"
        + TEST_PAD_NAME);
    expectedLocation.delete();
  }

  /**
   * Default use case for creation and persisting a one time pad to disk. Library users will most
   * likely run simethign similar.
   *
   * @throws PadGeneratorException in case persisting the one time pad to disk encounter an error.
   */
  @Test
  public void createAndPersistPadTest() throws  PadGeneratorException {

    // Sample pad users
    String[] parties = new String[]{"alice@luna", "bob@phobos"};

    // Create target json file object. Also verify the is not yet a pad thad would be erased
    File otpTargetFile = new File(System.getProperty("user.dir")
        + "/" + TEST_PAD_NAME);
    if (otpTargetFile.exists()) {
      throw new PadGeneratorException("Target file \"" + TEST_PAD_NAME + "\" already exists.");
    }

    // create the one time pad, consisting of many chunks for the individual messages.
    OneTimePad pad = OneTimePadGenerator.generatePad(parties);

    // Store pad on disk
    OneTimePadGenerator.persistPad(pad, otpTargetFile, true);
  }


  /**
   * Verifies refusal for empty parties.
   */
  @Test(expected = PadGeneratorException.class)
  public void refuseNoParties() throws PadGeneratorException {
    OneTimePadGenerator.generatePad(new String[] {});
  }

  /**
   * Verifies refusal of default otp location overwrites.
   */
  @Test(expected = PadGeneratorException.class)
  public void refuseOverwriteOtpTargetFileTest() throws PadGeneratorException, IOException {
    new File(System.getProperty("user.dir") + "/" + TEST_PAD_NAME).createNewFile();
    createAndPersistPadTest();
  }

  /**
   * Tests creation of chunk by verifying byte array size.
   */
  @Test
  public void generateChunkSizeTest() {
    byte[] testChunk = OneTimePadGenerator.generateChunk(8);
    Assert.assertEquals("Generated test chunk is not of requested size.", 8, testChunk.length);
  }

  /**
   * Tests randomness of generated chunk by ensuring two subsequent chunks are distinct in content.
   */
  @Test
  public void generateChunkEntropyTest() {

    // This test could in prinicple be false negative. Yet the likelinessis 1/((8*16)^2)=6E-5
    byte[] chunk1 = OneTimePadGenerator.generateChunk(16);
    byte[] chunk2 = OneTimePadGenerator.generateChunk(16);

    boolean identical = true;
    for (int i = 0; i < chunk1.length; i++) {
      identical &= chunk1[i] == chunk2[i];
    }
    Assert.assertFalse("Two generated test chunks were exactly identical.", identical);
  }
}
