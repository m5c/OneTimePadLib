/**
 * Unit tests for the one time pad de/serialization.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.Test;

public class OneTimePadSerializationToolsTest {

  /**
   * Creates a random one time pad, then tries to convert it to JSON and back to a one time pad
   * object.
   */
  @Test
  public void testBackAndForthConversion() throws PadGeneratorException {

    // Create new one time pad
    OneTimePad pad =
        OneTimePadGenerator.generatePad(12, 12, new String[] {"alice@luna", "bob@mars"});

    // Convert to JSON
    Gson converter = OneTimePadSerializationTools.getGsonPadConverter();
    String serializedPad = converter.toJson(pad);

    // Convert back to object
    OneTimePad restoredPad = converter.fromJson(serializedPad, OneTimePad.class);

    // Verify the deserialized pad is identical
    boolean identical = pad.equals(restoredPad);
    Assert.assertTrue("Json Pad is not equal to original.", identical);
  }
}
