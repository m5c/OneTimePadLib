/**
 * Unit tests for the cryptor class.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

public class CryptorTest {

  /**
   * Verify the xor bitmask operation implemented for message encryption is correct.
   *
   * @throws CryptorException if the provided byte arrays differ in length.
   */
  @Test
  public void xorByteArrayTest() throws CryptorException {

    // Note: java encodes values [-128, 127] using Two complements (first bit is -128)
    // https://en.wikipedia.org/wiki/Two%27s_complement

    // Illustration of Twos complement:
    // -128 = 10000000
    // 0 = 00000000
    // 127 = 01111111
    byte[] testMessage = new byte[] {-128, 0, 0, -1};
    byte[] testChunk = new byte[] {0, 127, 0, 0};
    byte[] expectedOutcome = new byte[] {-128, 127, 0, -1};
    byte[] cryptedOutcome = Cryptor.crypt(testMessage, testChunk);

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
}
