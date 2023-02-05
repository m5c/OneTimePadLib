package eu.kartoffelquadrat.otpgenerator;

/**
 * Main access poitn for library users. Using implementations of this interface class lets you
 * conveniently add new messages to a conversation, get the full plain history of exchanges messages
 * and basic serializtion or restore functionality.
 *
 * @author Maximilian Schiedermeier
 */
public interface Conversation {

  getConversationHistory

  /**
   * Adds an encrypted message to the internal store of conversations, encrypts the message and
   * @param encryptedMessage
   * @return
   */
  String addEncryptedMessage(EncryptedMessage encryptedMessage);

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
