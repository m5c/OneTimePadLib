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

  /**
   * Automatic safety deletion of all test cache files, before and aver every test execution, to
   * ensure the tests have no cross dependencies and are not blemished by preceding failed tests and
   * do not clutter.
   */
  @After
  @Before
  public void removeTestFiles() {

    // Tidy up, so this does not affect any other tests or subsequent runs.
    File expectedLocation = new File(System.getProperty("user.dir") + "/" + "otp-test");
    expectedLocation.delete();
  }

  /**
   * Verifies successful creation of OTP target directories at test location.
   */
  @Test
  public void createOtpTargetDirTest() {
    OneTimePadGenerator.createOtpTargetDir("otp-test");
    File expectedLocation = new File(System.getProperty("user.dir") + "/" + "otp-test");
    Assert.assertTrue("Created a test OTP target folder, but could not verify its existence.",
        expectedLocation.exists());
  }

  /**
   * Verifies refusal of default otp location overwrites.
   */
  @Test(expected = RuntimeException.class)
  public void refuseOverwriteOtpTargetDirTest() {
    OneTimePadGenerator.createOtpTargetDir("otp-test");
    OneTimePadGenerator.createOtpTargetDir("otp-test");
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

  @Test
  public void persistChunkTest() throws IOException {

    // Test creation of chunk
    removeTestFiles();
    File testOtpDir = OneTimePadGenerator.createOtpTargetDir("otp-test");
    byte[] testChunk = OneTimePadGenerator.generateChunk(8);
    OneTimePadGenerator.persistChunk(testChunk, 0, testOtpDir);

    // Verify existence of test chunk on disk
    File testChunkFile = new File(testOtpDir + "/0000");
    Assert.assertTrue("Tested persistence of a test chunk, but the file cannot be found.",
        testChunkFile.exists());

    // Remove test chunk
    testChunkFile.delete();
  }
}
