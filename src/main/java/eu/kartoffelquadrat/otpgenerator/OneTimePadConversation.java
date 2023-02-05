/**
 * One time pad specific implementation of the conversation interface.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;

public class OneTimePadConversation implements Conversation {

  // Party associated to this conversation object. New encrypted messages can only be added on behalf o this party.
  private final String conversationParty;
  private final List<EncryptedMessage> conversationEncMessages;

  // Internal reference for the next chunk Id to use for encryption. This field changes on every encrypted message added to the conversation, to rule out any double-use of a given one time pad chunk.
  private int nextChunkIdForEncryption;

  /**
   * Constructor to set up a new conversation. This associates a provided one time pad with a
   * provided party (the library client user) and initiliazes a new, blank conversation.
   */
  public OneTimePadConversation(OneTimePad oneTimePad, String party) throws InvalidPartyException {

    // Initialize empty message history.
    conversationEncMessages = new LinkedList<>();

    // Verify the provided party is valid and assiate it for all times with this conversation object.
    nextChunkIdForEncryption = oneTimePad.getStarterChunkIndexforParty(party);
    conversationParty = party;
  }

  @Override
  public List<PlainMessage> getConversationHistory() {

    // Iterate over all encrypted messages ever added, convert them to plain messages and return the
    // resulting list.
    // TODO add method to encrypted message that allows convenient conversion to plain message? Or is this one time pad methdod? A cryptor method?
  }

  @Override
  public EncryptedMessage addPlainMessage(PlainMessage message) {
    return null;
  }

  @Override
  public EncryptedMessage getEncryptedMessagePreview() {
    return null;
  }

  @Override
  public PlainMessage addEncryptedMessage(EncryptedMessage encryptedMessage) {
    return null;
  }

  @Override
  public String serializeToJson() {
    return null;
  }
}
