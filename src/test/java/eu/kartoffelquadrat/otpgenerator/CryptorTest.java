/**
 * Unit tests for the cryptor class.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

public class CryptorTest extends CommonTestUtils {

  /**
   * Verify the xor bitmask operation implemented for message encryption is correct.
   *
   * @throws CryptorException if the provided byte arrays differ in length.
   */
  @Test
  public void xorByteArrayTest() throws CryptorException {

    // Note: java encodes values [-128, 127] using Two complements (first bit is -128)
    // https://en.wikipedia.org/wiki/Two%27s_complement

    // Illustration of "Twos complement":
    // -128 = 10000000
    // 0 = 00000000
    // 127 = 01111111
    byte[] testMessage = new byte[] {-128, 0, 0, -1};
    byte[] testChunk = new byte[] {0, 127, 0, 0};
    byte[] expectedOutcome = new byte[] {-128, 127, 0, -1};
    byte[] cryptedOutcome = Cryptor.cryptChunkSizedMessage(testMessage, testChunk);

    Assert.assertTrue("Produced crypted outcome differs from expected.",
        Arrays.equals(expectedOutcome, cryptedOutcome));
  }

  /**
   * Test for the stirn gpadding algorithm (fill up of message string to match chunk size by
   * appending trailing whitespace characters.
   *
   * @throws CryptorException if the input string already exceeds the expected target length.
   */
  @Test
  public void testPadString() throws CryptorException {
    String unpaddedString = "foo";
    String paddedString = Cryptor.padString(unpaddedString, 16);

    Assert.assertEquals("Padding did not produce expected output length.", 16,
        paddedString.length());

    // Check the string is back to nromal if all trailing whitespaces removed.
    Assert.assertEquals("Padded string was not only whitespace extension of original.",
        paddedString.trim(), unpaddedString);
  }

  /**
   * Padding test where the input already exceeds the expected target length. Reaching the target
   * length by appending whitespaces is not possible and should result in a Cryptor Exception.
   *
   * @throws CryptorException as expected test outcome.
   */
  @Test(expected = CryptorException.class)
  public void testOverlyLongPadString() throws CryptorException {
    String unpaddedString = "foooooooooooooo";
    Cryptor.padString(unpaddedString, 4);
  }

  @Test
  public void testWhiteSpaceByteArrayGenerator() {
    int expectedValue = 4;
    byte[] whiteSpaceArray = Cryptor.getWhiteSpaceByteArray(expectedValue);
    Assert.assertEquals("Generated array length differs from expected value.",
        whiteSpaceArray.length, expectedValue);
  }

  @Test
  public void testMessageChopper() {

    byte[] testMessage = getSampleMessage();

    // Chop test message
    int chopSize = 16;
    byte[][] choppedMessage = Cryptor.chop(testMessage, chopSize);

    // Verify target lenght
    int targetLength = testMessage.length / chopSize + (testMessage.length % chopSize == 0 ? 0 : 1);
    Assert.assertEquals("Produced chopped array does not have target length.",
        choppedMessage.length, targetLength);

    // Verify all chops have equal size (16)
    for (int i = 0; i < choppedMessage.length; i++) {
      Assert.assertEquals("At least on entry in chopped message does not have expected length.",
          choppedMessage[i].length, chopSize);
    }
  }

  /**
   * This tests verifies the encryption algorithm by applying the identity pad (pad with only zeroes
   * on chunks) to a sample message. XOR with 0 always returns the original value, therefore the
   * expact outcome is the original message.
   */
  @Test(expected = NullPointerException.class)
  public void testEncryptMessageWithIdentityPad() throws CryptorException {

    byte[] sampleMessage = getSampleMessage();
    OneTimePad identityPad = createIdentityPad();

    // Encrypt test message
    EncryptedMessage encMessage = Cryptor.encryptMessage(sampleMessage, identityPad, 1);

    // Verify the first chunk in enc message is identical to start of sample message
    // 8 is the chunk size used in identity pad.
    boolean startIsIdentical =
        Arrays.equals(Arrays.copyOf(sampleMessage, 8), encMessage.getChop(1));
    Assert.assertTrue("Message encrypted with identity pad does not result in original.",
        startIsIdentical);

    // Verify access to the chunk 0 results in exception (not a key used in this pad)
    encMessage.getChop(0);
  }

  /**
   * Test to verify the chunk Ids used for consecutive encryption do not overlap.
   */
  @Test
  public void testConsecutiveEncryption() throws CryptorException {

    OneTimePad sampleOtp = createSamplePad();

    String firstMessage = "The quick ";
    byte[] firstMessageBytes = firstMessage.getBytes();
    String secondMessage = "brown fox.";
    byte[] secondMessageBytes = secondMessage.getBytes();

    // Encrypt both messages
    EncryptedMessage firstEncryptedMessage =
        Cryptor.encryptMessage(firstMessageBytes, sampleOtp, 0);
    EncryptedMessage secondEncryptedMessage = Cryptor.encryptMessage(secondMessageBytes, sampleOtp,
        firstEncryptedMessage.getFollowUpChunkIndex());

    // Debug output
    System.out.println(firstEncryptedMessage.serializeToHex());
    System.out.println(secondEncryptedMessage.serializeToHex());

    // Verify the first chunkId used in the second pad is greater than the last chunk Id used in the first pad
    int[] chunksOfFirstMessage = firstEncryptedMessage.getChunksUsed();
    int highestChunkIdFirstMessage = chunksOfFirstMessage[chunksOfFirstMessage.length - 1];
    int[] chunksOfSecondMessage = secondEncryptedMessage.getChunksUsed();
    int lowestChunkIdSecondMessage = chunksOfSecondMessage[0];

    // Verify there is no overlap
    Assert.assertTrue("The encrypted messages share common chunks!",
        highestChunkIdFirstMessage < lowestChunkIdSecondMessage);
  }

  /**
   * This tests attempts to encrypt a message that is longer than the remaining key material. The
   * expected ebhaviour is refusal by the cryptor class with a OutOfChunksException thrown.
   */
  @Test(expected = OutOfChunksException.class)
  public void testOutOfChunks() throws PadGeneratorException, CryptorException {

    // Create a tiny one time pad
    OneTimePad tinyPad =
        OneTimePadGenerator.generatePad(2, 2, new String[] {"alice@luna", "bob@mars"});
    // Create a long test message
    byte[] longSampleMessage = getSampleMessage();

    // Attempt encryption. Should fail, because there are not enough chunks in the pad.
    Cryptor.encryptMessage(longSampleMessage, tinyPad, 0);
  }

  /**
   * Tests back and forth conversion of a message and validates the outcome is identical to the
   * original message.
   */
  @Test
  public void testEncryptDecryptMatch() throws CryptorException, PadGeneratorException {

    // Generate a new random one time pad
    OneTimePad pad = OneTimePadGenerator.generatePad(new String[] {"alice@luna", "bob@mars"});

    // Retrieve a sample test message
    byte[] sampleMessage = getSampleMessage();

    // Encrypt the sample message (as alice)
    EncryptedMessage encryptedMessage = Cryptor.encryptMessage(sampleMessage, pad, 0);

    // Decrypt the encrypted message
    byte[] decryptedMessage = Cryptor.decryptMessage(encryptedMessage, pad, true);

    // Compare the outcome
    Assert.assertEquals("Decrypted message is not equal to original!",
        new String(sampleMessage).trim(), new String(decryptedMessage));
  }
}
