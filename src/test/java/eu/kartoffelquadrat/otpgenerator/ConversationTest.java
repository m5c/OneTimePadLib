/**
 * Tests in this class simulate the expected way to interact with the library, using the
 * Conversation interface.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import junit.framework.Assert;
import org.junit.Test;

public class ConversationTest extends CommonTestUtils {

  @Test
  public void testCreateConversation() throws InvalidPartyException, PadGeneratorException {

    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();

    // Create a conversation, assume the writing party is alice on machine luna.
    Conversation aliceConversation = new Conversation(pad, "alice@luna");
  }

  @Test(expected = InvalidPartyException.class)
  public void testRejectPartyMismatch() throws PadGeneratorException, InvalidPartyException {
    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();

    // Create a conversation. This should fail because alice@titan is not a valid party for the otp.
    new Conversation(pad, "alice@titan");
  }


  @Test
  public void testConversationMessageAdd() throws PadGeneratorException, CryptorException {

    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();

    // Create a conversation, assume the writing party is alice on machine luna.
    Conversation aliceConversation = new Conversation(pad, "alice@luna");

    // Create a secret plain message
    String message = "This is a super secret message. Shushhhhhhh!";
    PlainMessage plainMessage = new PlainMessage("alice", "luna", message.getBytes());

    // Attempt to add message to conversation, and retrieve encrypted counterpart
    EncryptedMessage encMessage = aliceConversation.addPlainMessage(plainMessage);

    // Verify the message was added
    Assert.assertEquals("Expected history size was 1, but the retrieved size is not.", 1,
        aliceConversation.getConversationHistory().size());

    // Verify the decrypted message is identical
    PlainMessage decryptedMessage = aliceConversation.getConversationHistory().iterator().next();
    Assert.assertEquals("Retreived decrypted message differs from original message.", plainMessage,
        decryptedMessage);
    Assert.assertEquals("Retreived decrytped payload string differs from original", message,
        decryptedMessage.getPayloadAsString());
  }

  @Test
  public void testConversationExportAndRestore() {
//    // Create a pad with four parties and massively chunks.
//    OneTimePad pad = createRealisticPad();
//
//    // Create a conversation, assume the writing party is alice on machine luna.
//    Conversation aliceConversation = new Conversation(pad, "alice@luna");
//
//    // Create a second conversation that belongs to bob, on machine phobos.
//    Conversation bobConversation = new Conversation(pad, "bob@titan");
  }


}

