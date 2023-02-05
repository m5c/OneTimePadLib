/**
 * Tests in this class simulate the expected way to interact with the library, using the
 * Conversation interface.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import org.junit.Test;

public class ConversationTest extends CommonTestUtils {

  @Test
  public void testCreateConversation() throws InvalidPartyException, PadGeneratorException {

    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();

    // Create a conversation, assume the writing party is alice on machine luna.
    Conversation aliceConversation = new Conversation(pad, "alice@luna");

    // Create a second conversation that belongs to bob, on machine phobos.
    Conversation bobConversation = new Conversation(pad, "bob@titan");
  }

  @Test(expected = InvalidPartyException.class)
  public void testRejectPartyMismatch() throws PadGeneratorException, InvalidPartyException {
    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();

    // Create a conversation. This should fail because alice@titan is not a valid party for the otp.
    new Conversation(pad, "alice@titan");
  }
}

