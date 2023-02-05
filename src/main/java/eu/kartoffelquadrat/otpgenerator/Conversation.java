/**
 * Generic interface for message conversations. Functionality offered by this interface is the main
 * access point for library users.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import java.util.List;

/**
 * Main access point for library users. Using implementations of this interface class lets you
 * conveniently add new messages to a conversation, get the full plain history of exchanges messages
 * and basic serializtion or restore functionality. Implementations of this class should be assiated
 * to an immutable party, that corresponds to the user of the client software of this library. This
 * ensures proper usage of chunk ids by the library user, without them having to bother about how to
 * determine the next save chunk to use for encryption.
 *
 * @author Maximilian Schiedermeier
 */
public interface Conversation {

  /**
   * Exports the unencrypted counterpart of the entire message history.
   *
   * @return List of all plain messages ever added to this conversation.
   */
  List<PlainMessage> getConversationHistory();

  /**
   * GHelper method to add a plain message to the conversation. The method implementation
   * automatically figures out the next chunk id to use to ensure message procetion and integrity.
   *
   * @param message as the plain message to add to the conversation.
   * @return the encrypted counterpart of the added message.
   */
  EncryptedMessage addPlainMessage(PlainMessage message);

  /**
   * Similar to previous message, but crates temporary preview of encrypted message without yet
   * adding the resulting encrypted message to the internal store or burning the associated one
   * chunk index. This method is meant to provide a preview of the encrypted message whiole th user
   * is still composing their message.
   *
   * @return a preview of the resutling encrypted message, if the plain message were to be added.
   */
  EncryptedMessage getEncryptedMessagePreview(PlainMessage);

  /**
   * Adds an encrypted message to the internal store of conversations, encrypts the message and
   *
   * @param encryptedMessage
   * @return
   */
  PlainMessage addEncryptedMessage(EncryptedMessage encryptedMessage);

  /**
   * Returns a printable version of the full conversation. Internal messages are stored encrypted
   * and in an ASCII compatible hexcode representation.
   *
   * @return Json string representation of the conversation object.
   */
  String serializeToJson();

  /**
   * Restores a previously exported conversation back to a java object. Useful for loading a
   * conversation from disk on program startup.
   *
   * @param serializedConversation as the Json String of the conversation to restore.
   * @return conversation as a java object.
   */
  static Conversation deserializeFromJson(String serializedConversation) {
    return SerializationTools.getGsonPadConverter()
        .fromJson(serializedConversation, Conversation.class);
  }
}
