/**
 * Class for all operations regarding conversations. Functionality offered by this interface is the
 * main access point for library users.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otplib;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Main access point for library users. Using this class lets you conveniently add new messages to a
 * conversation, get the full plain history of exchanges messages and basic serializtion or restore
 * functionality. Implementations of this class should be assiated to an immutable party, that
 * corresponds to the user of the client software of this library. This ensures proper usage of
 * chunk ids by the library user, without them having to bother about how to determine the next save
 * chunk to use for encryption.
 *
 * @author Maximilian Schiedermeier
 */
public class Conversation {

  // Party associated to this conversation object. New encrypted messages can only be added on
  // behalf o this party.
  private final String conversationParty;
  private final List<EncryptedMessage> history;

  // Internal reference for the next chunkId to use for encryption. This field changes on every
  // encrypted message added to the conversation, to rule out any double-use of a given one time
  // pad chunk.
  private int nextChunkIdForEncryption;

  // Coryptographic material used for this conversation
  private final OneTimePad oneTimePad;


  /**
   * Constructor to set up a new conversation. This associates a provided one time pad with a
   * provided party (the library client user) and initiliazes a new, blank conversation.
   *
   * @param oneTimePad as the cryptographic material to be used for this conversation.
   * @param party      as the identifier of the party adding plain messages to encrypt.
   * @throws InvalidPartyException if the provided party string does not match the pad.
   */
  public Conversation(OneTimePad oneTimePad, String party) throws InvalidPartyException {

    this(oneTimePad, party, new LinkedList<EncryptedMessage>());
  }

  /**
   * Overloaded constructor. For internal use only upon restoring a conversation from persisted
   * files.
   *
   * @param oneTimePad as the cryptographic material to be used for this conversation.
   * @param party      as the identifier of the party adding plain messages to encrypt.
   * @param history    as a list of all previously exchanged encrypted messages.
   */
  private Conversation(OneTimePad oneTimePad, String party, List<EncryptedMessage> history)
      throws InvalidPartyException {

    // Initialize empty message history.
    this.history = history;

    // Verify the provided party is valid and assiate it for all times with this conversation
    // object.
    this.conversationParty = party;

    // Set next chunk id to use by the associated party for message encryption
    this.nextChunkIdForEncryption = getNextAvailableChunkId(history, oneTimePad.getPartyAmount(),
        oneTimePad.getPartyIndex(party));

    // Store the cryptogrpahic material
    this.oneTimePad = oneTimePad;
  }


  /**
   * Getter for the assocaited party.
   *
   * @return string array describing the party who can add plain messages to this conversation.
   */
  public String getConversationParty() {
    return conversationParty;
  }

  /**
   * Exports the encrypted entire message history.
   *
   * @return List of all emcrypted messages ever added to this conversation.
   */
  public List<EncryptedMessage> getEncryptedConversationHistory() {

    return Collections.unmodifiableList(history);
  }


  /**
   * Exports the unencrypted counterpart of the entire message history.
   *
   * @return List of all plain messages ever added to this conversation.
   * @throws CryptorException if decrypting the history failed.
   */
  public List<PlainMessage> getPlainConversationHistory() throws CryptorException {


    // Convert the entire history to plain messages and return.
    List<PlainMessage> result = new LinkedList<>();

    // Iterate over history and decrypt
    for (EncryptedMessage encMessage : history) {
      result.add(Cryptor.decryptMessage(encMessage, oneTimePad, true));
    }

    return result;
  }

  /**
   * GHelper method to add a plain message to the conversation. The method implementation
   * automatically figures out the next chunk id to use to ensure message procetion and integrity.
   *
   * @param message as the plain message to add to the conversation.
   * @return the encrypted counterpart of the added message.
   * @throws CryptorException if encrypting the message failed.
   */
  public EncryptedMessage encryptAndAddMessage(PlainMessage message) throws CryptorException {

    // Apply next chunk to use to create a new encypted message
    EncryptedMessage encMessage =
        Cryptor.encryptMessage(message, oneTimePad, nextChunkIdForEncryption);

    // Then add to history and update chunk id reference (so next message has no chunk overlap)
    history.add(encMessage);
    nextChunkIdForEncryption = encMessage.getFollowUpChunkIndex();

    // Finally return the encrypted message object
    return encMessage;
  }


  /**
   * Similar to previous message, but crates temporary preview of encrypted message without yet
   * adding the resulting encrypted message to the internal store or burning the associated one
   * chunk index. This method is meant to provide a preview of the encrypted message whiole th user
   * is still composing their message.
   *
   * @param message as the plain message to encrypt.
   * @return a preview of the resutling encrypted message, if the plain message were to be added.
   * @throws CryptorException if the encryption of the plain message failed.
   */
  public EncryptedMessage getEncryptedMessagePreview(PlainMessage message) throws CryptorException {
    return Cryptor.encryptMessage(message, oneTimePad, nextChunkIdForEncryption);
  }

  /**
   * Adds an encrypted message to the internal store of conversations.
   *
   * @param encryptedMessage message that should be added to the history.
   * @return plain message variant of the encrypted message.
   * @throws CryptorException if the encryption of the plain message failed.
   */
  public PlainMessage addEncryptedMessage(EncryptedMessage encryptedMessage)
      throws CryptorException {

    PlainMessage message = Cryptor.decryptMessage(encryptedMessage, oneTimePad, true);
    history.add(encryptedMessage);
    return message;
  }

  /**
   * Returns a json string version of the full encrypted conversation history. Internal messages are
   * stored encrypted and in an ASCII compatible hexcode representation.
   *
   * @return Json string representation of an array of encrypted messages.
   */
  public String serializeEncryptedMessagesToJson() {
    return SerializationTools.getGsonPadConverter().toJson(history.toArray());
  }

  /**
   * Restores a previously exported conversation back to a java object. Useful for loading a
   * conversation from disk on program startup.
   *
   * @param serializedEncryptedMessages json string representing an array of encrypted messages.
   * @param party                       owner of this conversation.
   * @param oneTimePad                  the key material used for this conversation.
   * @return conversation as a java object.
   * @throws InvalidPartyException        if the provided party does not match the one time pad.
   * @throws OneTimePadMissmatchException if there is a mismatch between the provided pad and conv.
   */
  public static Conversation restore(String serializedEncryptedMessages, String party,
                                     OneTimePad oneTimePad)
      throws InvalidPartyException, OneTimePadMissmatchException {

    // verify the party is associated to this pad (throws exception if not indexed)
    oneTimePad.isAssociatedParty(party);

    // restore the provided json string of encrypted messages back to an object
    EncryptedMessage[] encryptedMessagesArray = SerializationTools.getGsonPadConverter()
        .fromJson(serializedEncryptedMessages, EncryptedMessage[].class);
    List<EncryptedMessage> history = Arrays.asList(encryptedMessagesArray);

    // verify the messages match the provided one time pad
    if (encryptedMessagesArray.length > 0) {
      if (!encryptedMessagesArray[0].getOtpHash().equals(oneTimePad.getHash())) {
        throw new OneTimePadMissmatchException(
            "Conversation cannot be restored because the provided message history is not compatible"
                + " to the provided cryptographic material.");
      }
    }

    // Combine the validated parameters back to a conversation object.
    return new Conversation(oneTimePad, party, history);
  }

  /**
   * Helper method to identify determine the next chunk id to use by the assicated partyu for
   * encryption. This is needed to determine the followUp chunkId on conversation reconstruction, to
   * ensure no chunk is used twice.
   *
   * @param history       as the list of encrytped messages to analyze.
   * @param amountParties as the number of parties associated to the conversation per one time pad.
   * @param partyIndex    integer indicating party for which we are seeking the most recent
   *                      message.
   * @return null if the parte never communicated or the most recent encrypted message otherwise.
   */
  private int getNextAvailableChunkId(List<EncryptedMessage> history, int amountParties,
                                      int partyIndex) {

    // Invert the history, so we begin search with the most recent messages.
    Collections.reverse(history);
    for (EncryptedMessage encMessage : history) {

      // return message on first party match
      if (encMessage.getChunksUsed()[0] % amountParties == partyIndex) {
        return encMessage.getFollowUpChunkIndex();
      }
    }

    return partyIndex;
  }
}
