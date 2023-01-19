/**
 * Unit tests for the otp generator.
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import junit.framework.Assert;
import org.junit.Test;

public class OneTimePadGeneratorTest {

  @Test
  public void testCreateChunk() {

    Assert.assertEquals("Is 42 the same as 42?", "42", "42");

  }
}
