/**
 * Unit tests for the EncryptedMessages class.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import junit.framework.Assert;
import org.junit.Test;

public class EncryptedMessageTest extends CommonTestUtils {

  @Test
  public void createMessageTest() {
    OneTimePad testPad = createSamplePad();

    // Create sample chops (as they would be created by cryptor)
    byte[][] sampleChops = new byte[2][];
        sampleChops[0] = "toto".getBytes();
        sampleChops[1] = "tata".getBytes();

    // Manually create an instance of Encrypted Message, without using Cryptor
    EncryptedMessage encMessage = new EncryptedMessage(testPad, 0, sampleChops);

    // Assert fields in encrypted message are correctly instantiated.
    Assert.assertEquals("Amount of chops in encrypted message does not correspond to input parameters.", encMessage.getChopAmount(), 2);


  }

  @Test
  public void serializeMessageTest() {

    OneTimePad testPad = createSamplePad();

    // Create sample chops (as they would be created by cryptor)
    byte[][] sampleChops = new byte[2][];
    sampleChops[0] = "toto".getBytes();
    sampleChops[1] = "tata".getBytes();

    // Manually create an instance of Encrypted Message, without using Cryptor
    EncryptedMessage encMessage = new EncryptedMessage(testPad, 0, sampleChops);

    // Serialize the encrypted message. Verify every line contains 3 segements, separated by "-"
    String serializedPad = encMessage.serializeToHex();

    // Verify the first is a hash, second is a zero padded index, the third is hexadecimal
    String[] lines = serializedPad.split(System.lineSeparator());

    Assert.assertEquals("Produced message does not contain correct amount of chops", lines.length, 2);
    Assert.assertEquals("Produced message lines do not containt 3 segments each", lines[0].split("-").length, 3);
    Assert.assertEquals("Produced message lines do not containt 3 segments each", lines[1].split("-").length, 3);
  }
}
