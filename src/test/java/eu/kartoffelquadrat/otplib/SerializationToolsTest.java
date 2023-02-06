/**
 * Unit tests for the one time pad de/serialization.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otplib;

import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

public class SerializationToolsTest {

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
    Gson converter = SerializationTools.getGsonPadConverter();
    String serializedPad = converter.toJson(pad);

    // Convert back to object
    OneTimePad restoredPad = converter.fromJson(serializedPad, OneTimePad.class);

    // Verify the deserialized pad is identical
    boolean identical = pad.equals(restoredPad);
    Assert.assertTrue("Json Pad is not equal to original.", identical);
  }

  /**
   * Test to verify if the custom Gson works as intended for encrypted messages. This test simulates
   * what a library user would do when saving / loading a chat conversation.
   */
  @Test
  public void testSerializeDeserialize() throws PadGeneratorException, CryptorException {

    // Create new one time pad
    OneTimePad pad =
        OneTimePadGenerator.generatePad(64, 16, new String[] {"alice@luna", "bob@mars"});

    // Create a series of encryptes messages
    byte[] alice01 = "The moon is bright tonight!".getBytes();
    PlainMessage plainAlice01 = new PlainMessage("alice", "luna", alice01);
    byte[] bob01 = "Which moon?".getBytes();
    PlainMessage plainBob01 = new PlainMessage("bob", "mars", bob01);
    byte[] alice02 = "Luna, of course. Or is there another moon!".getBytes();
    PlainMessage plainAlice02 = new PlainMessage("alice", "luna", alice02);
    byte[] bob02 =
        "That's no moon, its a space station. I have a bad feeling about this.".getBytes();
    PlainMessage plainBob02 = new PlainMessage("bob", "mars", bob02);

    // Encrypt the above messages
    EncryptedMessage aliceEnc01 = Cryptor.encryptMessage(plainAlice01, pad, 0);
    EncryptedMessage bobEnc01 = Cryptor.encryptMessage(plainBob01, pad, 1);
    EncryptedMessage aliceEnc02 =
        Cryptor.encryptMessage(plainAlice02, pad, aliceEnc01.getFollowUpChunkIndex());
    EncryptedMessage bobEnc02 =
        Cryptor.encryptMessage(plainBob02, pad, bobEnc01.getFollowUpChunkIndex());

    // Add the encrypted messages to a conversation
    List<EncryptedMessage> conversation = new LinkedList<>();
    conversation.add(aliceEnc01);
    conversation.add(bobEnc01);
    conversation.add(aliceEnc02);
    conversation.add(bobEnc02);

    // Convert to JSON
    // Serialization to array is recommended to avoid registration of custom type mappers.
    Gson converter = SerializationTools.getGsonPadConverter();
    String conversationJson = converter.toJson(conversation.toArray());
    System.out.println(conversationJson);

    // Convert back to conversation list
    EncryptedMessage[] deserializedConversation =
        converter.fromJson(conversationJson, EncryptedMessage[].class);

    // Decrypt the messages and verify their integrity.
    String decryptedMessage1 =
        new String(Cryptor.decryptMessage(deserializedConversation[0], pad, true).getPayload());
    String decryptedMessage2 =
        new String(Cryptor.decryptMessage(deserializedConversation[1], pad, true).getPayload());
    String decryptedMessage3 =
        new String(Cryptor.decryptMessage(deserializedConversation[2], pad, true).getPayload());
    String decryptedMessage4 =
        new String(Cryptor.decryptMessage(deserializedConversation[3], pad, true).getPayload());

    // Compare to original messages
    Assert.assertEquals("Message 1 was not identical after serialization and decryption",
        decryptedMessage1, new String(alice01));
    Assert.assertEquals("Message 2 was not identical after serialization and decryption",
        decryptedMessage2, new String(bob01));
    Assert.assertEquals("Message 3 was not identical after serialization and decryption",
        decryptedMessage3, new String(alice02));
    Assert.assertEquals("Message 4 was not identical after serialization and decryption",
        decryptedMessage4, new String(bob02));
  }
}
