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
}
