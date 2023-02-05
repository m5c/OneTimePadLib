/**
 * Tests in this class simulate the expected way to interact with the library, using the
 * Conversation interface.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import java.util.LinkedHashSet;
import java.util.Set;
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
    aliceConversation.addPlainMessage(plainMessage);

    // Verify the message was added
    Assert.assertEquals("Expected history size was 1, but the retrieved size is not.", 1,
        aliceConversation.getPlainConversationHistory().size());

    // Verify the decrypted message is identical
    PlainMessage decryptedMessage =
        aliceConversation.getPlainConversationHistory().iterator().next();
    Assert.assertEquals("Retreived decrypted message differs from original message.", plainMessage,
        decryptedMessage);
    Assert.assertEquals("Retreived decrytped payload string differs from original", message,
        decryptedMessage.getPayloadAsString());
  }

  @Test
  public void testChunkIdNoCollisionFourPartyConversation()
      throws PadGeneratorException, CryptorException {

    // Create pad for four parties
    OneTimePad fourPartyPad = createRealisticPad();
    String[] parties = getDefaultParties();
    String[] messageSeries = getSampleSeriesOfMessages();

    // Create the four associated conversations
    Conversation aliceConversationLuna = new Conversation(fourPartyPad, parties[0]);
    Conversation bobConversationMars = new Conversation(fourPartyPad, parties[1]);
    Conversation aliceConversationPhobos = new Conversation(fourPartyPad, parties[2]);
    Conversation bobConversationTitan = new Conversation(fourPartyPad, parties[3]);

    // Pump up one conversation with encrypted messages (and direct plain messages)
    // Some of them long, some of them short
    aliceConversationLuna.addPlainMessage(
        new PlainMessage("alice", "luna", messageSeries[0].getBytes()));
    aliceConversationLuna.addPlainMessage(
        new PlainMessage("alice", "luna", messageSeries[1].getBytes()));
    aliceConversationLuna.addPlainMessage(
        new PlainMessage("alice", "luna", messageSeries[2].getBytes()));

    aliceConversationLuna.addEncryptedMessage(bobConversationMars.addPlainMessage(
        new PlainMessage("bob", "mars", messageSeries[3].getBytes())));
    aliceConversationLuna.addEncryptedMessage(bobConversationMars.addPlainMessage(
        new PlainMessage("bob", "mars", messageSeries[4].getBytes())));

    aliceConversationLuna.addEncryptedMessage(aliceConversationPhobos.addPlainMessage(
        new PlainMessage("alice", "phobos", messageSeries[5].getBytes())));
    aliceConversationLuna.addEncryptedMessage(aliceConversationPhobos.addPlainMessage(
        new PlainMessage("alice", "phobos", messageSeries[6].getBytes())));

    aliceConversationLuna.addEncryptedMessage(bobConversationTitan.addPlainMessage(
        new PlainMessage("bob", "titan", messageSeries[7].getBytes())));

    aliceConversationLuna.addPlainMessage(
        new PlainMessage("alice", "luna", messageSeries[8].getBytes()));
    aliceConversationLuna.addPlainMessage(
        new PlainMessage("alice", "luna", messageSeries[9].getBytes()));

    // Exdtract all chunk IDs ever used in encrypted messages. Ensure there are no collisions.
    Set<Integer> allChunkIdsUsed = new LinkedHashSet<>();
    for (EncryptedMessage encMessage : aliceConversationLuna.getEncryptedConversationHistory()) {
      int[] chunksUsed = encMessage.getChunksUsed();
      for (int i = 0; i < chunksUsed.length; i++) {
        if (allChunkIdsUsed.contains(chunksUsed[i])) {
          Assert.fail("ChunkId collision in test conversation.");
        }
        allChunkIdsUsed.add(chunksUsed[i]);
      }
    }
  }

  @Test
  public void testAddEncryptedMessageToParallelConversation()
      throws PadGeneratorException, CryptorException {

    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();

    // Create a conversation, assume the writing party is alice on machine luna.
    Conversation aliceConversation = new Conversation(pad, "alice@luna");

    // Create a secret plain message
    String message = "This is a super secret message. Shushhhhhhh!";
    PlainMessage plainMessage = new PlainMessage("alice", "luna", message.getBytes());

    // Attempt to add message to conversation, and retrieve encrypted counterpart
    EncryptedMessage encryptedAliceMessage = aliceConversation.addPlainMessage(plainMessage);

    // Add encrypted counterpart to parallel conversation, maintained by bob
    // Create a conversation, assume the writing party is alice on machine luna.
    Conversation bobConversation = new Conversation(pad, "bob@titan");
    bobConversation.addEncryptedMessage(encryptedAliceMessage);

    // Verify the integrity if retreived from bob's conversation
    PlainMessage restoredMessage = bobConversation.getPlainConversationHistory().iterator().next();
    Assert.assertEquals("Encrypted message injhected to parallel conversation lost integrity",
        restoredMessage, plainMessage);
  }

  @Test
  public void testConversationExportAndRestore() throws PadGeneratorException, CryptorException {
    // Create a pad with four parties and massively chunks.
    OneTimePad pad = createRealisticPad();
    String party = "alice@luna";

    // Create a conversation, assume the writing party is alice on machine luna.
    Conversation aliceConversation = new Conversation(pad, party);

    // Create a second conversation that belongs to bob, on machine phobos.
    Conversation bobConversation = new Conversation(pad, "bob@titan");

    // Create a secret plain message
    String message = "This is a super secret message. Shushhhhhhh!";
    PlainMessage plainMessage = new PlainMessage("alice", "luna", message.getBytes());

    // Attempt to add message to conversation
    aliceConversation.addPlainMessage(plainMessage);

    // Try to export conversation to json
    String jsonConversation = aliceConversation.serializeEncryptedMessagesToJson();

    // try to convert conversation back
    Conversation restoredConversation = Conversation.restore(jsonConversation, party, pad);

    // Attempt to retrieve plain message and check its integrty
    // Verify the decrypted message is identical
    PlainMessage decryptedMessage =
        restoredConversation.getPlainConversationHistory().iterator().next();
    Assert.assertEquals("Retreived decrypted message differs from original message.", plainMessage,
        decryptedMessage);
    Assert.assertEquals("Retreived decrytped payload string differs from original", message,
        decryptedMessage.getPayloadAsString());

    // Verify the next chunk index is correcty set
    // Foura parties, party 0 is the one associated to this pad, one message was stored so far.
    // The next chunk index to use should be: 0 +4 = 4.
    // Test: create another message and verify the chunk ID used is 4.
    String followupMessage = "Just another message";
    PlainMessage followUpPlainMessage =
        new PlainMessage("alice", "luna", followupMessage.getBytes());
    EncryptedMessage followupEncryptedMessage =
        restoredConversation.getEncryptedMessagePreview(followUpPlainMessage);
    Assert.assertTrue("Followup Encrypted Message had incorrect chunk id used.",
        followupEncryptedMessage.getChunksUsed()[0] == 4);
  }


}

